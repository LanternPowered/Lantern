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
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientChangeSignCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientSendChatCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientClickRecipeCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientClickWindowCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientSettingsCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientStatusCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientRecipeBookCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientCreativeWindowActionCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInDataRequestBlock;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayInDataRequestEntity;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientEditCommandBlockBlockCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientEditCommandBlockEntityCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientEnchantItemCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientLocksDifficultyCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientModifyBookCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CloseWindowCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ConfirmWindowTransactionCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ChannelPayloadCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerHeldItemChangeCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientPickItemCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientFlyingStateCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientPlayerActionCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientBlockPlacementCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientDiggingCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientPlayerLookCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientPlayerMovementCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientPlayerMovementAndLookCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientPlayerOnGroundStateCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientPlayerSwingArmCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientUseItemCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientVehicleControlsCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientPlayerVehicleMovementCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.GenerateJigsawStructureCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ResourcePackStatusCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientSetDifficultyCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientSpectateCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientTabCompleteCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientConfirmTeleportCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.UpdateJigsawBlockMessageCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientUseEntityCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.AddPotionEffectCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.AdvancementsCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.BlockActionCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.BlockBreakAnimationCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.BlockChangeCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.BossBarCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ChangeGameStateCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ChatMessageCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ChunkCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutDataResponse;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetCommandsCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetRecipesCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutDestroyEntities;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutDisplayRecipe;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EffectCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityAnimationCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityCollectItemCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityEquipmentCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityHeadLookCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutEntityLook;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityLookAndRelativeMoveCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityMetadataCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityRelativeMoveCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntitySoundEffectCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityStatusCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityTeleportCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityVelocityCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutFaceAt;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutMultiBlockChange;
import org.lanternpowered.server.network.vanilla.packet.codec.play.NamedSoundEffectCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.OpenBookCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.OpenHorseWindowCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.OpenSignCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.OpenWindowCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerAbilitiesCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerHealthCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerJoinCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerPositionAndLookCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerRespawnCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerSpawnPositionCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.RemovePotionEffectCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetActiveScoreboardObjectiveCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutScoreboardObjective;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutScoreboardScore;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetActiveAdvancementTreeCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetResourcePackCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetCameraCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetCooldownCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetDifficultyCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetEntityPassengersCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetExperienceCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutSetWindowSlot;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SoundEffectCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnExperienceOrbCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnMobCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnObjectCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnPaintingCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnParticleCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnPlayerCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnThunderboltCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutStatistics;
import org.lanternpowered.server.network.vanilla.packet.codec.play.StopSoundsCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutTabComplete;
import org.lanternpowered.server.network.vanilla.packet.codec.play.TabListCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.TabListHeaderAndFooterCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutTags;
import org.lanternpowered.server.network.vanilla.packet.codec.play.TitleCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetWindowTradeOffersCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.UnloadChunkCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.UnlockRecipesCodec;
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
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientModifySignHandler;
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
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientUseEntityAttackHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientUseEntityInteractHandler;
import org.lanternpowered.server.network.vanilla.packet.processor.play.ProcessorPlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.packet.processor.play.SetGameModeProcessor;
import org.lanternpowered.server.network.vanilla.packet.processor.play.TabListProcessor;
import org.lanternpowered.server.network.vanilla.packet.processor.play.TheEndMessageProcessor;
import org.lanternpowered.server.network.vanilla.packet.processor.play.UpdateWorldSkyProcessor;
import org.lanternpowered.server.network.vanilla.packet.type.KeepAlivePacket;
import org.lanternpowered.server.network.vanilla.packet.type.DisconnectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.GenerateJigsawStructurePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientAcceptBeaconEffectsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ChangeAdvancementTreePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientItemRenamePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ChangeTradeOfferPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientModifySignPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSendChatPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientClickRecipePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientClickWindowPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSettingsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientCreativeWindowActionPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInDataRequest;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInDisplayedRecipe;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientDropHeldItemPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientEditCommandBlockPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientEnchantItemPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientLeaveBedPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientLockDifficultyPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientModifyBookPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.BrandPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ChannelPayloadPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.CloseWindowPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ConfirmWindowTransactionPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientFinishUsingItemPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerHeldItemChangePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutUnregisterChannels;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientRequestRespawnPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientPickItemPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientFlyingStatePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientBlockPlacementPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientDiggingPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientPlayerLookPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientPlayerMovementPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientPlayerMovementAndLookPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientMovementInputPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientPlayerOnGroundStatePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSneakStatePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSprintStatePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientPlayerSwingArmPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientUseItemPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientVehicleJumpPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientPlayerVehicleMovementPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientRecipeBookStatesPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientRequestStatisticsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ResourcePackStatusPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSetDifficultyPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSpectatePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientStartElytraFlyingPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSwapHandItemsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientTabCompletePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientConfirmTeleportPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.NamedSoundEffectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.UpdateJigsawBlockPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientUseEntityPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.AddPotionEffectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.AdvancementsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.BlockActionPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.BlockBreakAnimationPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.BlockChangePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutBlockEntity;
import org.lanternpowered.server.network.vanilla.packet.type.play.BossBarPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ChatMessagePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ChunkPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutDataResponse;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetCommandsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetRecipesPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutDestroyEntities;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutDisplayRecipe;
import org.lanternpowered.server.network.vanilla.packet.type.play.EffectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityAnimationPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityCollectItemPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityEquipmentPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityHeadLookPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityLookPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityLookAndRelativeMovePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityMetadataPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityRelativeMovePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.EntitySoundEffectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityStatusPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityTeleportPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityVelocityPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutFaceAt;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutMultiBlockChange;
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenBookPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenHorseWindowPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenSignPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ParticleEffectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerAbilitiesPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerHealthPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerJoinPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerPositionAndLookPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerRespawnPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerSpawnPositionPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetMusicDiscPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.RemovePotionEffectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetActiveScoreboardObjectivePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ScoreboardObjectivePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ScoreboardScorePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetActiveAdvancementTreePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetResourcePackPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetCameraPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetCooldownPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetDifficultyPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetEntityPassengersPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetExperiencePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetGameModePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetOpLevelPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetReducedDebugPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutSetWindowSlot;
import org.lanternpowered.server.network.vanilla.packet.type.play.SoundEffectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnExperienceOrbPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnMobPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnObjectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnPaintingPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnParticlePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnPlayerPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnThunderboltPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutStatistics;
import org.lanternpowered.server.network.vanilla.packet.type.play.StopSoundsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutTabComplete;
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListHeaderAndFooterPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutTags;
import org.lanternpowered.server.network.vanilla.packet.type.play.TeamPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.TheEndPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.TitlePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowTradeOffersPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.UnloadChunkPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.UnlockRecipesPacket;
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
        outbound.bindProcessor(ParticleEffectPacket.class, new ProcessorPlayOutParticleEffect());
        outbound.bindProcessor(SetGameModePacket.class, new SetGameModeProcessor());
        outbound.bindProcessor(UpdateWorldSkyPacket.class, new UpdateWorldSkyProcessor());
        outbound.bindProcessor(TabListPacket.class, new TabListProcessor());

        // Register the codecs and handlers of the default messages
        inbound.bind(ClientConfirmTeleportCodec.class, ClientConfirmTeleportPacket.class); // TODO: Handler
        inbound.bind(CodecPlayInDataRequestBlock.class, PacketPlayInDataRequest.Block.class); // TODO: Handler
        inbound.bind(ClientSetDifficultyCodec.class, ClientSetDifficultyPacket.class)
                .bindHandler(new ClientSetDifficultyHandler());
        inbound.bind(ClientSendChatCodec.class, ClientSendChatPacket.class)
                .bindHandler(new HandlerPlayInChatMessage());
        inbound.bind(ClientStatusCodec.class);
        inbound.bind(ClientSettingsCodec.class, ClientSettingsPacket.class)
                .bindHandler(new ClientSettingsHandler());
        inbound.bind(ClientTabCompleteCodec.class, ClientTabCompletePacket.class)
                .bindHandler(new HandlerPlayInTabComplete());
        inbound.bind(ConfirmWindowTransactionCodec.class, ConfirmWindowTransactionPacket.class); // TODO: Handler
        inbound.bind(ClientEnchantItemCodec.class, ClientEnchantItemPacket.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleEnchantItem));
        inbound.bind(ClientClickWindowCodec.class, ClientClickWindowPacket.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleWindowClick));
        inbound.bind(CloseWindowCodec.class, CloseWindowPacket.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleWindowClose));
        inbound.bind(ChannelPayloadCodec.class);
        inbound.bind(ClientModifyBookCodec.class);
        inbound.bind(CodecPlayInDataRequestEntity.class, PacketPlayInDataRequest.Entity.class); // TODO: Handler
        inbound.bind(ClientUseEntityCodec.class);
        inbound.bind(GenerateJigsawStructureCodec.class, GenerateJigsawStructurePacket.class);
        inbound.bind(KeepAliveCodec.class, KeepAlivePacket.class);
        inbound.bind(ClientLocksDifficultyCodec.class, ClientLockDifficultyPacket.class)
                .bindHandler(new ClientLockDifficultyHandler());
        inbound.bind(ClientPlayerMovementCodec.class, ClientPlayerMovementPacket.class)
                .bindHandler(new HandlerPlayInPlayerMovement());
        inbound.bind(ClientPlayerMovementAndLookCodec.class, ClientPlayerMovementAndLookPacket.class)
                .bindHandler(new HandlerPlayInPlayerMovementAndLook());
        inbound.bind(ClientPlayerLookCodec.class, ClientPlayerLookPacket.class)
                .bindHandler(new HandlerPlayInPlayerLook());
        inbound.bind(ClientPlayerOnGroundStateCodec.class, ClientPlayerOnGroundStatePacket.class)
                .bindHandler(new HandlerPlayInPlayerOnGroundState());
        inbound.bind(ClientPlayerVehicleMovementCodec.class, ClientPlayerVehicleMovementPacket.class)
                .bindHandler(new HandlerPlayInPlayerVehicleMovement());
        inbound.bind(); // TODO: Steer Boat
        inbound.bind(ClientPickItemCodec.class, ClientPickItemPacket.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handlePickItem));
        inbound.bind(ClientClickRecipeCodec.class, ClientClickRecipePacket.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleRecipeClick));
        inbound.bind(ClientFlyingStateCodec.class, ClientFlyingStatePacket.class)
                .bindHandler(new HandlerPlayInPlayerAbilities());
        inbound.bind(ClientDiggingCodec.class);
        inbound.bind(ClientPlayerActionCodec.class);
        inbound.bind(ClientVehicleControlsCodec.class);
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
        inbound.bind(PlayerHeldItemChangeCodec.class, PlayerHeldItemChangePacket.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleHeldItemChange));
        inbound.bind(ClientEditCommandBlockBlockCodec.class, ClientEditCommandBlockPacket.Block.class);
        inbound.bind(ClientEditCommandBlockEntityCodec.class, ClientEditCommandBlockPacket.Entity.class);
        inbound.bind(ClientCreativeWindowActionCodec.class, ClientCreativeWindowActionPacket.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleWindowCreativeClick));
        inbound.bind(UpdateJigsawBlockMessageCodec.class, UpdateJigsawBlockPacket.class); // TODO: Handler
        inbound.bind(); // TODO: Structure blocks
        inbound.bind(ClientChangeSignCodec.class, ClientModifySignPacket.class)
                .bindHandler(new ClientModifySignHandler());
        inbound.bind(ClientPlayerSwingArmCodec.class, ClientPlayerSwingArmPacket.class)
                .bindHandler(new HandlerPlayInPlayerSwingArm());
        inbound.bind(ClientSpectateCodec.class, ClientSpectatePacket.class); // TODO: Handler
        inbound.bind(ClientBlockPlacementCodec.class, ClientBlockPlacementPacket.class)
                .bindHandler(new ClientBlockPlacementHandler());
        inbound.bind(ClientUseItemCodec.class, ClientUseItemPacket.class)
                .bindHandler(new HandlerPlayInPlayerUseItem());

        // Provided by CodecPlayInOutCustomPayload
        inbound.bindMessage(BrandPacket.class); // TODO: Handler
        inbound.bindMessage(ClientModifyBookPacket.Edit.class)
                .bindHandler(new HandlerPlayInEditBook());
        inbound.bindMessage(ClientModifyBookPacket.Sign.class)
                .bindHandler(new HandlerPlayInSignBook());
        inbound.bindMessage(ChannelPayloadPacket.class)
                .bindHandler(new HandlerPlayInChannelPayload());
        inbound.bindMessage(PacketPlayInOutRegisterChannels.class)
                .bindHandler(new HandlerPlayInRegisterChannels());
        inbound.bindMessage(PacketPlayInOutUnregisterChannels.class)
                .bindHandler(new HandlerPlayInUnregisterChannels());
        // Provided by CodecPlayInUseEntity
        inbound.bindMessage(ClientUseEntityPacket.Attack.class)
                .bindHandler(new ClientUseEntityAttackHandler());
        inbound.bindMessage(ClientUseEntityPacket.Interact.class)
                .bindHandler(new ClientUseEntityInteractHandler());
        // Provided by CodecPlayInPlayerDigging
        inbound.bindMessage(ClientDiggingPacket.class)
                .bindHandler(new HandlerPlayInPlayerDigging());
        inbound.bindMessage(ClientDropHeldItemPacket.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleItemDrop));
        inbound.bindMessage(ClientFinishUsingItemPacket.class)
                .bindHandler(new HandlerPlayInFinishUsingItem());
        inbound.bindMessage(ClientSwapHandItemsPacket.class)
                .bindHandler(new HandlerPlayInSwapHandItems());
        inbound.bindMessage(ClientRequestRespawnPacket.class)
                .bindHandler((context, message) -> context.getSession().getPlayer().handleRespawn());
        inbound.bindMessage(ClientRequestStatisticsPacket.class)
                .bindHandler(new HandlerPlayInRequestStatistics());
        // Provided by CodecPlayInPlayerAction
        inbound.bindMessage(ClientLeaveBedPacket.class); // TODO: Handler
        inbound.bindMessage(ClientStartElytraFlyingPacket.class)
                .bindHandler(new HandlerPlayInStartElytraFlying());
        // Provided by CodecPlayInPlayerVehicleControls or CodecPlayInPlayerAction
        inbound.bindMessage(ClientSneakStatePacket.class)
                .bindHandler(new HandlerPlayInPlayerSneak());
        inbound.bindMessage(ClientSprintStatePacket.class)
                .bindHandler(new HandlerPlayInPlayerSprint());
        inbound.bindMessage(ClientVehicleJumpPacket.class); // TODO: Handler
        // Provided by CodecPlayInPlayerVehicleControls
        inbound.bindMessage(ClientMovementInputPacket.class)
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
        outbound.bind(SpawnExperienceOrbCodec.class, SpawnExperienceOrbPacket.class);
        outbound.bind(SpawnThunderboltCodec.class, SpawnThunderboltPacket.class);
        outbound.bind(SpawnMobCodec.class, SpawnMobPacket.class);
        outbound.bind(SpawnPaintingCodec.class, SpawnPaintingPacket.class);
        outbound.bind(SpawnPlayerCodec.class, SpawnPlayerPacket.class);
        outbound.bind(EntityAnimationCodec.class, EntityAnimationPacket.class);
        outbound.bind(CodecPlayOutStatistics.class, PacketPlayOutStatistics.class);
        outbound.bind(BlockBreakAnimationCodec.class, BlockBreakAnimationPacket.class);
        outbound.bind(CodecPlayOutUpdateBlockEntity.class, PacketPlayOutBlockEntity.class);
        outbound.bind(BlockActionCodec.class, BlockActionPacket.class);
        outbound.bind(BlockChangeCodec.class, BlockChangePacket.class);
        final CodecRegistration<BossBarPacket, BossBarCodec> codecPlayOutBossBar = outbound.bind(BossBarCodec.class);
        codecPlayOutBossBar.bind(BossBarPacket.Add.class);
        codecPlayOutBossBar.bind(BossBarPacket.Remove.class);
        codecPlayOutBossBar.bind(BossBarPacket.UpdatePercent.class);
        codecPlayOutBossBar.bind(BossBarPacket.UpdateStyle.class);
        codecPlayOutBossBar.bind(BossBarPacket.UpdateName.class);
        codecPlayOutBossBar.bind(BossBarPacket.UpdateFlags.class);
        outbound.bind(SetDifficultyCodec.class, SetDifficultyPacket.class);
        outbound.bind(ChatMessageCodec.class, ChatMessagePacket.class);
        outbound.bind(CodecPlayOutMultiBlockChange.class, PacketPlayOutMultiBlockChange.class);
        outbound.bind(CodecPlayOutTabComplete.class, PacketPlayOutTabComplete.class);
        outbound.bind(SetCommandsCodec.class, SetCommandsPacket.class);
        outbound.bind(ConfirmWindowTransactionCodec.class, ConfirmWindowTransactionPacket.class);
        outbound.bind(CloseWindowCodec.class, CloseWindowPacket.class);
        outbound.bind(SetWindowItemsCodec.class, SetWindowItemsPacket.class);
        outbound.bind(SetWindowPropertyCodec.class, SetWindowPropertyPacket.class);
        outbound.bind(CodecPlayOutSetWindowSlot.class, PacketPlayOutSetWindowSlot.class);
        outbound.bind(SetCooldownCodec.class, SetCooldownPacket.class);
        final CodecRegistration<Packet, ChannelPayloadCodec> codecPlayInOutCustomPayload = outbound.bind(
                ChannelPayloadCodec.class);
        codecPlayInOutCustomPayload.bind(ChannelPayloadPacket.class);
        codecPlayInOutCustomPayload.bind(BrandPacket.class);
        outbound.bind(NamedSoundEffectCodec.class, NamedSoundEffectPacket.class);
        outbound.bind(DisconnectCodec.class, DisconnectPacket.class);
        final CodecRegistration<Packet, EntityStatusCodec> codecPlayOutEntityStatus = outbound.bind(EntityStatusCodec.class);
        codecPlayOutEntityStatus.bind(EntityStatusPacket.class);
        codecPlayOutEntityStatus.bind(SetOpLevelPacket.class);
        codecPlayOutEntityStatus.bind(SetReducedDebugPacket.class);
        codecPlayOutEntityStatus.bind(ClientFinishUsingItemPacket.class);
        outbound.bind(); // TODO: Explosion
        outbound.bind(UnloadChunkCodec.class, UnloadChunkPacket.class);
        outbound.bind(ChangeGameStateCodec.class, ChangeGameStatePacket.class);
        outbound.bind(OpenHorseWindowCodec.class, OpenHorseWindowPacket.class);
        outbound.bind(KeepAliveCodec.class, KeepAlivePacket.class);
        final CodecRegistration<ChunkPacket, ChunkCodec> codecPlayOutChunkData = outbound.bind(ChunkCodec.class);
        codecPlayOutChunkData.bind(ChunkPacket.Init.class);
        codecPlayOutChunkData.bind(ChunkPacket.Update.class);
        final CodecRegistration<Packet, EffectCodec> codecPlayOutEntityEffect = outbound.bind(EffectCodec.class);
        codecPlayOutEntityEffect.bind(EffectPacket.class);
        codecPlayOutEntityEffect.bind(SetMusicDiscPacket.class);
        outbound.bind(SpawnParticleCodec.class, SpawnParticlePacket.class);
        outbound.bind(UpdateLightCodec.class, UpdateLightPacket.class);
        outbound.bind(PlayerJoinCodec.class, PlayerJoinPacket.class);
        outbound.bind(); // TODO: Map
        outbound.bind(SetWindowTradeOffersCodec.class, SetWindowTradeOffersPacket.class);
        outbound.bind(EntityRelativeMoveCodec.class, EntityRelativeMovePacket.class);
        outbound.bind(EntityLookAndRelativeMoveCodec.class, EntityLookAndRelativeMovePacket.class);
        outbound.bind(CodecPlayOutEntityLook.class, EntityLookPacket.class);
        outbound.bind(); // Entity
        outbound.bind(); // TODO: Vehicle Move
        outbound.bind(OpenSignCodec.class, OpenSignPacket.class);
        outbound.bind(OpenWindowCodec.class, OpenWindowPacket.class);
        outbound.bind(OpenBookCodec.class, OpenBookPacket.class);
        outbound.bind(CodecPlayOutDisplayRecipe.class, PacketPlayOutDisplayRecipe.class);
        outbound.bind(PlayerAbilitiesCodec.class, PlayerAbilitiesPacket.class);
        outbound.bind(); // TODO: Combat Event
        outbound.bind(TabListCodec.class, TabListPacket.class);
        final CodecRegistration<PacketPlayOutFaceAt, CodecPlayOutFaceAt> codecPlayOutFaceAt =
                outbound.bind(CodecPlayOutFaceAt.class);
        codecPlayOutFaceAt.bind(PacketPlayOutFaceAt.Entity.class);
        codecPlayOutFaceAt.bind(PacketPlayOutFaceAt.Position.class);
        outbound.bind(PlayerPositionAndLookCodec.class, PlayerPositionAndLookPacket.class);
        final CodecRegistration<UnlockRecipesPacket, UnlockRecipesCodec> codecPlayOutUnlockRecipes =
                outbound.bind(UnlockRecipesCodec.class);
        codecPlayOutUnlockRecipes.bind(UnlockRecipesPacket.Add.class);
        codecPlayOutUnlockRecipes.bind(UnlockRecipesPacket.Initialize.class);
        codecPlayOutUnlockRecipes.bind(UnlockRecipesPacket.Remove.class);
        outbound.bind(CodecPlayOutDestroyEntities.class, PacketPlayOutDestroyEntities.class);
        outbound.bind(RemovePotionEffectCodec.class, RemovePotionEffectPacket.class);
        outbound.bind(SetResourcePackCodec.class, SetResourcePackPacket.class);
        outbound.bind(PlayerRespawnCodec.class, PlayerRespawnPacket.class);
        outbound.bind(EntityHeadLookCodec.class, EntityHeadLookPacket.class);
        outbound.bind(SetActiveAdvancementTreeCodec.class, SetActiveAdvancementTreePacket.class);
        final CodecRegistration<PacketPlayOutWorldBorder, CodecPlayOutWorldBorder> codecPlayOutWorldBorder =
                outbound.bind(CodecPlayOutWorldBorder.class);
        codecPlayOutWorldBorder.bind(PacketPlayOutWorldBorder.Initialize.class);
        codecPlayOutWorldBorder.bind(PacketPlayOutWorldBorder.UpdateCenter.class);
        codecPlayOutWorldBorder.bind(PacketPlayOutWorldBorder.UpdateDiameter.class);
        codecPlayOutWorldBorder.bind(PacketPlayOutWorldBorder.UpdateLerpedDiameter.class);
        codecPlayOutWorldBorder.bind(PacketPlayOutWorldBorder.UpdateWarningDistance.class);
        codecPlayOutWorldBorder.bind(PacketPlayOutWorldBorder.UpdateWarningTime.class);
        outbound.bind(SetCameraCodec.class, SetCameraPacket.class);
        outbound.bind(PlayerHeldItemChangeCodec.class, PlayerHeldItemChangePacket.class);
        outbound.bind(UpdateViewPositionCodec.class, UpdateViewPositionPacket.class);
        outbound.bind(UpdateViewDistanceCodec.class, UpdateViewDistancePacket.class);
        outbound.bind(PlayerSpawnPositionCodec.class, PlayerSpawnPositionPacket.class);
        outbound.bind(SetActiveScoreboardObjectiveCodec.class, SetActiveScoreboardObjectivePacket.class);
        outbound.bind(EntityMetadataCodec.class, EntityMetadataPacket.class);
        outbound.bind(); // TODO: Attach Entity
        outbound.bind(EntityVelocityCodec.class, EntityVelocityPacket.class);
        outbound.bind(EntityEquipmentCodec.class, EntityEquipmentPacket.class);
        outbound.bind(SetExperienceCodec.class, SetExperiencePacket.class);
        outbound.bind(PlayerHealthCodec.class, PlayerHealthPacket.class);
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
        final CodecRegistration<TitlePacket, TitleCodec> codecPlayOutTitle = outbound.bind(TitleCodec.class);
        codecPlayOutTitle.bind(TitlePacket.Clear.class);
        codecPlayOutTitle.bind(TitlePacket.Reset.class);
        codecPlayOutTitle.bind(TitlePacket.SetSubtitle.class);
        codecPlayOutTitle.bind(TitlePacket.SetActionbarTitle.class);
        codecPlayOutTitle.bind(TitlePacket.SetTimes.class);
        codecPlayOutTitle.bind(TitlePacket.SetTitle.class);
        outbound.bind(EntitySoundEffectCodec.class, EntitySoundEffectPacket.class);
        outbound.bind(SoundEffectCodec.class, SoundEffectPacket.class);
        outbound.bind(StopSoundsCodec.class, StopSoundsPacket.class);
        outbound.bind(TabListHeaderAndFooterCodec.class, TabListHeaderAndFooterPacket.class);
        outbound.bind(CodecPlayOutDataResponse.class, PacketPlayOutDataResponse.class);
        outbound.bind(EntityCollectItemCodec.class, EntityCollectItemPacket.class);
        outbound.bind(EntityTeleportCodec.class, EntityTeleportPacket.class);
        outbound.bind(AdvancementsCodec.class, AdvancementsPacket.class);
        outbound.bind(); // TODO: Entity Properties
        outbound.bind(AddPotionEffectCodec.class, AddPotionEffectPacket.class);
        outbound.bind(SetRecipesCodec.class, SetRecipesPacket.class);
        outbound.bind(CodecPlayOutTags.class, PacketPlayOutTags.class);
    }
}
