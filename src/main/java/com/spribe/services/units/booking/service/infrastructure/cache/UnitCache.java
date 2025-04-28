package com.spribe.services.units.booking.service.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class UnitCache {

    private static final String CACHE_NAME = "availableUnits";
    private static final String CACHE_KEY = "count";
    private static final String CACHE_FILE_PATH = "available-units-cache.json";

    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void loadCacheFromFile() {
        try {
            File file = new File(CACHE_FILE_PATH);
            if (file.exists()) {
                Long count = objectMapper.readValue(file, Long.class);
                Cache cache = cacheManager.getCache(CACHE_NAME);
                if (cache != null) {
                    cache.put(CACHE_KEY, count);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCache(Supplier<Long> countSupplier) {
        long count = countSupplier.get();
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.put(CACHE_KEY, count);
            saveCacheToFile(count);
        }
    }

    public long getCount(Supplier<Long> countSupplier) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        Long count = cache != null ? cache.get(CACHE_KEY, Long.class) : null;

        if (count == null) {
            count = countSupplier.get();
            if (cache != null) {
                cache.put(CACHE_KEY, count);
                saveCacheToFile(count);
            }
        }
        return count;
    }

    private void saveCacheToFile(Long count) {
        try {
            objectMapper.writeValue(new File(CACHE_FILE_PATH), count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
