package com.newevent.registrationservice.exception;

/**
 * Thrown when Event Service reports there aren't enough seats left
 * (it returns 409 Conflict for this - Registration Service forwards the
 * same semantic to its own caller rather than swallowing the error and
 * saving a registration for seats that were never actually reserved).
 */
public class InsufficientSeatsException extends RuntimeException {
    public InsufficientSeatsException(String message) {
        super(message);
    }
}
