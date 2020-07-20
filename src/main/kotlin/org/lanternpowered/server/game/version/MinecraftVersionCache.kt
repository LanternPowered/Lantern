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
package org.lanternpowered.server.game.version

import com.google.gson.Gson
import com.google.gson.JsonArray
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.lanternpowered.api.util.optional.optional
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Optional

class MinecraftVersionCache {

    private val versionsByProtocol = Int2ObjectOpenHashMap<LanternMinecraftVersion>()
    private val legacyVersionsByProtocol = Int2ObjectOpenHashMap<LanternMinecraftVersion>()

    fun init() {
        load(MinecraftVersionCache::class.java.getResourceAsStream("/internal/mc_versions.json"), false)
        load(MinecraftVersionCache::class.java.getResourceAsStream("/internal/mc_legacy_versions.json"), true)
    }

    private fun load(inputStream: InputStream, legacy: Boolean) {
        val array = BufferedReader(InputStreamReader(inputStream)).use { reader -> Gson().fromJson(reader, JsonArray::class.java) }
        for (i in 0 until array.size()) {
            val obj = array[i].asJsonObject
            val json = obj["name"]
            val names = if (json.isJsonPrimitive) listOf(json.asString) else json.asJsonArray.map { element -> element.asString }
            val name = if (obj.has("overridden_name")) {
                obj["overridden_name"].asString.also {
                    check(names.contains(it)) { "The overridden name must be in the list of names." }
                }
            } else names[names.size - 1]
            val protocol = obj["version"].asInt
            val version = LanternMinecraftVersion(name, protocol, legacy)
            (if (legacy) this.legacyVersionsByProtocol else this.versionsByProtocol)[protocol] = version
        }
    }

    fun getVersion(protocol: Int, legacy: Boolean): LanternMinecraftVersion? =
            (if (legacy) this.legacyVersionsByProtocol else this.versionsByProtocol)[protocol]

    fun getVersionOrUnknown(protocol: Int, legacy: Boolean): LanternMinecraftVersion =
            getVersion(protocol, legacy) ?: if (legacy) LanternMinecraftVersion.UNKNOWN_LEGACY else LanternMinecraftVersion.UNKNOWN
}
