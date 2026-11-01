package com.matthewperiut.clay.entity.client.layer;
import com.matthewperiut.clay.entity.client.ClayRenderState;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.builtin.TextureLayerGeoLayer;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
public final class SoldierDollLeatherArmorLayer extends TextureLayerGeoLayer<SoldierDollEntity, Void, ClayRenderState> {
    public SoldierDollLeatherArmorLayer(GeoRenderer<SoldierDollEntity, Void, ClayRenderState> renderer) {
        super(renderer, Identifier.fromNamespaceAndPath("clay", "textures/entity/upgrade/tiny_chestplate.png"));
    }
    @Override protected Identifier getTextureResource(ClayRenderState state) {
        return state.wool ? Identifier.fromNamespaceAndPath("clay", "textures/entity/upgrade/tiny_chestplate_with_wool.png") : super.getTextureResource(state);
    }
    @Override protected RenderType getRenderType(ClayRenderState state) {
        return state.leather ? RenderTypes.entityCutout(getTextureResource(state)) : null;
    }
}
