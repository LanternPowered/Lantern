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

import com.google.common.reflect.TypeToken;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Game;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.advancement.TreeLayout;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.ScoreAdvancementCriterion;
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger;
import org.spongepowered.api.advancement.criteria.trigger.FilteredTriggerConfiguration;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.type.SkinPart;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.PlayerChatRouter;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.chat.ChatVisibility;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.action.LightningEvent;
import org.spongepowered.api.event.advancement.AdvancementEvent;
import org.spongepowered.api.event.advancement.AdvancementTreeEvent;
import org.spongepowered.api.event.advancement.CriterionEvent;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageFunction;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.ExpireEntityEvent;
import org.spongepowered.api.event.entity.HarvestEntityEvent;
import org.spongepowered.api.event.entity.ItemMergeWithItemEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.entity.living.player.CooldownEvent;
import org.spongepowered.api.event.entity.living.player.PlayerChangeClientSettingsEvent;
import org.spongepowered.api.event.entity.living.player.ResourcePackStatusEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.message.PlayerChatEvent;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.event.network.rcon.RconConnectionEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.event.server.query.QueryServerEvent;
import org.spongepowered.api.event.world.ChangeWorldBorderEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.network.RconConnection;
import org.spongepowered.api.network.ServerSideConnection;
import org.spongepowered.api.network.status.StatusClient;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.plugin.PluginContainer;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class LanternEventFactory {

    public static DropItemEvent.@NonNull Pre createDropItemEventPre(
            final @NonNull Cause cause,
            final @NonNull List<ItemStackSnapshot> originalDroppedItems,
            final @NonNull List<ItemStackSnapshot> droppedItems) {
        return SpongeEventFactory.createDropItemEventPre(cause, originalDroppedItems, droppedItems);
    }

    public static DropItemEvent.@NonNull Destruct createDropItemEventDestruct(
            final @NonNull Cause cause,
            final @NonNull List<Entity> entities) {
        return SpongeEventFactory.createDropItemEventDestruct(cause, entities);
    }

    public static @NonNull SpawnEntityEvent createSpawnEntityEvent(
            final @NonNull Cause cause,
            final @NonNull List<Entity> entities) {
        return SpongeEventFactory.createSpawnEntityEvent(cause, entities);
    }

    public static SpawnEntityEvent.@NonNull ChunkLoad createSpawnEntityEventChunkLoad(
            final @NonNull Cause cause,
            final @NonNull List<Entity> entities) {
        return SpongeEventFactory.createSpawnEntityEventChunkLoad(cause, entities);
    }

    public static ConstructEntityEvent.@NonNull Pre createConstructEntityEventPre(
            final @NonNull Cause cause,
            final @NonNull ServerLocation location,
            final @NonNull Vector3d rotation,
            final @NonNull EntityType<?> targetType) {
        return SpongeEventFactory.createConstructEntityEventPre(cause, location, rotation, targetType);
    }

    public static ChangeDataHolderEvent.@NonNull ValueChange createChangeDataHolderEventValueChange(
            final @NonNull Cause cause,
            final @NonNull DataTransactionResult originalChanges,
            DataHolder.@NonNull Mutable targetHolder) {
        return SpongeEventFactory.createChangeDataHolderEventValueChange(cause, originalChanges, targetHolder);
    }

    public static CooldownEvent.@NonNull End createCooldownEventEnd(
            final @NonNull Cause cause,
            final @NonNull ItemType itemType,
            final @NonNull ServerPlayer player) {
        return SpongeEventFactory.createCooldownEventEnd(cause, itemType, player);
    }

    public static CooldownEvent.@NonNull Set createCooldownEventSet(
            final @NonNull Cause cause,
            final int originalNewCooldown,
            final int newCooldown,
            final @NonNull ItemType itemType,
            final @NonNull ServerPlayer player,
            final @NonNull OptionalInt startingCooldown) {
        return SpongeEventFactory.createCooldownEventSet(
                cause, originalNewCooldown, newCooldown, itemType, player, startingCooldown);
    }

    public static ClientPingServerEvent.Response.@NonNull Players createClientPingServerEventResponsePlayers(
            final @NonNull List<GameProfile> profiles,
            final int max,
            final int online) {
        return SpongeEventFactory.createClientPingServerEventResponsePlayers(profiles, max, online);
    }

    public static @NonNull ClientPingServerEvent createClientPingServerEvent(
            final @NonNull Cause cause,
            final @NonNull StatusClient client,
            ClientPingServerEvent.@NonNull Response response) {
        return SpongeEventFactory.createClientPingServerEvent(cause, client, response);
    }

    public static ServerSideConnectionEvent.@NonNull Auth createServerSideConnectionEventAuth(
            final @NonNull Cause cause,
            final @NonNull Component originalMessage,
            final @NonNull Component message,
            final @NonNull ServerSideConnection connection,
            final boolean messageCancelled) {
        return SpongeEventFactory.createServerSideConnectionEventAuth(cause, originalMessage, message, connection, messageCancelled);
    }

    public static @NonNull PlayerChangeClientSettingsEvent createPlayerChangeClientSettingsEvent(
            final @NonNull Cause cause,
            final @NonNull ChatVisibility chatVisibility,
            final @NonNull Set<SkinPart> displayedSkinParts,
            final @NonNull Locale locale,
            final @NonNull ServerPlayer player,
            final boolean chatColorsEnabled,
            final int viewDistance) {
        return SpongeEventFactory.createPlayerChangeClientSettingsEvent(
                cause, chatVisibility, displayedSkinParts, locale, player, chatColorsEnabled, viewDistance);
    }

    public static @NonNull ConstructPluginEvent createConstructPluginEvent(
            final @NonNull Cause cause,
            final @NonNull Game game,
            final @NonNull PluginContainer plugin) {
        return SpongeEventFactory.createConstructPluginEvent(cause, game, plugin);
    }

    public static @NonNull ResourcePackStatusEvent createResourcePackStatusEvent(
            final @NonNull Cause cause,
            final @NonNull ResourcePack pack,
            final @NonNull ServerPlayer player,
            ResourcePackStatusEvent.@NonNull ResourcePackStatus status) {
        return SpongeEventFactory.createResourcePackStatusEvent(cause, pack, player, status);
    }

    public static RconConnectionEvent.@NonNull Connect createRconConnectionEventConnect(
            final @NonNull Cause cause,
            final @NonNull RconConnection connection) {
        return SpongeEventFactory.createRconConnectionEventConnect(cause, connection);
    }

    public static RconConnectionEvent.@NonNull Auth createRconConnectionEventAuth(
            final @NonNull Cause cause,
            final @NonNull RconConnection connection) {
        return SpongeEventFactory.createRconConnectionEventAuth(cause, connection);
    }

    public static RconConnectionEvent.@NonNull Disconnect createRconConnectionEventDisconnect(
            final @NonNull Cause cause,
            final @NonNull RconConnection connection) {
        return SpongeEventFactory.createRconConnectionEventDisconnect(cause, connection);
    }

    public static QueryServerEvent.@NonNull Basic createQueryServerEventBasic(
            final @NonNull Cause cause,
            final @NonNull InetSocketAddress address,
            final @NonNull String gameType,
            final @NonNull String map,
            final @NonNull String motd,
            final int maxPlayerCount,
            final int maxSize,
            final int playerCount,
            final int size) {
        return SpongeEventFactory.createQueryServerEventBasic(cause, address, gameType, map, motd, maxPlayerCount, maxSize, playerCount, size);
    }

    public static QueryServerEvent.@NonNull Full createQueryServerEventFull(
            final @NonNull Cause cause,
            final @NonNull InetSocketAddress address,
            final @NonNull Map<String, String> customValuesMap,
            final @NonNull String gameId,
            final @NonNull String gameType,
            final @NonNull String map,
            final @NonNull String motd,
            final @NonNull List<String> players,
            final @NonNull String plugins,
            final @NonNull String version,
            final int maxPlayerCount,
            final int maxSize,
            final int playerCount,
            final int size) {
        return SpongeEventFactory.createQueryServerEventFull(cause, address, customValuesMap, gameId, gameType,
                map, motd, players, plugins, version, maxPlayerCount, maxSize, playerCount, size);
    }

    public static AdvancementEvent.Grant createAdvancementEventGrant(
            final @NonNull Cause cause,
            final @NonNull Audience originalAudience,
            final @Nullable Audience audience,
            final @NonNull Component originalMessage,
            final @NonNull Component message,
            final @NonNull Advancement advancement,
            final @NonNull ServerPlayer player,
            final @NonNull Instant time,
            final boolean messageCancelled) {
        return SpongeEventFactory.createAdvancementEventGrant(cause, originalAudience, Optional.ofNullable(audience), originalMessage,
                message, advancement, player, time, messageCancelled);
    }

    public static CriterionEvent.Grant createCriterionEventGrant(
            final @NonNull Cause cause,
            final @NonNull Advancement advancement,
            final @NonNull AdvancementCriterion criterion,
            final @NonNull ServerPlayer player,
            final @NonNull Instant time) {
        return SpongeEventFactory.createCriterionEventGrant(cause, advancement, criterion, player, time);
    }

    public static CriterionEvent.Score.Grant createCriterionEventScoreGrant(
            final @NonNull Cause cause,
            final @NonNull Advancement advancement,
            final @NonNull ScoreAdvancementCriterion criterion,
            final @NonNull ServerPlayer player,
            final @NonNull Instant time,
            final int newScore,
            final int previousScore) {
        return SpongeEventFactory.createCriterionEventScoreGrant(cause, advancement, criterion, player, time, newScore, previousScore);
    }

    public static AdvancementEvent.Revoke createAdvancementEventRevoke(
            final @NonNull Cause cause,
            final @NonNull Advancement advancement,
            final @NonNull ServerPlayer player) {
        return SpongeEventFactory.createAdvancementEventRevoke(cause, advancement, player);
    }

    public static CriterionEvent.Revoke createCriterionEventRevoke(
            final @NonNull Cause cause,
            final @NonNull Advancement advancement,
            final @NonNull AdvancementCriterion criterion,
            final @NonNull ServerPlayer player) {
        return SpongeEventFactory.createCriterionEventRevoke(cause, advancement, criterion, player);
    }

    public static CriterionEvent.Score.Revoke createCriterionEventScoreRevoke(
            final @NonNull Cause cause,
            final @NonNull Advancement advancement,
            final @NonNull ScoreAdvancementCriterion criterion,
            final @NonNull ServerPlayer player,
            final int newScore,
            final int previousScore) {
        return SpongeEventFactory.createCriterionEventScoreRevoke(cause, advancement, criterion, player, newScore, previousScore);
    }

    public static CriterionEvent.Score.Change createCriterionEventScoreChange(
            final @NonNull Cause cause,
            final @NonNull Advancement advancement,
            final @NonNull ScoreAdvancementCriterion criterion,
            final @NonNull ServerPlayer player,
            final int newScore,
            final int previousScore) {
        return SpongeEventFactory.createCriterionEventScoreChange(cause, advancement, criterion, player, newScore, previousScore);
    }

    @SuppressWarnings("unchecked")
    public static <@NonNull C extends FilteredTriggerConfiguration> CriterionEvent.Trigger<C> createCriterionEventTrigger(
            final @NonNull Cause cause,
            final @NonNull Advancement advancement,
            final @NonNull AdvancementCriterion criterion,
            final @NonNull ServerPlayer player,
            final @NonNull FilteredTrigger<C> trigger,
            final boolean result) {
        return SpongeEventFactory.createCriterionEventTrigger(cause, advancement, criterion,
                TypeToken.of(trigger.getType().getConfigurationType()), player, trigger, result);
    }

    public static @NonNull DamageEntityEvent createDamageEntityEvent(
            final @NonNull Cause cause,
            final @NonNull Entity entity,
            final @NonNull List<@NonNull DamageFunction> originalFunctions,
            final double originalDamage) {
        return SpongeEventFactory.createDamageEntityEvent(cause, entity, originalFunctions, originalDamage);
    }

    public static DestructEntityEvent createDestructEntityEvent(
            final @NonNull Cause cause,
            final @NonNull Audience originalAudience,
            final @Nullable Audience audience,
            final @NonNull Component originalMessage,
            final @NonNull Component message,
            final @NonNull Entity entity,
            final boolean messageCancelled) {
        return SpongeEventFactory.createDestructEntityEvent(cause, originalAudience,
                Optional.ofNullable(audience), originalMessage, message, entity, messageCancelled);
    }

    // TODO: Fix keepInventory vs keepsInventory naming inconsistencies

    public static DestructEntityEvent.@NonNull Death createDestructEntityEventDeath(
            final @NonNull Cause cause,
            final @NonNull Audience originalAudience,
            final @Nullable Audience audience,
            final @NonNull Component originalMessage,
            final @NonNull Component message,
            final @NonNull Living entity,
            final boolean keepInventory,
            final boolean messageCancelled) {
        return SpongeEventFactory.createDestructEntityEventDeath(cause, originalAudience, Optional.ofNullable(audience),
                originalMessage, message, entity, keepInventory, messageCancelled);
    }

    public static @NonNull HarvestEntityEvent createHarvestEntityEvent(
            final @NonNull Cause cause,
            final int originalExperience,
            final int experience,
            final @NonNull Entity entity) {
        return SpongeEventFactory.createHarvestEntityEvent(cause, originalExperience, experience, entity);
    }

    public static HarvestEntityEvent.@NonNull TargetPlayer createHarvestEntityEventTargetPlayer(
            final @NonNull Cause cause,
            final int originalExperience,
            final int experience,
            final @NonNull ServerPlayer entity,
            final boolean keepsInventory,
            final boolean keepsLevel,
            final int level) {
        return SpongeEventFactory.createHarvestEntityEventTargetPlayer(cause, originalExperience,
                experience, entity, keepsInventory, keepsLevel, level);
    }

    public static @NonNull ExpireEntityEvent createExpireEntityEvent(
            final @NonNull Cause cause,
            final @NonNull Entity entity) {
        return SpongeEventFactory.createExpireEntityEvent(cause, entity);
    }

    public static LightningEvent.@NonNull Post createLightningEventPost(
            final @NonNull Cause cause) {
        return SpongeEventFactory.createLightningEventPost(cause);
    }

    public static LightningEvent.@NonNull Strike createLightningEventStrike(
            final @NonNull Cause cause,
            final @NonNull List<@NonNull Entity> entities,
            final @NonNull List<@NonNull Transaction<@NonNull BlockSnapshot>> transactions) {
        return SpongeEventFactory.createLightningEventStrike(cause, entities, transactions);
    }

    public static @NonNull ItemMergeWithItemEvent createItemMergeWithItemEvent(
            final @NonNull Cause cause,
            final @NonNull Item item,
            final @NonNull Item itemToMerge) {
        return SpongeEventFactory.createItemMergeWithItemEvent(cause, item, itemToMerge);
    }

    public static ChangeInventoryEvent.@NonNull Pickup createChangeInventoryEventPickup(
            final @NonNull Cause cause,
            final @NonNull Inventory inventory,
            final @NonNull List<@NonNull SlotTransaction> transactions) {
        return SpongeEventFactory.createChangeInventoryEventPickup(cause, inventory, transactions);
    }

    public static ServerSideConnectionEvent.@NonNull Login createServerSideConnectionEventLogin(
            final @NonNull Cause cause,
            final @NonNull Component originalMessage,
            final @NonNull Component message,
            final @NonNull ServerLocation fromLocation,
            final @NonNull ServerLocation toLocation,
            final @NonNull Vector3d fromRotation,
            final @NonNull Vector3d toRotation,
            final @NonNull ServerSideConnection connection,
            final @NonNull User user,
            final boolean messageCancelled) {
        return SpongeEventFactory.createServerSideConnectionEventLogin(cause, originalMessage, message, fromLocation,
                toLocation, fromRotation, toRotation, connection, user, messageCancelled);
    }

    public static ServerSideConnectionEvent.Join createServerSideConnectionEventJoin(
            final @NonNull Cause cause,
            final @NonNull Audience originalAudience,
            final @Nullable Audience audience,
            final @NonNull Component originalMessage,
            final @NonNull Component message,
            final @NonNull ServerSideConnection connection,
            final @NonNull ServerPlayer player,
            final boolean messageCancelled) {
        return SpongeEventFactory.createServerSideConnectionEventJoin(cause, originalAudience, Optional.ofNullable(audience),
                originalMessage, message, connection, player, messageCancelled);
    }

    public static ServerSideConnectionEvent.@NonNull Disconnect createServerSideConnectionEventDisconnect(
            final @NonNull Cause cause,
            final @NonNull Audience originalAudience,
            final @Nullable Audience audience,
            final @NonNull Component originalMessage,
            final @NonNull Component message,
            final @NonNull ServerSideConnection connection,
            final @NonNull ServerPlayer player,
            final boolean messageCancelled) {
        return SpongeEventFactory.createServerSideConnectionEventDisconnect(cause, originalAudience, Optional.ofNullable(audience),
                originalMessage, message, connection, player, messageCancelled);
    }

    public static ChangeWorldBorderEvent.@NonNull TargetPlayer createChangeWorldBorderEventTargetPlayer(
            final @NonNull Cause cause,
            final @Nullable WorldBorder newBorder,
            final @Nullable WorldBorder previousBorder,
            final @NonNull ServerPlayer player) {
        return SpongeEventFactory.createChangeWorldBorderEventTargetPlayer(cause,
                Optional.ofNullable(newBorder), player, Optional.ofNullable(previousBorder));
    }

    public static @NonNull PlayerChatEvent createPlayerChatEvent(
            final @NonNull Cause cause,
            final @NonNull PlayerChatRouter originalChatRouter,
            final @Nullable PlayerChatRouter chatRouter,
            final @NonNull Component originalMessage,
            final @NonNull Component message) {
        return SpongeEventFactory.createPlayerChatEvent(cause, originalChatRouter,
                Optional.ofNullable(chatRouter), originalMessage, message);
    }

    public static AdvancementTreeEvent.@NonNull GenerateLayout createAdvancementTreeEventGenerateLayout(
            final @NonNull Cause cause,
            final @NonNull TreeLayout layout,
            final @NonNull AdvancementTree tree) {
        return SpongeEventFactory.createAdvancementTreeEventGenerateLayout(cause, layout, tree);
    }

    public static ChangeInventoryEvent.@NonNull SwapHand createChangeInventoryEventSwapHand(
            @NonNull Cause cause,
            @NonNull Inventory inventory,
            @NonNull List<@NonNull SlotTransaction> transactions) {
        return SpongeEventFactory.createChangeInventoryEventSwapHand(cause, inventory, transactions);
    }
}
