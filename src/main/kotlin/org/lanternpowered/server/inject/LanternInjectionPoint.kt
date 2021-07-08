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
package org.lanternpowered.server.inject

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.inject.InjectionPoint
import org.lanternpowered.server.util.ToStringHelper
import java.lang.reflect.Executable
import java.util.Arrays

internal abstract class LanternInjectionPoint(
        override val source: TypeToken<*>,
        override val type: TypeToken<*>,
        private val annotations: Array<Annotation>
) : InjectionPoint {

    override fun <A : Annotation> getAnnotation(annotationClass: Class<A>): A? =
            this.annotations.firstOrNull { annotationClass.isInstance(it) }.uncheckedCast()

    override fun getAnnotations(): Array<Annotation> = Arrays.copyOf(this.annotations, this.annotations.size)
    override fun getDeclaredAnnotations(): Array<Annotation> = getAnnotations()

    override fun toString(): String {
        return ToStringHelper("InjectionPoint")
                .add("source", this.source)
                .add("type", this.type)
                .add("annotations", Arrays.toString(this.annotations))
                .toString()
    }

    internal class Field(
            source: TypeToken<*>,
            type: TypeToken<*>,
            annotations: Array<Annotation>,
            override val field: java.lang.reflect.Field
    ) : LanternInjectionPoint(source, type, annotations), InjectionPoint.Field

    internal class Parameter(
            source: TypeToken<*>,
            type: TypeToken<*>,
            annotations: Array<Annotation>,
            override val executable: Executable,
            override val parameterIndex: Int
    ) : LanternInjectionPoint(source, type, annotations), InjectionPoint.Parameter
}
