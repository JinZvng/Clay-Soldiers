package com.matthewperiut.clay.upgrade.misc;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.upgrade.ISoldierUpgrade;

public class SoldierGoldIngotUpgrade implements ISoldierUpgrade {
    public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "upgrades/soldier/gold_ingot_upgrade");

    @Override
    public ItemStack getUpgradeItem() {
        return new ItemStack(Items.GOLD_INGOT, 1);
    }

    @Override
    public Identifier getUpgradeIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean canUpgrade(ItemStack itemStack, SoldierDollEntity soldier) {
        return itemStack.is(Items.GOLD_INGOT) && !soldier.upgrades.contains(this);
    }

    @Override
    public void onAdd(SoldierDollEntity soldier) {

    }

    @Override
    public void onRemove(SoldierDollEntity soldier) {

    }

    @Override
    public void die(DamageSource source, SoldierDollEntity soldier) {
        if (source.getEntity() instanceof SoldierDollEntity) {
            // REVENGE THE KING
        }
    }
}
