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
package org.lanternpowered.api.script.function.action;

import org.lanternpowered.api.script.Parameter;
import org.lanternpowered.api.script.ScriptContext;

public final class EmptyAction implements Action {

    static final EmptyAction INSTANCE = new EmptyAction();

    private EmptyAction() {
    }

    @Override
    public void run(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext scriptContext) {
    }
}
