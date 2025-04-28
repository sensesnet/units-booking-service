package com.spribe.services.units.booking.service.service;

import com.spribe.services.units.booking.service.infrastructure.cache.UnitCache;
import com.spribe.services.units.booking.service.model.AccommodationType;
import com.spribe.services.units.booking.service.model.Booking;
import com.spribe.services.units.booking.service.model.BookingStatus;
import com.spribe.services.units.booking.service.model.Unit;
import com.spribe.services.units.booking.service.model.repo.BookingRepository;
import com.spribe.services.units.booking.service.model.repo.UnitRepository;
import com.spribe.services.units.booking.service.model.request.UnitRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;
    private final BookingRepository bookingRepository;
    private final UnitCache unitCache;

    public Unit createUnit(UnitRequest request) {
        Unit saved = unitRepository.save(Unit.builder()
                .floor(request.getFloor())
                .accommodationType(request.getAccommodationType())
                .baseCost(request.getBaseCost())
                .description(request.getDescription())
                .numberOfRooms(request.getNumberOfRooms())
                .build());
        unitCache.updateCache(this::calculateAvailableUnits);
        return saved;
    }

    public List<Unit> searchUnits(Integer rooms,
                                  AccommodationType type,
                                  Integer floor,
                                  BigDecimal minCost,
                                  BigDecimal maxCost,
                                  LocalDate startDate,
                                  LocalDate endDate,
                                  int page,
                                  int size,
                                  String sortBy) {
        Specification<Unit> spec = Specification.where(null);

        if (rooms != null)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("numberOfRooms"), rooms));
        if (type != null)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("accommodationType"), type));
        if (floor != null)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("floor"), floor));
        if (minCost != null)
            spec = spec.and((root, query, cb) -> cb.ge(root.get("baseCost"), minCost));
        if (maxCost != null)
            spec = spec.and((root, query, cb) -> cb.le(root.get("baseCost"), maxCost));

        Page<Unit> result = unitRepository
                .findAll(spec, PageRequest.of(page, size, Sort.by(sortBy)));

        return result.getContent().stream()
                .filter(unit -> isAvailable(unit, startDate, endDate))
                .toList();
    }

    public long countAvailableUnits() {
        return unitCache.getCount(this::calculateAvailableUnits);
    }

    private long calculateAvailableUnits() {
        return unitRepository.findAll().stream()
                .filter(unit -> isAvailable(unit, LocalDate.now(), LocalDate.now().plusDays(30)))
                .count();
    }

    private boolean isAvailable(Unit unit, LocalDate start, LocalDate end) {
        List<Booking> bookings = bookingRepository.findByUnitIdAndStatusIn(
                unit.getId(),
                List.of(BookingStatus.CREATED, BookingStatus.PAID)
        );

        if (bookings.isEmpty()) {
            return true;
        }

        return bookings.stream().noneMatch(booking ->
                !(booking.getEndDate().isBefore(start) || booking.getStartDate().isAfter(end))
        );
    }
}

