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

import com.google.inject.AbstractModule
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.Scopes
import com.google.inject.TypeLiteral
import com.google.inject.binder.AnnotatedBindingBuilder
import com.google.inject.binder.LinkedBindingBuilder
import com.google.inject.binder.ScopedBindingBuilder
import javax.inject.Provider
import kotlin.reflect.KClass

inline fun <reified T> typeLiteralOf(): TypeLiteral<T> = object : TypeLiteral<T>() {}

fun ScopedBindingBuilder.asSingleton(): Unit =
        `in`(Scopes.SINGLETON)

inline fun <reified T> Injector.getInstance(): T = getInstance(Key.get(typeLiteralOf<T>()))

abstract class GuiceModule : AbstractModule() {

    protected inline fun <reified T> bind(): AnnotatedBindingBuilder<T> = bind(typeLiteralOf())

    fun <T> LinkedBindingBuilder<T>.toProvider(providerType: KClass<out Provider<out T>>): ScopedBindingBuilder =
            toProvider(providerType.java)

    fun <T> LinkedBindingBuilder<T>.providedBy(provider: () -> T): ScopedBindingBuilder = toProvider(Provider(provider))
}
