package com.github.syren_dev_tech.hydra;

import com.github.syren_dev_tech.hydra.service.HydraEvents;
import com.github.syren_dev_tech.hydra.service.HydraNetworking;
import com.github.syren_dev_tech.hydra.service.HydraPlatform;
import com.github.syren_dev_tech.hydra.service.HydraClient;

import java.util.ServiceLoader;

public final class HydraServices {
    public static final HydraPlatform PLATFORM = load(HydraPlatform.class);
    public static final HydraNetworking NETWORK = load(HydraNetworking.class);

    public static class Client {
        public static final HydraEvents EVENTS = load(HydraEvents.class);
        public static final HydraClient ITEM_RENDERING = load(HydraClient.class);
    }

    private static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        HydraConstants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
