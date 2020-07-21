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

/**
 * Constructs a new [Title].
 *
 * @param title The title (big)
 * @param subtitle The subtitle (smaller and below the primary one)
 * @param fadeIn The fade in duration
 * @param stay The stay duration
 * @param fadeOut The fade out duration
 * @return The title
 */
fun titleOf(
        title: Text,
        subtitle: Text = emptyText(),
        fadeIn: Duration = 1.seconds,
        stay: Duration = 3.seconds,
        fadeOut: Duration = 1.seconds
): Title = Title.of(title, subtitle, fadeIn.toJavaDuration(), stay.toJavaDuration(), fadeOut.toJavaDuration())
