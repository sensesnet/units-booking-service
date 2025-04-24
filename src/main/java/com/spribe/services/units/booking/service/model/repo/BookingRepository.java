package com.spribe.services.units.booking.service.model.repo;

import com.spribe.services.units.booking.service.model.Booking;
import com.spribe.services.units.booking.service.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUnitIdAndStatusIn(Long unitId, List<BookingStatus> statuses);

    List<Booking> findByStatusAndPaidFalseAndCreatedAtBefore(BookingStatus status, LocalDateTime deadline);
}
