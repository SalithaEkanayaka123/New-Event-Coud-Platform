package com.newevent.programservice.controller;

import com.newevent.programservice.entity.Program;
import com.newevent.programservice.exception.ProgramNotFoundException;
import com.newevent.programservice.repository.ProgramRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for Program Service. Same FAQ exception as Event Service applies
 * - no frontend integration required, demonstrable via Postman/Hoppscotch.
 */
@RestController
@RequestMapping("/api/programs")
public class ProgramController {

    private final ProgramRepository programRepository;

    public ProgramController(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    @GetMapping
    public List<Program> getAllPrograms() {
        return programRepository.findAll();
    }

    @GetMapping("/{id}")
    public Program getProgram(@PathVariable Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new ProgramNotFoundException(id));
    }

    // e.g. GET /api/programs/by-event/1 - all agenda items for event 1
    @GetMapping("/by-event/{eventId}")
    public List<Program> getProgramsByEvent(@PathVariable Long eventId) {
        return programRepository.findByEventId(eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Program createProgram(@Valid @RequestBody Program program) {
        program.setId(null);
        return programRepository.save(program);
    }

    @PutMapping("/{id}")
    public Program updateProgram(@PathVariable Long id, @Valid @RequestBody Program updated) {
        Program existing = programRepository.findById(id)
                .orElseThrow(() -> new ProgramNotFoundException(id));

        existing.setEventId(updated.getEventId());
        existing.setDay(updated.getDay());
        existing.setTrack(updated.getTrack());
        existing.setSession(updated.getSession());
        existing.setSpeakerName(updated.getSpeakerName());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());

        return programRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProgram(@PathVariable Long id) {
        if (!programRepository.existsById(id)) {
            throw new ProgramNotFoundException(id);
        }
        programRepository.deleteById(id);
    }
}
