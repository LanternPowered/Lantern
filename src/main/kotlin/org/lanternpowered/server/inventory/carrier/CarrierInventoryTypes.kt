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
package org.lanternpowered.server.inventory.carrier

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.common.reflect.TypeToken
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.item.inventory.Carrier
import org.spongepowered.api.item.inventory.type.CarriedInventory

object CarrierInventoryTypes {

    private val carrierTypeVariable = CarriedInventory::class.java.typeParameters[0]
    private val carrierTypeCache = Caffeine.newBuilder()
            .weakKeys()
            .build<Class<*>, Class<out Carrier>?> { key: Class<*> ->
                if (!CarriedInventory::class.java.isAssignableFrom(key))
                    return@build null
                TypeToken.of(key).resolveType(this.carrierTypeVariable).rawType.uncheckedCast()
            }

    /**
     * Gets the carrier type of the target inventory, if applicable.
     */
    fun getCarrierType(inventoryType: Class<out Inventory>): Class<out Carrier>? = this.carrierTypeCache[inventoryType]
}
