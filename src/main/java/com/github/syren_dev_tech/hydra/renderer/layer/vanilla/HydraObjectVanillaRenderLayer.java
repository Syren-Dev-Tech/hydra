package com.github.syren_dev_tech.hydra.renderer.layer.vanilla;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.renderer.HydraObjectRenderer;

import java.util.function.Function;

/**
 * Built-in implementation of {@link AttachedAnimatableRenderLayer} specifically for {@link HydraObjectRenderer} rendering
 * <p>
 * This handles the vast majority of the boilerplate code for rendering a HydraObjectRenderer on a vanilla entity
 */
public abstract class HydraObjectVanillaRenderLayer<A extends HydraAnimatable, T extends Entity, M extends EntityModel<T>, R extends HydraObjectRenderer<A>> extends AttachedAnimatableRenderLayer<A, T, M, R> {
	/**
	 * Create a new {@link RenderLayer} instance
	 *
	 * @param renderer The vanilla renderer instance that the layer is being added to
	 * @param instanceFactory A factory that creates a new HydraObjectRenderer instance for rendering
	 */
	public HydraObjectVanillaRenderLayer(RenderLayerParent<T, M> renderer, Function<Level, A> instanceFactory) {
		super(renderer, instanceFactory);
	}
}
