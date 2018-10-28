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
package org.lanternpowered.server.inventory.client

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.game.registry.InternalRegistries

object ClientWindowTypes {

    private val keyToIdMap = HashMap<CatalogKey, ClientWindowType>()

    init {
        InternalRegistries.visit("menu") { name, internalId ->
            val key = CatalogKey.resolve(name)
            this.keyToIdMap[key] = ClientWindowType(key, internalId)
        }
    }

    fun get(key: String) = get(CatalogKey.resolve(key))
    fun get(key: CatalogKey) = requireNotNull(this.keyToIdMap[key]) { "Cannot find mapping for $key" }

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
    @JvmField val STONECUTTER = get("stonecutter")
}
