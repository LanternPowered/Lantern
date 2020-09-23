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
package org.lanternpowered.server.inventory.container

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.resolveNamespacedKey
import org.lanternpowered.server.game.registry.InternalRegistries

object ClientWindowTypes {

    private val keyToIdMap = HashMap<NamespacedKey, ClientWindowType>()

    init {
        InternalRegistries.visit("menu") { name, internalId ->
            val key = resolveNamespacedKey(name)
            this.keyToIdMap[key] = ClientWindowType(key, internalId)
        }
    }

    fun get(key: String) = get(resolveNamespacedKey(key))
    fun get(key: NamespacedKey) = requireNotNull(this.keyToIdMap[key]) { "Cannot find mapping for $key" }

    @JvmField val ANVIL = get("anvil")
    @JvmField val BEACON = get("beacon")
    @JvmField val BLAST_FURNACE = get("blast_furnace")
    @JvmField val BREWING_STAND = get("brewing_stand")
    @JvmField val CARTOGRAPHY = get("cartography")
    @JvmField val CRAFTING = get("crafting")
    @JvmField val ENCHANTMENT = get("enchantment")
    @JvmField val FURNACE = get("furnace")
    @JvmField val GENERIC_3x3 = get("generic_3x3")
    @JvmField val GENERIC_9x1 = get("generic_9x1")
    @JvmField val GENERIC_9x2 = get("generic_9x2")
    @JvmField val GENERIC_9x3 = get("generic_9x3")
    @JvmField val GENERIC_9x4 = get("generic_9x4")
    @JvmField val GENERIC_9x5 = get("generic_9x5")
    @JvmField val GENERIC_9x6 = get("generic_9x6")
    @JvmField val GRINDSTONE = get("grindstone")
    @JvmField val HOPPER = get("hopper")
    @JvmField val LECTERN = get("lectern")
    @JvmField val LOOM = get("loom")
    @JvmField val MERCHANT = get("merchant")
    @JvmField val SHULKER_BOX = get("shulker_box")
    @JvmField val SMOKER = get("smoker")
    @JvmField val STONE_CUTTER = get("stonecutter")
}
