package com.github.syren_dev_tech.hydra.model;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.GeoAnimatable;
import com.github.syren_dev_tech.hydra.cache.animation.Animation;
import com.github.syren_dev_tech.hydra.cache.HydraResources;
import com.github.syren_dev_tech.hydra.cache.model.BakedGeoModel;
import com.github.syren_dev_tech.hydra.constant.dataticket.DataTicket;
import com.github.syren_dev_tech.hydra.cache.animation.BakedAnimations;
import com.github.syren_dev_tech.hydra.renderer.GeoItemRenderer;
import com.github.syren_dev_tech.hydra.renderer.base.GeoRenderState;
import com.github.syren_dev_tech.hydra.renderer.base.GeoRenderer;

/// Base class for all code-based model objects
///
/// All models registered to a [GeoRenderer] should be an instance of this or one of its subclasses
///
/// @see <a href="https://github.com/bernie-g/hydra/wiki/Models">Hydra Wiki - Models</a>
public abstract class GeoModel<T extends GeoAnimatable> {
	/// Returns the resource ID for the [BakedGeoModel] (model JSON file) to render based on the provided [GeoRenderState]
	public abstract Identifier getModelResource(GeoRenderState renderState);

	/// Returns the resource path for the texture file to render based on the provided [GeoRenderState]
	public abstract Identifier getTextureResource(GeoRenderState renderState);

	/// Returns the resource ID for the [BakedAnimations] (animation JSON file) to use for animations based on the provided animatable
	public abstract Identifier getAnimationResource(T animatable);

	/// Returns the resource path for the [BakedAnimations] (animation JSON file) fallback locations in the event
	/// your animation isn't present in the [primary resource][#getAnimationResource(GeoAnimatable)].
	///
	/// Should **<u>NOT</u>** be used as the primary animation resource path, and in general shouldn't be used
	/// at all unless you know what you are doing
	public Identifier[] getAnimationResourceFallbacks(T animatable) {
		return new Identifier[0];
	}

    /// Add any additional [DataTicket]s you use in this model's getters here for use
    ///
    /// @param animatable The animatable being rendered
    /// @param relatedObject The associated data for the renderer calling this method (E.G. [GeoItemRenderer.RenderData])
    /// @param renderState The render state being compiled for this render pass
    public void addAdditionalStateData(T animatable, @Nullable Object relatedObject, GeoRenderState renderState) {}

    //<editor-fold defaultstate="collapsed" desc="<Internal Methods>">

	/// Get the baked geo model object used for rendering from the given resource path
	@ApiStatus.Internal
	public BakedGeoModel getBakedModel(Identifier location) {
        return HydraResources.getBakedModels().getModel(location);
	}

	/// Gets the baked [Animation] for the given animation `name`, if it exists
	///
	/// @param animatable The [GeoAnimatable] for the upcoming render pass
	/// @param name The name of the animation to retrieve
	/// @return The Animation instance for the provided `name`, or null if none match
	@ApiStatus.Internal
	public @Nullable Animation getBakedAnimation(T animatable, String name) throws RuntimeException {
        final Identifier animationFile = getAnimationResource(animatable);
        final Identifier[] fallbackFiles = getAnimationResourceFallbacks(animatable);

        return HydraResources.getBakedAnimations().getAnimation(animationFile, fallbackFiles, name);
	}
    //</editor-fold>
}
