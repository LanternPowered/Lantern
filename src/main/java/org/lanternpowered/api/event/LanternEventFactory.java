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
import org.spongepowered.api.data.type.SkinPart;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.entity.living.player.CooldownEvent;
import org.spongepowered.api.event.entity.living.player.PlayerChangeClientSettingsEvent;
import org.spongepowered.api.event.entity.living.player.ResourcePackStatusEvent;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.network.rcon.RconConnectionEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.event.server.query.QueryServerEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.network.RconConnection;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.network.status.StatusClient;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.text.chat.ChatVisibility;
import org.spongepowered.api.util.Transform;
import org.spongepowered.api.world.World;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

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

    public static ClientConnectionEvent.@NonNull Auth createClientConnectionEventAuth(
            @NonNull Cause cause,
            @NonNull RemoteConnection connection,
            MessageEvent.@NonNull MessageFormatter formatter,
            @NonNull GameProfile profile,
            boolean messageCancelled) {
        return SpongeEventFactory.createClientConnectionEventAuth(
                cause, connection, formatter, profile, messageCancelled);
    }

    public static @NonNull PlayerChangeClientSettingsEvent createPlayerChangeClientSettingsEvent(
            @NonNull Cause cause,
            @NonNull ChatVisibility chatVisibility,
            @NonNull Set<SkinPart> displayedSkinParts,
            @NonNull Locale locale,
            @NonNull Player player,
            boolean chatColorsEnabled,
            int viewDistance) {
        return SpongeEventFactory.createPlayerChangeClientSettingsEvent(
                cause, chatVisibility, displayedSkinParts, locale, player, chatColorsEnabled, viewDistance);
    }

    public static @NonNull GameConstructionEvent createGameConstructionEvent(@NonNull Cause cause) {
        return SpongeEventFactory.createGameConstructionEvent(cause);
    }

    public static @NonNull GameInitializationEvent createGameInitializationEvent(@NonNull Cause cause) {
        return SpongeEventFactory.createGameInitializationEvent(cause);
    }

    public static @NonNull GamePreInitializationEvent createGamePreInitializationEvent(@NonNull Cause cause) {
        return SpongeEventFactory.createGamePreInitializationEvent(cause);
    }

    public static @NonNull GamePostInitializationEvent createGamePostInitializationEvent(@NonNull Cause cause) {
        return SpongeEventFactory.createGamePostInitializationEvent(cause);
    }

    public static @NonNull GameLoadCompleteEvent createGameLoadCompleteEvent(@NonNull Cause cause) {
        return SpongeEventFactory.createGameLoadCompleteEvent(cause);
    }

    public static @NonNull GameAboutToStartServerEvent createGameAboutToStartServerEvent(@NonNull Cause cause) {
        return SpongeEventFactory.createGameAboutToStartServerEvent(cause);
    }

    public static @NonNull GameStartingServerEvent createGameStartingServerEvent(@NonNull Cause cause) {
        return SpongeEventFactory.createGameStartingServerEvent(cause);
    }

    public static @NonNull GameStartedServerEvent createGameStartedServerEvent(@NonNull Cause cause) {
        return SpongeEventFactory.createGameStartedServerEvent(cause);
    }

    public static @NonNull GameStoppingServerEvent createGameStoppingServerEvent(@NonNull Cause cause) {
        return SpongeEventFactory.createGameStoppingServerEvent(cause);
    }

    public static @NonNull GameStoppedServerEvent createGameStoppedServerEvent(@NonNull Cause cause) {
        return SpongeEventFactory.createGameStoppedServerEvent(cause);
    }

    public static @NonNull GameStoppingEvent createGameStoppingEvent(@NonNull Cause cause) {
        return SpongeEventFactory.createGameStoppingEvent(cause);
    }

    public static @NonNull GameStoppedEvent createGameStoppedEvent(@NonNull Cause cause) {
        return SpongeEventFactory.createGameStoppedEvent(cause);
    }

    public static @NonNull ResourcePackStatusEvent createResourcePackStatusEvent(
            @NonNull Cause cause,
            @NonNull ResourcePack pack,
            @NonNull Player player,
            ResourcePackStatusEvent.@NonNull ResourcePackStatus status) {
        return SpongeEventFactory.createResourcePackStatusEvent(cause, pack, player, status);
    }

    public static RconConnectionEvent.@NonNull Connect createRconConnectionEventConnect(
            @NonNull Cause cause,
            @NonNull RconConnection connection) {
        return SpongeEventFactory.createRconConnectionEventConnect(cause, connection);
    }

    public static RconConnectionEvent.@NonNull Auth createRconConnectionEventAuth(
            @NonNull Cause cause,
            @NonNull RconConnection connection) {
        return SpongeEventFactory.createRconConnectionEventAuth(cause, connection);
    }

    public static RconConnectionEvent.@NonNull Disconnect createRconConnectionEventDisconnect(
            @NonNull Cause cause,
            @NonNull RconConnection connection) {
        return SpongeEventFactory.createRconConnectionEventDisconnect(cause, connection);
    }

    public static QueryServerEvent.@NonNull Basic createQueryServerEventBasic(
            @NonNull Cause cause,
            @NonNull InetSocketAddress address,
            @NonNull String gameType,
            @NonNull String map,
            @NonNull String motd,
            int maxPlayerCount,
            int maxSize,
            int playerCount,
            int size) {
        return SpongeEventFactory.createQueryServerEventBasic(cause, address, gameType, map, motd, maxPlayerCount, maxSize, playerCount, size);
    }

    public static QueryServerEvent.@NonNull Full createQueryServerEventFull(
            @NonNull Cause cause,
            @NonNull InetSocketAddress address,
            @NonNull Map<String, String> customValuesMap,
            @NonNull String gameId,
            @NonNull String gameType,
            @NonNull String map,
            @NonNull String motd,
            @NonNull List<String> players,
            @NonNull String plugins,
            @NonNull String version,
            int maxPlayerCount,
            int maxSize,
            int playerCount,
            int size) {
        return SpongeEventFactory.createQueryServerEventFull(cause, address, customValuesMap, gameId, gameType,
                map, motd, players, plugins, version, maxPlayerCount, maxSize, playerCount, size);
    }
}
