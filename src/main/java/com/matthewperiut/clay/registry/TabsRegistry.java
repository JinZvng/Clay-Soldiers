package com.matthewperiut.clay.registry;
import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.platform.DeferredRegister;
import com.matthewperiut.clay.platform.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
public final class TabsRegistry {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(ClayMod.MOD_ID, Registries.CREATIVE_MODE_TAB);
    public static final RegistrySupplier<CreativeModeTab> CLAY_GROUP = TABS.register("clay_group", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.clay.clay_group"))
        .icon(() -> new ItemStack(ItemRegistry.CLAY_SOLDIER_ITEM.get()))
        .displayItems((parameters, output) -> {
            ItemRegistry.ITEMS.forEach(item -> output.accept(item.get()));
            ItemRegistry.HORSE_ITEMS.forEach(item -> output.accept(item.get()));
        }).build());
    public static final RegistrySupplier<CreativeModeTab> CLAY_MISC_GROUP = TABS.register("clay_misc_group", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.clay.clay_misc_group"))
        .icon(() -> new ItemStack(ItemRegistry.CLAY_DISRUPTOR.get()))
        .displayItems((parameters, output) -> ItemRegistry.MISC_ITEMS.forEach(item -> output.accept(item.get()))).build());
    public static void init() { TABS.register(); }
}
