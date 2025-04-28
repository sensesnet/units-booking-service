package com.spribe.services.units.booking.service.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Setter
@Getter
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Booking booking;

    private BigDecimal amount;

    private LocalDateTime paidAt;

    @PrePersist
    public void prePersist() {
        paidAt = LocalDateTime.now();
    }
}

