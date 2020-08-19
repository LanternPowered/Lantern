/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.event;

import com.google.common.collect.Streams;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.LanternItem;
import org.lanternpowered.server.world.EntitySpawningEntry;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.Transform;
import org.spongepowered.api.util.Tuple;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public final class LanternEventHelper {

    public static void handleDroppedItemSpawning(Iterable<Tuple<ItemStackSnapshot, Transform>> entries) {
        LanternWorld.handleEntitySpawning(toSpawningEntries(entries));
    }
    public static void handleDroppedItemSpawning(Iterable<Tuple<ItemStackSnapshot, Transform>> entries,
            BiFunction<Cause, List<Entity>, SpawnEntityEvent> spawnEventConstructor) {
        LanternWorld.handleEntitySpawning(toSpawningEntries(entries), spawnEventConstructor);
    }

    public static List<Entity> handlePreDroppedItemSpawning(Iterable<Tuple<ItemStackSnapshot, Transform>> entries) {
        return LanternWorld.handlePreEntitySpawning(toSpawningEntries(entries));
    }

    private static List<EntitySpawningEntry> toSpawningEntries(Iterable<Tuple<ItemStackSnapshot, Transform>> entries) {
        return Streams.stream(entries)
                .map(tuple -> new EntitySpawningEntry(EntityTypes.ITEM, tuple.getSecond(), entity -> {
                    entity.offer(Keys.ITEM_STACK_SNAPSHOT, tuple.getFirst());
                    entity.offer(LanternKeys.PICKUP_DELAY, LanternItem.DROPPED_PICKUP_DELAY);
                }))
                .collect(Collectors.toList());
    }

    public static Optional<Entity> handlePreDroppedItemSpawning(Transform transform, ItemStackSnapshot snapshot) {
        return LanternWorld.handlePreEntitySpawning(EntityTypes.ITEM, transform, entity -> {
            entity.offer(Keys.ITEM_STACK_SNAPSHOT, snapshot);
            entity.offer(LanternKeys.PICKUP_DELAY, LanternItem.DROPPED_PICKUP_DELAY);
        });
    }

    public static void handleDroppedItemSpawning(Transform transform, ItemStackSnapshot snapshot) {
        LanternWorld.handleEntitySpawning(EntityTypes.ITEM, transform, entity -> {
            entity.offer(Keys.ITEM_STACK_SNAPSHOT, snapshot);
            entity.offer(LanternKeys.PICKUP_DELAY, LanternItem.DROPPED_PICKUP_DELAY);
        }, SpongeEventFactory::createDropItemEventDispense);
    }
}
