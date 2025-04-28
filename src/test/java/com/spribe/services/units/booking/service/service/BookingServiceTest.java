package com.spribe.services.units.booking.service.service;

import com.spribe.services.units.booking.service.infrastructure.cache.UnitCache;
import com.spribe.services.units.booking.service.model.Booking;
import com.spribe.services.units.booking.service.model.BookingStatus;
import com.spribe.services.units.booking.service.model.Payment;
import com.spribe.services.units.booking.service.model.Unit;
import com.spribe.services.units.booking.service.model.User;
import com.spribe.services.units.booking.service.model.repo.BookingRepository;
import com.spribe.services.units.booking.service.model.repo.PaymentRepository;
import com.spribe.services.units.booking.service.model.repo.UnitRepository;
import com.spribe.services.units.booking.service.model.repo.UserRepository;
import com.spribe.services.units.booking.service.model.request.BookingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private UnitService unitService;
    @Mock
    private UnitCache unitCache;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateBookingSuccessfully() {
        Long unitId = 33L;
        Long userId = 3L;
        BookingRequest request = BookingRequest.builder()
                .unitId(unitId)
                .userId(userId)
                .startDate(LocalDate.now().plusDays(6))
                .endDate(LocalDate.now().plusDays(8))
                .build();

        Unit unit = new Unit();
        unit.setId(unitId);
        User user = new User();
        user.setId(userId);

        when(unitRepository.findById(unitId)).thenReturn(Optional.of(unit));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(unitService.searchUnits(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any()))
                .thenReturn(List.of(unit));

        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        Booking result = bookingService.createBooking(request);

        assertThat(result).isNotNull();
        assertThat(result.getUnit()).isEqualTo(unit);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.CREATED);

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void shouldThrowIllegalStateExceptionIfCreateBookingNotAvailable() {
        Long unitId = 1L;
        Long userId = 2L;
        BookingRequest request = BookingRequest.builder()
                .unitId(unitId)
                .userId(userId)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .build();

        Unit unit = new Unit();
        unit.setId(unitId);
        User user = new User();
        user.setId(userId);

        when(unitRepository.findById(unitId)).thenReturn(Optional.of(unit));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(unitService.searchUnits(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any()))
                .thenReturn(List.of());

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Unit is not available");
    }

    @Test
    void shouldCancelBookingSuccessfully() {
        Long bookingId = 1L;
        Booking booking = Booking.builder()
                .id(bookingId)
                .status(BookingStatus.CREATED)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(bookingId);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository).save(booking);
        verify(unitCache).updateCache(any());
    }

    @Test
    void performPaySuccessfully() {
        Long bookingId = 1L;
        Unit unit = Unit.builder()
                .baseCost(new BigDecimal(100.0))
                .build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .status(BookingStatus.CREATED)
                .paid(false)
                .unit(unit)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(paymentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Payment payment = bookingService.pay(bookingId);

        assertThat(payment).isNotNull();
        assertThat(payment.getAmount())
                .usingComparator(BigDecimal::compareTo)
                .isEqualTo(new BigDecimal(115));
        assertThat(payment.getBooking()).isEqualTo(booking);
        verify(bookingRepository).save(booking);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void shouldThrowIllegalStateExceptionIfPayAlreadyPaidBooking() {
        Long bookingId = 1L;
        Booking booking = Booking.builder()
                .id(bookingId)
                .status(BookingStatus.PAID)
                .paid(true)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.pay(bookingId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already paid");
    }

    @Test
    void shouldCancelExpiredBookingsSuccessfully() {
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.CREATED)
                .paid(false)
                .createdAt(LocalDateTime.now().minusMinutes(20))
                .build();

        when(bookingRepository.findByStatusAndPaidFalseAndCreatedAtBefore(
                eq(BookingStatus.CREATED), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        bookingService.cancelExpiredBookings();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository).save(booking);
        verify(unitCache).updateCache(any());
    }
}
