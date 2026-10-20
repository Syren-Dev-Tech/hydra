package com.github.syren_dev_tech.hydra.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.cache.object.BakedHydraModel;
import com.github.syren_dev_tech.hydra.cache.object.HydraBone;
import com.github.syren_dev_tech.hydra.model.HydraModel;
import com.github.syren_dev_tech.hydra.renderer.HydraRenderer;

/**
 * Render layer base class for rendering additional layers of effects or textures over an existing model at runtime
 * <p>
 * Contains the base boilerplate and helper code for various render layer features
 */
public abstract class HydraRenderLayer<T extends HydraAnimatable> {
	protected final HydraRenderer<T> renderer;

	public HydraRenderLayer(HydraRenderer<T> entityRendererIn) {
		this.renderer = entityRendererIn;
	}

	/**
	 * Get the {@link HydraModel} currently being rendered
	 */
	public HydraModel<T> getHydraModel() {
		return this.renderer.getHydraModel();
	}

	/**
	 * Gets the {@link BakedHydraModel} instance that is currently being used.
	 * This can be directly used for re-rendering
	 */
	public BakedHydraModel getDefaultBakedModel(T animatable) {
		return getHydraModel().getBakedModel(getHydraModel().getModelResource(animatable, getRenderer()));
	}

	/**
	 * Get the renderer responsible for the current render operation
	 */
	public HydraRenderer<T> getRenderer() {
		return this.renderer;
	}

	/**
	 * Get the texture resource path for the given {@link HydraAnimatable}.<br>
	 * By default, falls back to {@link HydraRenderer#getTextureLocation(HydraAnimatable)}
	 */
	protected ResourceLocation getTextureResource(T animatable) {
		return getRenderer().getTextureLocation(animatable);
	}

	/**
	 * This method is called by the {@link HydraRenderer} before rendering, immediately after {@link HydraRenderer#preRender} has been called
	 * <p>
	 * This allows for RenderLayers to perform pre-render manipulations such as hiding or showing bones
	 */
	public void preRender(PoseStack poseStack, T animatable, BakedHydraModel bakedModel, @Nullable RenderType renderType,
						  MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick,
						  int packedLight, int packedOverlay) {}

	/**
	 * This is the method that is actually called by the render for your render layer to function
	 * <p>
	 * This is called <i>after</i> the animatable has been rendered, but before supplementary rendering like nametags
	 * <p>
	 * <b><u>NOTE:</u></b> If the passed {@link VertexConsumer buffer} is null, then the animatable was not actually rendered (invisible, etc)
	 * and you may need to factor this in to your design
	 */
	public void render(PoseStack poseStack, T animatable, BakedHydraModel bakedModel, @Nullable RenderType renderType,
								MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick,
								int packedLight, int packedOverlay) {}

	/**
	 * This method is called by the {@link HydraRenderer} for each bone being rendered
	 * <p>
	 * This is a more expensive call, particularly if being used to render something on a different buffer
	 * It does however have the benefit of having the matrix translations and other transformations already applied from render-time
	 * It's recommended to avoid using this unless necessary
	 * <p>
	 * The {@link HydraBone} in question has already been rendered by this stage
	 * <p>
	 * If you <i>do</i> use it, and you render something that changes the {@link VertexConsumer buffer}, you need to reset it back to the previous buffer
	 * using {@link MultiBufferSource#getBuffer} before ending the method
	 */
	public void renderForBone(PoseStack poseStack, T animatable, HydraBone bone, RenderType renderType,
							  MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {}
}