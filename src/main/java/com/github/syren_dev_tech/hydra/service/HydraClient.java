package com.github.syren_dev_tech.hydra.service;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.model.HydraModel;

/**
 * Loader-agnostic service interface for clientside functionalities
 */
public interface HydraClient {
    /**
     * Helper method for retrieving an (ideally) cached instance of the armor model for a given Item
     * <p>
     * If no custom model applies to this item, the {@code defaultModel} is returned
     */
    @NotNull
    <T extends LivingEntity & HydraAnimatable> HumanoidModel<?> getArmorModelForItem(T animatable, ItemStack stack, EquipmentSlot slot, HumanoidModel<LivingEntity> defaultModel);

    /**
     * Helper method for retrieving an (ideally) cached instance of the HydraModel for the given item
     *
     * @return The HydraModel for the item, or null if not applicable
     */
    @Nullable
    HydraModel<?> getHydraModelForItem(ItemStack item);

    /**
     * Helper method for retrieving an (ideally) cached instance of the HydraModel for the given item's armor renderer
     *
     * @return The HydraModel for the item, or null if not applicable
     */
    @Nullable
    HydraModel<?> getHydraModelForArmor(ItemStack armour);
}
