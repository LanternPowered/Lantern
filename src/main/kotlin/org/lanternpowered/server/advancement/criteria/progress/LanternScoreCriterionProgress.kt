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
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.server.advancement.LanternAdvancementProgress
import org.lanternpowered.server.advancement.criteria.LanternScoreCriterion
import org.spongepowered.api.advancement.criteria.ScoreCriterionProgress
import org.spongepowered.api.event.advancement.CriterionEvent
import java.time.Instant
import java.util.Optional
import kotlin.math.max
import kotlin.math.min

class LanternScoreCriterionProgress(
        criterion: LanternScoreCriterion, progress: LanternAdvancementProgress
) : LanternCriterionProgressBase<LanternScoreCriterion>(criterion, progress), ScoreCriterionProgress {

    private var score = 0

    override fun grant(invalidator: () -> Unit): Optional<Instant> = set(this.goal, invalidator)
    override fun revoke(invalidator: () -> Unit): Optional<Instant> = set(0, invalidator)

    override fun getScore(): Int = this.score

    override fun set(score: Int): Optional<Instant> = set(score) { this.advancementProgress.invalidateAchievedState() }

    operator fun set(score: Int, invalidator: () -> Unit): Optional<Instant> {
        check(score >= 0) { "Score to set may not be negative" }
        check(score <= this.goal) { "Score to set may not be greater than the goal" }
        if (score == this.score)
            return this.get()
        val cause = CauseStack.current().currentCause
        val advancement = advancementProgress.advancement
        val player = this.advancementProgress.player
        val event = when {
            score == this.goal -> LanternEventFactory.createCriterionEventScoreGrant(
                    cause, advancement, this.criterion, player, Instant.now(), score, this.score)
            this.score == this.goal -> LanternEventFactory.createCriterionEventScoreRevoke(
                    cause, advancement, this.criterion, player, score, this.score)
            else -> LanternEventFactory.createCriterionEventScoreChange(
                    cause, advancement, this.criterion, player, score, this.score)
        }
        EventManager.post(event)
        if (event.isCancelled)
            return this.get()
        this.score = score
        if (event is CriterionEvent.Grant) {
            this.achievingTime = (event as CriterionEvent.Grant).time
            this.detachTrigger()
        } else if (event is CriterionEvent.Revoke) {
            this.achievingTime = null
            this.attachTrigger()
        }
        invalidator()
        return this.achievingTime.asOptional()
    }

    override fun add(score: Int): Optional<Instant> {
        check(score >= 0) { "Score to add may not be negative" }
        return this.set(min(this.score + score, this.goal))
    }

    override fun remove(score: Int): Optional<Instant> {
        check(score >= 0) { "Score to remove may not be negative" }
        return this.set(max(this.score - score, this.goal))
    }

    override fun fillProgress(progress: Object2LongMap<String>) {
        var now = -1L
        for (i in 0 until this.score) {
            val achievingTime = this.achievingTime
            val time = if (achievingTime != null) {
                achievingTime.toEpochMilli()
            } else {
                if (now == -1L)
                    now = System.currentTimeMillis()
                now
            }
            progress[this.criterion.ids[i]] = time
        }
    }

    override fun saveProgress(progress: MutableMap<String, Instant>) {
        var now: Instant? = null
        for (i in 0 until score) {
            val achievingTime = this.achievingTime
            val time = if (achievingTime != null) {
                achievingTime
            } else {
                if (now == null)
                    now = Instant.now()
                now!!
            }
            progress[this.criterion.ids[i]] = time
        }
        val achievingTime = this.achievingTime
        if (achievingTime != null)
            progress[this.criterion.name] = achievingTime
    }

    override fun loadProgress(progress: Map<String, Instant>) {
        this.achievingTime = progress[this.criterion.name]
        if (this.achievingTime == null) {
            this.score = 0
            var lastTime: Instant? = null
            for (i in 0 until this.goal) {
                val time = progress[this.criterion.ids[i]]
                if (time != null) {
                    this.score++
                    if (lastTime == null || time.isAfter(lastTime))
                        lastTime = time
                }
            }
            if (this.score == this.goal) {
                this.achievingTime = lastTime
            }
        } else {
            this.score = this.goal
        }
    }
}
