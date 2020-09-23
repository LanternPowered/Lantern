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

import org.lanternpowered.server.inventory.PlayerInventoryContainerSession;
import org.lanternpowered.server.network.packet.CodecRegistration;
import org.lanternpowered.server.network.packet.Packet;
import org.lanternpowered.server.network.packet.MessageRegistry;
import org.lanternpowered.server.network.vanilla.packet.codec.KeepAliveCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.DisconnectEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientAcceptBeaconEffectsCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ChangeAdvancementTreeCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientItemRenameCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientChangeTradeOfferCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientChangeSignCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientSendChatMessageDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientClickRecipeCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientClickWindowDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientSettingsDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientStatusDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientSetDisplayedRecipeCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientCreativeWindowActionCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientRequestBlockDataDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientRequestEntityDataDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientEditCommandBlockBlockCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientEditCommandBlockEntityCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientClickWindowButtonDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientLockDifficultyDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientModifyBookDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CloseWindowCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ConfirmWindowTransactionCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ChannelPayloadCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerHeldItemChangeEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientPickItemCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientFlyingStateCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientPlayerActionCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientBlockPlacementCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientDiggingCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientPlayerLookDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientPlayerMovementDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientPlayerMovementAndLookDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientPlayerOnGroundStateDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientPlayerSwingArmCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientUseItemCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientVehicleControlsCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientPlayerVehicleMovementDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.GenerateJigsawStructureDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ResourcePackStatusCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientSetDifficultyDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientSpectateCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientTabCompleteDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientConfirmTeleportDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.UpdateJigsawBlockMessageCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ClientUseEntityDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.AddPotionEffectEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.AdvancementsEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.BlockActionEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.BlockBreakAnimationEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.BlockChangeEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.BossBarEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ChangeGameStateEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ChatMessageEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ChunkInitOrUpdateEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.DataResponseEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetCommandsEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetRecipesEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.DestroyEntitiesEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetActiveRecipeCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EffectEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityAnimationEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityCollectItemEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityEquipmentEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityHeadLookEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityLookEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityLookAndRelativeMoveEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityMetadataEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityRelativeMoveEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntitySoundEffectEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityStatusEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityTeleportEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.EntityVelocityEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerFaceAtEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecPlayOutMultiBlockChange;
import org.lanternpowered.server.network.vanilla.packet.codec.play.NamedSoundEffectEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.OpenBookEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.OpenHorseWindowEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.OpenSignEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.OpenWindowEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerAbilitiesCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerHealthEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerJoinEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerPositionAndLookEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerRespawnEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.PlayerSpawnPositionEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.RemovePotionEffectEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetActiveScoreboardObjectiveEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ScoreboardObjectiveEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetActiveAdvancementTreeEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetResourcePackEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetCameraEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetCooldownEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetDifficultyEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetEntityPassengersCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetExperienceEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetWindowSlotEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SoundEffectEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnExperienceOrbEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnMobEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnObjectEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnPaintingEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnParticleEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnPlayerEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SpawnThunderboltCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.play.StatisticsEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.StopSoundsEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.TabCompleteEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.TabListEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.TabListHeaderAndFooterEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.TagsEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.TitleEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetWindowTradeOffersEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.ChunkUnloadEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.UnlockRecipesEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.UpdateBlockEntityEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.UpdateLightEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.UpdateViewDistanceEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.UpdateViewPositionEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetWindowItemsEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.SetWindowPropertyEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.WorldBorderEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.play.WorldTimeEncoder;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientLockDifficultyHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSendChatMessageHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSetDifficultyHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientAdvancementTreeHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientModifySignHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ChannelPayloadHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSettingsHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ContainerSessionForwardingHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientRecipeBookStatesHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientEditBookHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInFinishUsingItem;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInPlayerAbilities;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientBlockPlacementHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientDiggingHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerLookHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerMovementHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerMovementAndLookHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientMovementInputHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerOnGroundStateHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSneakStateHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSprintStateHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerSwingArmHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientUseItemHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerVehicleMovementHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.RegisterChannelsHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInRequestStatistics;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ResourcePackStatusHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSignBookHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientStartElytraFlyingHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInSwapHandItems;
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInTabComplete;
import org.lanternpowered.server.network.vanilla.packet.handler.play.UnregisterChannelsHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientUseEntityAttackHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientUseEntityInteractHandler;
import org.lanternpowered.server.network.vanilla.packet.processor.play.ParticleEffectProcessor;
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
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientChangeTradeOfferPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientModifySignPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSendChatMessagePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientClickRecipePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientClickWindowPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSettingsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientCreativeWindowActionPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientRequestDataPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSetDisplayedRecipePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientDropHeldItemPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientEditCommandBlockPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientClickWindowButtonPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientLeaveBedPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientLockDifficultyPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientModifyBookPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.BrandPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ChannelPayloadPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.CloseWindowPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ConfirmWindowTransactionPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.FinishUsingItemPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerHeldItemChangePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.RegisterChannelsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.UnregisterChannelsPacket;
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
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientRecipeBookStatePacket;
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
import org.lanternpowered.server.network.vanilla.packet.type.play.UpdateBlockEntityPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.BossBarPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ChatMessagePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ChunkPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.DataResponsePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetCommandsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetRecipesPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.DestroyEntitiesPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetActiveRecipePacket;
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
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerFaceAtPacket;
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
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowSlotPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SoundEffectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnExperienceOrbPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnMobPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnObjectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnPaintingPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnParticlePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnPlayerPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnThunderboltPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.StatisticsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.StopSoundsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.TabCompletePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListHeaderAndFooterPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.TagsPacket;
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
import org.lanternpowered.server.network.vanilla.packet.type.play.WorldBorderPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.UpdateWorldSkyPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.WorldTimePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.internal.ChangeGameStatePacket;

@SuppressWarnings("ALL")
final class ProtocolPlay extends ProtocolBase {

    ProtocolPlay() {
        final MessageRegistry inbound = inbound();
        final MessageRegistry outbound = outbound();

        // Register the processors
        outbound.bindProcessor(TheEndPacket.class, new TheEndMessageProcessor());
        outbound.bindProcessor(ParticleEffectPacket.class, new ParticleEffectProcessor());
        outbound.bindProcessor(SetGameModePacket.class, new SetGameModeProcessor());
        outbound.bindProcessor(UpdateWorldSkyPacket.class, new UpdateWorldSkyProcessor());
        outbound.bindProcessor(TabListPacket.class, new TabListProcessor());

        // Register the codecs and handlers of the default messages
        inbound.bind(ClientConfirmTeleportDecoder.class, ClientConfirmTeleportPacket.class); // TODO: Handler
        inbound.bind(ClientRequestBlockDataDecoder.class, ClientRequestDataPacket.Block.class); // TODO: Handler
        inbound.bind(ClientSetDifficultyDecoder.class, ClientSetDifficultyPacket.class)
                .bindHandler(new ClientSetDifficultyHandler());
        inbound.bind(ClientSendChatMessageDecoder.class, ClientSendChatMessagePacket.class)
                .bindHandler(new ClientSendChatMessageHandler());
        inbound.bind(ClientStatusDecoder.class);
        inbound.bind(ClientSettingsDecoder.class, ClientSettingsPacket.class)
                .bindHandler(new ClientSettingsHandler());
        inbound.bind(ClientTabCompleteDecoder.class, ClientTabCompletePacket.class)
                .bindHandler(new HandlerPlayInTabComplete());
        inbound.bind(ConfirmWindowTransactionCodec.class, ConfirmWindowTransactionPacket.class); // TODO: Handler
        inbound.bind(ClientClickWindowButtonDecoder.class, ClientClickWindowButtonPacket.class)
                .bindHandler(new ContainerSessionForwardingHandler<>(PlayerInventoryContainerSession::handleEnchantItem));
        inbound.bind(ClientClickWindowDecoder.class, ClientClickWindowPacket.class)
                .bindHandler(new ContainerSessionForwardingHandler<>(PlayerInventoryContainerSession::handleWindowClick));
        inbound.bind(CloseWindowCodec.class, CloseWindowPacket.class)
                .bindHandler(new ContainerSessionForwardingHandler<>(PlayerInventoryContainerSession::handleWindowClose));
        inbound.bind(ChannelPayloadCodec.class);
        inbound.bind(ClientModifyBookDecoder.class);
        inbound.bind(ClientRequestEntityDataDecoder.class, ClientRequestDataPacket.Entity.class); // TODO: Handler
        inbound.bind(ClientUseEntityDecoder.class);
        inbound.bind(GenerateJigsawStructureDecoder.class, GenerateJigsawStructurePacket.class);
        inbound.bind(KeepAliveCodec.class, KeepAlivePacket.class);
        inbound.bind(ClientLockDifficultyDecoder.class, ClientLockDifficultyPacket.class)
                .bindHandler(new ClientLockDifficultyHandler());
        inbound.bind(ClientPlayerMovementDecoder.class, ClientPlayerMovementPacket.class)
                .bindHandler(new ClientPlayerMovementHandler());
        inbound.bind(ClientPlayerMovementAndLookDecoder.class, ClientPlayerMovementAndLookPacket.class)
                .bindHandler(new ClientPlayerMovementAndLookHandler());
        inbound.bind(ClientPlayerLookDecoder.class, ClientPlayerLookPacket.class)
                .bindHandler(new ClientPlayerLookHandler());
        inbound.bind(ClientPlayerOnGroundStateDecoder.class, ClientPlayerOnGroundStatePacket.class)
                .bindHandler(new ClientPlayerOnGroundStateHandler());
        inbound.bind(ClientPlayerVehicleMovementDecoder.class, ClientPlayerVehicleMovementPacket.class)
                .bindHandler(new ClientPlayerVehicleMovementHandler());
        inbound.bind(); // TODO: Steer Boat
        inbound.bind(ClientPickItemCodec.class, ClientPickItemPacket.class)
                .bindHandler(new ContainerSessionForwardingHandler<>(PlayerInventoryContainerSession::handlePickItem));
        inbound.bind(ClientClickRecipeCodec.class, ClientClickRecipePacket.class)
                .bindHandler(new ContainerSessionForwardingHandler<>(PlayerInventoryContainerSession::handleRecipeClick));
        inbound.bind(ClientFlyingStateCodec.class, ClientFlyingStatePacket.class)
                .bindHandler(new HandlerPlayInPlayerAbilities());
        inbound.bind(ClientDiggingCodec.class);
        inbound.bind(ClientPlayerActionCodec.class);
        inbound.bind(ClientVehicleControlsCodec.class);
        inbound.bind(ClientSetDisplayedRecipeCodec.class);
        inbound.bind(ClientItemRenameCodec.class, ClientItemRenamePacket.class)
                .bindHandler(new ContainerSessionForwardingHandler<>(PlayerInventoryContainerSession::handleItemRename));
        inbound.bind(ResourcePackStatusCodec.class, ResourcePackStatusPacket.class)
                .bindHandler(new ResourcePackStatusHandler());
        inbound.bind(ChangeAdvancementTreeCodec.class);
        inbound.bind(ClientChangeTradeOfferCodec.class, ClientChangeTradeOfferPacket.class)
                .bindHandler(new ContainerSessionForwardingHandler<>(PlayerInventoryContainerSession::handleOfferChange));
        inbound.bind(ClientAcceptBeaconEffectsCodec.class, ClientAcceptBeaconEffectsPacket.class)
                .bindHandler(new ContainerSessionForwardingHandler<>(PlayerInventoryContainerSession::handleAcceptBeaconEffects));
        inbound.bind(PlayerHeldItemChangeEncoder.class, PlayerHeldItemChangePacket.class)
                .bindHandler(new ContainerSessionForwardingHandler<>(PlayerInventoryContainerSession::handleHeldItemChange));
        inbound.bind(ClientEditCommandBlockBlockCodec.class, ClientEditCommandBlockPacket.Block.class);
        inbound.bind(ClientEditCommandBlockEntityCodec.class, ClientEditCommandBlockPacket.Entity.class);
        inbound.bind(ClientCreativeWindowActionCodec.class, ClientCreativeWindowActionPacket.class)
                .bindHandler(new ContainerSessionForwardingHandler<>(PlayerInventoryContainerSession::handleWindowCreativeClick));
        inbound.bind(UpdateJigsawBlockMessageCodec.class, UpdateJigsawBlockPacket.class); // TODO: Handler
        inbound.bind(); // TODO: Structure blocks
        inbound.bind(ClientChangeSignCodec.class, ClientModifySignPacket.class)
                .bindHandler(new ClientModifySignHandler());
        inbound.bind(ClientPlayerSwingArmCodec.class, ClientPlayerSwingArmPacket.class)
                .bindHandler(new ClientPlayerSwingArmHandler());
        inbound.bind(ClientSpectateCodec.class, ClientSpectatePacket.class); // TODO: Handler
        inbound.bind(ClientBlockPlacementCodec.class, ClientBlockPlacementPacket.class)
                .bindHandler(new ClientBlockPlacementHandler());
        inbound.bind(ClientUseItemCodec.class, ClientUseItemPacket.class)
                .bindHandler(new ClientUseItemHandler());

        // Provided by CodecPlayInOutCustomPayload
        inbound.bindMessage(BrandPacket.class); // TODO: Handler
        inbound.bindMessage(ClientModifyBookPacket.Edit.class)
                .bindHandler(new ClientEditBookHandler());
        inbound.bindMessage(ClientModifyBookPacket.Sign.class)
                .bindHandler(new ClientSignBookHandler());
        inbound.bindMessage(ChannelPayloadPacket.class)
                .bindHandler(new ChannelPayloadHandler());
        inbound.bindMessage(RegisterChannelsPacket.class)
                .bindHandler(new RegisterChannelsHandler());
        inbound.bindMessage(UnregisterChannelsPacket.class)
                .bindHandler(new UnregisterChannelsHandler());
        // Provided by CodecPlayInUseEntity
        inbound.bindMessage(ClientUseEntityPacket.Attack.class)
                .bindHandler(new ClientUseEntityAttackHandler());
        inbound.bindMessage(ClientUseEntityPacket.Interact.class)
                .bindHandler(new ClientUseEntityInteractHandler());
        // Provided by CodecPlayInPlayerDigging
        inbound.bindMessage(ClientDiggingPacket.class)
                .bindHandler(new ClientDiggingHandler());
        inbound.bindMessage(ClientDropHeldItemPacket.class)
                .bindHandler(new ContainerSessionForwardingHandler<>(PlayerInventoryContainerSession::handleItemDrop));
        inbound.bindMessage(FinishUsingItemPacket.class)
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
                .bindHandler(new ClientStartElytraFlyingHandler());
        // Provided by CodecPlayInPlayerVehicleControls or CodecPlayInPlayerAction
        inbound.bindMessage(ClientSneakStatePacket.class)
                .bindHandler(new ClientSneakStateHandler());
        inbound.bindMessage(ClientSprintStatePacket.class)
                .bindHandler(new ClientSprintStateHandler());
        inbound.bindMessage(ClientVehicleJumpPacket.class); // TODO: Handler
        // Provided by CodecPlayInPlayerVehicleControls
        inbound.bindMessage(ClientMovementInputPacket.class)
                .bindHandler(new ClientMovementInputHandler());
        // Provided by CodecPlayInCraftingBookData
        inbound.bindMessage(ClientSetDisplayedRecipePacket.class)
                .bindHandler(new ContainerSessionForwardingHandler<>(PlayerInventoryContainerSession::handleDisplayedRecipe));
        inbound.bindMessage(ClientRecipeBookStatePacket.class)
                .bindHandler(new ClientRecipeBookStatesHandler());
        // Provided by CodecPlayInAdvancementTab
        final ClientAdvancementTreeHandler handlerPlayInAdvancementTree = new ClientAdvancementTreeHandler();
        inbound.bindMessage(ChangeAdvancementTreePacket.Close.class)
                .bindHandler(handlerPlayInAdvancementTree);
        inbound.bindMessage(ChangeAdvancementTreePacket.Open.class)
                .bindHandler(handlerPlayInAdvancementTree);

        outbound.bind(SpawnObjectEncoder.class, SpawnObjectPacket.class);
        outbound.bind(SpawnExperienceOrbEncoder.class, SpawnExperienceOrbPacket.class);
        outbound.bind(SpawnThunderboltCodec.class, SpawnThunderboltPacket.class);
        outbound.bind(SpawnMobEncoder.class, SpawnMobPacket.class);
        outbound.bind(SpawnPaintingEncoder.class, SpawnPaintingPacket.class);
        outbound.bind(SpawnPlayerEncoder.class, SpawnPlayerPacket.class);
        outbound.bind(EntityAnimationEncoder.class, EntityAnimationPacket.class);
        outbound.bind(StatisticsEncoder.class, StatisticsPacket.class);
        outbound.bind(BlockBreakAnimationEncoder.class, BlockBreakAnimationPacket.class);
        outbound.bind(UpdateBlockEntityEncoder.class, UpdateBlockEntityPacket.class);
        outbound.bind(BlockActionEncoder.class, BlockActionPacket.class);
        outbound.bind(BlockChangeEncoder.class, BlockChangePacket.class);
        final CodecRegistration<BossBarPacket, BossBarEncoder> codecPlayOutBossBar = outbound.bind(BossBarEncoder.class);
        codecPlayOutBossBar.bind(BossBarPacket.Add.class);
        codecPlayOutBossBar.bind(BossBarPacket.Remove.class);
        codecPlayOutBossBar.bind(BossBarPacket.UpdatePercent.class);
        codecPlayOutBossBar.bind(BossBarPacket.UpdateStyle.class);
        codecPlayOutBossBar.bind(BossBarPacket.UpdateName.class);
        codecPlayOutBossBar.bind(BossBarPacket.UpdateFlags.class);
        outbound.bind(SetDifficultyEncoder.class, SetDifficultyPacket.class);
        outbound.bind(ChatMessageEncoder.class, ChatMessagePacket.class);
        outbound.bind(CodecPlayOutMultiBlockChange.class, PacketPlayOutMultiBlockChange.class);
        outbound.bind(TabCompleteEncoder.class, TabCompletePacket.class);
        outbound.bind(SetCommandsEncoder.class, SetCommandsPacket.class);
        outbound.bind(ConfirmWindowTransactionCodec.class, ConfirmWindowTransactionPacket.class);
        outbound.bind(CloseWindowCodec.class, CloseWindowPacket.class);
        outbound.bind(SetWindowItemsEncoder.class, SetWindowItemsPacket.class);
        outbound.bind(SetWindowPropertyEncoder.class, SetWindowPropertyPacket.class);
        outbound.bind(SetWindowSlotEncoder.class, SetWindowSlotPacket.class);
        outbound.bind(SetCooldownEncoder.class, SetCooldownPacket.class);
        final CodecRegistration<Packet, ChannelPayloadCodec> codecPlayInOutCustomPayload = outbound.bind(
                ChannelPayloadCodec.class);
        codecPlayInOutCustomPayload.bind(ChannelPayloadPacket.class);
        codecPlayInOutCustomPayload.bind(BrandPacket.class);
        outbound.bind(NamedSoundEffectEncoder.class, NamedSoundEffectPacket.class);
        outbound.bind(DisconnectEncoder.class, DisconnectPacket.class);
        final CodecRegistration<Packet, EntityStatusEncoder> codecPlayOutEntityStatus = outbound.bind(EntityStatusEncoder.class);
        codecPlayOutEntityStatus.bind(EntityStatusPacket.class);
        codecPlayOutEntityStatus.bind(SetOpLevelPacket.class);
        codecPlayOutEntityStatus.bind(SetReducedDebugPacket.class);
        codecPlayOutEntityStatus.bind(FinishUsingItemPacket.class);
        outbound.bind(); // TODO: Explosion
        outbound.bind(ChunkUnloadEncoder.class, UnloadChunkPacket.class);
        outbound.bind(ChangeGameStateEncoder.class, ChangeGameStatePacket.class);
        outbound.bind(OpenHorseWindowEncoder.class, OpenHorseWindowPacket.class);
        outbound.bind(KeepAliveCodec.class, KeepAlivePacket.class);
        final CodecRegistration<ChunkPacket, ChunkInitOrUpdateEncoder> codecPlayOutChunkData = outbound.bind(ChunkInitOrUpdateEncoder.class);
        codecPlayOutChunkData.bind(ChunkPacket.Init.class);
        codecPlayOutChunkData.bind(ChunkPacket.Update.class);
        final CodecRegistration<Packet, EffectEncoder> codecPlayOutEntityEffect = outbound.bind(EffectEncoder.class);
        codecPlayOutEntityEffect.bind(EffectPacket.class);
        codecPlayOutEntityEffect.bind(SetMusicDiscPacket.class);
        outbound.bind(SpawnParticleEncoder.class, SpawnParticlePacket.class);
        outbound.bind(UpdateLightEncoder.class, UpdateLightPacket.class);
        outbound.bind(PlayerJoinEncoder.class, PlayerJoinPacket.class);
        outbound.bind(); // TODO: Map
        outbound.bind(SetWindowTradeOffersEncoder.class, SetWindowTradeOffersPacket.class);
        outbound.bind(EntityRelativeMoveEncoder.class, EntityRelativeMovePacket.class);
        outbound.bind(EntityLookAndRelativeMoveEncoder.class, EntityLookAndRelativeMovePacket.class);
        outbound.bind(EntityLookEncoder.class, EntityLookPacket.class);
        outbound.bind(); // Entity
        outbound.bind(); // TODO: Vehicle Move
        outbound.bind(OpenSignEncoder.class, OpenSignPacket.class);
        outbound.bind(OpenWindowEncoder.class, OpenWindowPacket.class);
        outbound.bind(OpenBookEncoder.class, OpenBookPacket.class);
        outbound.bind(SetActiveRecipeCodec.class, SetActiveRecipePacket.class);
        outbound.bind(PlayerAbilitiesCodec.class, PlayerAbilitiesPacket.class);
        outbound.bind(); // TODO: Combat Event
        outbound.bind(TabListEncoder.class, TabListPacket.class);
        final CodecRegistration<PlayerFaceAtPacket, PlayerFaceAtEncoder> codecPlayOutFaceAt =
                outbound.bind(PlayerFaceAtEncoder.class);
        codecPlayOutFaceAt.bind(PlayerFaceAtPacket.Entity.class);
        codecPlayOutFaceAt.bind(PlayerFaceAtPacket.Position.class);
        outbound.bind(PlayerPositionAndLookEncoder.class, PlayerPositionAndLookPacket.class);
        final CodecRegistration<UnlockRecipesPacket, UnlockRecipesEncoder> codecPlayOutUnlockRecipes =
                outbound.bind(UnlockRecipesEncoder.class);
        codecPlayOutUnlockRecipes.bind(UnlockRecipesPacket.Add.class);
        codecPlayOutUnlockRecipes.bind(UnlockRecipesPacket.Init.class);
        codecPlayOutUnlockRecipes.bind(UnlockRecipesPacket.Remove.class);
        outbound.bind(DestroyEntitiesEncoder.class, DestroyEntitiesPacket.class);
        outbound.bind(RemovePotionEffectEncoder.class, RemovePotionEffectPacket.class);
        outbound.bind(SetResourcePackEncoder.class, SetResourcePackPacket.class);
        outbound.bind(PlayerRespawnEncoder.class, PlayerRespawnPacket.class);
        outbound.bind(EntityHeadLookEncoder.class, EntityHeadLookPacket.class);
        outbound.bind(SetActiveAdvancementTreeEncoder.class, SetActiveAdvancementTreePacket.class);
        final CodecRegistration<WorldBorderPacket, WorldBorderEncoder> codecPlayOutWorldBorder =
                outbound.bind(WorldBorderEncoder.class);
        codecPlayOutWorldBorder.bind(WorldBorderPacket.Init.class);
        codecPlayOutWorldBorder.bind(WorldBorderPacket.UpdateCenter.class);
        codecPlayOutWorldBorder.bind(WorldBorderPacket.UpdateDiameter.class);
        codecPlayOutWorldBorder.bind(WorldBorderPacket.UpdateLerpedDiameter.class);
        codecPlayOutWorldBorder.bind(WorldBorderPacket.UpdateWarningDistance.class);
        codecPlayOutWorldBorder.bind(WorldBorderPacket.UpdateWarningTime.class);
        outbound.bind(SetCameraEncoder.class, SetCameraPacket.class);
        outbound.bind(PlayerHeldItemChangeEncoder.class, PlayerHeldItemChangePacket.class);
        outbound.bind(UpdateViewPositionEncoder.class, UpdateViewPositionPacket.class);
        outbound.bind(UpdateViewDistanceEncoder.class, UpdateViewDistancePacket.class);
        outbound.bind(PlayerSpawnPositionEncoder.class, PlayerSpawnPositionPacket.class);
        outbound.bind(SetActiveScoreboardObjectiveEncoder.class, SetActiveScoreboardObjectivePacket.class);
        outbound.bind(EntityMetadataEncoder.class, EntityMetadataPacket.class);
        outbound.bind(); // TODO: Attach Entity
        outbound.bind(EntityVelocityEncoder.class, EntityVelocityPacket.class);
        outbound.bind(EntityEquipmentEncoder.class, EntityEquipmentPacket.class);
        outbound.bind(SetExperienceEncoder.class, SetExperiencePacket.class);
        outbound.bind(PlayerHealthEncoder.class, PlayerHealthPacket.class);
        final CodecRegistration<ScoreboardObjectivePacket, ScoreboardObjectiveEncoder> codecPlayOutScoreboardObjective = outbound.bind(
                ScoreboardObjectiveEncoder.class);
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
        outbound.bind(WorldTimeEncoder.class, WorldTimePacket.class);
        final CodecRegistration<TitlePacket, TitleEncoder> codecPlayOutTitle = outbound.bind(TitleEncoder.class);
        codecPlayOutTitle.bind(TitlePacket.Clear.class);
        codecPlayOutTitle.bind(TitlePacket.Reset.class);
        codecPlayOutTitle.bind(TitlePacket.SetSubtitle.class);
        codecPlayOutTitle.bind(TitlePacket.SetActionbarTitle.class);
        codecPlayOutTitle.bind(TitlePacket.SetTimes.class);
        codecPlayOutTitle.bind(TitlePacket.SetTitle.class);
        outbound.bind(EntitySoundEffectEncoder.class, EntitySoundEffectPacket.class);
        outbound.bind(SoundEffectEncoder.class, SoundEffectPacket.class);
        outbound.bind(StopSoundsEncoder.class, StopSoundsPacket.class);
        outbound.bind(TabListHeaderAndFooterEncoder.class, TabListHeaderAndFooterPacket.class);
        outbound.bind(DataResponseEncoder.class, DataResponsePacket.class);
        outbound.bind(EntityCollectItemEncoder.class, EntityCollectItemPacket.class);
        outbound.bind(EntityTeleportEncoder.class, EntityTeleportPacket.class);
        outbound.bind(AdvancementsEncoder.class, AdvancementsPacket.class);
        outbound.bind(); // TODO: Entity Properties
        outbound.bind(AddPotionEffectEncoder.class, AddPotionEffectPacket.class);
        outbound.bind(SetRecipesEncoder.class, SetRecipesPacket.class);
        outbound.bind(TagsEncoder.class, TagsPacket.class);
    }
}
