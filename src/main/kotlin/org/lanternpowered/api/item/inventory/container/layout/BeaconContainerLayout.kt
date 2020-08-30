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

import org.lanternpowered.api.effect.potion.PotionEffectType
import org.lanternpowered.api.entity.player.Player

/**
 * Represents the top container layout of a beacon.
 */
interface BeaconContainerLayout : ContainerLayout {

    /**
     * The payment slot.
     */
    val payment: ContainerSlot

    /**
     * The primary potion effect type that's currently
     * selected, if any.
     */
    var selectedPrimaryEffect: PotionEffectType?

    /**
     * The secondary potion effect type that's currently
     * selected, if any.
     */
    var selectedSecondaryEffect: PotionEffectType?

    /**
     * The power level, this controls which buttons are enabled.
     *
     * A value between 0 and 4 (inclusive).
     */
    var powerLevel: Int

    /**
     * The function will be called when the player selected the
     * potion effect types and confirms.
     */
    fun onSelectEffects(fn: (player: Player, primaryEffect: PotionEffectType?, secondaryEffect: PotionEffectType?) -> Unit)
}
