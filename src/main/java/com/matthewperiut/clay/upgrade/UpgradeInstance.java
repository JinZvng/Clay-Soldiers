package com.matthewperiut.clay.upgrade;
import net.minecraft.nbt.CompoundTag;


/**
 * Allows Upgrades to save custom data
 */
public record UpgradeInstance(CompoundTag nbtCompound) {
}
