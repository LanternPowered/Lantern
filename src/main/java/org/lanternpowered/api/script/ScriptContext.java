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
package org.lanternpowered.api.script;

import org.lanternpowered.api.script.context.Parameter;

import java.util.Optional;

public interface ScriptContext {

    String CONTEXT_PARAMETER = "$context";

    <T> boolean has(Parameter<T> parameter);

    <T> Optional<T> get(Parameter<T> parameter);
}
