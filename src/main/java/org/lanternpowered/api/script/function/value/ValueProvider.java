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
package org.lanternpowered.api.script.function.value;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.api.script.Parameter;
import org.lanternpowered.api.script.ScriptContext;

@FunctionalInterface
public interface ValueProvider<T> {

    static <T> ValueProvider<T> constant(T value) {
        return new Constant<>(checkNotNull(value, "value"));
    }

    T get(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext scriptContext);

    final class Constant<T> implements ValueProvider<T> {

        private final T value;

        private Constant(T value) {
            this.value = value;
        }

        @Override
        public T get(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext scriptContext) {
            return this.value;
        }
    }
}
