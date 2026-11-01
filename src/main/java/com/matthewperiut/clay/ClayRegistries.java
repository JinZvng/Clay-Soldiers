package com.matthewperiut.clay;
import net.minecraft.resources.Identifier;

import com.matthewperiut.clay.registry.*;

public class ClayRegistries {

    public static void init() {
        EntityTypeRegistry.init();
        TabsRegistry.init();
        ItemRegistry.init();
        UpgradeRegistry.init();
        TeamRegistry.init();
    }

    public static void post() {
        ItemRegistry.post();
    }

    public static void initClient() {
        EntityTypeRegistry.clientRegister();
        ItemRegistry.clientRegister();
    }


    public static Identifier getIdentifier(String name) {
        return Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, name);
    }

}
