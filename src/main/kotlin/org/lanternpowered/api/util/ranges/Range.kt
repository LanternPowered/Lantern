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
package org.lanternpowered.api.util.ranges

/**
 * Creates a range from this [Int] value to the specified [Double] value.
 */
operator fun Int.rangeTo(that: Double) = toDouble().rangeTo(that)

/**
 * Creates a range from this [Int] value to the specified [Double] value.
 */
operator fun Double.rangeTo(that: Int) = rangeTo(that.toDouble())

/**
 * Creates a range from this [Int] value to the specified [Float] value.
 */
operator fun Int.rangeTo(that: Float) = toFloat().rangeTo(that)

/**
 * Creates a range from this [Int] value to the specified [Double] value.
 */
operator fun Float.rangeTo(that: Int) = rangeTo(that.toFloat())
