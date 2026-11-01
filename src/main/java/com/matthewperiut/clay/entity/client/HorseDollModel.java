package com.matthewperiut.clay.entity.client;
import com.matthewperiut.clay.entity.horse.HorseDollEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
public final class HorseDollModel extends GeoModel<HorseDollEntity> {
    private final Identifier texture;
    public HorseDollModel(Identifier texture) { this.texture = texture; }
    @Override public Identifier getModelResource(GeoRenderState state) { return Identifier.fromNamespaceAndPath("clay", "doll_horse"); }
    @Override public Identifier getTextureResource(GeoRenderState state) { return texture; }
    @Override public Identifier getAnimationResource(HorseDollEntity entity) { return Identifier.fromNamespaceAndPath("clay", "doll_horse"); }
}
