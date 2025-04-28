package com.spribe.services.units.booking.service.api.internal.v1;

import com.spribe.services.units.booking.service.api.BaseApiControllerTest;
import com.spribe.services.units.booking.service.model.AccommodationType;
import com.spribe.services.units.booking.service.model.Unit;
import com.spribe.services.units.booking.service.model.request.UnitRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UnitControllerTest extends BaseApiControllerTest {
    public static final String UNIT_BOOKING_BASE_PATH = "/units-booking-api/internal/v1";
    public static final String UNIT_PATH = UNIT_BOOKING_BASE_PATH + "/api/units";
    public static final String UNIT_AVAILABLE_COUNT_SUBPATH = "/available/count";
    public static final String APPLICATION_JSON = "application/json";
    public static final Long TEST_UNIT_ID_1 = 1L;
    public static final Long TEST_UNIT_ID_2 = 2L;
    public static final Long TEST_UNIT_AVAILABLE_COUNT = 5L;

    @Test
    void performCreateUnit() throws Exception {
        UnitRequest request = UnitRequest.builder()
                .floor(2)
                .accommodationType(AccommodationType.APARTMENTS)
                .baseCost(new BigDecimal("150.00"))
                .description("Luxury Apartment")
                .numberOfRooms(3)
                .build();

        Unit unit = Unit.builder()
                .id(TEST_UNIT_ID_1)
                .floor(2)
                .accommodationType(AccommodationType.APARTMENTS)
                .baseCost(new BigDecimal("150.00"))
                .description("Luxury Apartment")
                .numberOfRooms(3)
                .build();

        Mockito.when(unitService.createUnit(Mockito.any())).thenReturn(unit);

        mockMvc.perform(post(UNIT_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON));
    }

    @Test
    void shouldReturnUnits() throws Exception {
        List<Unit> units = List.of(
                Unit.builder()
                        .id(TEST_UNIT_ID_1)
                        .floor(2)
                        .accommodationType(AccommodationType.APARTMENTS)
                        .baseCost(new BigDecimal("100.00"))
                        .description("Standard Apartment")
                        .numberOfRooms(2)
                        .build(),
                Unit.builder()
                        .id(TEST_UNIT_ID_2)
                        .floor(3)
                        .accommodationType(AccommodationType.APARTMENTS)
                        .baseCost(new BigDecimal("200.00"))
                        .description("Luxury Apartment")
                        .numberOfRooms(4)
                        .build()
        );

        Mockito.when(unitService.searchUnits(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(),
                Mockito.anyInt(), Mockito.anyString()
        )).thenReturn(units);

        mockMvc.perform(get(UNIT_PATH)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON));
    }

    @Test
    void shouldCountAndReturnAvailableCount() throws Exception {
        Mockito.when(unitService.countAvailableUnits()).thenReturn(TEST_UNIT_AVAILABLE_COUNT);

        mockMvc.perform(get(UNIT_PATH + UNIT_AVAILABLE_COUNT_SUBPATH))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(TEST_UNIT_AVAILABLE_COUNT)));
    }
}