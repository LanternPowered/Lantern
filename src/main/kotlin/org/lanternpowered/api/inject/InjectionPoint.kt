/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
