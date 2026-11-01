package com.matthewperiut.clay.entity.client;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.constant.dataticket.DataTicket;
import java.util.Map;
import java.util.HashMap;
public final class ClayRenderState extends LivingEntityRenderState implements GeoRenderState {
    private final Map<DataTicket<?>, Object> data = new HashMap<>();
    public boolean leather, wool;
    @Override public Map<DataTicket<?>, Object> getDataMap() { return data; }
}
