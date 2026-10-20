package com.github.syren_dev_tech.hydra.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.HydraConstants;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.animatable.stateless.StatelessHydraSingletonAnimatable;
import com.github.syren_dev_tech.hydra.util.HydraUtil;

import java.util.function.Consumer;

public record StatelessSingletonStopAnimPacket(String syncableId, long instanceId, String animation) implements MultiloaderPacket {
    public static final Type<StatelessSingletonStopAnimPacket> TYPE = new Type<>(HydraConstants.id("stateless_singleton_stop_anim"));
    public static final StreamCodec<FriendlyByteBuf, StatelessSingletonStopAnimPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, StatelessSingletonStopAnimPacket::syncableId,
            ByteBufCodecs.VAR_LONG, StatelessSingletonStopAnimPacket::instanceId,
            ByteBufCodecs.STRING_UTF8, StatelessSingletonStopAnimPacket::animation,
            StatelessSingletonStopAnimPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            HydraAnimatable animatable = HydraUtil.getSyncedAnimatable(this.syncableId);

            if (animatable instanceof StatelessHydraSingletonAnimatable statelessAnimatable)
                statelessAnimatable.handleClientAnimationStop(animatable, this.instanceId, this.animation);
        });
    }
}
