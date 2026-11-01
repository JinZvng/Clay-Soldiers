package com.matthewperiut.clay.network.client;
import net.minecraft.world.entity.Entity;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.network.payload.SyncUpgradesPayload;
import com.matthewperiut.clay.upgrade.ISoldierUpgrade;
import dev.architectury.networking.NetworkManager;

import java.util.List;
import java.util.Optional;

public class ClientNetworkHandler {
    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SyncUpgradesPayload.ID, SyncUpgradesPayload.CODEC, (buf, context) -> {
            if (buf.action() == SyncUpgradesPayload.Action.ADD) {
                List<ISoldierUpgrade> upgrades = buf.upgrades();
                for (int i = 0; i < upgrades.size(); i++) {
                    Optional<SoldierUpgradeContainer> optionalSoldier = handleIncomingUpgradePacket(buf.entityId(), upgrades.get(i), context);
                    if (optionalSoldier.isEmpty())
                        return;
                    SoldierUpgradeContainer container = optionalSoldier.get();
                    container.soldier.upgrades.add(container.upgrade);
                    container.upgrade.onAdd(container.soldier);
                }
            } else if (buf.action() == SyncUpgradesPayload.Action.REMOVE) {
                List<ISoldierUpgrade> upgrades = buf.upgrades();
                for (int i = 0; i < upgrades.size(); i++) {
                    Optional<SoldierUpgradeContainer> optionalSoldier = handleIncomingUpgradePacket(buf.entityId(), upgrades.get(i), context);
                    if (optionalSoldier.isEmpty())
                        return;
                    SoldierUpgradeContainer container = optionalSoldier.get();
                    container.soldier.upgrades.remove(container.upgrade);
                    container.upgrade.onRemove(container.soldier);
                }
            }
        });
    }

    private static Optional<SoldierUpgradeContainer> handleIncomingUpgradePacket(int soldierEntityId, ISoldierUpgrade upgrade, NetworkManager.PacketContext context) {
        if (upgrade == null) {
            ClayMod.LOGGER.warn("Unknown upgrade id");
            return Optional.empty();
        }
        Entity entity = context.getPlayer().level().getEntity(soldierEntityId);
        if (entity instanceof SoldierDollEntity soldier) {
            return Optional.of(new SoldierUpgradeContainer(upgrade, soldier));
        }
        return Optional.empty();
    }


    static class SoldierUpgradeContainer {
        public SoldierDollEntity soldier;
        public ISoldierUpgrade upgrade;

        SoldierUpgradeContainer(ISoldierUpgrade _upgrade, SoldierDollEntity _soldier) {
            upgrade = _upgrade;
            soldier = _soldier;
        }
    }
}
