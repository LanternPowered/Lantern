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
package org.lanternpowered.server.data.io.store.entity;

import static org.lanternpowered.server.data.DataHelper.getOrCreateView;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.advancement.old.AdvancementTrees;
import org.lanternpowered.server.data.DataQueries;
import org.lanternpowered.server.data.io.store.ObjectSerializer;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.data.io.store.item.ItemStackStore;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.living.player.AbstractUser;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.entity.player.GameModeRegistryModule;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.inventory.vanilla.AbstractUserInventory;
import org.lanternpowered.server.inventory.vanilla.LanternMainPlayerInventory;
import org.lanternpowered.server.inventory.vanilla.LanternPlayerEquipmentInventory;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.LanternWorldProperties;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.GridInventory;
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
 * into one {@link DataView}. This listed under a sub view with the data query
 * {@link DataQueries#SPONGE_DATA}.
 */
@SuppressWarnings({"OptionalGetWithoutIsPresent", "unchecked", "ConstantConditions"})
public class UserStore<T extends AbstractUser> extends LivingStore<T> {

    private static final DataQuery ABILITIES = DataQuery.of("abilities");

    private static final DataQuery FLYING = DataQuery.of("flying");
    private static final DataQuery FLYING_SPEED = DataQuery.of("flySpeed");
    private static final DataQuery CAN_FLY = DataQuery.of("mayfly");
    private static final DataQuery SCORE = DataQuery.of("Score");
    private static final DataQuery GAME_MODE = DataQuery.of("playerGameType");
    private static final DataQuery SELECTED_ITEM_SLOT = DataQuery.of("SelectedItemSlot");
    private static final DataQuery DIMENSION = DataQuery.of("Dimension");

    private static final DataQuery BUKKIT_FIRST_DATE_PLAYED = DataQuery.of('.', "bukkit.firstPlayed");
    private static final DataQuery BUKKIT_LAST_DATE_PLAYED = DataQuery.of('.', "bukkit.lastPlayed");

    private static final DataQuery FIRST_DATE_PLAYED = DataQuery.of("FirstJoin");
    private static final DataQuery LAST_DATE_PLAYED = DataQuery.of("LastPlayed");

    private static final DataQuery RESPAWN_LOCATIONS = DataQuery.of("Spawns");
    private static final DataQuery RESPAWN_LOCATIONS_DIMENSION = DataQuery.of("Dim");
    private static final DataQuery RESPAWN_LOCATIONS_X = DataQuery.of("SpawnX");
    private static final DataQuery RESPAWN_LOCATIONS_Y = DataQuery.of("SpawnY");
    private static final DataQuery RESPAWN_LOCATIONS_Z = DataQuery.of("SpawnZ");
    private static final DataQuery RESPAWN_LOCATIONS_FORCED = DataQuery.of("SpawnForced");

    private static final DataQuery SLOT = DataQuery.of("Slot");
    private static final DataQuery INVENTORY = DataQuery.of("Inventory");
    private static final DataQuery ENDER_CHEST_INVENTORY = DataQuery.of("EnderItems");

    private static final DataQuery RECIPE_BOOK = DataQuery.of("recipeBook");
    private static final DataQuery RECIPE_BOOK_GUI_OPEN = DataQuery.of("isGuiOpen");
    private static final DataQuery RECIPE_BOOK_FILTER_ACTIVE = DataQuery.of("isFilteringCraftable");

    private static final DataQuery OPEN_ADVANCEMENT_TREE = DataQuery.of("openAdvancementTree"); // Lantern

    @Override
    public void deserialize(T player, DataView dataView) {
        super.deserialize(player, dataView);
        final int dimension = dataView.getInt(DIMENSION).orElse(0);
        Lantern.getWorldManager().getWorldProperties(dimension).ifPresent(worldProperties -> {
            final LanternWorldProperties worldProperties0 = (LanternWorldProperties) worldProperties;
            final Optional<LanternWorld> optWorld = worldProperties0.getWorld();
            if (optWorld.isPresent()) {
                player.setRawWorld(optWorld.get());
            } else {
                player.setTempWorld(worldProperties0);
            }
        });
    }

    @Override
    public void serialize(T entity, DataView dataView) {
        super.serialize(entity, dataView);
        dataView.remove(HEAD_ROTATION);
        final LanternWorld world = entity.getWorld();
        final UUID uniqueId = world != null ? world.getUniqueId() :
                entity.getTempWorld() != null ? entity.getTempWorld().getUniqueId() : null;
        dataView.set(DIMENSION, uniqueId == null ? 0 : Lantern.getWorldManager().getWorldDimensionId(uniqueId).orElse(0));
    }

    @Override
    public void serializeValues(T player, SimpleValueContainer valueContainer, DataView dataView) {
        valueContainer.remove(Keys.IS_SPRINTING);
        valueContainer.remove(Keys.IS_SNEAKING);
        valueContainer.remove(LanternKeys.ACTIVE_HAND);
        final DataView abilities = dataView.createView(ABILITIES);
        abilities.set(FLYING, (byte) (valueContainer.remove(Keys.IS_FLYING).orElse(false) ? 1 : 0));
        abilities.set(FLYING_SPEED, valueContainer.remove(Keys.FLYING_SPEED).orElse(0.1).floatValue());
        abilities.set(CAN_FLY, (byte) (valueContainer.remove(Keys.CAN_FLY).orElse(false) ? 1 : 0));
        final DataView spongeData = getOrCreateView(dataView, DataQueries.EXTENDED_SPONGE_DATA);
        spongeData.set(FIRST_DATE_PLAYED, valueContainer.remove(Keys.FIRST_DATE_PLAYED).orElse(Instant.now()).toEpochMilli());
        spongeData.set(LAST_DATE_PLAYED, valueContainer.remove(Keys.LAST_DATE_PLAYED).orElse(Instant.now()).toEpochMilli());
        spongeData.set(UNIQUE_ID, player.getUniqueId().toString());
        spongeData.set(Queries.CONTENT_VERSION, 1);
        final Map<UUID, RespawnLocation> respawnLocations = valueContainer.remove(Keys.RESPAWN_LOCATIONS).get();
        final List<DataView> respawnLocationViews = new ArrayList<>();
        for (RespawnLocation respawnLocation : respawnLocations.values()) {
            Lantern.getWorldManager().getWorldDimensionId(respawnLocation.getWorldUniqueId()).ifPresent(dimensionId -> {
                // Overworld respawn location is saved in the root container
                if (dimensionId == 0) {
                    serializeRespawnLocationTo(dataView, respawnLocation);
                } else {
                    respawnLocationViews.add(serializeRespawnLocationTo(DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED), respawnLocation)
                            .set(RESPAWN_LOCATIONS_DIMENSION, dimensionId));
                }
            });
        }
        dataView.set(RESPAWN_LOCATIONS, respawnLocationViews);
        dataView.set(GAME_MODE, ((LanternGameMode) valueContainer.remove(Keys.GAME_MODE).orElse(GameModes.NOT_SET)).getInternalId());
        dataView.set(SELECTED_ITEM_SLOT, player.getInventory().getHotbar().getSelectedSlotIndex());
        dataView.set(SCORE, valueContainer.remove(LanternKeys.SCORE).get());

        // Serialize the player inventory
        dataView.set(INVENTORY, serializePlayerInventory(player.getInventory()));
        // Serialize the ender chest inventory
        dataView.set(ENDER_CHEST_INVENTORY, serializeEnderChest(player.getEnderChestInventory()));

        final DataView recipeBook = dataView.createView(RECIPE_BOOK);
        recipeBook.set(RECIPE_BOOK_FILTER_ACTIVE, (byte) (valueContainer.remove(LanternKeys.RECIPE_BOOK_FILTER_ACTIVE).orElse(false) ? 1 : 0));
        recipeBook.set(RECIPE_BOOK_GUI_OPEN, (byte) (valueContainer.remove(LanternKeys.RECIPE_BOOK_GUI_OPEN).orElse(false) ? 1 : 0));

        valueContainer.remove(LanternKeys.OPEN_ADVANCEMENT_TREE).ifPresent(o -> {
            if (o.isPresent()) {
                dataView.set(OPEN_ADVANCEMENT_TREE, o.get().getId());
            }
        });

        super.serializeValues(player, valueContainer, dataView);
    }

    private static DataView serializeRespawnLocationTo(DataView dataView, RespawnLocation respawnLocation) {
        final Vector3d position = respawnLocation.getPosition();
        return dataView
                .set(RESPAWN_LOCATIONS_X, position.getX())
                .set(RESPAWN_LOCATIONS_Y, position.getY())
                .set(RESPAWN_LOCATIONS_Z, position.getZ())
                .set(RESPAWN_LOCATIONS_FORCED, respawnLocation.isForced());
    }

    @Override
    public void deserializeValues(T player, SimpleValueContainer valueContainer, DataView dataView) {
        // Try to convert old bukkit values first
        dataView.getLong(BUKKIT_FIRST_DATE_PLAYED).ifPresent(v -> valueContainer.set(Keys.FIRST_DATE_PLAYED, Instant.ofEpochMilli(v)));
        dataView.getLong(BUKKIT_LAST_DATE_PLAYED).ifPresent(v -> valueContainer.set(Keys.LAST_DATE_PLAYED, Instant.ofEpochMilli(v)));
        // Deserialize sponge data
        dataView.getView(DataQueries.EXTENDED_SPONGE_DATA).ifPresent(view -> {
            view.getLong(FIRST_DATE_PLAYED).ifPresent(v -> valueContainer.set(Keys.FIRST_DATE_PLAYED, Instant.ofEpochMilli(v)));
            view.getLong(LAST_DATE_PLAYED).ifPresent(v -> valueContainer.set(Keys.LAST_DATE_PLAYED, Instant.ofEpochMilli(v)));
        });
        dataView.getView(ABILITIES).ifPresent(view -> {
            view.getInt(FLYING).ifPresent(v -> valueContainer.set(Keys.IS_FLYING, v > 0));
            view.getDouble(FLYING_SPEED).ifPresent(v -> valueContainer.set(Keys.FLYING_SPEED, v));
            view.getInt(CAN_FLY).ifPresent(v -> valueContainer.set(Keys.CAN_FLY, v > 0));
        });
        final Map<UUID, RespawnLocation> respawnLocations = new HashMap<>();
        // Overworld respawn location is saved in the root container
        final Optional<Double> optSpawnX = dataView.getDouble(RESPAWN_LOCATIONS_X);
        final Optional<Double> optSpawnY = dataView.getDouble(RESPAWN_LOCATIONS_Y);
        final Optional<Double> optSpawnZ = dataView.getDouble(RESPAWN_LOCATIONS_Z);
        if (optSpawnX.isPresent() && optSpawnY.isPresent() && optSpawnZ.isPresent()) {
            UUID uniqueId = Lantern.getWorldManager().getWorldProperties(0).get().getUniqueId();
            respawnLocations.put(uniqueId, deserializeRespawnLocation(dataView, uniqueId, optSpawnX.get(),
                    optSpawnY.get(), optSpawnZ.get()));
        }
        dataView.getViewList(RESPAWN_LOCATIONS).ifPresent(v -> v.forEach(view -> {
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
        dataView.getInt(SCORE).ifPresent(v -> valueContainer.set(LanternKeys.SCORE, v));
        final GameMode gameMode = dataView.getInt(GAME_MODE)
                .flatMap(v -> GameModeRegistryModule.get().getByInternalId(v)).orElse(GameModes.NOT_SET);
        valueContainer.set(Keys.GAME_MODE, gameMode);
        player.getInventory().getHotbar().setSelectedSlotIndex(dataView.getInt(SELECTED_ITEM_SLOT).orElse(0));

        // Deserialize the player inventory
        dataView.getViewList(INVENTORY).ifPresent(views -> deserializePlayerInventory(player.getInventory(), views));
        // Deserialize the ender chest inventory
        dataView.getViewList(ENDER_CHEST_INVENTORY).ifPresent(views -> deserializeEnderChest(player.getEnderChestInventory(), views));

        dataView.getView(RECIPE_BOOK).ifPresent(view -> {
            view.getInt(RECIPE_BOOK_FILTER_ACTIVE).ifPresent(v -> valueContainer.set(LanternKeys.RECIPE_BOOK_FILTER_ACTIVE, v > 0));
            view.getInt(RECIPE_BOOK_GUI_OPEN).ifPresent(v -> valueContainer.set(LanternKeys.RECIPE_BOOK_GUI_OPEN, v > 0));
        });
        dataView.getString(OPEN_ADVANCEMENT_TREE).ifPresent(id -> valueContainer
                .set(LanternKeys.OPEN_ADVANCEMENT_TREE, AdvancementTrees.INSTANCE.get(id)));

        super.deserializeValues(player, valueContainer, dataView);
    }

    private static RespawnLocation deserializeRespawnLocation(DataView dataView, UUID worldUUID, double x, double y, double z) {
        boolean forced = dataView.getInt(RESPAWN_LOCATIONS_FORCED).orElse(0) > 0;
        return RespawnLocation.builder()
                .world(worldUUID)
                .position(new Vector3d(x, y, z))
                .forceSpawn(forced)
                .build();
    }

    private static List<DataView> serializeEnderChest(GridInventory enderChestInventory) {
        final List<DataView> itemViews = new ArrayList<>();
        final Iterable<Slot> slots = enderChestInventory.slots();
        for (Slot slot : slots) {
            final Optional<ItemStack> optItemStack = slot.peek();
            if (!optItemStack.isPresent()) {
                continue;
            }
            final DataView itemView = ItemStackStore.INSTANCE.serialize((LanternItemStack) optItemStack.get());
            itemView.set(SLOT, (byte) enderChestInventory.getProperty(slot, SlotIndex.class, null).get().getValue().intValue());
            itemViews.add(itemView);
        }

        return itemViews;
    }

    private static void deserializeEnderChest(GridInventory enderChestInventory, List<DataView> itemViews) {
        for (DataView itemView : itemViews) {
            final int slot = itemView.getByte(SLOT).get() & 0xff;
            final LanternItemStack itemStack = ItemStackStore.INSTANCE.deserialize(itemView);
            enderChestInventory.set(new SlotIndex(slot), itemStack);
        }
    }

    private static void deserializePlayerInventory(AbstractUserInventory<?> inventory, List<DataView> itemViews) {
        final LanternMainPlayerInventory mainInventory = inventory.getMain();
        final LanternPlayerEquipmentInventory equipmentInventory = inventory.getEquipment();
        final AbstractSlot offHandSlot = inventory.getOffhand();

        for (DataView itemView : itemViews) {
            final int slot = itemView.getByte(SLOT).get() & 0xff;
            final LanternItemStack itemStack = ItemStackStore.INSTANCE.deserialize(itemView);

            if (slot >= 0 && slot < mainInventory.capacity()) {
                mainInventory.set(new SlotIndex(slot), itemStack);
            } else if (slot >= 100 && slot - 100 < equipmentInventory.capacity()) {
                equipmentInventory.set(new SlotIndex(slot - 100), itemStack);
            } else if (slot == 150) {
                offHandSlot.set(itemStack);
            }
        }
    }

    private static List<DataView> serializePlayerInventory(AbstractUserInventory<?> inventory) {
        final List<DataView> itemViews = new ArrayList<>();

        final LanternMainPlayerInventory mainInventory = inventory.getMain();
        final LanternPlayerEquipmentInventory equipmentInventory = inventory.getEquipment();
        final AbstractSlot offHandSlot = inventory.getOffhand();

        Iterable<Slot> slots = mainInventory.slots();
        for (Slot slot : slots) {
            serializeSlot(mainInventory, slot, 0, ItemStackStore.INSTANCE, itemViews);
        }
        slots = equipmentInventory.slots();
        for (Slot slot : slots) {
            serializeSlot(equipmentInventory, slot, 100, ItemStackStore.INSTANCE, itemViews);
        }
        serializeSlot(150, offHandSlot, ItemStackStore.INSTANCE, itemViews);

        return itemViews;
    }

    private static void serializeSlot(Inventory parent, Slot slot, int indexOffset,
            ObjectSerializer<LanternItemStack> itemStackSerializer, List<DataView> views) {
        final SlotIndex index = parent.getProperty(slot, SlotIndex.class, "index").get(); // Key doesn't matter
        serializeSlot(index.getValue() + indexOffset, slot, itemStackSerializer, views);
    }

    private static void serializeSlot(int index, Slot slot, ObjectSerializer<LanternItemStack> itemStackSerializer, List<DataView> views) {
        final Optional<ItemStack> optItemStack = slot.peek();
        if (!optItemStack.isPresent()) {
            return;
        }
        final ItemStack itemStack = optItemStack.get();
        final DataView itemView = itemStackSerializer.serialize((LanternItemStack) itemStack);
        itemView.set(SLOT, (byte) index);
        views.add(itemView);
    }
}
