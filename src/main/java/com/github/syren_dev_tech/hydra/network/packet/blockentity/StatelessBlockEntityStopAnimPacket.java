package com.github.syren_dev_tech.hydra.network.packet.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import com.github.syren_dev_tech.hydra.HydraConstants;
import com.github.syren_dev_tech.hydra.animatable.GeoBlockEntity;
import com.github.syren_dev_tech.hydra.animatable.stateless.StatelessGeoBlockEntity;
import com.github.syren_dev_tech.hydra.network.packet.MultiloaderPacket;
import com.github.syren_dev_tech.hydra.util.ClientUtil;

import java.util.function.Consumer;

public record StatelessBlockEntityStopAnimPacket(BlockPos blockPos, String animation) implements MultiloaderPacket {
    public static final Type<StatelessBlockEntityStopAnimPacket> TYPE = new Type<>(HydraConstants.id("stateless_block_entity_stop_anim"));
    public static final StreamCodec<FriendlyByteBuf, StatelessBlockEntityStopAnimPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, StatelessBlockEntityStopAnimPacket::blockPos,
            ByteBufCodecs.STRING_UTF8, StatelessBlockEntityStopAnimPacket::animation,
            StatelessBlockEntityStopAnimPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            final Level level = ClientUtil.getLevel();

            if (level != null && level.getBlockEntity(this.blockPos) instanceof GeoBlockEntity blockEntity && blockEntity instanceof StatelessGeoBlockEntity statelessAnimatable)
                statelessAnimatable.handleClientAnimationStop(blockEntity, 0, this.animation);
        });
    }
}
