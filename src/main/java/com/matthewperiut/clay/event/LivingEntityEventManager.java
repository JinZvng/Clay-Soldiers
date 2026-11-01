package com.matthewperiut.clay.event;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.upgrade.ISoldierUpgrade;
import com.matthewperiut.clay.upgrade.UpgradeManager;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
public final class LivingEntityEventManager {
    public static void register() {
        NeoForge.EVENT_BUS.addListener((LivingDeathEvent event) -> {
            if (event.getEntity() instanceof SoldierDollEntity dying && event.getSource().getEntity() instanceof SoldierDollEntity attacker) {
                for (ISoldierUpgrade upgrade : attacker.upgrades) upgrade.onKill(dying, attacker);
                ISoldierUpgrade expired;
                while ((expired = attacker.removeUpgrades.poll()) != null) UpgradeManager.INSTANCE.removeUpgrade(attacker, expired);
            }
        });
    }
}
