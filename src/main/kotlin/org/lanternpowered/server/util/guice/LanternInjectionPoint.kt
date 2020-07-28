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
package org.lanternpowered.server.util.guice

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.uncheckedCast
import java.lang.reflect.Executable

internal abstract class LanternInjectionPoint(
        override val source: TypeToken<*>,
        override val type: TypeToken<*>,
        private val annotations: Array<Annotation>
) : InjectionPoint {

    override fun <A : Annotation> getAnnotation(annotationClass: Class<A>): A? =
            this.annotations.firstOrNull { annotationClass.isInstance(it) }.uncheckedCast()

    override fun getAnnotations(): Array<Annotation> = this.annotations.copyOf()
    override fun getDeclaredAnnotations(): Array<Annotation> = getAnnotations()

    override fun toString(): String = ToStringHelper("InjectionPoint")
            .add("source", this.source)
            .add("type", this.type)
            .add("annotations", this.annotations.contentToString())
            .toString()

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
