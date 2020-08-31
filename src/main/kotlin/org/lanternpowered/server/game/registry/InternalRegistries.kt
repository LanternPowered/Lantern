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
package org.lanternpowered.server.game.registry

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.lanternpowered.api.key.resolveNamespacedKey
import org.lanternpowered.api.util.gson.fromJson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.function.BiConsumer

object InternalRegistries {

    private val gson = Gson()

    @JvmStatic
    fun load(registryName: String) = load(registryName) { key, _ -> resolveNamespacedKey(key) }

    @JvmStatic
    fun <T : Any> load(registryName: String, objectConstructor: (String) -> T) = load(registryName) { key, _ -> objectConstructor(key) }

    @JvmStatic
    fun <T : Any> load(registryName: String, objectConstructor: (String, Int) -> T): InternalRegistry<T> {
        val keyToId = Object2IntOpenHashMap<String>()
        visit(registryName) { name, id -> keyToId[name] = id }
        return InternalRegistry(keyToId, objectConstructor)
    }

    @JvmStatic
    inline fun visit(registryName: String, function: (String) -> Unit) = visit(registryName) { name, _: Int -> function(name) }

    @JvmStatic
    inline fun visit(registryName: String, function: (String, Int) -> Unit) = visitElements(registryName) { name, id, _ -> function(name, id) }

    @JvmStatic
    fun visitElements(registryName: String, function: BiConsumer<String, JsonElement>) = visitElements(registryName, function::accept)

    @JvmStatic
    inline fun visitElements(registryName: String, function: (String, JsonElement) -> Unit) =
            visitElements(registryName) { name, _, element -> function(name, element) }

    @JvmStatic
    inline fun visitElements(registryName: String, function: (String, Int, JsonElement) -> Unit) {
        val jsonArray = readJsonArray(registryName)
        jsonArray.forEachIndexed { index, element ->
            if (element.isJsonPrimitive) {
                function(element.asString, index, JsonNull.INSTANCE)
            } else {
                function(element.asJsonObject["id"].asString, index, element)
            }
        }
    }

    fun readJsonArray(registryName: String): JsonArray {
        val input = InternalRegistries::class.java.getResourceAsStream("/internal/registries/$registryName.json")
        val reader = BufferedReader(InputStreamReader(input))
        try {
            reader.use {
                return this.gson.fromJson(reader)
            }
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }
}
