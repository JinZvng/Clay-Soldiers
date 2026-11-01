package com.matthewperiut.clay.entity.client;
import net.minecraft.resources.Identifier;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.geckolib.model.GeoModel;

public class SoldierDollModel extends GeoModel<SoldierDollEntity>
{
    Identifier texture_id;

    public SoldierDollModel(Identifier texture_id)
    {
        this.texture_id = texture_id;
    }

    @Override
    public Identifier getModelResource(SoldierDollEntity object)
    {
        return Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "geo/clay_soldier.geo.json");
    }

    @Override
    public Identifier getTextureResource(SoldierDollEntity object)
    {
        return texture_id;
    }

    @Override
    public Identifier getAnimationResource(SoldierDollEntity animatable)
    {
        return Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "animations/clay_soldier.animation.json");
    }
}
