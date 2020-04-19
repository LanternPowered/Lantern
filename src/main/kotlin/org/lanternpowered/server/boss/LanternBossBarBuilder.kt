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
package org.lanternpowered.server.boss

import org.lanternpowered.api.boss.BossBar
import org.lanternpowered.api.boss.BossBarBuilder
import org.lanternpowered.api.boss.BossBarColor
import org.lanternpowered.api.boss.BossBarColors
import org.lanternpowered.api.boss.BossBarOverlay
import org.lanternpowered.api.boss.BossBarOverlays
import org.lanternpowered.api.boss.createFog
import org.lanternpowered.api.boss.darkenSky
import org.lanternpowered.api.boss.playEndBossMusic
import org.lanternpowered.api.text.Text

import java.util.UUID

class LanternBossBarBuilder : BossBarBuilder {

    private var name: Text? = null
    private var percent: Float = 0f
    private lateinit var color: BossBarColor
    private lateinit var overlay: BossBarOverlay
    private var darkenSky: Boolean = false
    private var playEndBossMusic: Boolean = false
    private var createFog: Boolean = false
    private var visible: Boolean = false

    init {
        reset()
    }

    override fun percent(percent: Float): BossBarBuilder = apply {
        check(percent in 0f..1f) { "Percent must be between 0 and 1, but $percent is not" }
        this.percent = percent
    }

    override fun name(name: Text): BossBarBuilder = apply { this.name = name }
    override fun color(color: BossBarColor): BossBarBuilder = apply { this.color = color }
    override fun overlay(overlay: BossBarOverlay): BossBarBuilder = apply { this.overlay = overlay }
    override fun darkenSky(darkenSky: Boolean): BossBarBuilder = apply { this.darkenSky = darkenSky }
    override fun playEndBossMusic(playEndBossMusic: Boolean): BossBarBuilder = apply { this.playEndBossMusic = playEndBossMusic }
    override fun createFog(createFog: Boolean): BossBarBuilder = apply { this.createFog = createFog }
    override fun visible(visible: Boolean): BossBarBuilder = apply { this.visible = visible }

    override fun build(): LanternBossBar {
        val name = checkNotNull(this.name) { "The name is not set" }
        return LanternBossBar(UUID.randomUUID(), name, this.color, this.overlay, this.percent,
                this.darkenSky, this.playEndBossMusic, this.createFog, this.visible)
    }

    override fun from(value: BossBar): BossBarBuilder = apply {
        this.name = value.name
        this.percent = value.percent
        this.visible = value.isVisible
        this.color = value.color
        this.overlay = value.overlay
        this.createFog = value.createFog
        this.darkenSky = value.darkenSky
        this.playEndBossMusic = value.playEndBossMusic
    }

    override fun reset(): BossBarBuilder = apply {
        this.name = null
        this.percent = 0f
        this.visible = true
        this.color = BossBarColors.WHITE
        this.overlay = BossBarOverlays.PROGRESS
        this.createFog = false
        this.darkenSky = false
        this.playEndBossMusic = false
    }
}
