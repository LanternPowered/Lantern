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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.lanternpowered.server.data.DataQueries;
import org.lanternpowered.server.data.io.store.IdentifiableObjectStore;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.data.io.store.data.LocalMutableDataHolderStore;
import org.lanternpowered.server.data.io.store.misc.PotionEffectSerializer;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.math.vector.Vector3d;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class EntityStore<T extends LanternEntity> extends LocalMutableDataHolderStore<T> implements IdentifiableObjectStore<T> {

    private static final DataQuery SPONGE_DATA = DataQueries.FORGE_DATA.then(DataQueries.SPONGE_DATA);
    private static final DataQuery POSITION = DataQuery.of("Pos");
    private static final DataQuery VELOCITY = DataQuery.of("Motion");
    private static final DataQuery ROTATION = DataQuery.of("Rotation");
    private static final DataQuery SCALE = DataQuery.of("Scale"); // Lantern
    private static final DataQuery FALL_DISTANCE = DataQuery.of("FallDistance");
    private static final DataQuery FIRE_TICKS = DataQuery.of("Fire");
    private static final DataQuery UNIQUE_ID_MOST = DataQuery.of("UUIDMost");
    private static final DataQuery UNIQUE_ID_LEAST = DataQuery.of("UUIDLeast");
    static final DataQuery UNIQUE_ID = DataQuery.of("UUID");
    private static final DataQuery OLD_UNIQUE_ID_MOST = DataQuery.of("PersistentIDMSB");
    private static final DataQuery OLD_UNIQUE_ID_LEAST = DataQuery.of("PersistentIDLSB");
    // A field that will be converted if present
    private static final DataQuery BUKKIT_MAX_HEALTH = DataQuery.of("Bukkit.MaxHealth");
    private static final DataQuery OLD_HEALTH = DataQuery.of("HealF");
    private static final DataQuery HEALTH = DataQuery.of("Health");
    private static final DataQuery REMAINING_AIR = DataQuery.of("Air");
    private static final DataQuery MAX_AIR = DataQuery.of("maxAir");
    private static final DataQuery DISPLAY_NAME = DataQuery.of("CustomName");
    private static final DataQuery CUSTOM_NAME_VISIBLE = DataQuery.of("CustomNameVisible");
    private static final DataQuery CUSTOM_NAME = DataQuery.of("CustomName");
    private static final DataQuery INVULNERABLE = DataQuery.of("Invulnerable");
    private static final DataQuery PORTAL_COOLDOWN_TICKS = DataQuery.of("PortalCooldown");
    private static final DataQuery ON_GROUND = DataQuery.of("OnGround");
    private static final DataQuery NO_AI = DataQuery.of("NoAI");
    private static final DataQuery PERSISTENT = DataQuery.of("PersistenceRequired");
    private static final DataQuery CAN_GRIEF = DataQuery.of("CanGrief");
    private static final DataQuery ABSORPTION_AMOUNT = DataQuery.of("AbsorptionAmount");
    private static final DataQuery CAN_PICK_UP_LOOT = DataQuery.of("CanPickUpLoot");
    private static final DataQuery POTION_EFFECTS = DataQuery.of("ActiveEffects");
    private static final DataQuery EXHAUSTION = DataQuery.of("foodExhaustionLevel");
    private static final DataQuery SATURATION = DataQuery.of("foodSaturationLevel");
    private static final DataQuery FOOD_LEVEL = DataQuery.of("foodLevel");
    private static final DataQuery FOOD_TICK_TIMER = DataQuery.of("foodTickTimer"); // TODO
    private static final DataQuery IS_ELYTRA_FLYING = DataQuery.of("FallFlying");
    private static final DataQuery IS_GLOWING = DataQuery.of("Glowing");
    private static final DataQuery TAGS = DataQuery.of("Tags");
    private static final DataQuery NO_GRAVITY = DataQuery.of("NoGravity");

    // TODO: Use this more globally?
    private static final DataQuery DATA_VERSION = DataQuery.of("DataVersion");

    /**
     * The current data version.
     */
    private static final int CURRENT_DATA_VERSION = 158;

    @Override
    public UUID deserializeUniqueId(DataView dataView) {
        Optional<Long> uuidMost = dataView.getLong(UNIQUE_ID_MOST);
        Optional<Long> uuidLeast = dataView.getLong(UNIQUE_ID_LEAST);
        if (uuidMost.isPresent() && uuidLeast.isPresent()) {
            return new UUID(uuidMost.get(), uuidLeast.get());
        } else {
            // Try to convert from an older format
            uuidMost = dataView.getLong(OLD_UNIQUE_ID_MOST);
            uuidLeast = dataView.getLong(OLD_UNIQUE_ID_LEAST);
            if (uuidMost.isPresent() && uuidLeast.isPresent()) {
                return new UUID(uuidMost.get(), uuidLeast.get());
            } else {
                final Optional<String> uuidString = dataView.getString(UNIQUE_ID);
                if (uuidString.isPresent()) {
                    return UUID.fromString(uuidString.get());
                }
            }
        }
        return UUID.randomUUID();
    }

    @Override
    public void serializeUniqueId(DataView dataView, UUID uniqueId) {
        dataView.set(UNIQUE_ID_MOST, uniqueId.getMostSignificantBits());
        dataView.set(UNIQUE_ID_LEAST, uniqueId.getLeastSignificantBits());
    }

    @Override
    public void deserialize(T entity, DataView dataView) {
        entity.setPosition(fromDoubleList(dataView.getDoubleList(POSITION).get()));
        entity.setVelocity(fromDoubleList(dataView.getDoubleList(VELOCITY).get()));
        dataView.getDoubleList(SCALE).ifPresent(list -> entity.setScale(fromDoubleList(list)));
        final List<Float> rotationList = dataView.getFloatList(ROTATION).get();
        // Yaw, Pitch, Roll (X, Y, Z) - Index 0 and 1 are swapped!
        entity.setRotation(new Vector3d(rotationList.get(1), rotationList.get(0), rotationList.size() > 2 ? rotationList.get(2) : 0));
        entity.setOnGround(dataView.getInt(ON_GROUND).orElse(0) > 0);
        super.deserialize(entity, dataView);
    }

    @Override
    public void serialize(T entity, DataView dataView) {
        dataView.set(DATA_VERSION, CURRENT_DATA_VERSION);
        dataView.set(POSITION, toDoubleList(entity.getPosition()));
        final Vector3d rotation = entity.getRotation();
        // Yaw, Pitch, Roll (X, Y, Z) - Index 0 and 1 are swapped!
        dataView.set(ROTATION, Lists.newArrayList((float) rotation.getY(), (float) rotation.getX(), (float) rotation.getZ()));
        dataView.set(SCALE, toDoubleList(entity.getScale()));
        dataView.set(ON_GROUND, (byte) (entity.isOnGround() ? 1 : 0));
        super.serialize(entity, dataView);
    }

    static Vector3d fromDoubleList(List<Double> doubleList) {
        return new Vector3d(doubleList.get(0), doubleList.get(1), doubleList.get(2));
    }

    static List<Double> toDoubleList(Vector3d vector3d) {
        return Lists.newArrayList(vector3d.getX(), vector3d.getY(), vector3d.getZ());
    }

    @Override
    public void serializeValues(T object, SimpleValueContainer valueContainer, DataView dataView) {
        // Here will we remove all the vanilla properties and delegate the
        // rest through the default serialization system
        valueContainer.remove(Keys.VELOCITY).ifPresent(v -> dataView.set(VELOCITY, toDoubleList(v)));
        valueContainer.remove(Keys.FIRE_TICKS).ifPresent(v -> dataView.set(FIRE_TICKS, v));
        valueContainer.remove(Keys.FALL_DISTANCE).ifPresent(v -> dataView.set(FALL_DISTANCE, v));
        valueContainer.remove(Keys.HEALTH).ifPresent(v -> dataView.set(HEALTH, v.floatValue()));
        valueContainer.remove(Keys.REMAINING_AIR).ifPresent(v -> dataView.set(REMAINING_AIR, v));
        valueContainer.remove(Keys.ABSORPTION).ifPresent(v -> dataView.set(ABSORPTION_AMOUNT, v.floatValue()));
        final DataView spongeView = getOrCreateView(dataView, SPONGE_DATA);
        valueContainer.remove(Keys.MAX_AIR).ifPresent(v -> spongeView.set(MAX_AIR, v));
        valueContainer.remove(Keys.CAN_GRIEF).ifPresent(v -> spongeView.set(CAN_GRIEF, (byte) (v ? 1 : 0)));
        valueContainer.remove(Keys.DISPLAY_NAME).ifPresent(v -> dataView.set(DISPLAY_NAME, LanternTexts.toLegacy(v)));
        valueContainer.remove(Keys.CUSTOM_NAME_VISIBLE).ifPresent(v -> dataView.set(CUSTOM_NAME_VISIBLE, (byte) (v ? 1 : 0)));
        valueContainer.remove(Keys.INVULNERABLE).ifPresent(v -> dataView.set(INVULNERABLE, (byte) (v ? 1 : 0)));
        valueContainer.remove(LanternKeys.PORTAL_COOLDOWN_TICKS).ifPresent(v -> dataView.set(PORTAL_COOLDOWN_TICKS, v));
        valueContainer.remove(Keys.AI_ENABLED).ifPresent(v -> dataView.set(NO_AI, (byte) (v ? 0 : 1)));
        valueContainer.remove(Keys.PERSISTENT).ifPresent(v -> dataView.set(PERSISTENT, (byte) (v ? 1 : 0)));
        valueContainer.remove(LanternKeys.CAN_PICK_UP_LOOT).ifPresent(v -> dataView.set(CAN_PICK_UP_LOOT, (byte) (v ? 1 : 0)));
        valueContainer.remove(Keys.DISPLAY_NAME).ifPresent(v -> dataView.set(CUSTOM_NAME, LanternTexts.toLegacy(v)));
        valueContainer.remove(Keys.POTION_EFFECTS).ifPresent(v -> {
            if (v.isEmpty()) {
                return;
            }
            // TODO: Allow out impl to use integers as amplifier and use a string as effect id,
            // without breaking the official format
            dataView.set(POTION_EFFECTS, v.stream()
                    .map(PotionEffectSerializer::serialize)
                    .collect(Collectors.toList()));
        });
        valueContainer.remove(Keys.FOOD_LEVEL).ifPresent(v -> dataView.set(FOOD_LEVEL, v));
        valueContainer.remove(Keys.EXHAUSTION).ifPresent(v -> dataView.set(EXHAUSTION, v.floatValue()));
        valueContainer.remove(Keys.SATURATION).ifPresent(v -> dataView.set(SATURATION, v.floatValue()));
        valueContainer.remove(Keys.IS_ELYTRA_FLYING).ifPresent(v -> dataView.set(IS_ELYTRA_FLYING, (byte) (v ? 1 : 0)));
        valueContainer.remove(Keys.GLOWING).ifPresent(v -> dataView.set(IS_GLOWING, (byte) (v ? 1 : 0)));
        valueContainer.remove(Keys.TAGS).ifPresent(v -> dataView.set(TAGS, v));
        valueContainer.remove(Keys.HAS_GRAVITY).ifPresent(v -> dataView.set(NO_GRAVITY, (byte) (v ? 0 : 1)));
        super.serializeValues(object, valueContainer, dataView);
    }

    @Override
    public void deserializeValues(T object, SimpleValueContainer valueContainer, DataView dataView) {
        dataView.getInt(FIRE_TICKS).ifPresent(v -> valueContainer.set(Keys.FIRE_TICKS, v));
        dataView.getDouble(FALL_DISTANCE).ifPresent(v -> valueContainer.set(Keys.FALL_DISTANCE, v));
        dataView.getInt(REMAINING_AIR).ifPresent(v -> valueContainer.set(Keys.REMAINING_AIR, v));
        // The health
        Optional<Double> health = dataView.getDouble(HEALTH);
        if (!health.isPresent()) {
            // Try to convert old data
            health = dataView.getDouble(OLD_HEALTH);
        }
        health.ifPresent(v -> valueContainer.set(Keys.HEALTH, v));
        dataView.getString(DISPLAY_NAME).ifPresent(v -> valueContainer.set(Keys.DISPLAY_NAME, LanternTexts.fromLegacy(v)));
        dataView.getInt(CUSTOM_NAME_VISIBLE).ifPresent(v -> valueContainer.set(Keys.CUSTOM_NAME_VISIBLE, v > 0));
        dataView.getInt(INVULNERABLE).ifPresent(v -> valueContainer.set(Keys.INVULNERABLE, v > 0));
        dataView.getInt(PORTAL_COOLDOWN_TICKS).ifPresent(v -> valueContainer.set(LanternKeys.PORTAL_COOLDOWN_TICKS, v));
        dataView.getInt(NO_AI).ifPresent(v -> valueContainer.set(Keys.AI_ENABLED, v == 0));
        dataView.getInt(PERSISTENT).ifPresent(v -> valueContainer.set(Keys.PERSISTENT, v > 0));
        dataView.getView(SPONGE_DATA).ifPresent(view -> {
            view.getInt(MAX_AIR).ifPresent(v -> valueContainer.set(Keys.MAX_AIR, v));
            view.getInt(CAN_GRIEF).ifPresent(v -> valueContainer.set(Keys.CAN_GRIEF, v > 0));
        });
        dataView.getDouble(ABSORPTION_AMOUNT).ifPresent(v -> valueContainer.set(Keys.ABSORPTION, v));
        dataView.getDouble(CAN_PICK_UP_LOOT).ifPresent(v -> valueContainer.set(LanternKeys.CAN_PICK_UP_LOOT, v > 0));
        dataView.getString(CUSTOM_NAME).ifPresent(v -> valueContainer.set(Keys.DISPLAY_NAME, LanternTexts.fromLegacy(v)));
        dataView.getViewList(POTION_EFFECTS).ifPresent(v -> {
            if (v.isEmpty()) {
                return;
            }
            valueContainer.set(Keys.POTION_EFFECTS, v.stream()
                    .map(PotionEffectSerializer::deserialize)
                    .filter(Objects::nonNull)
                    .collect(ImmutableList.toImmutableList()));
        });
        dataView.getInt(FOOD_LEVEL).ifPresent(v -> valueContainer.set(Keys.FOOD_LEVEL, v));
        dataView.getDouble(EXHAUSTION).ifPresent(v -> valueContainer.set(Keys.EXHAUSTION, v));
        dataView.getDouble(SATURATION).ifPresent(v -> valueContainer.set(Keys.SATURATION, v));
        dataView.getInt(IS_ELYTRA_FLYING).ifPresent(v -> valueContainer.set(Keys.IS_ELYTRA_FLYING, v > 0));
        dataView.getInt(IS_GLOWING).ifPresent(v -> valueContainer.set(Keys.GLOWING, v > 0));
        dataView.getStringList(TAGS).ifPresent(v -> valueContainer.set(Keys.TAGS, new HashSet<>(v)));
        dataView.getInt(NO_GRAVITY).ifPresent(v -> valueContainer.set(Keys.HAS_GRAVITY, v <= 0));
        super.deserializeValues(object, valueContainer, dataView);
    }
}
