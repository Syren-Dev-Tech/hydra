package com.github.syren_dev_tech.hydra.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import com.github.syren_dev_tech.hydra.HydraServices;
import com.github.syren_dev_tech.hydra.cache.object.BakedHydraModel;
import com.github.syren_dev_tech.hydra.cache.object.HydraBone;
import com.github.syren_dev_tech.hydra.cache.texture.AnimatableTexture;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.animation.AnimationState;
import com.github.syren_dev_tech.hydra.model.HydraModel;
import com.github.syren_dev_tech.hydra.renderer.layer.HydraRenderLayer;
import com.github.syren_dev_tech.hydra.renderer.layer.HydraRenderLayersContainer;
import com.github.syren_dev_tech.hydra.util.RenderUtil;
import com.github.syren_dev_tech.hydra.mixin.client.LevelRendererAccessor;

import java.util.List;

/**
 * Base {@link HydraRenderer} class for rendering anything that isn't already handled by the other builtin HydraRenderer subclasses
 * <p>
 * Before using this class you should ensure your use-case isn't already covered by one of the other existing renderers
 * <p>
 * It is <b>strongly</b> recommended you override {@link HydraRenderer#getInstanceId} if using this renderer
 */
public class HydraObjectRenderer<T extends HydraAnimatable> implements HydraRenderer<T> {
    protected final HydraRenderLayersContainer<T> renderLayers = new HydraRenderLayersContainer<>(this);
    protected final HydraModel<T> model;

    protected T animatable;
    protected float scaleWidth = 1;
    protected float scaleHeight = 1;

    protected Matrix4f objectRenderTranslations = new Matrix4f();
    protected Matrix4f modelRenderTranslations = new Matrix4f();

    public HydraObjectRenderer(HydraModel<T> model) {
        this.model = model;
    }

    /**
     * Gets the model instance for this renderer
     */
    @Override
    public HydraModel<T> getHydraModel() {
        return this.model;
    }

    /**
     * Gets the {@link HydraAnimatable} instance currently being rendered
     */
    @Override
    public T getAnimatable() {
        return this.animatable;
    }

    /**
     * Shadowing override of {@link EntityRenderer#getTextureLocation}
     * <p>
     * This redirects the call to {@link HydraRenderer#getTextureLocation}
     */
    @Override
    public ResourceLocation getTextureLocation(T animatable) {
        return HydraRenderer.super.getTextureLocation(animatable);
    }

    /**
     * Returns the list of registered {@link HydraRenderLayer HydraRenderLayers} for this renderer
     */
    @Override
    public List<HydraRenderLayer<T>> getRenderLayers() {
        return this.renderLayers.getRenderLayers();
    }

    /**
     * Adds a {@link HydraRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public HydraObjectRenderer<T> addRenderLayer(HydraRenderLayer<T> renderLayer) {
        this.renderLayers.addLayer(renderLayer);

        return this;
    }

    /**
     * Sets a scale override for this renderer, telling Hydra to pre-scale the model
     */
    public HydraObjectRenderer<T> withScale(float scale) {
        return withScale(scale, scale);
    }

    /**
     * Sets a scale override for this renderer, telling Hydra to pre-scale the model
     */
    public HydraObjectRenderer<T> withScale(float scaleWidth, float scaleHeight) {
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;

        return this;
    }

    /**
     * The entry render point for this renderer
     * <p>
     * Call this whenever you want to render your object
     *
     * @param poseStack The PoseStack to render under
     * @param animatable The {@link T} instance to render
     * @param bufferSource The BufferSource to render with, or null to use the default
     * @param renderType The specific RenderType to use, or null to fall back to {@link HydraRenderer#getRenderType}
     * @param buffer The VertexConsumer to use for rendering, or null to use the default for the RenderType
     * @param packedLight The light level at the given render position for rendering
     */
    @ApiStatus.Internal
    public void render(PoseStack poseStack, T animatable, @Nullable MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, int packedLight, float partialTick) {
        this.animatable = animatable;

        if (buffer == null)
            bufferSource = ((LevelRendererAccessor) Minecraft.getInstance().levelRenderer).getRenderBuffers().bufferSource();

        defaultRender(poseStack, animatable, bufferSource, renderType, buffer, 0, partialTick, packedLight);
    }

    /**
     * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling and translating
     * <p>
     * {@link PoseStack} translations made here are kept until the end of the render process
     */
    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedHydraModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        this.objectRenderTranslations = new Matrix4f(poseStack.last().pose());

        scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);

        poseStack.translate(0.5f, 0.51f, 0.5f);
    }

    /**
     * The actual render method that subtype renderers should override to handle their specific rendering tasks
     * <p>
     * {@link HydraRenderer#preRender} has already been called by this stage, and {@link HydraRenderer#postRender} will be called directly after
     */
    @Override
    public void actuallyRender(PoseStack poseStack, T animatable, BakedHydraModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        poseStack.pushPose();

        if (!isReRender) {
            AnimationState<T> animationState = new AnimationState<>(animatable, 0, 0, partialTick, false);
            long instanceId = getInstanceId(animatable);
            HydraModel<T> currentModel = getHydraModel();

            currentModel.addAdditionalStateData(animatable, instanceId, animationState::setData);
            currentModel.handleAnimations(animatable, instanceId, animationState, partialTick);
        }

        this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

        if (buffer != null)
            HydraRenderer.super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        poseStack.popPose();
    }

    /**
     * Called after all render operations are completed and the render pass is considered functionally complete.
     * <p>
     * Use this method to clean up any leftover persistent objects stored during rendering or any other post-render maintenance tasks as required
     */
    @Override
    public void doPostRenderCleanup() {
        this.animatable = null;
    }

    /**
     * Renders the provided {@link HydraBone} and its associated child bones
     */
    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, HydraBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.isTrackingMatrices()) {
            Matrix4f poseState = new Matrix4f(poseStack.last().pose());

            bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
            bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.objectRenderTranslations));
        }

        HydraRenderer.super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    /**
     * Update the current frame of a {@link AnimatableTexture potentially animated} texture used by this HydraRenderer
     * <p>
     * This should only be called immediately prior to rendering
     *
     * @see AnimatableTexture#setAndUpdate
     */
    @Override
    public void updateAnimatedTextureFrame(T animatable) {
        AnimatableTexture.setAndUpdate(getTextureLocation(animatable));
    }

    /**
     * Create and fire the relevant {@code CompileLayers} event hook for this renderer
     */
    @Override
    public void fireCompileRenderLayersEvent() {
        HydraServices.Client.getEvents().fireCompileObjectRenderLayers(this);
    }

    /**
     * Create and fire the relevant {@code Pre-Render} event hook for this renderer
     *
     * @return Whether the renderer should proceed based on the cancellation state of the event
     */
    @Override
    public boolean firePreRenderEvent(PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return HydraServices.Client.getEvents().fireObjectPreRender(this, poseStack, model, bufferSource, partialTick, packedLight);
    }

    /**
     * Create and fire the relevant {@code Post-Render} event hook for this renderer
     */
    @Override
    public void firePostRenderEvent(PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        HydraServices.Client.getEvents().fireObjectPostRender(this, poseStack, model, bufferSource, partialTick, packedLight);
    }
}
