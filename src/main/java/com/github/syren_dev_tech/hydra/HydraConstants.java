package com.github.syren_dev_tech.hydra;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Holder class for several properties and/or handlers inherent to Hydra
 */
public final class HydraConstants {
    public static final Logger LOGGER = LogManager.getLogger("Hydra");
    public static final String MODID = "hydra";

    public static void init() {
        // Initialization complete
    }

    /**
     * Helper method to create a ResourceLocation predefined with Hydra's {@link #MODID}
     */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(HydraConstants.MODID, path);
    }

    /**
     * Throw an exception pertaining to a specific resource
     * <p>
     * This mostly serves as a helper for consistent formatting of exceptions
     *
     * @param resource The location or id of the resource the error pertains to
     * @param message The error message to display
     */
    public static RuntimeException exception(ResourceLocation resource, String message) {
        return new RuntimeException(resource + ": " + message);
    }

    /**
     * Throw an exception pertaining to a specific resource
     * <p>
     * This mostly serves as a helper for consistent formatting of exceptions
     *
     * @param resource The location or id of the resource the error pertains to
     * @param message The error message to display
     * @param exception The exception to throw
     */
    public static RuntimeException exception(ResourceLocation resource, String message, Throwable exception) {
        return new RuntimeException(resource + ": " + message, exception);
    }
}
