package com.github.syren_dev_tech.hydra.renderer.layer;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.renderer.HydraRenderer;

import java.util.List;

/**
 * Base interface for a container for {@link HydraRenderLayer HydraRenderLayers}
 * <p>
 * Each renderer should contain an instance of this, for holding its layers and handling events
 */
public class HydraRenderLayersContainer<T extends HydraAnimatable> {
	private final HydraRenderer<T> renderer;
	private final List<HydraRenderLayer<T>> layers = new ObjectArrayList<>();
	private boolean compiledLayers = false;

	public HydraRenderLayersContainer(HydraRenderer<T> renderer) {
		this.renderer = renderer;
	}

	/**
	 * Get the {@link HydraRenderLayer} list for usage
	 */
	public List<HydraRenderLayer<T>> getRenderLayers() {
		if (!this.compiledLayers)
			fireCompileRenderLayersEvent();

		return this.layers;
	}

	/**
	 * Add a new render layer to the container
	 */
	public void addLayer(HydraRenderLayer<T> layer) {
		this.layers.add(layer);
	}

	/**
	 * Create and fire the relevant {@code CompileRenderLayers} event hook for the owning renderer
	 */
	public void fireCompileRenderLayersEvent() {
		this.compiledLayers = true;

		this.renderer.fireCompileRenderLayersEvent();
	}
}
