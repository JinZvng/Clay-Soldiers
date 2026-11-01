package com.matthewperiut.clay.entity.soldier.variant;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;

public class LimeSoldierDoll extends SoldierDollEntity
{
    public static final Identifier TEXTURE_ID = Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/soldier/lime.png");

    public LimeSoldierDoll(EntityType<? extends PathfinderMob> type, Level worldIn)
    {
        super(type, worldIn);
    }
}
