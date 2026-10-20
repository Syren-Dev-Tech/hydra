package com.github.syren_dev_tech.hydra.animatable.stateless;

import net.minecraft.world.entity.Entity;
import com.github.syren_dev_tech.hydra.HydraServices;
import com.github.syren_dev_tech.hydra.animatable.HydraEntity;
import com.github.syren_dev_tech.hydra.animation.RawAnimation;
import com.github.syren_dev_tech.hydra.network.packet.StatelessEntityPlayAnimPacket;
import com.github.syren_dev_tech.hydra.network.packet.StatelessEntityStopAnimPacket;

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

        if (self.level().isClientSide) {
            handleClientAnimationPlay(this, self.getId(), animation);
        }
        else {
            HydraServices.NETWORK.sendToAllPlayersTrackingEntity(new StatelessEntityPlayAnimPacket(self.getId(), false, animation), self);
        }
    }

    /**
     * Stop an already-playing animation
     */
    @Override
    default void stopAnimation(String animation) {
        if (!(this instanceof Entity self))
            throw new ClassCastException("Cannot use StatelessHydraEntity on a non-entity animatable!");

        if (self.level().isClientSide) {
            handleClientAnimationStop(this, self.getId(), animation);
        }
        else {
            HydraServices.NETWORK.sendToAllPlayersTrackingEntity(new StatelessEntityStopAnimPacket(self.getId(), false, animation), self);
        }
    }
}
