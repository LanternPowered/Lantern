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
@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package org.lanternpowered.api.boss

import org.lanternpowered.api.text.Text
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// Use ServerBossBar instead, normal BossBar will never be used in Lantern
typealias BossBar = org.spongepowered.api.boss.ServerBossBar
typealias BossBarBuilder = org.spongepowered.api.boss.ServerBossBar.Builder
typealias BossBarColor = org.spongepowered.api.boss.BossBarColor
typealias BossBarColors = org.spongepowered.api.boss.BossBarColors
typealias BossBarOverlay = org.spongepowered.api.boss.BossBarOverlay
typealias BossBarOverlays = org.spongepowered.api.boss.BossBarOverlays

/**
 * Constructs a new [BossBar] with the given name and builder function.
 *
 * @param name The name
 * @param fn The builder function
 * @return The constructed boss bar
 */
inline fun bossBar(name: Text, fn: BossBarBuilder.() -> Unit = {}): BossBar {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    // Apply a few defaults, so only the name is required
    return BossBar.builder().name(name).color(BossBarColors.PURPLE).overlay(BossBarOverlays.PROGRESS).apply(fn).build()
}

/**
 * Whether fog should be created.
 */
inline var BossBar.createFog: Boolean
    get() = shouldCreateFog()
    set(value) { setCreateFog(value) }

/**
 * Whether the sky should darken.
 */
inline var BossBar.darkenSky: Boolean
    get() = shouldDarkenSky()
    set(value) { setDarkenSky(value) }

/**
 * Whether end boss music should be played.
 */
inline var BossBar.playEndBossMusic: Boolean
    get() = shouldPlayEndBossMusic()
    set(value) { setPlayEndBossMusic(value) }
