package com.github.syren_dev_tech.hydra.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.neoforge.common.NeoForge;
import com.github.syren_dev_tech.hydra.cache.object.BakedHydraModel;
import com.github.syren_dev_tech.hydra.renderer.*;
import com.github.syren_dev_tech.hydra.service.HydraEvents;

/**
 * NeoForge service implementation for Hydra's various events
 */
public class HydraEventsNeoForge implements HydraEvents {
    /**
     * Fire the {@link HydraRenderEvent.Block.CompileRenderLayers} event
     */
    @Override
    public void fireCompileBlockRenderLayers(HydraBlockRenderer<?> renderer) {
        NeoForge.EVENT_BUS.post(new HydraRenderEvent.Block.CompileRenderLayers(renderer));
    }

    /**
     * Fire the {@link HydraRenderEvent.Block.Pre} event
     */
    @Override
    public boolean fireBlockPreRender(HydraBlockRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return !NeoForge.EVENT_BUS.post(new HydraRenderEvent.Block.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    /**
     * Fire the {@link HydraRenderEvent.Block.Post} event
     */
    @Override
    public void fireBlockPostRender(HydraBlockRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        NeoForge.EVENT_BUS.post(new HydraRenderEvent.Block.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    /**
     * Fire the {@link HydraRenderEvent.Armor.CompileRenderLayers} event
     */
    @Override
    public void fireCompileArmorRenderLayers(HydraArmorRenderer<?> renderer) {
        NeoForge.EVENT_BUS.post(new HydraRenderEvent.Armor.CompileRenderLayers(renderer));
    }

    /**
     * Fire the {@link HydraRenderEvent.Armor.Pre} event
     */
    @Override
    public boolean fireArmorPreRender(HydraArmorRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return !NeoForge.EVENT_BUS.post(new HydraRenderEvent.Armor.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    /**
     * Fire the {@link HydraRenderEvent.Armor.Post} event
     */
    @Override
    public void fireArmorPostRender(HydraArmorRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        NeoForge.EVENT_BUS.post(new HydraRenderEvent.Armor.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    /**
     * Fire the {@link HydraRenderEvent.Entity.CompileRenderLayers} event
     */
    @Override
    public void fireCompileEntityRenderLayers(HydraEntityRenderer<?> renderer) {
        NeoForge.EVENT_BUS.post(new HydraRenderEvent.Entity.CompileRenderLayers(renderer));
    }

    /**
     * Fire the {@link HydraRenderEvent.Entity.Pre} event
     */
    @Override
    public boolean fireEntityPreRender(HydraEntityRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return !NeoForge.EVENT_BUS.post(new HydraRenderEvent.Entity.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    /**
     * Fire the {@link HydraRenderEvent.Entity.Post} event
     */
    @Override
    public void fireEntityPostRender(HydraEntityRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        NeoForge.EVENT_BUS.post(new HydraRenderEvent.Entity.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    /**
     * Fire the {@link HydraRenderEvent.ReplacedEntity.CompileRenderLayers} event
     */
    @Override
    public void fireCompileReplacedEntityRenderLayers(HydraReplacedEntityRenderer<?, ?> renderer) {
        NeoForge.EVENT_BUS.post(new HydraRenderEvent.ReplacedEntity.CompileRenderLayers(renderer));
    }

    /**
     * Fire the {@link HydraRenderEvent.ReplacedEntity.Pre} event
     */
    @Override
    public boolean fireReplacedEntityPreRender(HydraReplacedEntityRenderer<?, ?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return !NeoForge.EVENT_BUS.post(new HydraRenderEvent.ReplacedEntity.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    /**
     * Fire the {@link HydraRenderEvent.ReplacedEntity.Post} event
     */
    @Override
    public void fireReplacedEntityPostRender(HydraReplacedEntityRenderer<?, ?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        NeoForge.EVENT_BUS.post(new HydraRenderEvent.ReplacedEntity.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    /**
     * Fire the {@link HydraRenderEvent.Item.CompileRenderLayers} event
     */
    @Override
    public void fireCompileItemRenderLayers(HydraItemRenderer<?> renderer) {
        NeoForge.EVENT_BUS.post(new HydraRenderEvent.Item.CompileRenderLayers(renderer));
    }

    /**
     * Fire the {@link HydraRenderEvent.Item.Pre} event
     */
    @Override
    public boolean fireItemPreRender(HydraItemRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return !NeoForge.EVENT_BUS.post(new HydraRenderEvent.Item.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    /**
     * Fire the {@link HydraRenderEvent.Item.Post} event
     */
    @Override
    public void fireItemPostRender(HydraItemRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        NeoForge.EVENT_BUS.post(new HydraRenderEvent.Item.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    /**
     * Fire the {@link HydraRenderEvent.Object.CompileRenderLayers} event
     */
    @Override
    public void fireCompileObjectRenderLayers(HydraObjectRenderer<?> renderer) {
        NeoForge.EVENT_BUS.post(new HydraRenderEvent.Object.CompileRenderLayers(renderer));
    }

    /**
     * Fire the {@link HydraRenderEvent.Object.Pre} event
     */
    @Override
    public boolean fireObjectPreRender(HydraObjectRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return !NeoForge.EVENT_BUS.post(new HydraRenderEvent.Object.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    /**
     * Fire the {@link HydraRenderEvent.Object.Post} event
     */
    @Override
    public void fireObjectPostRender(HydraObjectRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        NeoForge.EVENT_BUS.post(new HydraRenderEvent.Object.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }
}
