/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardScore;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LanternScore implements Score {

    private final Set<Objective> objectives = new HashSet<>();
    private final Text name;
    private final String legacyName;
    private int score;

    public LanternScore(Text name) {
        this.legacyName = LanternTexts.toLegacy(name);
        this.name = name;
    }

    public String getLegacyName() {
        return this.legacyName;
    }

    @Override
    public Text getName() {
        return this.name;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public void setScore(int score) {
        if (this.score == score) {
            return;
        }
        this.score = score;
        Multimap<Scoreboard, Objective> scoreboards = HashMultimap.create();
        for (Objective objective : this.objectives) {
            for (Scoreboard scoreboard : ((LanternObjective) objective).scoreboards) {
                scoreboards.put(scoreboard, objective);
            }
        }
        if (!scoreboards.isEmpty()) {
            Map<Objective, Message> messages = Maps.newHashMap();
            for (Map.Entry<Scoreboard, Objective> entry : scoreboards.entries()) {
                ((LanternScoreboard) entry.getKey()).sendToPlayers(() -> Collections.singletonList(
                        messages.computeIfAbsent(entry.getValue(), obj -> new MessagePlayOutScoreboardScore.CreateOrUpdate(
                                obj.getName(), this.legacyName, score))));
            }
        }
    }

    @Override
    public Set<Objective> getObjectives() {
        return ImmutableSet.copyOf(this.objectives);
    }

    void addObjective(Objective objective) {
        this.objectives.add(objective);
    }

    void removeObjective(Objective objective) {
        this.objectives.remove(objective);
    }
}
