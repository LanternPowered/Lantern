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
