package com.newevent.registrationservice.repository;

import com.newevent.registrationservice.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    // Useful for the viva demo: "show me all registrations for this event"
    List<Registration> findByEventId(Long eventId);
}
