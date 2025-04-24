package com.spribe.services.units.booking.service.service;

import com.spribe.services.units.booking.service.model.AccommodationType;
import com.spribe.services.units.booking.service.model.Booking;
import com.spribe.services.units.booking.service.model.BookingStatus;
import com.spribe.services.units.booking.service.model.Unit;
import com.spribe.services.units.booking.service.model.repo.BookingRepository;
import com.spribe.services.units.booking.service.model.repo.UnitRepository;
import com.spribe.services.units.booking.service.model.request.UnitRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;
    private final BookingRepository bookingRepository;
    private final CacheManager cacheManager;

    public Unit createUnit(UnitRequest request) {
        Unit saved = unitRepository.save(Unit.builder()
                .floor(request.getFloor())
                .accommodationType(request.getAccommodationType())
                .baseCost(request.getBaseCost())
                .bookings(request.getBookings())
                .description(request.getDescription())
                .numberOfRooms(request.getNumberOfRooms())
                .build());
        updateAvailableUnitsCache();
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
        Cache cache = cacheManager.getCache("availableUnits");
        Long count = cache != null ? cache.get("count", Long.class) : null;

        if (count == null) {
            count = unitRepository.findAll().stream()
                    .filter(unit -> isAvailable(unit, LocalDate.now(), LocalDate.now().plusDays(30)))
                    .count();
            if (cache != null) cache.put("count", count);
        }

        return count;
    }

    public void updateAvailableUnitsCache() {
        if (cacheManager.getCache("availableUnits") != null) {
            Objects.requireNonNull(cacheManager.getCache("availableUnits"))
                    .put("count", countAvailableUnits());
        }
    }

    private boolean isAvailable(Unit unit, LocalDate start, LocalDate end) {
        List<Booking> bookings = bookingRepository.findByUnitIdAndStatusIn(
                unit.getId(),
                List.of(BookingStatus.CREATED, BookingStatus.PAID)
        );
        return bookings.stream().noneMatch(booking ->
                !booking.getStartDate().isAfter(end) && !booking.getEndDate().isBefore(start)
        );
    }
}

