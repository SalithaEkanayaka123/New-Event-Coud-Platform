package com.newevent.registrationservice.exception;

/**
 * Thrown when Event Service can't be reached at all (connection refused,
 * timeout) - distinct from EventNotFoundException, which means Event
 * Service responded but said "no such event". This distinction matters:
 * one is a data problem (404), the other is an infrastructure problem
 * (503) - conflating them would give a misleading error to the caller.
 */
public class EventServiceUnavailableException extends RuntimeException {
    public EventServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
