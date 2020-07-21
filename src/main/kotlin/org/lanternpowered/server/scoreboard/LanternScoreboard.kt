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
import org.lanternpowered.api.scoreboard.ScoreboardObjective
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.collections.immutableSetBuilderOf
import org.lanternpowered.api.util.collections.toImmutableMap
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.server.entity.living.player.LanternPlayer
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutScoreboardDisplayObjective
import org.lanternpowered.server.network.vanilla.packet.type.play.ScoreboardObjectivePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ScoreboardScorePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.TeamPacket
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.scoreboard.Score
import org.spongepowered.api.scoreboard.Scoreboard
import org.spongepowered.api.scoreboard.Team
import org.spongepowered.api.scoreboard.criteria.Criterion
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot
import java.util.Optional

class LanternScoreboard : Scoreboard {

    private val players = mutableSetOf<LanternPlayer>()
    private val objectives = mutableMapOf<String, ScoreboardObjective>()
    private val objectivesByCriterion = HashMultimap.create<Criterion, ScoreboardObjective>()
    private val objectivesInSlot = mutableMapOf<DisplaySlot, ScoreboardObjective>()
    private val teams = mutableMapOf<String, Team>()

    fun sendToPlayers(packetSupplier: () -> List<Packet>) {
        if (this.players.isEmpty())
            return
        val messages = packetSupplier()
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

    private fun collectRemoveMessages(packets: MutableList<Packet>): List<Packet> {
        for (objective in objectives.values)
            packets.add(ScoreboardObjectivePacket.Remove(objective.name))
        for (team in teams.values)
            packets.add(TeamPacket.Remove(team.name))
        return packets
    }

    private fun collectAddMessages(packets: MutableList<Packet>): List<Packet> {
        for (objective in objectives.values)
            packets.addAll(createObjectiveInitMessages(objective))
        for ((key, value) in objectivesInSlot) {
            packets.add(PacketPlayOutScoreboardDisplayObjective(value.name, key))
        }
        for (team in teams.values) {
            packets.add((team as LanternTeam).toCreateMessage())
        }
        return packets
    }

    fun refreshPlayer(player: Player) {
        refreshPlayers(listOf(player))
    }

    private fun refreshPlayers(players: Iterable<Player>) {
        val messages = mutableListOf<Packet>()
        collectRemoveMessages(messages)
        collectAddMessages(messages)
        players.forEach { player -> (player as LanternPlayer).connection.send(messages) }
    }

    fun getObjectivesInSlot(): Map<DisplaySlot, ScoreboardObjective> = this.objectivesInSlot.toImmutableMap()

    override fun getObjective(name: String): Optional<ScoreboardObjective> = this.objectives[name].optional()
    override fun getObjective(slot: DisplaySlot): Optional<ScoreboardObjective> = this.objectivesInSlot[slot].optional()

    override fun addObjective(objective: ScoreboardObjective) {
        check(!this.objectives.containsKey(objective.name)) { "A score with the name ${objective.name} already exists!" }
        this.objectives[objective.name] = objective
        this.objectivesByCriterion.put(objective.criterion, objective)
        (objective as LanternObjective).scoreboards.add(this)
        // Create the scoreboard objective on the client
        sendToPlayers { createObjectiveInitMessages(objective) }
    }

    private fun createObjectiveInitMessages(objective: ScoreboardObjective): List<Packet> {
        val messages = mutableListOf<Packet>()
        messages.add(ScoreboardObjectivePacket.Create(
                objective.name, objective.displayName, objective.displayMode))
        for (score in (objective as LanternObjective).scores.values) {
            messages.add(ScoreboardScorePacket.CreateOrUpdate(
                    objective.name, score.name, score.score))
        }
        return messages
    }

    override fun updateDisplaySlot(objective: ScoreboardObjective?, displaySlot: DisplaySlot) {
        if (objective == null) {
            val oldObjective = this.objectivesInSlot.remove(displaySlot)
            if (oldObjective != null) {
                // Clear the display slot on the client
                sendToPlayers { listOf(PacketPlayOutScoreboardDisplayObjective(null, displaySlot)) }
            }
        } else {
            check(this.objectives.containsValue(objective)) { "The specified objective does not exist in this scoreboard." }
            if (this.objectivesInSlot.put(displaySlot, objective) !== objective) {
                // Update the displayed objective on the client
                sendToPlayers { listOf(PacketPlayOutScoreboardDisplayObjective(objective.name, displaySlot)) }
            }
        }
    }

    override fun getObjectivesByCriterion(criterion: Criterion): Set<ScoreboardObjective> = this.objectivesByCriterion[criterion].toImmutableSet()
    override fun getObjectives(): Set<ScoreboardObjective> = this.objectives.values.toImmutableSet()

    override fun removeObjective(objective: ScoreboardObjective) {
        if (this.objectives.remove(objective.name, objective)) {
            (objective as LanternObjective).scoreboards.remove(this)
            this.objectivesByCriterion.remove(objective.criterion, objective)
            this.objectivesInSlot.entries.removeIf { (_, value) -> value == objective }
            sendToPlayers { listOf(ScoreboardObjectivePacket.Remove(objective.name)) }
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
