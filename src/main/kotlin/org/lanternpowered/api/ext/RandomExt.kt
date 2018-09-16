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
package org.lanternpowered.api.ext

import com.flowpowered.math.GenericMath
import java.util.Random
import java.util.concurrent.ThreadLocalRandom

/**
 * The global [Random] instance. Doesn't supports
 * its seed to be modified.
 */
val random: Random = ThreadLocalRandom.current()

/**
 * Gets a random value between [ClosedRange.start] and [ClosedRange.endInclusive] (inclusive).
 */
fun Random.nextDouble(range: ClosedRange<Int>)
        = range.start + nextInt(range.endInclusive + 1 - range.start)

/**
 * Gets a random double value between [ClosedRange.start] and [ClosedRange.endInclusive] (inclusive).
 */
fun Random.nextDouble(range: ClosedRange<Double>): Double {
    val value = range.start + nextDouble() * (range.endInclusive - range.start + GenericMath.DBL_EPSILON)
    return if (value > range.endInclusive) range.endInclusive else value
}

/**
 * Gets a random double value between [ClosedRange.start] and [ClosedRange.endInclusive] (inclusive).
 */
fun Random.nextDouble(range: ClosedRange<Float>): Float {
    val value = range.start + nextFloat() * (range.endInclusive - range.start + GenericMath.FLT_EPSILON)
    return if (value > range.endInclusive) range.endInclusive else value
}
