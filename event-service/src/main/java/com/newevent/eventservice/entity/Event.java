package com.newevent.eventservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event entity - matches the brief's exact required fields:
 * Event ID, Title, Venue, Date/Time, Ticket Price, Capacity, Seats Available.
 *
 * Design notes (for the report / viva):
 * - The DB-generated `id` IS the "Event ID" - no separate redundant field.
 * - ticketPrice is BigDecimal, not double/float - money should never use
 *   floating point, since binary floating-point can't represent most decimal
 *   fractions exactly, which compounds into real rounding errors over many
 *   transactions.
 * - capacity vs seatsAvailable are tracked separately so seatsAvailable can
 *   be decremented independently (by Registration Service, via the PATCH
 *   endpoint) without ever needing to know or recompute the original capacity.
 */
@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Venue is required")
    private String venue;

    @NotNull(message = "Event date/time is required")
    @Column(name = "event_date_time")
    private LocalDateTime eventDateTime;

    @NotNull(message = "Ticket price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Ticket price cannot be negative")
    @Column(name = "ticket_price", precision = 10, scale = 2)
    private BigDecimal ticketPrice;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    private Integer capacity;

    @NotNull(message = "Seats available is required")
    @PositiveOrZero(message = "Seats available cannot be negative")
    @Column(name = "seats_available")
    private Integer seatsAvailable;
}
