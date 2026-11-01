package com.matthewperiut.clay.entity.client;
import net.minecraft.resources.Identifier;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.entity.horse.HorseDollEntity;
import com.geckolib.model.GeoModel;

public class HorseDollModel extends GeoModel<HorseDollEntity>
{
    Identifier texture_id;

    public HorseDollModel(Identifier texture_id)
    {
        this.texture_id = texture_id;
    }

    @Override
    public Identifier getModelResource(HorseDollEntity object)
    {
        return Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "geo/doll_horse.geo.json");
    }

    @Override
    public Identifier getTextureResource(HorseDollEntity object)
    {
        return texture_id;
    }

    @Override
    public Identifier getAnimationResource(HorseDollEntity animatable)
    {
        return Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "animations/doll_horse.animation.json");
    }
}
