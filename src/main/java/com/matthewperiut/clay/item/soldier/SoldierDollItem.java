package com.matthewperiut.clay.item.soldier;
import net.minecraft.world.entity.EntityType;

import com.matthewperiut.clay.item.common.SpawnDollItem;

import java.util.function.Supplier;

public class SoldierDollItem extends SpawnDollItem
{
    public SoldierDollItem(Supplier<EntityType<?>> defaultType, Properties settings)
    {
        super(defaultType, settings);
    }
}
