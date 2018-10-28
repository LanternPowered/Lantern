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

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import org.lanternpowered.api.util.TypeToken
import java.io.Reader
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType

fun Gson.fromJson(json: String, type: KType): Any = fromJson(json, type.javaType)
fun Gson.fromJson(json: Reader, type: KType): Any = fromJson(json, type.javaType)
fun Gson.fromJson(json: JsonElement, type: KType): Any = fromJson(json, type.javaType)

fun <T : Any> Gson.fromJson(json: String, type: KClass<T>): T = fromJson(json, type.java)
fun <T : Any> Gson.fromJson(json: Reader, type: KClass<T>): T = fromJson(json, type.java)
fun <T : Any> Gson.fromJson(json: JsonElement, type: KClass<T>): T = fromJson(json, type.java)

fun <T> Gson.fromJson(json: String, type: TypeToken<T>): T = fromJson(json, type.type)
fun <T> Gson.fromJson(json: Reader, type: TypeToken<T>): T = fromJson(json, type.type)
fun <T> Gson.fromJson(json: JsonElement, type: TypeToken<T>): T = fromJson(json, type.type)

inline fun <reified T> Gson.fromJson(json: String): T = fromJson(json, object : TypeToken<T>() {})
inline fun <reified T> Gson.fromJson(json: Reader): T = fromJson(json, object : TypeToken<T>() {})
inline fun <reified T> Gson.fromJson(json: JsonElement): T = fromJson(json, object : TypeToken<T>() {})

private val gson = Gson()

/**
 * Parses the [String] as a [JsonElement].
 */
fun String?.parseJson(): JsonElement = if (this == null) JsonNull.INSTANCE else gson.fromJson(this)
