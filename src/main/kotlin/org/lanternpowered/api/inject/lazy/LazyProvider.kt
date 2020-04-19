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
package org.lanternpowered.api.inject.lazy

import com.google.common.reflect.TypeToken
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.Provider
import org.lanternpowered.api.inject.InjectionPoint
import org.lanternpowered.api.util.type.typeLiteral
import org.lanternpowered.api.util.uncheckedCast
import java.lang.reflect.TypeVariable

/**
 * A provider for [Lazy] values.
 */
internal class LazyProvider<T : Any> : Provider<Lazy<T>> {

    @Inject private lateinit var injector: Injector
    @Inject private lateinit var point: InjectionPoint

    override fun get(): Lazy<T> {
        // Extract the value type from the injection point
        val valueType = Key.get(this.point.type.resolveType(valueVariable).uncheckedCast<TypeToken<T>>().typeLiteral)
        // Must be present, so not the object class
        if (valueType == Object::class.java) {
            throw IllegalStateException("Missing value type.")
        }
        return object : Lazy<T> {
            override val value: T by lazy { injector.getInstance(valueType) }
        }
    }

    companion object {
        private val valueVariable: TypeVariable<*> = Lazy::class.java.typeParameters[0]
    }
}
