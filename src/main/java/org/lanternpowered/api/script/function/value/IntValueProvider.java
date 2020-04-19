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

import org.lanternpowered.api.script.Parameter;
import org.lanternpowered.api.script.ScriptContext;
import org.lanternpowered.server.script.LanternRandom;

@FunctionalInterface
public interface IntValueProvider {

    static Constant constant(int value) {
        return new Constant(value);
    }

    static Range range(int min, int max) {
        return new Range(min, max);
    }

    int get(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext scriptContext);

    final class Constant implements IntValueProvider {

        private final int value;

        private Constant(int value) {
            this.value = value;
        }

        @Override
        public int get(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext scriptContext) {
            return this.value;
        }
    }

    final class Range implements IntValueProvider {

        private final int min;
        private final int max;

        private Range(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public int get(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext scriptContext) {
            return LanternRandom.$random.range(this.min, this.max);
        }

        public int getMin() {
            return this.min;
        }

        public int getMax() {
            return this.max;
        }
    }
}
