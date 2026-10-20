package com.github.syren_dev_tech.hydra.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import com.github.syren_dev_tech.hydra.HydraConstants;
import com.github.syren_dev_tech.hydra.animatable.client.HydraRenderProvider;
import com.github.syren_dev_tech.hydra.renderer.HydraArmorRenderer;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Internal-only class to store common code mostly for implementation purposes.
 * <p>
 * Shouldn't be used by dependent mods
 */
@ApiStatus.Internal
public class InternalUtil {
    /**
     * Attempt to render a Hydra {@link HydraArmorRenderer armor piece} for the given slot
     * <p>
     * This is typically only called by an internal mixin
     *
     * @return true if the armor piece was a Hydra armor piece and rendered
     * @param <T> The entity that is rendering its armor
     */
    public static <T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> boolean tryRenderHydraArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, T entity, ItemStack stack, EquipmentSlot equipmentSlot, M parentModel, A baseModel,
                                                                                                                                  float partialTick, int packedLight, float limbSwing, float limbSwingAmount, float lerpedTickCount, float netHeadYaw, float headPitch,
                                                                                                                                  BiConsumer<A, EquipmentSlot> partVisibilitySetter) {
        final Item item = stack.getItem();

        if (!(item instanceof Equipable equipable) || equipable.getEquipmentSlot() != equipmentSlot)
            return false;

        final HumanoidModel<?> hydraModel = HydraRenderProvider.of(item).getHydraArmorRenderer(entity, stack, equipmentSlot, baseModel);

        if (hydraModel == null)
            return false;

        parentModel.copyPropertiesTo(baseModel);
        partVisibilitySetter.accept(baseModel, equipmentSlot);

        if (hydraModel instanceof HydraArmorRenderer<?> hydraArmorRenderer)
            hydraArmorRenderer.prepForRender(entity, stack, equipmentSlot, baseModel, bufferSource, partialTick, limbSwing, limbSwingAmount, netHeadYaw, headPitch);

        baseModel.copyPropertiesTo((A)hydraModel);
        hydraModel.renderToBuffer(poseStack, null, packedLight, OverlayTexture.NO_OVERLAY, Color.WHITE.argbInt());

        return true;
    }

    /**
     * Perform an {@link Object#equals(Object)} check on two {@link PatchedDataComponentMap}s,
     * ignoring any Hydra stack ids that may be present.
     * <p>
     * This is typically only called by an internal mixin
     */
    public static boolean areComponentsMatchingIgnoringHydraId(PatchedDataComponentMap map1, PatchedDataComponentMap map2) {
        final DataComponentType<Long> stackId = HydraConstants.STACK_ANIMATABLE_ID_COMPONENT.get();
        boolean patched = false;

        if (map1.has(stackId)) {
            PatchedDataComponentMap prevMap = map1;
            boolean copyOnWrite = prevMap.copyOnWrite;
            (map1 = map1.copy()).remove(stackId);
            map1.copyOnWrite = copyOnWrite;
            patched = true;
        }

        if (map2.has(stackId)) {
            PatchedDataComponentMap prevMap = map2;
            boolean copyOnWrite = prevMap.copyOnWrite;
            (map2 = map2.copy()).remove(stackId);
            map2.copyOnWrite = copyOnWrite;
            patched = true;
        }

        return patched && Objects.equals(map1, map2);
    }
}
