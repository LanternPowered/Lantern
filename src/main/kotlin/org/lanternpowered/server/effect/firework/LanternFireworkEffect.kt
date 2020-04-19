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
