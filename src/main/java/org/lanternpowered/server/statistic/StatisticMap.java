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
package org.lanternpowered.server.statistic;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.lanternpowered.server.game.registry.type.statistic.StatisticRegistryModule;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutStatistics;
import org.spongepowered.api.statistic.Statistic;

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

    public PacketPlayOutStatistics createStatisticsMessage() {
        return new PacketPlayOutStatistics(this.statisticEntries.entrySet().stream()
                .filter(entry -> entry.getValue().isDirty(true))
                .map(entry -> new PacketPlayOutStatistics.Entry(entry.getKey(), (int) entry.getValue().get()))
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
