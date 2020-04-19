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
import org.lanternpowered.api.script.function.condition.Condition;

public final class ConditionalAction implements Action {

    private final Action action;
    private final Condition condition;

    ConditionalAction(Condition condition, Action action) {
        this.condition = condition;
        this.action = action;
    }

    @Override
    public void run(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext context) {
        if (!this.condition.test(context)) {
            return;
        }
        this.action.run(context);
    }

    public Condition getCondition() {
        return this.condition;
    }

    public Action getAction() {
        return this.action;
    }
}
