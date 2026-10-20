package com.github.syren_dev_tech.hydra.mixin.client;

import com.github.syren_dev_tech.hydra.object.VanillaModelModifier;
import com.github.syren_dev_tech.hydra.renderer.base.GeoRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelFeatureRenderer.class)
public class ModelFeatureRendererMixin {
    /// Inject [VanillaModelModifier#postRenderReset] into the render chain
    @Inject(method = "prepareModel", at = @At("TAIL"))
    public <S> void hydra$injectModelCleanup(ModelFeatureRenderer.Submit<S> submit, CallbackInfo ci) {
        if (submit.state() instanceof GeoRenderState renderState)
            //noinspection unchecked,rawtypes
            VanillaModelModifier.runModifierCleanup((Model)submit.model(), renderState);
    }
}
