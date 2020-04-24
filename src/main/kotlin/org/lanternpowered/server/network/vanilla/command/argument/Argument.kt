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
package org.lanternpowered.server.network.vanilla.command.argument

interface Argument

object EmptyArgument : Argument

interface NumberArgument<N : Number> : Argument {
    val min: N?
    val max: N?
}

data class DoubleArgument(override val min: Double?, override val max: Double?) : NumberArgument<Double>

data class EntityArgument(val allowMultipleEntities: Boolean, val allowOnlyPlayers: Boolean) : Argument

data class FloatArgument(override val min: Float?, override val max: Float?) : NumberArgument<Float>

data class IntArgument(override val min: Int?, override val max: Int?) : NumberArgument<Int>

data class LongArgument(override val min: Long?, override val max: Long?) : NumberArgument<Long>

data class ScoreHolderArgument(val allowMultipleEntities: Boolean, val hasUnknownFlag: Boolean) : Argument

data class StringArgument(val type: Type) : Argument {

    enum class Type {
        SINGLE_WORD, QUOTABLE_PHRASE, GREEDY_PHRASE
    }
}
