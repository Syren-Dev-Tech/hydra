package com.github.syren_dev_tech.hydra.animation.state;

import com.github.syren_dev_tech.hydra.animatable.GeoAnimatable;
import com.github.syren_dev_tech.hydra.animation.AnimationController;
import com.github.syren_dev_tech.hydra.cache.animation.Keyframe;
import com.github.syren_dev_tech.hydra.cache.animation.keyframeevent.KeyFrameData;
import com.github.syren_dev_tech.hydra.renderer.base.GeoRenderState;

/// Keyframe event object generated when a keyframe marker is encountered for the current render pass by an
/// [AnimationController]
///
/// @param animatable The [GeoAnimatable] being animated
/// @param renderState The [GeoRenderState] for this render pass
/// @param controller The [AnimationController] responsible for the currently playing animation
/// @param keyframeData The [KeyFrameData] relevant to the encountered [Keyframe]
/// @param <T> Animatable class type
/// @param <E> Keyframe data class type
///
/// @see AnimationController.KeyframeEventHandler
public record KeyFrameEvent<T extends GeoAnimatable, E extends KeyFrameData>(T animatable, GeoRenderState renderState, AnimationController<T> controller, E keyframeData) {
    /// Return the partial tick value for the current render pass
    public float getPartialTick() {
        return this.renderState.getPartialTick();
    }
	/// Gets the amount of time (in ticks) that have passed in either the current transition or
	/// animation, depending on the controller's AnimationState.
	///
	/// Note that this does necessarily match the keyframe marker's time, as the controller may have passed the exact marker time
	public double getAnimationTick() {
		return this.controller.getCurrentAnimationTime();
	}
}
