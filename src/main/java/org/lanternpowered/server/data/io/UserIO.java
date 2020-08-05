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
package org.lanternpowered.server.data.io;

import org.lanternpowered.server.data.DataQueries;
import org.lanternpowered.server.data.io.store.ObjectStore;
import org.lanternpowered.server.data.io.store.ObjectStoreRegistry;
import org.lanternpowered.server.data.persistence.nbt.NbtStreamUtils;
import org.lanternpowered.server.entity.player.AbstractPlayer;
import org.lanternpowered.server.entity.player.AbstractUser;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;

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

    public static void load(Path dataFolder, AbstractPlayer player) throws IOException {
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

    public static void save(Path dataFolder, AbstractPlayer player) throws IOException {
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
