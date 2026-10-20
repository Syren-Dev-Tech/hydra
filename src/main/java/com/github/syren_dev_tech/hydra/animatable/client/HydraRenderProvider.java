package com.github.syren_dev_tech.hydra.animatable.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.HydraItem;
import com.github.syren_dev_tech.hydra.renderer.HydraArmorRenderer;
import com.github.syren_dev_tech.hydra.renderer.HydraItemRenderer;

/**
 * Internal interface for safely providing a custom item and armor renderer instances at runtime
 * <p>
 * This can be safely instantiated as a new anonymous class inside your {@link Item} class
 * <p>
 * This is a functional equivalent to Forge's {@code IClientItemExtensions}. If on Forge, either can be used
 *
 * @see com.github.syren_dev_tech.hydra.renderer.HydraItemRenderer HydraItemRenderer
 * @see com.github.syren_dev_tech.hydra.renderer.HydraArmorRenderer HydraArmorRenderer
 */
public interface HydraRenderProvider {
    HydraRenderProvider DEFAULT = new HydraRenderProvider() {};

    /**
     * Get the HydraRenderProvider instance for the given ItemStack, if applicable
     *
     * @param itemStack The ItemStack to get the provider instance for
     * @return The HydraRenderProvider instance for this stack, or a defaulted empty instance if not defined
     */
    static HydraRenderProvider of(ItemStack itemStack) {
        return of(itemStack.getItem());
    }

    /**
     * Get the HydraRenderProvider instance for the given Item, if applicable
     *
     * @param item The ItemStack to get the provider instance for
     * @return The HydraRenderProvider instance for this item, or a defaulted empty instance if not defined
     */
    static HydraRenderProvider of(Item item) {
        if (item instanceof HydraItem hydraItem)
            return (HydraRenderProvider)hydraItem.getRenderProvider();

        return DEFAULT;
    }

    /**
     * Get the cached {@link BlockEntityWithoutLevelRenderer} instance for this provider.
     * <p>
     * Normally this would be an instance of {@link HydraItemRenderer}
     *
     * @return The cached BEWLR instance for this provider, or null if not applicable
     */
    @Nullable
    default BlockEntityWithoutLevelRenderer getHydraItemRenderer() {
        return null;
    }

    /**
     * Get the cached {@link HydraArmorRenderer} instance for this provider.
     * <p>
     * Normally this would be an instance of {@link HydraArmorRenderer}
     *
     * @param livingEntity The entity currently wearing the item, if applicable
     * @param itemStack The ItemStack for this provider
     * @param equipmentSlot The slot the ItemStack is currently in, if applicable
     * @param original The base HumanoidModel (usually the default vanilla armor model), if applicable
     * @return The cached HumanoidModel instance for this provider, or null if not applicable
     */
    @Nullable
    default <T extends LivingEntity> HumanoidModel<?> getHydraArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
        return null;
    }
}