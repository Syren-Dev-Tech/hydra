package com.github.syren_dev_tech.hydra;

import com.github.syren_dev_tech.hydra.service.HydraNetworking;
import com.github.syren_dev_tech.hydra.service.HydraPlatform;
import org.jspecify.annotations.Nullable;
import com.github.syren_dev_tech.hydra.service.*;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/// Service holder class for access to the various SPI platforms that Hydra uses
public final class HydraServices {
    public static final HydraPlatform PLATFORM = load(HydraPlatform.class);
    public static final HydraNetworking NETWORK = load(HydraNetworking.class);

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
