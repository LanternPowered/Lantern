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
package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.inventory.PlayerContainerSession;
import org.lanternpowered.server.network.packet.CodecRegistration;
import org.lanternpowered.server.network.packet.Packet;
import org.lanternpowered.server.network.packet.MessageRegistry;
import org.lanternpowered.server.network.vanilla.packet.codec.KeepAliveCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.DisconnectCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientAcceptBeaconEffectsCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ChangeAdvancementTreeCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientItemRenameCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ChangeTradeOfferCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInChangeSign;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientSendChatCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInClickRecipe;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClickWindowCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientSettingsCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInClientStatus;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientRecipeBookCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInCreativeWindowAction;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInDataRequestBlock;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInDataRequestEntity;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInEditCommandBlockBlock;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInEditCommandBlockEntity;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInEnchantItem;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientLocksDifficultyCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInModifyBook;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CloseWindowCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInOutConfirmWindowTransaction;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ChannelPayloadCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInOutHeldItemChange;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInPickItem;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInPlayerAbilities;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInPlayerAction;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientBlockPlacementCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInPlayerDigging;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInPlayerLook;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInPlayerMovement;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInPlayerMovementAndLook;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInPlayerOnGroundState;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInPlayerSwingArm;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInPlayerUseItem;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInPlayerVehicleControls;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInPlayerVehicleMovement;
import org.lanternpowered.server.network.vanilla.packet.codec.play.GenerateJigsawStructureCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ResourcePackStatusCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientSetDifficultyCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInSpectate;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInTabComplete;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInTeleportConfirm;
import org.lanternpowered.server.network.vanilla.packet.codec.play.UpdateJigsawBlockMessageCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInUseEntity;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutAddPotionEffect;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutAdvancements;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutBlockAction;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutBlockBreakAnimation;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutBlockChange;
import org.lanternpowered.server.network.vanilla.packet.codec.play.BossBarCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ChangeGameStateCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutChatMessage;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ChunkCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutDataResponse;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutDefineCommands;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutDefineRecipes;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutDestroyEntities;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutDisplayRecipe;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutEffect;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutEntityAnimation;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutEntityCollectItem;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutEntityEquipment;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutEntityHeadLook;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutEntityLook;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutEntityLookAndRelativeMove;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutEntityMetadata;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutEntityRelativeMove;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntitySoundEffectCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutEntityStatus;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutEntityTeleport;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutEntityVelocity;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutFaceAt;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutMultiBlockChange;
import org.lanternpowered.server.network.vanilla.packet.codec.play.NamedSoundEffectCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.OpenBookCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.OpenHorseWindowCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutOpenSign;
import org.lanternpowered.server.network.vanilla.packet.codec.play.OpenWindowCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutPlayerAbilities;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutPlayerHealthUpdate;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerJoinCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutPlayerPositionAndLook;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerRespawnCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutPlayerSpawnPosition;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutRemovePotionEffect;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutScoreboardDisplayObjective;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutScoreboardObjective;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutScoreboardScore;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutSelectAdvancementTree;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetResourcePackCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetCameraCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetCooldownCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetDifficultyCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetEntityPassengersCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetExperienceCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutSetWindowSlot;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SoundEffectCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutSpawnExperienceOrb;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnMobCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnObjectCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnPaintingCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnParticleCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnPlayerCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnThunderboltCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutStatistics;
import org.lanternpowered.server.network.vanilla.packet.codec.play.StopSoundsCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutTabComplete;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutTabListHeaderAndFooter;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutTags;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutTeams;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutTitle;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetWindowTradeOffersCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutUnloadChunk;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutUnlockRecipes;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutUpdateBlockEntity;
import org.lanternpowered.server.network.vanilla.packet.codec.play.UpdateLightCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.UpdateViewDistanceCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.UpdateViewPositionCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetWindowItemsCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetWindowPropertyCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutWorldBorder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutWorldTime;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientLockDifficultyHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSetDifficultyHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientAdvancementTreeHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInChangeSign;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInChannelPayload;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInChatMessage;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSettingsHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInContainerSessionForwarding;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientRecipeBookStatesHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInEditBook;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInFinishUsingItem;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInPlayerAbilities;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientBlockPlacementHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInPlayerDigging;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInPlayerLook;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInPlayerMovement;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInPlayerMovementAndLook;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInPlayerMovementInput;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInPlayerOnGroundState;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInPlayerSneak;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInPlayerSprint;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInPlayerSwingArm;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInPlayerUseItem;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInPlayerVehicleMovement;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInRegisterChannels;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInRequestStatistics;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ResourcePackStatusHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInSignBook;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInStartElytraFlying;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInSwapHandItems;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInTabComplete;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInUnregisterChannels;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInUseEntityAttack;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInUseEntityInteract;
import org.lanternpowered.server.network.vanilla.packet.processor.play.ProcessorPlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.packet.processor.play.SetGameModeProcessor;
import org.lanternpowered.server.network.vanilla.packet.processor.play.ProcessorPlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.packet.processor.play.TheEndMessageProcessor;
import org.lanternpowered.server.network.vanilla.packet.processor.play.UpdateWorldSkyProcessor;
import org.lanternpowered.server.network.vanilla.packet.type.KeepAlivePacket;
import org.lanternpowered.server.network.vanilla.packet.type.DisconnectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.GenerateJigsawStructurePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientAcceptBeaconEffectsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ChangeAdvancementTreePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientItemRenamePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ChangeTradeOfferPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInChangeSign;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSendChatPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInClickRecipe;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClickWindowPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSettingsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInCreativeWindowAction;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInDataRequest;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInDisplayedRecipe;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInDropHeldItem;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInEditCommandBlock;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInEnchantItem;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInLeaveBed;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientLockDifficultyPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInModifyBook;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutBrand;
import org.lanternpowered.server.network.vanilla.packet.type.play.ChannelPayloadPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.CloseWindowPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutConfirmWindowTransaction;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutFinishUsingItem;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutHeldItemChange;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutUnregisterChannels;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPerformRespawn;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPickItem;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerAbilities;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientBlockPlacementPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerDigging;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerLook;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerMovement;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerMovementAndLook;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerMovementInput;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerOnGroundState;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerSneak;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerSprint;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerSwingArm;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerUseItem;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerVehicleJump;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerVehicleMovement;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientRecipeBookStatesPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInRequestStatistics;
import org.lanternpowered.server.network.vanilla.packet.type.play.ResourcePackStatusPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSetDifficultyPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInSpectate;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInStartElytraFlying;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInSwapHandItems;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInTabComplete;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInTeleportConfirm;
import org.lanternpowered.server.network.vanilla.packet.type.play.NamedSoundEffectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.UpdateJigsawBlockPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInUseEntity;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutAddPotionEffect;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutAdvancements;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutBlockAction;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutBlockBreakAnimation;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutBlockChange;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutBlockEntity;
import org.lanternpowered.server.network.vanilla.packet.type.play.BossBarPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.MessagePlayOutChatPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ChunkPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutDataResponse;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutDefineCommands;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutDefineRecipes;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutDestroyEntities;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutDisplayRecipe;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEffect;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityAnimation;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityCollectItem;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityEquipment;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityHeadLook;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityLook;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityLookAndRelativeMove;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityMetadata;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityRelativeMove;
import org.lanternpowered.server.network.vanilla.packet.type.play.EntitySoundEffectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityStatus;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityTeleport;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityVelocity;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutFaceAt;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutMultiBlockChange;
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenBookPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenHorseWindowPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutOpenSign;
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutPlayerAbilities;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutPlayerHealthUpdate;
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerJoinPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutPlayerPositionAndLook;
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerRespawnPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutPlayerSpawnPosition;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetMusicDiscPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutRemovePotionEffect;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutScoreboardDisplayObjective;
import org.lanternpowered.server.network.vanilla.packet.type.play.ScoreboardObjectivePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ScoreboardScorePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutSelectAdvancementTree;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetResourcePackPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetCameraPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetCooldownPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetDifficultyPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetEntityPassengersPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetExperiencePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetGameModePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutSetOpLevel;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetReducedDebugPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutSetWindowSlot;
import org.lanternpowered.server.network.vanilla.packet.type.play.SoundEffectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutSpawnExperienceOrb;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnMobPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnObjectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnPaintingPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnParticlePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnPlayerPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnThunderboltPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutStatistics;
import org.lanternpowered.server.network.vanilla.packet.type.play.StopSoundsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutTabComplete;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutTabListHeaderAndFooter;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutTags;
import org.lanternpowered.server.network.vanilla.packet.type.play.TeamPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.TheEndPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutTitle;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowTradeOffersPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutUnloadChunk;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutUnlockRecipes;
import org.lanternpowered.server.network.vanilla.packet.type.play.UpdateLightPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.UpdateViewDistancePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.UpdateViewPositionPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowItemsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowPropertyPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutWorldBorder;
import org.lanternpowered.server.network.vanilla.packet.type.play.UpdateWorldSkyPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutWorldTime;
import org.lanternpowered.server.network.vanilla.packet.type.play.internal.ChangeGameStatePacket;

@SuppressWarnings("ALL")
final class ProtocolPlay extends ProtocolBase {

    ProtocolPlay() {
        final MessageRegistry inbound = inbound();
        final MessageRegistry outbound = outbound();

        // Register the processors
        outbound.bindProcessor(TheEndPacket.class, new TheEndMessageProcessor());
        outbound.bindProcessor(PacketPlayOutParticleEffect.class, new ProcessorPlayOutParticleEffect());
        outbound.bindProcessor(SetGameModePacket.class, new SetGameModeProcessor());
        outbound.bindProcessor(UpdateWorldSkyPacket.class, new UpdateWorldSkyProcessor());
        outbound.bindProcessor(PacketPlayOutTabListEntries.class, new ProcessorPlayOutTabListEntries());

        // Register the codecs and handlers of the default messages
        inbound.bind(CodecPlayInTeleportConfirm.class, PacketPlayInTeleportConfirm.class); // TODO: Handler
        inbound.bind(CodecPlayInDataRequestBlock.class, PacketPlayInDataRequest.Block.class); // TODO: Handler
        inbound.bind(ClientSetDifficultyCodec.class, ClientSetDifficultyPacket.class)
                .bindHandler(new ClientSetDifficultyHandler());
        inbound.bind(ClientSendChatCodec.class, ClientSendChatPacket.class)
                .bindHandler(new HandlerPlayInChatMessage());
        inbound.bind(CodecPlayInClientStatus.class);
        inbound.bind(ClientSettingsCodec.class, ClientSettingsPacket.class)
                .bindHandler(new ClientSettingsHandler());
        inbound.bind(CodecPlayInTabComplete.class, PacketPlayInTabComplete.class)
                .bindHandler(new HandlerPlayInTabComplete());
        inbound.bind(CodecPlayInOutConfirmWindowTransaction.class, PacketPlayInOutConfirmWindowTransaction.class); // TODO: Handler
        inbound.bind(CodecPlayInEnchantItem.class, PacketPlayInEnchantItem.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleEnchantItem));
        inbound.bind(ClickWindowCodec.class, ClickWindowPacket.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleWindowClick));
        inbound.bind(CloseWindowCodec.class, CloseWindowPacket.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleWindowClose));
        inbound.bind(ChannelPayloadCodec.class);
        inbound.bind(CodecPlayInModifyBook.class);
        inbound.bind(CodecPlayInDataRequestEntity.class, PacketPlayInDataRequest.Entity.class); // TODO: Handler
        inbound.bind(CodecPlayInUseEntity.class);
        inbound.bind(GenerateJigsawStructureCodec.class, GenerateJigsawStructurePacket.class);
        inbound.bind(KeepAliveCodec.class, KeepAlivePacket.class);
        inbound.bind(ClientLocksDifficultyCodec.class, ClientLockDifficultyPacket.class)
                .bindHandler(new ClientLockDifficultyHandler());
        inbound.bind(CodecPlayInPlayerMovement.class, PacketPlayInPlayerMovement.class)
                .bindHandler(new HandlerPlayInPlayerMovement());
        inbound.bind(CodecPlayInPlayerMovementAndLook.class, PacketPlayInPlayerMovementAndLook.class)
                .bindHandler(new HandlerPlayInPlayerMovementAndLook());
        inbound.bind(CodecPlayInPlayerLook.class, PacketPlayInPlayerLook.class)
                .bindHandler(new HandlerPlayInPlayerLook());
        inbound.bind(CodecPlayInPlayerOnGroundState.class, PacketPlayInPlayerOnGroundState.class)
                .bindHandler(new HandlerPlayInPlayerOnGroundState());
        inbound.bind(CodecPlayInPlayerVehicleMovement.class, PacketPlayInPlayerVehicleMovement.class)
                .bindHandler(new HandlerPlayInPlayerVehicleMovement());
        inbound.bind(); // TODO: Steer Boat
        inbound.bind(CodecPlayInPickItem.class, PacketPlayInPickItem.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handlePickItem));
        inbound.bind(CodecPlayInClickRecipe.class, PacketPlayInClickRecipe.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleRecipeClick));
        inbound.bind(CodecPlayInPlayerAbilities.class, PacketPlayInPlayerAbilities.class)
                .bindHandler(new HandlerPlayInPlayerAbilities());
        inbound.bind(CodecPlayInPlayerDigging.class);
        inbound.bind(CodecPlayInPlayerAction.class);
        inbound.bind(CodecPlayInPlayerVehicleControls.class);
        inbound.bind(ClientRecipeBookCodec.class);
        inbound.bind(ClientItemRenameCodec.class, ClientItemRenamePacket.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleItemRename));
        inbound.bind(ResourcePackStatusCodec.class, ResourcePackStatusPacket.class)
                .bindHandler(new ResourcePackStatusHandler());
        inbound.bind(ChangeAdvancementTreeCodec.class);
        inbound.bind(ChangeTradeOfferCodec.class, ChangeTradeOfferPacket.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleOfferChange));
        inbound.bind(ClientAcceptBeaconEffectsCodec.class, ClientAcceptBeaconEffectsPacket.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleAcceptBeaconEffects));
        inbound.bind(CodecPlayInOutHeldItemChange.class, PacketPlayInOutHeldItemChange.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleHeldItemChange));
        inbound.bind(CodecPlayInEditCommandBlockBlock.class, PacketPlayInEditCommandBlock.Block.class);
        inbound.bind(CodecPlayInEditCommandBlockEntity.class, PacketPlayInEditCommandBlock.Entity.class);
        inbound.bind(CodecPlayInCreativeWindowAction.class, PacketPlayInCreativeWindowAction.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleWindowCreativeClick));
        inbound.bind(UpdateJigsawBlockMessageCodec.class, UpdateJigsawBlockPacket.class); // TODO: Handler
        inbound.bind(); // TODO: Structure blocks
        inbound.bind(CodecPlayInChangeSign.class, PacketPlayInChangeSign.class)
                .bindHandler(new HandlerPlayInChangeSign());
        inbound.bind(CodecPlayInPlayerSwingArm.class, PacketPlayInPlayerSwingArm.class)
                .bindHandler(new HandlerPlayInPlayerSwingArm());
        inbound.bind(CodecPlayInSpectate.class, PacketPlayInSpectate.class); // TODO: Handler
        inbound.bind(ClientBlockPlacementCodec.class, ClientBlockPlacementPacket.class)
                .bindHandler(new ClientBlockPlacementHandler());
        inbound.bind(CodecPlayInPlayerUseItem.class, PacketPlayInPlayerUseItem.class)
                .bindHandler(new HandlerPlayInPlayerUseItem());

        // Provided by CodecPlayInOutCustomPayload
        inbound.bindMessage(PacketPlayInOutBrand.class); // TODO: Handler
        inbound.bindMessage(PacketPlayInModifyBook.Edit.class)
                .bindHandler(new HandlerPlayInEditBook());
        inbound.bindMessage(PacketPlayInModifyBook.Sign.class)
                .bindHandler(new HandlerPlayInSignBook());
        inbound.bindMessage(ChannelPayloadPacket.class)
                .bindHandler(new HandlerPlayInChannelPayload());
        inbound.bindMessage(PacketPlayInOutRegisterChannels.class)
                .bindHandler(new HandlerPlayInRegisterChannels());
        inbound.bindMessage(PacketPlayInOutUnregisterChannels.class)
                .bindHandler(new HandlerPlayInUnregisterChannels());
        // Provided by CodecPlayInUseEntity
        inbound.bindMessage(PacketPlayInUseEntity.Attack.class)
                .bindHandler(new HandlerPlayInUseEntityAttack());
        inbound.bindMessage(PacketPlayInUseEntity.Interact.class)
                .bindHandler(new HandlerPlayInUseEntityInteract());
        // Provided by CodecPlayInPlayerDigging
        inbound.bindMessage(PacketPlayInPlayerDigging.class)
                .bindHandler(new HandlerPlayInPlayerDigging());
        inbound.bindMessage(PacketPlayInDropHeldItem.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleItemDrop));
        inbound.bindMessage(PacketPlayInOutFinishUsingItem.class)
                .bindHandler(new HandlerPlayInFinishUsingItem());
        inbound.bindMessage(PacketPlayInSwapHandItems.class)
                .bindHandler(new HandlerPlayInSwapHandItems());
        inbound.bindMessage(PacketPlayInPerformRespawn.class)
                .bindHandler((context, message) -> context.getSession().getPlayer().handleRespawn());
        inbound.bindMessage(PacketPlayInRequestStatistics.class)
                .bindHandler(new HandlerPlayInRequestStatistics());
        // Provided by CodecPlayInPlayerAction
        inbound.bindMessage(PacketPlayInLeaveBed.class); // TODO: Handler
        inbound.bindMessage(PacketPlayInStartElytraFlying.class)
                .bindHandler(new HandlerPlayInStartElytraFlying());
        // Provided by CodecPlayInPlayerVehicleControls or CodecPlayInPlayerAction
        inbound.bindMessage(PacketPlayInPlayerSneak.class)
                .bindHandler(new HandlerPlayInPlayerSneak());
        inbound.bindMessage(PacketPlayInPlayerSprint.class)
                .bindHandler(new HandlerPlayInPlayerSprint());
        inbound.bindMessage(PacketPlayInPlayerVehicleJump.class); // TODO: Handler
        // Provided by CodecPlayInPlayerVehicleControls
        inbound.bindMessage(PacketPlayInPlayerMovementInput.class)
                .bindHandler(new HandlerPlayInPlayerMovementInput());
        // Provided by CodecPlayInCraftingBookData
        inbound.bindMessage(PacketPlayInDisplayedRecipe.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleDisplayedRecipe));
        inbound.bindMessage(ClientRecipeBookStatesPacket.class)
                .bindHandler(new ClientRecipeBookStatesHandler());
        // Provided by CodecPlayInAdvancementTab
        final ClientAdvancementTreeHandler handlerPlayInAdvancementTree = new ClientAdvancementTreeHandler();
        inbound.bindMessage(ChangeAdvancementTreePacket.Close.class)
                .bindHandler(handlerPlayInAdvancementTree);
        inbound.bindMessage(ChangeAdvancementTreePacket.Open.class)
                .bindHandler(handlerPlayInAdvancementTree);

        outbound.bind(SpawnObjectCodec.class, SpawnObjectPacket.class);
        outbound.bind(CodecPlayOutSpawnExperienceOrb.class, PacketPlayOutSpawnExperienceOrb.class);
        outbound.bind(SpawnThunderboltCodec.class, SpawnThunderboltPacket.class);
        outbound.bind(SpawnMobCodec.class, SpawnMobPacket.class);
        outbound.bind(SpawnPaintingCodec.class, SpawnPaintingPacket.class);
        outbound.bind(SpawnPlayerCodec.class, SpawnPlayerPacket.class);
        outbound.bind(CodecPlayOutEntityAnimation.class, PacketPlayOutEntityAnimation.class);
        outbound.bind(CodecPlayOutStatistics.class, PacketPlayOutStatistics.class);
        outbound.bind(CodecPlayOutBlockBreakAnimation.class, PacketPlayOutBlockBreakAnimation.class);
        outbound.bind(CodecPlayOutUpdateBlockEntity.class, PacketPlayOutBlockEntity.class);
        outbound.bind(CodecPlayOutBlockAction.class, PacketPlayOutBlockAction.class);
        outbound.bind(CodecPlayOutBlockChange.class, PacketPlayOutBlockChange.class);
        final CodecRegistration<BossBarPacket, BossBarCodec> codecPlayOutBossBar = outbound.bind(BossBarCodec.class);
        codecPlayOutBossBar.bind(BossBarPacket.Add.class);
        codecPlayOutBossBar.bind(BossBarPacket.Remove.class);
        codecPlayOutBossBar.bind(BossBarPacket.UpdatePercent.class);
        codecPlayOutBossBar.bind(BossBarPacket.UpdateStyle.class);
        codecPlayOutBossBar.bind(BossBarPacket.UpdateName.class);
        codecPlayOutBossBar.bind(BossBarPacket.UpdateFlags.class);
        outbound.bind(SetDifficultyCodec.class, SetDifficultyPacket.class);
        outbound.bind(CodecPlayOutChatMessage.class, MessagePlayOutChatPacket.class);
        outbound.bind(CodecPlayOutMultiBlockChange.class, PacketPlayOutMultiBlockChange.class);
        outbound.bind(CodecPlayOutTabComplete.class, PacketPlayOutTabComplete.class);
        outbound.bind(CodecPlayOutDefineCommands.class, PacketPlayOutDefineCommands.class);
        outbound.bind(CodecPlayInOutConfirmWindowTransaction.class, PacketPlayInOutConfirmWindowTransaction.class);
        outbound.bind(CloseWindowCodec.class, CloseWindowPacket.class);
        outbound.bind(SetWindowItemsCodec.class, SetWindowItemsPacket.class);
        outbound.bind(SetWindowPropertyCodec.class, SetWindowPropertyPacket.class);
        outbound.bind(CodecPlayOutSetWindowSlot.class, PacketPlayOutSetWindowSlot.class);
        outbound.bind(SetCooldownCodec.class, SetCooldownPacket.class);
        final CodecRegistration<Packet, ChannelPayloadCodec> codecPlayInOutCustomPayload = outbound.bind(
                ChannelPayloadCodec.class);
        codecPlayInOutCustomPayload.bind(ChannelPayloadPacket.class);
        codecPlayInOutCustomPayload.bind(PacketPlayInOutBrand.class);
        outbound.bind(NamedSoundEffectCodec.class, NamedSoundEffectPacket.class);
        outbound.bind(DisconnectCodec.class, DisconnectPacket.class);
        final CodecRegistration<Packet, CodecPlayOutEntityStatus> codecPlayOutEntityStatus = outbound.bind(CodecPlayOutEntityStatus.class);
        codecPlayOutEntityStatus.bind(PacketPlayOutEntityStatus.class);
        codecPlayOutEntityStatus.bind(PacketPlayOutSetOpLevel.class);
        codecPlayOutEntityStatus.bind(SetReducedDebugPacket.class);
        codecPlayOutEntityStatus.bind(PacketPlayInOutFinishUsingItem.class);
        outbound.bind(); // TODO: Explosion
        outbound.bind(CodecPlayOutUnloadChunk.class, PacketPlayOutUnloadChunk.class);
        outbound.bind(ChangeGameStateCodec.class, ChangeGameStatePacket.class);
        outbound.bind(OpenHorseWindowCodec.class, OpenHorseWindowPacket.class);
        outbound.bind(KeepAliveCodec.class, KeepAlivePacket.class);
        final CodecRegistration<ChunkPacket, ChunkCodec> codecPlayOutChunkData = outbound.bind(ChunkCodec.class);
        codecPlayOutChunkData.bind(ChunkPacket.Init.class);
        codecPlayOutChunkData.bind(ChunkPacket.Update.class);
        final CodecRegistration<Packet, CodecPlayOutEffect> codecPlayOutEntityEffect = outbound.bind(CodecPlayOutEffect.class);
        codecPlayOutEntityEffect.bind(PacketPlayOutEffect.class);
        codecPlayOutEntityEffect.bind(SetMusicDiscPacket.class);
        outbound.bind(SpawnParticleCodec.class, SpawnParticlePacket.class);
        outbound.bind(UpdateLightCodec.class, UpdateLightPacket.class);
        outbound.bind(PlayerJoinCodec.class, PlayerJoinPacket.class);
        outbound.bind(); // TODO: Map
        outbound.bind(SetWindowTradeOffersCodec.class, SetWindowTradeOffersPacket.class);
        outbound.bind(CodecPlayOutEntityRelativeMove.class, PacketPlayOutEntityRelativeMove.class);
        outbound.bind(CodecPlayOutEntityLookAndRelativeMove.class, PacketPlayOutEntityLookAndRelativeMove.class);
        outbound.bind(CodecPlayOutEntityLook.class, PacketPlayOutEntityLook.class);
        outbound.bind(); // Entity
        outbound.bind(); // TODO: Vehicle Move
        outbound.bind(CodecPlayOutOpenSign.class, PacketPlayOutOpenSign.class);
        outbound.bind(OpenWindowCodec.class, OpenWindowPacket.class);
        outbound.bind(OpenBookCodec.class, OpenBookPacket.class);
        outbound.bind(CodecPlayOutDisplayRecipe.class, PacketPlayOutDisplayRecipe.class);
        outbound.bind(CodecPlayOutPlayerAbilities.class, PacketPlayOutPlayerAbilities.class);
        outbound.bind(); // TODO: Combat Event
        outbound.bind(CodecPlayOutTabListEntries.class, PacketPlayOutTabListEntries.class);
        final CodecRegistration<PacketPlayOutFaceAt, CodecPlayOutFaceAt> codecPlayOutFaceAt =
                outbound.bind(CodecPlayOutFaceAt.class);
        codecPlayOutFaceAt.bind(PacketPlayOutFaceAt.Entity.class);
        codecPlayOutFaceAt.bind(PacketPlayOutFaceAt.Position.class);
        outbound.bind(CodecPlayOutPlayerPositionAndLook.class, PacketPlayOutPlayerPositionAndLook.class);
        final CodecRegistration<PacketPlayOutUnlockRecipes, CodecPlayOutUnlockRecipes> codecPlayOutUnlockRecipes =
                outbound.bind(CodecPlayOutUnlockRecipes.class);
        codecPlayOutUnlockRecipes.bind(PacketPlayOutUnlockRecipes.Add.class);
        codecPlayOutUnlockRecipes.bind(PacketPlayOutUnlockRecipes.Init.class);
        codecPlayOutUnlockRecipes.bind(PacketPlayOutUnlockRecipes.Remove.class);
        outbound.bind(CodecPlayOutDestroyEntities.class, PacketPlayOutDestroyEntities.class);
        outbound.bind(CodecPlayOutRemovePotionEffect.class, PacketPlayOutRemovePotionEffect.class);
        outbound.bind(SetResourcePackCodec.class, SetResourcePackPacket.class);
        outbound.bind(PlayerRespawnCodec.class, PlayerRespawnPacket.class);
        outbound.bind(CodecPlayOutEntityHeadLook.class, PacketPlayOutEntityHeadLook.class);
        outbound.bind(CodecPlayOutSelectAdvancementTree.class, PacketPlayOutSelectAdvancementTree.class);
        final CodecRegistration<PacketPlayOutWorldBorder, CodecPlayOutWorldBorder> codecPlayOutWorldBorder =
                outbound.bind(CodecPlayOutWorldBorder.class);
        codecPlayOutWorldBorder.bind(PacketPlayOutWorldBorder.Initialize.class);
        codecPlayOutWorldBorder.bind(PacketPlayOutWorldBorder.UpdateCenter.class);
        codecPlayOutWorldBorder.bind(PacketPlayOutWorldBorder.UpdateDiameter.class);
        codecPlayOutWorldBorder.bind(PacketPlayOutWorldBorder.UpdateLerpedDiameter.class);
        codecPlayOutWorldBorder.bind(PacketPlayOutWorldBorder.UpdateWarningDistance.class);
        codecPlayOutWorldBorder.bind(PacketPlayOutWorldBorder.UpdateWarningTime.class);
        outbound.bind(SetCameraCodec.class, SetCameraPacket.class);
        outbound.bind(CodecPlayInOutHeldItemChange.class, PacketPlayInOutHeldItemChange.class);
        outbound.bind(UpdateViewPositionCodec.class, UpdateViewPositionPacket.class);
        outbound.bind(UpdateViewDistanceCodec.class, UpdateViewDistancePacket.class);
        outbound.bind(CodecPlayOutPlayerSpawnPosition.class, PacketPlayOutPlayerSpawnPosition.class);
        outbound.bind(CodecPlayOutScoreboardDisplayObjective.class, PacketPlayOutScoreboardDisplayObjective.class);
        outbound.bind(CodecPlayOutEntityMetadata.class, PacketPlayOutEntityMetadata.class);
        outbound.bind(); // TODO: Attach Entity
        outbound.bind(CodecPlayOutEntityVelocity.class, PacketPlayOutEntityVelocity.class);
        outbound.bind(CodecPlayOutEntityEquipment.class, PacketPlayOutEntityEquipment.class);
        outbound.bind(SetExperienceCodec.class, SetExperiencePacket.class);
        outbound.bind(CodecPlayOutPlayerHealthUpdate.class, PacketPlayOutPlayerHealthUpdate.class);
        final CodecRegistration<ScoreboardObjectivePacket, CodecPlayOutScoreboardObjective> codecPlayOutScoreboardObjective = outbound.bind(
                CodecPlayOutScoreboardObjective.class);
        codecPlayOutScoreboardObjective.bind(ScoreboardObjectivePacket.Create.class);
        codecPlayOutScoreboardObjective.bind(ScoreboardObjectivePacket.Update.class);
        codecPlayOutScoreboardObjective.bind(ScoreboardObjectivePacket.Remove.class);
        outbound.bind(SetEntityPassengersCodec.class, SetEntityPassengersPacket.class);
        final CodecRegistration<TeamPacket, CodecPlayOutTeams> codecPlayOutTeams = outbound.bind(CodecPlayOutTeams.class);
        codecPlayOutTeams.bind(TeamPacket.AddMembers.class);
        codecPlayOutTeams.bind(TeamPacket.Create.class);
        codecPlayOutTeams.bind(TeamPacket.Update.class);
        codecPlayOutTeams.bind(TeamPacket.Remove.class);
        codecPlayOutTeams.bind(TeamPacket.RemoveMembers.class);
        final CodecRegistration<ScoreboardScorePacket, CodecPlayOutScoreboardScore> codecPlayOutScoreboardScore = outbound.bind(
                CodecPlayOutScoreboardScore.class);
        codecPlayOutScoreboardScore.bind(ScoreboardScorePacket.CreateOrUpdate.class);
        codecPlayOutScoreboardScore.bind(ScoreboardScorePacket.Remove.class);
        outbound.bind(CodecPlayOutWorldTime.class, PacketPlayOutWorldTime.class);
        final CodecRegistration<PacketPlayOutTitle, CodecPlayOutTitle> codecPlayOutTitle = outbound.bind(CodecPlayOutTitle.class);
        codecPlayOutTitle.bind(PacketPlayOutTitle.Clear.class);
        codecPlayOutTitle.bind(PacketPlayOutTitle.Reset.class);
        codecPlayOutTitle.bind(PacketPlayOutTitle.SetSubtitle.class);
        codecPlayOutTitle.bind(PacketPlayOutTitle.SetActionbarTitle.class);
        codecPlayOutTitle.bind(PacketPlayOutTitle.SetTimes.class);
        codecPlayOutTitle.bind(PacketPlayOutTitle.SetTitle.class);
        outbound.bind(EntitySoundEffectCodec.class, EntitySoundEffectPacket.class);
        outbound.bind(SoundEffectCodec.class, SoundEffectPacket.class);
        outbound.bind(StopSoundsCodec.class, StopSoundsPacket.class);
        outbound.bind(CodecPlayOutTabListHeaderAndFooter.class, PacketPlayOutTabListHeaderAndFooter.class);
        outbound.bind(CodecPlayOutDataResponse.class, PacketPlayOutDataResponse.class);
        outbound.bind(CodecPlayOutEntityCollectItem.class, PacketPlayOutEntityCollectItem.class);
        outbound.bind(CodecPlayOutEntityTeleport.class, PacketPlayOutEntityTeleport.class);
        outbound.bind(CodecPlayOutAdvancements.class, PacketPlayOutAdvancements.class);
        outbound.bind(); // TODO: Entity Properties
        outbound.bind(CodecPlayOutAddPotionEffect.class, PacketPlayOutAddPotionEffect.class);
        outbound.bind(CodecPlayOutDefineRecipes.class, PacketPlayOutDefineRecipes.class);
        outbound.bind(CodecPlayOutTags.class, PacketPlayOutTags.class);
    }
}
