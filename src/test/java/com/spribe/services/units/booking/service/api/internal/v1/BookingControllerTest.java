package com.spribe.services.units.booking.service.api.internal.v1;

import com.spribe.services.units.booking.service.api.BaseApiControllerTest;
import com.spribe.services.units.booking.service.model.AccommodationType;
import com.spribe.services.units.booking.service.model.Booking;
import com.spribe.services.units.booking.service.model.BookingStatus;
import com.spribe.services.units.booking.service.model.Payment;
import com.spribe.services.units.booking.service.model.User;
import com.spribe.services.units.booking.service.model.Unit;
import com.spribe.services.units.booking.service.model.request.BookingRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BookingControllerTest extends BaseApiControllerTest {
    public static final String UNIT_BOOKING_BASE_PATH = "/units-booking-api/internal/v1";
    public static final String BOOKING_PATH = UNIT_BOOKING_BASE_PATH + "/api/bookings";
    public static final String BOOKING_CANCEL_SUBPATH = "/cancel";
    public static final String BOOKING_PAY_SUBPATH = "/pay";
    public static final Long TEST_BOOKING_ID = 1L;
    public static final Long TEST_UNIT_ID = 101L;
    public static final Long TEST_USER_ID = 201L;

    @Test
    void performCreateBooking() throws Exception {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setName("Test User");
        user.setEmail("testuser@example.com");

        Unit unit = new Unit();
        unit.setId(TEST_UNIT_ID);
        unit.setFloor(2);
        unit.setAccommodationType(AccommodationType.APARTMENTS);
        unit.setBaseCost(new BigDecimal("150.00"));
        unit.setDescription("Luxury Apartment");
        unit.setNumberOfRooms(3);

        BookingRequest request = BookingRequest.builder()
                .unitId(TEST_UNIT_ID)
                .userId(TEST_USER_ID)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(10))
                .build();

        Booking booking = Booking.builder()
                .id(TEST_BOOKING_ID)
                .user(user)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(10))
                .status(BookingStatus.CREATED)
                .build();

        Mockito.when(bookingService.createBooking(Mockito.any())).thenReturn(booking);

        mockMvc.perform(post(BOOKING_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void performCancelBooking() throws Exception {
        mockMvc.perform(post(BOOKING_PATH + "/" + TEST_BOOKING_ID + BOOKING_CANCEL_SUBPATH))
                .andExpect(status().isNoContent());

        Mockito.verify(bookingService).cancelBooking(TEST_BOOKING_ID);
    }

    @Test
    void performPayBooking() throws Exception {
        Payment payment = Payment.builder()
                .id(TEST_BOOKING_ID)
                .amount(new BigDecimal(500.00))
                .build();

        Mockito.when(bookingService.pay(TEST_BOOKING_ID)).thenReturn(payment);

        mockMvc.perform(post(BOOKING_PATH + "/" + TEST_BOOKING_ID + BOOKING_PAY_SUBPATH))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}