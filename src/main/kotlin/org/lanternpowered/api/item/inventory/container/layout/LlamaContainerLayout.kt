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
interface LlamaContainerLayout : EntityContainerLayout {

    /**
     * The carpet slot.
     */
    val carpet: ContainerSlot

    /**
     * The chest of this layout, the size can be 0 if
     * there are no chest slots.
     */
    val chest: GridContainerLayout

    /**
     * The horse entity of this container layout. Only entities
     * which are "llamas" will be displayed.
     */
    override var entity: Entity?
}
