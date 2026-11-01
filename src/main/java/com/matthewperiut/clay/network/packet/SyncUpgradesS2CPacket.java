package com.matthewperiut.clay.network.packet;
import net.minecraft.network.FriendlyByteBuf;

import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.upgrade.ISoldierUpgrade;
import com.matthewperiut.clay.upgrade.UpgradeManager;

import java.util.LinkedList;
import java.util.List;

public class SyncUpgradesS2CPacket {
    private final List<ISoldierUpgrade> upgrades;

    public SyncUpgradesS2CPacket(SoldierDollEntity entity) {
        this.upgrades = entity.upgrades.stream().filter(ISoldierUpgrade::shouldSyncToClient).toList();
    }

    public SyncUpgradesS2CPacket(FriendlyByteBuf buf) {
        upgrades = new LinkedList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            upgrades.add(UpgradeManager.INSTANCE.getUpgrade(buf.readIdentifier()));
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(upgrades.size());
        for (ISoldierUpgrade upgrade : upgrades) {
            buf.writeIdentifier(upgrade.getUpgradeIdentifier());
        }
    }

    public List<ISoldierUpgrade> getUpgrades() {
        return upgrades;
    }
}
