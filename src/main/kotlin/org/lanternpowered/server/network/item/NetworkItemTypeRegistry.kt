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
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntMaps
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.resolveNamespacedKey
import org.lanternpowered.api.registry.CatalogTypeRegistry
import org.lanternpowered.server.game.registry.InternalIDRegistries
import org.lanternpowered.server.item.LanternItemType
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * A registry which will handle everything related to [ItemType]s that
 * need to be synced between the client and server.
 */
object NetworkItemTypeRegistry {

    private const val NO_NETWORK_ID = -1

    private var vanillaToNetworkId: Object2IntMap<NamespacedKey>
    private var networkIdToVanilla: Int2ObjectMap<NamespacedKey>

    init {
        val gson = Gson()
        val normalToNetwork = Object2IntOpenHashMap<NamespacedKey>()
        normalToNetwork.defaultReturnValue(NO_NETWORK_ID)
        val networkToNormal = Int2ObjectOpenHashMap<NamespacedKey>()
        try {
            val input = InputStreamReader(InternalIDRegistries::class.java
                    .getResourceAsStream("/internal/registries/item.json"))
            BufferedReader(input).use { reader ->
                val jsonArray = gson.fromJson(reader, JsonArray::class.java)
                for (index in 0 until jsonArray.size()) {
                    val element = jsonArray.get(index)
                    val id = if (element.isJsonPrimitive) element.asString else element.asJsonObject.get("id").asString
                    val key = resolveNamespacedKey(id)
                    normalToNetwork[key] = index
                    networkToNormal[index] = key
                }
            }
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
        this.vanillaToNetworkId = Object2IntMaps.unmodifiable(normalToNetwork)
        this.networkIdToVanilla = Int2ObjectMaps.unmodifiable(networkToNormal)
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
            var internalId = this.vanillaToNetworkId.getInt(type.key)
            if (internalId == NO_NETWORK_ID)
                internalId = internalIdCounter++
            val appearanceKey = type.appearance?.itemTypeKey ?: type.key
            val networkId = this.vanillaToNetworkId.getInt(appearanceKey)
            if (networkId == NO_NETWORK_ID)
                throw IllegalStateException("No network id was for the appearance item type key: $appearanceKey")
            val networkItemType = NetworkItemType(type, networkId, internalId)
            this.byInternalId[internalId] = networkItemType
            this.byKey[type.key] = networkItemType
        }
    }
}
