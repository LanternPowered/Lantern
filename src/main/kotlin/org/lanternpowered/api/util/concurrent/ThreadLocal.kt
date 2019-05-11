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
@file:JvmName("ThreadLocals")
@file:Suppress("FunctionName", "UNUSED_PARAMETER", "NOTHING_TO_INLINE", "UNCHECKED_CAST")

package org.lanternpowered.api.util.concurrent

import java.lang.ref.SoftReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

typealias ThreadLocal<V> = io.netty.util.concurrent.FastThreadLocal<V>

/**
 * Provides a delegate for a [ThreadLocal].
 */
operator fun <V> ThreadLocal<V>.provideDelegate(thisRef: Any, property: KProperty<*>): ReadWriteProperty<Any, V> = ThreadLocalDelegate(this)

private class ThreadLocalDelegate<V>(private val threadLocal: ThreadLocal<V>) : ReadWriteProperty<Any, V> {
    override fun setValue(thisRef: Any, property: KProperty<*>, value: V) = this.threadLocal.set(value)
    override fun getValue(thisRef: Any, property: KProperty<*>) = this.threadLocal.get() as V
}

/**
 * Constructs a new [ThreadLocal] with the initial value supplier.
 *
 * @param fn The initial value supplier
 * @return The thread local
 */
@JvmName("of")
fun <V> ThreadLocal(fn: () -> V): ThreadLocal<V> = InitializedThreadLocal(fn)

private class InitializedThreadLocal<V>(val fn: () -> V) : ThreadLocal<V>() {
    override fun initialValue(): V = this.fn()
}

/**
 * Constructs a new [SoftThreadLocal] with no initializer,
 * this means that nullable values may be expected.
 */
@JvmName("softThreadLocalOf")
inline fun <V> SoftThreadLocal(): SoftThreadLocal<V?> = SoftThreadLocal { null }

/**
 * This is a [ThreadLocal] that uses [SoftReference]s to store
 * the values to prevent possible memory leaks for some use purposes.
 *
 * @param V The type of the value
 * @param supplier The initial value supplier
 * @constructor Constructs a new soft thread local with the initial supplier
 */
class SoftThreadLocal<V>(private val supplier: () -> V) {

    private val threadLocal = ThreadLocal<SoftReference<V>?> {
        val obj = this.supplier()
        if (obj == null) null else SoftReference(obj)
    }

    /**
     * Gets the current thread's value for this thread-local variable.
     *
     * @return The value
     */
    fun get(): V {
        val ref = this.threadLocal.get()
        if (ref != null) {
            val nullable = ref.get()
            val value: V
            if (nullable == null) {
                value = this.supplier()
                set(value)
            } else {
                value = nullable
            }
            return value
        }
        return null as V // Depending on the target value this will be null or not
    }

    /**
     * Removes the current thread's value for this thread-local variable.
     */
    fun remove() = this.threadLocal.remove()

    /**
     * Sets the current thread's value for this thread-local variable.
     *
     * @param value The value
     */
    fun set(value: V) = this.threadLocal.set(if (value == null) null else SoftReference(value))

    /**
     * Provides a delegate for this [SoftThreadLocal].
     */
    operator fun provideDelegate(thisRef: Any, property: KProperty<*>): ReadWriteProperty<Any, V> = SoftThreadLocalDelegate(this)

    private class SoftThreadLocalDelegate<V>(private val threadLocal: SoftThreadLocal<V>) : ReadWriteProperty<Any, V> {
        override fun setValue(thisRef: Any, property: KProperty<*>, value: V) = this.threadLocal.set(value)
        override fun getValue(thisRef: Any, property: KProperty<*>) = this.threadLocal.get()
    }
}
