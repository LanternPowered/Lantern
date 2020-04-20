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
@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package org.lanternpowered.api.util.type

import com.google.inject.TypeLiteral
import org.lanternpowered.api.util.uncheckedCast
import java.lang.reflect.Type
import kotlin.reflect.KClass

typealias TypeToken<T> = com.google.common.reflect.TypeToken<T>

/**
 * Constructs a [TypeToken] for the [KClass].
 */
val <T : Any> KClass<T>.typeToken: TypeToken<T>
    get() = TypeToken.of(this.java)

/**
 * Gets the [TypeToken] for the [Class].
 */
val <T : Any> Class<T>.typeToken: TypeToken<T>
    get() = TypeToken.of(this)

/**
 * Gets the [TypeToken] for the [Type].
 */
val Type.typeToken: TypeToken<*>
    get() = TypeToken.of(this)

/**
 * Gets the [TypeLiteral] for the [TypeToken].
 */
val <T : Any> TypeToken<T>.typeLiteral: TypeLiteral<T>
    get() = TypeLiteral.get(this.type).uncheckedCast()

/**
 * Constructs a [TypeToken] for type [T].
 */
inline fun <reified T> typeTokenOf(): TypeToken<T> = object : TypeToken<T>() {}

/**
 * Constructs a [TypeToken] for the given type.
 */
inline fun <T : Any> typeTokenOf(type: KClass<T>): TypeToken<T> = TypeToken.of(type.java)

/**
 * Constructs a [TypeToken] for the given type.
 */
inline fun <T> typeTokenOf(type: Class<T>): TypeToken<T> = TypeToken.of(type)

/**
 * Constructs a [TypeToken] for the given type.
 */
inline fun <T> typeTokenOf(type: Type): TypeToken<T> = TypeToken.of(type) as TypeToken<T>
