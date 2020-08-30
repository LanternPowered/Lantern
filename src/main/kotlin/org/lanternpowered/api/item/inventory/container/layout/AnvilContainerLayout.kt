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
 * Represents the top container layout of an anvil.
 */
interface AnvilContainerLayout : ContainerLayout {

    /**
     * The repair cost in experience levels.
     */
    var repairCost: Int

    /**
     * The sub layout with all the inputs.
     */
    val inputs: ContainerLayout

    /**
     * The output slot.
     */
    val output: ContainerSlot

    /**
     * The function will be called when the name in the
     * anvil text field is being renamed.
     */
    fun onChangeName(fn: (player: Player, name: String) -> Unit)
}
