package com.matthewperiut.clay.registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import com.matthewperiut.clay.ClayMod;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

import static net.minecraft.registry.RegistryKeys.ITEM_GROUP;

public class TabsRegistry {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(ClayMod.MOD_ID, CREATIVE_MODE_TAB);
    public static final RegistrySupplier<CreativeModeTab> CLAY_GROUP = TABS.register("clay_group", () -> CreativeTabRegistry.create(Component.translatable("itemGroup." + ClayMod.MOD_ID + ".clay_group"), () -> new ItemStack(ItemRegistry.CLAY_SOLDIER_ITEM.get())));
    public static final RegistrySupplier<CreativeModeTab> CLAY_MISC_GROUP = TABS.register("clay_misc_group", () -> CreativeTabRegistry.create(Component.translatable("itemGroup." + ClayMod.MOD_ID + ".clay_misc_group"), () -> new ItemStack(ItemRegistry.CLAY_SOLDIER_ITEM.get())));

    public static void init() {
        TABS.register();
    }

}
