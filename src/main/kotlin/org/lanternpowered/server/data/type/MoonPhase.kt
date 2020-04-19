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
package org.lanternpowered.server.data.type

import org.spongepowered.api.util.Cycleable

/**
 * All the moon phases in minecraft.
 */
enum class MoonPhase : Cycleable<MoonPhase> {
    /**
     * The full moon phase
     */
    FULL_MOON,
    /**
     * The waning gibbous phase
     */
    WANING_GIBBOUS,
    /**
     * The last quarter phase
     */
    LAST_QUARTER,
    /**
     * The waning crescent phase
     */
    WANING_CRESCENT,
    /**
     * The new moon phase
     */
    NEW_MOON,
    /**
     * The waxing crescent phase
     */
    WAXING_CRESCENT,
    /**
     * The first quarter phase
     */
    FIRST_QUARTER,
    /**
     * The waxing gibbous phase
     */
    WAXING_GIBBOUS;

    /**
     * Cycles to the next moon phase.
     */
    override fun cycleNext() = rotate(1)

    /**
     * Cycles to the previous moon phase.
     */
    fun cyclePrevious() = rotate(1)

    /**
     * Rotates the moon phase enum to the next index.
     *
     * @param add the indexes to add
     * @return the new moon phase
     */
    fun rotate(add: Int): MoonPhase {
        val length = phases.size
        var index = (this.ordinal + add) % length
        if (index < 0) {
            index += length
        }
        return phases[index]
    }

    companion object {

        private val phases = values()
    }
}
