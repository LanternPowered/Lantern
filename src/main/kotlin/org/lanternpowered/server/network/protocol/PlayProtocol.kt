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
package org.lanternpowered.server.network.protocol

import org.lanternpowered.server.inventory.PlayerInventoryContainerSession
import org.lanternpowered.server.network.vanilla.packet.codec.DisconnectEncoder
import org.lanternpowered.server.network.vanilla.packet.codec.EncoderPlaceholder
import org.lanternpowered.server.network.vanilla.packet.codec.KeepAliveCodec
import org.lanternpowered.server.network.vanilla.packet.codec.play.*
import org.lanternpowered.server.network.vanilla.packet.handler.play.ChannelPayloadHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientAdvancementTreeHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientBlockPlacementHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientDiggingHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientEditBookHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientFinishUsingItemHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientFlyingStateHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientLockDifficultyHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientModifySignHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientMovementInputHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerLookHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerMovementAndLookHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerMovementHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerOnGroundStateHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerSwingArmHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerVehicleMovementHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientRecipeBookStateHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientRequestRespawnHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientRequestStatisticsHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSendChatMessageHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSetDifficultyHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSettingsHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSignBookHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSneakStateHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSprintStateHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientStartElytraFlyingHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSwapHandItemsHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientUseItemHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ContainerSessionForwardingHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientTabCompleteHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.RegisterChannelsHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ResourcePackStatusHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.UnregisterChannelsHandler
import org.lanternpowered.server.network.vanilla.packet.processor.play.ParticleEffectProcessor
import org.lanternpowered.server.network.vanilla.packet.processor.play.SetGameModeProcessor
import org.lanternpowered.server.network.vanilla.packet.processor.play.TabListProcessor
import org.lanternpowered.server.network.vanilla.packet.processor.play.TheEndMessageProcessor
import org.lanternpowered.server.network.vanilla.packet.processor.play.UpdateWorldSkyProcessor
import org.lanternpowered.server.network.vanilla.packet.type.DisconnectPacket
import org.lanternpowered.server.network.vanilla.packet.type.KeepAlivePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.*
import org.lanternpowered.server.network.vanilla.packet.type.play.internal.ChangeGameStatePacket

val PlayProtocol = protocol {
    inbound {
        bind().decoder(ClientConfirmTeleportDecoder)
        bind().decoder(ClientRequestBlockDataDecoder)
        bind().decoder(ClientSetDifficultyDecoder)
        bind().decoder(ClientSendChatMessageDecoder)
        bind().decoder(ClientStatusDecoder)
        bind().decoder(ClientSettingsDecoder)
        bind().decoder(ClientTabCompleteDecoder)
        bind().decoder(ConfirmWindowTransactionCodec)
        bind().decoder(ClientClickWindowButtonDecoder)
        bind().decoder(ClientClickWindowDecoder)
        bind().decoder(CloseWindowCodec)
        bind().decoder(ChannelPayloadCodec)
        bind().decoder(ClientModifyBookDecoder)
        bind().decoder(ClientRequestEntityDataDecoder)
        bind().decoder(ClientUseEntityDecoder)
        bind().decoder(GenerateJigsawStructureDecoder)
        bind().decoder(KeepAliveCodec)
        bind().decoder(ClientLockDifficultyDecoder)
        bind().decoder(ClientPlayerMovementDecoder)
        bind().decoder(ClientPlayerMovementAndLookDecoder)
        bind().decoder(ClientPlayerLookDecoder)
        bind().decoder(ClientPlayerOnGroundStateDecoder)
        bind().decoder(ClientPlayerVehicleMovementDecoder)
        bind().decoder(ClientBoatControlsDecoder)
        bind().decoder(ClientPickItemDecoder)
        bind().decoder(ClientClickRecipeDecoder)
        bind().decoder(ClientFlyingStateDecoder)
        bind().decoder(ClientDiggingDecoder)
        bind().decoder(ClientPlayerActionDecoder)
        bind().decoder(ClientVehicleControlsDecoder)
        bind().decoder(ClientSetDisplayedRecipeDecoder)
        bind().decoder(ClientRecipeBookStateDecoder)
        bind().decoder(ClientItemRenameDecoder)
        bind().decoder(ResourcePackStatusDecoder)
        bind().decoder(ChangeAdvancementTreeDecoder)
        bind().decoder(ClientChangeTradeOfferDecoder)
        bind().decoder(ClientAcceptBeaconEffectsDecoder)
        bind().decoder(PlayerHeldItemChangeCodec)
        bind().decoder(ClientEditCommandBlockBlockDecoder)
        bind().decoder(ClientEditCommandBlockEntityDecoder)
        bind().decoder(ClientCreativeWindowActionDecoder)
        bind().decoder(ClientUpdateJigsawBlockDecoder)
        bind().decoder(ClientUpdateStructureBlockDecoder)
        bind().decoder(ClientModifySignDecoder)
        bind().decoder(ClientPlayerSwingArmDecoder)
        bind().decoder(ClientSpectateDecoder)
        bind().decoder(ClientBlockPlacementDecoder)
        bind().decoder(ClientUseItemDecoder)

        type(ClientConfirmTeleportPacket::class) // TODO: Handler
        type(ClientRequestDataPacket.Block::class) // TODO: Handler
        type(ClientSetDifficultyPacket::class).handler(ClientSetDifficultyHandler)
        type(ClientSendChatMessagePacket::class).handler(ClientSendChatMessageHandler)
        type(ClientRequestRespawnPacket::class).handler(ClientRequestRespawnHandler)
        type(ClientRequestStatisticsPacket::class).handler(ClientRequestStatisticsHandler)
        type(ClientSettingsPacket::class).handler(ClientSettingsHandler)
        type(ClientTabCompletePacket::class).handler(ClientTabCompleteHandler)
        type(ChannelPayloadPacket::class).handler(ChannelPayloadHandler)
        type(RegisterChannelsPacket::class).handler(RegisterChannelsHandler)
        type(UnregisterChannelsPacket::class).handler(UnregisterChannelsHandler)
        type(BrandPacket::class) // TODO: Handler
        type(ClientModifyBookPacket.Edit::class).handler(ClientEditBookHandler)
        type(ClientModifyBookPacket.Sign::class).handler(ClientSignBookHandler)
        type(ClientRequestDataPacket.Entity::class) // TODO: Handler
        type(ClientUseEntityPacket::class) // TODO: Handler
        type(GenerateJigsawStructurePacket::class) // TODO: Handler
        type(KeepAlivePacket::class)
        type(ClientLockDifficultyPacket::class).handler(ClientLockDifficultyHandler)
        type(ClientPlayerMovementPacket::class).handler(ClientPlayerMovementHandler)
        type(ClientPlayerMovementAndLookPacket::class).handler(ClientPlayerMovementAndLookHandler)
        type(ClientPlayerLookPacket::class).handler(ClientPlayerLookHandler)
        type(ClientPlayerOnGroundStatePacket::class).handler(ClientPlayerOnGroundStateHandler)
        type(ClientPlayerVehicleMovementPacket::class).handler(ClientPlayerVehicleMovementHandler)
        type(ClientFlyingStatePacket::class).handler(ClientFlyingStateHandler)
        type(ClientDiggingPacket::class).handler(ClientDiggingHandler)
        type(ClientDropHeldItemPacket::class).handler(ContainerSessionForwardingHandler(PlayerInventoryContainerSession::handleItemDrop))
        type(FinishUsingItemPacket::class).handler(ClientFinishUsingItemHandler)
        type(ClientSwapHandItemsPacket::class).handler(ClientSwapHandItemsHandler)
        type(ClientLeaveBedPacket::class) // TODO: Handler
        type(ClientStartElytraFlyingPacket::class).handler(ClientStartElytraFlyingHandler)
        type(ClientSneakStatePacket::class).handler(ClientSneakStateHandler)
        type(ClientSprintStatePacket::class).handler(ClientSprintStateHandler)
        type(ClientVehicleJumpPacket::class) // TODO: Handler
        type(ClientMovementInputPacket::class).handler(ClientMovementInputHandler)
        type(ClientRecipeBookStatePacket::class).handler(ClientRecipeBookStateHandler)
        type(ResourcePackStatusPacket::class).handler(ResourcePackStatusHandler)
        type(ChangeAdvancementTreePacket.Open::class).handler(ClientAdvancementTreeHandler)
        type(ChangeAdvancementTreePacket.Close::class).handler(ClientAdvancementTreeHandler)
        type(ClientEditCommandBlockPacket.Block::class) // TODO: Handler
        type(ClientEditCommandBlockPacket.Entity::class) // TODO: Handler
        type(ClientUpdateJigsawBlockPacket::class) // TODO: Handler
        type(ClientUpdateStructureBlockPacket::class) // TODO: Handler
        type(ClientModifySignPacket::class).handler(ClientModifySignHandler)
        type(ClientPlayerSwingArmPacket::class).handler(ClientPlayerSwingArmHandler)
        type(ClientSpectatePacket::class) // TODO: Handler
        type(ClientBlockPlacementPacket::class).handler(ClientBlockPlacementHandler)
        type(ClientUseItemPacket::class).handler(ClientUseItemHandler)
        type(ClientBoatControlsPacket::class) // TODO: Handler

        type(ConfirmWindowTransactionPacket::class) // TODO: Handler
        type(ClientClickWindowButtonPacket::class)
                .handler(ContainerSessionForwardingHandler(PlayerInventoryContainerSession::handleEnchantItem))
        type(ClientClickWindowPacket::class)
                .handler(ContainerSessionForwardingHandler(PlayerInventoryContainerSession::handleWindowClick))
        type(CloseWindowPacket::class)
                .handler(ContainerSessionForwardingHandler(PlayerInventoryContainerSession::handleWindowClose))
        type(ClientPickItemPacket::class)
                .handler(ContainerSessionForwardingHandler(PlayerInventoryContainerSession::handlePickItem))
        type(ClientClickRecipePacket::class)
                .handler(ContainerSessionForwardingHandler(PlayerInventoryContainerSession::handleRecipeClick))
        type(ClientSetDisplayedRecipePacket::class)
                .handler(ContainerSessionForwardingHandler(PlayerInventoryContainerSession::handleDisplayedRecipe))
        type(ClientItemRenamePacket::class)
                .handler(ContainerSessionForwardingHandler(PlayerInventoryContainerSession::handleItemRename))
        type(ClientChangeTradeOfferPacket::class)
                .handler(ContainerSessionForwardingHandler(PlayerInventoryContainerSession::handleOfferChange))
        type(ClientAcceptBeaconEffectsPacket::class)
                .handler(ContainerSessionForwardingHandler(PlayerInventoryContainerSession::handleAcceptBeaconEffects))
        type(PlayerHeldItemChangePacket::class)
                .handler(ContainerSessionForwardingHandler(PlayerInventoryContainerSession::handleHeldItemChange))
        type(ClientCreativeWindowActionPacket::class)
                .handler(ContainerSessionForwardingHandler(PlayerInventoryContainerSession::handleWindowCreativeClick))
    }
    outbound {
        // With processors
        type(TheEndPacket::class).processor(TheEndMessageProcessor)
        type(ParticleEffectPacket::class).processor(ParticleEffectProcessor)
        type(SetGameModePacket::class).processor(SetGameModeProcessor)
        type(UpdateWorldSkyPacket::class).processor(UpdateWorldSkyProcessor)
        type(TabListPacket::class).processor(TabListProcessor)

        // With opcodes
        bind().encoder(SpawnObjectEncoder).accept(SpawnObjectPacket::class)
        bind().encoder(SpawnExperienceOrbEncoder).accept(SpawnExperienceOrbPacket::class)
        bind().encoder(SpawnMobEncoder).accept(SpawnMobPacket::class)
        bind().encoder(SpawnPaintingEncoder).accept(SpawnPaintingPacket::class)
        bind().encoder(SpawnPlayerEncoder).accept(SpawnPlayerPacket::class)
        bind().encoder(EntityAnimationEncoder).accept(EntityAnimationPacket::class)
        bind().encoder(StatisticsEncoder).accept(StatisticsPacket::class)
        bind().encoder(BlockBreakAnimationEncoder).accept(BlockBreakAnimationPacket::class)
        bind().encoder(UpdateBlockEntityEncoder).accept(UpdateBlockEntityPacket::class)
        bind().encoder(BlockActionEncoder).accept(BlockActionPacket::class)
        bind().encoder(BlockChangeEncoder).accept(BlockChangePacket::class)
        bind().encoder(BossBarEncoder).acceptAll(BossBarPacket.Add::class,
                BossBarPacket.Remove::class,
                BossBarPacket.UpdateFlags::class,
                BossBarPacket.UpdateName::class,
                BossBarPacket.UpdatePercent::class,
                BossBarPacket.UpdateStyle::class
        )
        bind().encoder(SetDifficultyEncoder).accept(SetDifficultyPacket::class)
        bind().encoder(ChatMessageEncoder).accept(ChatMessagePacket::class)
        bind().encoder(MultiBlockChangeEncoder).accept(MultiBlockChangePacket::class)
        bind().encoder(TabCompleteEncoder).accept(TabCompletePacket::class)
        bind().encoder(SetCommandsEncoder).accept(SetCommandsPacket::class)
        bind().encoder(ConfirmWindowTransactionCodec).accept(ConfirmWindowTransactionPacket::class)
        bind().encoder(CloseWindowCodec).accept(CloseWindowPacket::class)
        bind().encoder(SetWindowItemsEncoder).accept(SetWindowItemsPacket::class)
        bind().encoder(SetWindowPropertyEncoder).accept(SetWindowPropertyPacket::class)
        bind().encoder(SetWindowSlotEncoder).accept(SetWindowSlotPacket::class)
        bind().encoder(SetCooldownEncoder).accept(SetCooldownPacket::class)
        bind().encoder(ChannelPayloadCodec).acceptAll(ChannelPayloadPacket::class, BrandPacket::class)
        bind().encoder(NamedSoundEffectEncoder).accept(NamedSoundEffectPacket::class)
        bind().encoder(DisconnectEncoder).accept(DisconnectPacket::class)
        bind().encoder(EntityStatusEncoder).acceptAll(EntityStatusPacket::class,
                SetOpLevelPacket::class,
                SetReducedDebugPacket::class,
                FinishUsingItemPacket::class
        )
        bind().encoder(ChunkUnloadEncoder).accept(ChunkPacket.Unload::class)
        bind().encoder(ChangeGameStateEncoder).accept(ChangeGameStatePacket::class)
        bind().encoder(OpenHorseWindowEncoder).accept(OpenHorseWindowPacket::class)
        bind().encoder(KeepAliveCodec).accept(KeepAlivePacket::class)
        bind().encoder(ChunkInitOrUpdateEncoder).acceptAll(ChunkPacket.Init::class, ChunkPacket.Update::class)
        bind().encoder(EffectEncoder).acceptAll(EffectPacket::class, SetMusicDiscPacket::class)
        bind().encoder(SpawnParticleEncoder).accept(SpawnParticlePacket::class)
        bind().encoder(UpdateLightEncoder).accept(UpdateLightPacket::class)
        bind().encoder(PlayerJoinEncoder).accept(PlayerJoinPacket::class)
        bind().encoder(EncoderPlaceholder).accept(MapPacket::class)
        bind().encoder(SetWindowTradeOffersEncoder).accept(SetWindowTradeOffersPacket::class)
        bind().encoder(EntityRelativeMoveEncoder).accept(EntityRelativeMovePacket::class)
        bind().encoder(EntityLookAndRelativeMoveEncoder).accept(EntityLookAndRelativeMovePacket::class)
        bind().encoder(EntityLookEncoder).accept(EntityLookPacket::class)
        bind() // "Entity" packet, not used
        bind().encoder(EncoderPlaceholder).accept(VehicleMovePacket::class)
        bind().encoder(OpenSignEncoder).accept(OpenSignPacket::class)
        bind().encoder(OpenWindowEncoder).accept(OpenWindowPacket::class)
        bind().encoder(OpenBookEncoder).accept(OpenBookPacket::class)
        bind().encoder(EncoderPlaceholder).accept(CombatEventPacket::class)
        bind().encoder(TabListEncoder).accept(TabListPacket::class)
        bind().encoder(PlayerFaceAtEncoder).acceptAll(
                PlayerFaceAtPacket.Entity::class,
                PlayerFaceAtPacket.Position::class
        )
        bind().encoder(PlayerPositionAndLookEncoder).accept(PlayerPositionAndLookPacket::class)
        bind().encoder(UnlockRecipesEncoder).acceptAll(
                UnlockRecipesPacket.Add::class,
                UnlockRecipesPacket.Init::class,
                UnlockRecipesPacket.Remove::class
        )
        bind().encoder(DestroyEntitiesEncoder).accept(DestroyEntitiesPacket::class)
        bind().encoder(RemovePotionEffectEncoder).accept(RemovePotionEffectPacket::class)
        bind().encoder(SetResourcePackEncoder).accept(SetResourcePackPacket::class)
        bind().encoder(PlayerRespawnEncoder).accept(PlayerRespawnPacket::class)
        bind().encoder(EntityHeadLookEncoder).accept(EntityHeadLookPacket::class)
        bind().encoder(SetActiveAdvancementTreeEncoder).accept(SetActiveAdvancementTreePacket::class)
        bind().encoder(WorldBorderEncoder).acceptAll(
                WorldBorderPacket.Init::class,
                WorldBorderPacket.UpdateCenter::class,
                WorldBorderPacket.UpdateDiameter::class,
                WorldBorderPacket.UpdateLerpedDiameter::class,
                WorldBorderPacket.UpdateWarningDistance::class,
                WorldBorderPacket.UpdateWarningTime::class
        )
        bind().encoder(SetCameraEncoder).accept(SetCameraPacket::class)
        bind().encoder(PlayerHeldItemChangeCodec).accept(PlayerHeldItemChangePacket::class)
        bind().encoder(UpdateViewPositionEncoder).accept(UpdateViewPositionPacket::class)
        bind().encoder(UpdateViewDistanceEncoder).accept(UpdateViewDistancePacket::class)
        bind().encoder(PlayerSpawnPositionEncoder).accept(PlayerSpawnPositionPacket::class)
        bind().encoder(SetActiveScoreboardObjectiveEncoder).accept(SetActiveScoreboardObjectivePacket::class)
        bind().encoder(EntityMetadataEncoder).accept(EntityMetadataPacket::class)
        bind().encoder(EncoderPlaceholder).accept(AttachEntityPacket::class)
        bind().encoder(EntityVelocityEncoder).accept(EntityVelocityPacket::class)
        bind().encoder(EntityEquipmentEncoder).accept(EntityEquipmentPacket::class)
        bind().encoder(SetExperienceEncoder).accept(SetExperiencePacket::class)
        bind().encoder(PlayerHealthEncoder).accept(PlayerHealthPacket::class)
        bind().encoder(ScoreboardObjectiveEncoder).acceptAll(
                ScoreboardObjectivePacket.Create::class,
                ScoreboardObjectivePacket.Remove::class,
                ScoreboardObjectivePacket.Update::class
        )
        bind().encoder(SetEntityPassengersCodec).accept(SetEntityPassengersPacket::class)
        bind().encoder(TeamEncoder).acceptAll(
                TeamPacket.AddMembers::class,
                TeamPacket.Create::class,
                TeamPacket.Update::class,
                TeamPacket.Remove::class,
                TeamPacket.RemoveMembers::class
        )
        bind().encoder(ScoreboardScoreEncoder).acceptAll(
                ScoreboardScorePacket.CreateOrUpdate::class,
                ScoreboardScorePacket.Remove::class
        )
        bind().encoder(WorldTimeEncoder).accept(WorldTimePacket::class)
        bind().encoder(TitleEncoder).acceptAll(
                TitlePacket.SetActionbarTitle::class,
                TitlePacket.SetSubtitle::class,
                TitlePacket.SetTitle::class,
                TitlePacket.SetTimes::class,
                TitlePacket.Clear::class,
                TitlePacket.Reset::class
        )
        bind().encoder(EntitySoundEffectEncoder).accept(EntitySoundEffectPacket::class)
        bind().encoder(SoundEffectEncoder).accept(SoundEffectPacket::class)
        bind().encoder(StopSoundsEncoder).accept(StopSoundsPacket::class)
        bind().encoder(TabListHeaderAndFooterEncoder).accept(TabListHeaderAndFooterPacket::class)
        bind().encoder(DataResponseEncoder).accept(DataResponsePacket::class)
        bind().encoder(EntityCollectItemEncoder).accept(EntityCollectItemPacket::class)
        bind().encoder(EntityTeleportEncoder).accept(EntityTeleportPacket::class)
        bind().encoder(AdvancementsEncoder).accept(AdvancementsPacket::class)
        bind().encoder(EncoderPlaceholder).accept(EntityPropertiesPacket::class)
        bind().encoder(AddPotionEffectEncoder).accept(AddPotionEffectPacket::class)
        bind().encoder(SetRecipesEncoder).accept(SetRecipesPacket::class)
        bind().encoder(TagsEncoder).accept(TagsPacket::class)
    }
}
