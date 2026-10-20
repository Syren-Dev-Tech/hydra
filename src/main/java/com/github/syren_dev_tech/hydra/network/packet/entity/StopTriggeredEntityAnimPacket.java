package com.github.syren_dev_tech.hydra.network.packet.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import com.github.syren_dev_tech.hydra.HydraConstants;
import com.github.syren_dev_tech.hydra.animatable.GeoEntity;
import com.github.syren_dev_tech.hydra.animatable.GeoReplacedEntity;
import com.github.syren_dev_tech.hydra.network.packet.MultiloaderPacket;
import com.github.syren_dev_tech.hydra.util.ClientUtil;
import com.github.syren_dev_tech.hydra.util.RenderUtil;

import java.util.Optional;
import java.util.function.Consumer;

public record StopTriggeredEntityAnimPacket(int entityId, boolean isReplacedEntity, Optional<String> controllerName, Optional<String> animName) implements MultiloaderPacket {
    public static final Type<StopTriggeredEntityAnimPacket> TYPE = new Type<>(HydraConstants.id("stop_triggered_entity_anim"));
    public static final StreamCodec<FriendlyByteBuf, StopTriggeredEntityAnimPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, StopTriggeredEntityAnimPacket::entityId,
            ByteBufCodecs.BOOL, StopTriggeredEntityAnimPacket::isReplacedEntity,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional), StopTriggeredEntityAnimPacket::controllerName,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional), StopTriggeredEntityAnimPacket::animName,
            StopTriggeredEntityAnimPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            final Level level = ClientUtil.getLevel();
            final Entity entity;

            if (level == null || (entity = level.getEntity(this.entityId)) == null)
                return;

            if (!this.isReplacedEntity) {
                if (entity instanceof GeoEntity geoEntity)
                    geoEntity.stopTriggeredAnim(this.controllerName.orElse(null), this.animName.orElse(null));

                return;
            }

            if (RenderUtil.getReplacedAnimatable(entity.getType()) instanceof GeoReplacedEntity replacedEntity)
                replacedEntity.stopTriggeredAnim(entity, this.controllerName.orElse(null), this.animName.orElse(null));
        });
    }
}
