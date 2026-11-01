package com.matthewperiut.clay.upgrade.extension;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.upgrade.ISoldierUpgrade;

import java.util.UUID;

import static com.matthewperiut.clay.registry.UpgradeRegistry.STICK_UPGRADE;

public class SoldierFlintUpgrade implements ISoldierUpgrade {
    public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "upgrades/soldier/flint_upgrade");
    protected static final Identifier SOLDIER_FLINT_UPGRADE = Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "soldier_flint_upgrade");

    @Override
    public ItemStack getUpgradeItem() {
        return new ItemStack(Items.FLINT, 1);
    }

    @Override
    public Identifier getUpgradeIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean canUpgrade(ItemStack itemStack, SoldierDollEntity soldier) {
        return !soldier.upgrades.contains(this) && itemStack.isOf(Items.FLINT) && soldier.upgrades.contains(STICK_UPGRADE.get());
    }

    @Override
    public void pushNewPack(SoldierDollEntity soldier) {
        soldier.playSound(SoundEvents.ANVIL_USE);
        AttributeInstance attackInstance = soldier.getAttributeInstance(Attributes.ATTACK_DAMAGE);
        AttributeModifier attributeModifier = new AttributeModifier(SOLDIER_FLINT_UPGRADE, 1, AttributeModifier.Operation.ADD_VALUE);
        if (attackInstance != null && !attackInstance.hasModifier(attributeModifier.id()))
            attackInstance.addPermanentModifier(attributeModifier);
    }

    @Override
    public void onRemove(SoldierDollEntity soldier) {
        AttributeInstance attackInstance = soldier.getAttributeInstance(Attributes.ATTACK_DAMAGE);
        if (attackInstance != null)
            attackInstance.removeModifier(SOLDIER_FLINT_UPGRADE);
    }

    @Override
    public ISoldierUpgrade dependentsOn() {
        return STICK_UPGRADE.get();
    }
}
