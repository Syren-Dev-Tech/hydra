package com.github.syren_dev_tech.hydra.animatable.stateless;

import net.minecraft.world.entity.Entity;
import com.github.syren_dev_tech.hydra.animatable.SingletonHydraAnimatable;
import com.github.syren_dev_tech.hydra.animation.RawAnimation;
import com.github.syren_dev_tech.hydra.util.HydraUtil;

/**
 * Extension of {@link StatelessAnimatable} for {@link SingletonHydraAnimatable} animatables.
 * <p>
 * Animatables <b><u>MUST</u></b> be registered with {@link SingletonHydraAnimatable#registerSyncedAnimatable} to use this interface
 */
public non-sealed interface StatelessHydraSingletonAnimatable extends StatelessAnimatable, SingletonHydraAnimatable {
    /**
     * Start or continue an animation, letting its pre-defined loop type determine whether it should loop or not
     */
    default void playAnimation(String animation, Entity relatedEntity, long instanceId) {
        playAnimation(RawAnimation.begin().thenPlay(animation), relatedEntity, instanceId);
    }

    /**
     * Start or continue an animation, forcing it to loop continuously until stopped
     */
    default void playLoopingAnimation(String animation, Entity relatedEntity, long instanceId) {
        playAnimation(RawAnimation.begin().thenLoop(animation), relatedEntity, instanceId);
    }

    /**
     * Start or continue an animation, then hold the pose at the end of the animation until otherwise stopped
     */
    default void playAndHoldAnimation(String animation, Entity relatedEntity, long instanceId) {
        playAnimation(RawAnimation.begin().thenPlayAndHold(animation), relatedEntity, instanceId);
    }

    /**
     * Stop an already-playing animation
     */
    default void stopAnimation(RawAnimation animation, Entity relatedEntity, long instanceId) {
        stopAnimation(animation.getStageCount() == 1 ? animation.getAnimationStages().getFirst().animationName() : animation.toString(), relatedEntity, instanceId);
    }

    /**
     * Start or continue a pre-defined animation
     */
    default void playAnimation(RawAnimation animation, Entity relatedEntity, long instanceId) {
        // Client-side only animation handling
        handleClientAnimationPlay(this, instanceId, animation);
    }

    /**
     * Stop an already-playing animation
     */
    default void stopAnimation(String animation, Entity relatedEntity, long instanceId) {
        // Client-side only animation handling
        handleClientAnimationStop(this, instanceId, animation);
    }

    // Unsupported method handlers below; do not use

    /**
     * @deprecated Use {@link #playAnimation(String, Entity, long)} instead.
     */
    @Deprecated
    default void playAnimation(String animation) {
        throw new IllegalStateException("Cannot use non-level method handlers on StatelessSingletonHydraAnimatable");
    }

    /**
     * @deprecated Use {@link #playLoopingAnimation(String, Entity, long)} instead.
     */
    @Deprecated
    default void playLoopingAnimation(String animation) {
        throw new IllegalStateException("Cannot use non-level method handlers on StatelessSingletonHydraAnimatable");
    }

    /**
     * @deprecated Use {@link #playAndHoldAnimation(String, Entity, long)} instead.
     */
    @Deprecated
    default void playAndHoldAnimation(String animation) {
        throw new IllegalStateException("Cannot use non-level method handlers on StatelessSingletonHydraAnimatable");
    }

    /**
     * @deprecated Use {@link #stopAnimation(RawAnimation, Entity, long)} instead.
     */
    @Deprecated
    default void stopAnimation(RawAnimation animation) {
        throw new IllegalStateException("Cannot use non-level method handlers on StatelessSingletonHydraAnimatable");
    }

    /**
     * @deprecated Use {@link #playAnimation(RawAnimation, Entity, long)} instead.
     */
    @Deprecated
    default void playAnimation(RawAnimation animation) {
        throw new IllegalStateException("Cannot use non-level method handlers on StatelessSingletonHydraAnimatable");
    }

    /**
     * @deprecated Use {@link #stopAnimation(String, Entity, long)} instead.
     */
    @Deprecated
    default void stopAnimation(String animation) {
        throw new IllegalStateException("Cannot use non-level method handlers on StatelessSingletonHydraAnimatable");
    }
}
