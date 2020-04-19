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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.api.script.Parameter;
import org.lanternpowered.api.script.ScriptContext;
import org.lanternpowered.api.script.function.condition.Condition;

@FunctionalInterface
public interface Action {

    static EmptyAction empty() {
        return EmptyAction.INSTANCE;
    }

    static ConditionalAction conditional(Condition condition, Action action) {
        return new ConditionalAction(checkNotNull(condition, "condition"), checkNotNull(action, "action"));
    }

    static MultiAction multi(Iterable<Action> actions) {
        return new MultiAction(ImmutableList.copyOf(actions));
    }

    static MultiAction multi(Action... actions) {
        return new MultiAction(ImmutableList.copyOf(actions));
    }

    void run(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext scriptContext);

}
