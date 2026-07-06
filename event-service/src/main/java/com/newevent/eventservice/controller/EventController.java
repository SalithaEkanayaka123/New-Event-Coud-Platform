package com.newevent.eventservice.controller;

import com.newevent.eventservice.dto.SeatUpdateRequest;
import com.newevent.eventservice.entity.Event;
import com.newevent.eventservice.exception.EventNotFoundException;
import com.newevent.eventservice.repository.EventRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for Event Service.
 *
 * Per the module FAQ, this does NOT need to be integrated with the
 * frontend - it's fine (and expected) to demonstrate this via Postman /
 * Hoppscotch at the viva. The one consumer that matters is
 * Registration Service, which calls PATCH /seats when someone registers.
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event createEvent(@Valid @RequestBody Event event) {
        // id is auto-generated - ignore any id the client sends
        event.setId(null);
        return eventRepository.save(event);
    }

    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Long id, @Valid @RequestBody Event updatedEvent) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        existing.setTitle(updatedEvent.getTitle());
        existing.setVenue(updatedEvent.getVenue());
        existing.setEventDateTime(updatedEvent.getEventDateTime());
        existing.setTicketPrice(updatedEvent.getTicketPrice());
        existing.setCapacity(updatedEvent.getCapacity());
        existing.setSeatsAvailable(updatedEvent.getSeatsAvailable());

        return eventRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException(id);
        }
        eventRepository.deleteById(id);
    }

    /**
     * Called by Registration Service after a successful registration.
     * Decrements seatsAvailable by the ticket count and returns the updated
     * Event - Registration Service checks the returned seatsAvailable
     * against its own threshold to decide whether to trigger the Lambda
     * (requirement #2 in the brief). The threshold logic deliberately does
     * NOT live here - Event Service just owns and updates the data.
     */
    @PatchMapping("/{id}/seats")
    public ResponseEntity<Event> decrementSeats(@PathVariable Long id,
                                                  @Valid @RequestBody SeatUpdateRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        if (event.getSeatsAvailable() < request.getTicketCount()) {
            throw new IllegalStateException(
                    "Not enough seats available: requested " + request.getTicketCount()
                            + ", only " + event.getSeatsAvailable() + " remaining");
        }

        event.setSeatsAvailable(event.getSeatsAvailable() - request.getTicketCount());
        Event saved = eventRepository.save(event);
        return ResponseEntity.ok(saved);
    }
}
