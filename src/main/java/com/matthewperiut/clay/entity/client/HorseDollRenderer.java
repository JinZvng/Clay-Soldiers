package com.matthewperiut.clay.entity.client;
import com.matthewperiut.clay.entity.horse.HorseDollEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import com.geckolib.renderer.GeoEntityRenderer;
public final class HorseDollRenderer extends GeoEntityRenderer<HorseDollEntity, ClayRenderState> {
    public HorseDollRenderer(EntityRendererProvider.Context context, Identifier texture) {
        super(context, new HorseDollModel(texture));
        this.shadowRadius = 0.1f;
    }
    @Override public ClayRenderState createRenderState(HorseDollEntity entity, Void ignored) { return new ClayRenderState(); }
}
