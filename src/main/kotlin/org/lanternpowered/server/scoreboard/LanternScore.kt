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