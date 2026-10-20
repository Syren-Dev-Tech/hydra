package com.github.syren_dev_tech.hydra.animatable.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper class for HydraRenderProvider, to allow an externally held HydraRenderProvider to be used as the basis for this one
 * <p>
 * Should be anonymously instantiated for side-safety
 */
public interface DeferredHydraRenderProvider extends HydraRenderProvider {
    /**
     * Return the externally held HydraRenderProvider.
     * <p>
     * Normally this would be stored as a field in your item class
     */
    MutableObject<HydraRenderProvider> getRenderProvider();

    @Override
    @Nullable
    default BlockEntityWithoutLevelRenderer getHydraItemRenderer() {
        return getRenderProvider().getValue().getHydraItemRenderer();
    }

    @Override
    @Nullable
    default <T extends LivingEntity> HumanoidModel<?> getHydraArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
        return getRenderProvider().getValue().getHydraArmorRenderer(livingEntity, itemStack, equipmentSlot, original);
    }
}