package com.matthewperiut.clay.upgrade.behavior;
import net.minecraft.nbt.CompoundTag;

import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;

public interface IDurable {
    String NBT_KEY = "durable";

    short getUses(SoldierDollEntity soldier);

    default void writeNBTDurability(CompoundTag compound, SoldierDollEntity soldier) {
        compound.putShort(NBT_KEY, getUses(soldier));
    }

    default void reduceNBTDurability(CompoundTag compound, SoldierDollEntity soldier) {
        compound.putShort(NBT_KEY, (short) (getUses(soldier) - 1));
    }

}
