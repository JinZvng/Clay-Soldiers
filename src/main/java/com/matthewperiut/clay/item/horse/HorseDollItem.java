package com.matthewperiut.clay.item.horse;
import net.minecraft.world.entity.EntityType;

import com.matthewperiut.clay.item.common.SpawnDollItem;

import java.util.ArrayList;
import java.util.function.Supplier;

public class HorseDollItem extends SpawnDollItem
{
    public HorseDollItem(ArrayList<Supplier<EntityType<?>>> types, Properties settings)
    {
        super(types, settings);
    }

    public HorseDollItem(Supplier<EntityType<?>> defaultType, Properties settings)
    {
        super(defaultType, settings);
    }
}
