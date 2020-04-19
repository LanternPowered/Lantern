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
