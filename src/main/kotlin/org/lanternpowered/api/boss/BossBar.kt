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
@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package org.lanternpowered.api.boss

import org.lanternpowered.api.ext.*
import org.lanternpowered.api.text.Text

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
inline fun BossBar(name: Text, fn: BossBarBuilder.() -> Unit = {}): BossBar =
        // Apply a few defaults, so only the name is required
        BossBarBuilder().name(name).color(BossBarColors.PURPLE).overlay(BossBarOverlays.PROGRESS).apply(fn).build()

/**
 * Constructs a new [BossBarBuilder].
 *
 * @return The constructed boss bar builder
 */
inline fun BossBarBuilder(): BossBarBuilder = builderOf()
