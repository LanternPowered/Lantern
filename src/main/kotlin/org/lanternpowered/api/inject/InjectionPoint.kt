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
package org.lanternpowered.api.inject

import com.google.common.reflect.TypeToken
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Executable

/**
 * Represents a point where a specific
 * value is being injected.
 */
interface InjectionPoint : AnnotatedElement {

    /**
     * The source class where the injection
     * point is located.
     */
    val source: TypeToken<*>

    /**
     * The value type of the field/parameter
     * at this injection point.
     */
    val type: TypeToken<*>

    /**
     * Represents a injection point which targets
     * a [java.lang.reflect.Field].
     */
    interface Field : InjectionPoint {

        /**
         * The backing field of the injection point.
         */
        val field: java.lang.reflect.Field
    }

    /**
     * Represents a injection point which targets a
     * [java.lang.reflect.Method] or
     * [java.lang.reflect.Constructor]
     * parameter.
     */
    interface Parameter : InjectionPoint {

        /**
         * The backing executable of the injection point.
         */
        val executable: Executable

        /**
         * The index of the parameter.
         */
        val parameterIndex: Int
    }
}
