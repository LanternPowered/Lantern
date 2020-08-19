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

import org.lanternpowered.api.registry.builderOf
import org.spongepowered.api.entity.Entity
import org.lanternpowered.api.cause.Cause
import org.spongepowered.api.util.ResettableBuilder
import java.util.function.DoubleUnaryOperator

/**
 * Represents a modifier that will apply a function on a damage value to deal
 * towards an entity such that the raw damage is the input of a
 * [DoubleUnaryOperator] such that the output will be the final damage
 * applied to the [Entity].
 */
interface HealthModifier {

    /**
     * Gets the [HealthModifierType] for this [HealthModifier].
     *
     * @return The damage modifier type
     */
    val type: HealthModifierType

    /**
     * Gets the cause of this [HealthModifier].
     *
     * @return The cause of this damage modifier
     */
    val cause: Cause

    /**
     * A builder that creates [HealthModifier]s, for use in both plugin
     * and implementation requirements.
     */
    class Builder private constructor() : ResettableBuilder<HealthModifier, Builder> {

        var type: HealthModifierType? = null
        var cause: Cause? = null

        /**
         * Sets the [HealthModifierType] for the [HealthModifier] to build.
         *
         * @param type The health modifier type
         * @return This builder, for chaining
         */
        fun type(type: HealthModifierType): Builder = apply {
            this.type = type
        }

        /**
         * Sets the [Cause] for the [HealthModifier] to build.
         *
         * @param cause The cause for the health modifier
         * @return This builder, for chaining
         */
        fun cause(cause: Cause): Builder = apply {
            this.cause = cause
        }

        /**
         * Creates a new [HealthModifier] with this builder's provided
         * [Cause] and [HealthModifierType].
         *
         * @return The newly created health modifier
         */
        fun build(): HealthModifier {
            val type = checkNotNull(this.type) { "The HealthModifierType must not be null!" }
            val cause = checkNotNull(this.cause) { "The cause for the HealthModifier must not be null!" }
            return ImplementedHealthModifier(type, cause)
        }

        fun from(value: HealthModifier): Builder = apply {
            reset()
            this.type = value.type
            this.cause = value.cause
        }

        override fun reset(): Builder = apply {
            this.type = null
            this.cause = null
        }

        private data class ImplementedHealthModifier(
                override val type: HealthModifierType,
                override val cause: Cause
        ) : HealthModifier

        companion object {

            /**
             * Creates a new [Builder].
             *
             * @return The new builder instance
             */
            fun builder(): Builder {
                return Builder()
            }
        }
    }

    companion object {

        /**
         * Creates a new [Builder] for constructing new [HealthModifier]s.
         *
         * @return A new builder
         */
        fun builder(): Builder = builderOf()
    }
}
