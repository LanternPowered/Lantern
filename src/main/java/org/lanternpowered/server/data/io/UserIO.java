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
package org.lanternpowered.server.data.io;

import org.lanternpowered.server.data.DataQueries;
import org.lanternpowered.server.data.io.store.ObjectStore;
import org.lanternpowered.server.data.io.store.ObjectStoreRegistry;
import org.lanternpowered.server.data.persistence.nbt.NbtStreamUtils;
import org.lanternpowered.server.entity.living.player.AbstractUser;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

public final class UserIO {

    private final static Path SPONGE_PLAYER_DATA_FOLDER = Paths.get("data", "sponge");
    private final static Path PLAYER_DATA_FOLDER = Paths.get("playerdata");
    private final static Path STATISTICS_FOLDER = Paths.get("stats");
    private final static DataQuery NAME = DataQuery.of("Name");

    public static boolean exists(Path dataFolder, UUID uniqueId) {
        final String fileName = uniqueId.toString() + ".dat";
        final Path dataFile = dataFolder.resolve(PLAYER_DATA_FOLDER).resolve(fileName);
        return Files.exists(dataFile);
    }

    public static Optional<String> loadName(Path dataFolder, UUID uniqueId) throws IOException {
        final Path path = dataFolder.resolve(SPONGE_PLAYER_DATA_FOLDER).resolve(uniqueId.toString() + ".dat");
        if (Files.exists(path)) {
            return NbtStreamUtils.read(Files.newInputStream(path), true).getString(NAME);
        }
        return Optional.empty();
    }

    public static void load(Path dataFolder, AbstractUser player) throws IOException {
        final String fileName = player.getUniqueId().toString() + ".dat";

        // Search for the player data and load it
        Path dataFile = dataFolder.resolve(PLAYER_DATA_FOLDER).resolve(fileName);
        if (Files.exists(dataFile)) {
            final DataContainer dataContainer = NbtStreamUtils.read(Files.newInputStream(dataFile), true);

            // Load sponge data if present and attach it to the main data
            dataFile = dataFolder.resolve(SPONGE_PLAYER_DATA_FOLDER).resolve(fileName);
            if (Files.exists(dataFile)) {
                final DataContainer spongeDataContainer = NbtStreamUtils.read(Files.newInputStream(dataFile), true);
                dataContainer.set(DataQueries.EXTENDED_SPONGE_DATA, spongeDataContainer);
            }

            final ObjectStore<AbstractUser> objectStore = ObjectStoreRegistry.get().get(AbstractUser.class).get();
            objectStore.deserialize(player, dataContainer);
        }

        final Path statisticsFile = dataFolder.resolve(STATISTICS_FOLDER).resolve(player.getUniqueId().toString() + ".json");
        if (Files.exists(statisticsFile)) {
            player.getStatisticMap().load(statisticsFile);
        }
    }

    public static void save(Path dataFolder, AbstractUser player) throws IOException {
        final String fileName = player.getUniqueId().toString() + ".dat";

        final DataContainer dataContainer = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        final ObjectStore<AbstractUser> objectStore = ObjectStoreRegistry.get().get(AbstractUser.class).get();
        objectStore.serialize(player, dataContainer);

        final Optional<DataView> optSpongeData = dataContainer.getView(DataQueries.EXTENDED_SPONGE_DATA);
        dataContainer.remove(DataQueries.EXTENDED_SPONGE_DATA);

        Path dataFolder0 = dataFolder.resolve(PLAYER_DATA_FOLDER);
        if (!Files.exists(dataFolder0)) {
            Files.createDirectories(dataFolder0);
        }
        Path dataFile = dataFolder0.resolve(fileName);
        NbtStreamUtils.write(dataContainer, Files.newOutputStream(dataFile), true);

        dataFolder0 = dataFolder.resolve(SPONGE_PLAYER_DATA_FOLDER);
        if (!Files.exists(dataFolder0)) {
            Files.createDirectories(dataFolder0);
        }
        dataFile = dataFolder0.resolve(fileName);
        if (optSpongeData.isPresent()) {
            final DataView spongeData = optSpongeData.get();
            spongeData.set(NAME, player.getName());
            NbtStreamUtils.write(spongeData, Files.newOutputStream(dataFile), true);
        } else {
            Files.deleteIfExists(dataFile);
        }

        final Path statisticsFile = dataFolder.resolve(STATISTICS_FOLDER).resolve(player.getUniqueId().toString() + ".json");
        player.getStatisticMap().save(statisticsFile);
    }

    private UserIO() {
    }
}
