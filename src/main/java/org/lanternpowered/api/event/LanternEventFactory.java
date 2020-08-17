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
            @NonNull ServerLocation location,
            @NonNull Vector3d rotation,
            @NonNull EntityType<?> targetType) {
        return SpongeEventFactory.createConstructEntityEventPre(cause, location, rotation, targetType);
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
            @NonNull ServerPlayer player) {
        return SpongeEventFactory.createCooldownEventEnd(cause, itemType, player);
    }

    public static CooldownEvent.@NonNull Set createCooldownEventSet(
            @NonNull Cause cause,
            int originalNewCooldown,
            int newCooldown,
            @NonNull ItemType itemType,
            @NonNull ServerPlayer player,
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

    public static ServerSideConnectionEvent.@NonNull Auth createServerSideConnectionEventAuth(
            @NonNull Cause cause,
            @NonNull Component originalMessage,
            @NonNull Component message,
            @NonNull ServerSideConnection connection,
            boolean messageCancelled) {
        return SpongeEventFactory.createServerSideConnectionEventAuth(cause, originalMessage, message, connection, messageCancelled);
    }

    public static @NonNull PlayerChangeClientSettingsEvent createPlayerChangeClientSettingsEvent(
            @NonNull Cause cause,
            @NonNull ChatVisibility chatVisibility,
            @NonNull Set<SkinPart> displayedSkinParts,
            @NonNull Locale locale,
            @NonNull ServerPlayer player,
            boolean chatColorsEnabled,
            int viewDistance) {
        return SpongeEventFactory.createPlayerChangeClientSettingsEvent(
                cause, chatVisibility, displayedSkinParts, locale, player, chatColorsEnabled, viewDistance);
    }

    public static @NonNull ConstructPluginEvent createConstructPluginEvent(
            @NonNull Cause cause,
            @NonNull Game game,
            @NonNull PluginContainer plugin) {
        return SpongeEventFactory.createConstructPluginEvent(cause, game, plugin);
    }

    public static @NonNull ResourcePackStatusEvent createResourcePackStatusEvent(
            @NonNull Cause cause,
            @NonNull ResourcePack pack,
            @NonNull ServerPlayer player,
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

    public static AdvancementEvent.Grant createAdvancementEventGrant(
            @NonNull Cause cause,
            @NonNull Audience originalAudience,
            @Nullable Audience audience,
            @NonNull Component originalMessage,
            @NonNull Component message,
            @NonNull Advancement advancement,
            @NonNull ServerPlayer player,
            @NonNull Instant time,
            boolean messageCancelled) {
        return SpongeEventFactory.createAdvancementEventGrant(cause, originalAudience, Optional.ofNullable(audience), originalMessage,
                message, advancement, player, time, messageCancelled);
    }

    public static CriterionEvent.Grant createCriterionEventGrant(
            @NonNull Cause cause,
            @NonNull Advancement advancement,
            @NonNull AdvancementCriterion criterion,
            @NonNull ServerPlayer player,
            @NonNull Instant time) {
        return SpongeEventFactory.createCriterionEventGrant(cause, advancement, criterion, player, time);
    }

    public static CriterionEvent.Score.Grant createCriterionEventScoreGrant(
            @NonNull Cause cause,
            @NonNull Advancement advancement,
            @NonNull ScoreAdvancementCriterion criterion,
            @NonNull ServerPlayer player,
            @NonNull Instant time,
            int newScore,
            int previousScore) {
        return SpongeEventFactory.createCriterionEventScoreGrant(cause, advancement, criterion, player, time, newScore, previousScore);
    }

    public static AdvancementEvent.Revoke createAdvancementEventRevoke(
            @NonNull Cause cause,
            @NonNull Advancement advancement,
            @NonNull ServerPlayer player) {
        return SpongeEventFactory.createAdvancementEventRevoke(cause, advancement, player);
    }

    public static CriterionEvent.Revoke createCriterionEventRevoke(
            @NonNull Cause cause,
            @NonNull Advancement advancement,
            @NonNull AdvancementCriterion criterion,
            @NonNull ServerPlayer player) {
        return SpongeEventFactory.createCriterionEventRevoke(cause, advancement, criterion, player);
    }

    public static CriterionEvent.Score.Revoke createCriterionEventScoreRevoke(
            @NonNull Cause cause,
            @NonNull Advancement advancement,
            @NonNull ScoreAdvancementCriterion criterion,
            @NonNull ServerPlayer player,
            int newScore,
            int previousScore) {
        return SpongeEventFactory.createCriterionEventScoreRevoke(cause, advancement, criterion, player, newScore, previousScore);
    }

    public static CriterionEvent.Score.Change createCriterionEventScoreChange(
            @NonNull Cause cause,
            @NonNull Advancement advancement,
            @NonNull ScoreAdvancementCriterion criterion,
            @NonNull ServerPlayer player,
            int newScore,
            int previousScore) {
        return SpongeEventFactory.createCriterionEventScoreChange(cause, advancement, criterion, player, newScore, previousScore);
    }

    @SuppressWarnings("unchecked")
    public static <@NonNull C extends FilteredTriggerConfiguration> CriterionEvent.Trigger<C> createCriterionEventTrigger(
            @NonNull Cause cause,
            @NonNull Advancement advancement,
            @NonNull AdvancementCriterion criterion,
            @NonNull ServerPlayer player,
            @NonNull FilteredTrigger<C> trigger,
            boolean result) {
        return SpongeEventFactory.createCriterionEventTrigger(cause, advancement, criterion,
                TypeToken.of(trigger.getType().getConfigurationType()), player, trigger, result);
    }

    public static @NonNull DamageEntityEvent createDamageEntityEvent(
            @NonNull Cause cause,
            @NonNull Entity entity,
            @NonNull List<@NonNull DamageFunction> originalFunctions,
            double originalDamage) {
        return SpongeEventFactory.createDamageEntityEvent(cause, entity, originalFunctions, originalDamage);
    }

    public static DestructEntityEvent createDestructEntityEvent(
            @NonNull Cause cause,
            @NonNull Audience originalAudience,
            @Nullable Audience audience,
            @NonNull Component originalMessage,
            @NonNull Component message,
            @NonNull Entity entity,
            boolean messageCancelled) {
        return SpongeEventFactory.createDestructEntityEvent(cause, originalAudience,
                Optional.ofNullable(audience), originalMessage, message, entity, messageCancelled);
    }

    // TODO: Fix keepInventory vs keepsInventory naming inconsistencies

    public static DestructEntityEvent.@NonNull Death createDestructEntityEventDeath(
            @NonNull Cause cause,
            @NonNull Audience originalAudience,
            @Nullable Audience audience,
            @NonNull Component originalMessage,
            @NonNull Component message,
            @NonNull Living entity,
            boolean keepInventory,
            boolean messageCancelled) {
        return SpongeEventFactory.createDestructEntityEventDeath(cause, originalAudience, Optional.ofNullable(audience),
                originalMessage, message, entity, keepInventory, messageCancelled);
    }

    public static @NonNull HarvestEntityEvent createHarvestEntityEvent(
            @NonNull Cause cause,
            int originalExperience,
            int experience,
            @NonNull Entity entity) {
        return SpongeEventFactory.createHarvestEntityEvent(cause, originalExperience, experience, entity);
    }

    public static HarvestEntityEvent.@NonNull TargetPlayer createHarvestEntityEventTargetPlayer(
            @NonNull Cause cause,
            int originalExperience,
            int experience,
            @NonNull ServerPlayer entity,
            boolean keepsInventory,
            boolean keepsLevel,
            int level) {
        return SpongeEventFactory.createHarvestEntityEventTargetPlayer(cause, originalExperience,
                experience, entity, keepsInventory, keepsLevel, level);
    }

    public static @NonNull ExpireEntityEvent createExpireEntityEvent(
            @NonNull Cause cause,
            @NonNull Entity entity) {
        return SpongeEventFactory.createExpireEntityEvent(cause, entity);
    }

    public static LightningEvent.@NonNull Post createLightningEventPost(
            @NonNull Cause cause) {
        return SpongeEventFactory.createLightningEventPost(cause);
    }

    public static LightningEvent.@NonNull Strike createLightningEventStrike(
            @NonNull Cause cause,
            @NonNull List<@NonNull Entity> entities,
            @NonNull List<@NonNull Transaction<@NonNull BlockSnapshot>> transactions) {
        return SpongeEventFactory.createLightningEventStrike(cause, entities, transactions);
    }

    public static @NonNull ItemMergeWithItemEvent createItemMergeWithItemEvent(
            @NonNull Cause cause,
            @NonNull Item item,
            @NonNull Item itemToMerge) {
        return SpongeEventFactory.createItemMergeWithItemEvent(cause, item, itemToMerge);
    }

    public static ChangeInventoryEvent.@NonNull Pickup createChangeInventoryEventPickup(
            @NonNull Cause cause,
            @NonNull Inventory inventory,
            @NonNull List<@NonNull SlotTransaction> transactions) {
        return SpongeEventFactory.createChangeInventoryEventPickup(cause, inventory, transactions);
    }

    public static ServerSideConnectionEvent.@NonNull Login createServerSideConnectionEventLogin(
            @NonNull Cause cause,
            @NonNull Component originalMessage,
            @NonNull Component message,
            @NonNull ServerLocation fromLocation,
            @NonNull ServerLocation toLocation,
            @NonNull Vector3d fromRotation,
            @NonNull Vector3d toRotation,
            @NonNull ServerSideConnection connection,
            @NonNull User user,
            boolean messageCancelled) {
        return SpongeEventFactory.createServerSideConnectionEventLogin(cause, originalMessage, message, fromLocation,
                toLocation, fromRotation, toRotation, connection, user, messageCancelled);
    }

    public static ServerSideConnectionEvent.Join createServerSideConnectionEventJoin(
            @NonNull Cause cause,
            @NonNull Audience originalAudience,
            @Nullable Audience audience,
            @NonNull Component originalMessage,
            @NonNull Component message,
            @NonNull ServerSideConnection connection,
            @NonNull ServerPlayer player,
            boolean messageCancelled) {
        return SpongeEventFactory.createServerSideConnectionEventJoin(cause, originalAudience, Optional.ofNullable(audience),
                originalMessage, message, connection, player, messageCancelled);
    }

    public static ServerSideConnectionEvent.@NonNull Disconnect createServerSideConnectionEventDisconnect(
            @NonNull Cause cause,
            @NonNull Audience originalAudience,
            @Nullable Audience audience,
            @NonNull Component originalMessage,
            @NonNull Component message,
            @NonNull ServerSideConnection connection,
            @NonNull ServerPlayer player,
            boolean messageCancelled) {
        return SpongeEventFactory.createServerSideConnectionEventDisconnect(cause, originalAudience, Optional.ofNullable(audience),
                originalMessage, message, connection, player, messageCancelled);
    }

    public static ChangeWorldBorderEvent.@NonNull TargetPlayer createChangeWorldBorderEventTargetPlayer(
            @NonNull Cause cause,
            @Nullable WorldBorder newBorder,
            @Nullable WorldBorder previousBorder,
            @NonNull ServerPlayer player) {
        return SpongeEventFactory.createChangeWorldBorderEventTargetPlayer(cause,
                Optional.ofNullable(newBorder), player, Optional.ofNullable(previousBorder));
    }

    public static @NonNull PlayerChatEvent createPlayerChatEvent(Cause cause,
            @NonNull PlayerChatRouter originalChatRouter,
            @Nullable PlayerChatRouter chatRouter,
            @NonNull Component originalMessage,
            @NonNull Component message) {
        return SpongeEventFactory.createPlayerChatEvent(cause, originalChatRouter,
                Optional.ofNullable(chatRouter), originalMessage, message);
    }

    public static AdvancementTreeEvent.@NonNull GenerateLayout createAdvancementTreeEventGenerateLayout(
            @NonNull Cause cause,
            @NonNull TreeLayout layout,
            @NonNull AdvancementTree tree) {
        return SpongeEventFactory.createAdvancementTreeEventGenerateLayout(cause, layout, tree);
    }
}
