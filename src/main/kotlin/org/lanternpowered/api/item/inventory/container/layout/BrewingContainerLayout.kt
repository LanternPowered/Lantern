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

/**
 * Represents the top container layout of a brewing stand.
 */
interface BrewingContainerLayout : ContainerLayout {

    /**
     * The sub layout with all the bottles.
     */
    val bottles: ContainerLayout

    /**
     * The ingredient slot.
     */
    val ingredient: ContainerSlot

    /**
     * The fuel (blaze powder) slot.
     */
    val fuel: ContainerSlot

    /**
     * The brew progress arrow, 0 - 1. When it's 0 the icon is
     * empty and for 1 it is filled.
     */
    var brewProgress: Double

    /**
     * The fuel burn progress, 0 - 1. When it's 0 the icon is
     * empty and for 1 it is filled.
     */
    var fuelProgress: Double
}
