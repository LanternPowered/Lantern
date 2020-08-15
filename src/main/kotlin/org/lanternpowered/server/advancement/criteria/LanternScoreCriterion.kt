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
import org.spongepowered.api.advancement.criteria.ScoreAdvancementCriterion
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger

class LanternScoreCriterion internal constructor(
        name: String, trigger: FilteredTrigger<*>?, private val goal: Int
) : LanternCriterion(name, trigger), ScoreAdvancementCriterion {

    /**
     * The internal ids of this [LanternScoreCriterion].
     */
    val ids: Array<String> = Array(this.goal) { index -> "$name&score_index=$index" }

    override fun getGoal(): Int = this.goal

    override fun toStringHelper(): ToStringHelper = super.toStringHelper()
            .add("goal", this.goal)
}
