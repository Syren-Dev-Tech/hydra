package com.github.syren_dev_tech.hydra;

import org.jspecify.annotations.Nullable;
import com.github.syren_dev_tech.hydra.service.HydraClient;
import com.github.syren_dev_tech.hydra.service.HydraEvents;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/// Service holder class for access to the various SPI platforms that Hydra uses for client-specific functionality
public class HydraClientServices {
    public static final HydraEvents EVENTS = load(HydraEvents.class);
    public static final HydraClient ITEM_RENDERING = load(HydraClient.class);

    private static <T> T load(Class<T> clazz) {
        return load(clazz, null);
    }

    private static <T> T load(Class<T> clazz, @Nullable Supplier<T> defaultInstance) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .or(defaultInstance == null ? Optional::empty : () -> Optional.of(defaultInstance.get()))
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        HydraConstants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
