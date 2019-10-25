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
package org.lanternpowered.server.data

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.data.value.CopyHelper
import org.lanternpowered.server.data.value.ValueFactory
import org.lanternpowered.server.util.function.TriConsumer
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.Optional

@Suppress("UNCHECKED_CAST")
internal open class LanternElementKeyRegistration<V : Value<E>, E : Any, H : DataHolder>(key: Key<V>) :
        LanternLocalKeyRegistration<V, E, H>(key), ElementKeyRegistration<V, E, H> {

    private var backing: E? = null
    private var removable = true
    private var validator: (H.(element: E) -> Boolean)? = null

    override fun nonRemovable() = apply { this.removable = false }
    override fun removable() = apply { this.removable = true }

    override fun get() = this.backing

    override fun set(element: E) = apply {
        this.backing = element
    }

    override fun remove() = apply {
        if (this.removable) {
            this.backing = null
        }
    }

    override fun validator(validator: H.(element: E) -> Boolean) = apply {
        this.validator = validator
    }

    override fun addChangeListener(listener: H.(newValue: E?, oldValue: E?) -> Unit) = apply { super.addChangeListener(listener) }
    override fun addChangeListener(listener: H.(newValue: E?) -> Unit) = apply { super.addChangeListener(listener) }
    override fun addChangeListener(listener: H.() -> Unit) = apply { super.addChangeListener(listener) }
    override fun addChangeListener(listener: TriConsumer<H, E?, E?>) = apply { super.addChangeListener(listener) }

    protected fun copyTo(copy: LanternElementKeyRegistration<V, E, H>) {
        copy.backing = CopyHelper.copy(this.backing)
        copy.removable = this.removable
        copy.validator = this.validator
        this.changeListeners?.let { copy.changeListeners = ArrayList(it) }
    }

    override fun copy(): LanternLocalKeyRegistration<V, E, H> {
        return LanternElementKeyRegistration<V, E, H>(this.key).also(::copyTo)
    }

    protected open fun validate(holder: H, element: E): Boolean {
        return this.validator?.invoke(holder, element) ?: false
    }

    protected open fun immutableValueOf(holder: H, element: E): Value.Immutable<E> {
        return ValueFactory.immutableOf(this.key, element).asImmutable()
    }

    override val dataProvider: IDataProvider<V, E> = object : IDataProvider<V, E> {

        override fun offer(container: DataHolder.Mutable, element: E): DataTransactionResult {
            container as H
            if (!validate(container, element)) {
                return DataTransactionResult.failResult(immutableValueOf(container, element))
            }
            val replacedElement = backing
            backing = element
            changeListeners?.also { listeners ->
                if (element != replacedElement) {
                    listeners.forEach { listener -> listener(container.uncheckedCast(), element, replacedElement) }
                }
            }
            val successful = immutableValueOf(container, element).asImmutable()
            return if (replacedElement != null) {
                val replaced = immutableValueOf(container, element)
                DataTransactionResult.successReplaceResult(successful, replaced)
            } else {
                DataTransactionResult.successResult(successful)
            }
        }

        override fun offerFast(container: DataHolder.Mutable, element: E): Boolean {
            if (!validate(container.uncheckedCast(), element)) {
                return false
            }
            val replacedElement = backing
            changeListeners?.also { listeners ->
                if (element != replacedElement) {
                    listeners.forEach { listener -> listener(container.uncheckedCast(), element, replacedElement) }
                }
            }
            backing = element
            return true
        }

        override fun offerValueFast(container: DataHolder.Mutable, value: V): Boolean {
            return offerFast(container, value.get())
        }

        override fun offerValue(container: DataHolder.Mutable, value: V): DataTransactionResult {
            container as H
            val element = value.get()
            if (!validate(container, element)) {
                return DataTransactionResult.failResult(value.asImmutable())
            }
            val replacedElement = backing
            backing = element
            changeListeners?.also { listeners ->
                if (element != replacedElement) {
                    listeners.forEach { listener -> listener(container, element, replacedElement) }
                }
            }
            return if (replacedElement != null) {
                val replaced = immutableValueOf(container, replacedElement)
                DataTransactionResult.successReplaceResult(value.asImmutable(), replaced)
            } else {
                DataTransactionResult.successResult(value.asImmutable())
            }
        }

        override fun allowsAsynchronousAccess(token: TypeToken<out DataHolder>) = false // TODO

        override fun get(container: DataHolder): Optional<E> {
            return backing.optional()
        }

        override fun getKey() = this@LanternElementKeyRegistration.key

        override fun isSupported(container: DataHolder) = true

        override fun removeFast(container: DataHolder.Mutable): Boolean {
            val value = backing
            if (value == null || !removable) {
                return false
            }
            backing = null
            changeListeners?.forEach { listener -> listener(container.uncheckedCast(), null, value) }
            return true
        }

        override fun remove(container: DataHolder.Mutable): DataTransactionResult {
            val value = backing ?: return DataTransactionResult.failNoData()
            if (!removable) {
                return DataTransactionResult.failNoData()
            }
            backing = null
            changeListeners?.forEach { listener -> listener(container.uncheckedCast(), null, value) }
            container as H
            return DataTransactionResult.successRemove(immutableValueOf(container, value))
        }

        override fun <I : DataHolder.Immutable<I>> with(immutable: I, element: E): Optional<I> = emptyOptional()
        override fun <I : DataHolder.Immutable<I>> without(immutable: I): Optional<I> = emptyOptional()
    }
}
