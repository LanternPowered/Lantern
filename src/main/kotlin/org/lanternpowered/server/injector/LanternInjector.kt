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
package org.lanternpowered.server.injector

import com.google.inject.BindingAnnotation
import com.google.inject.Key
import io.netty.util.concurrent.FastThreadLocal
import org.lanternpowered.api.injector.ChildInjector
import org.lanternpowered.api.injector.Injector
import org.lanternpowered.api.injector.InjectorBuilder
import org.lanternpowered.api.injector.get
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.uncheckedCast
import javax.inject.Qualifier
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType
import com.google.inject.Injector as GuiceInjector

class DefaultInjector : Injector {

    override fun createChild(fn: InjectorBuilder.() -> Unit): Injector {
        TODO("Not yet implemented")
    }

    override fun <T : Any> get(type: KType): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any> get(type: KClass<T>): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any> get(type: TypeToken<T>): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any> getLazy(type: KType): Lazy<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> getLazy(type: KClass<T>): Lazy<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> getLazy(type: TypeToken<T>): Lazy<T> {
        TODO("Not yet implemented")
    }

}

private val globalInjector = FastThreadLocal<Injector?>()

object LanternGlobalInjector : Injector {

    override fun createChild(fn: InjectorBuilder.() -> Unit): Injector {
        TODO("Not yet implemented")
    }

    override fun <T : Any> get(type: KType): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any> get(type: KClass<T>): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any> get(type: TypeToken<T>): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any> getLazy(type: KType): Lazy<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> getLazy(type: KClass<T>): Lazy<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> getLazy(type: TypeToken<T>): Lazy<T> {
        TODO("Not yet implemented")
    }
}

private val currentInjector = FastThreadLocal<Pair<Injector, ChildInjector.Context>>()

private class InjectorContext(injector: Injector) : ChildInjector.Context {

    var closed = false
    val previous: Pair<Injector, ChildInjector.Context> = currentInjector.get()

    init {
        currentInjector.set(injector to this)
    }

    override fun close() {
        if (this.closed)
            return
        val pair = currentInjector.get() ?: return
        if (pair.second !== this)
            throw IllegalStateException("Injector close order problem. An context that was opened " +
                    "after this one must be closed before closing this one.")
        currentInjector.set(this.previous)
        this.closed = true
    }
}

class LanternInjector(
        private val guiceInjector: GuiceInjector
) : ChildInjector {

    override fun createChild(fn: InjectorBuilder.() -> Unit): Injector {
        TODO("Not yet implemented")
    }

    override fun openContext(): ChildInjector.Context = InjectorContext(this)

    override fun <T : Any> get(type: KType): T {
        val bindingAnnotation = type.annotations
                .firstOrNull { annotation -> annotation.annotationClass.annotations.any { it is Qualifier || it is BindingAnnotation } }
        val key = if (bindingAnnotation != null) Key.get(type.javaType, bindingAnnotation) else Key.get(type.javaType)
        return this.guiceInjector.getInstance(key.uncheckedCast<Key<T>>())
    }

    override fun <T : Any> get(type: KClass<T>): T =
            this.guiceInjector.getInstance(type.java)

    override fun <T : Any> get(type: TypeToken<T>): T =
            this.guiceInjector.getInstance(Key.get(type.type)).uncheckedCast()

    override fun <T : Any> getLazy(type: KType): Lazy<T> = lazy { get<T>(type) }
    override fun <T : Any> getLazy(type: KClass<T>): Lazy<T> = lazy { get(type) }
    override fun <T : Any> getLazy(type: TypeToken<T>): Lazy<T> = lazy { get(type) }
}
