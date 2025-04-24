package com.spribe.services.units.booking.service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_log")
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    private String details;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}

