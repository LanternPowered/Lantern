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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.util.text

import org.apache.commons.lang3.StringUtils

/**
 * Normalizes the spaces of this [String].
 */
inline fun String.normalizeSpaces(): String = StringUtils.normalizeSpace(this)

/**
 * Normalizes the spaces of this [CharSequence].
 */
inline fun CharSequence.normalizeSpaces(): CharSequence = StringUtils.normalizeSpace(toString())
