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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardObjective;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardScore;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criterion;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LanternObjective implements Objective {

    private final String name;
    private final Criterion criterion;
    final Map<Text, Score> scores = new HashMap<>();
    final Set<Scoreboard> scoreboards = new HashSet<>();
    private ObjectiveDisplayMode displayMode;
    private Text displayName;
    private String legacyDisplayName;

    LanternObjective(String name, Criterion criterion, ObjectiveDisplayMode displayMode, Text displayName) {
        this.legacyDisplayName = LanternTexts.toLegacy(displayName);
        this.displayName = displayName;
        this.displayMode = displayMode;
        this.criterion = criterion;
        this.name = name;
    }

    void addScoreboard(Scoreboard scoreboard) {
        this.scoreboards.add(scoreboard);
    }

    void removeScoreboard(Scoreboard scoreboard) {
        this.scoreboards.remove(scoreboard);
    }

    public String getLegacyDisplayName() {
        return this.legacyDisplayName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Text getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(Text displayName) throws IllegalArgumentException {
        final String legacyDisplayName = LanternTexts.toLegacy(checkNotNull(displayName, "displayName"));
        checkArgument(legacyDisplayName.length() <= 32, "Display name is %s characters long! It must be at most 32.",
                legacyDisplayName.length());
        final boolean update = !legacyDisplayName.equals(this.legacyDisplayName);
        this.legacyDisplayName = legacyDisplayName;
        this.displayName = displayName;
        if (update) {
            this.sendObjectiveUpdate();
        }
    }

    private void sendObjectiveUpdate() {
        if (!this.scoreboards.isEmpty()) {
            final List<Message> message = Collections.singletonList(new MessagePlayOutScoreboardObjective.Update(
                    this.name, this.legacyDisplayName, this.displayMode));
            for (Scoreboard scoreboard : this.scoreboards) {
                ((LanternScoreboard) scoreboard).sendToPlayers(() -> message);
            }
        }
    }

    @Override
    public Criterion getCriterion() {
        return this.criterion;
    }

    @Override
    public ObjectiveDisplayMode getDisplayMode() {
        return this.displayMode;
    }

    @Override
    public void setDisplayMode(ObjectiveDisplayMode displayMode) {
        final boolean update = !checkNotNull(displayMode, "displayMode").equals(this.displayMode);
        this.displayMode = displayMode;
        if (update) {
            this.sendObjectiveUpdate();
        }
    }

    @Override
    public Map<Text, Score> getScores() {
        return ImmutableMap.copyOf(this.scores);
    }

    @Override
    public boolean hasScore(Text name) {
        return this.scores.containsKey(checkNotNull(name, "name"));
    }

    @Override
    public void addScore(Score score) throws IllegalArgumentException {
        if (this.scores.containsKey(checkNotNull(score, "score").getName())) {
            throw new IllegalArgumentException(String.format("A score with the name %s already exists!",
                    ((LanternScore) score).getLegacyName()));
        }
        this.scores.put(score.getName(), score);
        ((LanternScore) score).addObjective(this);
        this.sendScoreToClient(score);
    }

    private void sendScoreToClient(Score score) {
        if (!this.scoreboards.isEmpty()) {
            List<Message> message = Collections.singletonList(new MessagePlayOutScoreboardScore.CreateOrUpdate(this.getName(),
                    LanternTexts.toLegacy(score.getName()), score.getScore()));
            for (Scoreboard scoreboard : this.scoreboards) {
                ((LanternScoreboard) scoreboard).sendToPlayers(() -> message);
            }
        }
    }

    @Override
    public Score getOrCreateScore(Text name) {
        return this.scores.computeIfAbsent(name, name1 -> {
            final LanternScore score = new LanternScore(name1);
            score.addObjective(this);
            this.sendScoreToClient(score);
            return score;
        });
    }

    @Override
    public boolean removeScore(Score score) {
        if (this.scores.remove(checkNotNull(score, "score").getName(), score)) {
            ((LanternScore) score).removeObjective(this);
            this.updateClientAfterRemove(score);
            return true;
        }
        return false;
    }

    private void updateClientAfterRemove(Score score) {
        final Map<Objective, Message> messages = new HashMap<>();
        for (Scoreboard scoreboard : this.scoreboards) {
            ((LanternScoreboard) scoreboard).sendToPlayers(() -> Collections.singletonList(
                    messages.computeIfAbsent(this, obj -> new MessagePlayOutScoreboardScore.Remove(
                            this.getName(), ((LanternScore) score).getLegacyName()))));
        }
    }

    @Override
    public boolean removeScore(Text name) {
        final Score score = this.scores.remove(checkNotNull(name, "name"));
        if (score != null) {
            ((LanternScore) score).removeObjective(this);
            this.updateClientAfterRemove(score);
            return true;
        }
        return false;
    }

    @Override
    public Set<Scoreboard> getScoreboards() {
        return ImmutableSet.copyOf(this.scoreboards);
    }
}
