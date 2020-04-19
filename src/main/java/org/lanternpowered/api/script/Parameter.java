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

import org.lanternpowered.api.script.function.condition.Condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to override the default names of the
 * parameters of a script method.
 * <p>
 * For example:
 * The interface {@link Condition} has a method {@link Condition#test(ScriptContext)},
 * where the {@link ScriptContext} parameter will be named as {@code scriptContext} by
 * default. But this can be overridden by this annotation.
 * <p>
 * Note that this has only effect for single line scripts, where the method
 * body is not predefined.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Parameter {

    /**
     * The parameter name.
     *
     * @return The value
     */
    String value();
}
