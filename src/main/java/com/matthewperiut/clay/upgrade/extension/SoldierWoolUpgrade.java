package com.matthewperiut.clay.upgrade.extension;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.upgrade.ISoldierUpgrade;

import java.util.UUID;

import static com.matthewperiut.clay.registry.UpgradeRegistry.LEATHER_UPGRADE;

public class SoldierWoolUpgrade implements ISoldierUpgrade {
    public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "upgrades/soldier/wool_upgrade");
    protected static final Identifier SOLDIER_WOOL_UPGRADE = Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "soldier_wool_upgrade");


    @Override
    public ItemStack getUpgradeItem() {
        return new ItemStack(Items.WHITE_WOOL, 1);
    }

    @Override
    public Identifier getUpgradeIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean canUpgrade(ItemStack itemStack, SoldierDollEntity soldier) {
        return itemStack.is(ItemTags.WOOL) && soldier.upgrades.contains(LEATHER_UPGRADE.get()) && !soldier.upgrades.contains(this);
    }

    @Override
    public ISoldierUpgrade dependentsOn() {
        return LEATHER_UPGRADE.get();
    }

    @Override
    public boolean shouldSyncToClient() {
        return true;
    }

    @Override
    public void onAdd(SoldierDollEntity soldier) {
        if (soldier.level().isClientSide()) return;

        soldier.playSound(SoundEvents.WOOL_PLACE);

        AttributeInstance armorInstance = soldier.getAttribute(Attributes.ARMOR);
        AttributeModifier attributeModifier = new AttributeModifier(SOLDIER_WOOL_UPGRADE, 2, AttributeModifier.Operation.ADD_VALUE);
        if (armorInstance != null && !armorInstance.hasModifier(attributeModifier.id())) {
            armorInstance.addPermanentModifier(attributeModifier);
        }
    }

    @Override
    public void onRemove(SoldierDollEntity soldier) {
        if (soldier.level().isClientSide()) return;

        AttributeInstance armorInstance = soldier.getAttribute(Attributes.ARMOR);
        if (armorInstance != null)
            armorInstance.removeModifier(SOLDIER_WOOL_UPGRADE);
    }
}
