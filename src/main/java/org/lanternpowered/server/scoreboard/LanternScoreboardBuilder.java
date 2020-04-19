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
package org.lanternpowered.server.scoreboard;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.objective.Objective;

import java.util.ArrayList;
import java.util.List;

public class LanternScoreboardBuilder implements Scoreboard.Builder {

    private List<Objective> objectives = new ArrayList<>();
    private List<Team> teams = new ArrayList<>();

    @Override
    public Scoreboard.Builder objectives(List<Objective> objectives) {
        this.objectives = checkNotNull(objectives, "objectives");
        return this;
    }

    @Override
    public Scoreboard.Builder teams(List<Team> teams) {
        this.teams = checkNotNull(teams, "teams");
        return this;
    }

    @Override
    public Scoreboard.Builder from(Scoreboard value) {
        this.objectives = new ArrayList<>(value.getObjectives());
        this.teams = new ArrayList<>(value.getTeams());
        return this;
    }

    @Override
    public Scoreboard.Builder reset() {
        this.objectives = new ArrayList<>();
        this.teams = new ArrayList<>();
        return this;
    }

    @Override
    public Scoreboard build() throws IllegalStateException {
        LanternScoreboard scoreboard = new LanternScoreboard();
        this.objectives.forEach(scoreboard::addObjective);
        this.teams.forEach(scoreboard::registerTeam);
        return scoreboard;
    }
}
