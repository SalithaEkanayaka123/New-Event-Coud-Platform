package com.newevent.registrationservice.exception;

/** Thrown when Event Service reports the event doesn't exist. */
public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(Long eventId) {
        super("Event not found with id: " + eventId);
    }
}
