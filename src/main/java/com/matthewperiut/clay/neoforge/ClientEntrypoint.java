package com.matthewperiut.clay.neoforge;
import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.registry.RenderRegistry;
import com.matthewperiut.clay.network.payload.SyncUpgradesPayload;
import com.matthewperiut.clay.network.client.ClientNetworkHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
public final class ClientEntrypoint {
    public static void init(IEventBus bus) {
        ClayMod.initClient();
        bus.addListener(RenderRegistry::register);
        bus.addListener((RegisterClientPayloadHandlersEvent event) -> event.register(SyncUpgradesPayload.ID, (payload, context) -> ClientNetworkHandler.handle(payload, context.player())));
    }
}
