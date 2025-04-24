package com.spribe.services.units.booking.service.model.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {
    private Long userId;
    private Long unitId;
    private LocalDate startDate;
    private LocalDate endDate;
}
