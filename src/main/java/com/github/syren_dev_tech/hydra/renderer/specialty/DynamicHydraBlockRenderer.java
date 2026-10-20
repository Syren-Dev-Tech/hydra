package com.github.syren_dev_tech.hydra.renderer.specialty;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.cache.object.*;
import com.github.syren_dev_tech.hydra.model.HydraModel;
import com.github.syren_dev_tech.hydra.renderer.HydraBlockRenderer;
import com.github.syren_dev_tech.hydra.util.RenderUtil;

import java.util.Map;

/**
 * Extended special-block renderer for more advanced or dynamic models
 * <p>
 * Because of the extra performance cost of this renderer, it is advised to avoid using it unnecessarily,
 * and consider whether the benefits are worth the cost for your needs.
 */
public abstract class DynamicHydraBlockRenderer<T extends BlockEntity & HydraAnimatable> extends HydraBlockRenderer<T> {
	protected static Map<ResourceLocation, IntIntPair> TEXTURE_DIMENSIONS_CACHE = new Object2ObjectOpenHashMap<>();

	protected ResourceLocation textureOverride = null;

	public DynamicHydraBlockRenderer(HydraModel<T> model) {
		super(model);
	}

	/**
	 * For each bone rendered, this method is called
	 * <p>
	 * If a ResourceLocation is returned, the renderer will render the bone using that texture instead of the default
	 * This can be useful for custom rendering on a per-bone basis
	 * <p>
	 * There is a somewhat significant performance cost involved in this however, so only use as needed
	 *
	 * @return The specified ResourceLocation, or null if no override
	 */
	@Nullable
	protected ResourceLocation getTextureOverrideForBone(HydraBone bone, T animatable, float partialTick) {
		return null;
	}

	/**
	 * For each bone rendered, this method is called
	 * <p>
	 * If a RenderType is returned, the renderer will render the bone using that RenderType instead of the default
	 * This can be useful for custom rendering operations on a per-bone basis
	 * <p>
	 * There is a somewhat significant performance cost involved in this however, so only use as needed
	 *
	 * @return The specified RenderType, or null if no override
	 */
	@Nullable
	protected RenderType getRenderTypeOverrideForBone(HydraBone bone, T animatable, ResourceLocation texturePath, MultiBufferSource bufferSource, float partialTick) {
		return null;
	}

	/**
	 * Override this to handle a given {@link HydraBone HydraBone's} rendering in a particular way
	 *
	 * @return Whether the renderer should skip rendering the {@link HydraCube cubes} of the given HydraBone or not
	 */
	protected boolean boneRenderOverride(PoseStack poseStack, HydraBone bone, MultiBufferSource bufferSource, VertexConsumer buffer,
										 float partialTick, int packedLight, int packedOverlay, int colour) {
		return false;
	}

	/**
	 * Renders the provided {@link HydraBone} and its associated child bones
	 */
	@Override
	public void renderRecursively(PoseStack poseStack, T animatable, HydraBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
		poseStack.pushPose();
		RenderUtil.translateMatrixToBone(poseStack, bone);
		RenderUtil.translateToPivotPoint(poseStack, bone);
		RenderUtil.rotateMatrixAroundBone(poseStack, bone);
		RenderUtil.scaleMatrixForBone(poseStack, bone);

		if (bone.isTrackingMatrices()) {
			Matrix4f poseState = new Matrix4f(poseStack.last().pose());
			Matrix4f localMatrix = RenderUtil.invertAndMultiplyMatrices(poseState, this.blockRenderTranslations);
			Matrix4f worldState = new Matrix4f(localMatrix);
			BlockPos pos = this.animatable.getBlockPos();

			bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
			bone.setLocalSpaceMatrix(localMatrix);
			bone.setWorldSpaceMatrix(worldState.translate(new Vector3f(pos.getX(), pos.getY(), pos.getZ())));
		}

		RenderUtil.translateAwayFromPivotPoint(poseStack, bone);

		this.textureOverride = getTextureOverrideForBone(bone, this.animatable, partialTick);
		ResourceLocation texture = this.textureOverride == null ? getTextureLocation(this.animatable) : this.textureOverride;
		RenderType renderTypeOverride = getRenderTypeOverrideForBone(bone, this.animatable, texture, bufferSource, partialTick);

		if (texture != null && renderTypeOverride == null)
			renderTypeOverride = getRenderType(this.animatable, texture, bufferSource, partialTick);

		if (renderTypeOverride != null)
			buffer = bufferSource.getBuffer(renderTypeOverride);

		if (!boneRenderOverride(poseStack, bone, bufferSource, buffer, partialTick, packedLight, packedOverlay, colour))
			super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);

		if (renderTypeOverride != null)
			buffer = bufferSource.getBuffer(renderType);

		if (!isReRender)
			applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

		buffer = checkAndRefreshBuffer(isReRender, buffer, bufferSource, renderType);

		super.renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

		poseStack.popPose();
	}

	/**
	 * Called after rendering the model to buffer. Post-render modifications should be performed here
	 * <p>
	 * {@link PoseStack} transformations will be unused and lost once this method ends
	 */
	@Override
	public void postRender(PoseStack poseStack, T animatable, BakedHydraModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
		this.textureOverride = null;

		super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
	}

	/**
	 * Applies the {@link HydraQuad Quad's} {@link HydraVertex vertices} to the given {@link VertexConsumer buffer} for rendering
	 * <p>
	 * Custom override to handle custom non-baked textures for DynamicHydraBlockRenderer
	 */
	@Override
	public void createVerticesOfQuad(HydraQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer,
									 int packedLight, int packedOverlay, int colour) {
		if (this.textureOverride == null) {
			super.createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay,
					colour);

			return;
		}

		IntIntPair boneTextureSize = computeTextureSize(this.textureOverride);
		IntIntPair blockTextureSize = computeTextureSize(getTextureLocation(this.animatable));

		if (boneTextureSize == null || blockTextureSize == null) {
			super.createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay,
					colour);

			return;
		}

		for (HydraVertex vertex : quad.vertices()) {
			Vector4f vector4f = poseState.transform(new Vector4f(vertex.position().x(), vertex.position().y(), vertex.position().z(), 1.0f));
			float texU = (vertex.texU() * blockTextureSize.firstInt()) / boneTextureSize.firstInt();
			float texV = (vertex.texV() * blockTextureSize.secondInt()) / boneTextureSize.secondInt();

			buffer.addVertex(vector4f.x(), vector4f.y(), vector4f.z(), colour, texU, texV,
					packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
		}
	}

	/**
	 * Retrieve or compute the height and width of a given texture from its {@link ResourceLocation}
	 * <p>
	 * This is used for dynamically mapping vertices on a given quad
	 * <p>
	 * This is inefficient however, and should only be used where required
	 */
	protected IntIntPair computeTextureSize(ResourceLocation texture) {
		return TEXTURE_DIMENSIONS_CACHE.computeIfAbsent(texture, RenderUtil::getTextureDimensions);
	}
}