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

typealias BossBar = net.kyori.adventure.bossbar.BossBar
typealias BossBarColor = net.kyori.adventure.bossbar.BossBar.Color
typealias BossBarFlag = net.kyori.adventure.bossbar.BossBar.Flag
typealias BossBarOverlay = net.kyori.adventure.bossbar.BossBar.Overlay

/**
 * Constructs a new [BossBar] with the given name and builder function.
 *
 * @param name The name
 * @param percent The percentage
 * @param color The color
 * @param overlay The overlay
 * @param flags The flags to apply
 * @return The boss bar
 */
inline fun bossBarOf(
        name: Text,
        percent: Double = 1.0,
        color: BossBarColor = BossBarColor.PURPLE,
        overlay: BossBarOverlay = BossBarOverlay.PROGRESS,
        flags: Set<BossBarFlag> = emptySet()
): BossBar = BossBar.of(name, percent.toFloat(), color, overlay, flags)
