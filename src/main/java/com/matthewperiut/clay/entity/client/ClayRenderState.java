package com.matthewperiut.clay.entity.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

/**
 * Per-frame Clay visual flags. GeckoLib's EntityRenderState mixin owns ticket
 * storage; overriding getDataMap alone splits reads from the mixin's writes.
 */
public final class ClayRenderState extends LivingEntityRenderState implements GeoRenderState {
    public boolean leather, wool;
}
