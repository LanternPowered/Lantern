/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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
package org.lanternpowered.server.data.world;

/**
 * All the moon phases in minecraft.
 * 
 * TODO: This will be replaced once SpongeAPI has added api for this.
 */
public enum MoonPhase {
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
     * Gets the next moon state.
     * 
     * @return the moon state
     */
    public MoonPhase next() {
        return this.rotate(1);
    }

    /**
     * Gets the previous moon state.
     * 
     * @return the moon state
     */
    public MoonPhase previous() {
        return this.rotate(-1);
    }

    /**
     * Rotates the moon phase enum to the next index.
     * 
     * @param add the indexes to add
     * @return the new moon phase
     */
    private MoonPhase rotate(int add) {
        int index = this.ordinal();
        int size = values().length;

        index += add;
        while (index > size) {
            index -= size;
        }
        while (index < size) {
            index += size;
        }

        return values()[index];
    }

}
