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
package org.lanternpowered.api.item.inventory.container.layout

import org.spongepowered.api.entity.Entity

/**
 * A container layout where an entity is being rendered on the layout.
 */
interface EntityContainerLayout : ContainerLayout {

    /**
     * The entity of this container layout. Some containers
     * may only display entities which the layout was designed
     * for on the official client.
     */
    var entity: Entity?
}
