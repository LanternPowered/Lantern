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
package org.lanternpowered.api.script.function.condition;

import org.lanternpowered.api.script.Parameter;
import org.lanternpowered.api.script.ScriptContext;

import java.util.List;

public final class OrCondition implements Condition {

    private final List<Condition> conditions;

    OrCondition(List<Condition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean test(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext context) {
        for (Condition condition : this.conditions) {
            if (condition.test(context)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a {@link List} with all the conditions of which one must
     * return {@code true} to make this condition return {@code true}.
     *
     * @return The conditions
     */
    public List<Condition> getConditions() {
        return this.conditions;
    }
}
