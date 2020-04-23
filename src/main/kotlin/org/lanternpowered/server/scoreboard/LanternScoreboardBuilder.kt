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

import org.lanternpowered.api.util.collections.toImmutableList
import org.spongepowered.api.scoreboard.Scoreboard
import org.spongepowered.api.scoreboard.Team
import org.spongepowered.api.scoreboard.objective.Objective

class LanternScoreboardBuilder : Scoreboard.Builder {

    private var objectives = emptyList<Objective>()
    private var teams= emptyList<Team>()

    override fun objectives(objectives: List<Objective>): Scoreboard.Builder = apply { this.objectives = objectives }
    override fun teams(teams: List<Team>): Scoreboard.Builder = apply { this.teams = teams }

    override fun from(value: Scoreboard): Scoreboard.Builder = apply {
        this.objectives = value.objectives.toImmutableList()
        this.teams = value.teams.toImmutableList()
    }

    override fun reset(): Scoreboard.Builder = apply {
        this.objectives = emptyList()
        this.teams = emptyList()
    }

    override fun build(): Scoreboard {
        val scoreboard = LanternScoreboard()
        this.objectives.forEach { objective -> scoreboard.addObjective(objective) }
        this.teams.forEach { team -> scoreboard.registerTeam(team) }
        return scoreboard
    }
}
