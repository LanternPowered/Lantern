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

import static org.lanternpowered.server.data.util.DataUtil.deserializeManipulatorList;
import static org.lanternpowered.server.data.util.DataUtil.getOrCreateView;
import static org.lanternpowered.server.data.util.DataUtil.getSerializedManipulatorList;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.data.io.store.data.DataHolderStore;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.util.DataQueries;
import org.lanternpowered.server.effect.potion.LanternPotionEffect;
import org.lanternpowered.server.effect.potion.LanternPotionEffectType;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.effect.PotionEffectTypeRegistryModule;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntityStore<T extends LanternEntity> extends DataHolderStore<T> {

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
    private static final DataQuery HEALTH = DataQuery.of("Health");
    private static final DataQuery HEALTH_OLD = DataQuery.of("HealF");
    private static final DataQuery REMAINING_AIR = DataQuery.of("Air");
    private static final DataQuery MAX_AIR = DataQuery.of("maxAir");
    private static final DataQuery DISPLAY_NAME = DataQuery.of("CustomName");
    private static final DataQuery CUSTOM_NAME_VISIBLE = DataQuery.of("CustomNameVisible");
    private static final DataQuery CUSTOM_NAME = DataQuery.of("CustomName");
    private static final DataQuery INVULNERABLE = DataQuery.of("Invulnerable");
    private static final DataQuery PORTAL_COOLDOWN_TICKS = DataQuery.of("PortalCooldown");
    private static final DataQuery ON_GROUND = DataQuery.of("OnGround");
    private static final DataQuery NO_AI = DataQuery.of("NoAI");
    private static final DataQuery PERSISTS = DataQuery.of("PersistenceRequired");
    private static final DataQuery CAN_GRIEF = DataQuery.of("CanGrief");
    private static final DataQuery ABSORPTION_AMOUNT = DataQuery.of("AbsorptionAmount");
    private static final DataQuery CAN_PICK_UP_LOOT = DataQuery.of("CanPickUpLoot");
    private static final DataQuery POTION_EFFECTS = DataQuery.of("ActiveEffects");
    private static final DataQuery POTION_EFFECT_ID = DataQuery.of("Id");
    private static final DataQuery POTION_EFFECT_AMPLIFIER = DataQuery.of("Amplifier");
    private static final DataQuery POTION_EFFECT_DURATION = DataQuery.of("Duration");
    private static final DataQuery POTION_EFFECT_AMBIENT = DataQuery.of("Ambient");
    private static final DataQuery POTION_EFFECT_SHOW_PARTICLES = DataQuery.of("ShowParticles");
    private static final DataQuery EXHAUSTION = DataQuery.of("foodExhaustionLevel");
    private static final DataQuery SATURATION = DataQuery.of("foodSaturationLevel");
    private static final DataQuery FOOD_LEVEL = DataQuery.of("foodLevel");
    private static final DataQuery FOOD_TICK_TIMER = DataQuery.of("foodTickTimer"); // TODO

    // TODO: Use this more globally?
    private static final DataQuery DATA_VERSION = DataQuery.of("DataVersion");

    /**
     * The current data version.
     */
    private static final int CURRENT_DATA_VERSION = 158;

    @Override
    public void deserialize(T entity, DataContainer dataContainer) {
        Optional<Long> uuidMost = dataContainer.getLong(OLD_UNIQUE_ID_MOST);
        Optional<Long> uuidLeast = dataContainer.getLong(OLD_UNIQUE_ID_LEAST);
        UUID uuid;
        if (uuidMost.isPresent() && uuidLeast.isPresent()) {
            uuid = new UUID(uuidMost.get(), uuidLeast.get());
        } else {
            uuidMost = dataContainer.getLong(UNIQUE_ID_MOST);
            uuidLeast = dataContainer.getLong(UNIQUE_ID_LEAST);
            if (uuidMost.isPresent() && uuidLeast.isPresent()) {
                uuid = new UUID(uuidMost.get(), uuidLeast.get());
            } else {
                Optional<String> uuidString = dataContainer.getString(UNIQUE_ID);
                if (uuidString.isPresent()) {
                    uuid = UUID.fromString(uuidString.get());
                } else {
                    uuid = UUID.randomUUID();
                }
            }
        }
        entity.setPosition(fromDoubleList(dataContainer.getDoubleList(POSITION).get()));
        entity.setVelocity(fromDoubleList(dataContainer.getDoubleList(VELOCITY).get()));
        dataContainer.getDoubleList(SCALE).ifPresent(list -> entity.setScale(fromDoubleList(list)));
        final List<Float> rotationList = dataContainer.getFloatList(ROTATION).get();
        // Yaw, Pitch, Roll (X, Y, Z) - Index 0 and 1 are swapped!
        entity.setRotation(new Vector3d(rotationList.get(1), rotationList.get(0), rotationList.size() > 2 ? rotationList.get(2) : 0));
        entity.setOnGround(dataContainer.getInt(ON_GROUND).orElse(0) > 0);
        super.deserialize(entity, dataContainer);
    }

    @Override
    public void serialize(T entity, DataContainer dataContainer) {
        dataContainer.set(DATA_VERSION, CURRENT_DATA_VERSION);
        dataContainer.set(POSITION, toDoubleList(entity.getPosition()));
        dataContainer.set(VELOCITY, toDoubleList(entity.getVelocity()));
        final Vector3d rotation = entity.getRotation();
        // Yaw, Pitch, Roll (X, Y, Z) - Index 0 and 1 are swapped!
        dataContainer.set(ROTATION, Lists.newArrayList((float) rotation.getY(), (float) rotation.getX(), (float) rotation.getZ()));
        dataContainer.set(SCALE, toDoubleList(entity.getScale()));
        dataContainer.set(ON_GROUND, (byte) (entity.isOnGround() ? 1 : 0));
        final UUID uuid = entity.getUniqueId();
        dataContainer.set(UNIQUE_ID_MOST, uuid.getMostSignificantBits());
        dataContainer.set(UNIQUE_ID_LEAST, uuid.getLeastSignificantBits());
        super.serialize(entity, dataContainer);
    }

    static Vector3d fromDoubleList(List<Double> doubleList) {
        return new Vector3d(doubleList.get(0), doubleList.get(1), doubleList.get(2));
    }

    static List<Double> toDoubleList(Vector3d vector3d) {
        return Lists.newArrayList(vector3d.getX(), vector3d.getY(), vector3d.getZ());
    }

    @Override
    public void serializeValues(T object, SimpleValueContainer valueContainer, DataContainer dataContainer) {
        // Here will we remove all the vanilla properties and delegate the
        // rest through the default serialization system
        valueContainer.remove(Keys.FIRE_TICKS).ifPresent(v -> dataContainer.set(FIRE_TICKS, v));
        valueContainer.remove(Keys.FALL_DISTANCE).ifPresent(v -> dataContainer.set(FALL_DISTANCE, v));
        valueContainer.remove(Keys.HEALTH).ifPresent(v -> dataContainer.set(HEALTH, v.floatValue()));
        valueContainer.remove(Keys.REMAINING_AIR).ifPresent(v -> dataContainer.set(REMAINING_AIR, v));
        valueContainer.remove(LanternKeys.ABSORPTION_AMOUNT).ifPresent(v -> dataContainer.set(ABSORPTION_AMOUNT, v.floatValue()));
        DataView spongeView = getOrCreateView(dataContainer, SPONGE_DATA);
        valueContainer.remove(Keys.MAX_AIR).ifPresent(v -> spongeView.set(MAX_AIR, v));
        valueContainer.remove(Keys.CAN_GRIEF).ifPresent(v -> spongeView.set(CAN_GRIEF, (byte) (v ? 1 : 0)));
        valueContainer.remove(Keys.DISPLAY_NAME).ifPresent(v -> dataContainer.set(DISPLAY_NAME, LanternTexts.toLegacy(v)));
        valueContainer.remove(Keys.CUSTOM_NAME_VISIBLE).ifPresent(v -> dataContainer.set(CUSTOM_NAME_VISIBLE, (byte) (v ? 1 : 0)));
        valueContainer.remove(LanternKeys.INVULNERABLE).ifPresent(v -> dataContainer.set(INVULNERABLE, (byte) (v ? 1 : 0)));
        valueContainer.remove(LanternKeys.PORTAL_COOLDOWN_TICKS).ifPresent(v -> dataContainer.set(PORTAL_COOLDOWN_TICKS, v));
        valueContainer.remove(Keys.AI_ENABLED).ifPresent(v -> dataContainer.set(NO_AI, (byte) (v ? 0 : 1)));
        valueContainer.remove(Keys.PERSISTS).ifPresent(v -> dataContainer.set(PERSISTS, (byte) (v ? 1 : 0)));
        valueContainer.remove(LanternKeys.CAN_PICK_UP_LOOT).ifPresent(v -> dataContainer.set(CAN_PICK_UP_LOOT, (byte) (v ? 1 : 0)));
        valueContainer.remove(Keys.DISPLAY_NAME).ifPresent(v -> dataContainer.set(CUSTOM_NAME, LanternTexts.toLegacy(v)));
        valueContainer.remove(Keys.POTION_EFFECTS).ifPresent(v -> {
            if (v.isEmpty()) {
                return;
            }
            // TODO: Allow out impl to use integers as amplifier and use a string as effect id,
            // without breaking the official format
            dataContainer.set(POTION_EFFECTS, v.stream().map(effect -> new MemoryDataContainer()
                    .set(POTION_EFFECT_ID, (byte) ((LanternPotionEffectType) effect.getType()).getInternalId())
                    .set(POTION_EFFECT_AMPLIFIER, (byte) effect.getAmplifier())
                    .set(POTION_EFFECT_DURATION, effect.getDuration())
                    .set(POTION_EFFECT_AMBIENT, (byte) (effect.isAmbient() ? 1 : 0))
                    .set(POTION_EFFECT_SHOW_PARTICLES, (byte) (effect.getShowParticles() ? 1 : 0))));
        });
        valueContainer.remove(Keys.FOOD_LEVEL).ifPresent(v -> dataContainer.set(FOOD_LEVEL, v));
        valueContainer.remove(Keys.EXHAUSTION).ifPresent(v -> dataContainer.set(EXHAUSTION, v.floatValue()));
        valueContainer.remove(Keys.SATURATION).ifPresent(v -> dataContainer.set(SATURATION, v.floatValue()));

        super.serializeValues(object, valueContainer, dataContainer);
    }

    @Override
    public void deserializeValues(T object, SimpleValueContainer valueContainer, DataContainer dataContainer) {
        dataContainer.getInt(FIRE_TICKS).ifPresent(v -> valueContainer.set(Keys.FIRE_TICKS, v));
        dataContainer.getDouble(FALL_DISTANCE).ifPresent(v -> valueContainer.set(Keys.FALL_DISTANCE, v.floatValue()));
        dataContainer.getInt(REMAINING_AIR).ifPresent(v -> valueContainer.set(Keys.REMAINING_AIR, v));
        Optional<Double> health = dataContainer.getDouble(HEALTH);
        // Try for the old health data
        if (!health.isPresent()) {
            health = dataContainer.getDouble(HEALTH_OLD);
        }
        health.ifPresent(v -> valueContainer.set(Keys.HEALTH, v));
        dataContainer.getString(DISPLAY_NAME).ifPresent(v -> valueContainer.set(Keys.DISPLAY_NAME, LanternTexts.fromLegacy(v)));
        dataContainer.getInt(CUSTOM_NAME_VISIBLE).ifPresent(v -> valueContainer.set(Keys.CUSTOM_NAME_VISIBLE, v > 0));
        dataContainer.getInt(INVULNERABLE).ifPresent(v -> valueContainer.set(LanternKeys.INVULNERABLE, v > 0));
        dataContainer.getInt(PORTAL_COOLDOWN_TICKS).ifPresent(v -> valueContainer.set(LanternKeys.PORTAL_COOLDOWN_TICKS, v));
        dataContainer.getInt(NO_AI).ifPresent(v -> valueContainer.set(Keys.AI_ENABLED, v == 0));
        dataContainer.getInt(PERSISTS).ifPresent(v -> valueContainer.set(Keys.PERSISTS, v > 0));
        dataContainer.getView(SPONGE_DATA).ifPresent(view -> {
            view.getInt(MAX_AIR).ifPresent(v -> valueContainer.set(Keys.MAX_AIR, v));
            view.getInt(CAN_GRIEF).ifPresent(v -> valueContainer.set(Keys.CAN_GRIEF, v > 0));
        });
        dataContainer.getDouble(ABSORPTION_AMOUNT).ifPresent(v -> valueContainer.set(LanternKeys.ABSORPTION_AMOUNT, v));
        dataContainer.getDouble(CAN_PICK_UP_LOOT).ifPresent(v -> valueContainer.set(LanternKeys.CAN_PICK_UP_LOOT, v > 0));
        dataContainer.getString(CUSTOM_NAME).ifPresent(v -> valueContainer.set(Keys.DISPLAY_NAME, LanternTexts.fromLegacy(v)));
        dataContainer.getViewList(POTION_EFFECTS).ifPresent(v -> {
            if (v.isEmpty()) {
                return;
            }
            PotionEffectTypeRegistryModule module = Lantern.getRegistry().getRegistryModule(PotionEffectTypeRegistryModule.class).get();
            List<PotionEffect> potionEffects = new ArrayList<>();
            for (DataView view : v) {
                int internalId = view.getInt(POTION_EFFECT_ID).get() & 0xff;
                PotionEffectType type = module.getByInternalId(internalId).orElse(null);
                if (type == null) {
                    Lantern.getLogger().warn("Deserialized a potion effect type with unknown id: " + internalId);
                    continue;
                }
                int amplifier = view.getInt(POTION_EFFECT_AMPLIFIER).get() & 0xff;
                int duration = view.getInt(POTION_EFFECT_DURATION).get();
                boolean ambient = view.getInt(POTION_EFFECT_AMBIENT).orElse(0) > 0;
                boolean showParticles = view.getInt(POTION_EFFECT_SHOW_PARTICLES).orElse(0) > 0;
                potionEffects.add(new LanternPotionEffect(type, duration, amplifier, ambient, showParticles));
            }
            if (!potionEffects.isEmpty()) {
                valueContainer.set(Keys.POTION_EFFECTS, potionEffects);
            }
        });
        dataContainer.getInt(FOOD_LEVEL).ifPresent(v -> valueContainer.set(Keys.FOOD_LEVEL, v));
        dataContainer.getDouble(EXHAUSTION).ifPresent(v -> valueContainer.set(Keys.EXHAUSTION, v));
        dataContainer.getDouble(SATURATION).ifPresent(v -> valueContainer.set(Keys.SATURATION, v));

        super.deserializeValues(object, valueContainer, dataContainer);
    }

    @Override
    public void serializeAdditionalData(T object, List<DataManipulator<?, ?>> manipulators, DataContainer dataContainer) {
        DataView spongeData = getOrCreateView(dataContainer, SPONGE_DATA);
        if (!manipulators.isEmpty()) {
            spongeData.set(DataQueries.CUSTOM_MANIPULATORS, getSerializedManipulatorList(manipulators));
        }
    }

    @Override
    public void deserializeAdditionalData(T object, List<DataManipulator<?, ?>> manipulators, DataContainer dataContainer) {
        try {
            dataContainer.getView(SPONGE_DATA).ifPresent(view -> dataContainer.getViewList(DataQueries.CUSTOM_MANIPULATORS)
                    .ifPresent(views -> manipulators.addAll(deserializeManipulatorList(views))));
        } catch (InvalidDataException e) {
            Lantern.getLogger().error("Could not deserialize custom plugin data! ", e);
        }
    }
}
