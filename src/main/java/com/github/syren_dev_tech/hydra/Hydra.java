package com.github.syren_dev_tech.hydra;

import net.minecraft.core.registries.Registries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.github.syren_dev_tech.hydra.network.HydraNetworkingNeoForge;

@Mod(HydraConstants.MODID)
public final class Hydra {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS_REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, HydraConstants.MODID);

    public Hydra(IEventBus modBus) {
        HydraNetworkingNeoForge.init(modBus);
        DATA_COMPONENTS_REGISTER.register(modBus);
        HydraConstants.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
            HydraClient.init();
    }
}
