package com.spribe.services.units.booking.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@JsonIgnoreProperties({"bookings"})
@Table(name = "units")
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numberOfRooms;

    @Enumerated(EnumType.STRING)
    private AccommodationType accommodationType;

    private int floor;

    private BigDecimal baseCost;

    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    public BigDecimal getTotalCost() {
        return baseCost.add(baseCost.multiply(BigDecimal.valueOf(0.15)));
    }
}
