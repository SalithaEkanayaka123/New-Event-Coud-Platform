package com.newevent.registrationservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Registration entity - matches the brief's exact required fields:
 * Registration ID, Event ID, Name, Email, Ticket Count, and Timestamp.
 *
 * Design note: eventId is a plain field, same reasoning as Program Service -
 * no cross-service JPA relationship. This service never touches Event
 * Service's database directly; it only ever talks to it over HTTP
 * (see EventServiceClient), which is the whole point of splitting these
 * into separate microservices in the first place.
 */
@Entity
@Table(name = "registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Event ID is required")
    @Column(name = "event_id")
    private Long eventId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Ticket count is required")
    @Positive(message = "Ticket count must be positive")
    @Column(name = "ticket_count")
    private Integer ticketCount;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
}
