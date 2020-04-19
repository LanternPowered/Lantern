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

import java.util.List;

public final class MultiAction implements Action {

    private final List<Action> actions;

    MultiAction(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public void run(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext context) {
        this.actions.forEach(action -> action.run(context));
    }

    public List<Action> getActions() {
        return this.actions;
    }
}
