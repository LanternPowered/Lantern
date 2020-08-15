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

import org.spongepowered.api.advancement.criteria.AdvancementCriterion
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger

class LanternCriterionBuilder : AbstractCriterionBuilder<AdvancementCriterion, AdvancementCriterion.Builder>(), AdvancementCriterion.Builder {
    override fun build(name: String, trigger: FilteredTrigger<*>?): AdvancementCriterion = LanternCriterion(name, trigger)
}
