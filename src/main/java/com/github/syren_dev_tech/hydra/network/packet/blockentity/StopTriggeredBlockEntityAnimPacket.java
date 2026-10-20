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
import com.github.syren_dev_tech.hydra.network.packet.MultiloaderPacket;
import com.github.syren_dev_tech.hydra.util.ClientUtil;

import java.util.Optional;
import java.util.function.Consumer;

public record StopTriggeredBlockEntityAnimPacket(BlockPos pos, Optional<String> controllerName, Optional<String> animName) implements MultiloaderPacket {
    public static final Type<StopTriggeredBlockEntityAnimPacket> TYPE = new Type<>(HydraConstants.id("stop_triggered_blockentity_anim"));
    public static final StreamCodec<FriendlyByteBuf, StopTriggeredBlockEntityAnimPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, StopTriggeredBlockEntityAnimPacket::pos,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional), StopTriggeredBlockEntityAnimPacket::controllerName,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional), StopTriggeredBlockEntityAnimPacket::animName,
            StopTriggeredBlockEntityAnimPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            final Level level = ClientUtil.getLevel();

            if (level != null && level.getBlockEntity(this.pos) instanceof GeoBlockEntity blockEntity)
                blockEntity.stopTriggeredAnim(this.controllerName.orElse(null), this.animName.orElse(null));
        });
    }
}
