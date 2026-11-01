package com.matthewperiut.clay.entity.client;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

import com.matthewperiut.clay.entity.horse.HorseDollEntity;
import com.geckolib.renderer.GeoEntityRenderer;

public class HorseDollRenderer extends GeoEntityRenderer<HorseDollEntity>
{
    public Identifier texture_id;

    public HorseDollRenderer(EntityRendererProvider.Context blockRenderDispatcher, Identifier texture_id)
    {
        super(blockRenderDispatcher, new HorseDollModel(texture_id));
        this.texture_id = texture_id;
        this.shadowRadius = 0.1f;
    }
}
