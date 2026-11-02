package com.matthewperiut.clay.neoforge;
import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.registry.AttributeRegistry;
import com.matthewperiut.clay.network.ClayNetworking;
import com.matthewperiut.clay.platform.DeferredRegister;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
@Mod(ClayMod.MOD_ID)
public final class ClayModNeoForge {
    public ClayModNeoForge(IEventBus bus) {
        DeferredRegister.attach(bus);
        ClayMod.init();
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(com.matthewperiut.clay.entity.AllayPilot::tick);
        bus.addListener(AttributeRegistry::register);
        bus.addListener(ClayNetworking::register);
        bus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(ClayMod::post));
        if (FMLEnvironment.getDist() == Dist.CLIENT) ClientEntrypoint.init(bus);
    }
}
