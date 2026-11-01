package com.matthewperiut.clay.network;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.network.payload.SyncUpgradesPayload;
import com.matthewperiut.clay.upgrade.ISoldierUpgrade;
import com.matthewperiut.clay.util.NetworkUtils;

import java.util.List;


public class UpgradeAdded {

    public static void sendPacket(Iterable<ServerPlayer> players, int entityId, ISoldierUpgrade upgrade) {
        com.matthewperiut.clay.network.ClayNetworking.sendToPlayers(players, new SyncUpgradesPayload(entityId, SyncUpgradesPayload.Action.ADD, List.of(upgrade)));
    }
}
