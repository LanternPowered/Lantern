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
package org.lanternpowered.api.event;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.CooldownEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.Transform;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.OptionalInt;

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

    public static CooldownEvent.@NonNull End createCooldownEventEnd(
            @NonNull Cause cause,
            @NonNull ItemType itemType,
            @NonNull Player player) {
        return SpongeEventFactory.createCooldownEventEnd(cause, itemType, player);
    }

    public static CooldownEvent.Set createCooldownEventSet(
            @NonNull Cause cause,
            int originalNewCooldown,
            int newCooldown,
            @NonNull ItemType itemType,
            @NonNull Player player,
            @NonNull OptionalInt startingCooldown) {
        return SpongeEventFactory.createCooldownEventSet(
                cause, originalNewCooldown, newCooldown, itemType, player, startingCooldown);
    }

}
