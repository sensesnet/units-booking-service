package com.spribe.services.units.booking.service.model.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder(toBuilder = true)
@Data
public class BookingRequest {
    private Long userId;
    private Long unitId;
    private LocalDate startDate;
    private LocalDate endDate;
}
