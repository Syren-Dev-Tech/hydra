package com.github.syren_dev_tech.hydra.event.replacedentity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;
import com.github.syren_dev_tech.hydra.animatable.GeoAnimatable;
import com.github.syren_dev_tech.hydra.event.GeoRenderEvent;
import com.github.syren_dev_tech.hydra.renderer.GeoReplacedEntityRenderer;
import com.github.syren_dev_tech.hydra.renderer.base.GeoRenderState;

/// One-time event for a [GeoReplacedEntityRenderer] called on first initialisation
///
/// Use this event to add render layers to the renderer as needed
///
/// **<u>NOTE:</u>** Some methods on this event are not overridden in this class. Check [GeoRenderEvent]
///
/// @see GeoRenderEvent
/// @see CompileRenderLayers
public class CompileReplacedEntityRenderLayersEvent<T extends GeoAnimatable, E extends Entity, R extends EntityRenderState> extends Event implements GeoRenderEvent.ReplacedEntity.CompileRenderLayers<T, E, R> {
    private final GeoReplacedEntityRenderer<T, E, R> renderer;

    public CompileReplacedEntityRenderLayersEvent(GeoReplacedEntityRenderer<T, E, R> renderer) {
        this.renderer = renderer;
    }

    @Override
    public GeoReplacedEntityRenderer<T, E, R> getRenderer() {
        return this.renderer;
    }
}
