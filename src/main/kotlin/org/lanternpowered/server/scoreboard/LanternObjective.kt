/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.scoreboard

import org.lanternpowered.api.util.collections.toImmutableMap
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.server.network.message.Message
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardObjective
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardScore
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
        if (scoreboards.isEmpty())
            return
        val message = listOf(MessagePlayOutScoreboardObjective.Update(
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
        check(!scores.containsKey(score.name)) { "A score with the name ${score.name} already exists!" }
        scores[score.name] = score
        (score as LanternScore).addObjective(this)
        sendScoreToClient(score)
    }

    private fun sendScoreToClient(score: Score) {
        if (this.scoreboards.isEmpty())
            return
        val message = listOf(MessagePlayOutScoreboardScore.CreateOrUpdate(getName(), score.name, score.score))
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
        val messages = mutableMapOf<Objective, Message>()
        for (scoreboard in this.scoreboards) {
            (scoreboard as LanternScoreboard).sendToPlayers {
                listOf(messages.computeIfAbsent(this) {
                    MessagePlayOutScoreboardScore.Remove(getName(), score.name)
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