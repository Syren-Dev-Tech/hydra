package com.github.syren_dev_tech.hydra.animatable.stateless;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.github.syren_dev_tech.hydra.HydraServices;
import com.github.syren_dev_tech.hydra.animatable.HydraBlockEntity;
import com.github.syren_dev_tech.hydra.animation.RawAnimation;
import com.github.syren_dev_tech.hydra.network.packet.StatelessBlockEntityPlayAnimPacket;
import com.github.syren_dev_tech.hydra.network.packet.StatelessBlockEntityStopAnimPacket;

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

        if (self.getLevel() instanceof ServerLevel level) {
            HydraServices.NETWORK.sendToAllPlayersTrackingBlock(new StatelessBlockEntityPlayAnimPacket(self.getBlockPos(), animation), level, self.getBlockPos());
        }
        else {
            handleClientAnimationPlay(this, 0, animation);
        }
    }

    /**
     * Stop an already-playing animation
     */
    @Override
    default void stopAnimation(String animation) {
        if (!(this instanceof BlockEntity self))
            throw new ClassCastException("Cannot use StatelessHydraBlockEntity on a non-blockentity animatable!");

        if (self.getLevel() instanceof ServerLevel level) {
            HydraServices.NETWORK.sendToAllPlayersTrackingBlock(new StatelessBlockEntityStopAnimPacket(self.getBlockPos(), animation), level, self.getBlockPos());
        }
        else {
            handleClientAnimationStop(this, 0, animation);
        }
    }
}