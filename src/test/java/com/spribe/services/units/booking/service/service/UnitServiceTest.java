package com.spribe.services.units.booking.service.service;

import com.spribe.services.units.booking.service.infrastructure.cache.UnitCache;
import com.spribe.services.units.booking.service.model.AccommodationType;
import com.spribe.services.units.booking.service.model.Booking;
import com.spribe.services.units.booking.service.model.BookingStatus;
import com.spribe.services.units.booking.service.model.Unit;
import com.spribe.services.units.booking.service.model.repo.BookingRepository;
import com.spribe.services.units.booking.service.model.repo.UnitRepository;
import com.spribe.services.units.booking.service.model.request.UnitRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UnitCache unitCache;

    @InjectMocks
    private UnitService unitService;

    private Unit unit;
    private Booking booking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        unit = Unit.builder()
                .id(1L)
                .floor(2)
                .accommodationType(AccommodationType.APARTMENTS)
                .baseCost(new BigDecimal("100.00"))
                .description("Test unit")
                .numberOfRooms(3)
                .build();

        booking = Booking.builder()
                .id(1L)
                .unit(unit)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .status(BookingStatus.CREATED)
                .build();
    }

    @Test
    void shouldCreateUnitSuccessfully() {
        UnitRequest request = UnitRequest.builder()
                .floor(2)
                .accommodationType(AccommodationType.APARTMENTS)
                .baseCost(new BigDecimal("100.00"))
                .description("Test unit")
                .numberOfRooms(3)
                .build();

        when(unitRepository.save(any(Unit.class))).thenReturn(unit);

        Unit createdUnit = unitService.createUnit(request);

        assertThat(createdUnit).isNotNull();
        assertThat(createdUnit.getDescription()).isEqualTo("Test unit");
        assertThat(createdUnit.getBaseCost()).isEqualTo(new BigDecimal("100.00"));
        verify(unitRepository).save(any(Unit.class));
        verify(unitCache).updateCache(any());
    }

    @Test
    void shouldSearchUnitsSuccessfully() {
        Unit unit = Unit.builder()
                .id(1L)
                .accommodationType(AccommodationType.APARTMENTS)
                .numberOfRooms(3)
                .floor(2)
                .baseCost(new BigDecimal("100.00"))
                .build();

        Booking booking = Booking.builder()
                .id(unit.getId())
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .status(BookingStatus.PAID)
                .build();

        List<Unit> units = Arrays.asList(unit);
        Page<Unit> unitPage = new PageImpl<>(units);

        when(unitRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(unitPage);

        when(bookingRepository.findByUnitIdAndStatusIn(anyLong(), anyList()))
                .thenReturn(Arrays.asList(booking));

        List<Unit> result = unitService.searchUnits(
                3,
                AccommodationType.APARTMENTS,
                2,
                new BigDecimal("50.00"),
                new BigDecimal("150.00"),
                LocalDate.now().plusDays(6),
                LocalDate.now().plusDays(10),
                0,
                10,
                "baseCost"
        );

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(unit.getId());
        verify(unitRepository).findAll(any(Specification.class), any(PageRequest.class));
        verify(bookingRepository).findByUnitIdAndStatusIn(anyLong(), anyList());
    }

    @Test
    void shouldSearchOverlappingBookingsAndReturnEmptySuccessfully() {
        Unit unit = Unit.builder()
                .id(1L)
                .accommodationType(AccommodationType.APARTMENTS)
                .numberOfRooms(3)
                .floor(2)
                .baseCost(new BigDecimal("100.00"))
                .build();

        Booking booking = Booking.builder()
                .id(unit.getId())
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .status(BookingStatus.PAID)
                .build();

        List<Unit> units = Arrays.asList(unit);
        Page<Unit> unitPage = new PageImpl<>(units);

        when(unitRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(unitPage);

        when(bookingRepository.findByUnitIdAndStatusIn(anyLong(), anyList()))
                .thenReturn(Arrays.asList(booking));

        List<Unit> result = unitService.searchUnits(
                3,
                AccommodationType.APARTMENTS,
                2,
                new BigDecimal("50.00"),
                new BigDecimal("150.00"),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                0,
                10,
                "baseCost"
        );

        assertThat(result).isEmpty();
    }

    @Test
    void shouldCountAvailableUnitsSuccessfully() {
        when(unitCache.getCount(any())).thenReturn(10L);

        long availableUnits = unitService.countAvailableUnits();

        assertThat(availableUnits).isEqualTo(10L);
        verify(unitCache).getCount(any());
    }
}