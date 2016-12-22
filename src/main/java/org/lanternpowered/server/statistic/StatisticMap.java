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
package org.lanternpowered.server.statistic;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.lanternpowered.server.game.registry.type.statistic.StatisticRegistryModule;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutStatistics;
import org.lanternpowered.server.statistic.achievement.IAchievement;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.achievement.Achievement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public final class StatisticMap {

    private static final Gson GSON = new Gson();

    private final Map<String, StatisticEntry> statisticEntries = new ConcurrentHashMap<>();

    public StatisticEntry get(Statistic statistic) {
        return this.statisticEntries.computeIfAbsent(((LanternStatistic) statistic).getInternalId(),
                id -> new StatisticEntry((LanternStatistic) statistic));
    }

    public Map<Statistic, Long> getStatisticValues() {
        final Map<Statistic, Long> map = new HashMap<>();
        for (Map.Entry<String, StatisticEntry> entry : this.statisticEntries.entrySet()) {
            final LanternStatistic statistic = entry.getValue().getStatistic();
            if (statistic != null) {
                map.put(statistic, entry.getValue().get());
            }
        }
        return map;
    }

    public void setStatisticValues(Map<Statistic, Long> values) {
        final Set<StatisticEntry> restEntries = new HashSet<>(this.statisticEntries.values());
        for (Map.Entry<Statistic, Long> entry : values.entrySet()) {
            final StatisticEntry statisticEntry = get(entry.getKey());
            statisticEntry.set(entry.getValue());
            restEntries.remove(statisticEntry);
        }
        for (StatisticEntry entry : restEntries) {
            entry.set(0);
        }
    }

    private StatisticEntry get(String statistic) {
        return this.statisticEntries.computeIfAbsent(statistic,
                id -> new StatisticEntry((LanternStatistic) StatisticRegistryModule.get().getByInternalId(statistic).orElse(null)));
    }

    @Nullable
    public MessagePlayOutStatistics createAchievementsMessage(boolean initial) {
        Set<MessagePlayOutStatistics.Entry> entries = initial ? new HashSet<>() : null;
        for (Map.Entry<String, StatisticEntry> entry : this.statisticEntries.entrySet()) {
            final LanternStatistic statistic = entry.getValue().getStatistic();
            if (!(statistic instanceof Achievement) || !entry.getValue().isDirty(true)) {
                continue;
            }
            final IAchievement achievement = (IAchievement) statistic;
            final long value = entry.getValue().get();
            if (value >= achievement.getStatisticTargetValue()) {
                if (entries == null) {
                    entries = new HashSet<>();
                }
                entries.add(new MessagePlayOutStatistics.Entry(((LanternStatistic) achievement).getInternalId(), (int) value));
            }
        }
        return entries == null ? null : new MessagePlayOutStatistics(entries);
    }

    public MessagePlayOutStatistics createStatisticsMessage() {
        return new MessagePlayOutStatistics(this.statisticEntries.entrySet().stream()
                .filter(entry -> entry.getValue().getStatistic() instanceof Achievement && entry.getValue().isDirty(true))
                .map(entry -> new MessagePlayOutStatistics.Entry(entry.getKey(), (int) entry.getValue().get()))
                .collect(Collectors.toSet()));
    }

    public void save(Path path) throws IOException {
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        try (final BufferedWriter writer = Files.newBufferedWriter(path)) {
            final JsonObject object = new JsonObject();

            for (Map.Entry<String, StatisticEntry> entry : this.statisticEntries.entrySet()) {
                final long value = entry.getValue().get();
                if (value != 0) {
                    object.addProperty(entry.getKey(), value);
                }
            }

            GSON.toJson(object, writer);
            writer.flush();
        }
    }

    public void load(Path path) throws IOException {
        try (final BufferedReader reader = Files.newBufferedReader(path)) {
            this.statisticEntries.clear();
            final JsonObject object = GSON.fromJson(reader, JsonObject.class);

            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                get(entry.getKey()).set(entry.getValue().getAsLong());
            }
        }
    }
}
