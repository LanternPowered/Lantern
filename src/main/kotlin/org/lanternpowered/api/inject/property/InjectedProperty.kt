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
