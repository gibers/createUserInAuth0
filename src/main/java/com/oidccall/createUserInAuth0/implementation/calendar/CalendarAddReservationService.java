package com.oidccall.createUserInAuth0.implementation.calendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.oidccall.createUserInAuth0.dtos.front.ReservationCardRequestDto;
import com.oidccall.createUserInAuth0.dtos.front.SeatingCapacityMax;
import com.oidccall.createUserInAuth0.dtos.front.ServiceTypeDto;
import com.oidccall.createUserInAuth0.dtos.mappers.SeatingCapacityMaxMapper;
import com.oidccall.createUserInAuth0.entities.Client;
import com.oidccall.createUserInAuth0.entities.Reservation;
import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.SeatingCapacity;
import com.oidccall.createUserInAuth0.entities.Template;
import com.oidccall.createUserInAuth0.exceptions.ErrorsEnum;
import com.oidccall.createUserInAuth0.implementation.calendar.events.ReservationCreatedEvent;
import com.oidccall.createUserInAuth0.repository.ClientRepository;
import com.oidccall.createUserInAuth0.repository.ReservationRepository;
import com.oidccall.createUserInAuth0.repository.RestaurateurRepository;
import com.oidccall.createUserInAuth0.repository.SeatingCapacityRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class CalendarAddReservationService {

	private final RestaurateurRepository restaurateurRepository;
	private final ReservationRepository reservationRepository;
	private final ClientRepository clientRepository;
	private final SeatingCapacityRepository seatingCapacityRepository;
	private final ApplicationEventPublisher eventPublisher;

	public Map<ServiceTypeDto, List<SeatingCapacityMax>> getListBookableTableForLunchAndDinner(long userId, LocalDate dateReservation) {
		Restaurateur restaurateur = restaurateurRepository.findById(userId).orElseThrow();
		List<SeatingCapacity> scFromTemplateLunch = getBookableTableForOneService(restaurateur, dateReservation, LocalTime.of(15, 0));
		List<SeatingCapacity> scFromTemplateDinner = getBookableTableForOneService(restaurateur, dateReservation, LocalTime.of(16, 0));
		return Map.of(ServiceTypeDto.LUNCH, SeatingCapacityMaxMapper.mapToSeatingCapacityMax(scFromTemplateLunch),
				ServiceTypeDto.DINNER, SeatingCapacityMaxMapper.mapToSeatingCapacityMax(scFromTemplateDinner));
	}

	private List<SeatingCapacity> getBookableTableForOneService(Restaurateur restaurateur, LocalDate dateReservation, LocalTime startTime) {
		Template templateFromDate = getTemplateFromDate(restaurateur, dateReservation);
		return getBookableTableFromTemplateAndTime(templateFromDate, startTime);
	}

	// todo: we need to add 2 things:
	//  1. check if the table is available (not already reserved)
	//  2. check if the returned table matches with the client constraints capacity (still not implemented)
	@Transactional
	public void addReservationImpl(ReservationCardRequestDto reservationCardRequestDto) {
		Restaurateur restaurateur = restaurateurRepository.findById(reservationCardRequestDto.restaurantId()).orElseThrow();

		Template templateFromDate = getTemplateFromDate(restaurateur, reservationCardRequestDto.dateReservation());
		List<SeatingCapacity> scFromTemplate = getBookableTableFromTemplateAndTime(templateFromDate, reservationCardRequestDto.startTime());

		SeatingCapacity seatingCapacityFound = getFirstTableIdWithGreaterCapacityThanNbPerson(templateFromDate.getId(), scFromTemplate,
				reservationCardRequestDto.nbPerson());
		Reservation reservation = this.saveReservation(reservationCardRequestDto, templateFromDate, seatingCapacityFound, restaurateur);
		log.info("Reservation saved successfully {}", reservation.getId());

		eventPublisher.publishEvent(new ReservationCreatedEvent(
				restaurateur.getId(), reservation.getReservationDate()));
	}

	private List<SeatingCapacity> getBookableTableFromTemplateAndTime(Template templateFromDate, LocalTime startTime) {
		Set<String> tableNumbers = getTableIdFromTime(templateFromDate, startTime);
		return this.seatingCapacityRepository.findAllByRestaurateurAndTableNumberIn(templateFromDate.getRestaurateur(), tableNumbers);
	}

	private Set<String> getTableIdFromTime(Template templateFromDate, LocalTime localTime) {
		return (localTime.isBefore(LocalTime.of(16, 0))) ? templateFromDate.getLunchTableNumbers()
				: templateFromDate.getDinnerTableNumbers();
	}

	private SeatingCapacity getFirstTableIdWithGreaterCapacityThanNbPerson(Long templateId,
			List<SeatingCapacity> scFromTemplate, int nbPerson) {
		return scFromTemplate.stream()
				.filter(sc -> sc.getCapacity() >= nbPerson)
				.min(Comparator.comparingInt(SeatingCapacity::getCapacity))
				.orElseThrow(() -> {
					log.error(ErrorsEnum.E_1201.getPrivateErrorMessage(), templateId, nbPerson);
					return new DataIntegrityViolationException(ErrorsEnum.E_1201.getPublicErrorMessage());
				});
	}

	// we return the template with the dateSolo if it exists, otherwise we return the first available template.
	// If we call this method, we know upfront that there is at least one active template for that date.
	private Template getTemplateFromDate(Restaurateur restaurateur, LocalDate dateReservation) {
		return restaurateur.getTemplates().stream()
				.filter(Template::isActive)
				.filter(t -> Objects.equals(t.getDateSolo(), dateReservation)
						|| isTemplateAvailableForThatDate(t, dateReservation))
				.min(Comparator.comparingInt(t ->
						Objects.equals(t.getDateSolo(), dateReservation) ? 0 : 1))
				.orElseThrow(() -> {
					log.error(ErrorsEnum.E_1200.getPrivateErrorMessage(), dateReservation);
					return new DataIntegrityViolationException(
							ErrorsEnum.E_1200.getPublicErrorMessage());
				});
	}

	private boolean isTemplateAvailableForThatDate(Template template, LocalDate dateReservation) {
		return switch (dateReservation.getDayOfWeek()) {
			case MONDAY -> template.isMonday();
			case TUESDAY -> template.isTuesday();
			case WEDNESDAY -> template.isWednesday();
			case THURSDAY -> template.isThursday();
			case FRIDAY -> template.isFriday();
			case SATURDAY -> template.isSaturday();
			case SUNDAY -> template.isSunday();
		};
	}

	private Reservation saveReservation(ReservationCardRequestDto reservationCardRequestDto, Template templateFromDate,
			SeatingCapacity seatingCapacityFound, Restaurateur restaurateur) {
		Client newClientFromDB = saveClient(reservationCardRequestDto);
		Reservation.ReservationBuilder builder = Reservation.builder();
		builder.client(newClientFromDB);
		builder.seatingCapacity(seatingCapacityFound);
		builder.template(templateFromDate);
		builder.restaurateur(restaurateur);

		builder.reservationDate(reservationCardRequestDto.dateReservation());
		builder.startTime(reservationCardRequestDto.startTime());
		builder.endTime(reservationCardRequestDto.startTime().plusHours(1));
		builder.nbPerson(reservationCardRequestDto.nbPerson());
		Reservation reservation = builder.build();
		return reservationRepository.save(reservation);
	}

	private Client saveClient(ReservationCardRequestDto reservationCardRequestDto) {
		Client.ClientBuilder bu = Client.builder();
		bu.name(reservationCardRequestDto.name());
		bu.firstname(reservationCardRequestDto.firstname());
		bu.phone(reservationCardRequestDto.phone());
		Client build = bu.build();
		return clientRepository.save(build);
	}

}
