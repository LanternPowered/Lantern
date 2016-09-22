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
package org.lanternpowered.server.script.context;

import org.lanternpowered.api.script.ScriptContext;
import org.lanternpowered.api.script.context.ContextParameter;
import org.spongepowered.api.util.Tuple;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EmptyScriptContext implements ScriptContext {

    public static final EmptyScriptContext INSTANCE = new EmptyScriptContext();

    @Override
    public boolean contains(ContextParameter<?> parameter) {
        return false;
    }

    @Override
    public boolean containsValueType(Class<?> valueType) {
        return false;
    }

    @Override
    public boolean containsValue(Object object) {
        return false;
    }

    @Override
    public <T> Optional<T> get(ContextParameter<T> parameter) {
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> put(ContextParameter<T> parameter, T value) {
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> remove(ContextParameter<T> parameter) {
        return Optional.empty();
    }

    @Override
    public <T> Optional<Tuple<ContextParameter<T>, T>> first(Class<T> valueType) {
        return Optional.empty();
    }

    @Override
    public <T> Optional<Tuple<ContextParameter<T>, T>> last(Class<T> valueType) {
        return Optional.empty();
    }

    @Override
    public <T> List<T> allOf(Class<T> valueType) {
        return Collections.emptyList();
    }
}
