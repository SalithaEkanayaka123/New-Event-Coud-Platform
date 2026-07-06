package com.newevent.registrationservice.service;

import com.newevent.registrationservice.client.EventServiceClient;
import com.newevent.registrationservice.dto.EventResponse;
import com.newevent.registrationservice.dto.RegistrationRequest;
import com.newevent.registrationservice.entity.Registration;
import com.newevent.registrationservice.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Orchestrates the actual business flow behind "register for an event":
 *   1. Ask Event Service to decrement seats (this is the source of truth
 *      for seat counts - Registration Service never touches that data
 *      directly)
 *   2. If that succeeds, check the returned seatsAvailable against the
 *      configured threshold
 *   3. Below threshold -> notify (stub today, real Lambda call from Day 7)
 *   4. Save the registration record locally
 *
 * Note the ordering: seats are decremented FIRST, registration is saved
 * SECOND. If step 1 fails (not enough seats, event doesn't exist), nothing
 * is saved - there's no such thing as a registration that couldn't
 * actually reserve a seat.
 */
@Service
public class RegistrationService {

    private final EventServiceClient eventServiceClient;
    private final RegistrationRepository registrationRepository;
    private final SeatThresholdNotifier seatThresholdNotifier;
    private final int seatThreshold;

    public RegistrationService(
            EventServiceClient eventServiceClient,
            RegistrationRepository registrationRepository,
            SeatThresholdNotifier seatThresholdNotifier,
            @Value("${app.seat-threshold}") int seatThreshold) {
        this.eventServiceClient = eventServiceClient;
        this.registrationRepository = registrationRepository;
        this.seatThresholdNotifier = seatThresholdNotifier;
        this.seatThreshold = seatThreshold;
    }

    public Registration register(RegistrationRequest request) {
        // Step 1 & 2: decrement seats via Event Service, get the new count back.
        // Throws EventNotFoundException / InsufficientSeatsException /
        // EventServiceUnavailableException on failure - caught by
        // GlobalExceptionHandler, nothing saved if this fails.
        EventResponse updatedEvent = eventServiceClient.decrementSeats(
                request.getEventId(), request.getTicketCount());

        // Step 3: threshold check + notify (brief requirement #2)
        if (updatedEvent.getSeatsAvailable() < seatThreshold) {
            seatThresholdNotifier.notifyLowSeats(
                    request.getEventId(), updatedEvent.getSeatsAvailable());
        }

        // Step 4: only now do we persist the registration
        Registration registration = new Registration(
                null,
                request.getEventId(),
                request.getName(),
                request.getEmail(),
                request.getTicketCount(),
                LocalDateTime.now());

        return registrationRepository.save(registration);
    }
}
