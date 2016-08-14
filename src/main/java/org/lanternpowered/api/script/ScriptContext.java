/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.api.script;

import org.lanternpowered.api.script.context.ContextParameter;
import org.spongepowered.api.util.Tuple;

import java.util.List;
import java.util.Optional;

public interface ScriptContext {

    String CONTEXT_PARAMETER = "$context";

    boolean contains(ContextParameter<?> parameter);

    boolean containsValueType(Class<?> valueType);

    boolean containsValue(Object object);

    <T> Optional<T> get(ContextParameter<T> parameter);

    <T> Optional<T> put(ContextParameter<T> parameter, T value);

    <T> Optional<T> remove(ContextParameter<T> parameter);

    <T> Optional<Tuple<ContextParameter<T>, T>> first(Class<T> valueType);

    default <T> Optional<T> firstValue(Class<T> valueType) {
        return first(valueType).map(Tuple::getSecond);
    }

    <T> Optional<Tuple<ContextParameter<T>, T>> last(Class<T> valueType);

    default <T> Optional<T> lastValue(Class<T> valueType) {
        return last(valueType).map(Tuple::getSecond);
    }

    <T> List<T> allOf(Class<T> valueType);
}
