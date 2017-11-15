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
package org.lanternpowered.server.network.vanilla.message.processor.play;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.netty.handler.codec.CodecException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.data.type.LanternNotePitch;
import org.lanternpowered.server.effect.particle.LanternParticleEffect;
import org.lanternpowered.server.effect.particle.LanternParticleType;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.network.entity.EntityProtocolManager;
import org.lanternpowered.server.network.entity.parameter.DefaultParameterList;
import org.lanternpowered.server.network.entity.vanilla.EntityParameters;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDestroyEntities;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityMetadata;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityStatus;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnObject;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnParticle;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Direction;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public final class ProcessorPlayOutParticleEffect implements Processor<MessagePlayOutParticleEffect> {

    /**
     * Using a cache to bring the amount of operations down for spawning particles.
     */
    private final LoadingCache<ParticleEffect, ICachedMessage> cache = Caffeine.newBuilder()
            .weakKeys().expireAfterAccess(3, TimeUnit.MINUTES)
            .build(this::preProcess);

    private final Object2IntMap<PotionEffectType> potionEffectTypeToId = new Object2IntOpenHashMap<>();

    {
        this.potionEffectTypeToId.defaultReturnValue(0); // Default to water?
        this.potionEffectTypeToId.put(PotionEffectTypes.NIGHT_VISION, 5);
        this.potionEffectTypeToId.put(PotionEffectTypes.INVISIBILITY, 7);
        this.potionEffectTypeToId.put(PotionEffectTypes.JUMP_BOOST, 9);
        this.potionEffectTypeToId.put(PotionEffectTypes.FIRE_RESISTANCE, 12);
        this.potionEffectTypeToId.put(PotionEffectTypes.SPEED, 14);
        this.potionEffectTypeToId.put(PotionEffectTypes.SLOWNESS, 17);
        this.potionEffectTypeToId.put(PotionEffectTypes.WATER_BREATHING, 19);
        this.potionEffectTypeToId.put(PotionEffectTypes.INSTANT_HEALTH, 21);
        this.potionEffectTypeToId.put(PotionEffectTypes.INSTANT_DAMAGE, 23);
        this.potionEffectTypeToId.put(PotionEffectTypes.POISON, 25);
        this.potionEffectTypeToId.put(PotionEffectTypes.REGENERATION, 28);
        this.potionEffectTypeToId.put(PotionEffectTypes.STRENGTH, 31);
        this.potionEffectTypeToId.put(PotionEffectTypes.WEAKNESS, 34);
        this.potionEffectTypeToId.put(PotionEffectTypes.LUCK, 36);
    }

    private static int getBlockState(LanternParticleEffect effect, Optional<BlockState> defaultBlockState) {
        final Optional<BlockState> blockState = effect.getOption(ParticleOptions.BLOCK_STATE);
        if (blockState.isPresent()) {
            return BlockRegistryModule.get().getStateInternalId(blockState.get());
        } else {
            final Optional<ItemStackSnapshot> optSnapshot = effect.getOption(ParticleOptions.ITEM_STACK_SNAPSHOT);
            if (optSnapshot.isPresent()) {
                final ItemStackSnapshot snapshot = optSnapshot.get();
                final Optional<BlockType> blockType = snapshot.getType().getBlock();
                if (blockType.isPresent()) {
                    final BlockState state;
                    if (blockType.get().getDefaultState().getTraits().isEmpty()) {
                        state = blockType.get().getDefaultState();
                    } else {
                        final BlockState.Builder builder = BlockState.builder().blockType(blockType.get());
                        snapshot.getValues().forEach(value -> builder.add((Key) value.getKey(), value.get()));
                        state = builder.build();
                    }
                    return BlockRegistryModule.get().getStateInternalId(state);
                } else {
                    return 0;
                }
            } else {
                return BlockRegistryModule.get().getStateInternalId(defaultBlockState.get());
            }
        }
    }

    private static int getDirectionData(Direction direction) {
        if (direction.isSecondaryOrdinal()) {
            direction = Direction.getClosest(direction.asOffset(), Direction.Division.ORDINAL);
        }
        switch (direction) {
            case SOUTHEAST:
                return 0;
            case SOUTH:
                return 1;
            case SOUTHWEST:
                return 2;
            case EAST:
                return 3;
            case WEST:
                return 5;
            case NORTHEAST:
                return 6;
            case NORTH:
                return 7;
            case NORTHWEST:
                return 8;
            default:
                return 4;
        }
    }

    private ICachedMessage preProcess(ParticleEffect effect0) {
        final LanternParticleEffect effect = (LanternParticleEffect) effect0;
        final LanternParticleType type = effect.getType();
        final Integer internalType = type.getInternalType();

        // Special cases
        if (internalType == null) {
            if (type == ParticleTypes.FIREWORKS) {
                // Create the fireworks data item
                final LanternItemStack itemStack = new LanternItemStack(ItemTypes.FIREWORK_ROCKET);
                itemStack.tryOffer(Keys.FIREWORK_EFFECTS, effect.getOptionOrDefault(ParticleOptions.FIREWORK_EFFECTS).get());

                // Write the item to a parameter list
                final DefaultParameterList parameterList = new DefaultParameterList();
                parameterList.add(EntityParameters.Fireworks.ITEM, itemStack);

                return new CachedFireworksMessage(new MessagePlayOutEntityMetadata(CachedFireworksMessage.ENTITY_ID, parameterList));
            } else if (type == ParticleTypes.FERTILIZER) {
                final int quantity = effect.getOptionOrDefault(ParticleOptions.QUANTITY).get();
                return new CachedEffectMessage(2005, quantity, false);
            } else if (type == ParticleTypes.BREAK_SPLASH_POTION) {
                final int potionId = this.potionEffectTypeToId.getInt(effect.getOptionOrDefault(ParticleOptions.POTION_EFFECT_TYPE).get());
                return new CachedEffectMessage(2002, potionId, false);
            } else if (type == ParticleTypes.BREAK_BLOCK) {
                final int state = getBlockState(effect, type.getDefaultOption(ParticleOptions.BLOCK_STATE));
                if (state == 0) {
                    return EmptyCachedMessage.INSTANCE;
                }
                return new CachedEffectMessage(2001, state, false);
            } else if (type == ParticleTypes.MOBSPAWNER_FLAMES) {
                return new CachedEffectMessage(2004, 0, false);
            } else if (type == ParticleTypes.BREAK_EYE_OF_ENDER) {
                return new CachedEffectMessage(2003, 0, false);
            } else if (type == ParticleTypes.DRAGON_BREATH_ATTACK) {
                return new CachedEffectMessage(2006, 0, false);
            } else if (type == ParticleTypes.FIRE_SMOKE) {
                final Direction direction = effect.getOptionOrDefault(ParticleOptions.DIRECTION).get();
                return new CachedEffectMessage(2000, getDirectionData(direction), false);
            }
            return EmptyCachedMessage.INSTANCE;
        }

        final int internalId = internalType;
        final Vector3f offset = effect.getOption(ParticleOptions.OFFSET).map(Vector3d::toFloat).orElse(Vector3f.ZERO);
        final int quantity = effect.getOption(ParticleOptions.QUANTITY).orElse(1);

        MessagePlayOutSpawnParticle.Data extra = null;

        // The extra values, normal behavior offsetX, offsetY, offsetZ
        double f0 = 0f;
        double f1 = 0f;
        double f2 = 0f;

        // Depends on behavior
        // Note: If the count > 0 -> speed = 0f else if count = 0 -> speed = 1f

        final Optional<BlockState> defaultBlockState;
        if (type != ParticleTypes.ITEM && (defaultBlockState = type.getDefaultOption(ParticleOptions.BLOCK_STATE)).isPresent()) {
            final int state = getBlockState(effect, defaultBlockState);
            if (state == 0) {
                return EmptyCachedMessage.INSTANCE;
            }
            extra = new MessagePlayOutSpawnParticle.BlockData(state);
        }

        final Optional<ItemStackSnapshot> defaultItemStackSnapshot;
        if (extra == null && (defaultItemStackSnapshot = type.getDefaultOption(ParticleOptions.ITEM_STACK_SNAPSHOT)).isPresent()) {
            final Optional<ItemStackSnapshot> optItemStackSnapshot = effect.getOption(ParticleOptions.ITEM_STACK_SNAPSHOT);
            ItemStack item;
            if (optItemStackSnapshot.isPresent()) {
                item = optItemStackSnapshot.get().createStack();
            } else {
                final Optional<BlockState> optBlockState = effect.getOption(ParticleOptions.BLOCK_STATE);
                if (optBlockState.isPresent()) {
                    final BlockState blockState = optBlockState.get();
                    final Optional<ItemType> optItemType = blockState.getType().getItem();
                    if (optItemType.isPresent()) {
                        item = ItemStack.of(optItemType.get(), 1);
                    } else {
                        return EmptyCachedMessage.INSTANCE;
                    }
                } else {
                    item = defaultItemStackSnapshot.get().createStack();
                }
            }
            extra = new MessagePlayOutSpawnParticle.ItemData(item);
        }

        final Optional<Double> defaultScale = type.getDefaultOption(ParticleOptions.SCALE);
        final Optional<Color> defaultColor;
        final Optional<NotePitch> defaultNote;
        final Optional<Vector3d> defaultVelocity;
        if (type == ParticleTypes.DUST) {
            defaultColor = type.getDefaultOption(ParticleOptions.COLOR);

            // The following options must be present for dust
            final double scale = effect.getOption(ParticleOptions.SCALE).orElse(defaultScale.get());
            final Color color = effect.getOption(ParticleOptions.COLOR).orElse(defaultColor.get());

            final float r = (float) color.getRed() / 255f;
            final float g = (float) color.getGreen() / 255f;
            final float b = (float) color.getBlue() / 255f;

            extra = new MessagePlayOutSpawnParticle.DustData(r, g, b, (float) scale);
        } else if (defaultScale.isPresent()) {
            double scale = effect.getOption(ParticleOptions.SCALE).orElse(defaultScale.get());

            // The formula of the large explosion acts strange
            // Client formula: sizeClient = 1 - sizeServer * 0.5
            // The particle effect returns the client value so
            // Server formula: sizeServer = (-sizeClient * 2) + 2
            if (type == ParticleTypes.EXPLOSION || type == ParticleTypes.SWEEP_ATTACK) {
                scale = (-scale * 2f) + 2f;
            }

            if (scale == 0f) {
                return new CachedParticleMessage(internalId, offset, quantity, extra);
            }

            f0 = scale;
        } else if (type != ParticleTypes.DUST && (defaultColor = type.getDefaultOption(ParticleOptions.COLOR)).isPresent()) {
            final boolean isSpell = type == ParticleTypes.ENTITY_EFFECT || type == ParticleTypes.AMBIENT_ENTITY_EFFECT;
            Color color = effect.getOption(ParticleOptions.COLOR).orElse(null);

            if (!isSpell && (color == null || color.equals(defaultColor.get()))) {
                return new CachedParticleMessage(internalId, offset, quantity, extra);
            } else if (isSpell && color == null) {
                color = defaultColor.get();
            }

            f0 = color.getRed() / 255f;
            f1 = color.getGreen() / 255f;
            f2 = color.getBlue() / 255f;

            // Make sure that the x and z component are never 0 for these effects,
            // they would trigger the slow horizontal velocity (unsupported on the server),
            // but we already chose for the color, can't have both
            if (isSpell) {
                f0 = Math.max(f0, 0.001f);
                f2 = Math.max(f0, 0.001f);
            }
        } else if ((defaultNote = type.getDefaultOption(ParticleOptions.NOTE)).isPresent()) {
            final NotePitch notePitch = effect.getOption(ParticleOptions.NOTE).orElse(defaultNote.get());
            final float note = ((LanternNotePitch) notePitch).getInternalId();

            if (note == 0f) {
                return new CachedParticleMessage(internalId, offset, quantity, extra);
            }

            f0 = note / 24f;
        }

        if ((defaultVelocity = type.getDefaultOption(ParticleOptions.VELOCITY)).isPresent()) {
            final Vector3d velocity = effect.getOption(ParticleOptions.VELOCITY).orElse(defaultVelocity.get());

            f0 = velocity.getX();
            f1 = velocity.getY();
            f2 = velocity.getZ();

            final Optional<Boolean> slowHorizontalVelocity = type.getDefaultOption(ParticleOptions.SLOW_HORIZONTAL_VELOCITY);
            if (slowHorizontalVelocity.isPresent() &&
                    effect.getOption(ParticleOptions.SLOW_HORIZONTAL_VELOCITY).orElse(slowHorizontalVelocity.get())) {
                f0 = 0f;
                f2 = 0f;
            }

            // The y value won't work for this effect, if the value isn't 0 the velocity won't work
            if (type == ParticleTypes.RAIN_SPLASH) {
                f1 = 0f;
            }

            if (f0 == 0f && f1 == 0f && f2 == 0f) {
                return new CachedParticleMessage(internalId, offset, quantity, extra);
            }
        }

        // Is this check necessary?
        if (f0 == 0f && f1 == 0f && f2 == 0f) {
            return new CachedParticleMessage(internalId, offset, quantity, extra);
        }

        return new CachedOffsetParticleMessage(internalId, new Vector3f(f0, f1, f2), offset, quantity, extra);
    }

    @Override
    public void process(CodecContext context, MessagePlayOutParticleEffect message, List<Message> output) throws CodecException {
        final ICachedMessage cached = this.cache.get(message.getParticleEffect());
        cached.process(message.getPosition(), output);
    }

    private static final class EmptyCachedMessage implements ICachedMessage {

        public static final EmptyCachedMessage INSTANCE = new EmptyCachedMessage();

        @Override
        public void process(Vector3d position, List<Message> output) {
        }
    }

    private static final class CachedFireworksMessage implements ICachedMessage {

        // Get the next free entity id
        private static final int ENTITY_ID;
        private static final UUID UNIQUE_ID;

        private static final MessagePlayOutDestroyEntities DESTROY_ENTITY;
        private static final MessagePlayOutEntityStatus TRIGGER_EFFECT;

        static {
            ENTITY_ID = EntityProtocolManager.acquireEntityId();
            UNIQUE_ID = UUID.randomUUID();

            DESTROY_ENTITY = new MessagePlayOutDestroyEntities(ENTITY_ID);
            // The status index that is used to trigger the fireworks effect
            TRIGGER_EFFECT = new MessagePlayOutEntityStatus(ENTITY_ID, 17);
        }

        private final MessagePlayOutEntityMetadata entityMetadataMessage;

        private CachedFireworksMessage(MessagePlayOutEntityMetadata entityMetadataMessage) {
            this.entityMetadataMessage = entityMetadataMessage;
        }

        @Override
        public void process(Vector3d position, List<Message> output) {
            // 76 -> The internal id used to spawn fireworks
            output.add(new MessagePlayOutSpawnObject(ENTITY_ID, UNIQUE_ID, 76, 0, position, 0, 0, Vector3d.ZERO));
            output.add(this.entityMetadataMessage);
            output.add(TRIGGER_EFFECT);
            output.add(DESTROY_ENTITY);
        }
    }

    private static final class CachedParticleMessage implements ICachedMessage {

        private final int particleId;
        private final Vector3f offsetData;
        private final int count;
        @Nullable private final MessagePlayOutSpawnParticle.Data extra;

        private CachedParticleMessage(int particleId, Vector3f offsetData, int count,
                @Nullable MessagePlayOutSpawnParticle.Data extra) {
            this.particleId = particleId;
            this.offsetData = offsetData;
            this.count = count;
            this.extra = extra;
        }

        @Override
        public void process(Vector3d position, List<Message> output) {
            output.add(new MessagePlayOutSpawnParticle(this.particleId, position.toFloat(),
                    this.offsetData, 0f, this.count, this.extra));
        }
    }

    private static final class CachedOffsetParticleMessage implements ICachedMessage {

        private final int particleId;
        private final Vector3f offsetData;
        private final Vector3f offset;
        private final int count;
        @Nullable private final MessagePlayOutSpawnParticle.Data extra;

        private CachedOffsetParticleMessage(int particleId, Vector3f offsetData, Vector3f offset, int count,
                @Nullable MessagePlayOutSpawnParticle.Data extra) {
            this.particleId = particleId;
            this.offsetData = offsetData;
            this.offset = offset;
            this.count = count;
            this.extra = extra;
        }

        @Override
        public void process(Vector3d position, List<Message> output) {
            final Random random = ThreadLocalRandom.current();

            if (this.offset.equals(Vector3f.ZERO)) {
                final MessagePlayOutSpawnParticle message = new MessagePlayOutSpawnParticle(
                        this.particleId, position.toFloat(), this.offsetData, 1f, 0, this.extra);
                for (int i = 0; i < this.count; i++) {
                    output.add(message);
                }
            } else {
                final float px = (float) position.getX();
                final float py = (float) position.getY();
                final float pz = (float) position.getZ();

                final float ox = this.offset.getX();
                final float oy = this.offset.getY();
                final float oz = this.offset.getZ();

                for (int i = 0; i < this.count; i++) {
                    final double px0 = px + (random.nextFloat() * 2f - 1f) * ox;
                    final double py0 = py + (random.nextFloat() * 2f - 1f) * oy;
                    final double pz0 = pz + (random.nextFloat() * 2f - 1f) * oz;

                    output.add(new MessagePlayOutSpawnParticle(this.particleId, new Vector3f(px0, py0, pz0),
                            this.offsetData, 1f, 0, this.extra));
                }
            }
        }
    }

    private static final class CachedEffectMessage implements ICachedMessage {

        private final int type;
        private final int data;
        private final boolean broadcast;

        private CachedEffectMessage(int type, int data, boolean broadcast) {
            this.broadcast = broadcast;
            this.type = type;
            this.data = data;
        }

        @Override
        public void process(Vector3d position, List<Message> output) {
            output.add(new MessagePlayOutEffect(position.round().toInt(), this.type, this.data, this.broadcast));
        }
    }

    private interface ICachedMessage {

        void process(Vector3d position, List<Message> output);
    }
}
