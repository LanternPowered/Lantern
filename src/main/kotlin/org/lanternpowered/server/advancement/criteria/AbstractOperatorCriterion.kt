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

import org.lanternpowered.api.util.collections.contentToString
import org.lanternpowered.api.util.collections.immutableSetBuilderOf
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.emptyOptional
import org.spongepowered.api.advancement.criteria.AdvancementCriterion
import org.spongepowered.api.advancement.criteria.OperatorCriterion
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger
import java.util.Optional

abstract class AbstractOperatorCriterion internal constructor(
        namePrefix: String, private val criteria: Collection<AdvancementCriterion>
) : AbstractCriterion(namePrefix + criteria.asSequence().map { it.name }.contentToString()), OperatorCriterion {

    private val _recursiveChildren by lazy { this.getAllChildrenCriteria(false) }
    private val _leafCriteria by lazy { this.getAllChildrenCriteria(true) }

    override fun getTrigger(): Optional<FilteredTrigger<*>> = emptyOptional()

    private fun getAllChildrenCriteria(onlyLeaves: Boolean): Collection<AdvancementCriterion> {
        val criteria = immutableSetBuilderOf<AdvancementCriterion>()
        if (!onlyLeaves)
            criteria.add(this)
        for (criterion in this.criteria) {
            if (criterion is AbstractOperatorCriterion) {
                criteria.addAll(criterion.getAllChildrenCriteria(onlyLeaves))
            } else {
                criteria.add(criterion)
            }
        }
        return criteria.build()
    }

    val recursiveChildren: Collection<AdvancementCriterion>
        get() = this._recursiveChildren

    override fun getCriteria(): Collection<AdvancementCriterion> = this.criteria
    override fun getLeafCriteria(): Collection<AdvancementCriterion> = this._leafCriteria

    override fun find(name: String): Collection<AdvancementCriterion> =
            this.recursiveChildren.asSequence()
                    .filter { criterion -> criterion.name == name }
                    .toImmutableSet()

    override fun findFirst(name: String): Optional<AdvancementCriterion> =
            this.recursiveChildren.asSequence()
                    .filter { criterion -> criterion.name == name }
                    .firstOrNull()
                    .asOptional()
}
