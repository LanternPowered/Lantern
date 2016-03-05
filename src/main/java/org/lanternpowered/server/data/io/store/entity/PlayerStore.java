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
package org.lanternpowered.server.data.io.store.entity;

import static org.lanternpowered.server.data.util.DataUtil.getOrCreateView;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.util.DataQueries;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.entity.player.GameModeRegistryModule;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.LanternWorldProperties;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.util.RespawnLocation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Note: This store assumes that all the Sponge data (world/data/sponge) is merged
 * into one {@link DataContainer}. This listed under a sub view with the data query
 * {@link DataQueries#SPONGE_DATA}.
 */
public class PlayerStore extends LivingStore<LanternPlayer> {

    private static final DataQuery ABILITIES = DataQuery.of("abilities");

    private static final DataQuery FLYING = DataQuery.of("flying");
    private static final DataQuery FLYING_SPEED = DataQuery.of("flySpeed");
    private static final DataQuery CAN_FLY = DataQuery.of("mayfly");
    private static final DataQuery SCORE = DataQuery.of("Score");
    private static final DataQuery GAME_MODE = DataQuery.of("playerGameType");

    private static final DataQuery DIMENSION = DataQuery.of("Dimension");

    private static final DataQuery FIRST_DATE_PLAYED = DataQuery.of("FirstJoin");
    private static final DataQuery LAST_DATE_PLAYED = DataQuery.of("LastPlayed");

    private static final DataQuery RESPAWN_LOCATIONS = DataQuery.of("Spawns");
    private static final DataQuery RESPAWN_LOCATIONS_DIMENSION = DataQuery.of("Dim");
    private static final DataQuery RESPAWN_LOCATIONS_X = DataQuery.of("SpawnX");
    private static final DataQuery RESPAWN_LOCATIONS_Y = DataQuery.of("SpawnY");
    private static final DataQuery RESPAWN_LOCATIONS_Z = DataQuery.of("SpawnZ");
    private static final DataQuery RESPAWN_LOCATIONS_FORCED = DataQuery.of("SpawnForced");

    @Override
    public void deserialize(LanternPlayer player, DataContainer dataContainer) {
        super.deserialize(player, dataContainer);

        int dimension = dataContainer.getInt(DIMENSION).orElse(0);
        Lantern.getWorldManager().getWorldProperties(dimension).ifPresent(worldProperties -> {
            LanternWorldProperties worldProperties0 = (LanternWorldProperties) worldProperties;
            Optional<LanternWorld> optWorld = worldProperties0.getWorld();
            if (optWorld.isPresent()) {
                player.setRawWorld(optWorld.get());
            } else {
                player.setTempWorld(worldProperties0);
            }
        });
    }

    @Override
    public void serialize(LanternPlayer entity, DataContainer dataContainer) {
        super.serialize(entity, dataContainer);

        dataContainer.set(DIMENSION, Lantern.getWorldManager().getWorldDimensionId(entity.getWorld().getUniqueId()).orElse(0));
    }

    @Override
    public void serializeValues(LanternPlayer player, SimpleValueContainer valueContainer, DataContainer dataContainer) {
        DataView abilities = dataContainer.createView(ABILITIES);
        abilities.set(FLYING, (byte) (valueContainer.remove(Keys.IS_FLYING).orElse(false) ? 1 : 0));
        abilities.set(FLYING_SPEED, valueContainer.remove(Keys.FLYING_SPEED).orElse(0.1).floatValue());
        abilities.set(CAN_FLY, (byte) (valueContainer.remove(Keys.CAN_FLY).orElse(false) ? 1 : 0));
        DataView spongeData = getOrCreateView(dataContainer, DataQueries.EXTENDED_SPONGE_DATA);
        spongeData.set(FIRST_DATE_PLAYED, valueContainer.remove(Keys.FIRST_DATE_PLAYED).orElse(Instant.now()).toEpochMilli());
        spongeData.set(LAST_DATE_PLAYED, valueContainer.remove(Keys.LAST_DATE_PLAYED).orElse(Instant.now()).toEpochMilli());
        spongeData.set(UNIQUE_ID, player.getUniqueId().toString());
        spongeData.set(Queries.CONTENT_VERSION, 1);
        Map<UUID, RespawnLocation> respawnLocations = valueContainer.remove(Keys.RESPAWN_LOCATIONS).get();
        List<DataView> respawnLocationViews = new ArrayList<>();
        for (RespawnLocation respawnLocation : respawnLocations.values()) {
            Lantern.getWorldManager().getWorldDimensionId(respawnLocation.getWorldUniqueId()).ifPresent(dimensionId -> {
                // Overworld respawn location is saved in the root container
                if (dimensionId == 0) {
                    serializeRespawnLocationTo(dataContainer, respawnLocation);
                } else {
                    respawnLocationViews.add(serializeRespawnLocationTo(new MemoryDataContainer(), respawnLocation)
                            .set(RESPAWN_LOCATIONS_DIMENSION, dimensionId));
                }
            });
        }
        dataContainer.set(RESPAWN_LOCATIONS, respawnLocationViews);
        dataContainer.set(GAME_MODE, ((LanternGameMode) valueContainer.remove(Keys.GAME_MODE).orElse(GameModes.NOT_SET)).getInternalId());
        super.serializeValues(player, valueContainer, dataContainer);
    }

    private static DataView serializeRespawnLocationTo(DataView dataView, RespawnLocation respawnLocation) {
        Vector3d position = respawnLocation.getPosition();
        return dataView
                .set(RESPAWN_LOCATIONS_X, position.getX())
                .set(RESPAWN_LOCATIONS_Y, position.getY())
                .set(RESPAWN_LOCATIONS_Z, position.getZ())
                .set(RESPAWN_LOCATIONS_FORCED, respawnLocation.isForced());
    }

    @Override
    public void deserializeValues(LanternPlayer player, SimpleValueContainer valueContainer, DataContainer dataContainer) {
        dataContainer.getView(ABILITIES).ifPresent(view -> {
            view.getInt(FLYING).ifPresent(v -> valueContainer.set(Keys.IS_FLYING, v > 0));
            view.getDouble(FLYING_SPEED).ifPresent(v -> valueContainer.set(Keys.FLYING_SPEED, v));
            view.getInt(CAN_FLY).ifPresent(v -> valueContainer.set(Keys.CAN_FLY, v > 0));
        });
        dataContainer.getView(DataQueries.EXTENDED_SPONGE_DATA).ifPresent(view -> {
            view.getLong(FIRST_DATE_PLAYED).ifPresent(v -> valueContainer.set(Keys.FIRST_DATE_PLAYED, Instant.ofEpochMilli(v)));
            view.getLong(LAST_DATE_PLAYED).ifPresent(v -> valueContainer.set(Keys.LAST_DATE_PLAYED, Instant.ofEpochMilli(v)));
        });
        Map<UUID, RespawnLocation> respawnLocations = new HashMap<>();
        // Overworld respawn location is saved in the root container
        Optional<Double> optSpawnX = dataContainer.getDouble(RESPAWN_LOCATIONS_X);
        Optional<Double> optSpawnY = dataContainer.getDouble(RESPAWN_LOCATIONS_Y);
        Optional<Double> optSpawnZ = dataContainer.getDouble(RESPAWN_LOCATIONS_Z);
        if (optSpawnX.isPresent() && optSpawnY.isPresent() && optSpawnZ.isPresent()) {
            UUID uniqueId = Lantern.getWorldManager().getWorldProperties(0).get().getUniqueId();
            respawnLocations.put(uniqueId, deserializeRespawnLocation(dataContainer, uniqueId, optSpawnX.get(),
                    optSpawnY.get(), optSpawnZ.get()));
        }
        dataContainer.getViewList(RESPAWN_LOCATIONS).ifPresent(v -> v.forEach(view -> {
            int dimensionId = view.getInt(RESPAWN_LOCATIONS_DIMENSION).get();
            Lantern.getWorldManager().getWorldProperties(dimensionId).ifPresent(props -> {
                UUID uniqueId = props.getUniqueId();
                double x = view.getDouble(RESPAWN_LOCATIONS_X).get();
                double y = view.getDouble(RESPAWN_LOCATIONS_Y).get();
                double z = view.getDouble(RESPAWN_LOCATIONS_Z).get();
                respawnLocations.put(uniqueId, deserializeRespawnLocation(view, uniqueId, x, y, z));
            });
        }));
        valueContainer.set(Keys.RESPAWN_LOCATIONS, respawnLocations);
        dataContainer.getInt(SCORE).ifPresent(v -> valueContainer.set(LanternKeys.SCORE, v));
        GameMode gameMode = dataContainer.getInt(GAME_MODE)
                .flatMap(v -> GameModeRegistryModule.getInstance().getByInternalId(v)).orElse(GameModes.NOT_SET);
        valueContainer.set(Keys.GAME_MODE, gameMode);
        super.deserializeValues(player, valueContainer, dataContainer);
    }

    private static RespawnLocation deserializeRespawnLocation(DataView dataView, UUID worldUUID, double x, double y, double z) {
        boolean forced = dataView.getInt(RESPAWN_LOCATIONS_FORCED).orElse(0) > 0;
        return RespawnLocation.builder()
                .world(worldUUID)
                .position(new Vector3d(x, y, z))
                .forceSpawn(forced)
                .build();
    }
}
