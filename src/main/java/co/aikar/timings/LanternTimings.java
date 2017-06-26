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

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

public final class LanternTimings {

    public static final Timing playerListTimer = LanternTimingsFactory.ofSafe("Player List");
    public static final Timing connectionTimer = LanternTimingsFactory.ofSafe("Connection Handler");
    public static final Timing tickablesTimer = LanternTimingsFactory.ofSafe("Tickables");
    public static final Timing schedulerTimer = LanternTimingsFactory.ofSafe("Scheduler");
    public static final Timing chunkIOTickTimer = LanternTimingsFactory.ofSafe("ChunkIOTick");
    public static final Timing timeUpdateTimer = LanternTimingsFactory.ofSafe("Time Update");
    public static final Timing serverCommandTimer = LanternTimingsFactory.ofSafe("Server Command");
    public static final Timing worldSaveTimer = LanternTimingsFactory.ofSafe("World Save");

    public static final Timing processQueueTimer = LanternTimingsFactory.ofSafe("processQueue");

    public static final Timing playerCommandTimer = LanternTimingsFactory.ofSafe("playerCommand");

    public static final Timing entityActivationCheckTimer = LanternTimingsFactory.ofSafe("entityActivationCheck");
    public static final Timing checkIfActiveTimer = LanternTimingsFactory.ofSafe("checkIfActive");

    public static final Timing antiXrayUpdateTimer = LanternTimingsFactory.ofSafe("anti-xray - update");
    public static final Timing antiXrayObfuscateTimer = LanternTimingsFactory.ofSafe("anti-xray - obfuscate");

    public static final Timing dataGetManipulator = LanternTimingsFactory.ofSafe("## getManipulator");
    public static final Timing dataGetOrCreateManipulator = LanternTimingsFactory.ofSafe("## getOrCreateManipulator");
    public static final Timing dataOfferManipulator = LanternTimingsFactory.ofSafe("## offerData");
    public static final Timing dataOfferMultiManipulators = LanternTimingsFactory.ofSafe("## offerManipulators");
    public static final Timing dataRemoveManipulator = LanternTimingsFactory.ofSafe("## removeManipulator");
    public static final Timing dataSupportsManipulator = LanternTimingsFactory.ofSafe("## supportsManipulator");
    public static final Timing dataOfferKey = LanternTimingsFactory.ofSafe("## offerKey");
    public static final Timing dataGetByKey = LanternTimingsFactory.ofSafe("## getKey");
    public static final Timing dataGetValue = LanternTimingsFactory.ofSafe("## getValue");
    public static final Timing dataSupportsKey = LanternTimingsFactory.ofSafe("## supportsKey");
    public static final Timing dataRemoveKey = LanternTimingsFactory.ofSafe("## removeKey");

    public static final Timing TRACKING_PHASE_UNWINDING = LanternTimingsFactory.ofSafe("## unwindPhase");

    private LanternTimings() {
    }

    /**
     * Gets a timer associated with a plugins tasks.
     *
     * @param task The plugin task
     * @param period The period of time
     * @return The timing
     */
    public static Timing getPluginTaskTimings(Task task, long period) {
        if (task.isAsynchronous()) {
            return null;
        }
        final PluginContainer plugin = task.getOwner();

        String name = "Task: " + task.getName();
        if (period > 0) {
            name += " (interval:" + period + ")";
        } else {
            name += " (Single)";
        }

        return LanternTimingsFactory.ofSafe(plugin, name);
    }

    /**
     * Get a named timer for the specified entity type to track type
     * specific timings.
     *
     * @param entity The entity type to track
     * @return The timing
     */
    public static Timing getEntityTiming(Entity entity) {
        EntityType type = entity.getType();
        final String entityType = type != null ? type.getId() : entity.getClass().getName();
        return LanternTimingsFactory.ofSafe("Minecraft", "## tickEntity - " + entityType);
    }

    /**
     * Get a named timer for the specified tile entity type
     * to track type specific timings.
     *
     *
     * @param entity The tile entity to track
     * @return The timing
     */
    public static Timing getTileEntityTiming(TileEntity entity) {
        TileEntityType type = entity.getType();
        final String entityType = type != null ? type.getId() : entity.getClass().getName();
        return LanternTimingsFactory.ofSafe("Minecraft", "## tickTileEntity - " + entityType);
    }

    public static Timing getModTimings(PluginContainer plugin, String context) {
        return LanternTimingsFactory.ofSafe(plugin.getName(), context, TimingsManager.MOD_EVENT_HANDLER);
    }

    public static Timing getPluginTimings(PluginContainer plugin, String context) {
        return LanternTimingsFactory.ofSafe(plugin.getName(), context, TimingsManager.PLUGIN_EVENT_HANDLER);
    }

    public static Timing getPluginSchedulerTimings(PluginContainer plugin) {
        return LanternTimingsFactory.ofSafe(plugin.getName(), TimingsManager.PLUGIN_SCHEDULER_HANDLER);
    }

    public static Timing getCancelTasksTimer() {
        return LanternTimingsFactory.ofSafe("Cancel Tasks");
    }

    public static Timing getCancelTasksTimer(PluginContainer plugin) {
        return LanternTimingsFactory.ofSafe(plugin, "Cancel Tasks");
    }

    public static void stopServer() {
        TimingsManager.stopServer();
    }

    public static Timing getBlockTiming(BlockType type) {
        return LanternTimingsFactory.ofSafe("## Scheduled Block: " + type.getId());
    }

}
