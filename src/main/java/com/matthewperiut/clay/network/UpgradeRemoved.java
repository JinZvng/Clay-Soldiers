package com.matthewperiut.clay.network;
import net.minecraft.server.level.ServerPlayer;

import com.matthewperiut.clay.network.payload.SyncUpgradesPayload;
import com.matthewperiut.clay.upgrade.ISoldierUpgrade;
import dev.architectury.networking.NetworkManager;

import java.util.List;

public class UpgradeRemoved {
    public static void sendPacket(Iterable<ServerPlayer> players, int entityId, ISoldierUpgrade upgrade) {
        NetworkManager.sendToPlayers(players, new SyncUpgradesPayload(entityId, SyncUpgradesPayload.Action.REMOVE, List.of(upgrade)));
    }
}
