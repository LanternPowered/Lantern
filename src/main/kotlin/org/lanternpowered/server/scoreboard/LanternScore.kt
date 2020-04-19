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

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.server.network.message.Message
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardScore
import org.spongepowered.api.scoreboard.Score
import org.spongepowered.api.scoreboard.Scoreboard
import org.spongepowered.api.scoreboard.objective.Objective
import org.spongepowered.api.text.Text

class LanternScore(private val name: Text) : Score {

    private val objectives = mutableSetOf<Objective>()
    private var score = 0

    override fun getName(): Text = this.name
    override fun getScore(): Int = this.score
    override fun getObjectives(): Set<Objective> = this.objectives.toImmutableSet()

    override fun setScore(score: Int) {
        if (this.score == score) {
            return
        }
        this.score = score
        val scoreboards: Multimap<Scoreboard, Objective> = HashMultimap.create()
        for (objective in this.objectives) {
            for (scoreboard in (objective as LanternObjective).scoreboards) {
                scoreboards.put(scoreboard, objective)
            }
        }
        if (!scoreboards.isEmpty) {
            val messages = mutableMapOf<Objective, Message>()
            for ((key, value) in scoreboards.entries()) {
                (key as LanternScoreboard).sendToPlayers {
                    listOf(messages.computeIfAbsent(value) { obj: Objective ->
                        MessagePlayOutScoreboardScore.CreateOrUpdate(obj.name, this.name, score)
                    })
                }
            }
        }
    }

    fun addObjective(objective: Objective) {
        this.objectives.add(objective)
    }

    fun removeObjective(objective: Objective) {
        this.objectives.remove(objective)
    }
}