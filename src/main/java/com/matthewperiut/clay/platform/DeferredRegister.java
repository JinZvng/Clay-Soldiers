package com.matthewperiut.clay.platform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.Consumer;
public final class DeferredRegister<T> {
    private final net.neoforged.neoforge.registries.DeferredRegister<T> delegate;
    private final List<RegistrySupplier<T>> entries = new ArrayList<>();
    private static IEventBus bus;
    public static void attach(IEventBus eventBus) { bus = eventBus; }
    private DeferredRegister(String mod, ResourceKey<? extends Registry<T>> key) {
        delegate = net.neoforged.neoforge.registries.DeferredRegister.create(key, mod);
    }
    public static <T> DeferredRegister<T> create(String mod, ResourceKey<? extends Registry<T>> key) { return new DeferredRegister<>(mod, key); }
    public <I extends T> RegistrySupplier<I> register(String name, Supplier<? extends I> factory) {
        var holder = delegate.register(name, factory);
        var entry = new RegistrySupplier<I>(holder);
        entries.add(new RegistrySupplier<T>(holder));
        return entry;
    }
    public void register() {
        if (bus == null) throw new IllegalStateException("Clay mod event bus is not attached");
        delegate.register(bus);
    }
    public void forEach(Consumer<RegistrySupplier<T>> consumer) { entries.forEach(consumer); }
}
