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
@file:JvmName("TitleFactory")
@file:Suppress("UNUSED_PARAMETER", "NOTHING_TO_INLINE", "FunctionName")

package org.lanternpowered.api.text.title

import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.emptyText
import kotlin.time.Duration
import kotlin.time.seconds
import kotlin.time.toJavaDuration

typealias Title = net.kyori.adventure.title.Title
typealias TitleTimes = net.kyori.adventure.title.Title.Times

/**
 * Constructs a new [TitleTimes].
 *
 * @param fadeIn The fade in time
 * @param stay The stay time
 * @param fadeOut The fade out time
 * @return The title times
 */
fun titleTimesOf(
        fadeIn: Duration = 0.5.seconds,
        stay: Duration = 3.5.seconds,
        fadeOut: Duration = 1.seconds
): TitleTimes = TitleTimes.of(fadeIn.toJavaDuration(), stay.toJavaDuration(), fadeOut.toJavaDuration())

/**
 * Constructs a new [Title].
 *
 * @param title The title (big)
 * @param subtitle The subtitle (smaller and below the primary one)
 * @param times The times
 * @return The title
 */
fun titleOf(
        title: Text,
        subtitle: Text = emptyText(),
        times: TitleTimes? = null
): Title = Title.of(title, subtitle, times)

/**
 * Gets a new title with the given times.
 *
 * @param fadeIn The fade in time
 * @param stay The stay time
 * @param fadeOut The fade out time
 */
fun Title.times(
        fadeIn: Duration = 0.5.seconds,
        stay: Duration = 3.5.seconds,
        fadeOut: Duration = 1.seconds
): Title = Title.of(this.title(), this.subtitle(), titleTimesOf(fadeIn, stay, fadeOut))
