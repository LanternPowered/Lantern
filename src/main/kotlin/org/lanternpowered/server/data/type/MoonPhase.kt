/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
