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

import it.unimi.dsi.fastutil.objects.Object2LongMap
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.server.advancement.LanternAdvancementProgress
import org.lanternpowered.server.advancement.criteria.EmptyCriterion
import java.time.Instant
import java.util.Optional

class LanternEmptyCriterionProgress(
        criterion: EmptyCriterion, progress: LanternAdvancementProgress
) : AbstractCriterionProgress<EmptyCriterion>(criterion, progress) {

    private val now = Instant.now()

    override fun achieved(): Boolean = true
    override fun grant(invalidator: () -> Unit): Optional<Instant> = this.now.asOptional()
    override fun revoke(invalidator: () -> Unit): Optional<Instant> = emptyOptional()
    override fun get(): Optional<Instant> = this.now.asOptional()

    override fun fillProgress(progress: Object2LongMap<String>) {
        progress[this.criterion.name] = this.now.toEpochMilli()
    }
}
