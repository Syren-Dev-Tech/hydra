package com.github.syren_dev_tech.hydra.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.github.syren_dev_tech.hydra.animatable.client.HydraRenderProvider;

/**
 * Injection into the BEWLR rendering point to defer to Hydra item rendering if applicable
 * <p>
 * Cancels the remainder of the render call if Hydra renders, otherwise does nothing
 */
@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {
    @Inject(method = "renderByItem", at = @At("HEAD"), cancellable = true)
    public void hydra$renderHydraItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, CallbackInfo ci) {
        final BlockEntityWithoutLevelRenderer hydraRenderer = HydraRenderProvider.of(stack).getHydraItemRenderer();

        if (hydraRenderer != null) {
            hydraRenderer.renderByItem(stack, displayContext, poseStack, bufferSource, packedLight, packedOverlay);

            ci.cancel();
        }
    }
}
