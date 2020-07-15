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
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.item.inventory.ContainerType
import org.spongepowered.api.item.inventory.ContainerTypes
import org.spongepowered.api.item.inventory.type.CarriedInventory
import kotlin.math.ceil

object ClientContainerRegistryModule : DefaultCatalogRegistryModule<ContainerType>(ContainerTypes::class) {

    override fun registerDefaults() {
        fun register(id: String, supplier: (AbstractInventory) -> ClientContainer) {
            register(ClientContainerType(ResourceKey.minecraft(id), supplier))
        }

        register("chest") { inventory ->
            val rows = ceil(inventory.capacity().toDouble() / 9.0).toInt()
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
            capacity = ceil(capacity.toDouble() / 3).toInt()
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
