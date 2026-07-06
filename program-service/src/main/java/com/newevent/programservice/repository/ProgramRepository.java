package com.newevent.programservice.repository;

import com.newevent.programservice.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {

    // Lets the frontend/API fetch just one event's agenda instead of
    // filtering the full list client-side - natural fit for the
    // "Our Programs" section, and Spring Data derives the query from the
    // method name alone, no custom SQL needed.
    List<Program> findByEventId(Long eventId);
}
