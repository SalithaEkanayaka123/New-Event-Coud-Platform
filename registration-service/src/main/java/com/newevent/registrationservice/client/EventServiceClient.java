package com.newevent.registrationservice.client;

import com.newevent.registrationservice.dto.EventResponse;
import com.newevent.registrationservice.dto.SeatDecrementRequest;
import com.newevent.registrationservice.exception.EventNotFoundException;
import com.newevent.registrationservice.exception.EventServiceUnavailableException;
import com.newevent.registrationservice.exception.InsufficientSeatsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

/**
 * The ONLY way Registration Service talks to Event Service - over HTTP,
 * through Event Service's own public API. This is the concrete
 * implementation of the microservice boundary: no shared database, no
 * shared JPA entities, just this one client class.
 *
 * eventServiceBaseUrl is externalised (application.yml) rather than
 * hardcoded, since it's genuinely different between local dev
 * (http://localhost:8081) and the cluster (http://event-service:8080,
 * using k8s's internal service DNS) - a real environment-specific config
 * decision worth mentioning if asked about the deployment architecture.
 */
@Component
public class EventServiceClient {

    private final RestClient restClient;

    public EventServiceClient(@Value("${event-service.base-url}") String eventServiceBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(eventServiceBaseUrl)
                .build();
    }

    public EventResponse decrementSeats(Long eventId, int ticketCount) {
        try {
            return restClient.patch()
                    .uri("/api/events/{id}/seats", eventId)
                    .body(new SeatDecrementRequest(ticketCount))
                    .retrieve()
                    .body(EventResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new EventNotFoundException(eventId);
        } catch (HttpClientErrorException.Conflict ex) {
            // Deliberately NOT parsing Event Service's raw error JSON here -
            // reaching into another service's internal error body format
            // would couple the two services through an implementation
            // detail that could change independently. A clean, generic
            // message is more robust across the service boundary.
            throw new InsufficientSeatsException(
                    "Not enough seats available for event " + eventId + " (requested " + ticketCount + ")");
        } catch (ResourceAccessException ex) {
            // Connection refused / timeout - Event Service isn't reachable
            // at all, distinct from it responding with an error.
            throw new EventServiceUnavailableException(
                    "Event Service is unreachable", ex);
        }
    }

}
