package com.newevent.registrationservice.service;

/**
 * The seam between "seats dropped below threshold" (real logic, working
 * today) and "trigger the serverless function" (doesn't exist until Day 5,
 * not wired to this service until Day 7). Defining this as an interface now
 * means Day 7's change is: implement this interface for real with an AWS
 * SDK Lambda invoke call, swap the Spring bean - nothing else in
 * RegistrationService changes.
 */
public interface SeatThresholdNotifier {
    void notifyLowSeats(Long eventId, int seatsRemaining);
}
