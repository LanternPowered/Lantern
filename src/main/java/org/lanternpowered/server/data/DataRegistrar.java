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
package org.lanternpowered.server.data;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistry;
import org.lanternpowered.server.data.meta.LanternPatternLayer;
import org.lanternpowered.server.data.persistence.DataTranslators;
import org.lanternpowered.server.data.persistence.DataTypeSerializers;
import org.lanternpowered.server.data.property.block.GroundLuminancePropertyStore;
import org.lanternpowered.server.data.property.block.SkyLuminancePropertyStore;
import org.lanternpowered.server.data.property.entity.DominantHandPropertyStore;
import org.lanternpowered.server.data.value.LanternValueFactory;
import org.lanternpowered.server.effect.potion.LanternPotionEffectBuilder;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.registry.type.data.DataSerializerRegistry;
import org.lanternpowered.server.item.enchantment.LanternEnchantmentBuilder;
import org.lanternpowered.server.profile.LanternGameProfileBuilder;
import org.lanternpowered.server.profile.LanternProfilePropertyBuilder;
import org.lanternpowered.server.text.serializer.BookViewConfigSerializer;
import org.lanternpowered.server.text.serializer.TextConfigSerializer;
import org.lanternpowered.server.util.copy.Copyable;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.property.Properties;
import org.spongepowered.api.data.property.PropertyRegistry;
import org.spongepowered.api.data.type.BodyPart;
import org.spongepowered.api.data.type.BodyParts;
import org.spongepowered.api.data.type.WireAttachmentType;
import org.spongepowered.api.data.type.WireAttachmentTypes;
import org.spongepowered.api.data.value.CompositeValueStore;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.RespawnLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DataRegistrar {

    public static void setupRegistrations(LanternGame game) {
        Copyable.register(ImmutableMap.class, map -> map);
        Copyable.register(ImmutableList.class, list -> list);
        Copyable.register(ImmutableSet.class, set -> set);
        Copyable.register(List.class, ArrayList::new);
        Copyable.register(Set.class, HashSet::new);
        Copyable.register(Map.class, HashMap::new);

        final PropertyRegistry propertyRegistry = game.getPropertyRegistry();

        // Block property stores
        propertyRegistry.register(Properties.SKY_LUMINANCE, new SkyLuminancePropertyStore());
        propertyRegistry.register(Properties.BLOCK_LUMINANCE, new GroundLuminancePropertyStore());

        // Entity property stores
        propertyRegistry.register(Properties.DOMINANT_HAND, new DominantHandPropertyStore());

        // Item property stores
        // TODO
        // propertyRegistry.register(BurningFuelProperty.class, new BurningFuelPropertyStore());

        final LanternDataManager dataManager = game.getDataManager();
        // Register the data type serializers
        DataTypeSerializers.registerSerializers(DataSerializerRegistry.INSTANCE);
        // Register the data serializers
        DataTranslators.registerSerializers(DataSerializerRegistry.INSTANCE);

        // Register the data builders
        dataManager.registerBuilder(PatternLayer.class, new LanternPatternLayer.Builder(game));
        dataManager.registerBuilder(Text.class, new TextConfigSerializer());
        dataManager.registerBuilder(BookView.class, new BookViewConfigSerializer());
        dataManager.registerBuilder(PotionEffect.class, new LanternPotionEffectBuilder());
        dataManager.registerBuilder(RespawnLocation.class, new RespawnLocation.Builder());
        dataManager.registerBuilder(Enchantment.class, new LanternEnchantmentBuilder());
        dataManager.registerBuilder(ProfileProperty.class, new LanternProfilePropertyBuilder());
        dataManager.registerBuilder(GameProfile.class, new LanternGameProfileBuilder());

        final LanternValueFactory valueFactory = LanternValueFactory.get();
        valueFactory.registerKey(Keys.BIG_MUSHROOM_PORES).add(builder -> builder
                .applicableTester(valueContainer ->
                        valueContainer.supports(Keys.BIG_MUSHROOM_PORES_UP) || valueContainer.supports(Keys.BIG_MUSHROOM_PORES_WEST) ||
                                valueContainer.supports(Keys.BIG_MUSHROOM_PORES_SOUTH) || valueContainer.supports(Keys.BIG_MUSHROOM_PORES_NORTH) ||
                                valueContainer.supports(Keys.BIG_MUSHROOM_PORES_EAST) || valueContainer.supports(Keys.BIG_MUSHROOM_PORES_DOWN))
                .retrieveHandler((valueContainer, key)  -> {
                    final Set<Direction> directions = new HashSet<>();
                    if (valueContainer.get(Keys.BIG_MUSHROOM_PORES_WEST).orElse(false)) {
                        directions.add(Direction.WEST);
                    }
                    if (valueContainer.get(Keys.BIG_MUSHROOM_PORES_EAST).orElse(false)) {
                        directions.add(Direction.EAST);
                    }
                    if (valueContainer.get(Keys.BIG_MUSHROOM_PORES_SOUTH).orElse(false)) {
                        directions.add(Direction.SOUTH);
                    }
                    if (valueContainer.get(Keys.BIG_MUSHROOM_PORES_NORTH).orElse(false)) {
                        directions.add(Direction.NORTH);
                    }
                    if (valueContainer.get(Keys.BIG_MUSHROOM_PORES_UP).orElse(false)) {
                        directions.add(Direction.UP);
                    }
                    if (valueContainer.get(Keys.BIG_MUSHROOM_PORES_DOWN).orElse(false)) {
                        directions.add(Direction.DOWN);
                    }
                    return Optional.of(directions);
                })
                .offerHandler((valueContainer, key, directions) -> {
                    if (valueContainer instanceof CompositeValueStore) {
                        final CompositeValueStore store = (CompositeValueStore) valueContainer;
                        final DataTransactionResult.Builder resultBuilder = DataTransactionResult.builder();
                        resultBuilder.absorbResult(store.offer(Keys.BIG_MUSHROOM_PORES_WEST, directions.contains(Direction.WEST)));
                        resultBuilder.absorbResult(store.offer(Keys.BIG_MUSHROOM_PORES_EAST, directions.contains(Direction.EAST)));
                        resultBuilder.absorbResult(store.offer(Keys.BIG_MUSHROOM_PORES_SOUTH, directions.contains(Direction.SOUTH)));
                        resultBuilder.absorbResult(store.offer(Keys.BIG_MUSHROOM_PORES_NORTH, directions.contains(Direction.NORTH)));
                        resultBuilder.absorbResult(store.offer(Keys.BIG_MUSHROOM_PORES_UP, directions.contains(Direction.UP)));
                        resultBuilder.absorbResult(store.offer(Keys.BIG_MUSHROOM_PORES_DOWN, directions.contains(Direction.DOWN)));
                        return resultBuilder.result(DataTransactionResult.Type.SUCCESS).build();
                    }
                    return DataTransactionResult.successNoData();
                })
                .failAlwaysRemoveHandler());
        valueFactory.registerKey(Keys.CONNECTED_DIRECTIONS).add(builder -> builder
                .applicableTester(valueContainer ->
                        valueContainer.supports(Keys.CONNECTED_WEST) || valueContainer.supports(Keys.CONNECTED_EAST) ||
                        valueContainer.supports(Keys.CONNECTED_NORTH) || valueContainer.supports(Keys.CONNECTED_SOUTH))
                .retrieveHandler((valueContainer, key)  -> {
                    final Set<Direction> directions = new HashSet<>();
                    if (valueContainer.get(Keys.CONNECTED_WEST).orElse(false)) {
                        directions.add(Direction.WEST);
                    }
                    if (valueContainer.get(Keys.CONNECTED_EAST).orElse(false)) {
                        directions.add(Direction.EAST);
                    }
                    if (valueContainer.get(Keys.CONNECTED_SOUTH).orElse(false)) {
                        directions.add(Direction.SOUTH);
                    }
                    if (valueContainer.get(Keys.CONNECTED_NORTH).orElse(false)) {
                        directions.add(Direction.NORTH);
                    }
                    return Optional.of(directions);
                })
                .offerHandler((valueContainer, key, directions) -> {
                    if (valueContainer instanceof ICompositeValueStore) {
                        final ICompositeValueStore store = (ICompositeValueStore) valueContainer;
                        final DataTransactionResult.Builder resultBuilder = DataTransactionResult.builder();
                        resultBuilder.absorbResult(store.offerNoEvents(Keys.CONNECTED_WEST, directions.contains(Direction.WEST)));
                        resultBuilder.absorbResult(store.offerNoEvents(Keys.CONNECTED_EAST, directions.contains(Direction.EAST)));
                        resultBuilder.absorbResult(store.offerNoEvents(Keys.CONNECTED_SOUTH, directions.contains(Direction.SOUTH)));
                        resultBuilder.absorbResult(store.offerNoEvents(Keys.CONNECTED_NORTH, directions.contains(Direction.NORTH)));
                        return resultBuilder.result(DataTransactionResult.Type.SUCCESS).build();
                    }
                    return DataTransactionResult.successNoData();
                })
                .failAlwaysRemoveHandler());
        valueFactory.registerKey(Keys.WIRE_ATTACHMENTS).add(builder -> builder
                .applicableTester(valueContainer ->
                        valueContainer.supports(Keys.WIRE_ATTACHMENT_WEST) || valueContainer.supports(Keys.WIRE_ATTACHMENT_EAST) ||
                                valueContainer.supports(Keys.WIRE_ATTACHMENT_NORTH) || valueContainer.supports(Keys.WIRE_ATTACHMENT_SOUTH))
                .retrieveHandler((valueContainer, key) -> {
                    final Map<Direction, WireAttachmentType> attachments = new HashMap<>();
                    valueContainer.get(Keys.WIRE_ATTACHMENT_WEST).ifPresent(type -> attachments.put(Direction.WEST, type));
                    valueContainer.get(Keys.WIRE_ATTACHMENT_EAST).ifPresent(type -> attachments.put(Direction.EAST, type));
                    valueContainer.get(Keys.WIRE_ATTACHMENT_SOUTH).ifPresent(type -> attachments.put(Direction.SOUTH, type));
                    valueContainer.get(Keys.WIRE_ATTACHMENT_NORTH).ifPresent(type -> attachments.put(Direction.NORTH, type));
                    return Optional.of(attachments);
                })
                .offerHandler((key, valueContainer, attachments) -> {
                    if (valueContainer instanceof ICompositeValueStore) {
                        final ICompositeValueStore store = (ICompositeValueStore) valueContainer;
                        final DataTransactionResult.Builder resultBuilder = DataTransactionResult.builder();
                        WireAttachmentType type = attachments.get(Direction.WEST);
                        resultBuilder.absorbResult(store.offerNoEvents(Keys.CONNECTED_WEST,
                                type == null ? WireAttachmentTypes.NONE : type));
                        type = attachments.get(Direction.EAST);
                        resultBuilder.absorbResult(store.offerNoEvents(Keys.CONNECTED_EAST,
                                type == null ? WireAttachmentTypes.NONE : type));
                        type = attachments.get(Direction.SOUTH);
                        resultBuilder.absorbResult(store.offerNoEvents(Keys.CONNECTED_SOUTH,
                                type == null ? WireAttachmentTypes.NONE : type));
                        type = attachments.get(Direction.NORTH);
                        resultBuilder.absorbResult(store.offerNoEvents(Keys.CONNECTED_NORTH,
                                type == null ? WireAttachmentTypes.NONE : type));
                        return resultBuilder.result(DataTransactionResult.Type.SUCCESS).build();
                    }
                    return DataTransactionResult.successNoData();
                })
                .failAlwaysRemoveHandler());
        valueFactory.registerKey(Keys.BODY_ROTATIONS).add(builder -> builder
                .applicableTester(valueContainer ->
                        valueContainer.supports(Keys.RIGHT_ARM_ROTATION) || valueContainer.supports(Keys.LEFT_ARM_ROTATION) ||
                                valueContainer.supports(Keys.RIGHT_LEG_ROTATION) || valueContainer.supports(Keys.LEFT_LEG_ROTATION) ||
                                valueContainer.supports(Keys.HEAD_ROTATION) || valueContainer.supports(Keys.CHEST_ROTATION))
                .retrieveHandler((valueContainer, key) -> {
                    final Map<BodyPart, Vector3d> rotations = new HashMap<>();
                    valueContainer.get(Keys.RIGHT_ARM_ROTATION).ifPresent(type -> rotations.put(BodyParts.RIGHT_ARM, type));
                    valueContainer.get(Keys.RIGHT_LEG_ROTATION).ifPresent(type -> rotations.put(BodyParts.RIGHT_LEG, type));
                    valueContainer.get(Keys.LEFT_ARM_ROTATION).ifPresent(type -> rotations.put(BodyParts.LEFT_ARM, type));
                    valueContainer.get(Keys.LEFT_LEG_ROTATION).ifPresent(type -> rotations.put(BodyParts.LEFT_LEG, type));
                    valueContainer.get(Keys.HEAD_ROTATION).ifPresent(type -> rotations.put(BodyParts.HEAD, type));
                    valueContainer.get(Keys.CHEST_ROTATION).ifPresent(type -> rotations.put(BodyParts.CHEST, type));
                    return Optional.of(rotations);
                })
                .offerHandler((key, valueContainer, rotations) -> {
                    if (valueContainer instanceof CompositeValueStore) {
                        final ICompositeValueStore store = (ICompositeValueStore) valueContainer;
                        final DataTransactionResult.Builder resultBuilder = DataTransactionResult.builder();
                        Vector3d rot;
                        if ((rot = rotations.get(BodyParts.RIGHT_ARM)) != null) {
                            resultBuilder.absorbResult(store.offerNoEvents(Keys.RIGHT_ARM_ROTATION, rot));
                        }
                        if ((rot = rotations.get(BodyParts.RIGHT_LEG)) != null) {
                            resultBuilder.absorbResult(store.offerNoEvents(Keys.RIGHT_LEG_ROTATION, rot));
                        }
                        if ((rot = rotations.get(BodyParts.LEFT_ARM)) != null) {
                            resultBuilder.absorbResult(store.offerNoEvents(Keys.LEFT_ARM_ROTATION, rot));
                        }
                        if ((rot = rotations.get(BodyParts.LEFT_LEG)) != null) {
                            resultBuilder.absorbResult(store.offerNoEvents(Keys.LEFT_LEG_ROTATION, rot));
                        }
                        if ((rot = rotations.get(BodyParts.HEAD)) != null) {
                            resultBuilder.absorbResult(store.offerNoEvents(Keys.HEAD_ROTATION, rot));
                        }
                        if ((rot = rotations.get(BodyParts.CHEST)) != null) {
                            resultBuilder.absorbResult(store.offerNoEvents(Keys.CHEST_ROTATION, rot));
                        }
                        return resultBuilder.result(DataTransactionResult.Type.SUCCESS).build();
                    }
                    return DataTransactionResult.successNoData();
                })
                .failAlwaysRemoveHandler());

        DataManipulatorRegistry.get();
    }

    public static void finalizeRegistrations(LanternGame game) {
        game.getPropertyRegistry().completeRegistration();
    }
}
