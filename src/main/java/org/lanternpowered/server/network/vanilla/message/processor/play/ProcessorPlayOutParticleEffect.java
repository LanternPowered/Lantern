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

import com.flowpowered.math.vector.Vector3f;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.data.type.LanternNotePitch;
import org.lanternpowered.server.effect.particle.LanternParticleType;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnParticle;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.effect.particle.BlockParticle;
import org.spongepowered.api.effect.particle.ColoredParticle;
import org.spongepowered.api.effect.particle.ItemParticle;
import org.spongepowered.api.effect.particle.NoteParticle;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.particle.ResizableParticle;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

@NonnullByDefault
public final class ProcessorPlayOutParticleEffect implements Processor<MessagePlayOutParticleEffect> {

    /**
     * Using a cache to bring the amount of operations down for spawning particles.
     */
    private final LoadingCache<ParticleEffect, CachedParticleEffect> cache =
            Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(this::preProcess);

    private CachedParticleEffect preProcess(ParticleEffect effect) {
        LanternParticleType type = (LanternParticleType) effect.getType();
        Vector3f offset = effect.getOffset().toFloat();

        int count = effect.getCount();
        // Don't even try...
        if (count <= 0) {
            return CachedParticleEffect.EMPTY;
        }

        int[] extra = new int[0];

        // The extra values, normal behavior offsetX, offsetY, offsetZ
        float f0 = 0f;
        float f1 = 0f;
        float f2 = 0f;

        // Depends on behavior
        // Note: If the count > 0 -> speed = 0f else if count = 0 -> speed = 1f

        if (effect instanceof ItemParticle) {
            ItemStackSnapshot item = ((ItemParticle) effect).getItem();
            ItemType itemType = item.getType();
            int extraData = 0;
            if (type == ParticleTypes.ITEM_CRACK) {
                extraData = ItemRegistryModule.get().getInternalId(itemType);
            } else if (type == ParticleTypes.BLOCK_CRACK || type == ParticleTypes.BLOCK_DUST) {
                BlockType blockType = itemType.getBlock().orElse(null);
                // Only block types are allowed
                if (blockType != null) {
                    int id = BlockRegistryModule.get().getStateInternalId(blockType.getDefaultState());
                    int data = 0; // TODO: Retrieve data value from item stack
                    extraData = data << 12 | id;
                }
            }
            if (extraData == 0) {
                return CachedParticleEffect.EMPTY;
            }
            extra = new int[] { extraData };
        } else if (effect instanceof BlockParticle) {
            if (type == ParticleTypes.BLOCK_CRACK || type == ParticleTypes.BLOCK_DUST) {
                BlockState blockState = ((BlockParticle) effect).getBlockState();
                int id = BlockRegistryModule.get().getStateInternalId(blockState);
                int data = BlockRegistryModule.get().getStateData(blockState);
                extra = new int[] { data << 12 | id };
            } else {
                return CachedParticleEffect.EMPTY;
            }
        }

        if (effect instanceof ResizableParticle) {
            float size = ((ResizableParticle) effect).getSize();

            // The formula of the large explosion acts strange
            // Client formula: sizeClient = 1 - sizeServer * 0.5
            // The particle effect returns the client value so
            // Server formula: sizeServer = (-sizeClient * 2) + 2
            if (type == ParticleTypes.EXPLOSION_LARGE) {
                size = (-size * 2f) + 2f;
            }

            if (size == 0f) {
                return new CachedParticleEffect(Collections.singletonList(new MessagePlayOutSpawnParticle(
                        type.getInternalId(), Vector3f.ZERO, offset, 0f, count, extra)), null);
            }

            f0 = size;
        } else if (effect instanceof ColoredParticle) {
            Color color0 = ((ColoredParticle) effect).getColor();
            Color color1 = ((ParticleType.Colorable) type).getDefaultColor();

            if (color0.equals(color1)) {
                return new CachedParticleEffect(Collections.singletonList(new MessagePlayOutSpawnParticle(
                        type.getInternalId(), Vector3f.ZERO, offset, 0f, count, extra)), null);
            }

            f0 = color0.getRed() / 255f;
            f1 = color0.getGreen() / 255f;
            f2 = color0.getBlue() / 255f;

            // If the f0 value 0 is, the redstone will set it automatically to red 255
            if (f0 == 0f && type == ParticleTypes.REDSTONE) {
                f0 = 0.00001f;
            }
        } else if (effect instanceof NoteParticle) {
            NotePitch note = ((NoteParticle) effect).getNote();
            int internalId = ((LanternNotePitch) note).getInternalId();

            if (internalId == 0) {
                return new CachedParticleEffect(Collections.singletonList(new MessagePlayOutSpawnParticle(
                        type.getInternalId(), Vector3f.ZERO, offset, 0f, count, extra)), null);
            }

            f0 = (float) internalId / 24f;
        } else if (type.hasMotion()) {
            Vector3f motion = effect.getMotion().toFloat();

            float mx = motion.getX();
            float my = motion.getY();
            float mz = motion.getZ();

            // The y value won't work for this effect, if the value isn't 0 the motion won't work
            if (type == ParticleTypes.WATER_SPLASH) {
                my = 0f;
            }

            if (mx == 0f && my == 0f && mz == 0f) {
                return new CachedParticleEffect(Collections.singletonList(new MessagePlayOutSpawnParticle(
                        type.getInternalId(), Vector3f.ZERO, offset, 0f, count, extra)), null);
            }

            f0 = mx;
            f1 = my;
            f2 = mz;
        }

        if (f0 == 0f && f1 == 0f && f2 == 0f) {
            return new CachedParticleEffect(Collections.singletonList(new MessagePlayOutSpawnParticle(
                    type.getInternalId(), Vector3f.ZERO, offset, 0f, count, extra)), null);
        }

        final List<MessagePlayOutSpawnParticle> messages = new ArrayList<>(count);
        if (offset.equals(Vector3f.ZERO)) {
            for (int i = 0; i < count; i++) {
                messages.add(new MessagePlayOutSpawnParticle(type.getInternalId(), Vector3f.ZERO, offset, 1f, 0, extra));
            }
            return new CachedParticleEffect(messages, null);
        } else {
            final Vector3f value = new Vector3f(f0, f1, f2);
            for (int i = 0; i < count; i++) {
                messages.add(new MessagePlayOutSpawnParticle(type.getInternalId(), Vector3f.ZERO, value, 1f, 0, extra));
            }
            return new CachedParticleEffect(messages, offset);
        }
    }

    @Override
    public void process(CodecContext context, MessagePlayOutParticleEffect message, List<Message> output) throws CodecException {
        final CachedParticleEffect cached = this.cache.get(message.getParticleEffect());
        final Vector3f position = message.getPosition().toFloat();

        if (cached.offset == null) {
            for (MessagePlayOutSpawnParticle message0 : cached.messages) {
                output.add(new MessagePlayOutSpawnParticle(message0.getParticleId(), position, message0.getOffset(),
                        message0.getData(), message0.getCount(), message0.getExtra()));
            }
        } else {
            Random random = new Random();

            float px = position.getX();
            float py = position.getY();
            float pz = position.getZ();

            float ox = cached.offset.getX();
            float oy = cached.offset.getY();
            float oz = cached.offset.getZ();

            for (MessagePlayOutSpawnParticle message0 : cached.messages) {
                double px0 = px + (random.nextFloat() * 2f - 1f) * ox;
                double py0 = py + (random.nextFloat() * 2f - 1f) * oy;
                double pz0 = pz + (random.nextFloat() * 2f - 1f) * oz;

                output.add(new MessagePlayOutSpawnParticle(message0.getParticleId(), new Vector3f(px0, py0, pz0), message0.getOffset(),
                        message0.getData(), message0.getCount(), message0.getExtra()));
            }
        }
    }

    /**
     * Represents a {@link ParticleEffect} that is cached.
     */
    private static class CachedParticleEffect {

        static final CachedParticleEffect EMPTY = new CachedParticleEffect(Collections.emptyList(), null);

        /**
         * If the offset isn't null, it means that all the returned messages should have
         * a offset added to them before sending to the client.
         */
        @Nullable private final Vector3f offset;

        /**
         * The messages that are valid for a
         */
        private final List<MessagePlayOutSpawnParticle> messages;

        private CachedParticleEffect(List<MessagePlayOutSpawnParticle> messages, @Nullable Vector3f offset) {
            this.messages = messages;
            this.offset = offset;
        }
    }
}
