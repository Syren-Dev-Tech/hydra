package com.github.syren_dev_tech.hydra.mixin.client;

import com.github.syren_dev_tech.hydra.renderer.GeoArmorRenderer;
import com.github.syren_dev_tech.hydra.renderer.base.GeoRenderState;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = EntityRenderer.class, priority = 5000)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    /// Injection mixin to allow for capture of data for [GeoRenderState]s for [GeoArmorRenderer]s,
    /// given that they never normally receive the entity context
    @SuppressWarnings({"ConstantValue", "rawtypes", "unchecked"})
    @WrapMethod(method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;")
    public S hydra$captureDataForArmorLayer(T entity, float partialTick, Operation<S> original) {
        S renderState = original.call(entity, partialTick);

        if (renderState instanceof HumanoidRenderState && (Object)this instanceof LivingEntityRenderer livingRenderer) {
            for (Object layer : livingRenderer.layers) {
                if (layer instanceof HumanoidArmorLayer armorLayer) {
                    GeoArmorRenderer.captureRenderStates(hydra$castRenderState(renderState), (LivingEntity)entity, partialTick,
                                                         (renderState2, slot) -> armorLayer.getArmorModel(renderState2, slot),
                                                         slot -> hydra$castRenderState(slot == EquipmentSlot.HEAD ? renderState : original.call(entity, partialTick)));

                    break;
                }
            }
        }

        return renderState;
    }

    /// Sugar method for blind-casting RenderStates to Hydra-supported generic types
    @SuppressWarnings("unchecked")
    @Unique
    private static <R extends HumanoidRenderState> R hydra$castRenderState(EntityRenderState renderState) {
        return (R)renderState;
    }
}
