package com.newevent.registrationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * TEMPORARY implementation for Day 4 - logs instead of actually calling
 * Lambda, since Lambda doesn't exist until Day 5 and isn't wired to this
 * service until Day 7. Replace with a real AWS SDK invoke call then
 * (e.g. LambdaSeatThresholdNotifier), and update the @Service/@Primary
 * wiring - RegistrationService itself won't need to change at all.
 */
@Service
public class LoggingSeatThresholdNotifier implements SeatThresholdNotifier {

    private static final Logger log = LoggerFactory.getLogger(LoggingSeatThresholdNotifier.class);

    @Override
    public void notifyLowSeats(Long eventId, int seatsRemaining) {
        log.info("[STUB - Day 7 will replace this with a real Lambda invoke] " +
                        "Seat threshold reached for event {}: only {} seats remaining",
                eventId, seatsRemaining);
    }
}
