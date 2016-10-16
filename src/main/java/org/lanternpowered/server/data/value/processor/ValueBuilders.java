/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.data.value.processor;

import org.lanternpowered.server.data.value.IValueContainer;
import org.lanternpowered.server.data.value.LanternValueFactory;
import org.lanternpowered.server.util.functions.TriFunction;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.mutable.Value;

@SuppressWarnings("unchecked")
public final class ValueBuilders {

    private static final TriFunction<Key, IValueContainer, Object, Value> DEFAULT =
            (key, valueContainer, object) -> (Value) LanternValueFactory.getInstance().createValueForKey(key, object);

    /**
     * Gets the default value builder. It will create a value through the{@link LanternValueFactory#createValueForKey(Key, Object)}
     * which will try to create a value for the appropriate type of the {@link BaseValue} attached to the {@link Key}.
     *
     * @param <V> The value type
     * @param <E> The element type
     * @return The value builder
     */
    public static <V extends BaseValue<E>, E> TriFunction<Key<? extends V>, IValueContainer<?>, E, V> def() {
        return (TriFunction) DEFAULT;
    }

}
