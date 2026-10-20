package com.github.syren_dev_tech.hydra.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.cache.object.HydraBone;
import com.github.syren_dev_tech.hydra.renderer.HydraRenderer;
import com.github.syren_dev_tech.hydra.util.RenderUtil;

import java.util.function.BiFunction;

/**
 * {@link HydraRenderLayer} for rendering {@link BlockState BlockStates}
 * or {@link ItemStack ItemStacks} on a given {@link HydraAnimatable}
 */
public class BlockAndItemHydraLayer<T extends HydraAnimatable> extends HydraRenderLayer<T> {
    protected final BiFunction<HydraBone, T, ItemStack> stackForBone;
    protected final BiFunction<HydraBone, T, BlockState> blockForBone;

    public BlockAndItemHydraLayer(HydraRenderer<T> renderer) {
        this(renderer, (bone, animatable) -> null, (bone, animatable) -> null);
    }

    public BlockAndItemHydraLayer(HydraRenderer<T> renderer, BiFunction<HydraBone, T, ItemStack> stackForBone, BiFunction<HydraBone, T, BlockState> blockForBone) {
        super(renderer);

        this.stackForBone = stackForBone;
        this.blockForBone = blockForBone;
    }

    /**
     * Return an ItemStack relevant to this bone for rendering, or null if no ItemStack to render
     */
    @Nullable
    protected ItemStack getStackForBone(HydraBone bone, T animatable) {
        return this.stackForBone.apply(bone, animatable);
    }

    /**
     * Return a BlockState relevant to this bone for rendering, or null if no BlockState to render
     */
    @Nullable
    protected BlockState getBlockForBone(HydraBone bone, T animatable) {
        return this.blockForBone.apply(bone, animatable);
    }

    /**
     * Return a specific TransFormType for this {@link ItemStack} render for this bone.
     */
    protected ItemDisplayContext getTransformTypeForStack(HydraBone bone, ItemStack stack, T animatable) {
        return ItemDisplayContext.NONE;
    }

    /**
     * This method is called by the {@link HydraRenderer} for each bone being rendered
     * <p>
     * This is a more expensive call, particularly if being used to render something on a different buffer.
     * It does however have the benefit of having the matrix translations and other transformations already applied from render-time
     * It's recommended to avoid using this unless necessary
     * <p>
     * The {@link HydraBone} in question has already been rendered by this stage
     * <p>
     * If you <i>do</i> use it, and you render something that changes the {@link VertexConsumer buffer}, you need to reset it back to the previous buffer
     * using {@link MultiBufferSource#getBuffer} before ending the method
     */
    @Override
    public void renderForBone(PoseStack poseStack, T animatable, HydraBone bone, RenderType renderType, MultiBufferSource bufferSource,
                              VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        ItemStack stack = getStackForBone(bone, animatable);
        BlockState blockState = getBlockForBone(bone, animatable);

        if (stack == null && blockState == null)
            return;

        poseStack.pushPose();
        RenderUtil.translateAndRotateMatrixForBone(poseStack, bone);

        if (stack != null)
            renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);

        if (blockState != null)
            renderBlockForBone(poseStack, bone, blockState, animatable, bufferSource, partialTick, packedLight, packedOverlay);

        poseStack.popPose();
    }

    /**
     * Render the given {@link ItemStack} for the provided {@link HydraBone}.
     */
    protected void renderStackForBone(PoseStack poseStack, HydraBone bone, ItemStack stack, T animatable, MultiBufferSource bufferSource,
                                      float partialTick, int packedLight, int packedOverlay) {
        if (animatable instanceof LivingEntity livingEntity) {
            Minecraft.getInstance().getItemRenderer().renderStatic(livingEntity, stack,
                    getTransformTypeForStack(bone, stack, animatable), false, poseStack, bufferSource, livingEntity.level(),
                    packedLight, packedOverlay, livingEntity.getId());
        } else {
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, getTransformTypeForStack(bone, stack, animatable),
                    packedLight, packedOverlay, poseStack, bufferSource, Minecraft.getInstance ().level, (int) this.renderer.getInstanceId(animatable));
        }
    }

    /**
     * Render the given {@link BlockState} for the provided {@link HydraBone}.
     */
    protected void renderBlockForBone(PoseStack poseStack, HydraBone bone, BlockState state, T animatable, MultiBufferSource bufferSource,
                                      float partialTick, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(-0.25f, -0.25f, -0.25f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
