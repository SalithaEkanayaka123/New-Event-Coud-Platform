package com.newevent.eventservice.repository;

import com.newevent.eventservice.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // JpaRepository already gives us findAll, findById, save, deleteById -
    // that covers the brief's "stores and updates event details" requirement
    // without needing any custom queries yet.
}
