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
package org.lanternpowered.api.cause.entity.health.source

import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.cause.entity.health.HealingType
import org.lanternpowered.api.registry.BaseBuilder
import org.lanternpowered.api.registry.builderOf
import org.lanternpowered.api.world.difficulty.Difficulty
import java.util.function.Supplier

private typealias HealingSourceBuilder = HealingSource.Builder

/**
 * Constructs a new [HealingSource].
 */
fun healingSourceOf(type: Supplier<out HealingType>): HealingSource =
        builderOf<HealingSource.Builder>().type(type).build()

/**
 * Constructs a new [HealingSource].
 */
fun healingSourceOf(type: HealingType): HealingSource =
        builderOf<HealingSource.Builder>().type(type).build()

/**
 * Constructs a new [HealingSource].
 */
fun healingSource(block: HealingSourceBuilder.() -> Unit): HealingSource =
        builderOf<HealingSource.Builder>().apply(block).build()

/**
 * Represents a [Cause] for damage on the [Entity] being
 * healed. This will help inform what type of healing
 */
interface HealingSource {

    /**
     * The [HealingType] for this source.
     */
    val healingType: HealingType

    /**
     * Whether this [HealingSource]'s healing amount is scaled by [Difficulty].
     */
    val isDifficultyScaled: Boolean

    /**
     * Whether this [HealingSource] is considered to be magical
     * healing, such as potions, or other sources.
     */
    val isMagic: Boolean

    /**
     * A builder to build [HealingSource] specifically.
     */
    interface Builder : BaseBuilder<HealingSource, Builder> {

        /**
         * Sets for the built [HealingSource] to have scaled with
         * difficulty, usually meaning that the amount is scaled.
         *
         * @return This builder, for chaining
         */
        fun scalesWithDifficulty(): Builder

        /**
         * Sets that the built [HealingSource] to have been a "magical"
         * source.
         *
         * @return This builder, for chaining
         */
        fun magical(): Builder

        /**
         * Sets the [HealingType].
         *
         * @param type The healing type
         * @return This builder, for chaining
         */
        fun type(type: Supplier<out HealingType>): Builder = type(type.get())

        /**
         * Sets the [HealingType].
         *
         * @param type The healing type
         * @return This builder, for chaining
         */
        fun type(type: HealingType): Builder

        /**
         * Builds the healing source.
         */
        fun build(): HealingSource
    }

    companion object {

        /**
         * Creates a new [Builder] to construct a new [HealingSource].
         *
         * @return A new builder
         */
        fun builder(): Builder = builderOf()
    }
}
