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
package org.lanternpowered.server.advancement.criteria.progress

import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.advancement.LanternAdvancementProgress
import org.lanternpowered.server.advancement.criteria.LanternAndCriterion
import java.time.Instant
import java.util.Optional

class LanternAndCriterionProgress(
        criterion: LanternAndCriterion, progress: LanternAdvancementProgress
) : AbstractOperatorCriterionProgress<LanternAndCriterion>(criterion, progress) {

    override fun get0(): Instant? {
        var time: Instant? = null
        for (criterion in this.criterion.criteria) {
            val time1 = this.advancementProgress[criterion].get().get().orNull()
            if (time1 == null) {
                return null
            } else if (time == null || time1.isAfter(time)) {
                time = time1
            }
        }
        return time
    }
}
