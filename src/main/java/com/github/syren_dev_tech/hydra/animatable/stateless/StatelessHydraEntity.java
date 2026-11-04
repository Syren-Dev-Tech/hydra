package com.github.syren_dev_tech.hydra.animatable.stateless;

import net.minecraft.world.entity.Entity;
import com.github.syren_dev_tech.hydra.animatable.HydraEntity;
import com.github.syren_dev_tech.hydra.animation.RawAnimation;

/**
 * Extension of {@link StatelessAnimatable} for {@link HydraEntity} animatables
 */
public non-sealed interface StatelessHydraEntity extends StatelessAnimatable, HydraEntity {
    /**
     * Start or continue a pre-defined animation
     */
    @Override
    default void playAnimation(RawAnimation animation) {
        if (!(this instanceof Entity self))
            throw new ClassCastException("Cannot use StatelessHydraEntity on a non-entity animatable!");

        // Client-side only animation handling
        handleClientAnimationPlay(this, self.getId(), animation);
    }

    /**
     * Stop an already-playing animation
     */
    @Override
    default void stopAnimation(String animation) {
        if (!(this instanceof Entity self))
            throw new ClassCastException("Cannot use StatelessHydraEntity on a non-entity animatable!");

        // Client-side only animation handling
        handleClientAnimationStop(this, self.getId(), animation);
    }
}
