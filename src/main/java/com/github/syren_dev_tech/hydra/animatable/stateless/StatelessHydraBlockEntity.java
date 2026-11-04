package com.github.syren_dev_tech.hydra.animatable.stateless;

import net.minecraft.world.level.block.entity.BlockEntity;
import com.github.syren_dev_tech.hydra.animatable.HydraBlockEntity;
import com.github.syren_dev_tech.hydra.animation.RawAnimation;

/**
 * Extension of {@link StatelessAnimatable} for {@link HydraBlockEntity} animatables
 */
public non-sealed interface StatelessHydraBlockEntity extends StatelessAnimatable, HydraBlockEntity {
    /**
     * Start or continue a pre-defined animation
     */
    @Override
    default void playAnimation(RawAnimation animation) {
        if (!(this instanceof BlockEntity self))
            throw new ClassCastException("Cannot use StatelessHydraBlockEntity on a non-blockentity animatable!");

        // Client-side only animation handling
        handleClientAnimationPlay(this, 0, animation);
    }

    /**
     * Stop an already-playing animation
     */
    @Override
    default void stopAnimation(String animation) {
        if (!(this instanceof BlockEntity self))
            throw new ClassCastException("Cannot use StatelessHydraBlockEntity on a non-blockentity animatable!");

        // Client-side only animation handling
        handleClientAnimationStop(this, 0, animation);
    }
}
