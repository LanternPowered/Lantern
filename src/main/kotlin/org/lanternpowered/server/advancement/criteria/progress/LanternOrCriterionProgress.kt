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
import org.lanternpowered.server.advancement.criteria.LanternOrCriterion
import java.time.Instant

class LanternOrCriterionProgress(
        criterion: LanternOrCriterion, progress: LanternAdvancementProgress
) : AbstractOperatorCriterionProgress<LanternOrCriterion>(criterion, progress) {

    override fun get0(): Instant? = this.criterion.criteria.asSequence()
            .map { criterion -> this.advancementProgress[criterion].get().get().orNull() }
            .filterNotNull()
            .min()
}
