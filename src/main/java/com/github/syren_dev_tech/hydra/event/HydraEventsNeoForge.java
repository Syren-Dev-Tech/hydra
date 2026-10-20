package com.github.syren_dev_tech.hydra.event;

import com.github.syren_dev_tech.hydra.renderer.*;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.GeoAnimatable;
import com.github.syren_dev_tech.hydra.animatable.GeoItem;
import com.github.syren_dev_tech.hydra.event.armor.CompileArmorRenderLayersEvent;
import com.github.syren_dev_tech.hydra.event.armor.CompileArmorRenderStateEvent;
import com.github.syren_dev_tech.hydra.event.armor.GeoArmorPreRenderEvent;
import com.github.syren_dev_tech.hydra.event.block.CompileBlockRenderLayersEvent;
import com.github.syren_dev_tech.hydra.event.block.CompileBlockRenderStateEvent;
import com.github.syren_dev_tech.hydra.event.block.GeoBlockPreRenderEvent;
import com.github.syren_dev_tech.hydra.event.entity.CompileEntityRenderLayersEvent;
import com.github.syren_dev_tech.hydra.event.entity.CompileEntityRenderStateEvent;
import com.github.syren_dev_tech.hydra.event.entity.GeoEntityPreRenderEvent;
import com.github.syren_dev_tech.hydra.event.item.CompileItemRenderLayersEvent;
import com.github.syren_dev_tech.hydra.event.item.CompileItemRenderStateEvent;
import com.github.syren_dev_tech.hydra.event.item.GeoItemPreRenderEvent;
import com.github.syren_dev_tech.hydra.event.object.CompileObjectRenderLayersEvent;
import com.github.syren_dev_tech.hydra.event.object.CompileObjectRenderStateEvent;
import com.github.syren_dev_tech.hydra.event.object.GeoObjectPreRenderEvent;
import com.github.syren_dev_tech.hydra.event.replacedentity.CompileReplacedEntityRenderLayersEvent;
import com.github.syren_dev_tech.hydra.event.replacedentity.CompileReplacedEntityRenderStateEvent;
import com.github.syren_dev_tech.hydra.event.replacedentity.GeoReplacedEntityPreRenderEvent;
import com.github.syren_dev_tech.hydra.renderer.*;
import com.github.syren_dev_tech.hydra.renderer.base.GeoRenderState;
import com.github.syren_dev_tech.hydra.renderer.base.RenderPassInfo;
import com.github.syren_dev_tech.hydra.service.HydraEvents;

/// NeoForge service implementation for Hydra's various events
public class HydraEventsNeoForge implements HydraEvents {
    /// Fire the [GeoRenderEvent.Block.CompileRenderLayers] event
    @Override
    public <T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState>
    void fireCompileBlockRenderLayers(GeoBlockRenderer<T, R> renderer) {
        NeoForge.EVENT_BUS.post(new CompileBlockRenderLayersEvent<>(renderer));
    }

    /// Fire the [GeoRenderEvent.Block.CompileRenderState] event
    @Override
    public <T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState>
    void fireCompileBlockRenderState(GeoBlockRenderer<T, R> renderer, R renderState, T animatable) {
        NeoForge.EVENT_BUS.post(new CompileBlockRenderStateEvent<>(renderer, renderState, animatable));
    }

    /// Fire the [GeoRenderEvent.Block.Pre] event, returning true if the event was not cancelled
    @Override
    public <T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState>
    boolean fireBlockPreRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        return !NeoForge.EVENT_BUS.post(new GeoBlockPreRenderEvent<>(renderPassInfo, renderTasks)).isCanceled();
    }

    /// Fire the [GeoRenderEvent.Armor.CompileRenderLayers] event
    @Override
    public <T extends Item & GeoItem, R extends HumanoidRenderState>
    void fireCompileArmorRenderLayers(GeoArmorRenderer<T, R> renderer) {
        NeoForge.EVENT_BUS.post(new CompileArmorRenderLayersEvent<>(renderer));
    }

    /// Fire the [GeoRenderEvent.Armor.Pre] event, returning true if the event was not cancelled
    @Override
    public <T extends Item & GeoItem, O extends GeoArmorRenderer.RenderData, R extends HumanoidRenderState>
    void fireCompileArmorRenderState(GeoArmorRenderer<T, R> renderer, R renderState, T animatable, O renderData) {
        NeoForge.EVENT_BUS.post(new CompileArmorRenderStateEvent<>(renderer, renderState, animatable, renderData));
    }

    /// Fire the [GeoRenderEvent.Armor.Pre] event, returning true if the event was not cancelled
    @Override
    public <T extends Item & GeoItem, R extends HumanoidRenderState>
    boolean fireArmorPreRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        return !NeoForge.EVENT_BUS.post(new GeoArmorPreRenderEvent<>(renderPassInfo, renderTasks)).isCanceled();
    }

    /// Fire the [GeoRenderEvent.Entity.CompileRenderLayers] event
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState>
    void fireCompileEntityRenderLayers(GeoEntityRenderer<T, R> renderer) {
        NeoForge.EVENT_BUS.post(new CompileEntityRenderLayersEvent<>(renderer));
    }

    /// Fire the [GeoRenderEvent.Entity.CompileRenderState] event
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState>
    void fireCompileEntityRenderState(GeoEntityRenderer<T, R> renderer, R renderState, T animatable) {
        NeoForge.EVENT_BUS.post(new CompileEntityRenderStateEvent<>(renderer, renderState, animatable));
    }

    /// Fire the [GeoRenderEvent.Entity.Pre] event, returning true if the event was not cancelled
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState>
    boolean fireEntityPreRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        return !NeoForge.EVENT_BUS.post(new GeoEntityPreRenderEvent<>(renderPassInfo, renderTasks)).isCanceled();
    }

    /// Fire the [GeoRenderEvent.ReplacedEntity.CompileRenderLayers] event
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState>
    void fireCompileReplacedEntityRenderLayers(GeoReplacedEntityRenderer<T, E, R> renderer) {
        NeoForge.EVENT_BUS.post(new CompileReplacedEntityRenderLayersEvent<>(renderer));
    }

    /// Fire the [GeoRenderEvent.ReplacedEntity.CompileRenderState] event
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState>
    void fireCompileReplacedEntityRenderState(GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, T animatable, E entity) {
        NeoForge.EVENT_BUS.post(new CompileReplacedEntityRenderStateEvent<>(renderer, renderState, animatable, entity));
    }

    /// Fire the [GeoRenderEvent.ReplacedEntity.Pre] event, returning true if the event was not cancelled
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState>
    boolean fireReplacedEntityPreRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        return !NeoForge.EVENT_BUS.post(new GeoReplacedEntityPreRenderEvent<>(renderPassInfo, renderTasks)).isCanceled();
    }

    /// Fire the [GeoRenderEvent.Item.CompileRenderLayers] event
    @Override
    public <T extends Item & GeoAnimatable>
    void fireCompileItemRenderLayers(GeoItemRenderer<T> renderer) {
        NeoForge.EVENT_BUS.post(new CompileItemRenderLayersEvent<>(renderer));
    }

    /// Fire the [GeoRenderEvent.Item.CompileRenderState] event
    @Override
    public <T extends Item & GeoAnimatable, O extends GeoItemRenderer.RenderData, R extends GeoRenderState>
    void fireCompileItemRenderState(GeoItemRenderer<T> renderer, R renderState, T animatable, O renderData) {
        NeoForge.EVENT_BUS.post(new CompileItemRenderStateEvent<>(renderer, renderState, animatable, renderData));
    }

    /// Fire the [GeoRenderEvent.Item.Pre] event, returning true if the event was not cancelled
    @Override
    public <T extends Item & GeoAnimatable>
    boolean fireItemPreRender(RenderPassInfo<GeoRenderState> renderPassInfo, SubmitNodeCollector renderTasks) {
        return !NeoForge.EVENT_BUS.post(new GeoItemPreRenderEvent<>(renderPassInfo, renderTasks)).isCanceled();
    }

    /// Fire the [GeoRenderEvent.Object.CompileRenderLayers] event
    @Override
    public <T extends GeoAnimatable, E, R extends GeoRenderState>
    void fireCompileObjectRenderLayers(GeoObjectRenderer<T, E, R> renderer) {
        NeoForge.EVENT_BUS.post(new CompileObjectRenderLayersEvent<>(renderer));
    }

    /// Fire the [GeoRenderEvent.Object.CompileRenderState] event
    @Override
    public <T extends GeoAnimatable, E, R extends GeoRenderState>
    void fireCompileObjectRenderState(GeoObjectRenderer<T, E, R> renderer, R renderState, T animatable, @Nullable E relatedObject) {
        NeoForge.EVENT_BUS.post(new CompileObjectRenderStateEvent<>(renderer, renderState, animatable, relatedObject));
    }

    /// Fire the [GeoRenderEvent.Object.Pre] event, returning true if the event was not cancelled
    @Override
    public <T extends GeoAnimatable, E, R extends GeoRenderState>
    boolean fireObjectPreRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        return !NeoForge.EVENT_BUS.post(new GeoObjectPreRenderEvent<>(renderPassInfo, renderTasks)).isCanceled();
    }
}
