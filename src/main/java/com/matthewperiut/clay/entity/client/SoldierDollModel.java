package com.matthewperiut.clay.entity.client;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
public final class SoldierDollModel extends GeoModel<SoldierDollEntity> {
    private final Identifier texture;
    public SoldierDollModel(Identifier texture) { this.texture = texture; }
    @Override public Identifier getModelResource(GeoRenderState state) { return Identifier.fromNamespaceAndPath("clay", "clay_soldier"); }
    @Override public Identifier getTextureResource(GeoRenderState state) { return texture; }
    @Override public Identifier getAnimationResource(SoldierDollEntity entity) { return Identifier.fromNamespaceAndPath("clay", "clay_soldier"); }
}
