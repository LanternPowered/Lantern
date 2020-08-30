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

import org.lanternpowered.api.item.inventory.container.ExtendedContainerType
import org.lanternpowered.api.item.inventory.container.layout.ContainerLayout
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.server.catalog.DefaultCatalogType

class LanternContainerType<L : ContainerLayout>(key: NamespacedKey, private val layoutProvider: () -> L) :
        DefaultCatalogType(key), ExtendedContainerType<L> {

    /**
     * Creates a new layout.
     */
    fun createLayout(): L = this.layoutProvider()
}
