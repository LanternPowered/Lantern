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
import org.spongepowered.api.data.type.BannerPatternShape
import org.spongepowered.api.data.type.BannerPatternShapes

/**
 * Represents the top container layout of a loom.
 */
interface LoomContainerLayout : ContainerLayout {

    /**
     * The banner input slot.
     */
    val banner: ContainerSlot

    /**
     * The dye input slot.
     */
    val dye: ContainerSlot

    /**
     * The pattern input slot.
     */
    val pattern: ContainerSlot

    /**
     * The output slot.
     */
    val output: ContainerSlot

    /**
     * The selected shape. Using [BannerPatternShapes.BASE] will
     * reset the selected pattern.
     */
    var selectedShape: BannerPatternShape

    /**
     * Is called when a player clicks on one of the shape buttons.
     */
    fun onClickShape(fn: (player: Player, shape: BannerPatternShape) -> Unit)
}
