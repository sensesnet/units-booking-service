package com.spribe.services.units.booking.service.infrastructure.data;

import com.spribe.services.units.booking.service.model.AccommodationType;
import com.spribe.services.units.booking.service.model.Unit;
import com.spribe.services.units.booking.service.model.repo.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class StartupDataLoader implements ApplicationRunner {

    private final UnitRepository unitRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (unitRepository.count() >= 100) return;

        Random random = new Random();

        List<Unit> units = IntStream.range(0, 90)
                .mapToObj(i -> Unit.builder()
                        .numberOfRooms(random.nextInt(5 - 1) + 1)
                        .accommodationType(randomAccommodation())
                        .floor(random.nextInt(20))
                        .description("RandomUnit_" + UUID.randomUUID())
                        .baseCost(BigDecimal.valueOf(random.nextInt(500 - 50) + 50))
                        .build())
                .collect(Collectors.toList());

        unitRepository.saveAll(units);
    }

    private AccommodationType randomAccommodation() {
        AccommodationType[] types = AccommodationType.values();
        return types[new Random().nextInt(types.length)];
    }
}
