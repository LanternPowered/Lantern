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
package org.lanternpowered.api.ext

import com.google.inject.TypeLiteral
import org.lanternpowered.api.util.TypeToken
import java.lang.reflect.Type
import kotlin.reflect.KClass

// Utilities related to classes and type tokens

/**
 * Constructs a [TypeToken] for the [KClass].
 */
val <T : Any> KClass<T>.typeToken: TypeToken<T> get() = TypeToken.of(java)

/**
 * Constructs a [TypeToken] for the [Class].
 */
val <T : Any> Class<T>.typeToken: TypeToken<T> get() = TypeToken.of(this)

/**
 * Constructs a [TypeToken] for the [Type].
 */
val Type.typeToken: TypeToken<*> get() = TypeToken.of(this)

val <T : Any> TypeToken<T>.typeLiteral: TypeLiteral<T> get() = TypeLiteral.get(this.type).uncheckedCast()

/**
 * Constructs a [TypeToken] for type [T].
 */
inline fun <reified T> typeTokenOf(): TypeToken<T> = object : TypeToken<T>() {}
