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

import org.spongepowered.api.advancement.criteria.ScoreAdvancementCriterion
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger

class LanternScoreCriterionBuilder : AbstractCriterionBuilder<ScoreAdvancementCriterion, ScoreAdvancementCriterion.Builder>(),
        ScoreAdvancementCriterion.Builder {

    private var goal = 1

    override fun from(value: ScoreAdvancementCriterion): ScoreAdvancementCriterion.Builder = this.apply {
        this.goal = value.goal
        super.from(value)
    }

    override fun reset(): ScoreAdvancementCriterion.Builder = this.apply {
        this.goal = 1
        super.reset()
    }

    override fun goal(goal: Int): ScoreAdvancementCriterion.Builder = this.apply {
        check(goal > 0) { "The goal must be greater than zero." }
        this.goal = goal
    }

    override fun build(name: String, trigger: FilteredTrigger<*>?): ScoreAdvancementCriterion =
            LanternScoreCriterion(name, trigger, this.goal)
}
