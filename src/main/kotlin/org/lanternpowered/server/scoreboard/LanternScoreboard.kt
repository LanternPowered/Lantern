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
import org.lanternpowered.api.util.collections.immutableSetBuilderOf
import org.lanternpowered.api.util.collections.toImmutableMap
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.server.entity.living.player.LanternPlayer
import org.lanternpowered.server.network.message.Message
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardDisplayObjective
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardObjective
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardScore
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTeams
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.scoreboard.Score
import org.spongepowered.api.scoreboard.Scoreboard
import org.spongepowered.api.scoreboard.Team
import org.spongepowered.api.scoreboard.criteria.Criterion
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot
import org.spongepowered.api.scoreboard.objective.Objective
import org.spongepowered.api.text.Text
import java.util.Optional

class LanternScoreboard : Scoreboard {

    private val players = mutableSetOf<LanternPlayer>()
    private val objectives = mutableMapOf<String, Objective>()
    private val objectivesByCriterion = HashMultimap.create<Criterion, Objective>()
    private val objectivesInSlot = mutableMapOf<DisplaySlot, Objective>()
    private val teams = mutableMapOf<String, Team>()

    fun sendToPlayers(messageSupplier: () -> List<Message>) {
        if (this.players.isEmpty())
            return
        val messages = messageSupplier()
        this.players.forEach { player -> player.connection.send(messages) }
    }

    fun removePlayer(player: LanternPlayer) {
        if (this.players.remove(player))
            player.connection.send(collectRemoveMessages(mutableListOf()))
    }

    fun addPlayer(player: LanternPlayer) {
        if (this.players.add(player))
            player.connection.send(collectAddMessages(mutableListOf()))
    }

    private fun collectRemoveMessages(messages: MutableList<Message>): List<Message> {
        for (objective in objectives.values)
            messages.add(MessagePlayOutScoreboardObjective.Remove(objective.name))
        for (team in teams.values)
            messages.add(MessagePlayOutTeams.Remove(team.name))
        return messages
    }

    private fun collectAddMessages(messages: MutableList<Message>): List<Message> {
        for (objective in objectives.values)
            messages.addAll(createObjectiveInitMessages(objective))
        for ((key, value) in objectivesInSlot) {
            messages.add(MessagePlayOutScoreboardDisplayObjective(value.name, key))
        }
        for (team in teams.values) {
            messages.add((team as LanternTeam).toCreateMessage())
        }
        return messages
    }

    fun refreshPlayer(player: Player) {
        refreshPlayers(listOf(player))
    }

    private fun refreshPlayers(players: Iterable<Player>) {
        val messages = mutableListOf<Message>()
        collectRemoveMessages(messages)
        collectAddMessages(messages)
        players.forEach { player -> (player as LanternPlayer).connection.send(messages) }
    }

    fun getObjectivesInSlot(): Map<DisplaySlot, Objective> = this.objectivesInSlot.toImmutableMap()

    override fun getObjective(name: String): Optional<Objective> = this.objectives[name].optional()
    override fun getObjective(slot: DisplaySlot): Optional<Objective> = this.objectivesInSlot[slot].optional()

    override fun addObjective(objective: Objective) {
        check(!this.objectives.containsKey(objective.name)) { "A score with the name ${objective.name} already exists!" }
        this.objectives[objective.name] = objective
        this.objectivesByCriterion.put(objective.criterion, objective)
        (objective as LanternObjective).scoreboards.add(this)
        // Create the scoreboard objective on the client
        sendToPlayers { createObjectiveInitMessages(objective) }
    }

    private fun createObjectiveInitMessages(objective: Objective): List<Message> {
        val messages = mutableListOf<Message>()
        messages.add(MessagePlayOutScoreboardObjective.Create(
                objective.name, objective.displayName, objective.displayMode))
        for (score in (objective as LanternObjective).scores.values) {
            messages.add(MessagePlayOutScoreboardScore.CreateOrUpdate(
                    objective.name, score.name, score.score))
        }
        return messages
    }

    override fun updateDisplaySlot(objective: Objective?, displaySlot: DisplaySlot) {
        if (objective == null) {
            val oldObjective = this.objectivesInSlot.remove(displaySlot)
            if (oldObjective != null) {
                // Clear the display slot on the client
                sendToPlayers { listOf(MessagePlayOutScoreboardDisplayObjective(null, displaySlot)) }
            }
        } else {
            check(this.objectives.containsValue(objective)) { "The specified objective does not exist in this scoreboard." }
            if (this.objectivesInSlot.put(displaySlot, objective) !== objective) {
                // Update the displayed objective on the client
                sendToPlayers { listOf(MessagePlayOutScoreboardDisplayObjective(objective.name, displaySlot)) }
            }
        }
    }

    override fun getObjectivesByCriterion(criterion: Criterion): Set<Objective> = this.objectivesByCriterion[criterion].toImmutableSet()
    override fun getObjectives(): Set<Objective> = this.objectives.values.toImmutableSet()

    override fun removeObjective(objective: Objective) {
        if (this.objectives.remove(objective.name, objective)) {
            (objective as LanternObjective).scoreboards.remove(this)
            this.objectivesByCriterion.remove(objective.criterion, objective)
            this.objectivesInSlot.entries.removeIf { (_, value) -> value == objective }
            sendToPlayers { listOf(MessagePlayOutScoreboardObjective.Remove(objective.name)) }
        }
    }

    override fun getScores(): Set<Score> {
        val scores = immutableSetBuilderOf<Score>()
        for (objective in this.objectives.values)
            scores.addAll((objective as LanternObjective).scores.values)
        return scores.build()
    }

    override fun getScores(name: Text): Set<Score> {
        val scores = immutableSetBuilderOf<Score>()
        for (objective in this.objectives.values)
            objective.getScore(name).ifPresent { score -> scores.add(score) }
        return scores.build()
    }

    override fun removeScores(name: Text) {
        for (objective in this.objectives.values)
            objective.removeScore(name)
    }

    override fun getTeam(teamName: String): Optional<Team> = this.teams[teamName].optional()

    override fun registerTeam(team: Team) {
        check(!this.teams.containsKey(team.name)) { "A team with the name ${team.name} already exists!" }
        check(!team.scoreboard.isPresent) { "The team is already attached to a scoreboard." }
        this.teams[team.name] = team
        team as LanternTeam
        team.setScoreboard(this)
        sendToPlayers { listOf(team.toCreateMessage()) }
    }

    override fun getTeams(): Set<Team> = this.teams.values.toImmutableSet()

    override fun getMemberTeam(member: Text): Optional<Team> {
        for (team in this.teams.values) {
            if ((team as LanternTeam).hasMember(member)) {
                return team.optional()
            }
        }
        return emptyOptional()
    }

    fun removeTeam(team: Team) {
        this.teams.remove(team.name)
    }
}
