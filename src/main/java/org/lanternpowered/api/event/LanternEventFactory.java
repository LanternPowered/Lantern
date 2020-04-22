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

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
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
import org.spongepowered.api.event.entity.living.player.CooldownEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.network.status.StatusClient;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.util.Transform;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.checkerframework.checker.nullness.qual.NonNull;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
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
            @NonNull World<?> world) {
        return SpongeEventFactory.createConstructEntityEventPre(cause, targetType, transform, world);
    }

    public static ChangeDataHolderEvent.@NonNull ValueChange createChangeDataHolderEventValueChange(
            @NonNull Cause cause,
            @NonNull DataTransactionResult originalChanges,
            DataHolder.@NonNull Mutable targetHolder) {
        return SpongeEventFactory.createChangeDataHolderEventValueChange(cause, originalChanges, targetHolder);
    }

    public static <T> ChangeServiceProviderEvent<T> createChangeServiceProviderEvent(
            @NonNull Cause cause,
            @NonNull ProviderRegistration<T> newProviderRegistration,
            @Nullable ProviderRegistration<T> previousProviderRegistration) {
        final TypeToken<ChangeServiceProviderEvent<T>> typeToken = new TypeToken<ChangeServiceProviderEvent<T>>() {}.where(
                new TypeParameter<T>() {}, newProviderRegistration.getService());
        //noinspection unchecked
        return SpongeEventFactory.createChangeServiceProviderEvent(cause, typeToken,
                newProviderRegistration, Optional.ofNullable(previousProviderRegistration));
    }

    public static CooldownEvent.@NonNull End createCooldownEventEnd(
            @NonNull Cause cause,
            @NonNull ItemType itemType,
            @NonNull Player player) {
        return SpongeEventFactory.createCooldownEventEnd(cause, itemType, player);
    }

    public static CooldownEvent.@NonNull Set createCooldownEventSet(
            @NonNull Cause cause,
            int originalNewCooldown,
            int newCooldown,
            @NonNull ItemType itemType,
            @NonNull Player player,
            @NonNull OptionalInt startingCooldown) {
        return SpongeEventFactory.createCooldownEventSet(
                cause, originalNewCooldown, newCooldown, itemType, player, startingCooldown);
    }

    public static ClientPingServerEvent.Response.@NonNull Players createClientPingServerEventResponsePlayers(
            @NonNull List<GameProfile> profiles,
            int max,
            int online) {
        return SpongeEventFactory.createClientPingServerEventResponsePlayers(profiles, max, online);
    }

    public static @NonNull ClientPingServerEvent createClientPingServerEvent(
            @NonNull Cause cause,
            @NonNull StatusClient client,
            ClientPingServerEvent.@NonNull Response response) {
        return SpongeEventFactory.createClientPingServerEvent(cause, client, response);
    }
}
