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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.criteria.Criterion;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

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
        player.getConnection().send(collectRemoveMessages(new ArrayList<>()));
    }

    public void addPlayer(LanternPlayer player) {
        this.players.add(player);
        player.getConnection().send(collectAddMessages(new ArrayList<>()));
    }

    private List<Message> collectRemoveMessages(List<Message> messages) {
        for (Objective objective : this.objectives.values()) {
            messages.add(new MessagePlayOutScoreboardObjective.Remove(objective.getName()));
        }
        for (Team team : this.teams.values()) {
            messages.add(new MessagePlayOutTeams.Remove(team.getName()));
        }
        return messages;
    }

    private List<Message> collectAddMessages(List<Message> messages) {
        for (Objective objective : this.objectives.values()) {
            messages.addAll(createObjectiveInitMessages(objective));
        }
        for (Map.Entry<DisplaySlot, Objective> entry : this.objectivesInSlot.entrySet()) {
            messages.add(new MessagePlayOutScoreboardDisplayObjective(entry.getValue().getName(), entry.getKey()));
        }
        for (Team team : this.teams.values()) {
            messages.add(((LanternTeam) team).toCreateMessage());
        }
        return messages;
    }

    public void refreshPlayer(Player player) {
        refreshPlayers(Collections.singletonList(player));
    }

    public void refreshPlayers(Iterable<Player> players) {
        final List<Message> messages = new ArrayList<>();
        collectRemoveMessages(messages);
        collectAddMessages(messages);
        players.forEach(player -> ((LanternPlayer) player).getConnection().send(messages));
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
        ((LanternObjective) objective).scoreboards.add(this);
        // Create the scoreboard objective on the client
        this.sendToPlayers(() -> this.createObjectiveInitMessages(objective));
    }

    private List<Message> createObjectiveInitMessages(Objective objective) {
        final List<Message> messages = new ArrayList<>();
        messages.add(new MessagePlayOutScoreboardObjective.Create(
                objective.getName(), objective.getDisplayName(), objective.getDisplayMode()));
        for (Score score : ((LanternObjective) objective).scores.values()) {
            messages.add(new MessagePlayOutScoreboardScore.CreateOrUpdate(
                    objective.getName(),score.getName(), score.getScore()));
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
    public Set<Objective> getObjectivesByCriterion(Criterion criteria) {
        return ImmutableSet.copyOf(this.objectivesByCriterion.get(checkNotNull(criteria, "criteria")));
    }

    @Override
    public Set<Objective> getObjectives() {
        return ImmutableSet.copyOf(this.objectives.values());
    }

    @Override
    public void removeObjective(Objective objective) {
        checkNotNull(objective, "objective");
        if (this.objectives.remove(objective.getName(), objective)) {
            ((LanternObjective) objective).scoreboards.remove(this);
            this.objectivesByCriterion.remove(objective.getCriterion(), objective);
            this.objectivesInSlot.entrySet().removeIf(entry -> entry.getValue().equals(objective));
            sendToPlayers(() -> Collections.singletonList(new MessagePlayOutScoreboardObjective.Remove(objective.getName())));
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
        final LanternTeam lanternTeam = (LanternTeam) team;
        lanternTeam.setScoreboard(this);
        sendToPlayers(() -> Collections.singletonList(lanternTeam.toCreateMessage()));
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
