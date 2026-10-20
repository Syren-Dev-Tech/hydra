package com.github.syren_dev_tech.hydra.mixin.client;

import com.github.syren_dev_tech.hydra.object.VanillaModelModifier;
import com.github.syren_dev_tech.hydra.renderer.base.GeoRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantValue")
@Mixin(Model.class)
public class ModelMixin<S> {
    /// Inject Hydra's [VanillaModelModifier]s into the model [Model#setupAnim] call
    @SuppressWarnings("unchecked")
    @Inject(method = "setupAnim", at = @At("TAIL"))
    public void hydra$callModelSetupCallbacks(S instance, CallbackInfo ci) {
        if (!((Object)this instanceof HumanoidModel) && instance instanceof GeoRenderState renderState)
            //noinspection unchecked,rawtypes
            VanillaModelModifier.runModifierSetup((Model)(Object)this, renderState);
    }
}
