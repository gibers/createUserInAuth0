package com.oidccall.createUserInAuth0.implementation.calendar;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.implementation.calendar.events.ReservationCreatedEvent;
import com.oidccall.createUserInAuth0.repository.RestaurateurRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarNotificationService {

	private final RestaurateurRepository restaurateurRepository;
	private final ConcurrentMap<Long, SseEmitter> emittersByRestaurateurId = new ConcurrentHashMap<>();

	public SseEmitter subscribe(long usersId) {
		Restaurateur restaurateur = restaurateurRepository.findByUsersId(usersId).orElseThrow();
		long restaurateurId = restaurateur.getId();
		SseEmitter emitter = new SseEmitter(0L);

		SseEmitter previousEmitter = emittersByRestaurateurId.put(restaurateurId, emitter);
		if (previousEmitter != null) {
			previousEmitter.complete();
		}

		Runnable removeEmitter = () -> emittersByRestaurateurId.remove(restaurateurId, emitter);
		emitter.onCompletion(removeEmitter);
		emitter.onTimeout(removeEmitter);
		emitter.onError(error -> removeEmitter.run());

		try {
			emitter.send(SseEmitter.event().name("connected").data("connected"));
		} catch (IOException exception) {
			removeEmitter.run();
			emitter.completeWithError(exception);
			log.warn("Could not establish reservation notification stream for restaurateur {}", restaurateurId,
					exception);
		}

		return emitter;
	}

	public void sendReservationCreated(ReservationCreatedEvent event) {
		SseEmitter emitter = emittersByRestaurateurId.get(event.restaurateurId());
		if (emitter == null) {
			return;
		}

		try {
			emitter.send(SseEmitter.event()
					.name("reservation-created")
					.data(event));
		} catch (IOException | IllegalStateException exception) {
			emittersByRestaurateurId.remove(event.restaurateurId(), emitter);
			emitter.completeWithError(exception);
			log.warn("Could not send reservation notification to restaurateur {}", event.restaurateurId(), exception);
		}
	}
}
