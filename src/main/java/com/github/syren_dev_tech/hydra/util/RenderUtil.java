package com.github.syren_dev_tech.hydra.util;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import com.github.syren_dev_tech.hydra.HydraConstants;
import com.github.syren_dev_tech.hydra.HydraServices;
import com.github.syren_dev_tech.hydra.cache.object.HydraBone;
import com.github.syren_dev_tech.hydra.cache.object.HydraCube;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.model.HydraModel;
import com.github.syren_dev_tech.hydra.renderer.HydraRenderer;
import com.github.syren_dev_tech.hydra.renderer.HydraReplacedEntityRenderer;

/**
 * Helper class for various methods and functions useful while rendering
 */
public final class RenderUtil {
    public static void translateMatrixToBone(PoseStack poseStack, HydraBone bone) {
        poseStack.translate(-bone.getPosX() / 16f, bone.getPosY() / 16f, bone.getPosZ() / 16f);
    }

    public static void rotateMatrixAroundBone(PoseStack poseStack, HydraBone bone) {
        RenderUtil.optionalRotateZYX(poseStack, bone.getRotZ(), bone.getRotY(), bone.getRotX());
    }

    public static void rotateMatrixAroundCube(PoseStack poseStack, HydraCube cube) {
        Vec3 rotation = cube.rotation();

        poseStack.mulPose(new Quaternionf().rotationXYZ(0, 0, (float) rotation.z()));
        poseStack.mulPose(new Quaternionf().rotationXYZ(0, (float) rotation.y(), 0));
        poseStack.mulPose(new Quaternionf().rotationXYZ((float) rotation.x(), 0, 0));
    }

    public static void scaleMatrixForBone(PoseStack poseStack, HydraBone bone) {
        poseStack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
    }

    public static void translateToPivotPoint(PoseStack poseStack, HydraCube cube) {
        Vec3 pivot = cube.pivot();
        poseStack.translate(pivot.x() / 16f, pivot.y() / 16f, pivot.z() / 16f);
    }

    public static void translateToPivotPoint(PoseStack poseStack, HydraBone bone) {
        poseStack.translate(bone.getPivotX() / 16f, bone.getPivotY() / 16f, bone.getPivotZ() / 16f);
    }

    public static void translateAwayFromPivotPoint(PoseStack poseStack, HydraCube cube) {
        Vec3 pivot = cube.pivot();

        poseStack.translate(-pivot.x() / 16f, -pivot.y() / 16f, -pivot.z() / 16f);
    }

    public static void translateAwayFromPivotPoint(PoseStack poseStack, HydraBone bone) {
        poseStack.translate(-bone.getPivotX() / 16f, -bone.getPivotY() / 16f, -bone.getPivotZ() / 16f);
    }

    public static void translateAndRotateMatrixForBone(PoseStack poseStack, HydraBone bone) {
        translateToPivotPoint(poseStack, bone);
        rotateMatrixAroundBone(poseStack, bone);
    }

    public static void prepMatrixForBone(PoseStack poseStack, HydraBone bone) {
        translateMatrixToBone(poseStack, bone);
        translateToPivotPoint(poseStack, bone);
        rotateMatrixAroundBone(poseStack, bone);
        scaleMatrixForBone(poseStack, bone);
        translateAwayFromPivotPoint(poseStack, bone);
    }

    public static Matrix4f invertAndMultiplyMatrices(Matrix4f baseMatrix, Matrix4f inputMatrix) {
        inputMatrix = new Matrix4f(inputMatrix);

        inputMatrix.invert();
        inputMatrix.mul(baseMatrix);

        return inputMatrix;
    }

    /**
     * Translates the provided {@link PoseStack} to face towards the given {@link Entity}'s rotation
     * <p>
     * Usually used for rotating projectiles towards their trajectory, in an {@link HydraRenderer#preRender} override
     */
    public static void faceRotation(PoseStack poseStack, Entity animatable, float partialTick) {
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) - 90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
    }

    /**
     * Add a positional vector to a matrix
     * <p>
     * This is specifically implemented to act as a translation of an x/y/z coordinate triplet to a render matrix
     */
    public static Matrix4f translateMatrix(Matrix4f matrix, Vector3f vector) {
        return matrix.add(new Matrix4f().m30(vector.x).m31(vector.y).m32(vector.z));
    }

    /**
     * Gets the actual dimensions of a texture resource from a given path
     * <p>
     * Not performance-efficient, and should not be relied upon
     *
     * @param texture The path of the texture resource to check
     * @return The dimensions (width x height) of the texture, or null if unable to find or read the file
     */
    @Nullable
    public static IntIntPair getTextureDimensions(ResourceLocation texture) {
        if (texture == null)
            return null;

        AbstractTexture originalTexture = null;
        Minecraft mc = Minecraft.getInstance();

        try {
            originalTexture = mc.submit(() -> mc.getTextureManager().getTexture(texture)).get();
        } catch (Exception e) {
            HydraConstants.LOGGER.warn("Failed to load image for id {}", texture);
            e.printStackTrace();
        }

        if (originalTexture == null)
            return null;

        NativeImage image = null;

        try {
            image = originalTexture instanceof DynamicTexture dynamicTexture ? dynamicTexture.getPixels() : NativeImage.read(mc.getResourceManager().getResource(texture).get().open());
        } catch (Exception e) {
            HydraConstants.LOGGER.error("Failed to read image for id {}", texture);
            e.printStackTrace();
        }

        return image == null ? null : IntIntImmutablePair.of(image.getWidth(), image.getHeight());
    }

    public static double getCurrentSystemTick() {
        return System.nanoTime() / 1E6 / 50d;
    }

    /**
     * Returns the current time (in ticks) that the {@link org.lwjgl.glfw.GLFW GLFW} instance has been running
     * <p>
     * This is effectively a permanent timer that counts up since the game was launched.
     */
    public static double getCurrentTick() {
        return Blaze3D.getTime() * 20d;
    }

    /**
     * Returns a float equivalent of a boolean
     * <p>
     * Output table:
     * <ul>
     * <li>true -> 1</li>
     * <li>false -> 0</li>
     * </ul>
     */
    public static float booleanToFloat(boolean input) {
        return input ? 1f : 0f;
    }

    /**
     * Converts a given double array to its {@link Vec3} equivalent
     */
    public static Vec3 arrayToVec(double[] array) {
        final double x;
        final double y;
        final double z;

        if (array.length == 1) {
            x = y = z = array[0];
        } else {
            x = array[0];
            y = array[1];
            z = array[2];
        }

        if (x == 0 && y == 0 && z == 0)
            return Vec3.ZERO;

        return new Vec3(x, y, z);
    }

    /**
     * Rotates a {@link HydraBone} to match a provided {@link ModelPart}'s rotations
     * <p>
     * Usually used for items or armor rendering to match the rotations of other non-hydra model parts
     */
    public static void matchModelPartRot(ModelPart from, HydraBone to) {
        to.updateRotation(-from.xRot, -from.yRot, from.zRot);
    }

    /**
     * If a {@link HydraCube} is a 2d plane the {@link com.github.syren_dev_tech.hydra.cache.object.HydraQuad Quad's} normal is inverted in an intersecting plane,it can cause issues with shaders and other lighting tasks
     * <p>
     * This performs a pseudo-ABS function to help resolve some of those issues
     */
    public static void fixInvertedFlatCube(HydraCube cube, Vector3f normal) {
        if (normal.x() < 0 && (cube.size().y() == 0 || cube.size().z() == 0))
            normal.mul(-1, 1, 1);

        if (normal.y() < 0 && (cube.size().x() == 0 || cube.size().z() == 0))
            normal.mul(1, -1, 1);

        if (normal.z() < 0 && (cube.size().x() == 0 || cube.size().y() == 0))
            normal.mul(1, 1, -1);
    }

    /**
     * Rotate a [PoseStack] around a given z/y/x axis, reducing the amount of work performed to the minimal required for the given values
     */
    public static void optionalRotateZYX(PoseStack poseStack, double z, double y, double x) {
        if (z == 0 && y == 0 && x == 0)
            return;

        final Quaternionf quat = new Quaternionf();

        if (x == 0 && y == 0) {
            quat.rotationZ((float) z);
        } else if (x == 0 && z == 0) {
            quat.rotationY((float) y);
        } else if (y == 0 && z == 0) {
            quat.rotationX((float) x);
        } else {
            quat.rotationZYX((float) z, (float) y, (float) x);
        }

        poseStack.mulPose(quat);
    }

    /**
     * Converts a {@link Direction} to a rotational float for rotation purposes
     */
    public static float getDirectionAngle(Direction direction) {
        return switch (direction) {
            case SOUTH -> 90f;
            case NORTH -> 270f;
            case EAST -> 180f;
            default -> 0f;
        };
    }

    /**
     * Special helper function for lerping yaw.
     * <p>
     * This exists because yaw in Minecraft handles its yaw a bit strangely, and can cause incorrect results if lerped without accounting for special-cases
     */
    public static double lerpYaw(double delta, double start, double end) {
        start = Mth.wrapDegrees(start);
        end = Mth.wrapDegrees(end);
        double diff = start - end;
        end = diff > 180 || diff < -180 ? start + Math.copySign(360 - Math.abs(diff), diff) : end;

        return Mth.lerp(delta, start, end);
    }

    /**
     * Gets a {@link HydraModel} instance from a given {@link EntityType}
     * <p>
     * This only works if you're calling this method for an EntityType known to be using a {@link HydraRenderer Hydra Renderer}
     * <p>
     * Generally speaking you probably shouldn't be calling this method at all.
     *
     * @param entityType The {@code EntityType} to retrieve the HydraModel for
     * @return The HydraModel, or null if one isn't found
     */
    @Nullable
    public static HydraModel<?> getHydraModelForEntityType(EntityType<?> entityType) {
        EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(entityType);

        return renderer instanceof HydraRenderer<?> hydraRenderer ? hydraRenderer.getHydraModel() : null;
    }

    /**
     * Gets a HydraAnimatable instance that has been registered as the replacement renderer for a given {@link EntityType}
     *
     * @param entityType The {@code EntityType} to retrieve the replaced {@link HydraAnimatable} for
     * @return The {@code HydraAnimatable} instance, or null if one isn't found
     */
    @Nullable
    public static HydraAnimatable getReplacedAnimatable(EntityType<?> entityType) {
        EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(entityType);

        return renderer instanceof HydraReplacedEntityRenderer<?, ?> replacedEntityRenderer ? replacedEntityRenderer.getAnimatable() : null;
    }

    /**
     * Gets a {@link HydraModel} instance from a given {@link Entity}
     * <p>
     * This only works if you're calling this method for an Entity known to be using a {@link HydraRenderer Hydra Renderer}
     * <p>
     * Generally speaking you probably shouldn't be calling this method at all.
     *
     * @param entity The {@code Entity} to retrieve the HydraModel for
     * @return The HydraModel, or null if one isn't found
     */
    @Nullable
    public static HydraModel<?> getHydraModelForEntity(Entity entity) {
        EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);

        return renderer instanceof HydraRenderer<?> hydraRenderer ? hydraRenderer.getHydraModel() : null;
    }

    /**
     * Gets a {@link HydraModel} instance from a given {@link Item}
     * <p>
     * This only works if you're calling this method for an Item known to be using a {@link HydraRenderer Hydra Renderer}
     * <p>
     * Generally speaking you probably shouldn't be calling this method at all.
     *
     * @param item The {@code ItemStack} to retrieve the HydraModel for
     * @return The HydraModel, or null if one isn't found
     */
    @Nullable
    public static HydraModel<?> getHydraModelForItem(ItemStack item) {
        return HydraServices.Client.ITEM_RENDERING.getHydraModelForItem(item);
    }

    /**
     * Gets a {@link HydraModel} instance from a given {@link BlockEntity}
     * <p>
     * This only works if you're calling this method for a BlockEntity known to be using a {@link HydraRenderer Hydra Renderer}
     * <p>
     * Generally speaking you probably shouldn't be calling this method at all
     *
     * @param blockEntity The {@code BlockEntity} to retrieve the HydraModel for
     * @return The HydraModel, or null if one isn't found
     */
    @Nullable
    public static HydraModel<?> getHydraModelForBlock(BlockEntity blockEntity) {
        BlockEntityRenderer<?> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity);

        return renderer instanceof HydraRenderer<?> hydraRenderer ? hydraRenderer.getHydraModel() : null;
    }

    /**
     * Gets a {@link HydraModel} instance from a given {@link Item}
     * <p>
     * This only works if you're calling this method for an Item known to be using a {@link com.github.syren_dev_tech.hydra.renderer.HydraArmorRenderer HydraArmorRenderer}
     * <p>
     * Generally speaking you probably shouldn't be calling this method at all
     *
     * @param stack The ItemStack to retrieve the HydraModel for
     * @return The HydraModel, or null if one isn't found
     */
    @Nullable
    public static HydraModel<?> getHydraModelForArmor(ItemStack stack) {
        return HydraServices.Client.ITEM_RENDERING.getHydraModelForArmor(stack);
    }
}
