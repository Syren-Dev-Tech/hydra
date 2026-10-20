package com.github.syren_dev_tech.hydra.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.cache.object.BakedHydraModel;
import com.github.syren_dev_tech.hydra.cache.object.HydraBone;
import com.github.syren_dev_tech.hydra.renderer.HydraRenderer;

/**
 * {@link HydraRenderLayer} for auto-applying some form of modification to bones of a model prior to rendering
 * <p>
 * This can be useful for enabling or disabling bone rendering based on arbitrary conditions
 * <p>
 * NOTE: Despite this layer existing, it is much more efficient to use {@link FastBoneFilterHydraLayer} instead
 */
public class BoneFilterHydraLayer<T extends HydraAnimatable> extends HydraRenderLayer<T> {
	protected final TriConsumer<HydraBone, T, Float> checkAndApply;

	public BoneFilterHydraLayer(HydraRenderer<T> renderer) {
		this(renderer, (bone, animatable, partialTick) -> {});
	}

	public BoneFilterHydraLayer(HydraRenderer<T> renderer, TriConsumer<HydraBone, T, Float> checkAndApply) {
		super(renderer);

		this.checkAndApply = checkAndApply;
	}

	/**
	 * This method is called for each bone in the model
	 * <p>
	 * Check whether the bone should be affected and apply the modification as needed.
	 */
	protected void checkAndApply(HydraBone bone, T animatable, float partialTick) {
		this.checkAndApply.accept(bone, animatable, partialTick);
	}

	/**
	 * This method is called by the {@link HydraRenderer} before rendering, immediately after {@link HydraRenderer#preRender} has been called
	 * <p>
	 * This allows for RenderLayers to perform pre-render manipulations such as hiding or showing bones
	 */
	@Override
	public void preRender(PoseStack poseStack, T animatable, BakedHydraModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		for (HydraBone bone : bakedModel.topLevelBones()) {
			checkChildBones(bone, animatable, partialTick);
		}
	}

	private void checkChildBones(HydraBone parentBone, T animatable, float partialTick) {
		checkAndApply(parentBone, animatable, partialTick);

		for (HydraBone bone : parentBone.getChildBones()) {
			checkChildBones(bone, animatable, partialTick);
		}
	}
}
