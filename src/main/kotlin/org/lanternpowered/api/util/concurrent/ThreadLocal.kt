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
                this.set(value)
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
