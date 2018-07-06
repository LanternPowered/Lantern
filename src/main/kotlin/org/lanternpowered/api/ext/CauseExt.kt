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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.ext

import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.cause.CauseContextKey
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.CauseStackManager
import org.lanternpowered.api.cause.CauseStackManagerFrame
import java.util.Optional
import kotlin.reflect.KClass

inline fun <T> CauseStackManager.first(target: Class<T>): Optional<T> = this.currentCause.first(target)
inline fun <T> CauseStackManager.last(target: Class<T>): Optional<T> = this.currentCause.last(target)
inline fun CauseStackManager.containsType(target: Class<*>): Boolean = this.currentCause.containsType(target)
inline operator fun CauseStackManager.contains(any: Any): Boolean = any in this.currentCause

inline fun <reified T> CauseStackManager.first(): T? = !this.currentCause.first(T::class.java)
inline fun <T : Any> CauseStackManager.first(clazz: KClass<T>): T? = !this.currentCause.first(clazz.java)
inline fun <reified T> CauseStackManager.last(): T? = !this.currentCause.last(T::class.java)
inline fun <T : Any> CauseStackManager.last(clazz: KClass<T>): T? = !this.currentCause.last(clazz.java)

inline fun <reified T> CauseStack.first(): T? = !first(T::class.java)
inline fun <T : Any> CauseStack.first(clazz: KClass<T>): T? = !first(clazz.java)
inline fun <reified T> CauseStack.last(): T? = !last(T::class.java)
inline fun <T : Any> CauseStack.last(clazz: KClass<T>): T? = !last(clazz.java)

inline operator fun <T> CauseStackManager.get(key: CauseContextKey<T>): T? = !getContext(key)
inline operator fun <T> CauseStackManager.set(key: CauseContextKey<T>, value: T) { addContext(key, value) }

inline fun CauseStackManager.withFrame(fn: CauseStackManagerFrame.() -> Unit) = pushCauseFrame().use(fn)
inline fun CauseStackManager.withPlugin(plugin: Any, fn: () -> Unit) = withCause(checkPluginInstance(plugin), fn)
inline fun CauseStackManager.withCause(cause: Any, fn: () -> Unit) {
    withFrame {
        pushCause(cause)
        fn()
    }
}

inline fun CauseStack.withFrame(fn: CauseStack.Frame.() -> Unit) = pushCauseFrame().use(fn)

inline fun <reified T> Cause.first(): T? = !first(T::class.java)
inline fun <T : Any> Cause.first(clazz: KClass<T>): T? = !first(clazz.java)
inline fun <reified T> Cause.last(): T? = !last(T::class.java)
inline fun <T : Any> Cause.last(clazz: KClass<T>): T? = !last(clazz.java)
inline fun <reified T> Cause.before(): Any? = !before(T::class.java)
inline fun Cause.before(clazz: KClass<*>): Any? = !before(clazz.java)
inline fun <reified T> Cause.after(): Any? = !after(T::class.java)
inline fun Cause.after(clazz: KClass<*>): Any? = !after(clazz.java)
inline fun <reified T> Cause.allOf(): List<T> = allOf(T::class.java)
inline fun <T : Any> Cause.allOf(clazz: KClass<T>): List<T> = allOf(clazz.java)
inline fun <reified T> Cause.noneOf(): List<Any> = noneOf(T::class.java)
inline fun Cause.noneOf(clazz: KClass<*>): List<Any> = noneOf(clazz.java)
