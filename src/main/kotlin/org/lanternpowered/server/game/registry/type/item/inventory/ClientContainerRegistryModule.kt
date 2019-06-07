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
package org.lanternpowered.server.game.registry.type.item.inventory

import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule
import org.lanternpowered.server.inventory.AbstractInventory
import org.lanternpowered.server.inventory.client.AnvilClientContainer
import org.lanternpowered.server.inventory.client.BeaconClientContainer
import org.lanternpowered.server.inventory.client.BrewingStandClientContainer
import org.lanternpowered.server.inventory.client.ChestClientContainer
import org.lanternpowered.server.inventory.client.ClientContainer
import org.lanternpowered.server.inventory.client.ClientContainerType
import org.lanternpowered.server.inventory.client.CraftingTableClientContainer
import org.lanternpowered.server.inventory.client.DispenserClientContainer
import org.lanternpowered.server.inventory.client.EnchantmentTableClientContainer
import org.lanternpowered.server.inventory.client.EntityEquipmentClientContainer
import org.lanternpowered.server.inventory.client.FurnaceClientContainer
import org.lanternpowered.server.inventory.client.GrindstoneClientContainer
import org.lanternpowered.server.inventory.client.HopperClientContainer
import org.lanternpowered.server.inventory.client.ShulkerBoxClientContainer
import org.lanternpowered.server.inventory.client.TradingClientContainer
import org.lanternpowered.server.network.entity.EntityProtocolManager.INVALID_ENTITY_ID
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.item.inventory.type.CarriedInventory

object ClientContainerRegistryModule : DefaultCatalogRegistryModule<GuiId>(GuiIds::class) {

    override fun registerDefaults() {
        fun register(id: String, supplier: (AbstractInventory) -> ClientContainer) {
            register(ClientContainerType(CatalogKey.minecraft(id), supplier))
        }

        register("chest") { inventory ->
            val rows = Math.ceil(inventory.capacity().toDouble() / 9.0).toInt()
            ChestClientContainer(rows)
        }
        register("furnace") { FurnaceClientContainer() }
        register("dispenser") { DispenserClientContainer() }
        register("crafting_table") { CraftingTableClientContainer() }
        register("brewing_stand") { BrewingStandClientContainer() }
        register("hopper") { HopperClientContainer() }
        register("beacon") { BeaconClientContainer() }
        register("enchanting_table") { EnchantmentTableClientContainer() }
        register("anvil") { AnvilClientContainer() }
        register("villager") { TradingClientContainer() }
        register("horse") { inventory ->
            var capacity = inventory.capacity()
            capacity -= 2
            capacity = Math.ceil(capacity.toDouble() / 3).toInt()
            var entityId = -1
            if (inventory is CarriedInventory<*>) {
                val carrier = inventory.carrier.orElse(null)
                if (carrier is Entity) {
                    val entity = carrier as LanternEntity
                    entityId = entity.world.entityProtocolManager.getProtocolId(entity)
                }
            }
            if (entityId == INVALID_ENTITY_ID) {
                throw IllegalStateException("Invalid carrier entity to create a container.")
            }
            // TODO: Dummy entity support?
            EntityEquipmentClientContainer(capacity, entityId)
        }
        register("shulker_box") { ShulkerBoxClientContainer() }
        register("grindstone") { GrindstoneClientContainer() }
    }
}
