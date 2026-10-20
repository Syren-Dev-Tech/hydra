package com.github.syren_dev_tech.hydra.mixin.client;

import com.github.syren_dev_tech.hydra.object.VanillaModelModifier;
import com.github.syren_dev_tech.hydra.renderer.base.GeoRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantValue")
@Mixin(PlayerModel.class)
public class PlayerModelMixin<T extends AvatarRenderState> {
	/// Inject Hydra's [VanillaModelModifier]s into the model [PlayerModel#setupAnim(AvatarRenderState)] call
	@SuppressWarnings("unchecked")
	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("TAIL"))
	public void hydra$callModelSetupCallbacks(T instance, CallbackInfo ci) {
		if (instance instanceof GeoRenderState renderState)
			//noinspection unchecked,rawtypes
			VanillaModelModifier.runModifierSetup((Model)(Object)this, renderState);
	}
}