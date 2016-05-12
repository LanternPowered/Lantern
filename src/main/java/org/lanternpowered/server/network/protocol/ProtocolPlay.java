/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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

import org.lanternpowered.server.network.message.CodecRegistration;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.MessageRegistry;
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecInOutPing;
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInChangeSign;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInChatMessage;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInClientSettings;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInClientStatus;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInOutCloseWindow;
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
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInResourcePackStatus;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInSpectate;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInTabComplete;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInTeleportConfirm;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInUseEntity;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutBlockChange;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutBossBar;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutChangeGameState;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutChatMessage;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutChunkData;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutDestroyEntities;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityCollectItem;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityHeadLook;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityLook;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityLookAndRelativeMove;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityMetadata;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityRelativeMove;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityStatus;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityTeleport;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityVelocity;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutMultiBlockChange;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutOpenSign;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerHealthUpdate;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerJoinGame;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerPositionAndLook;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerRespawn;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerSpawnPosition;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutScoreboardDisplayObjective;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutScoreboardObjective;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutScoreboardScore;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSetCamera;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSetCooldown;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSetDifficulty;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSetExperience;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSoundEffect;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSpawnExperienceOrb;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSpawnMob;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSpawnObject;
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
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutUpdateTileEntity;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutWindowSetSlot;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutWorldBorder;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutWorldTime;
import org.lanternpowered.server.network.vanilla.message.handler.connection.HandlerInPing;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInAllPlayerMovement;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInChangeSign;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInChannelPayload;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInChatMessage;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInClientSettings;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInPlayerDigging;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInResourcePackStatus;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInTabComplete;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInUnregisterChannels;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutTheEnd;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutSetGameMode;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutWorldSky;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageInOutPing;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInEditBook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInEditCommandBlock;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeItemName;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeOffer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeSign;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChatMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClientSettings;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInDropHeldItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInFinishUsingItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInLeaveBed;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOpenInventory;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutBrand;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutCloseWindow;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutHeldItemChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutUnregisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPerformRespawn;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerAbilities;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerBlockPlacement;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerDigging;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerMovement;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerMovementAndLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerOnGroundState;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSneak;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSprint;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSwingArm;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerUseItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerVehicleJump;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerVehicleMovement;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInRequestStatistics;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInResourcePackStatus;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInSignBook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInSpectate;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInSwapHandItems;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInTabComplete;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInTeleportConfirm;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInUseEntity;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBossBar;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChatMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChunkData;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDestroyEntities;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityCollectItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityHeadLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityLookAndRelativeMove;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityMetadata;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityRelativeMove;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityTeleport;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityVelocity;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutMultiBlockChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutNamedSoundEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenBook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnParticle;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutStopSound;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTheEnd;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenSign;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerHealthUpdate;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerJoinGame;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerPositionAndLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerRespawn;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerSpawnPosition;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardDisplayObjective;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardObjective;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardScore;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetCamera;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetCooldown;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetDifficulty;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetExperience;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetGameMode;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetOpLevel;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetReducedDebug;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetWindowSlot;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSoundEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnExperienceOrb;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnMob;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnObject;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnPlayer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnThunderbolt;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutStatistics;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabComplete;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListHeaderAndFooter;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTeams;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTitle;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutUnloadChunk;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutUpdateTileEntity;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldBorder;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldSky;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldTime;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayOutChangeGameState;

public final class ProtocolPlay extends ProtocolBase {

    public ProtocolPlay() {
        MessageRegistry inbound = this.inbound();
        MessageRegistry outbound = this.outbound();

        // Register the processors
        outbound.bindProcessor(MessagePlayOutTheEnd.class, new ProcessorPlayOutTheEnd());
        outbound.bindProcessor(MessagePlayOutParticleEffect.class, new ProcessorPlayOutParticleEffect());
        outbound.bindProcessor(MessagePlayOutSetGameMode.class, new ProcessorPlayOutSetGameMode());
        outbound.bindProcessor(MessagePlayOutWorldSky.class, new ProcessorPlayOutWorldSky());
        outbound.bindProcessor(MessagePlayOutTabListEntries.class, new ProcessorPlayOutTabListEntries());

        HandlerPlayInAllPlayerMovement playerMovementHandler = new HandlerPlayInAllPlayerMovement();

        // Register the codecs and handlers of the default messages
        inbound.bind(0x00, CodecPlayInTeleportConfirm.class, MessagePlayInTeleportConfirm.class); // TODO: Handler
        inbound.bind(0x01, CodecPlayInTabComplete.class, MessagePlayInTabComplete.class)
                .bindHandler(new HandlerPlayInTabComplete());
        inbound.bind(0x02, CodecPlayInChatMessage.class, MessagePlayInChatMessage.class)
                .bindHandler(new HandlerPlayInChatMessage());
        inbound.bind(0x03, CodecPlayInClientStatus.class);
        inbound.bind(0x04, CodecPlayInClientSettings.class, MessagePlayInClientSettings.class)
                .bindHandler(new HandlerPlayInClientSettings());
        // ...
        inbound.bind(0x08, CodecPlayInOutCloseWindow.class, MessagePlayInOutCloseWindow.class); // TODO: Handler
        inbound.bind(0x09, CodecPlayInOutCustomPayload.class);
        inbound.bind(0x0a, CodecPlayInUseEntity.class);
        inbound.bind(0x0b, CodecInOutPing.class, MessageInOutPing.class)
                .bindHandler(new HandlerInPing());
        inbound.bind(0x0c, CodecPlayInPlayerMovement.class, MessagePlayInPlayerMovement.class)
                .bindHandler(playerMovementHandler.new HandlerPlayInPlayerMovement());
        inbound.bind(0x0d, CodecPlayInPlayerMovementAndLook.class, MessagePlayInPlayerMovementAndLook.class)
                .bindHandler(playerMovementHandler.new HandlerPlayInPlayerMovementAndLook());
        inbound.bind(0x0e, CodecPlayInPlayerLook.class, MessagePlayInPlayerLook.class)
                .bindHandler(playerMovementHandler.new HandlerPlayInPlayerLook());
        inbound.bind(0x0f, CodecPlayInPlayerOnGroundState.class, MessagePlayInPlayerOnGroundState.class);
        // ...
        inbound.bind(0x12, CodecPlayInPlayerAbilities.class, MessagePlayInPlayerAbilities.class); // TODO: Handler
        inbound.bind(0x13, CodecPlayInPlayerDigging.class);
        inbound.bind(0x14, CodecPlayInPlayerAction.class);
        inbound.bind(0x15, CodecPlayInPlayerVehicleControls.class);
        inbound.bind(0x16, CodecPlayInResourcePackStatus.class, MessagePlayInResourcePackStatus.class)
                .bindHandler(new HandlerPlayInResourcePackStatus());
        inbound.bind(0x17, CodecPlayInOutHeldItemChange.class, MessagePlayInOutHeldItemChange.class);
        // 0x18
        inbound.bind(0x19, CodecPlayInChangeSign.class, MessagePlayInChangeSign.class)
                .bindHandler(new HandlerPlayInChangeSign());
        inbound.bind(0x1a, CodecPlayInPlayerSwingArm.class, MessagePlayInPlayerSwingArm.class); // TODO: Handler
        inbound.bind(0x1b, CodecPlayInSpectate.class, MessagePlayInSpectate.class); // TODO: Handler
        inbound.bind(0x1c, CodecPlayInPlayerBlockPlacement.class, MessagePlayInPlayerBlockPlacement.class); // TODO: Handler
        inbound.bind(0x1d, CodecPlayInPlayerUseItem.class, MessagePlayInPlayerUseItem.class); // TODO: Handler

        // Provided by CodecPlayInOutCustomPayload
        inbound.bind(MessagePlayInOutBrand.class); // TODO: Handler
        inbound.bind(MessagePlayInChangeItemName.class); // TODO: Handler
        inbound.bind(MessagePlayInChangeOffer.class); // TODO: Handler
        inbound.bind(MessagePlayInEditCommandBlock.Block.class); // TODO: Handler
        inbound.bind(MessagePlayInEditCommandBlock.AdvancedBlock.class); // TODO: Handler
        inbound.bind(MessagePlayInEditCommandBlock.Entity.class); // TODO: Handler
        inbound.bind(MessagePlayInEditBook.class); // TODO: Handler
        inbound.bind(MessagePlayInSignBook.class); // TODO: Handler
        inbound.bind(MessagePlayInOutChannelPayload.class).bindHandler(new HandlerPlayInChannelPayload());
        inbound.bind(MessagePlayInOutRegisterChannels.class).bindHandler(new HandlerPlayInRegisterChannels());
        inbound.bind(MessagePlayInOutUnregisterChannels.class).bindHandler(new HandlerPlayInUnregisterChannels());
        // Provided by CodecPlayInUseEntity
        inbound.bind(MessagePlayInUseEntity.Attack.class); // TODO: Handler
        inbound.bind(MessagePlayInUseEntity.Interact.class); // TODO: Handler
        // Provided by CodecPlayInPlayerDigging
        inbound.bind(MessagePlayInPlayerDigging.class).bindHandler(new HandlerPlayInPlayerDigging());
        inbound.bind(MessagePlayInDropHeldItem.class); // TODO: Handler
        inbound.bind(MessagePlayInFinishUsingItem.class); // TODO: Handler
        inbound.bind(MessagePlayInSwapHandItems.class); // TODO: Handler
        // Provided by CodecPlayInClientStatus
        inbound.bind(MessagePlayInOpenInventory.class); // TODO: Handler
        inbound.bind(MessagePlayInPerformRespawn.class); // TODO: Handler
        inbound.bind(MessagePlayInRequestStatistics.class); // TODO: Handler
        // Provided by CodecPlayInPlayerAction
        inbound.bind(MessagePlayInLeaveBed.class);// TODO: Handler
        // Provided by CodecPlayInPlayerVehicleControls or CodecPlayInPlayerAction
        inbound.bind(MessagePlayInPlayerSneak.class); // TODO: Handler
        inbound.bind(MessagePlayInPlayerSprint.class); // TODO: Handler
        inbound.bind(MessagePlayInPlayerVehicleJump.class); // TODO: Handler
        // Provided by CodecPlayInPlayerVehicleControls
        inbound.bind(MessagePlayInPlayerVehicleMovement.class); // TODO: Handler

        outbound.bind(0x00, CodecPlayOutSpawnObject.class, MessagePlayOutSpawnObject.class);
        outbound.bind(0x01, CodecPlayOutSpawnExperienceOrb.class, MessagePlayOutSpawnExperienceOrb.class);
        outbound.bind(0x02, CodecPlayOutSpawnThunderbolt.class, MessagePlayOutSpawnThunderbolt.class);
        outbound.bind(0x03, CodecPlayOutSpawnMob.class, MessagePlayOutSpawnMob.class);
        // 0x04
        outbound.bind(0x05, CodecPlayOutSpawnPlayer.class, MessagePlayOutSpawnPlayer.class);
        // 0x06
        outbound.bind(0x07, CodecPlayOutStatistics.class, MessagePlayOutStatistics.class);
        // 0x08
        outbound.bind(0x09, CodecPlayOutUpdateTileEntity.class, MessagePlayOutUpdateTileEntity.class);
        // ...
        outbound.bind(0x0b, CodecPlayOutBlockChange.class, MessagePlayOutBlockChange.class);
        CodecRegistration<MessagePlayOutBossBar, CodecPlayOutBossBar> codecPlayOutBossBar = outbound.bind(0x0c, CodecPlayOutBossBar.class);
        codecPlayOutBossBar.bind(MessagePlayOutBossBar.Add.class);
        codecPlayOutBossBar.bind(MessagePlayOutBossBar.Remove.class);
        codecPlayOutBossBar.bind(MessagePlayOutBossBar.UpdateHealth.class);
        codecPlayOutBossBar.bind(MessagePlayOutBossBar.UpdateStyle.class);
        codecPlayOutBossBar.bind(MessagePlayOutBossBar.UpdateTitle.class);
        codecPlayOutBossBar.bind(MessagePlayOutBossBar.UpdateMisc.class);
        outbound.bind(0x0d, CodecPlayOutSetDifficulty.class, MessagePlayOutSetDifficulty.class);
        outbound.bind(0x0e, CodecPlayOutTabComplete.class, MessagePlayOutTabComplete.class);
        outbound.bind(0x0f, CodecPlayOutChatMessage.class, MessagePlayOutChatMessage.class);
        outbound.bind(0x10, CodecPlayOutMultiBlockChange.class, MessagePlayOutMultiBlockChange.class);
        // 0x11
        outbound.bind(0x12, CodecPlayInOutCloseWindow.class, MessagePlayInOutCloseWindow.class);
        // ...
        outbound.bind(0x16, CodecPlayOutWindowSetSlot.class, MessagePlayOutSetWindowSlot.class);
        outbound.bind(0x17, CodecPlayOutSetCooldown.class, MessagePlayOutSetCooldown.class);
        CodecRegistration<Message, CodecPlayInOutCustomPayload> codecPlayInOutCustomPayload = outbound.bind(
                0x18, CodecPlayInOutCustomPayload.class);
        codecPlayInOutCustomPayload.bind(MessagePlayInOutChannelPayload.class);
        codecPlayInOutCustomPayload.bind(MessagePlayOutOpenBook.class);
        codecPlayInOutCustomPayload.bind(MessagePlayInOutBrand.class);
        codecPlayInOutCustomPayload.bind(MessagePlayOutStopSound.class);
        outbound.bind(0x19, CodecPlayOutSoundEffect.class, MessagePlayOutNamedSoundEffect.class);
        outbound.bind(0x1a, CodecOutDisconnect.class, MessageOutDisconnect.class);
        CodecRegistration<Message, CodecPlayOutEntityStatus> codecPlayOutEntityStatus = outbound.bind(0x1b, CodecPlayOutEntityStatus.class);
        codecPlayOutEntityStatus.bind(MessagePlayOutSetOpLevel.class);
        codecPlayOutEntityStatus.bind(MessagePlayOutSetReducedDebug.class);
        // 0x1c
        outbound.bind(0x1d, CodecPlayOutUnloadChunk.class, MessagePlayOutUnloadChunk.class);
        outbound.bind(0x1e, CodecPlayOutChangeGameState.class, MessagePlayOutChangeGameState.class);
        outbound.bind(0x1f, CodecInOutPing.class, MessageInOutPing.class);
        outbound.bind(0x20, CodecPlayOutChunkData.class, MessagePlayOutChunkData.class);
        // 0x21
        outbound.bind(0x22, CodecPlayOutSpawnParticle.class, MessagePlayOutSpawnParticle.class);
        outbound.bind(0x23, CodecPlayOutPlayerJoinGame.class, MessagePlayOutPlayerJoinGame.class);
        // 0x24
        outbound.bind(0x25, CodecPlayOutEntityRelativeMove.class, MessagePlayOutEntityRelativeMove.class);
        outbound.bind(0x26, CodecPlayOutEntityLookAndRelativeMove.class, MessagePlayOutEntityLookAndRelativeMove.class);
        outbound.bind(0x27, CodecPlayOutEntityLook.class, MessagePlayOutEntityLook.class);
        // ...
        outbound.bind(0x2a, CodecPlayOutOpenSign.class, MessagePlayOutOpenSign.class);
        // ...
        outbound.bind(0x2d, CodecPlayOutTabListEntries.class, MessagePlayOutTabListEntries.class);
        outbound.bind(0x2e, CodecPlayOutPlayerPositionAndLook.class, MessagePlayOutPlayerPositionAndLook.class);
        // ...
        outbound.bind(0x30, CodecPlayOutDestroyEntities.class, MessagePlayOutDestroyEntities.class);
        // ...
        outbound.bind(0x33, CodecPlayOutPlayerRespawn.class, MessagePlayOutPlayerRespawn.class);
        outbound.bind(0x34, CodecPlayOutEntityHeadLook.class, MessagePlayOutEntityHeadLook.class);
        CodecRegistration<MessagePlayOutWorldBorder, CodecPlayOutWorldBorder> codecPlayOutWorldBorder =
                outbound.bind(0x35, CodecPlayOutWorldBorder.class);
        codecPlayOutWorldBorder.bind(MessagePlayOutWorldBorder.Initialize.class);
        codecPlayOutWorldBorder.bind(MessagePlayOutWorldBorder.UpdateCenter.class);
        codecPlayOutWorldBorder.bind(MessagePlayOutWorldBorder.UpdateDiameter.class);
        codecPlayOutWorldBorder.bind(MessagePlayOutWorldBorder.UpdateLerpedDiameter.class);
        codecPlayOutWorldBorder.bind(MessagePlayOutWorldBorder.UpdateWarningDistance.class);
        codecPlayOutWorldBorder.bind(MessagePlayOutWorldBorder.UpdateWarningTime.class);
        outbound.bind(0x36, CodecPlayOutSetCamera.class, MessagePlayOutSetCamera.class);
        outbound.bind(0x37, CodecPlayInOutHeldItemChange.class, MessagePlayInOutHeldItemChange.class);
        outbound.bind(0x38, CodecPlayOutScoreboardDisplayObjective.class, MessagePlayOutScoreboardDisplayObjective.class);
        outbound.bind(0x39, CodecPlayOutEntityMetadata.class, MessagePlayOutEntityMetadata.class);
        // 0x3a
        outbound.bind(0x3b, CodecPlayOutEntityVelocity.class, MessagePlayOutEntityVelocity.class);
        // 0x3c
        outbound.bind(0x3d, CodecPlayOutSetExperience.class, MessagePlayOutSetExperience.class);
        outbound.bind(0x3e, CodecPlayOutPlayerHealthUpdate.class, MessagePlayOutPlayerHealthUpdate.class);
        CodecRegistration<MessagePlayOutScoreboardObjective, CodecPlayOutScoreboardObjective> codecPlayOutScoreboardObjective = outbound.bind(
                0x3f, CodecPlayOutScoreboardObjective.class);
        codecPlayOutScoreboardObjective.bind(MessagePlayOutScoreboardObjective.Create.class);
        codecPlayOutScoreboardObjective.bind(MessagePlayOutScoreboardObjective.Update.class);
        codecPlayOutScoreboardObjective.bind(MessagePlayOutScoreboardObjective.Remove.class);
        // ...
        CodecRegistration<MessagePlayOutTeams, CodecPlayOutTeams> codecPlayOutTeams = outbound.bind(
                0x41, CodecPlayOutTeams.class);
        codecPlayOutTeams.bind(MessagePlayOutTeams.AddPlayers.class);
        codecPlayOutTeams.bind(MessagePlayOutTeams.Create.class);
        codecPlayOutTeams.bind(MessagePlayOutTeams.Update.class);
        codecPlayOutTeams.bind(MessagePlayOutTeams.Remove.class);
        codecPlayOutTeams.bind(MessagePlayOutTeams.RemovePlayers.class);
        CodecRegistration<MessagePlayOutScoreboardScore, CodecPlayOutScoreboardScore> codecPlayOutScoreboardScore = outbound.bind(
                0x42, CodecPlayOutScoreboardScore.class);
        codecPlayOutScoreboardScore.bind(MessagePlayOutScoreboardScore.CreateOrUpdate.class);
        codecPlayOutScoreboardScore.bind(MessagePlayOutScoreboardScore.Remove.class);
        outbound.bind(0x43, CodecPlayOutPlayerSpawnPosition.class, MessagePlayOutPlayerSpawnPosition.class);
        outbound.bind(0x44, CodecPlayOutWorldTime.class, MessagePlayOutWorldTime.class);
        CodecRegistration<MessagePlayOutTitle, CodecPlayOutTitle> codecPlayOutTitle = outbound.bind(0x45, CodecPlayOutTitle.class);
        codecPlayOutTitle.bind(MessagePlayOutTitle.Clear.class);
        codecPlayOutTitle.bind(MessagePlayOutTitle.Reset.class);
        codecPlayOutTitle.bind(MessagePlayOutTitle.SetSubtitle.class);
        codecPlayOutTitle.bind(MessagePlayOutTitle.SetTimes.class);
        codecPlayOutTitle.bind(MessagePlayOutTitle.SetTitle.class);
        outbound.bind(0x46, CodecPlayOutSoundEffect.class, MessagePlayOutSoundEffect.class);
        outbound.bind(0x47, CodecPlayOutTabListHeaderAndFooter.class, MessagePlayOutTabListHeaderAndFooter.class);
        outbound.bind(0x48, CodecPlayOutEntityCollectItem.class, MessagePlayOutEntityCollectItem.class);
        outbound.bind(0x49, CodecPlayOutEntityTeleport.class, MessagePlayOutEntityTeleport.class);
        // ...
    }
}
