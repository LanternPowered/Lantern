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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import org.lanternpowered.api.script.ScriptContext;
import org.lanternpowered.api.script.context.Parameter;

import java.util.Map;
import java.util.Optional;

public class ContextImpl implements ScriptContext {

    private final Map<Parameter<?>, Object> parameters;

    public ContextImpl(Map<Parameter<?>, Object> parameters) {
        this.parameters = ImmutableMap.copyOf(parameters);
    }

    @Override
    public <T> boolean has(Parameter<T> parameter) {
        checkNotNull(parameter, "parameter");
        return this.parameters.containsKey(parameter);
    }

    @Override
    public <T> Optional<T> get(Parameter<T> parameter) {
        checkNotNull(parameter, "parameter");
        //noinspection unchecked
        return Optional.ofNullable((T) this.parameters.get(parameter));
    }
}
