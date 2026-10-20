package com.github.syren_dev_tech.hydra.event.block;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.Event;
import com.github.syren_dev_tech.hydra.animatable.GeoAnimatable;
import com.github.syren_dev_tech.hydra.event.GeoRenderEvent;
import com.github.syren_dev_tech.hydra.renderer.GeoArmorRenderer;
import com.github.syren_dev_tech.hydra.renderer.GeoBlockRenderer;
import com.github.syren_dev_tech.hydra.renderer.base.GeoRenderState;

/// One-time event for a [GeoArmorRenderer] called on first initialisation
///
/// Use this event to add render layers to the renderer as needed
///
/// **<u>NOTE:</u>** Some methods on this event are not overridden in this class. Check [GeoRenderEvent]
///
/// @see GeoRenderEvent
/// @see GeoRenderEvent.Armor.CompileRenderLayers
public class CompileBlockRenderLayersEvent<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState> extends Event implements GeoRenderEvent.Block.CompileRenderLayers<T, R> {
    private final GeoBlockRenderer<T, R> renderer;

    public CompileBlockRenderLayersEvent(GeoBlockRenderer<T, R> renderer) {
        this.renderer = renderer;
    }

    @Override
    public GeoBlockRenderer<T, R> getRenderer() {
        return this.renderer;
    }
}
