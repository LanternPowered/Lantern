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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.util.gson

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
