package com.matthewperiut.clay.neoforge;
import com.matthewperiut.clay.ClayMod;
import dev.architectury.platform.Platform;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
@Mod(ClayMod.MOD_ID)
public final class ClayModNeoForge {
    public ClayModNeoForge(IEventBus bus) {
        ClayMod.init();
        if (Platform.getEnv() == Dist.DEDICATED_SERVER) {
            dev.architectury.networking.NetworkManager.registerS2CPayloadType(com.matthewperiut.clay.network.payload.SyncUpgradesPayload.ID, com.matthewperiut.clay.network.payload.SyncUpgradesPayload.CODEC);
        }
        bus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(ClayMod::post));
        if (Platform.getEnv() == Dist.CLIENT) {
            ClayMod.initClient();
        }
    }
}
