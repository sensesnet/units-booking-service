package com.spribe.services.units.booking.service.api.internal.v1;

import com.spribe.services.units.booking.service.model.AccommodationType;
import com.spribe.services.units.booking.service.model.Unit;
import com.spribe.services.units.booking.service.model.request.UnitRequest;
import com.spribe.services.units.booking.service.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.spribe.services.units.booking.service.api.RestApiConstants.UNIT_AVAILABLE_COUNT_SUBPATH;
import static com.spribe.services.units.booking.service.api.RestApiConstants.UNIT_PATH;

@RestController
@RequestMapping(UNIT_PATH)
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @PostMapping
    public ResponseEntity<Unit> create(@RequestBody UnitRequest request) {
        return ResponseEntity.ok(unitService.createUnit(request));
    }

    @GetMapping
    public ResponseEntity<List<Unit>> search(
            @RequestParam(required = false) Integer rooms,
            @RequestParam(required = false) AccommodationType type,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) BigDecimal minCost,
            @RequestParam(required = false) BigDecimal maxCost,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(unitService.searchUnits(rooms,
                type,
                floor,
                minCost,
                maxCost,
                start,
                end,
                page,
                size,
                sortBy));
    }

    @GetMapping(path = UNIT_AVAILABLE_COUNT_SUBPATH)
    public ResponseEntity<Long> countAvailable() {
       return ResponseEntity.ok(unitService.countAvailableUnits());
    }
}

