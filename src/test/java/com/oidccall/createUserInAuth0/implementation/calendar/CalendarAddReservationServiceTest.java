package com.oidccall.createUserInAuth0.implementation.calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;

import com.oidccall.createUserInAuth0.dtos.front.ReservationCardRequestDto;
import com.oidccall.createUserInAuth0.dtos.front.SeatingCapacityMax;
import com.oidccall.createUserInAuth0.dtos.front.ServiceTypeDto;
import com.oidccall.createUserInAuth0.entities.Client;
import com.oidccall.createUserInAuth0.entities.Reservation;
import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.SeatingCapacity;
import com.oidccall.createUserInAuth0.entities.Template;
import com.oidccall.createUserInAuth0.implementation.calendar.events.ReservationCreatedEvent;
import com.oidccall.createUserInAuth0.repository.ClientRepository;
import com.oidccall.createUserInAuth0.repository.ReservationRepository;
import com.oidccall.createUserInAuth0.repository.RestaurateurRepository;
import com.oidccall.createUserInAuth0.repository.SeatingCapacityRepository;

class CalendarAddReservationServiceTest {

	private final RestaurateurRepository restaurateurRepository = mock(RestaurateurRepository.class);
	private final ReservationRepository reservationRepository = mock(ReservationRepository.class);
	private final ClientRepository clientRepository = mock(ClientRepository.class);
	private final SeatingCapacityRepository seatingCapacityRepository = mock(SeatingCapacityRepository.class);
	private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);

	private final CalendarAddReservationService service = new CalendarAddReservationService(
			restaurateurRepository,
			reservationRepository,
			clientRepository,
			seatingCapacityRepository,
			eventPublisher);

	private Restaurateur restaurateur;
	private Template template;
	private ReservationCardRequestDto request;

	@BeforeEach
	void setUp() {
		LocalDate reservationDate = LocalDate.of(2026, 10, 26); // Monday
		restaurateur = Restaurateur.builder().id(42L).build();
		template = Template.builder()
				.id(7L)
				.restaurateur(restaurateur)
				.active(true)
				.monday(true)
				.lunchTableNumbers(Set.of("L1", "L2"))
				.dinnerTableNumbers(Set.of("D1"))
				.build();
		restaurateur.setTemplates(List.of(template));
		request = new ReservationCardRequestDto(42L, reservationDate, LocalTime.of(12, 30), 3,
				"Martin", "Alice", "+41791234567");

		when(restaurateurRepository.findById(42L)).thenReturn(Optional.of(restaurateur));
		when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
	}

	@Test
	void getListBookableTableForLunchAndDinner_returnsTablesFromTheirServiceSets() {
		SeatingCapacity lunchTable = seatingCapacity("L1", 2);
		SeatingCapacity dinnerTable = seatingCapacity("D1", 4);
		when(seatingCapacityRepository.findAllByRestaurateurAndTableNumberIn(restaurateur, Set.of("L1", "L2")))
				.thenReturn(List.of(lunchTable));
		when(seatingCapacityRepository.findAllByRestaurateurAndTableNumberIn(restaurateur, Set.of("D1")))
				.thenReturn(List.of(dinnerTable));

		Map<ServiceTypeDto, List<SeatingCapacityMax>> result = service.getListBookableTableForLunchAndDinner(
				42L, request.dateReservation());

		assertEquals(Map.of(
				ServiceTypeDto.LUNCH, List.of(new SeatingCapacityMax("L1", 2)),
				ServiceTypeDto.DINNER, List.of(new SeatingCapacityMax("D1", 4))), result);
		verify(restaurateurRepository).findById(42L);
		verify(seatingCapacityRepository).findAllByRestaurateurAndTableNumberIn(restaurateur, Set.of("L1", "L2"));
		verify(seatingCapacityRepository).findAllByRestaurateurAndTableNumberIn(restaurateur, Set.of("D1"));
	}

	@Test
	void getListBookableTableForLunchAndDinner_throwsWhenRestaurateurDoesNotExist() {
		when(restaurateurRepository.findById(42L)).thenReturn(Optional.empty());

		assertThrows(java.util.NoSuchElementException.class,
				() -> service.getListBookableTableForLunchAndDinner(42L, request.dateReservation()));

		verify(seatingCapacityRepository, never()).findAllByRestaurateurAndTableNumberIn(any(), any());
	}

	@Test
	void addReservationImpl_savesClientAndReservationUsingSmallestSuitableLunchTableAndPublishesEvent() {
		SeatingCapacity tooSmall = seatingCapacity("L1", 2);
		SeatingCapacity exactFit = seatingCapacity("L2", 3);
		when(seatingCapacityRepository.findAllByRestaurateurAndTableNumberIn(restaurateur, Set.of("L1", "L2")))
				.thenReturn(List.of(tooSmall, seatingCapacity("L2", 5), exactFit));
		ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
		ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
		ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);

		service.addReservationImpl(request);

		verify(clientRepository).save(clientCaptor.capture());
		assertEquals("Martin", clientCaptor.getValue().getName());
		assertEquals("Alice", clientCaptor.getValue().getFirstname());
		assertEquals("+41791234567", clientCaptor.getValue().getPhone());
		verify(reservationRepository).save(reservationCaptor.capture());
		Reservation savedReservation = reservationCaptor.getValue();
		assertSame(exactFit, savedReservation.getSeatingCapacity());
		assertSame(template, savedReservation.getTemplate());
		assertSame(restaurateur, savedReservation.getRestaurateur());
		assertEquals(request.dateReservation(), savedReservation.getReservationDate());
		assertEquals(request.startTime(), savedReservation.getStartTime());
		assertEquals(LocalTime.of(13, 30), savedReservation.getEndTime());
		assertEquals(3, savedReservation.getNbPerson());
		assertSame(clientCaptor.getValue(), savedReservation.getClient());
		verify(eventPublisher).publishEvent(eventCaptor.capture());
		assertEquals(new ReservationCreatedEvent(42L, request.dateReservation()), eventCaptor.getValue());
	}

	@Test
	void addReservationImpl_usesDinnerTablesAtOrAfterFourPm() {
		ReservationCardRequestDto dinnerRequest = new ReservationCardRequestDto(42L, request.dateReservation(),
				LocalTime.of(16, 0), 2, "Martin", "Alice", "+41791234567");
		SeatingCapacity dinnerTable = seatingCapacity("D1", 2);
		when(seatingCapacityRepository.findAllByRestaurateurAndTableNumberIn(restaurateur, Set.of("D1")))
				.thenReturn(List.of(dinnerTable));
		ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);

		service.addReservationImpl(dinnerRequest);

		verify(reservationRepository).save(reservationCaptor.capture());
		assertSame(dinnerTable, reservationCaptor.getValue().getSeatingCapacity());
		assertEquals(LocalTime.of(17, 0), reservationCaptor.getValue().getEndTime());
		verify(eventPublisher).publishEvent(new ReservationCreatedEvent(42L, dinnerRequest.dateReservation()));
	}

	@Test
	void addReservationImpl_throwsWhenNoActiveTemplateMatchesDate() {
		template.setMonday(false);

		assertThrows(DataIntegrityViolationException.class, () -> service.addReservationImpl(request));

		verify(seatingCapacityRepository, never()).findAllByRestaurateurAndTableNumberIn(any(), any());
		verify(clientRepository, never()).save(any(Client.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(eventPublisher, never()).publishEvent(any(Object.class));
	}

	@Test
	void addReservationImpl_throwsWhenNoTableCanFitAndDoesNotSaveAnything() {
		when(seatingCapacityRepository.findAllByRestaurateurAndTableNumberIn(restaurateur, Set.of("L1", "L2")))
				.thenReturn(List.of(seatingCapacity("L1", 2), seatingCapacity("L2", 2)));

		assertThrows(DataIntegrityViolationException.class, () -> service.addReservationImpl(request));

		verify(clientRepository, never()).save(any(Client.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(eventPublisher, never()).publishEvent(any(Object.class));
	}

	@Test
	void addReservationImpl_throwsWhenRestaurateurDoesNotExist() {
		when(restaurateurRepository.findById(42L)).thenReturn(Optional.empty());

		assertThrows(java.util.NoSuchElementException.class, () -> service.addReservationImpl(request));

		verify(seatingCapacityRepository, never()).findAllByRestaurateurAndTableNumberIn(any(), any());
		verify(clientRepository, never()).save(any(Client.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(eventPublisher, never()).publishEvent(any(Object.class));
	}

	private SeatingCapacity seatingCapacity(String tableNumber, int capacity) {
		return SeatingCapacity.builder()
				.restaurateur(restaurateur)
				.tableNumber(tableNumber)
				.capacity(capacity)
				.build();
	}
}
