package com.github.syren_dev_tech.hydra;

import com.github.syren_dev_tech.hydra.service.HydraEvents;
import com.github.syren_dev_tech.hydra.service.HydraClient;
import java.util.ServiceLoader;

/**
 * Service provider for Hydra library utilities.
 *
 * THIS IS A LIBRARY - not a standalone mod. The importing mod MUST provide implementations for all services before using any Hydra functionality.
 *
 * The importing mod should: 1. Create implementations of HydraEvents and HydraClient 2. Call setters to register implementations 3. Call {@link #init()} on the library after all services are registered
 */
public final class HydraServices {
    private HydraServices() {
        /* This utility class should not be instantiated */
    }

    public static class Client {
        private Client() {
            /* This utility class should not be instantiated */
        }

        private static HydraEvents cachedEvents = null;
        private static HydraClient cachedClient = null;

        public static HydraEvents getEvents() {
            if (cachedEvents == null) {
                cachedEvents = load(HydraEvents.class);
            }
            return cachedEvents;
        }

        public static HydraClient getItemRendering() {
            if (cachedClient == null) {
                cachedClient = load(HydraClient.class);
            }
            return cachedClient;
        }

        private static <T> T load(Class<T> clazz) {
            final T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
            HydraConstants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

            return loadedService;
        }
    }
}
