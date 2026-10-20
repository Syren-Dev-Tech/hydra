package com.github.syren_dev_tech.hydra.event.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import com.github.syren_dev_tech.hydra.animatable.GeoAnimatable;
import com.github.syren_dev_tech.hydra.event.GeoRenderEvent;
import com.github.syren_dev_tech.hydra.renderer.GeoItemRenderer;

/// One-time event for a [GeoItemRenderer] called on first initialisation
///
/// Use this event to add render layers to the renderer as needed
///
/// **<u>NOTE:</u>** Some methods on this event are not overridden in this class. Check [GeoRenderEvent]
///
/// @see GeoRenderEvent
/// @see CompileRenderLayers
public class CompileItemRenderLayersEvent<T extends Item & GeoAnimatable> extends Event implements GeoRenderEvent.Item.CompileRenderLayers<T> {
    private final GeoItemRenderer<T> renderer;

    public CompileItemRenderLayersEvent(GeoItemRenderer<T> renderer) {
        this.renderer = renderer;
    }

    @Override
    public GeoItemRenderer<T> getRenderer() {
        return this.renderer;
    }
}
