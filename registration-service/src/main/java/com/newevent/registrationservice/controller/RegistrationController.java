package com.newevent.registrationservice.controller;

import com.newevent.registrationservice.dto.RegistrationRequest;
import com.newevent.registrationservice.entity.Registration;
import com.newevent.registrationservice.exception.RegistrationNotFoundException;
import com.newevent.registrationservice.repository.RegistrationRepository;
import com.newevent.registrationservice.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final RegistrationRepository registrationRepository;

    public RegistrationController(RegistrationService registrationService,
                                    RegistrationRepository registrationRepository) {
        this.registrationService = registrationService;
        this.registrationRepository = registrationRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Registration createRegistration(@Valid @RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }

    @GetMapping
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    @GetMapping("/{id}")
    public Registration getRegistration(@PathVariable Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationNotFoundException(id));
    }

    @GetMapping("/by-event/{eventId}")
    public List<Registration> getRegistrationsByEvent(@PathVariable Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }
}
