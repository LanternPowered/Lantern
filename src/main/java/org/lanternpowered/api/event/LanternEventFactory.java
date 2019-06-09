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
package org.lanternpowered.api.event;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.Transform;
import org.spongepowered.api.world.World;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

public class LanternEventFactory {

    public static DropItemEvent.@NonNull Pre createDropItemEventPre(
            @NonNull Cause cause,
            @NonNull List<ItemStackSnapshot> originalDroppedItems,
            @NonNull List<ItemStackSnapshot> droppedItems) {
        return SpongeEventFactory.createDropItemEventPre(cause, originalDroppedItems, droppedItems);
    }

    public static DropItemEvent.@NonNull Destruct createDropItemEventDestruct(
            @NonNull Cause cause,
            @NonNull List<Entity> entities) {
        return SpongeEventFactory.createDropItemEventDestruct(cause, entities);
    }

    public static @NonNull SpawnEntityEvent createSpawnEntityEvent(
            @NonNull Cause cause,
            @NonNull List<Entity> entities) {
        return SpongeEventFactory.createSpawnEntityEvent(cause, entities);
    }

    public static SpawnEntityEvent.@NonNull ChunkLoad createSpawnEntityEventChunkLoad(
            @NonNull Cause cause,
            @NonNull List<Entity> entities) {
        return SpongeEventFactory.createSpawnEntityEventChunkLoad(cause, entities);
    }

    public static ConstructEntityEvent.@NonNull Pre createConstructEntityEventPre(
            @NonNull Cause cause,
            @NonNull EntityType<?> targetType,
            @NonNull Transform transform,
            @NonNull World world) {
        return SpongeEventFactory.createConstructEntityEventPre(cause, targetType, transform, world);
    }

    public static ChangeDataHolderEvent.@NonNull ValueChange createChangeDataHolderEventValueChange(
            @NonNull Cause cause,
            @NonNull DataTransactionResult originalChanges,
            DataHolder.@NonNull Mutable targetHolder) {
        return SpongeEventFactory.createChangeDataHolderEventValueChange(cause, originalChanges, targetHolder);
    }
}
