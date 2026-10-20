package com.github.syren_dev_tech.hydra.service;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import com.github.syren_dev_tech.hydra.cache.object.BakedHydraModel;
import com.github.syren_dev_tech.hydra.renderer.*;

/**
 * Loader-agnostic service interface for Hydra's various events
 */
public interface HydraEvents {
    /**
     * Fire the Block.CompileRenderLayers event
     */
    void fireCompileBlockRenderLayers(HydraBlockRenderer<?> renderer);
    /**
     * Fire the Block.Pre event
     */
    boolean fireBlockPreRender(HydraBlockRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight);
    /**
     * Fire the Block.Post event
     */
    void fireBlockPostRender(HydraBlockRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight);

    /**
     * Fire the Armor.CompileRenderLayers event
     */
    void fireCompileArmorRenderLayers(HydraArmorRenderer<?> renderer);
    /**
     * Fire the Armor.Pre event
     */
    boolean fireArmorPreRender(HydraArmorRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight);
    /**
     * Fire the Armor.Post event
     */
    void fireArmorPostRender(HydraArmorRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight);

    /**
     * Fire the Entity.CompileRenderLayers event
     */
    void fireCompileEntityRenderLayers(HydraEntityRenderer<?> renderer);
    /**
     * Fire the Entity.Pre event
     */
    boolean fireEntityPreRender(HydraEntityRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight);
    /**
     * Fire the Entity.Post event
     */
    void fireEntityPostRender(HydraEntityRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight);

    /**
     * Fire the ReplacedEntity.CompileRenderLayers event
     */
    void fireCompileReplacedEntityRenderLayers(HydraReplacedEntityRenderer<?, ?> renderer);
    /**
     * Fire the ReplacedEntity.Pre event
     */
    boolean fireReplacedEntityPreRender(HydraReplacedEntityRenderer<?, ?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight);
    /**
     * Fire the ReplacedEntity.Post event
     */
    void fireReplacedEntityPostRender(HydraReplacedEntityRenderer<?, ?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight);

    /**
     * Fire the Item.CompileRenderLayers event
     */
    void fireCompileItemRenderLayers(HydraItemRenderer<?> renderer);
    /**
     * Fire the Item.Pre event
     */
    boolean fireItemPreRender(HydraItemRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight);
    /**
     * Fire the Item.Post event
     */
    void fireItemPostRender(HydraItemRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight);

    /**
     * Fire the Object.CompileRenderLayers event
     */
    void fireCompileObjectRenderLayers(HydraObjectRenderer<?> renderer);
    /**
     * Fire the Object.Pre event
     */
    boolean fireObjectPreRender(HydraObjectRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight);
    /**
     * Fire the Object.Post event
     */
    void fireObjectPostRender(HydraObjectRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight);
}
