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
import org.lanternpowered.api.item.enchantment.Enchantment

/**
 * Represents the top container layout of an enchanting table.
 */
interface EnchantingContainerLayout : ContainerLayout {

    /**
     * The seed that is used to draw enchanting names (in SGA).
     */
    var seed: Int

    /**
     * All the buttons of this layout.
     */
    val buttons: List<Button>

    /**
     * The input slot.
     */
    val input: ContainerSlot

    /**
     * The lapis lazuli slot.
     */
    val lapis: ContainerSlot

    /**
     * Is called when the button was clicked by the given player.
     */
    fun onClickButton(fn: (player: Player, button: Button) -> Unit)

    /**
     * Represents a button in the layout.
     */
    interface Button {

        /**
         * The index of the button.
         */
        val index: Int

        /**
         * The level that is required to use the button.
         */
        var levelRequirement: Int?

        /**
         * The enchantment that should be displayed when hovering
         * over the button. Or `null` if nothing.
         */
        var enchantment: Enchantment?
    }
}
