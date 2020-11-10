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
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.server.event.LanternEventFactory
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.server.advancement.LanternAdvancementProgress
import org.lanternpowered.server.advancement.criteria.AbstractCriterion
import java.time.Instant
import java.util.Optional

class LanternCriterionProgress(
        criterion: AbstractCriterion, progress: LanternAdvancementProgress
) : LanternCriterionProgressBase<AbstractCriterion>(criterion, progress) {

    override fun grant(invalidator: () -> Unit): Optional<Instant> {
        if (this.achievingTime != null)
            return this.achievingTime.asOptional()
        val cause = CauseStack.currentCause
        val advancement = this.advancementProgress.advancement
        val event = LanternEventFactory.createCriterionEventGrant(
                cause, advancement, criterion, advancementProgress.player, Instant.now())
        EventManager.post(event)
        if (event.isCancelled)
            return emptyOptional()
        this.achievingTime = event.time
        detachTrigger()
        invalidator()
        return this.achievingTime.asOptional()
    }

    override fun revoke(invalidator: () -> Unit): Optional<Instant> {
        if (this.achievingTime == null)
            return emptyOptional()
        val cause = CauseStack.currentCause
        val advancement = this.advancementProgress.advancement
        val event = LanternEventFactory.createCriterionEventRevoke(
                cause, advancement, criterion, advancementProgress.player)
        EventManager.post(event)
        if (event.isCancelled)
            return emptyOptional()
        val achievingTime = achievingTime
        this.achievingTime = null
        attachTrigger()
        invalidator()
        return achievingTime.asOptional()
    }

    override fun fillProgress(progress: Object2LongMap<String>) {
        val achievingTime = this.achievingTime
        if (achievingTime != null)
            progress[this.criterion.name] = achievingTime.toEpochMilli()
    }

    override fun saveProgress(progress: MutableMap<String, Instant>) {
        val achievingTime = this.achievingTime
        if (achievingTime != null)
            progress[this.criterion.name] = achievingTime
    }

    override fun loadProgress(progress: Map<String, Instant>) {
        this.achievingTime = progress[this.criterion.name]
    }
}
