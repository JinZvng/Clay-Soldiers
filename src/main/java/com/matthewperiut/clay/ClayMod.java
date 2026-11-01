package com.matthewperiut.clay;


import com.matthewperiut.clay.event.LivingEntityEventManager;
import com.matthewperiut.clay.network.client.ClientNetworkHandler;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class ClayMod
{
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MOD_ID = "clay";

    public static void init()
    {
        ClayRegistries.init();
        LivingEntityEventManager.register();
    }

    public static void post() {
        ClayRegistries.post();
    }

    public static void initClient() {
        ClayRegistries.initClient();
    }
}
