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
package org.lanternpowered.server.world;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.GenericMath;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.lanternpowered.server.game.LanternGame;
import org.slf4j.Logger;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBorder.ChunkPreGenerate;

import java.util.function.Consumer;

import javax.annotation.Nullable;

public final class LanternChunkPreGenerate implements ChunkPreGenerate {

    private static final int TICK_INTERVAL = 10;
    private static final float DEFAULT_TICK_PERCENT = 0.15f;
    private World world;
    private Vector3d center;
    private double diameter;
    @Nullable private Object plugin = null;
    @Nullable private Logger logger = null;
    private int tickInterval = TICK_INTERVAL;
    private int chunkCount = 0;
    private float tickPercent = DEFAULT_TICK_PERCENT;

    public LanternChunkPreGenerate(World world, Vector3d center, double diameter) {
        this.world = world;
        this.center = center;
        this.diameter = diameter;
    }

    @Override
    public LanternChunkPreGenerate owner(Object plugin) {
        checkNotNull(plugin, "plugin");
        this.plugin = plugin;
        return this;
    }

    @Override
    public LanternChunkPreGenerate logger(@Nullable Logger logger) {
        this.logger = logger;
        return this;
    }

    @Override
    public LanternChunkPreGenerate tickInterval(int tickInterval) {
        checkArgument(tickInterval > 0, "tickInterval must be greater than zero");
        this.tickInterval = tickInterval;
        return this;
    }

    @Override
    public LanternChunkPreGenerate chunksPerTick(int chunkCount) {
        this.chunkCount = chunkCount;
        return this;
    }

    @Override
    public LanternChunkPreGenerate tickPercentLimit(float tickPercent) {
        checkArgument(tickPercent <= 1, "tickPercent must be smaller or equal to 1");
        this.tickPercent = tickPercent;
        return this;
    }

    @Override
    public Task start() {
        checkNotNull(this.plugin, "owner not set");
        checkArgument(this.chunkCount > 0 || this.tickPercent > 0, "Must use at least one of \"chunks per tick\" or \"tick percent limit\"");
        return Task.builder().name(toString())
                .execute(new ChunkPreGenerator(this.world, this.center, this.diameter, this.chunkCount, this.tickPercent, this.logger))
                .intervalTicks(this.tickInterval).submit(this.plugin);
    }

    @Override
    public LanternChunkPreGenerate reset() {
        this.plugin = null;
        this.logger = null;
        this.tickInterval = 0;
        this.chunkCount = 0;
        this.tickPercent = DEFAULT_TICK_PERCENT;
        return this;
    }

    @Override
    public ChunkPreGenerate from(Task value) {
        final Consumer<Task> c0 = value.getConsumer();
        if (c0 instanceof ChunkPreGenerator) {
            final ChunkPreGenerator c1 = (ChunkPreGenerator) c0;
            this.center = c1.center;
            this.diameter = c1.diameter;
            this.chunkCount = c1.chunkCount;
            this.tickPercent = c1.tickPercent;
            this.world = c1.world;
            this.tickInterval = (int) value.getInterval();
            this.plugin = value.getOwner();
            this.logger = c1.logger;
        }
        return this;
    }

    @Override
    public String toString() {
        return "SpongeChunkPreGen{" +
                "center=" + this.center +
                ", diameter=" + this.diameter +
                ", plugin=" + this.plugin +
                ", world=" + this.world +
                ", tickInterval=" + this.tickInterval +
                ", chunkCount=" + this.chunkCount +
                ", tickPercent=" + this.tickPercent +
                '}';
    }

    private static class ChunkPreGenerator implements Consumer<Task> {

        private static final Vector3i[] OFFSETS = {
                Vector3i.UNIT_X,
                Vector3i.UNIT_Z,
                Vector3i.UNIT_X.negate(),
                Vector3i.UNIT_Z.negate()
        };
        private static final String TIME_FORMAT = "s's 'S'ms'";
        private final World world;
        private final int chunkRadius;
        private final int chunkCount;
        private final float tickPercent;
        private final long tickTimeLimit;
        private final double diameter;
        @Nullable private final Logger logger;
        private Vector3i currentPosition;
        private final Vector3d center;
        private int currentLayerIndex;
        private int currentLayerSize;
        private int currentIndexInLayer;
        private int totalCount;
        private long totalTime;

        public ChunkPreGenerator(World world, Vector3d center, double diameter, int chunkCount, float tickPercent, @Nullable Logger logger) {
            this.world = world;
            this.diameter = diameter;
            this.chunkRadius = GenericMath.floor(diameter / 32);
            this.chunkCount = chunkCount;
            this.tickPercent = tickPercent;
            this.logger = logger;
            this.tickTimeLimit = Math.round(LanternGame.get().getScheduler().getPreferredTickInterval() * tickPercent);
            this.currentPosition = LanternGame.get().getServer().getChunkLayout().toChunk(center.toInt()).get();
            this.currentLayerIndex = 0;
            this.center = center;
            this.currentLayerSize = 0;
            this.currentIndexInLayer = 0;
            this.totalCount = 0;
            this.totalTime = 0;
        }

        @Override
        public void accept(Task task) {
            final long startTime = System.currentTimeMillis();
            int count = 0;
            do {
                this.world.loadChunk(nextChunkPosition(), true).ifPresent(Chunk::unloadChunk);
            } while (hasNextChunkPosition() && checkChunkCount(++count) && checkTickTime(System.currentTimeMillis() - startTime));
            if (this.logger != null) {
                this.totalCount += count;
                final long deltaTime = System.currentTimeMillis() - startTime;
                this.totalTime += deltaTime;
                this.logger.info("Generated {} chunks in {}, {}% complete", count,
                        DurationFormatUtils.formatDuration(deltaTime, TIME_FORMAT, false),
                        Math.round((float) this.totalCount / (this.chunkRadius * this.chunkRadius * 4) * 100));
            }
            if (!hasNextChunkPosition()) {
                if (this.logger != null) {
                    this.logger.info("Done! Generated a total of {} chunks in {}", this.totalCount,
                            DurationFormatUtils.formatDuration(this.totalTime, TIME_FORMAT, false));
                }
                task.cancel();
            }
        }

        private boolean hasNextChunkPosition() {
            return this.currentLayerIndex <= this.chunkRadius;
        }

        private Vector3i nextChunkPosition() {
            final Vector3i nextPosition = this.currentPosition;
            if (++this.currentIndexInLayer >= this.currentLayerSize * 4) {
                this.currentLayerIndex++;
                this.currentLayerSize += 2;
                this.currentIndexInLayer = 0;
                this.currentPosition = this.currentPosition.sub(Vector3i.UNIT_Z).sub(Vector3i.UNIT_X);
            }
            this.currentPosition = this.currentPosition.add(OFFSETS[this.currentIndexInLayer / this.currentLayerSize]);
            return nextPosition;
        }

        private boolean checkChunkCount(int count) {
            return this.chunkCount <= 0 || count < this.chunkCount;
        }

        private boolean checkTickTime(long tickTime) {
            return this.tickPercent <= 0 || tickTime < this.tickTimeLimit;
        }

    }

}
