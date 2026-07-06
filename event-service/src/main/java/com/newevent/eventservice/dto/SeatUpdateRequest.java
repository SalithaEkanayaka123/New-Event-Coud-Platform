package com.newevent.eventservice.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Used by the PATCH /api/events/{id}/seats endpoint.
 * Registration Service calls this with the number of tickets just booked;
 * Event Service decrements seatsAvailable and returns the updated Event so
 * Registration Service can check the new count against its own threshold
 * logic (that threshold check belongs in Registration Service, per the
 * brief - Event Service just owns the data).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatUpdateRequest {

    @Positive(message = "Ticket count must be positive")
    private int ticketCount;
}
