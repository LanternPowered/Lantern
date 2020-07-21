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
package org.lanternpowered.server.scoreboard

import org.lanternpowered.api.util.collections.toImmutableMap
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.server.network.message.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutScoreboardObjective
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutScoreboardScore
import org.spongepowered.api.scoreboard.Score
import org.spongepowered.api.scoreboard.Scoreboard
import org.spongepowered.api.scoreboard.criteria.Criterion
import org.spongepowered.api.scoreboard.objective.Objective
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode
import org.spongepowered.api.text.Text

class LanternObjective internal constructor(
        private val name: String,
        private val criterion: Criterion,
        private var displayMode: ObjectiveDisplayMode,
        private var displayName: Text
) : Objective {

    @JvmField val scores = mutableMapOf<Text, Score>()
    @JvmField val scoreboards = mutableSetOf<Scoreboard>()

    override fun getName() = this.name
    override fun getDisplayName() = this.displayName
    override fun getCriterion() = this.criterion
    override fun getDisplayMode() = this.displayMode
    override fun getScores() = this.scores.toImmutableMap()
    override fun hasScore(name: Text) = this.scores.containsKey(name)
    override fun getScoreboards() = this.scoreboards.toImmutableSet()

    override fun setDisplayName(displayName: Text) {
        val length = displayName.toPlain().length
        check(length <= 32) { "Display name is $length characters long! It must be at most 32." }
        val update = this.displayName != displayName
        this.displayName = displayName
        if (update)
            sendObjectiveUpdate()
    }

    private fun sendObjectiveUpdate() {
        if (this.scoreboards.isEmpty())
            return
        val message = listOf(PacketPlayOutScoreboardObjective.Update(
                this.name, this.displayName, this.displayMode))
        for (scoreboard in scoreboards) {
            (scoreboard as LanternScoreboard).sendToPlayers { message }
        }
    }

    override fun setDisplayMode(displayMode: ObjectiveDisplayMode) {
        val update = displayMode != this.displayMode
        this.displayMode = displayMode
        if (update)
            sendObjectiveUpdate()
    }

    override fun addScore(score: Score) {
        check(!this.scores.containsKey(score.name)) { "A score with the name ${score.name} already exists!" }
        this.scores[score.name] = score
        (score as LanternScore).addObjective(this)
        sendScoreToClient(score)
    }

    private fun sendScoreToClient(score: Score) {
        if (this.scoreboards.isEmpty())
            return
        val message = listOf(PacketPlayOutScoreboardScore.CreateOrUpdate(getName(), score.name, score.score))
        for (scoreboard in this.scoreboards) {
            (scoreboard as LanternScoreboard).sendToPlayers { message }
        }
    }

    override fun getOrCreateScore(name: Text): Score = this.scores.computeIfAbsent(name) {
        val score = LanternScore(name)
        score.addObjective(this)
        sendScoreToClient(score)
        score
    }

    override fun removeScore(score: Score): Boolean {
        if (this.scores.remove(score.name, score)) {
            (score as LanternScore).removeObjective(this)
            updateClientAfterRemove(score)
            return true
        }
        return false
    }

    private fun updateClientAfterRemove(score: Score) {
        val messages = mutableMapOf<Objective, Packet>()
        for (scoreboard in this.scoreboards) {
            (scoreboard as LanternScoreboard).sendToPlayers {
                listOf(messages.computeIfAbsent(this) {
                    PacketPlayOutScoreboardScore.Remove(getName(), score.name)
                })
            }
        }
    }

    override fun removeScore(name: Text): Boolean {
        val score = this.scores.remove(name)
        if (score != null) {
            (score as LanternScore).removeObjective(this)
            updateClientAfterRemove(score)
            return true
        }
        return false
    }
}