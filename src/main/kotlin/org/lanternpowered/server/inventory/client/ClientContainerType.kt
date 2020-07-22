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

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.inventory.AbstractInventory
import org.spongepowered.api.item.inventory.ContainerType

class ClientContainerType(key: NamespacedKey, private val containerProvider: (AbstractInventory) -> ClientContainer) :
        DefaultCatalogType(key), ContainerType {

    /**
     * Creates a new [ClientContainer].
     *
     * @return The client container
     */
    fun createContainer(openInventory: AbstractInventory): ClientContainer = this.containerProvider(openInventory)
}
