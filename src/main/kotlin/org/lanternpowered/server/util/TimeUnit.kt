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
package org.lanternpowered.server.util

import org.lanternpowered.api.ext.*
import java.time.Duration
import java.time.temporal.Temporal
import java.time.temporal.TemporalUnit

/**
 * A time based [TemporalUnit].
 *
 * @param name The name of time unit
 * @param duration The duration
 */
class TimeUnit(private val name: String, private val duration: Duration) : TemporalUnit {

    override fun getDuration() = this.duration

    override fun isDurationEstimated() = false
    override fun isDateBased() = false
    override fun isTimeBased() = true

    override fun <R : Temporal> addTo(temporal: R, amount: Long) = temporal.plus(amount, this).uncheckedCast<R>()
    override fun between(temporal1Inclusive: Temporal, temporal2Exclusive: Temporal) = temporal1Inclusive.until(temporal2Exclusive, this)

    override fun toString() = this.name
}

//public static final TimeUnit MINECRAFT_TICKS = new TimeUnit("MinecraftTicks", Duration.ofMillis(50));
//public static final TimeUnit MINECRAFT_DAYS = new TimeUnit("MinecraftDays", MINECRAFT_TICKS.getDuration().multipliedBy(24000));
