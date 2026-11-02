package com.matthewperiut.clay.upgrade.armor;
import net.minecraft.nbt.CompoundTag;
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
import com.matthewperiut.clay.upgrade.behavior.IDurable;

import java.util.UUID;


public class SoldierLeatherUpgrade implements ISoldierUpgrade, IDurable {
    public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "upgrades/soldier/leather_upgrade");
    private static final short durability = 20;

    private static final Identifier LEATHER_UPGRADE = Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "soldier_leather_upgrade");

    @Override
    public ItemStack getUpgradeItem() {
        return new ItemStack(Items.LEATHER, 1);
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
    public boolean needsCustomData() {
        return true;
    }

    @Override
    public boolean canUpgrade(ItemStack itemStack, SoldierDollEntity soldier) {
        return !soldier.hasToy("copper") && itemStack.is(Items.LEATHER) && !soldier.upgrades.contains(this);
    }

    @Override
    public void onAdd(SoldierDollEntity soldier) {
        if (soldier.level().isClientSide()) return;

        soldier.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value());

        soldier.upgradeInstances.get(this).nbtCompound().putShort(IDurable.NBT_KEY, durability);

        AttributeInstance armorInstance = soldier.getAttribute(Attributes.ARMOR);
        AttributeModifier attributeModifier = new AttributeModifier(LEATHER_UPGRADE, 3, AttributeModifier.Operation.ADD_VALUE);
        if (armorInstance != null && !armorInstance.hasModifier(attributeModifier.id())) {
            armorInstance.addPermanentModifier(attributeModifier);
        }
    }

    @Override
    public void onRemove(SoldierDollEntity soldier) {
        if (soldier.level().isClientSide()) return;

        soldier.playSound(SoundEvents.ITEM_BREAK.value());

        AttributeInstance armorInstance = soldier.getAttribute(Attributes.ARMOR);
        if (armorInstance != null) {
            armorInstance.removeModifier(LEATHER_UPGRADE);
        }
    }

    @Override
    public void doPostHurtEffects(SoldierDollEntity attacker, SoldierDollEntity soldier) {
        reduceNBTDurability(soldier.upgradeInstances.get(this).nbtCompound(), soldier);
        if (getUses(soldier) <= 0) {
            soldier.removeUpgrades.add(this);
        }
    }

    @Override
    public void writeCustomNBTData(SoldierDollEntity soldier, CompoundTag nbt) {
        writeNBTDurability(nbt, soldier);
    }

    @Override
    public void readCustomNBTData(SoldierDollEntity soldier, CompoundTag nbt) {
        soldier.upgradeInstances.get(this).nbtCompound().merge(nbt);
    }

    @Override
    public short getUses(SoldierDollEntity soldier) {
        return soldier.upgradeInstances.get(this).nbtCompound().getShort(IDurable.NBT_KEY).orElse((short) 0);
    }
}
