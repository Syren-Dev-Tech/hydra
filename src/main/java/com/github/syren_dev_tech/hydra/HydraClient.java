package com.github.syren_dev_tech.hydra;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import com.github.syren_dev_tech.hydra.cache.HydraResources;

/// Main Hydra client entrypoint
@EventBusSubscriber(value = Dist.CLIENT, modid = HydraConstants.MODID)
public final class HydraClient {
    @SubscribeEvent
    public static void registerReloadListeners(final AddClientReloadListenersEvent ev) {
        ev.addListener(HydraResources.RELOAD_LISTENER_ID, new HydraResources());
    }
}
