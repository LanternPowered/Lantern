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
 * Represents the top container layout of a furnace.
 */
interface SmeltingContainerLayout : ContainerLayout {

    /**
     * The input slot.
     */
    val input: ContainerSlot

    /**
     * The fuel slot.
     */
    val fuel: ContainerSlot

    /**
     * The output slot.
     */
    val output: ContainerSlot

    /**
     * The progress arrow, 0 - 1. When it's 0 the arrow is
     * empty and for 1 it is filled.
     */
    var smeltProgress: Double

    /**
     * The fuel burn progress, 0 - 1. When it's 0 the icon is
     * empty and for 1 it is filled.
     */
    var fuelProgress: Double
}
