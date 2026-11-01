package com.matthewperiut.clay.upgrade.misc;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.upgrade.ISoldierUpgrade;

import java.util.Objects;

public class SoldierGlowstoneUpgrade implements ISoldierUpgrade {
    public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "upgrades/soldier/glowstone_upgrade");

    @Override
    public ItemStack getUpgradeItem() {
        return new ItemStack(Items.GLOWSTONE_DUST, 1);
    }

    @Override
    public Identifier getUpgradeIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean shouldSyncToClient() {
        return true;
    }

    @Override
    public boolean canUpgrade(ItemStack itemStack, SoldierDollEntity soldier) {
        return !soldier.upgrades.contains(this) && itemStack.isOf(Items.GLOWSTONE_DUST);
    }

    @Override
    public void pushNewPack(SoldierDollEntity soldier) {
        if (soldier.level().isClientSide())
            soldier.setLightBlockUnaffected(true);
    }

    @Override
    public void onRemove(SoldierDollEntity soldier) {
        if (soldier.level().isClientSide())
            soldier.setLightBlockUnaffected(false);
    }

    @Override
    public void onLoad(SoldierDollEntity soldier) {
        pushNewPack(soldier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(IDENTIFIER.toString());
    }
}
