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
        if (Boolean.getBoolean("clay.checkRenderStateStorage")) {
            bus.addListener((net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) -> event.enqueueWork(ClientEntrypoint::checkRenderStateStorage));
        }
        bus.addListener(RenderRegistry::register);
        bus.addListener((RegisterClientPayloadHandlersEvent event) -> event.register(SyncUpgradesPayload.ID, (payload, context) -> ClientNetworkHandler.handle(payload, context.player())));
    }

    // Integration check runs only in the configured development client, after
    // GeckoLib has transformed EntityRenderState. It exercises its real mixin.
    private static void checkRenderStateStorage() {
        var state = new com.matthewperiut.clay.entity.client.ClayRenderState();
        var ticket = com.geckolib.constant.dataticket.DataTicket.create("clay_storage_probe", String.class);
        state.addGeckolibData(ticket, "present");
        if (!state.hasGeckolibData(ticket) || !"present".equals(state.getGeckolibData(ticket))) {
            throw new IllegalStateException("Clay render-state ticket writes and reads use different storage");
        }
        ClayMod.LOGGER.info("CLAY_RENDER_STATE_STORAGE_OK");
    }
}
