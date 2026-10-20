package com.github.syren_dev_tech.hydra.event.armor;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import com.github.syren_dev_tech.hydra.animatable.GeoItem;
import com.github.syren_dev_tech.hydra.event.GeoRenderEvent;
import com.github.syren_dev_tech.hydra.renderer.GeoArmorRenderer;
import com.github.syren_dev_tech.hydra.renderer.GeoBlockRenderer;
import com.github.syren_dev_tech.hydra.renderer.base.GeoRenderState;

/// One-time event for a [GeoBlockRenderer] called on first initialisation
///
/// Use this event to add render layers to the renderer as needed
///
/// **<u>NOTE:</u>** Some methods on this event are not overridden in this class. Check [GeoRenderEvent]
///
/// @see GeoRenderEvent
/// @see CompileRenderLayers
public class CompileArmorRenderLayersEvent<T extends Item & GeoItem, R extends HumanoidRenderState> extends Event implements GeoRenderEvent.Armor.CompileRenderLayers<T, R> {
    private final GeoArmorRenderer<T, R> renderer;

    public CompileArmorRenderLayersEvent(GeoArmorRenderer<T, R> renderer) {
        this.renderer = renderer;
    }

    @Override
    public GeoArmorRenderer<T, R> getRenderer() {
        return this.renderer;
    }
}
