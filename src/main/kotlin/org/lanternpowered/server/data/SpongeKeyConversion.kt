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
package org.lanternpowered.server.data

import org.lanternpowered.api.data.Key
import org.lanternpowered.api.data.Keys
import org.lanternpowered.api.util.math.times
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.LanternGame
import org.spongepowered.api.data.value.Value
import org.spongepowered.math.vector.Vector3d
import java.util.function.Supplier
import kotlin.time.Duration
import kotlin.time.seconds

private const val TPS = LanternGame.MINECRAFT_TICKS_PER_SECOND
private const val TPS_DOUBLE = TPS.toDouble()

fun GlobalKeyRegistry.registerSpongeKeyConversions() {
    convertDurationTicks(SpongeKeys.AGE, Keys.AGE)

    convertMetersPerTick(SpongeKeys.VELOCITY, Keys.VELOCITY)
    convertMetersPerTick(SpongeKeys.POTENTIAL_MAX_SPEED, Keys.POTENTIAL_MAX_SPEED)

    convert(SpongeKeys.FOOD_LEVEL, Keys.FOOD, { it.toInt() }, { it.toDouble() })
}

private val Int.ticks: Duration
    get() = (this.toDouble() / TPS_DOUBLE).seconds

private val Duration.inTicks: Int
    get() = (this.inMilliseconds.toInt() / TPS)

/**
 * Converts the meters per tick to meters per second.
 */
private val Double.inMetersPerSecond: Double
    get() = this * TPS_DOUBLE

/**
 * Converts the meters per second to meters per tick.
 */
private val Double.inMetersPerTick: Double
    get() = this / TPS_DOUBLE

/**
 * Converts the meters per tick to meters per second.
 */
private val Vector3d.inMetersPerSecond: Vector3d
    get() = this * TPS_DOUBLE

/**
 * Converts the meters per second to meters per tick.
 */
private val Vector3d.inMetersPerTick: Vector3d
    get() = this / TPS_DOUBLE

@JvmName("convertMetersPerTickVector")
private fun <SV : Value<Vector3d>, LV : Value<Vector3d>> GlobalKeyRegistry.convertMetersPerTick(
        spongeKey: Supplier<out Key<SV>>,
        lanternKey: Key<LV>
) = this.convert(spongeKey, lanternKey, { value -> value.inMetersPerTick }, { value -> value.inMetersPerSecond })

private fun <SV : Value<Double>, LV : Value<Double>> GlobalKeyRegistry.convertMetersPerTick(
        spongeKey: Supplier<out Key<SV>>,
        lanternKey: Key<LV>
) = this.convert(spongeKey, lanternKey, { value -> value.inMetersPerTick }, { value -> value.inMetersPerSecond })

private fun <SV : Value<Int>, LV : Value<Duration>> GlobalKeyRegistry.convertDurationTicks(
        spongeKey: Supplier<out Key<SV>>,
        lanternKey: Key<LV>
) = this.convert(spongeKey, lanternKey, { value -> value.inTicks }, { value -> value.ticks })

private fun <SV : Value<E>, LV : Value<E>, E : Any> GlobalKeyRegistry.identity(
        spongeKey: Supplier<out Key<SV>>,
        lanternKey: Key<LV>
) = this.convert(spongeKey, lanternKey, { it }, { it })

private fun <SV : Value<S>, S : Any, LV : Value<L>, L : Any> GlobalKeyRegistry.convert(
        spongeKey: Supplier<out Key<SV>>,
        lanternKey: Key<LV>,
        toSponge: (L) -> S,
        toLantern: (S) -> L
) = this.convert(spongeKey.get(), lanternKey, toSponge, toLantern)

private fun <SV : Value<S>, S : Any, LV : Value<L>, L : Any> GlobalKeyRegistry.convert(
        spongeKey: Key<SV>,
        lanternKey: Key<LV>,
        toSponge: (L) -> S,
        toLantern: (S) -> L
) {
    this.registerProvider(spongeKey) {
        get { this.get(lanternKey).map(toSponge).orNull() }
        offerFast { value -> (this as MutableDataHolder).offerFast(lanternKey, toLantern(value)) }
        removeFast { (this as MutableDataHolder).removeFast(lanternKey) }
    }
}
