package com.matthewperiut.clay.platform;
import java.util.function.Supplier;
public final class RegistrySupplier<T> implements Supplier<T> {
    private final Supplier<? extends T> supplier;
    public RegistrySupplier(Supplier<? extends T> supplier) { this.supplier = supplier; }
    @Override public T get() { return supplier.get(); }
}
