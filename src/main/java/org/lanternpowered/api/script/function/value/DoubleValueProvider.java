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
public interface DoubleValueProvider {

    static Constant constant(double value) {
        return new Constant(value);
    }

    static Range range(double min, double max) {
        return new Range(min, max);
    }

    double get(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext scriptContext);

    final class Constant implements DoubleValueProvider {

        private final double value;

        private Constant(double value) {
            this.value = value;
        }

        @Override
        public double get(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext scriptContext) {
            return this.value;
        }
    }

    final class Range implements DoubleValueProvider {

        private final double min;
        private final double max;

        private Range(double min, double max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public double get(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext scriptContext) {
            return LanternRandom.$random.range(this.min, this.max);
        }

        public double getMin() {
            return this.min;
        }

        public double getMax() {
            return this.max;
        }
    }
}
