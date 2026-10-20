package com.github.syren_dev_tech.hydra.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.animation.AnimationState;
import com.github.syren_dev_tech.hydra.cache.object.HydraBone;
import com.github.syren_dev_tech.hydra.constant.DataTickets;
import com.github.syren_dev_tech.hydra.model.data.EntityModelData;

/**
 * {@link DefaultedHydraModel} specific to {@link net.minecraft.world.entity.Entity Entities}
 * <p>
 * Using this class pre-sorts provided asset paths into the "entity" subdirectory
 * <p>
 * Additionally it can automatically handle head-turning if the entity has a "head" bone
 */
public class DefaultedEntityHydraModel<T extends HydraAnimatable> extends DefaultedHydraModel<T> {
	@Nullable
	protected String headBone;
	@Deprecated(forRemoval = true)
	protected boolean turnsHead; // Use headbone's non-null state instead

	/**
	 * Create a new instance of this model class
	 * <p>
	 * The asset path should be the truncated relative path from the base folder
	 * <p>
	 * E.G.
	 * <pre>{@code
	 * 	new ResourceLocation("myMod", "animals/red_fish")
	 * }</pre>
	 */
	public DefaultedEntityHydraModel(ResourceLocation assetSubpath) {
		this(assetSubpath, false);
	}

	public DefaultedEntityHydraModel(ResourceLocation assetSubpath, boolean turnsHead) {
		this(assetSubpath, turnsHead ? "head" : null);
	}

	/**
	 * Create a new instance of this model class, optionally providing the name of the head bone to auto-handle rotating
	 */
	public DefaultedEntityHydraModel(ResourceLocation assetSubpath, @Nullable String headBone) {
		super(assetSubpath);

		this.turnsHead = headBone != null;
		this.headBone = headBone;
	}

	/**
	 * Returns the subtype string for this type of model
	 * <p>
	 * This allows for sorting of asset files into neat subdirectories for clean management.
	 */
	@Override
	protected String subtype() {
		return "entity";
	}

	@Override
	public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
		if (this.headBone == null || !this.turnsHead)
			return;

		HydraBone head = getAnimationProcessor().getBone(this.headBone);

		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}
	}

	/**
	 * Changes the constructor-defined model path for this model to an alternate
	 * <p>
	 * This is useful if your animatable shares a model path with another animatable that differs in path to the texture and animations for this model
	 */
	@Override
	public DefaultedEntityHydraModel<T> withAltModel(ResourceLocation altPath) {
		return (DefaultedEntityHydraModel<T>)super.withAltModel(altPath);
	}

	/**
	 * Changes the constructor-defined animations path for this model to an alternate
	 * <p>
	 * This is useful if your animatable shares an animations path with another animatable that differs in path to the model and texture for this model
	 */
	@Override
	public DefaultedEntityHydraModel<T> withAltAnimations(ResourceLocation altPath) {
		return (DefaultedEntityHydraModel<T>)super.withAltAnimations(altPath);
	}

	/**
	 * Changes the constructor-defined texture path for this model to an alternate
	 * <p>
	 * This is useful if your animatable shares a texture path with another animatable that differs in path to the model and animations for this model
	 */
	@Override
	public DefaultedEntityHydraModel<T> withAltTexture(ResourceLocation altPath) {
		return (DefaultedEntityHydraModel<T>)super.withAltTexture(altPath);
	}
}
