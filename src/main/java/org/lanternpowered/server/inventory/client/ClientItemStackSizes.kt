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
package org.lanternpowered.server.inventory.client

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.spongepowered.api.item.ItemType
import java.io.BufferedReader
import java.io.InputStreamReader

internal object ClientItemStackSizes {

    private val stackSizes = Object2IntOpenHashMap<String>()

    fun getOriginalMaxSize(itemType: ItemType): Int {
        val size = this.stackSizes.getInt(itemType.key.toString())
        return if (size == 0) itemType.maxStackQuantity else size
    }

    init {
        val gson = Gson()
        val input = ClientItemStackSizes::class.java.getResourceAsStream("/internal/registries/item.json")
        val json = gson.fromJson(BufferedReader(InputStreamReader(input)), JsonObject::class.java)
        for (entry in json.entrySet()) {
            val element: JsonElement = entry.value
            val id: String
            var maxStackSize = 64
            if (element.isJsonPrimitive) {
                id = element.asString
            } else {
                val jsonObject = element.asJsonObject
                id = jsonObject.get("id").asString
                if (jsonObject.has("max_stack_size"))
                    maxStackSize = jsonObject.get("max_stack_size").asInt
            }
            stackSizes[id] = maxStackSize
        }
    }
}
