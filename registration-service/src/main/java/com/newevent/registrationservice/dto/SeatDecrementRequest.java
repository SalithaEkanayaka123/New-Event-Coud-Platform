package com.newevent.registrationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * What gets sent to Event Service's PATCH /api/events/{id}/seats.
 * Mirrors Event Service's SeatUpdateRequest shape (ticketCount) - again,
 * deliberately duplicated rather than shared, same reasoning as EventResponse.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatDecrementRequest {
    private int ticketCount;
}
