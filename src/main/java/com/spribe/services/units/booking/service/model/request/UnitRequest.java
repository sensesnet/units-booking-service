package com.spribe.services.units.booking.service.model.request;

import com.spribe.services.units.booking.service.model.AccommodationType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
@Builder(toBuilder = true)
@Value
public class UnitRequest {

    int numberOfRooms;
    AccommodationType accommodationType;
    int floor;
    BigDecimal baseCost;
    String description;
}
