package com.github.syren_dev_tech.hydra.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.model.HydraModel;
import com.github.syren_dev_tech.hydra.service.HydraClient;

/**
 * Default implementation of HydraClient that provides no custom models
 * This is used as a fallback when no other HydraClient implementation is available
 */
public class DefaultHydraClient implements HydraClient {
    @NotNull
    @Override
    public <T extends LivingEntity & HydraAnimatable> HumanoidModel<?> getArmorModelForItem(T animatable, ItemStack stack, EquipmentSlot slot, HumanoidModel<LivingEntity> defaultModel) {
        return defaultModel;
    }

    @Nullable
    @Override
    public HydraModel<?> getHydraModelForItem(ItemStack item) {
        return null;
    }

    @Nullable
    @Override
    public HydraModel<?> getHydraModelForArmor(ItemStack armour) {
        return null;
    }
}
