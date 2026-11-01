package com.matthewperiut.clay.platform;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
/** Internal named definitions. Both logical sides construct the same stable IDs. */
public final class SmallRegistry<T> {
    private final ResourceKey<Registry<T>> key;
    private final Map<ResourceKey<T>, T> entries = new LinkedHashMap<>();
    public SmallRegistry(Identifier id) { key = ResourceKey.createRegistryKey(id); }
    public RegistrySupplier<T> register(Identifier id, Supplier<T> factory) {
        T value = factory.get();
        if (entries.putIfAbsent(ResourceKey.create(key, id), value) != null) throw new IllegalArgumentException("Duplicate Clay definition " + id);
        return new RegistrySupplier<>(() -> value);
    }
    public T get(Identifier id) { return entries.get(ResourceKey.create(key, id)); }
    public Set<Map.Entry<ResourceKey<T>, T>> entrySet() { return entries.entrySet(); }
}
