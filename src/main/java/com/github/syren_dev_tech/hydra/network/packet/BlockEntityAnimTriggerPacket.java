package com.github.syren_dev_tech.hydra.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.HydraConstants;
import com.github.syren_dev_tech.hydra.animatable.HydraBlockEntity;
import com.github.syren_dev_tech.hydra.util.ClientUtil;

import java.util.function.Consumer;

public record BlockEntityAnimTriggerPacket(BlockPos pos, String controllerName, String animName) implements MultiloaderPacket {
    public static final CustomPacketPayload.Type<BlockEntityAnimTriggerPacket> TYPE = new Type<>(HydraConstants.id("blockentity_anim_trigger"));
    public static final StreamCodec<FriendlyByteBuf, BlockEntityAnimTriggerPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BlockEntityAnimTriggerPacket::pos,
            ByteBufCodecs.STRING_UTF8, BlockEntityAnimTriggerPacket::controllerName,
            ByteBufCodecs.STRING_UTF8, BlockEntityAnimTriggerPacket::animName,
            BlockEntityAnimTriggerPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            if (ClientUtil.getLevel().getBlockEntity(this.pos) instanceof HydraBlockEntity blockEntity)
                blockEntity.triggerAnim(this.controllerName.isEmpty() ? null : this.controllerName, this.animName);
        });
    }
}
