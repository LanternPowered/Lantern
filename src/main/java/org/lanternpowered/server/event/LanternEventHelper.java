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
package org.lanternpowered.server.event;

import com.google.common.collect.Streams;
import org.lanternpowered.api.Lantern;
import org.lanternpowered.api.entity.spawn.EntitySpawnEntry;
import org.lanternpowered.server.entity.LanternItem;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class LanternEventHelper {

    public static void handleDroppedItemSpawning(Iterable<Tuple<ItemStackSnapshot, Transform<World>>> entries) {
        Lantern.getEntitySpawner().spawn(toSpawningEntries(entries));
    }

    public static List<Entity> handlePreDroppedItemSpawning(Iterable<Tuple<ItemStackSnapshot, Transform<World>>> entries) {
        return Lantern.getEntitySpawner().preSpawn(toSpawningEntries(entries));
    }

    private static List<EntitySpawnEntry> toSpawningEntries(Iterable<Tuple<ItemStackSnapshot, Transform<World>>> entries) {
        return Streams.stream(entries)
                .map(tuple -> new EntitySpawnEntry(EntityTypes.ITEM, tuple.getSecond(), entity -> {
                    entity.offer(Keys.REPRESENTED_ITEM, tuple.getFirst());
                    entity.offer(Keys.PICKUP_DELAY, LanternItem.DROPPED_PICKUP_DELAY);
                }))
                .collect(Collectors.toList());
    }

    public static Optional<Entity> handlePreDroppedItemSpawning(Transform<World> transform, ItemStackSnapshot snapshot) {
        return Lantern.getEntitySpawner().preSpawn(EntityTypes.ITEM, transform, entity -> {
            entity.offer(Keys.REPRESENTED_ITEM, snapshot);
            entity.offer(Keys.PICKUP_DELAY, LanternItem.DROPPED_PICKUP_DELAY);
        });
    }

    public static void handleDroppedItemSpawning(Transform<World> transform, ItemStackSnapshot snapshot) {
        Lantern.getEntitySpawner().spawn(EntityTypes.ITEM, transform, SpongeEventFactory::createDropItemEventDispense, entity -> {
            entity.offer(Keys.REPRESENTED_ITEM, snapshot);
            entity.offer(Keys.PICKUP_DELAY, LanternItem.DROPPED_PICKUP_DELAY);
        });
    }
}
