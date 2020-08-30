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

import org.lanternpowered.api.entity.player.Player

/**
 * Represents a lectern container layout.
 */
interface LecternContainerLayout : ContainerLayout {

    /**
     * The book slot.
     */
    val book: ContainerSlot

    /**
     * The current page that is selected, starting at 0.
     */
    var page: Int

    /**
     * Is called when a player interacts with the lectern.
     */
    fun onClick(fn: (player: Player, action: LecternClickAction) -> Unit)
}
