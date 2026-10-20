package com.github.syren_dev_tech.hydra.renderer.layer.vanilla;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.HydraEntity;
import com.github.syren_dev_tech.hydra.renderer.HydraEntityRenderer;
import com.github.syren_dev_tech.hydra.renderer.HydraRenderer;

import java.util.function.Function;

/**
 * Built-in implementation of {@link AttachedAnimatableRenderLayer} specifically for {@link HydraEntity} rendering
 * <p>
 * This handles the vast majority of the boilerplate code for rendering a HydraEntity on a vanilla entity
 */
public abstract class HydraEntityVanillaRenderLayer<A extends Entity & HydraEntity, T extends Entity, M extends EntityModel<T>, R extends HydraEntityRenderer<A>> extends AttachedAnimatableRenderLayer<A, T, M, R> {
	/**
	 * Create a new {@link RenderLayer} instance to render a {@link HydraEntity} on a vanilla entity model
	 *
	 * @param renderer        The vanilla renderer instance that the layer is being added to
	 * @param instanceFactory A factory that creates a new HydraEntity instance for rendering
	 */
	public HydraEntityVanillaRenderLayer(RenderLayerParent<T, M> renderer, Function<Level, A> instanceFactory) {
		super(renderer, instanceFactory);
	}
	
	/**
	 * Get the {@link HydraRenderer} instance for this {@link HydraEntity}
	 */
	@Override
	protected @Nullable R getRenderer(A animatable) {
		final EntityRenderDispatcher entityRenderers = Minecraft.getInstance().getEntityRenderDispatcher();
		
		//noinspection unchecked
		return entityRenderers.getRenderer(animatable) instanceof HydraEntityRenderer<? super A> hydraRenderer ? (R)hydraRenderer : null;
	}
}
