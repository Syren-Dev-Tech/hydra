/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package com.github.syren_dev_tech.hydra.animation.keyframe.event;

import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.animation.AnimationController;
import com.github.syren_dev_tech.hydra.animation.keyframe.event.data.SoundKeyframeData;

/**
 * The {@link KeyFrameEvent} specific to the {@link AnimationController#soundKeyframeHandler}
 * <p>
 * Called when a sound instruction keyframe is encountered
 */
public class SoundKeyframeEvent<T extends HydraAnimatable> extends KeyFrameEvent<T, SoundKeyframeData> {
	public SoundKeyframeEvent(T entity, double animationTick, AnimationController<T> controller, SoundKeyframeData keyFrameData) {
		super(entity, animationTick, controller, keyFrameData);
	}

	@Override
	public SoundKeyframeData getKeyframeData() {
		return super.getKeyframeData();
	}
}
