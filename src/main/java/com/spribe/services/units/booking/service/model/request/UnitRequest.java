package com.spribe.services.units.booking.service.model.request;

import com.spribe.services.units.booking.service.model.AccommodationType;
import com.spribe.services.units.booking.service.model.Booking;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class UnitRequest {

    private int numberOfRooms;
    private AccommodationType accommodationType;
    private int floor;
    private BigDecimal baseCost;
    private String description;
    private List<Booking> bookings = new ArrayList<>();
}
