package com.spribe.services.units.booking.service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spribe.services.units.booking.service.service.BookingService;
import com.spribe.services.units.booking.service.service.UnitService;
import com.spribe.services.units.booking.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@ActiveProfiles("test")
public abstract class BaseApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected BookingService bookingService;
    @MockBean
    protected UnitService unitService;
    @MockBean
    protected UserService userService;
}
