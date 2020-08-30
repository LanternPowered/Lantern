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
package org.lanternpowered.server.network.item

import com.google.gson.Gson
import com.google.gson.JsonArray
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.resolveNamespacedKey
import org.lanternpowered.api.registry.CatalogTypeRegistry
import org.lanternpowered.api.util.collections.toImmutableMap
import org.lanternpowered.server.item.LanternItemType
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * A registry which will handle everything related to [ItemType]s that
 * need to be synced between the client and server.
 */
object NetworkItemTypeRegistry {

    private var vanillaToData: Map<NamespacedKey, VanillaItemData>
    private var networkIdToVanilla: Int2ObjectMap<NamespacedKey>

    private class VanillaItemData(
            val networkId: Int,
            val maxStackSize: Int
    )

    init {
        val gson = Gson()
        val vanillaToData = HashMap<NamespacedKey, VanillaItemData>()
        val networkToVanilla = Int2ObjectOpenHashMap<NamespacedKey>()
        val input = InputStreamReader(NetworkItemTypeRegistry::class.java
                .getResourceAsStream("/internal/registries/item.json"))
        BufferedReader(input).use { reader ->
            val jsonArray = gson.fromJson(reader, JsonArray::class.java)
            for (index in 0 until jsonArray.size()) {
                val element = jsonArray.get(index)
                var maxStackSize = 64
                val id: String
                if (element.isJsonPrimitive) {
                    id = element.asString
                } else {
                    val obj = element.asJsonObject
                    id = obj.get("id").asString
                    if (obj.has("max_stack_size"))
                        maxStackSize = obj.get("max_stack_size").asInt
                }
                val key = resolveNamespacedKey(id)
                vanillaToData[key] = VanillaItemData(index, maxStackSize)
                networkToVanilla[index] = key
            }
        }
        this.vanillaToData = vanillaToData.toImmutableMap()
        this.networkIdToVanilla = Int2ObjectMaps.unmodifiable(networkToVanilla)
    }

    private val byNetworkId = Int2ObjectOpenHashMap<NetworkItemType>()
    private val byInternalId = Int2ObjectOpenHashMap<NetworkItemType>()
    private val byKey = HashMap<NamespacedKey, NetworkItemType>()

    fun getVanillaKeyByNetworkId(networkId: Int): NamespacedKey? = this.networkIdToVanilla[networkId]
    fun getByNetworkId(networkId: Int): NetworkItemType? = this.byNetworkId[networkId]
    fun getByInternalId(internalId: Int): NetworkItemType? = this.byInternalId[internalId]
    fun getByKey(key: NamespacedKey): NetworkItemType? = this.byKey[key]
    fun getByType(type: ItemType): NetworkItemType? = this.getByKey(type.key)

    fun load(registry: CatalogTypeRegistry<ItemType>) {
        // Start counting after the vanilla ids
        var internalIdCounter = this.networkIdToVanilla.keys.max()!! + 1
        for (type in registry.all) {
            type as LanternItemType
            val data = this.vanillaToData[type.key]
            val internalId = data?.networkId ?: internalIdCounter++
            val appearanceKey = type.appearance?.itemTypeKey ?: type.key
            val appearanceData = this.vanillaToData[appearanceKey]
                    ?: throw IllegalStateException("No network id was for the appearance item type key: $appearanceKey")
            val networkItemType = NetworkItemType(type, appearanceData.networkId, internalId, appearanceData.maxStackSize)
            this.byInternalId[internalId] = networkItemType
            this.byKey[type.key] = networkItemType
        }
    }
}
