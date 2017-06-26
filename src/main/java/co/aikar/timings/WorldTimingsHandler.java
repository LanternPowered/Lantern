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
package co.aikar.timings;

import org.lanternpowered.api.world.World;

/**
 * Set of timers per world, to track world specific Timings.
 */
public class WorldTimingsHandler {

    public final Timing mobSpawn;
    public final Timing doChunkUnload;
    public final Timing doPortalForcer;
    public final Timing scheduledBlocks;
    public final Timing scheduledBlocksCleanup;
    public final Timing scheduledBlocksTicking;
    public final Timing updateBlocks;
    public final Timing updateBlocksCheckNextLight;
    public final Timing updateBlocksChunkTick;
    public final Timing updateBlocksIceAndSnow;
    public final Timing updateBlocksRandomTick;
    public final Timing updateBlocksThunder;
    public final Timing doVillages;
    public final Timing doChunkMap;
    public final Timing doChunkGC;
    public final Timing doSounds;
    public final Timing entityRemoval;
    public final Timing entityTick;
    public final Timing tileEntityTick;
    public final Timing tileEntityPending;
    public final Timing tileEntityRemoval;
    public final Timing tracker1;
    public final Timing tracker2;
    public final Timing doTick;
    public final Timing tickEntities;

    // Chunk Load
    public final Timing syncChunkLoadTimer;
    public final Timing syncChunkLoadDataTimer;
    public final Timing syncChunkLoadStructuresTimer;
    public final Timing syncChunkLoadEntitiesTimer;
    public final Timing syncChunkLoadTileEntitiesTimer;
    public final Timing syncChunkLoadTileTicksTimer;
    public final Timing syncChunkLoadPostTimer;

    // Tracking
    public final Timing causeTrackerBlockTimer;
    public final Timing causeTrackerBlockBreakTimer;
    public final Timing causeTrackerEntityTimer;
    public final Timing causeTrackerEntityItemTimer;

    // Chunk population
    public final Timing chunkPopulate;

    public WorldTimingsHandler(World world) {
        String name = world.getName() + " - ";

        this.mobSpawn = LanternTimingsFactory.ofSafe(name + "mobSpawn");
        this.doChunkUnload = LanternTimingsFactory.ofSafe(name + "doChunkUnload");
        this.scheduledBlocks = LanternTimingsFactory.ofSafe(name + "Scheduled Blocks");
        this.scheduledBlocksCleanup = LanternTimingsFactory.ofSafe(name + "Scheduled Blocks - Cleanup");
        this.scheduledBlocksTicking = LanternTimingsFactory.ofSafe(name + "Scheduled Blocks - Ticking");
        this.updateBlocks = LanternTimingsFactory.ofSafe(name + "Update Blocks");
        this.updateBlocksCheckNextLight = LanternTimingsFactory.ofSafe(name + "Update Blocks - CheckNextLight");
        this.updateBlocksChunkTick = LanternTimingsFactory.ofSafe(name + "Update Blocks - ChunkTick");
        this.updateBlocksIceAndSnow = LanternTimingsFactory.ofSafe(name + "Update Blocks - IceAndSnow");
        this.updateBlocksRandomTick = LanternTimingsFactory.ofSafe(name + "Update Blocks - RandomTick");
        this.updateBlocksThunder = LanternTimingsFactory.ofSafe(name + "Update Blocks - Thunder");
        this.doVillages = LanternTimingsFactory.ofSafe(name + "doVillages");
        this.doChunkMap = LanternTimingsFactory.ofSafe(name + "doChunkMap");
        this.doSounds = LanternTimingsFactory.ofSafe(name + "doSounds");
        this.doChunkGC = LanternTimingsFactory.ofSafe(name + "doChunkGC");
        this.doPortalForcer = LanternTimingsFactory.ofSafe(name + "doPortalForcer");
        this.entityTick = LanternTimingsFactory.ofSafe(name + "entityTick");
        this.entityRemoval = LanternTimingsFactory.ofSafe(name + "entityRemoval");
        this.tileEntityTick = LanternTimingsFactory.ofSafe(name + "tileEntityTick");
        this.tileEntityPending = LanternTimingsFactory.ofSafe(name + "tileEntityPending");
        this.tileEntityRemoval = LanternTimingsFactory.ofSafe(name + "tileEntityRemoval");

        this.syncChunkLoadTimer = LanternTimingsFactory.ofSafe(name + "syncChunkLoad");
        this.syncChunkLoadDataTimer = LanternTimingsFactory.ofSafe(name + "syncChunkLoad - Data");
        this.syncChunkLoadStructuresTimer = LanternTimingsFactory.ofSafe(name + "chunkLoad - Structures");
        this.syncChunkLoadEntitiesTimer = LanternTimingsFactory.ofSafe(name + "chunkLoad - Entities");
        this.syncChunkLoadTileEntitiesTimer = LanternTimingsFactory.ofSafe(name + "chunkLoad - TileEntities");
        this.syncChunkLoadTileTicksTimer = LanternTimingsFactory.ofSafe(name + "chunkLoad - TileTicks");
        this.syncChunkLoadPostTimer = LanternTimingsFactory.ofSafe(name + "chunkLoad - Post");

        this.tracker1 = LanternTimingsFactory.ofSafe(name + "tracker stage 1");
        this.tracker2 = LanternTimingsFactory.ofSafe(name + "tracker stage 2");
        this.doTick = LanternTimingsFactory.ofSafe(name + "doTick");
        this.tickEntities = LanternTimingsFactory.ofSafe(name + "tickEntities");

        this.causeTrackerBlockTimer = LanternTimingsFactory.ofSafe(name + "causeTracker - BlockCaptures");
        this.causeTrackerBlockBreakTimer = LanternTimingsFactory.ofSafe(name + "causeTracker - BlockBreakCaptures");
        this.causeTrackerEntityTimer = LanternTimingsFactory.ofSafe(name + "causeTracker - EntityCaptures");
        this.causeTrackerEntityItemTimer = LanternTimingsFactory.ofSafe(name + "causeTracker - EntityItemCaptures");

        this.chunkPopulate = LanternTimingsFactory.ofSafe(name + "chunkPopulate");
    }

}
