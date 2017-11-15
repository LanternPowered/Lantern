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
package org.lanternpowered.server.network.vanilla.message.type.play;

import com.flowpowered.math.vector.Vector3f;
import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.annotation.Nullable;

public final class MessagePlayOutSpawnParticle implements Message {

    private final int particleId;

    private final Vector3f position;
    private final Vector3f offset;

    private final float data;
    private final int count;
    @Nullable private final Data extra;

    private final boolean longDistance;

    public MessagePlayOutSpawnParticle(int particleId, Vector3f position, Vector3f offset, float data,
            int count, @Nullable Data extra) {
        this(particleId, position, offset, data, count, extra, true);
    }

    public MessagePlayOutSpawnParticle(int particleId, Vector3f position, Vector3f offset, float data,
            int count, @Nullable Data extra, boolean longDistance) {
        this.longDistance = longDistance;
        this.particleId = particleId;
        this.position = position;
        this.offset = offset;
        this.count = count;
        this.extra = extra;
        this.data = data;
    }

    public int getParticleId() {
        return this.particleId;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public Vector3f getOffset() {
        return this.offset;
    }

    public float getData() {
        return this.data;
    }

    public int getCount() {
        return this.count;
    }

    @Nullable
    public Data getExtra() {
        return this.extra;
    }

    public boolean isLongDistance() {
        return this.longDistance;
    }

    public static abstract class Data {
    }

    public static final class ItemData extends Data {

        private final ItemStack itemStack;

        public ItemData(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public ItemStack getItemStack() {
            return this.itemStack;
        }
    }

    public static final class BlockData extends Data {

        private final int blockState;

        public BlockData(int blockState) {
            this.blockState = blockState;
        }

        public int getBlockState() {
            return this.blockState;
        }
    }

    public static final class DustData extends Data {

        private final float red;
        private final float green;
        private final float blue;
        private final float scale;

        public DustData(float red, float green, float blue, float scale) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.scale = scale;
        }

        public float getRed() {
            return this.red;
        }

        public float getBlue() {
            return this.blue;
        }

        public float getGreen() {
            return this.green;
        }

        public float getScale() {
            return this.scale;
        }
    }
}
