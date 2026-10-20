package com.github.syren_dev_tech.hydra.platform;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.animatable.client.HydraRenderProvider;
import com.github.syren_dev_tech.hydra.model.HydraModel;
import com.github.syren_dev_tech.hydra.renderer.HydraArmorRenderer;
import com.github.syren_dev_tech.hydra.renderer.HydraRenderer;
import com.github.syren_dev_tech.hydra.service.HydraClient;

/**
 * NeoForge service implementation for clientside functionalities
 */
public class HydraClientNeoForge implements HydraClient {
    /**
     * Helper method for retrieving an (ideally) cached instance of the armor model for a given Item
     * <p>
     * If no custom model applies to this item, the {@code defaultModel} is returned
     */
    @NotNull
    @Override
    public <T extends LivingEntity & HydraAnimatable> HumanoidModel<?> getArmorModelForItem(T animatable, ItemStack stack, EquipmentSlot slot, HumanoidModel<LivingEntity> defaultModel) {
        Item item = stack.getItem();
        HumanoidModel<?> model = IClientItemExtensions.of(item).getHumanoidArmorModel(animatable, stack, slot, defaultModel);

        if (model == defaultModel && HydraRenderProvider.of(item).getHydraArmorRenderer(animatable, stack, slot, defaultModel) instanceof HydraArmorRenderer<?> hydraArmorRenderer)
            return hydraArmorRenderer;

        return model;
    }

    /**
     * Helper method for retrieving an (ideally) cached instance of the HydraModel for the given item
     *
     * @return The HydraModel for the item, or null if not applicable
     */
    @Nullable
    @Override
    public HydraModel<?> getHydraModelForItem(ItemStack item) {
        if (IClientItemExtensions.of(item).getCustomRenderer() instanceof HydraRenderer<?> hydraRenderer)
            return hydraRenderer.getHydraModel();

        if (HydraRenderProvider.of(item).getHydraItemRenderer() instanceof HydraRenderer<?> hydraRenderer)
            return hydraRenderer.getHydraModel();

        return null;
    }

    /**
     * Helper method for retrieving an (ideally) cached instance of the HydraModel for the given item's armor renderer
     *
     * @return The HydraModel for the item, or null if not applicable
     */
    @Nullable
    @Override
    public HydraModel<?> getHydraModelForArmor(ItemStack armour) {
        if (IClientItemExtensions.of(armour).getHumanoidArmorModel(null, armour, null, null) instanceof HydraArmorRenderer<?> armorRenderer)
            return armorRenderer.getHydraModel();

        if (HydraRenderProvider.of(armour).getHydraArmorRenderer(null, armour, null, null) instanceof HydraArmorRenderer<?> armorRenderer)
            return armorRenderer.getHydraModel();

        return null;
    }
}
