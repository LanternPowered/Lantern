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
 * Represents the top container layout of a horse.
 */
interface HorseContainerLayout : EntityContainerLayout {

    /**
     * The saddle slot.
     */
    val saddle: ContainerSlot

    /**
     * The armor slot.
     */
    val armor: ContainerSlot

    /**
     * The horse entity of this container layout. Only entities
     * which are "regular horses" will be displayed.
     */
    override var entity: Entity?
}
