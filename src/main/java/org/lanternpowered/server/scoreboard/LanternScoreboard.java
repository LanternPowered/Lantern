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
package org.lanternpowered.server.scoreboard;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardDisplayObjective;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardObjective;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardScore;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTeams;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.critieria.Criterion;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public class LanternScoreboard implements Scoreboard {

    private final Set<LanternPlayer> players = new HashSet<>();
    private final Map<String, Objective> objectives = new HashMap<>();
    private final Multimap<Criterion, Objective> objectivesByCriterion = HashMultimap.create();
    private final Map<DisplaySlot, Objective> objectivesInSlot = new HashMap<>();
    private final Map<String, Team> teams = new HashMap<>();

    void sendToPlayers(Supplier<List<Message>> messageSupplier) {
        if (!this.players.isEmpty()) {
            final List<Message> messages = messageSupplier.get();
            this.players.forEach(player -> player.getConnection().send(messages));
        }
    }

    public void removePlayer(LanternPlayer player) {
        this.players.remove(player);
        for (Objective objective : this.objectives.values()) {
            player.getConnection().send(new MessagePlayOutScoreboardObjective.Remove(objective.getName()));
        }
        for (Team team : this.teams.values()) {
            player.getConnection().send(new MessagePlayOutTeams.Remove(team.getName()));
        }
    }

    public void addPlayer(LanternPlayer player) {
        this.players.add(player);
        for (Objective objective : this.objectives.values()) {
            player.getConnection().send(this.createObjectiveInitMessages(objective));
        }
        for (Map.Entry<DisplaySlot, Objective> entry : this.objectivesInSlot.entrySet()) {
            player.getConnection().send(new MessagePlayOutScoreboardDisplayObjective(entry.getValue().getName(), entry.getKey()));
        }
        for (Team team : this.teams.values()) {
            player.getConnection().send(((LanternTeam) team).toCreateOrUpdateMessage(true));
        }
    }

    public Map<DisplaySlot, Objective> getObjectivesInSlot() {
        return ImmutableMap.copyOf(this.objectivesInSlot);
    }

    @Override
    public Optional<Objective> getObjective(String name) {
        return Optional.ofNullable(this.objectives.get(checkNotNull(name, "name")));
    }

    @Override
    public Optional<Objective> getObjective(DisplaySlot slot) {
        return Optional.ofNullable(this.objectivesInSlot.get(checkNotNull(slot, "slot")));
    }

    @Override
    public void addObjective(Objective objective) throws IllegalArgumentException {
        checkNotNull(objective, "objective");
        checkArgument(!this.objectives.containsKey(objective.getName()), "A score with the name %s already exists!",
                objective.getName());
        this.objectives.put(objective.getName(), objective);
        this.objectivesByCriterion.put(objective.getCriterion(), objective);
        ((LanternObjective) objective).addScoreboard(this);
        // Create the scoreboard objective on the client
        this.sendToPlayers(() -> this.createObjectiveInitMessages(objective));
    }

    private List<Message> createObjectiveInitMessages(Objective objective) {
        final List<Message> messages = new ArrayList<>();
        messages.add(new MessagePlayOutScoreboardObjective.Create(
                objective.getName(), ((LanternObjective) objective).getLegacyDisplayName(), objective.getDisplayMode()));
        for (Score score : ((LanternObjective) objective).scores.values()) {
            messages.add(new MessagePlayOutScoreboardScore.CreateOrUpdate(objective.getName(),
                    LanternTexts.toLegacy(score.getName()), score.getScore()));
        }
        return messages;
    }

    @Override
    public void updateDisplaySlot(@Nullable Objective objective, DisplaySlot displaySlot) throws IllegalStateException {
        checkNotNull(displaySlot, "displaySlot");
        if (objective == null) {
            final Objective oldObjective = this.objectivesInSlot.remove(displaySlot);
            if (oldObjective != null) {
                // Clear the display slot on the client
                this.sendToPlayers(() -> Collections.singletonList(
                        new MessagePlayOutScoreboardDisplayObjective(null, displaySlot)));
            }
        } else {
            checkState(this.objectives.containsValue(objective),
                    "The specified objective does not exist in this scoreboard.");
            if (this.objectivesInSlot.put(displaySlot, objective) != objective) {
                // Update the displayed objective on the client
                this.sendToPlayers(() -> Collections.singletonList(
                        new MessagePlayOutScoreboardDisplayObjective(objective.getName(), displaySlot)));
            }
        }
    }

    @Override
    public Set<Objective> getObjectivesByCriteria(Criterion criteria) {
        return ImmutableSet.copyOf(this.objectivesByCriterion.get(checkNotNull(criteria, "criteria")));
    }

    @Override
    public Set<Objective> getObjectives() {
        return ImmutableSet.copyOf(this.objectives.values());
    }

    @Override
    public void removeObjective(Objective objective) {
        if (this.objectives.remove(checkNotNull(objective, "objective").getName(), objective)) {
            ((LanternObjective) objective).removeScoreboard(this);
            this.objectivesByCriterion.remove(objective.getCriterion(), objective);
            final Iterator<Map.Entry<DisplaySlot, Objective>> it = this.objectivesInSlot.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry<DisplaySlot, Objective> entry = it.next();
                if (entry.getValue().equals(objective)) {
                    it.remove();
                }
            }
            this.sendToPlayers(() -> Collections.singletonList(new MessagePlayOutScoreboardObjective.Remove(objective.getName())));
        }
    }

    @Override
    public Set<Score> getScores() {
        final ImmutableSet.Builder<Score> scores = ImmutableSet.builder();
        for (Objective objective : this.objectives.values()) {
            scores.addAll(((LanternObjective) objective).scores.values());
        }
        return scores.build();
    }

    @Override
    public Set<Score> getScores(Text name) {
        checkNotNull(name, "name");
        final ImmutableSet.Builder<Score> scores = ImmutableSet.builder();
        for (Objective objective : this.objectives.values()) {
            objective.getScore(name).ifPresent(scores::add);
        }
        return scores.build();
    }

    @Override
    public void removeScores(Text name) {
        checkNotNull(name, "name");
        for (Objective objective : this.objectives.values()) {
            objective.removeScore(name);
        }
    }

    @Override
    public Optional<Team> getTeam(String teamName) {
        return Optional.ofNullable(this.teams.get(checkNotNull(teamName, "teamName")));
    }

    @Override
    public void registerTeam(Team team) throws IllegalArgumentException {
        checkNotNull(team, "team");
        checkArgument(!this.teams.containsKey(team.getName()), "A team with the name %s already exists!",
                team.getName());
        checkArgument(!team.getScoreboard().isPresent(), "The team is already attached to a scoreboard.");
        this.teams.put(team.getName(), team);
        ((LanternTeam) team).setScoreboard(this);
        this.sendToPlayers(() -> Collections.singletonList(((LanternTeam) team).toCreateOrUpdateMessage(true)));
    }

    @Override
    public Set<Team> getTeams() {
        return ImmutableSet.copyOf(this.teams.values());
    }

    @Override
    public Optional<Team> getMemberTeam(Text member) {
        checkNotNull(member, "member");
        for (Team team : this.teams.values()) {
            if (((LanternTeam) team).members.contains(member)) {
                return Optional.of(team);
            }
        }
        return Optional.empty();
    }

    void removeTeam(Team team) {
        this.teams.remove(team.getName());
    }
}
