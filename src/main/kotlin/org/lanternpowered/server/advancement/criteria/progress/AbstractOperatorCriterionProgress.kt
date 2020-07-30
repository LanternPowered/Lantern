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
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.server.advancement.LanternAdvancementProgress
import org.lanternpowered.server.advancement.criteria.AbstractOperatorCriterion
import java.time.Instant
import java.util.Optional

abstract class AbstractOperatorCriterionProgress<T : AbstractOperatorCriterion> internal constructor(
        criterion: T, progress: LanternAdvancementProgress
) : AbstractCriterionProgress<T>(criterion, progress) {

    private var cachedAchievedState: Optional<Instant>? = null
    private val lastAchievingTime: Instant? = null

    override fun get(): Optional<Instant> {
        if (this.cachedAchievedState == null) {
            this.cachedAchievedState = get0().optional()
        }
        return this.cachedAchievedState!!
    }

    abstract fun get0(): Instant?

    override fun grant(invalidator: () -> Unit): Optional<Instant> {
        var time: Instant? = null
        val change = BooleanArray(1)
        for (criterion in this.criterion.criteria) {
            val time1 = (this.advancementProgress[criterion].get() as AbstractCriterionProgress<*>).grant { change[0] = true }
            if (!time1.isPresent) {
                time = Instant.MAX
            } else if (time == null || time1.get().isAfter(time)) {
                time = time1.get()
            }
        }
        if (change[0])
            invalidator()
        time!!
        return if (time === Instant.MAX) {
            // Somebody prevented a criterion to be granted
            emptyOptional()
        } else time.optional()
    }

    override fun revoke(invalidator: () -> Unit): Optional<Instant> {
        val previousState = get()
        val change = BooleanArray(1)
        for (criterion in this.criterion.criteria) {
            (this.advancementProgress[criterion].get() as AbstractCriterionProgress<*>).revoke { change[0] = true }
        }
        if (change[0])
            invalidator()
        return previousState
    }

    override fun invalidateAchievedState() {
        this.cachedAchievedState = null
    }

    override fun fillProgress(progress: Object2LongMap<String>) {
        val achievingTime = get().orElse(null)
        progress[this.criterion.name] = achievingTime?.toEpochMilli() ?: INVALID_TIME.toLong()
    }
}
