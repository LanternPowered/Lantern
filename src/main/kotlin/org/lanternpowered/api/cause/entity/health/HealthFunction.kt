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
package org.lanternpowered.api.cause.entity.health

import org.spongepowered.api.event.cause.entity.damage.ModifierFunction
import java.util.function.DoubleUnaryOperator

data class HealthFunction(
        private val modifier: HealthModifier,
        private val function: DoubleUnaryOperator
) : ModifierFunction<HealthModifier> {

    /**
     * Creates a new [HealthFunction] with the provided
     * [HealthModifier]. The caveat is that the provided
     * [DoubleUnaryOperator] is by default going to provide `0`
     * healing modifications.
     *
     * @param modifier The damage modifier
     */
    constructor(modifier: HealthModifier) : this(modifier, DoubleUnaryOperator { 0.0 })

    /**
     * Gets the [HealthModifier] for this function.
     *
     * @return The health modifier
     */
    override fun getModifier(): HealthModifier = this.modifier

    /**
     * Gets the [DoubleUnaryOperator] for this function.
     *
     * @return The healing function
     */
    override fun getFunction(): DoubleUnaryOperator = this.function

    companion object {

        /**
         * Constructs a new health function.
         *
         * @param first The health modifier to use
         * @param second The unary operator to use
         * @return The resulting health function
         */
        fun of(first: HealthModifier, second: DoubleUnaryOperator): HealthFunction {
            return HealthFunction(first, second)
        }
    }
}
