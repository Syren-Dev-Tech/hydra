package com.github.syren_dev_tech.hydra.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import com.github.syren_dev_tech.hydra.HydraConstants;
import com.github.syren_dev_tech.hydra.network.packet.MultiloaderPacket;
import com.github.syren_dev_tech.hydra.service.HydraNetworking;

/// NeoForge service implementation for Hydra's networking functionalities
public class HydraNetworkingNeoForge implements HydraNetworking {
    private static PayloadRegistrar registrar = null;

    public static void init(IEventBus modBus) {
        modBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
            registrar = event.registrar(HydraConstants.MODID).optional();
            HydraNetworking.init();
            registrar = null;
        });
    }

    /// Register a Hydra packet for use
    ///
    /// **<u>FOR HYDRA USE ONLY</u>**
    @SuppressWarnings("unchecked")
    @Override
    public <B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerPacketInternal(CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, boolean isClientBound) {
        if (isClientBound) {
            registrar.playToClient(payloadType, (StreamCodec<FriendlyByteBuf, P>)codec, (packet, context) -> packet.receiveMessage(context.player(), context::enqueueWork));
        }
        else {
            registrar.playToServer(payloadType, (StreamCodec<FriendlyByteBuf, P>)codec, (packet, context) -> packet.receiveMessage(context.player(), context::enqueueWork));
        }
    }

    /// Send a packet to all players currently tracking a given entity
    ///
    /// Good as a shortcut for sending a packet to all players that may have an interest in a given entity or its dealings
    ///
    /// Will also send the packet to the entity itself if the entity is also a player
    @Override
    public void sendToAllPlayersTrackingEntity(MultiloaderPacket packet, Entity trackingEntity) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(trackingEntity, packet);
    }

    /// Send a packet to all players tracking a given block position
    @Override
    public void sendToAllPlayersTrackingBlock(MultiloaderPacket packet, ServerLevel level, BlockPos pos) {
        PacketDistributor.sendToPlayersTrackingChunk(level, ChunkPos.containing(pos), packet);
    }

    /// Send a packet to the given player
    @Override
    public void sendToPlayer(MultiloaderPacket packet, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, packet);
    }
}
