package com.github.syren_dev_tech.hydra.mixin.client;

import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.client.renderer.LevelRenderer.class)
public interface LevelRendererAccessor {
    @Accessor("renderBuffers")
    RenderBuffers getRenderBuffers();
}
