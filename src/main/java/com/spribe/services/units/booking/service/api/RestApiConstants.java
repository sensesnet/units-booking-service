package com.spribe.services.units.booking.service.api;

public class RestApiConstants {
    public static final String UNIT_BOOKING_BASE_PATH = "/units-booking-api/internal/v1";
    public static final String BOOKING_PATH = UNIT_BOOKING_BASE_PATH + "/api/bookings";
    public static final String UNIT_PATH = UNIT_BOOKING_BASE_PATH + "/api/units";

    public static final String BOOKING_CANCEL_SUBPATH = "/{id}/cancel";
    public static final String BOOKING_PAY_SUBPATH = "/{id}/pay";

    public static final String UNIT_AVAILABLE_COUNT_SUBPATH = "/available/count";

}
