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
public interface FloatValueProvider {

    static Constant constant(float value) {
        return new Constant(value);
    }

    static Range range(float min, float max) {
        return new Range(min, max);
    }

    float get(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext scriptContext);

    final class Constant implements FloatValueProvider {

        private final float value;

        private Constant(float value) {
            this.value = value;
        }

        @Override
        public float get(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext context) {
            return this.value;
        }
    }

    final class Range implements FloatValueProvider {

        private final float min;
        private final float max;

        private Range(float min, float max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public float get(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext scriptContext) {
            return LanternRandom.$random.range(this.min, this.max);
        }

        public float getMin() {
            return this.min;
        }

        public float getMax() {
            return this.max;
        }
    }
}
