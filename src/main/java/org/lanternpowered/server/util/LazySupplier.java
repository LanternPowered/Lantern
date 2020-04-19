/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.util;

import java.util.function.Supplier;

public final class LazySupplier<T> implements Supplier<T> {

    public static <T> LazySupplier<T> of(Supplier<T> supplier) {
        return new LazySupplier<>(supplier);
    }

    private static final Object NONE = new Object();

    private Supplier<T> supplier;
    private T value = (T) NONE;

    private LazySupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (this.value == NONE) {
            this.value = this.supplier.get();
            this.supplier = null;
        }
        return this.value;
    }

}
