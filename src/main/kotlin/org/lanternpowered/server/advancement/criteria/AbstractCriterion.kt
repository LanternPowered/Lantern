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
package org.lanternpowered.server.advancement.criteria

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.collections.toImmutableSet
import org.spongepowered.api.advancement.criteria.AdvancementCriterion
import org.spongepowered.api.advancement.criteria.AndCriterion
import org.spongepowered.api.advancement.criteria.OperatorCriterion
import org.spongepowered.api.advancement.criteria.OrCriterion

abstract class AbstractCriterion internal constructor(private val name: String) : AdvancementCriterion {

    override fun getName(): String = this.name

    override fun and(vararg criteria: AdvancementCriterion): AdvancementCriterion =
            this.and(criteria.asList())

    override fun and(criteria: Iterable<AdvancementCriterion>): AdvancementCriterion =
            build(AndCriterion::class.java, sequenceOf(this) + criteria.asSequence(), ::LanternAndCriterion)

    override fun or(vararg criteria: AdvancementCriterion): AdvancementCriterion =
            this.or(criteria.asList())

    override fun or(criteria: Iterable<AdvancementCriterion>): AdvancementCriterion =
            build(OrCriterion::class.java, sequenceOf(this) + criteria.asSequence(), ::LanternOrCriterion)

    override fun toString(): String = this.toStringHelper().toString()

    open fun toStringHelper(): ToStringHelper = ToStringHelper(this)
            .add("name", this.name)

    companion object {

        @JvmStatic
        fun getRecursiveCriteria(criterion: AdvancementCriterion): Collection<AdvancementCriterion> =
                if (criterion is AbstractOperatorCriterion) criterion.recursiveChildren else listOf(criterion)

        fun build(
                type: Class<out OperatorCriterion>,
                criteria: Sequence<AdvancementCriterion>,
                function: (Set<AdvancementCriterion>) -> AdvancementCriterion
        ): AdvancementCriterion {
            val builder = mutableListOf<AdvancementCriterion>()
            for (criterion in criteria)
                this.build(type, criterion, builder)
            if (builder.isEmpty())
                return EmptyCriterion
            return if (builder.size == 1) builder[0] else function(builder.toImmutableSet())
        }

        private fun build(type: Class<out OperatorCriterion>, criterion: AdvancementCriterion, criteria: MutableList<AdvancementCriterion>) {
            if (criterion === EmptyCriterion)
                return
            if (type.isInstance(criterion)) {
                criteria.addAll((criterion as OperatorCriterion).criteria)
            } else {
                criteria.add(criterion)
            }
        }
    }
}
