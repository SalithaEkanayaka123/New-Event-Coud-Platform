package com.newevent.programservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Program entity - matches the brief's exact required fields:
 * Day, Track, Session, Speaker Name, and Times.
 * (Track = category, e.g. "Cloud Computing Track" - per the brief's own example)
 *
 * Design notes (for the report / viva):
 * - eventId is a plain Long, NOT a JPA @ManyToOne relationship to Event.
 *   Program Service and Event Service are separate microservices; a real
 *   foreign key would couple their schemas together, which defeats the
 *   point of splitting them in the first place. If Program Service ever
 *   needs full event details, it calls Event Service's API - it doesn't
 *   join across a shared table.
 * - "day" is a String (e.g. "Day 1"), not a LocalDate. The brief doesn't
 *   specify whether this means a literal calendar date or just a day
 *   number within a multi-day conference - a String is the simplest
 *   representation that doesn't force an assumption either way.
 * - startTime/endTime are LocalTime, not LocalDateTime, since the actual
 *   date is already covered by "day" - Times just needs time-of-day.
 */
@Entity
@Table(name = "programs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Event ID is required")
    @Column(name = "event_id")
    private Long eventId;

    @NotBlank(message = "Day is required")
    private String day;

    @NotBlank(message = "Track is required")
    private String track;

    @NotBlank(message = "Session is required")
    private String session;

    @NotBlank(message = "Speaker name is required")
    @Column(name = "speaker_name")
    private String speakerName;

    @NotNull(message = "Start time is required")
    @Column(name = "start_time")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @Column(name = "end_time")
    private LocalTime endTime;
}
