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

import javax.annotation.Nullable;

public final class LanternTimings {

    // TODO: I don't think we need all of these

    public static final Timing PLAYER_LIST_TIMER = LanternTimingsFactory.ofSafe("Player List");
    public static final Timing CONNECTION_TIMER = LanternTimingsFactory.ofSafe("Connection Handler");
    public static final Timing TICKABLES_TIMER = LanternTimingsFactory.ofSafe("Tickables");
    public static final Timing SCHEDULER_TIMER = LanternTimingsFactory.ofSafe("Scheduler");
    public static final Timing CHUNK_IO_TICK_TIMER = LanternTimingsFactory.ofSafe("ChunkIOTick");
    public static final Timing TIME_UPDATE_TIMER = LanternTimingsFactory.ofSafe("Time Update");
    public static final Timing SERVER_COMMAND_TIMER = LanternTimingsFactory.ofSafe("Server Command");
    public static final Timing WORLD_SAVE_TIMER = LanternTimingsFactory.ofSafe("World Save");

    public static final Timing PROCESS_QUEUE_TIMER = LanternTimingsFactory.ofSafe("processQueue");

    public static final Timing PLAYER_COMMAND_TIMER = LanternTimingsFactory.ofSafe("playerCommand");

    public static final Timing ENTITY_ACTIVATION_CHECK_TIMER = LanternTimingsFactory.ofSafe("entityActivationCheck");
    public static final Timing CHECK_IF_ACTIVE_TIMER = LanternTimingsFactory.ofSafe("checkIfActive");

    public static final Timing DATA_GET_MANIPULATOR_TIMER = LanternTimingsFactory.ofSafe("## getManipulator");
    public static final Timing DATA_GET_OR_CREATE_MANIPULATOR_TIMER = LanternTimingsFactory.ofSafe("## getOrCreateManipulator");
    public static final Timing DATA_OFFER_MANIPULATOR_TIMER = LanternTimingsFactory.ofSafe("## offerData");
    public static final Timing DATA_OFFER_MULTI_MANIPULATORS_TIMER = LanternTimingsFactory.ofSafe("## offerManipulators");
    public static final Timing DATA_REMOVE_MANIPULATOR_TIMER = LanternTimingsFactory.ofSafe("## removeManipulator");
    public static final Timing DATA_SUPPORTS_MANIPULATOR_TIMER = LanternTimingsFactory.ofSafe("## supportsManipulator");
    public static final Timing DATA_OFFER_KEY_TIMER = LanternTimingsFactory.ofSafe("## offerKey");
    public static final Timing DATA_GET_BY_KEY_TIMER = LanternTimingsFactory.ofSafe("## getKey");
    public static final Timing DATA_GET_VALUE_TIMER = LanternTimingsFactory.ofSafe("## getValue");
    public static final Timing DATA_SUPPORTS_KEY_TIMER = LanternTimingsFactory.ofSafe("## supportsKey");
    public static final Timing DATA_REMOVE_KEY_TIMER = LanternTimingsFactory.ofSafe("## removeKey");

    private LanternTimings() {}

    /**
     * Gets a timer associated with a plugins' tasks.
     *
     * @param task The plugin task
     * @param period The period of time
     * @return The timing
     */
    @Nullable
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
     * @param entity The entity to track
     * @return The timing
     */
    public static Timing getEntityTiming(Entity entity) {
        return LanternTimingsFactory.ofSafe("Minecraft", "## tickEntity" + entity.getType().getId());
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
        return LanternTimingsFactory.ofSafe("Minecraft", "## tickTileEntity" + entity.getType().getId());
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
