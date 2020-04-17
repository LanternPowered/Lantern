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
package org.lanternpowered.api.inject.property

import org.lanternpowered.api.util.uncheckedCast
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A property that can be injected.
 */
interface InjectedProperty<T> : ReadOnlyProperty<Any, T> {

    /**
     * Injects the given value.
     */
    fun inject(provider: () -> T)
}

/**
 * A base class for the injectable property types.
 */
abstract class InjectedPropertyBase<T> : InjectedProperty<T> {

    internal var value = uninitialized

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        val value = this.value
        return if (value !== uninitialized) value.uncheckedCast() else throw IllegalStateException("Not yet injected.")
    }

    companion object {
        internal val uninitialized = Object()
    }
}

/**
 * The default injected property type, directly initializes the value.
 */
class DefaultInjectedProperty<T> : InjectedPropertyBase<T>() {

    override fun inject(provider: () -> T) {
        this.value = provider().uncheckedCast()
    }
}

/**
 * The lazy injected property type, the value will be
 * initialized the first time it's requested.
 */
class LazyInjectedProperty<T> : InjectedPropertyBase<T>() {

    private var provider: (() -> T)? = null

    override fun inject(provider: () -> T) {
        this.provider = provider
        this.value = uninitialized
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        val provider = this.provider
        if (this.value === uninitialized && provider != null) {
            this.value = provider().uncheckedCast()
        }
        return super.getValue(thisRef, property)
    }
}
