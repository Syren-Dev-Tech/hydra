package com.github.syren_dev_tech.hydra;

import com.github.syren_dev_tech.hydra.cache.HydraCache;

/**
 * Main Hydra client entrypoint
 */
public final class HydraClient {
    public static void init() {
        HydraCache.registerReloadListener();
    }
}
