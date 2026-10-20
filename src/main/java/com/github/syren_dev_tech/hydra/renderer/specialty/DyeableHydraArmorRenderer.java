package com.github.syren_dev_tech.hydra.renderer.specialty;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.HydraItem;
import com.github.syren_dev_tech.hydra.cache.object.BakedHydraModel;
import com.github.syren_dev_tech.hydra.cache.object.HydraBone;
import com.github.syren_dev_tech.hydra.renderer.HydraArmorRenderer;
import com.github.syren_dev_tech.hydra.util.Color;
import com.github.syren_dev_tech.hydra.model.HydraModel;

import java.util.Collection;
import java.util.Set;

/**
 * A shallow-level dyeable armour renderer for Hydra armor models
 * <p>
 * This approach avoids needing to change the JSON object format to natively support dyes, which is a whole can of worms
 */
public abstract class DyeableHydraArmorRenderer<T extends Item & HydraItem> extends HydraArmorRenderer<T> {
    protected final Set<HydraBone> dyeableBones = new ObjectArraySet<>();
    protected BakedHydraModel lastModel = null;

    public DyeableHydraArmorRenderer(HydraModel<T> model) {
        super(model);
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedHydraModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        if (!isReRender)
            checkBoneDyeCache(animatable, model, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public void renderCubesOfBone(PoseStack poseStack, HydraBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, int colour) {
        if (this.dyeableBones.contains(bone)) {
            final Color color = getColorForBone(bone);

            colour = FastColor.ARGB32.multiply(colour, color.argbInt());
        }

        super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);
    }

    /**
     * Whether the given HydraBone should be considered dyeable or not
     * <p>
     * Note that values returned from here are cached for the last rendered {@link BakedHydraModel} and require a manual reset if you intend to change these results
     *
     * @return whether the bone should be dyed or not
     */
    protected abstract boolean isBoneDyeable(HydraBone bone);

    /**
     * What color the given HydraBone should be dyed as
     * <p>
     * Only bones that were marked as 'dyeable' in {@link DyeableHydraArmorRenderer#isBoneDyeable(HydraBone)} are provided here
     */
    @NotNull
    protected abstract Color getColorForBone(HydraBone bone);

    /**
     * Check whether the dye cache should be considered dirty and recomputed
     * <p>
     * The less this forces re-computation, the better for performance
     */
    protected void checkBoneDyeCache(T animatable, BakedHydraModel model, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (model != this.lastModel) {
            this.dyeableBones.clear();
            this.lastModel = model;
            collectDyeableBones(model.topLevelBones());
        }
    }

    /**
     * Recursively parse through the given bones collection, collecting and caching dyeable bones as applicable
     */
    protected void collectDyeableBones(Collection<HydraBone> bones) {
        for (HydraBone bone : bones) {
            if (isBoneDyeable(bone))
                this.dyeableBones.add(bone);

            collectDyeableBones(bone.getChildBones());
        }
    }
}
