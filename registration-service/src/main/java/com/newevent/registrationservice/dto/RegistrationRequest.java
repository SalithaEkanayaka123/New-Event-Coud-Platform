package com.newevent.registrationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * What the client (frontend Register button, or Postman) actually sends.
 * Deliberately separate from the Registration entity - the client should
 * never be able to set id or registeredAt (server-controlled fields).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Ticket count is required")
    @Positive(message = "Ticket count must be positive")
    private Integer ticketCount;
}
