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

import com.google.common.collect.ImmutableList;
import org.lanternpowered.api.script.Parameter;
import org.lanternpowered.api.script.ScriptContext;

/**
 * Represents a condition that must return true in order for
 * a specific action to be executed.
 */
@FunctionalInterface
public interface Condition {

    static AndCondition and(Iterable<Condition> conditions) {
        return new AndCondition(ImmutableList.copyOf(conditions));
    }

    static AndCondition and(Condition... conditions) {
        return new AndCondition(ImmutableList.copyOf(conditions));
    }

    static OrCondition or(Iterable<Condition> conditions) {
        return new OrCondition(ImmutableList.copyOf(conditions));
    }

    static OrCondition or(Condition... conditions) {
        return new OrCondition(ImmutableList.copyOf(conditions));
    }

    /**
     * Gets the result of this condition for the specified {@link ScriptContext}.
     *
     * @param scriptContext The script context
     * @return The result
     */
    boolean test(@Parameter(ScriptContext.CONTEXT_PARAMETER) ScriptContext scriptContext);

}
