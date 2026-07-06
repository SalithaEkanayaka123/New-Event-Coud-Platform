package com.newevent.registrationservice.dto;

import lombok.Data;

/**
 * Mirrors the relevant fields of Event Service's Event entity - just enough
 * to read seatsAvailable back after calling PATCH /api/events/{id}/seats.
 * Deliberately NOT sharing a class/JAR with Event Service: these are two
 * independent microservices, and coupling their DTOs together (e.g. via a
 * shared library) would recreate the same tight coupling that splitting them
 * into services was meant to avoid. A little duplication here is the
 * intentional trade-off.
 */
@Data
public class EventResponse {
    private Long id;
    private String title;
    private Integer seatsAvailable;
}
