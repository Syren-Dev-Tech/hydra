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

import java.util.List;
import java.util.function.Supplier;

/**
 * A more efficient version of {@link BoneFilterHydraLayer}
 * <p>
 * This version requires you provide the list of bones to filter up-front, so that the bone hierarchy doesn't need to be traversed
 */
public class FastBoneFilterHydraLayer<T extends HydraAnimatable> extends BoneFilterHydraLayer<T> {
	protected final Supplier<List<String>> boneSupplier;

	public FastBoneFilterHydraLayer(HydraRenderer<T> renderer) {
		this(renderer, List::of);
	}

	public FastBoneFilterHydraLayer(HydraRenderer<T> renderer, Supplier<List<String>> boneSupplier) {
		this(renderer, boneSupplier, (bone, animatable, partialTick) -> {});
	}

	public FastBoneFilterHydraLayer(HydraRenderer<T> renderer, Supplier<List<String>> boneSupplier, TriConsumer<HydraBone, T, Float> checkAndApply) {
		super(renderer, checkAndApply);

		this.boneSupplier = boneSupplier;
	}

	/**
	 * Return a list of bone names to grab to then be filtered
	 * <p>
	 * This is even more efficient if you use a cached list.
	 */
	protected List<String> getAffectedBones() {
		return boneSupplier.get();
	};

	@Override
	public void preRender(PoseStack poseStack, T animatable, BakedHydraModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource,
						  @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		for (String boneName : getAffectedBones()) {
			this.renderer.getHydraModel().getBone(boneName).ifPresent(bone -> checkAndApply(bone, animatable, partialTick));
		}
	}
}
