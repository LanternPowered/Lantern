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
package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.message.MessageRegistry;
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecInOutKeepAlive;
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInAdvancementTree;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInChangeSign;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInChatMessage;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInClickRecipe;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInClickWindow;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInClientSettings;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInClientStatus;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInCraftingBookData;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInCreativeWindowAction;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInEnchantItem;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInOutCloseWindow;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInOutConfirmWindowTransaction;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInOutCustomPayload;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInOutHeldItemChange;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPlayerAbilities;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPlayerAction;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPlayerBlockPlacement;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPlayerDigging;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPlayerLook;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPlayerMovement;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPlayerMovementAndLook;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPlayerOnGroundState;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPlayerSwingArm;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPlayerUseItem;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPlayerVehicleControls;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPlayerVehicleMovement;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInResourcePackStatus;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInSpectate;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInTabComplete;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInTeleportConfirm;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInUseEntity;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutAddPotionEffect;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutAdvancements;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutBlockAction;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutBlockBreakAnimation;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutBlockChange;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutBossBar;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutChangeGameState;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutChatMessage;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutChunkData;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutDestroyEntities;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutDisplayRecipe;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEffect;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityAnimation;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityCollectItem;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityEquipment;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityHeadLook;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityLook;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityLookAndRelativeMove;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityMetadata;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityRelativeMove;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityStatus;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityTeleport;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityVelocity;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutMultiBlockChange;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutNamedSoundEffect;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutOpenSign;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutOpenWindow;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerAbilities;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerHealthUpdate;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerJoinGame;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerPositionAndLook;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerRespawn;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerSpawnPosition;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutRemovePotionEffect;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutScoreboardDisplayObjective;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutScoreboardObjective;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutScoreboardScore;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSelectAdvancementTree;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSendResourcePack;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSetCamera;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSetCooldown;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSetDifficulty;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSetEntityPassengers;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSetExperience;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSetWindowSlot;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSoundEffect;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSpawnExperienceOrb;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSpawnMob;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSpawnObject;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSpawnPainting;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSpawnParticle;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSpawnPlayer;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSpawnThunderbolt;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutStatistics;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutTabComplete;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutTabListHeaderAndFooter;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutTeams;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutTitle;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutUnloadChunk;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutUnlockRecipes;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutUpdateTileEntity;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutWindowItems;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutWindowProperty;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutWorldBorder;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutWorldTime;
import org.lanternpowered.server.network.vanilla.message.handler.ChannelMessagesHandler;
import org.lanternpowered.server.network.vanilla.message.handler.PlayProtocolMovementHandler;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInAdvancementTree;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInChangeSign;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInChatMessage;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInClientSettings;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInEditBook;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInRequestStatistics;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInResourcePackStatus;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInSignBook;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInSwapHandItems;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInTabComplete;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInUseEntityAttack;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInUseEntityInteract;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutSetGameMode;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutTheEnd;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutWorldSky;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageInOutKeepAlive;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInAdvancementTree;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeSign;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChatMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClientSettings;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInCraftingBookState;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInEditBook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutBrand;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutCloseWindow;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutConfirmWindowTransaction;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutFinishUsingItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutHeldItemChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPerformRespawn;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInRequestStatistics;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInResourcePackStatus;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInSignBook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInSwapHandItems;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInTabComplete;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInTeleportConfirm;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInUseEntity;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutAddPotionEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutAdvancements;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockAction;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockBreakAnimation;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBossBar;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChatMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChunkData;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDestroyEntities;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDisplayRecipe;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityAnimation;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityCollectItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityEquipment;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityHeadLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityLookAndRelativeMove;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityMetadata;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityRelativeMove;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityStatus;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityTeleport;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityVelocity;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutMultiBlockChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutNamedSoundEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenBook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenSign;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenWindow;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerAbilities;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerHealthUpdate;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerJoinGame;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerPositionAndLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerRespawn;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerSpawnPosition;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutRecord;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutRemovePotionEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardDisplayObjective;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardObjective;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardScore;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSelectAdvancementTree;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSendResourcePack;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetCamera;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetCooldown;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetDifficulty;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetEntityPassengers;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetExperience;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetGameMode;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetOpLevel;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetReducedDebug;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetWindowSlot;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSoundEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnExperienceOrb;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnMob;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnObject;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnPainting;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnParticle;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnPlayer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnThunderbolt;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutStatistics;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutStopSounds;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabComplete;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListHeaderAndFooter;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTeams;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTheEnd;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTitle;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutUnloadChunk;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutUnlockRecipes;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutUpdateTileEntity;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWindowItems;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWindowProperty;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldBorder;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldSky;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldTime;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayOutChangeGameState;

final class ProtocolPlay extends ProtocolBase {

    ProtocolPlay() {
        ////////////////////////
        /// Inbound Messages ///
        ////////////////////////

        final MessageRegistry inbound = inbound();

        // Register the codecs and handlers of the default messages
        inbound.bind(CodecPlayInTeleportConfirm.class, MessagePlayInTeleportConfirm.class);
        inbound.bind(CodecPlayInTabComplete.class);
        inbound.bind(CodecPlayInChatMessage.class);
        inbound.bind(CodecPlayInClientStatus.class);
        inbound.bind(CodecPlayInClientSettings.class);
        inbound.bind(CodecPlayInOutConfirmWindowTransaction.class);
        inbound.bind(CodecPlayInEnchantItem.class);
        inbound.bind(CodecPlayInClickWindow.class);
        inbound.bind(CodecPlayInOutCloseWindow.class);
        inbound.bind(CodecPlayInOutCustomPayload.class);
        inbound.bind(CodecPlayInUseEntity.class);
        inbound.bind(CodecInOutKeepAlive.class);
        inbound.bind(CodecPlayInPlayerOnGroundState.class);
        inbound.bind(CodecPlayInPlayerMovement.class);
        inbound.bind(CodecPlayInPlayerMovementAndLook.class);
        inbound.bind(CodecPlayInPlayerLook.class);
        inbound.bind(CodecPlayInPlayerVehicleMovement.class);
        inbound.bind(); // TODO: Steer Boat
        inbound.bind(CodecPlayInClickRecipe.class);
        inbound.bind(CodecPlayInPlayerAbilities.class);
        inbound.bind(CodecPlayInPlayerDigging.class);
        inbound.bind(CodecPlayInPlayerAction.class);
        inbound.bind(CodecPlayInPlayerVehicleControls.class);
        inbound.bind(CodecPlayInCraftingBookData.class);
        inbound.bind(CodecPlayInResourcePackStatus.class);
        inbound.bind(CodecPlayInAdvancementTree.class);
        inbound.bind(CodecPlayInOutHeldItemChange.class);
        inbound.bind(CodecPlayInCreativeWindowAction.class);
        inbound.bind(CodecPlayInChangeSign.class);
        inbound.bind(CodecPlayInPlayerSwingArm.class);

        inbound.bind(CodecPlayInSpectate.class);
        inbound.bind(CodecPlayInPlayerBlockPlacement.class);
        inbound.bind(CodecPlayInPlayerUseItem.class);

        // Bind the handlers
        inbound.addHandlerProvider((session, binder) -> {
            binder.bind(new ChannelMessagesHandler());
            binder.bind(new PlayProtocolMovementHandler());

            // TODO: Move more to new system

            // TODO: Handler for MessagePlayInTeleportConfirm
            binder.bind(MessagePlayInTabComplete.class, new HandlerPlayInTabComplete());
            binder.bind(MessagePlayInChatMessage.class, new HandlerPlayInChatMessage());
            binder.bind(MessagePlayInClientSettings.class, new HandlerPlayInClientSettings());
            // TODO: Handler for MessagePlayInOutConfirmWindowTransaction
            binder.bind(MessagePlayInResourcePackStatus.class, new HandlerPlayInResourcePackStatus());
            binder.bind(MessagePlayInChangeSign.class, new HandlerPlayInChangeSign());
            // TODO: Handler for MessagePlayInSpectate
            // TODO: Handler for MessagePlayInOutBrand
            // TODO: Handler for MessagePlayInEditCommandBlock.Block
            // TODO: Handler for MessagePlayInEditCommandBlock.AdvancedBlock
            // TODO: Handler for MessagePlayInEditCommandBlock.Entity
            binder.bind(MessagePlayInEditBook.class, new HandlerPlayInEditBook());
            binder.bind(MessagePlayInSignBook.class, new HandlerPlayInSignBook());
            binder.bind(MessagePlayInUseEntity.Attack.class, new HandlerPlayInUseEntityAttack());
            binder.bind(MessagePlayInUseEntity.Interact.class, new HandlerPlayInUseEntityInteract());
            binder.bind(MessagePlayInSwapHandItems.class, new HandlerPlayInSwapHandItems());
            binder.bind(MessagePlayInPerformRespawn.class, (context, message) -> context.getSession().getPlayer().handleRespawn());
            binder.bind(MessagePlayInRequestStatistics.class, new HandlerPlayInRequestStatistics());
            // TODO: Handler for MessagePlayInLeaveBed
            // TODO: Handler for MessagePlayInPlayerVehicleJump
            binder.bind(MessagePlayInAdvancementTree.class, new HandlerPlayInAdvancementTree());

            final LanternPlayer player = session.getPlayer();
            binder.bind(player.getInteractionHandler());
            binder.bind(player.getContainerSession());
        });

        /////////////////////////
        /// Outbound Messages ///
        /////////////////////////

        final MessageRegistry outbound = outbound();

        // Register the processors
        outbound.bindProcessor(MessagePlayOutTheEnd.class,
                new ProcessorPlayOutTheEnd());
        outbound.bindProcessor(MessagePlayOutParticleEffect.class,
                new ProcessorPlayOutParticleEffect());
        outbound.bindProcessor(MessagePlayOutSetGameMode.class,
                new ProcessorPlayOutSetGameMode());
        outbound.bindProcessor(MessagePlayOutWorldSky.class,
                new ProcessorPlayOutWorldSky());
        outbound.bindProcessor(MessagePlayOutTabListEntries.class,
                new ProcessorPlayOutTabListEntries());

        outbound.bind(CodecPlayOutSpawnObject.class,
                MessagePlayOutSpawnObject.class);
        outbound.bind(CodecPlayOutSpawnExperienceOrb.class,
                MessagePlayOutSpawnExperienceOrb.class);
        outbound.bind(CodecPlayOutSpawnThunderbolt.class,
                MessagePlayOutSpawnThunderbolt.class);
        outbound.bind(CodecPlayOutSpawnMob.class,
                MessagePlayOutSpawnMob.class);
        outbound.bind(CodecPlayOutSpawnPainting.class,
                MessagePlayOutSpawnPainting.class);
        outbound.bind(CodecPlayOutSpawnPlayer.class,
                MessagePlayOutSpawnPlayer.class);
        outbound.bind(CodecPlayOutEntityAnimation.class,
                MessagePlayOutEntityAnimation.class);
        outbound.bind(CodecPlayOutStatistics.class,
                MessagePlayOutStatistics.class);
        outbound.bind(CodecPlayOutBlockBreakAnimation.class,
                MessagePlayOutBlockBreakAnimation.class);
        outbound.bind(CodecPlayOutUpdateTileEntity.class,
                MessagePlayOutUpdateTileEntity.class);
        outbound.bind(CodecPlayOutBlockAction.class,
                MessagePlayOutBlockAction.class);
        outbound.bind(CodecPlayOutBlockChange.class,
                MessagePlayOutBlockChange.class);
        outbound.bind(CodecPlayOutBossBar.class,
                MessagePlayOutBossBar.Add.class,
                MessagePlayOutBossBar.Remove.class,
                MessagePlayOutBossBar.UpdatePercent.class,
                MessagePlayOutBossBar.UpdateStyle.class,
                MessagePlayOutBossBar.UpdateTitle.class,
                MessagePlayOutBossBar.UpdateMisc.class);
        outbound.bind(CodecPlayOutSetDifficulty.class,
                MessagePlayOutSetDifficulty.class);
        outbound.bind(CodecPlayOutTabComplete.class,
                MessagePlayOutTabComplete.class);
        outbound.bind(CodecPlayOutChatMessage.class,
                MessagePlayOutChatMessage.class);
        outbound.bind(CodecPlayOutMultiBlockChange.class,
                MessagePlayOutMultiBlockChange.class);
        outbound.bind(CodecPlayInOutConfirmWindowTransaction.class,
                MessagePlayInOutConfirmWindowTransaction.class);
        outbound.bind(CodecPlayInOutCloseWindow.class,
                MessagePlayInOutCloseWindow.class);
        outbound.bind(CodecPlayOutOpenWindow.class,
                MessagePlayOutOpenWindow.class);
        outbound.bind(CodecPlayOutWindowItems.class,
                MessagePlayOutWindowItems.class);
        outbound.bind(CodecPlayOutWindowProperty.class,
                MessagePlayOutWindowProperty.class);
        outbound.bind(CodecPlayOutSetWindowSlot.class,
                MessagePlayOutSetWindowSlot.class);
        outbound.bind(CodecPlayOutSetCooldown.class,
                MessagePlayOutSetCooldown.class);
        outbound.bind(CodecPlayInOutCustomPayload.class,
                MessagePlayInOutChannelPayload.class,
                MessagePlayOutOpenBook.class,
                MessagePlayInOutBrand.class,
                MessagePlayOutStopSounds.class);
        outbound.bind(CodecPlayOutNamedSoundEffect.class,
                MessagePlayOutNamedSoundEffect.class);
        outbound.bind(CodecOutDisconnect.class,
                MessageOutDisconnect.class);
        outbound.bind(CodecPlayOutEntityStatus.class,
                MessagePlayOutEntityStatus.class,
                MessagePlayOutSetOpLevel.class,
                MessagePlayOutSetReducedDebug.class,
                MessagePlayInOutFinishUsingItem.class);
        outbound.bind(); // TODO: Explosion
        outbound.bind(CodecPlayOutUnloadChunk.class,
                MessagePlayOutUnloadChunk.class);
        outbound.bind(CodecPlayOutChangeGameState.class,
                MessagePlayOutChangeGameState.class);
        outbound.bind(CodecInOutKeepAlive.class,
                MessageInOutKeepAlive.class);
        outbound.bind(CodecPlayOutChunkData.class,
                MessagePlayOutChunkData.class);
        outbound.bind(CodecPlayOutEffect.class,
                MessagePlayOutEffect.class,
                MessagePlayOutRecord.class);
        outbound.bind(CodecPlayOutSpawnParticle.class,
                MessagePlayOutSpawnParticle.class);
        outbound.bind(CodecPlayOutPlayerJoinGame.class,
                MessagePlayOutPlayerJoinGame.class);
        outbound.bind(); // TODO: Map
        outbound.bind(); // TODO: Entity ???
        outbound.bind(CodecPlayOutEntityRelativeMove.class,
                MessagePlayOutEntityRelativeMove.class);
        outbound.bind(CodecPlayOutEntityLookAndRelativeMove.class,
                MessagePlayOutEntityLookAndRelativeMove.class);
        outbound.bind(CodecPlayOutEntityLook.class,
                MessagePlayOutEntityLook.class);
        outbound.bind(); // TODO: Vehicle Move
        outbound.bind(CodecPlayOutOpenSign.class,
                MessagePlayOutOpenSign.class);
        outbound.bind(CodecPlayOutDisplayRecipe.class,
                MessagePlayOutDisplayRecipe.class);
        outbound.bind(CodecPlayOutPlayerAbilities.class,
                MessagePlayOutPlayerAbilities.class);
        outbound.bind(); // TODO: Combat Event
        outbound.bind(CodecPlayOutTabListEntries.class,
                MessagePlayOutTabListEntries.class);
        outbound.bind(CodecPlayOutPlayerPositionAndLook.class,
                MessagePlayOutPlayerPositionAndLook.class);
        outbound.bind(); // TODO: Use Bed
        outbound.bind(CodecPlayOutUnlockRecipes.class,
                MessagePlayOutUnlockRecipes.Add.class,
                MessagePlayOutUnlockRecipes.Init.class,
                MessagePlayOutUnlockRecipes.Remove.class);
        outbound.bind(CodecPlayOutDestroyEntities.class,
                MessagePlayOutDestroyEntities.class);
        outbound.bind(CodecPlayOutRemovePotionEffect.class,
                MessagePlayOutRemovePotionEffect.class);
        outbound.bind(CodecPlayOutSendResourcePack.class,
                MessagePlayOutSendResourcePack.class);
        outbound.bind(CodecPlayOutPlayerRespawn.class,
                MessagePlayOutPlayerRespawn.class);
        outbound.bind(CodecPlayOutEntityHeadLook.class,
                MessagePlayOutEntityHeadLook.class);
        outbound.bind(CodecPlayOutSelectAdvancementTree.class,
                MessagePlayOutSelectAdvancementTree.class);
        outbound.bind(CodecPlayOutWorldBorder.class,
                MessagePlayOutWorldBorder.Initialize.class,
                MessagePlayOutWorldBorder.UpdateCenter.class,
                MessagePlayOutWorldBorder.UpdateDiameter.class,
                MessagePlayOutWorldBorder.UpdateLerpedDiameter.class,
                MessagePlayOutWorldBorder.UpdateWarningDistance.class,
                MessagePlayOutWorldBorder.UpdateWarningTime.class);
        outbound.bind(CodecPlayOutSetCamera.class,
                MessagePlayOutSetCamera.class);
        outbound.bind(CodecPlayInOutHeldItemChange.class,
                MessagePlayInOutHeldItemChange.class);
        outbound.bind(CodecPlayOutScoreboardDisplayObjective.class,
                MessagePlayOutScoreboardDisplayObjective.class);
        outbound.bind(CodecPlayOutEntityMetadata.class,
                MessagePlayOutEntityMetadata.class);
        outbound.bind(); // TODO: Attach Entity
        outbound.bind(CodecPlayOutEntityVelocity.class,
                MessagePlayOutEntityVelocity.class);
        outbound.bind(CodecPlayOutEntityEquipment.class,
                MessagePlayOutEntityEquipment.class);
        outbound.bind(CodecPlayOutSetExperience.class,
                MessagePlayOutSetExperience.class);
        outbound.bind(CodecPlayOutPlayerHealthUpdate.class,
                MessagePlayOutPlayerHealthUpdate.class);
        outbound.bind(CodecPlayOutScoreboardObjective.class,
                MessagePlayOutScoreboardObjective.Create.class,
                MessagePlayOutScoreboardObjective.Update.class,
                MessagePlayOutScoreboardObjective.Remove.class);
        outbound.bind(CodecPlayOutSetEntityPassengers.class,
                MessagePlayOutSetEntityPassengers.class);
        outbound.bind(CodecPlayOutTeams.class,
                MessagePlayOutTeams.AddPlayers.class,
                MessagePlayOutTeams.Create.class,
                MessagePlayOutTeams.Update.class,
                MessagePlayOutTeams.Remove.class,
                MessagePlayOutTeams.RemovePlayers.class);
        outbound.bind(CodecPlayOutScoreboardScore.class,
                MessagePlayOutScoreboardScore.CreateOrUpdate.class,
                MessagePlayOutScoreboardScore.Remove.class);
        outbound.bind(CodecPlayOutPlayerSpawnPosition.class,
                MessagePlayOutPlayerSpawnPosition.class);
        outbound.bind(CodecPlayOutWorldTime.class,
                MessagePlayOutWorldTime.class);
        outbound.bind(CodecPlayOutTitle.class,
                MessagePlayOutTitle.Clear.class,
                MessagePlayOutTitle.Reset.class,
                MessagePlayOutTitle.SetSubtitle.class,
                MessagePlayOutTitle.SetActionbarTitle.class,
                MessagePlayOutTitle.SetTimes.class,
                MessagePlayOutTitle.SetTitle.class);
        outbound.bind(CodecPlayOutSoundEffect.class,
                MessagePlayOutSoundEffect.class);
        outbound.bind(CodecPlayOutTabListHeaderAndFooter.class,
                MessagePlayOutTabListHeaderAndFooter.class);
        outbound.bind(CodecPlayOutEntityCollectItem.class,
                MessagePlayOutEntityCollectItem.class);
        outbound.bind(CodecPlayOutEntityTeleport.class,
                MessagePlayOutEntityTeleport.class);
        outbound.bind(CodecPlayOutAdvancements.class,
                MessagePlayOutAdvancements.class);
        outbound.bind(); // TODO: Entity Properties
        outbound.bind(CodecPlayOutAddPotionEffect.class,
                MessagePlayOutAddPotionEffect.class);
    }
}
