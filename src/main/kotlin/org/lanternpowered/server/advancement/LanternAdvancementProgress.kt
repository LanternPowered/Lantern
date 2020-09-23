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
package org.lanternpowered.server.advancement

import it.unimi.dsi.fastutil.objects.Object2LongMap
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.server.event.LanternEventFactory
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.plus
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.text.toText
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.world.getGameRule
import org.lanternpowered.server.advancement.criteria.AbstractCriterion
import org.lanternpowered.server.advancement.criteria.EmptyCriterion
import org.lanternpowered.server.advancement.criteria.LanternAndCriterion
import org.lanternpowered.server.advancement.criteria.LanternCriterion
import org.lanternpowered.server.advancement.criteria.LanternOrCriterion
import org.lanternpowered.server.advancement.criteria.LanternScoreCriterion
import org.lanternpowered.server.advancement.criteria.progress.AbstractCriterionProgress
import org.lanternpowered.server.advancement.criteria.progress.LanternAndCriterionProgress
import org.lanternpowered.server.advancement.criteria.progress.LanternCriterionProgress
import org.lanternpowered.server.advancement.criteria.progress.LanternEmptyCriterionProgress
import org.lanternpowered.server.advancement.criteria.progress.LanternOrCriterionProgress
import org.lanternpowered.server.advancement.criteria.progress.LanternScoreCriterionProgress
import org.lanternpowered.server.entity.player.LanternPlayer
import org.lanternpowered.server.event.message.sendMessage
import org.spongepowered.api.advancement.Advancement
import org.spongepowered.api.advancement.AdvancementProgress
import org.spongepowered.api.advancement.criteria.AdvancementCriterion
import org.spongepowered.api.advancement.criteria.CriterionProgress
import org.spongepowered.api.advancement.criteria.ScoreAdvancementCriterion
import org.spongepowered.api.advancement.criteria.ScoreCriterionProgress
import org.lanternpowered.api.data.Keys
import org.spongepowered.api.world.gamerule.GameRules
import java.time.Instant
import java.util.HashMap
import java.util.Optional

class LanternAdvancementProgress internal constructor(
        val playerAdvancements: LanternPlayerAdvancements,
        private val advancement: LanternAdvancement
) : AdvancementProgress {

    private val progressByCriterion: MutableMap<AdvancementCriterion, AbstractCriterionProgress<*>> = HashMap()
    private var achievedState = false

    /**
     * Whether the progress of the advancement should be updated.
     */
    var dirtyProgress = false

    /**
     * Whether the visibility of the advancement should be updated.
     */
    var dirtyVisibility = false

    /**
     * Whether the advancement is currently visible
     */
    var visible = false

    /**
     * The player this advancement progress belongs to.
     */
    val player: LanternPlayer
        get() = this.playerAdvancements.player

    override fun getAdvancement(): Advancement = this.advancement

    fun cleanup() {
        for (progress in this.progressByCriterion.values)
            progress.detachTrigger()
    }

    fun loadProgress(progressMap: Map<String, Instant>) {
        for (progress in this.progressByCriterion.values)
            progress.loadProgress(progressMap)
        for (progress in this.progressByCriterion.values) {
            if (progress.achieved()) {
                progress.detachTrigger()
            } else {
                progress.attachTrigger()
            }
        }
        this.achievedState = this.achieved()
    }

    fun saveProgress(): Map<String, Instant> {
        val progressMap = HashMap<String, Instant>()
        for (progress in this.progressByCriterion.values)
            progress.saveProgress(progressMap)
        return progressMap
    }

    override fun get(criterion: ScoreAdvancementCriterion): Optional<ScoreCriterionProgress> =
            (this.progressByCriterion[criterion] as? ScoreCriterionProgress).asOptional()

    override fun get(criterion: AdvancementCriterion): Optional<CriterionProgress> =
            this.progressByCriterion[criterion].asOptional()

    override fun get(): Optional<Instant> =
            this.get(this.advancement.criterion).get().get()

    // TODO: Make this an optional in the API
    override fun grant(): Instant =
            this.get(this.advancement.criterion).get().grant()

    override fun revoke(): Optional<Instant> =
            this.get(this.advancement.criterion).get().revoke()

    fun invalidateAchievedState() {
        // Invalidate the achieved state of all the criterion progress
        for (progress in this.progressByCriterion.values)
            progress.invalidateAchievedState()
        // Get the new achieved state
        val achievedState = this.achieved()
        if (!this.achievedState && achievedState) {
            // The advancement got granted
            this.dirtyVisibility = true
            for (progress in this.progressByCriterion.values)
                progress.detachTrigger()

            val displayInfo = this.advancement.displayInfo.orNull()
            val playerName = this.player.get(Keys.DISPLAY_NAME).orElseGet { textOf(this.player.name) }
            val advancementText = this.advancement.toText()

            val sendMessage: Boolean
            val message: Text
            if (displayInfo != null) {
                sendMessage = this.player.world.getGameRule(GameRules.ANNOUNCE_ADVANCEMENTS) && displayInfo.doesAnnounceToChat()
                message = translatableTextOf("chat.type.advancement.${displayInfo.type.key.value}", playerName, advancementText)
            } else {
                sendMessage = false
                message = playerName + textOf(" achieved ") + advancementText
            }

            val cause = CauseStack.current().currentCause
            val instant = this.get().get()
            val audience = this.player.server.broadcastAudience

            val event = LanternEventFactory.createAdvancementEventGrant(cause, audience, audience,
                    message, message, this.advancement, this.player, instant, !sendMessage)
            EventManager.post(event)
            event.sendMessage()
        } else if (this.achievedState && !achievedState) {
            // The advancement got revoked
            this.dirtyVisibility = true
            for (progress in this.progressByCriterion.values) {
                if (!progress.achieved())
                    progress.attachTrigger()
            }
            val cause = CauseStack.current().currentCause
            val event = LanternEventFactory.createAdvancementEventRevoke(
                    cause, this.advancement, this.player)
            EventManager.post(event)
        }
        this.achievedState = achievedState
        // The progress should be updated
        this.dirtyProgress = true
        this.playerAdvancements.dirtyProgress.add(this)
    }

    fun collectProgress(): Object2LongMap<String> {
        val progress = Object2LongOpenHashMap<String>()
        val (criteria, _) = this.advancement.clientCriteria
        for (criterion in criteria)
            this.progressByCriterion[criterion]!!.fillProgress(progress)
        return progress
    }

    init {
        for (criterion in AbstractCriterion.getRecursiveCriteria(this.advancement.criterion)) {
            val progress = when (criterion) {
                is LanternAndCriterion -> LanternAndCriterionProgress(criterion, this)
                is LanternOrCriterion -> LanternOrCriterionProgress(criterion, this)
                is LanternScoreCriterion -> LanternScoreCriterionProgress(criterion, this)
                is LanternCriterion -> LanternCriterionProgress(criterion, this)
                is EmptyCriterion -> LanternEmptyCriterionProgress(criterion, this)
                else -> throw IllegalStateException("Unsupported criterion: $criterion")
            }
            progress.attachTrigger()
            this.progressByCriterion[criterion] = progress
        }
    }
}
