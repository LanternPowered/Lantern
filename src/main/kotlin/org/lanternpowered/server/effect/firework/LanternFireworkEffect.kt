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
package org.lanternpowered.server.effect.firework

import org.lanternpowered.api.effect.firework.FireworkEffect
import org.lanternpowered.api.effect.firework.FireworkShape
import org.lanternpowered.api.util.Color
import org.lanternpowered.server.data.DataQueries
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.Queries

internal data class LanternFireworkEffect(
        private val flicker: Boolean,
        private val trails: Boolean,
        private val colors: List<Color>,
        private val fades: List<Color>,
        private val shape: FireworkShape
) : FireworkEffect {

    override fun flickers(): Boolean = this.flicker
    override fun hasTrail(): Boolean = this.trails
    override fun getColors(): List<Color> = this.colors
    override fun getFadeColors(): List<Color> = this.fades
    override fun getShape(): FireworkShape = this.shape

    override fun getContentVersion(): Int = 1
    override fun toContainer(): DataContainer {
        return DataContainer.createNew()
                .set(Queries.CONTENT_VERSION, contentVersion)
                .set(DataQueries.FIREWORK_SHAPE, this.shape)
                .set(DataQueries.FIREWORK_COLORS, this.colors)
                .set(DataQueries.FIREWORK_FADE_COLORS, this.fades)
                .set(DataQueries.FIREWORK_TRAILS, this.trails)
                .set(DataQueries.FIREWORK_FLICKERS, this.flicker)
    }
}
