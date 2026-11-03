package com.matthewperiut.clay.network;
import com.matthewperiut.clay.network.payload.SyncUpgradesPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public final class ClayNetworking {
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(com.matthewperiut.clay.network.payload.WhistlePayload.ID, com.matthewperiut.clay.network.payload.WhistlePayload.CODEC, (payload, context) -> {
            var player=context.player();
            if(!player.isSpectator())com.matthewperiut.clay.item.CommandWhistleItem.issueSelected(player.level(),player,payload.offhand()?net.minecraft.world.InteractionHand.OFF_HAND:net.minecraft.world.InteractionHand.MAIN_HAND,payload.mode(),payload.team());
        });
        event.registrar("1").playToClient(SyncUpgradesPayload.ID, SyncUpgradesPayload.CODEC);
    }
    public static void sendToPlayers(Iterable<ServerPlayer> players, CustomPacketPayload payload) {
        for (ServerPlayer player : players) PacketDistributor.sendToPlayer(player, payload);
    }
}
