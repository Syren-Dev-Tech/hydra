package com.github.syren_dev_tech.hydra.renderer.layer.vanilla;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.animatable.HydraBlockEntity;
import com.github.syren_dev_tech.hydra.renderer.HydraBlockRenderer;
import com.github.syren_dev_tech.hydra.renderer.HydraRenderer;

import java.util.function.Function;

/**
 * Built-in implementation of {@link AttachedAnimatableRenderLayer} specifically for {@link HydraBlockEntity} rendering
 * <p>
 * This handles the vast majority of the boilerplate code for rendering a HydraBlockEntity on a vanilla entity
 */
public abstract class HydraBlockVanillaRenderLayer<A extends BlockEntity & HydraBlockEntity, T extends Entity, M extends EntityModel<T>, R extends HydraBlockRenderer<A>> extends AttachedAnimatableRenderLayer<A, T, M, R> {
	/**
	 * Create a new {@link RenderLayer} instance to render a {@link HydraBlockEntity} on a vanilla entity model
	 *
	 * @param renderer        The vanilla renderer instance that the layer is being added to
	 * @param instanceFactory A factory that creates a new HydraBlockEntity instance for rendering
	 */
	public HydraBlockVanillaRenderLayer(RenderLayerParent<T, M> renderer, Function<Level, A> instanceFactory) {
		super(renderer, instanceFactory);
	}
	/**
	 * Get the {@link HydraRenderer} instance for this {@link HydraAnimatable}
	 */
	/**
	 * Get the {@link HydraRenderer} instance for this {@link HydraBlockEntity}
	 */
	@Override
	protected @Nullable R getRenderer(A animatable) {
		final BlockEntityRenderDispatcher blockRenderers = Minecraft.getInstance().getBlockEntityRenderDispatcher();
		
		//noinspection unchecked
		return blockRenderers.getRenderer(animatable) instanceof HydraBlockRenderer<? super A> hydraRenderer ? (R)hydraRenderer : null;
	}
}
