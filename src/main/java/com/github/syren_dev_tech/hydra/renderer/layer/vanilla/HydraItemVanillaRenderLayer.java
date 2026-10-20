package com.github.syren_dev_tech.hydra.renderer.layer.vanilla;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.HydraItem;
import com.github.syren_dev_tech.hydra.animatable.client.HydraRenderProvider;
import com.github.syren_dev_tech.hydra.renderer.HydraItemRenderer;
import com.github.syren_dev_tech.hydra.renderer.HydraRenderer;

import java.util.function.Function;

/**
 * Built-in implementation of {@link AttachedAnimatableRenderLayer} specifically for {@link HydraItem} rendering
 * <p>
 * This handles the vast majority of the boilerplate code for rendering a HydraItem on a vanilla entity
 */
public abstract class HydraItemVanillaRenderLayer<A extends Item & HydraItem, T extends Entity, M extends EntityModel<T>, R extends HydraItemRenderer<A>> extends AttachedAnimatableRenderLayer<A, T, M, R> {
	/**
	 * Create a new {@link RenderLayer} instance
	 *
	 * @param renderer The vanilla renderer instance that the layer is being added to
	 * @param instanceFactory A factory that creates a new HydraItem instance for rendering
	 */
	public HydraItemVanillaRenderLayer(RenderLayerParent<T, M> renderer, Function<Level, A> instanceFactory) {
		super(renderer, instanceFactory);
	}
	
	/**
	 * Get the {@link HydraRenderer} instance for this {@link HydraItem}
	 */
	@Override
	protected @Nullable R getRenderer(A animatable) {
		//noinspection unchecked,rawtypes
		return HydraRenderProvider.of(animatable).getHydraItemRenderer() instanceof HydraItemRenderer hydraRenderer ? (R)hydraRenderer : null;
	}
}
