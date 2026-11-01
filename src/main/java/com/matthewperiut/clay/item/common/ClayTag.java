package com.matthewperiut.clay.item.common;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import com.matthewperiut.clay.ClayMod;

public class ClayTag
{
    public static final TagKey<Item> SOLDIERS = TagKey.of(BuiltInRegistries.ITEM.getKey(), Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "soldiers"));
}
