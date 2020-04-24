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
package org.lanternpowered.server.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Collections
import java.util.Enumeration
import java.util.ResourceBundle

class JsonResourceBundle(jsonObject: JsonObject) : ResourceBundle() {

    private val lookup: Map<String, String> = jsonObject.entrySet()
            .associate { (key, element) -> key to element.asString }

    override fun handleGetObject(key: String): Any = this.lookup[key] ?: error("Key $key not found.")
    override fun getKeys(): Enumeration<String> = Collections.enumeration(this.lookup.keys)

    companion object {

        @JvmStatic
        fun loadFrom(input: InputStream): JsonResourceBundle =
                JsonResourceBundle(Gson().fromJson(InputStreamReader(input), JsonObject::class.java))
    }
}
