package com.matthewperiut.clay.upgrade.hand;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.upgrade.ISoldierUpgrade;
import com.matthewperiut.clay.upgrade.behavior.IDurable;

import java.util.Objects;
import java.util.UUID;

public class SoldierStickUpgrade implements ISoldierUpgrade, IDurable {

    public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "upgrades/soldier/stick_upgrade");
    private static final short durability = 20;
    protected static final Identifier SOLDIER_STICK_UPGRADE = Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "soldier_stick_upgrade");


    @Override
    public ItemStack getUpgradeItem() {
        return new ItemStack(Items.STICK, 1);
    }

    @Override
    public Identifier getUpgradeIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean needsCustomData() {
        return true;
    }

    @Override
    public boolean canUpgrade(ItemStack itemStack, SoldierDollEntity soldier) {
        return !soldier.hasToy("bow") && !soldier.upgrades.contains(this) && itemStack.is(Items.STICK);
    }

    @Override
    public void onAdd(SoldierDollEntity soldier) {
        soldier.upgradeInstances.get(this).nbtCompound().putShort(IDurable.NBT_KEY, durability);
        soldier.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STICK, 1));
        AttributeInstance attackInstance = soldier.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeModifier attributeModifier = new AttributeModifier(SOLDIER_STICK_UPGRADE, 2, AttributeModifier.Operation.ADD_VALUE);
        if (attackInstance != null && !attackInstance.hasModifier(attributeModifier.id()))
            attackInstance.addPermanentModifier(attributeModifier);
    }

    @Override
    public void onRemove(SoldierDollEntity soldier) {
        soldier.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        AttributeInstance attackInstance = soldier.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackInstance != null)
            attackInstance.removeModifier(SOLDIER_STICK_UPGRADE);
        if (getUses(soldier) <= 0)
            soldier.playSound(SoundEvents.ITEM_BREAK.value());
    }

    @Override
    public void onAttack(SoldierDollEntity target, SoldierDollEntity attacker) {
        reduceNBTDurability(attacker.upgradeInstances.get(this).nbtCompound(), attacker);
        if (getUses(attacker) <= 0) {
            attacker.removeUpgrades.add(this);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(IDENTIFIER.toString());
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
