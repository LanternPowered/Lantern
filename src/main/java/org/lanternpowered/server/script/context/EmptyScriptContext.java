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

import org.lanternpowered.api.script.ScriptContext;
import org.lanternpowered.api.script.context.Parameter;

import java.util.Optional;

public class EmptyScriptContext implements ScriptContext {

    public static final EmptyScriptContext INSTANCE = new EmptyScriptContext();

    @Override
    public <T> boolean has(Parameter<T> parameter) {
        return false;
    }

    @Override
    public <T> Optional<T> get(Parameter<T> parameter) {
        return Optional.empty();
    }
}
