package com.matthewperiut.clay.network.client;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.network.payload.SyncUpgradesPayload;
import com.matthewperiut.clay.upgrade.ISoldierUpgrade;
import net.minecraft.world.entity.player.Player;
public final class ClientNetworkHandler {
    public static void handle(SyncUpgradesPayload packet, Player player) {
        if (!(player.level().getEntity(packet.entityId()) instanceof SoldierDollEntity soldier)) return;
        if (packet.action() == SyncUpgradesPayload.Action.SET) {
            for (ISoldierUpgrade upgrade : soldier.upgrades) upgrade.onRemove(soldier);
            soldier.upgrades.clear();
        }
        for (ISoldierUpgrade upgrade : packet.upgrades()) {
            if (upgrade == null) continue;
            if (packet.action() == SyncUpgradesPayload.Action.REMOVE) {
                if (soldier.upgrades.remove(upgrade)) upgrade.onRemove(soldier);
            } else if (soldier.upgrades.add(upgrade)) {
                upgrade.onAdd(soldier);
            }
        }
    }
}
