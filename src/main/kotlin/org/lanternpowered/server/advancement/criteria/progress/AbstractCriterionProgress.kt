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
import org.lanternpowered.server.advancement.LanternAdvancementProgress
import org.lanternpowered.server.advancement.criteria.AbstractCriterion
import org.spongepowered.api.advancement.criteria.CriterionProgress
import java.time.Instant
import java.util.Optional

abstract class AbstractCriterionProgress<T : AbstractCriterion> internal constructor(
        private val criterion: T,
        val advancementProgress: LanternAdvancementProgress
) : CriterionProgress {

    override fun getCriterion(): T = this.criterion

    // TODO: Return optional
    override fun grant(): Instant = grant { this.advancementProgress.invalidateAchievedState() }.orElse(Instant.MIN)
    abstract fun grant(invalidator: () -> Unit): Optional<Instant>

    override fun revoke(): Optional<Instant> = revoke { this.advancementProgress.invalidateAchievedState() }
    abstract fun revoke(invalidator: () -> Unit): Optional<Instant>

    open fun attachTrigger() {}
    open fun detachTrigger() {}
    open fun saveProgress(progress: MutableMap<String, Instant>) {}
    open fun loadProgress(progress: Map<String, Instant>) {}
    open fun fillProgress(progress: Object2LongMap<String>) {}
    open fun invalidateAchievedState() {}

    companion object {
        const val INVALID_TIME = -1
    }
}
