package com.spribe.services.units.booking.service.api.internal.v1;

import com.spribe.services.units.booking.service.model.Booking;
import com.spribe.services.units.booking.service.model.request.BookingRequest;
import com.spribe.services.units.booking.service.model.Payment;
import com.spribe.services.units.booking.service.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.spribe.services.units.booking.service.api.RestApiConstants.BOOKING_CANCEL_SUBPATH;
import static com.spribe.services.units.booking.service.api.RestApiConstants.BOOKING_PATH;
import static com.spribe.services.units.booking.service.api.RestApiConstants.BOOKING_PAY_SUBPATH;

@RestController
@RequestMapping(path = BOOKING_PATH)
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Booking> create(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @PostMapping(path = BOOKING_CANCEL_SUBPATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id) {
        bookingService.cancelBooking(id);
    }

    @PostMapping(path = BOOKING_PAY_SUBPATH)
    public ResponseEntity<Payment> pay(@PathVariable Long id) {
        Payment payment = bookingService.pay(id);
        return ResponseEntity.ok(payment);
    }
}

