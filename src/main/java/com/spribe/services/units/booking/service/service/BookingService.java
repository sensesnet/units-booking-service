package com.spribe.services.units.booking.service.service;

import com.spribe.services.units.booking.service.model.*;
import com.spribe.services.units.booking.service.model.repo.BookingRepository;
import com.spribe.services.units.booking.service.model.repo.PaymentRepository;
import com.spribe.services.units.booking.service.model.repo.UnitRepository;
import com.spribe.services.units.booking.service.model.repo.UserRepository;
import com.spribe.services.units.booking.service.model.request.BookingRequest;
import com.spribe.services.units.booking.service.service.exception.BookingNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final UnitService unitService;

    @Transactional
    public Booking createBooking(BookingRequest request) {
        var unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new BookingNotFoundException("Unit not found"));
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BookingNotFoundException("User not found"));

        boolean available = unitService.searchUnits(null,
                        null,
                        null,
                        null,
                        null,
                        request.getStartDate(),
                        request.getEndDate(),
                        0,
                        1,
                        "id")
                .stream().anyMatch(u -> u.getId().equals(unit.getId()));

        if (!available) {
            throw new IllegalStateException("Unit is not available for the selected dates");
        }

        return bookingRepository.save(Booking.builder()
                .unit(unit)
                .user(user)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(BookingStatus.CREATED)
                .paid(false)
                .build());
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingNotFoundException("Booking not [id]:" + bookingId));

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setPaid(false);
        bookingRepository.save(booking);
        unitService.updateAvailableUnitsCache();
    }

    @Transactional
    public Payment pay(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingNotFoundException("Booking not found [id]:" + bookingId));

        if (booking.isPaid()) {
            throw new IllegalStateException("Booking [id:" + bookingId + "] is already paid");
        }

        booking.setStatus(BookingStatus.PAID);
        booking.setPaid(true);
        bookingRepository.save(booking);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getUnit().getTotalCost());

        return paymentRepository.save(payment);
    }

    @Scheduled(fixedDelay = 60_000)
    public void cancelExpiredBookings() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(15);
        List<Booking> expired = bookingRepository.findByStatusAndPaidFalseAndCreatedAtBefore(
                BookingStatus.CREATED, expirationTime);

        for (Booking booking : expired) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
        }

        if (!expired.isEmpty()) {
            unitService.updateAvailableUnitsCache();
        }
    }
}
