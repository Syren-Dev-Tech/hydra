package com.github.syren_dev_tech.hydra.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.animation.AnimationState;
import com.github.syren_dev_tech.hydra.cache.object.*;
import com.github.syren_dev_tech.hydra.cache.texture.AnimatableTexture;
import com.github.syren_dev_tech.hydra.loading.math.MolangQueries;
import com.github.syren_dev_tech.hydra.model.HydraModel;
import com.github.syren_dev_tech.hydra.renderer.layer.HydraRenderLayer;
import com.github.syren_dev_tech.hydra.util.Color;
import com.github.syren_dev_tech.hydra.util.RenderUtil;

import java.lang.reflect.Method;
import java.util.List;

// TODO Split sources support
/**
 * Base interface for all Hydra renderers.<br>
 */
public interface HydraRenderer<T extends HydraAnimatable> {
    /**
     * Gets the model instance for this renderer
     */
    HydraModel<T> getHydraModel();

    /**
     * Gets the {@link HydraAnimatable} instance currently being rendered
     */
    T getAnimatable();

    /**
     * Gets the texture resource location to render for the given animatable
     */
    default ResourceLocation getTextureLocation(T animatable) {
        return getHydraModel().getTextureResource(animatable, this);
    }

    /**
     * Returns the list of registered {@link HydraRenderLayer HydraRenderLayers} for this renderer
     */
    default List<HydraRenderLayer<T>> getRenderLayers() {
        return List.of();
    }

    /**
     * Gets the {@link RenderType} to render the given animatable with
     * <p>
     * Uses the {@link RenderType#entityCutoutNoCull} {@code RenderType} by default
     * <p>
     * Override this to change the way a model will render (such as translucent models, etc)
     *
     * @return Return the RenderType to use, or null to prevent the model rendering. Returning null will not prevent animation functions taking place
     */
    @Nullable
    default RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return getHydraModel().getRenderType(animatable, texture);
    }

    /**
     * Gets a tint-applying color to render the given animatable with
     * <p>
     * Returns {@link Color#WHITE} by default
     */
    default Color getRenderColor(T animatable, float partialTick, int packedLight) {
        return Color.WHITE;
    }

    /**
     * Gets a packed overlay coordinate pair for rendering
     * <p>
     * Mostly just used for the red tint when an entity is hurt, but can be used for other things like the {@link net.minecraft.world.entity.monster.Creeper} white tint when exploding.
     */
    default int getPackedOverlay(T animatable, float u, float partialTick) {
        return OverlayTexture.NO_OVERLAY;
    }

    /**
     * Gets the id that represents the current animatable's instance for animation purposes
     * <p>
     * This is mostly useful for things like items, which have a single registered instance for all objects
     */
    default long getInstanceId(T animatable) {
        return animatable.hashCode();
    }

    /**
     * Determines the threshold value before the animatable should be considered moving for animation purposes
     * <p>
     * The default value and usage for this varies depending on the renderer
     * <ul>
     * <li>For entities, it represents the averaged lateral velocity of the object.</li>
     * <li>For {@link com.github.syren_dev_tech.hydra.animatable.HydraBlockEntity Tile Entities} and {@link com.github.syren_dev_tech.hydra.animatable.HydraItem Items}, it's currently unused</li>
     * </ul>
     *
     *
     * The lower the value, the more sensitive the {@link AnimationState#isMoving()} check will be.<br>
     * Particularly low values may have adverse effects however
     */
    default float getMotionAnimThreshold(T animatable) {
        return 0.015f;
    }

    /**
     * Initial access point for rendering. It all begins here
     * <p>
     * All Hydra renderers should immediately defer their respective default {@code render} calls to this, for consistent handling
     */
    default void defaultRender(PoseStack poseStack, T animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        poseStack.pushPose();

        int renderColor = getRenderColor(animatable, partialTick, packedLight).argbInt();
        int packedOverlay = getPackedOverlay(animatable, 0, partialTick);
        BakedHydraModel model = getHydraModel().getBakedModel(getHydraModel().getModelResource(animatable, this));

        if (renderType == null)
            renderType = getRenderType(animatable, getTextureLocation(animatable), bufferSource, partialTick);

        if (buffer == null && renderType != null)
            buffer = bufferSource.getBuffer(renderType);

        preRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, renderColor);

        if (firePreRenderEvent(poseStack, model, bufferSource, partialTick, packedLight)) {
            preApplyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
            actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, renderColor);
            applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
            postRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, renderColor);
            firePostRenderEvent(poseStack, model, bufferSource, partialTick, packedLight);
        }

        poseStack.popPose();

        renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, renderColor);
        doPostRenderCleanup();
        MolangQueries.clearActor();
    }

    /**
     * Re-renders the provided {@link BakedHydraModel} using the existing {@link HydraRenderer}
     * <p>
     * Usually you'd use this for rendering alternate {@link RenderType} layers or for sub-model rendering whilst inside a {@link HydraRenderLayer} or similar
     */
    default void reRender(BakedHydraModel model, PoseStack poseStack, MultiBufferSource bufferSource, T animatable, RenderType renderType, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int colour) {
        poseStack.pushPose();
        preRender(poseStack, animatable, model, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
        actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
        postRender(poseStack, animatable, model, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
        poseStack.popPose();
    }

    /**
     * The actual render method that sub-type renderers should override to handle their specific rendering tasks
     * <p>
     * {@link HydraRenderer#preRender} has already been called by this stage, and {@link HydraRenderer#postRender} will be called directly after
     */
    default void actuallyRender(PoseStack poseStack, T animatable, BakedHydraModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (buffer == null) {
            if (renderType == null)
                return;

            buffer = bufferSource.getBuffer(renderType);
        }

        updateAnimatedTextureFrame(animatable);

        for (HydraBone group : model.topLevelBones()) {
            renderRecursively(poseStack, animatable, group, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        }
    }

    /**
     * Calls back to the various {@link HydraRenderLayer RenderLayers} that have been registered to this renderer for their {@link HydraRenderLayer#preRender pre-render} actions.
     */
    default void preApplyRenderLayers(PoseStack poseStack, T animatable, BakedHydraModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        for (HydraRenderLayer<T> renderLayer : getRenderLayers()) {
            renderLayer.preRender(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        }
    }

    /**
     * Calls back to the various {@link HydraRenderLayer RenderLayers} that have been registered to this renderer for their {@link HydraRenderLayer#renderForBone per-bone} render actions.
     */
    default void applyRenderLayersForBone(PoseStack poseStack, T animatable, HydraBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        for (HydraRenderLayer<T> renderLayer : getRenderLayers()) {
            renderLayer.renderForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        }
    }

    // TODO append renderColor to layers
    /**
     * Render the various {@link HydraRenderLayer RenderLayers} that have been registered to this renderer
     */
    default void applyRenderLayers(PoseStack poseStack, T animatable, BakedHydraModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        for (HydraRenderLayer<T> renderLayer : getRenderLayers()) {
            renderLayer.render(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        }
    }

    /**
     * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling and translating
     * <p>
     * {@link PoseStack} translations made here are kept until the end of the render process
     */
    default void preRender(PoseStack poseStack, T animatable, BakedHydraModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {}

    /**
     * Called after rendering the model to buffer. Post-render modifications should be performed here
     * <p>
     * {@link PoseStack} transformations will be unused and lost once this method ends
     */
    default void postRender(PoseStack poseStack, T animatable, BakedHydraModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {}

    /**
     * Call after all other rendering work has taken place, including reverting the {@link PoseStack}'s state
     * <p>
     * This method is <u>not</u> called in {@link HydraRenderer#reRender re-render}
     */
    default void renderFinal(PoseStack poseStack, T animatable, BakedHydraModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int colour) {}

    /**
     * Called after all render operations are completed and the render pass is considered functionally complete.
     * <p>
     * Use this method to clean up any leftover persistent objects stored during rendering or any other post-render maintenance tasks as required
     */
    default void doPostRenderCleanup() {}

    /**
     * Renders the provided {@link HydraBone} and its associated child bones
     */
    default void renderRecursively(PoseStack poseStack, T animatable, HydraBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        poseStack.pushPose();
        RenderUtil.prepMatrixForBone(poseStack, bone);

        buffer = checkAndRefreshBuffer(isReRender, buffer, bufferSource, renderType);

        renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);

        if (!isReRender)
            applyRenderLayersForBone(poseStack, getAnimatable(), bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

        renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        poseStack.popPose();
    }

    /**
     * Renders the {@link HydraCube HydraCubes} associated with a given {@link HydraBone}
     */
    default void renderCubesOfBone(PoseStack poseStack, HydraBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, int colour) {
        if (bone.isHidden())
            return;

        for (HydraCube cube : bone.getCubes()) {
            poseStack.pushPose();
            renderCube(poseStack, cube, buffer, packedLight, packedOverlay, colour);
            poseStack.popPose();
        }
    }

    /**
     * Render the child bones of a given {@link HydraBone}
     * <p>
     * Note that this does not render the bone itself. That should be done through {@link HydraRenderer#renderCubesOfBone} separately
     */
    default void renderChildBones(PoseStack poseStack, T animatable, HydraBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.isHidingChildren())
            return;

        for (HydraBone childBone : bone.getChildBones()) {
            renderRecursively(poseStack, animatable, childBone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        }
    }

    /**
     * Renders an individual {@link HydraCube}
     * <p>
     * This tends to be called recursively from something like {@link HydraRenderer#renderCubesOfBone}
     */
    default void renderCube(PoseStack poseStack, HydraCube cube, VertexConsumer buffer, int packedLight, int packedOverlay, int colour) {
        RenderUtil.translateToPivotPoint(poseStack, cube);
        RenderUtil.rotateMatrixAroundCube(poseStack, cube);
        RenderUtil.translateAwayFromPivotPoint(poseStack, cube);

        Matrix3f normalisedPoseState = poseStack.last().normal();
        Matrix4f poseState = new Matrix4f(poseStack.last().pose());

        for (HydraQuad quad : cube.quads()) {
            if (quad == null)
                continue;

            Vector3f normal = normalisedPoseState.transform(new Vector3f(quad.normal()));

            RenderUtil.fixInvertedFlatCube(cube, normal);
            createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, colour);
        }
    }

    /**
     * Applies the {@link HydraQuad Quad's} {@link HydraVertex vertices} to the given {@link VertexConsumer buffer} for rendering
     */
    default void createVerticesOfQuad(HydraQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer, int packedLight, int packedOverlay, int colour) {
        for (HydraVertex vertex : quad.vertices()) {
            Vector3f position = vertex.position();
            Vector4f vector4f = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0f));

            buffer.addVertex(vector4f.x(), vector4f.y(), vector4f.z(), colour, vertex.texU(), vertex.texV(), packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
        }
    }

    /**
     * Create and fire the relevant {@code CompileLayers} event hook for this renderer
     */
    void fireCompileRenderLayersEvent();

    /**
     * Create and fire the relevant {@code Pre-Render} event hook for this renderer.<br>
     *
     * @return Whether the renderer should proceed based on the cancellation state of the event
     */
    boolean firePreRenderEvent(PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight);

    /**
     * Create and fire the relevant {@code Post-Render} event hook for this renderer
     */
    void firePostRenderEvent(PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight);

    /**
     * Scales the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link HydraRenderLayer} or external render call
     * <p>
     * Override and call super with modified scale values as needed to further modify the scale of the model (E.G. child entities)
     */
    default void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, T animatable, BakedHydraModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        if (!isReRender && (widthScale != 1 || heightScale != 1))
            poseStack.scale(widthScale, heightScale, widthScale);
    }

    /**
     * Update the current frame of a {@link AnimatableTexture potentially animated} texture used by this HydraRenderer
     * <p>
     * This should only be called immediately prior to rendering
     *
     * @see AnimatableTexture#setAndUpdate
     */
    void updateAnimatedTextureFrame(T animatable);

    /**
     * Bandaid-patch to fix the buffer change Mojang made in 1.21.
     *
     * This will be removed in a future breaking update, but will require an adjustment to the underlying Hydra API.
     */
    @ApiStatus.Internal
    default VertexConsumer checkAndRefreshBuffer(boolean isReRender, VertexConsumer buffer, MultiBufferSource bufferSource, RenderType renderType) {
        if (isReRender)
            return buffer;

        if (buffer instanceof BufferBuilder builder && !isBufferBuilderBuilding(builder))
            return bufferSource.getBuffer(renderType);
        if (isOutlineGenerator(buffer) && bufferNeedsRefresh(getOutlineGeneratorDelegate(buffer)))
            return createOutlineGenerator(bufferSource.getBuffer(renderType), getOutlineGeneratorColor(buffer));
        return buffer;
    }

    static final Method BUFFER_BUILDER_BUILDING_METHOD = loadBufferBuilderMethod();
    static final Class<?> OUTLINE_GENERATOR_CLASS = loadOutlineGeneratorClass();
    static final java.lang.reflect.Constructor<?> OUTLINE_GENERATOR_CONSTRUCTOR = loadOutlineGeneratorConstructor();

    private static Method loadBufferBuilderMethod() {
        try {
            return BufferBuilder.class.getDeclaredMethod("building");
        } catch (NoSuchMethodException e) {
            // building() method not available in this version
            return null;
        }
    }

    private static Class<?> loadOutlineGeneratorClass() {
        try {
            return Class.forName("net.minecraft.client.renderer.OutlineBufferSource$EntityOutlineGenerator");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static java.lang.reflect.Constructor<?> loadOutlineGeneratorConstructor() {
        if (OUTLINE_GENERATOR_CLASS == null)
            return null;
        try {
            return OUTLINE_GENERATOR_CLASS.getDeclaredConstructor(VertexConsumer.class, int.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static boolean isBufferBuilderBuilding(BufferBuilder builder) {
        try {
            if (BUFFER_BUILDER_BUILDING_METHOD != null) {
                return (boolean) BUFFER_BUILDER_BUILDING_METHOD.invoke(builder);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean bufferNeedsRefresh(VertexConsumer buffer) {
        if (buffer instanceof BufferBuilder builder) {
            return !isBufferBuilderBuilding(builder);
        }
        return true;
    }

    private static boolean isOutlineGenerator(VertexConsumer buffer) {
        return OUTLINE_GENERATOR_CLASS != null && OUTLINE_GENERATOR_CLASS.isInstance(buffer);
    }

    private static VertexConsumer getOutlineGeneratorDelegate(VertexConsumer buffer) {
        try {
            return (VertexConsumer) OUTLINE_GENERATOR_CLASS.getDeclaredMethod("delegate").invoke(buffer);
        } catch (Exception e) {
            return null;
        }
    }

    private static int getOutlineGeneratorColor(VertexConsumer buffer) {
        try {
            return (int) OUTLINE_GENERATOR_CLASS.getDeclaredMethod("color").invoke(buffer);
        } catch (Exception e) {
            return 0;
        }
    }

    private static VertexConsumer createOutlineGenerator(VertexConsumer delegate, int color) {
        try {
            return (VertexConsumer) OUTLINE_GENERATOR_CONSTRUCTOR.newInstance(delegate, color);
        } catch (Exception e) {
            return delegate;
        }
    }
}
