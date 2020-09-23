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
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientEditBookHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientLockDifficultyHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerLookHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerMovementAndLookHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerMovementHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerOnGroundStateHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientPlayerVehicleMovementHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSendChatMessageHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSetDifficultyHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSettingsHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ClientSignBookHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.ContainerSessionForwardingHandler
import org.lanternpowered.server.network.vanilla.packet.handler.play.HandlerPlayInTabComplete
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
        bind().type(ClientConfirmTeleportPacket::class).decoder(ClientConfirmTeleportDecoder) // TODO: Handler
        bind().type(ClientRequestDataPacket.Block::class).decoder(ClientRequestBlockDataDecoder) // TODO: Handler
        bind().type(ClientSetDifficultyPacket::class).decoder(ClientSetDifficultyDecoder)
                .handler(ClientSetDifficultyHandler)
        bind().type(ClientSendChatMessagePacket::class).decoder(ClientSendChatMessageDecoder)
                .handler(ClientSendChatMessageHandler)
        bind().decoder(ClientStatusDecoder)
        type(ClientSetDifficultyPacket::class).handler(ClientSetDifficultyHandler)
        type(ClientSendChatMessagePacket::class).handler(ClientSendChatMessageHandler)
        bind().type(ClientSettingsPacket::class).decoder(ClientSettingsDecoder)
                .handler(ClientSettingsHandler)
        bind().type(ClientTabCompletePacket::class).decoder(ClientTabCompleteDecoder)
                .handler(HandlerPlayInTabComplete()) // TODO: Update handler
        bind().type(ConfirmWindowTransactionPacket::class).decoder(ConfirmWindowTransactionCodec) // TODO: Handler
        bind().type(ClientClickWindowButtonPacket::class).decoder(ClientClickWindowButtonDecoder)
                .handler(ContainerSessionForwardingHandler(PlayerInventoryContainerSession::handleEnchantItem))
        bind().type(ClientClickWindowPacket::class).decoder(ClientClickWindowDecoder)
                .handler(ContainerSessionForwardingHandler(PlayerInventoryContainerSession::handleWindowClick))
        bind().type(CloseWindowPacket::class).decoder(CloseWindowCodec)
                .handler(ContainerSessionForwardingHandler(PlayerInventoryContainerSession::handleWindowClose))
        bind().decoder(ChannelPayloadCodec)
        type(ChannelPayloadPacket::class).handler(ChannelPayloadHandler)
        bind().decoder(ClientModifyBookDecoder)
        type(ClientModifyBookPacket.Edit::class).handler(ClientEditBookHandler)
        type(ClientModifyBookPacket.Sign::class).handler(ClientSignBookHandler)
        bind().type(ClientRequestDataPacket.Entity::class).decoder(ClientRequestEntityDataDecoder) // TODO: Handler
        bind().type(ClientUseEntityPacket::class).decoder(ClientUseEntityDecoder) // TODO: Handler
        bind().type(GenerateJigsawStructurePacket::class).decoder(GenerateJigsawStructureDecoder) // TODO: Handler
        bind().type(KeepAlivePacket::class).decoder(KeepAliveCodec)
        bind().type(ClientLockDifficultyPacket::class).decoder(ClientLockDifficultyDecoder)
                .handler(ClientLockDifficultyHandler)
        bind().type(ClientPlayerMovementPacket::class).decoder(ClientPlayerMovementDecoder)
                .handler(ClientPlayerMovementHandler)
        bind().type(ClientPlayerMovementAndLookPacket::class).decoder(ClientPlayerMovementAndLookDecoder)
                .handler(ClientPlayerMovementAndLookHandler)
        bind().type(ClientPlayerLookPacket::class).decoder(ClientPlayerLookDecoder)
                .handler(ClientPlayerLookHandler)
        bind().type(ClientPlayerOnGroundStatePacket::class).decoder(ClientPlayerOnGroundStateDecoder)
                .handler(ClientPlayerOnGroundStateHandler)
        bind().type(ClientPlayerVehicleMovementPacket::class).decoder(ClientPlayerVehicleMovementDecoder)
                .handler(ClientPlayerVehicleMovementHandler)
    }
    outbound {
        // With processors
        type(TheEndPacket::class).processor(TheEndMessageProcessor)
        type(ParticleEffectPacket::class).processor(ParticleEffectProcessor)
        type(SetGameModePacket::class).processor(SetGameModeProcessor)
        type(UpdateWorldSkyPacket::class).processor(UpdateWorldSkyProcessor)
        type(TabListPacket::class).processor(TabListProcessor)

        // With opcodes
        bind().type(SpawnObjectPacket::class).encoder(SpawnObjectEncoder)
        bind().type(SpawnExperienceOrbPacket::class).encoder(SpawnExperienceOrbEncoder)
        bind().type(SpawnMobPacket::class).encoder(SpawnMobEncoder)
        bind().type(SpawnPaintingPacket::class).encoder(SpawnPaintingEncoder)
        bind().type(SpawnPlayerPacket::class).encoder(SpawnPlayerEncoder)
        bind().type(EntityAnimationPacket::class).encoder(EntityAnimationEncoder)
        bind().type(StatisticsPacket::class).encoder(StatisticsEncoder)
        bind().type(BlockBreakAnimationPacket::class).encoder(BlockBreakAnimationEncoder)
        bind().type(UpdateBlockEntityPacket::class).encoder(UpdateBlockEntityEncoder)
        bind().type(BlockActionPacket::class).encoder(BlockActionEncoder)
        bind().type(BlockChangePacket::class).encoder(BlockChangeEncoder)
        bind().types(BossBarPacket.Add::class,
                BossBarPacket.Remove::class,
                BossBarPacket.UpdateFlags::class,
                BossBarPacket.UpdateName::class,
                BossBarPacket.UpdatePercent::class,
                BossBarPacket.UpdateStyle::class
        ).encoder(BossBarEncoder)
        bind().type(SetDifficultyPacket::class).encoder(SetDifficultyEncoder)
        bind().type(ChatMessagePacket::class).encoder(ChatMessageEncoder)
        bind().type(PacketPlayOutMultiBlockChange::class).encoder(CodecPlayOutMultiBlockChange()) // TODO
        bind().type(TabCompletePacket::class).encoder(TabCompleteEncoder)
        bind().type(SetCommandsPacket::class).encoder(SetCommandsEncoder)
        bind().type(ConfirmWindowTransactionPacket::class).encoder(ConfirmWindowTransactionCodec)
        bind().type(CloseWindowPacket::class).encoder(CloseWindowCodec)
        bind().type(SetWindowItemsPacket::class).encoder(SetWindowItemsEncoder)
        bind().type(SetWindowPropertyPacket::class).encoder(SetWindowPropertyEncoder)
        bind().type(SetWindowSlotPacket::class).encoder(SetWindowSlotEncoder)
        bind().type(SetCooldownPacket::class).encoder(SetCooldownEncoder)
        bind().types(ChannelPayloadPacket::class, BrandPacket::class).encoder(ChannelPayloadCodec)
        bind().type(NamedSoundEffectPacket::class).encoder(NamedSoundEffectEncoder)
        bind().type(DisconnectPacket::class).encoder(DisconnectEncoder)
        bind().types(EntityStatusPacket::class,
                SetOpLevelPacket::class,
                SetReducedDebugPacket::class,
                FinishUsingItemPacket::class
        ).encoder(EntityStatusEncoder)
        bind().type(ChunkPacket.Unload::class).encoder(ChunkUnloadEncoder)
        bind().type(ChangeGameStatePacket::class).encoder(ChangeGameStateEncoder)
        bind().type(OpenHorseWindowPacket::class).encoder(OpenHorseWindowEncoder)
        bind().type(KeepAlivePacket::class).encoder(KeepAliveCodec)
        bind().types(ChunkPacket.Init::class, ChunkPacket.Update::class).encoder(ChunkInitOrUpdateEncoder)
        bind().types(EffectPacket::class, SetMusicDiscPacket::class).encoder(EffectEncoder)
        bind().type(SpawnParticlePacket::class).encoder(SpawnParticleEncoder)
        bind().type(UpdateLightPacket::class).encoder(UpdateLightEncoder)
        bind().type(PlayerJoinPacket::class).encoder(PlayerJoinEncoder)
        bind().type(MapPacket::class).encoder(EncoderPlaceholder)
        bind().type(SetWindowTradeOffersPacket::class).encoder(SetWindowTradeOffersEncoder)
        bind().type(EntityRelativeMovePacket::class).encoder(EntityRelativeMoveEncoder)
        bind().type(EntityLookAndRelativeMovePacket::class).encoder(EntityLookAndRelativeMoveEncoder)
        bind().type(EntityLookPacket::class).encoder(EntityLookEncoder)
        bind() // "Entity" packet, not used
        bind().type(VehicleMovePacket::class).encoder(EncoderPlaceholder)
        bind().type(OpenSignPacket::class).encoder(OpenSignEncoder)
        bind().type(OpenWindowPacket::class).encoder(OpenWindowEncoder)
        bind().type(OpenBookPacket::class).encoder(OpenBookEncoder)
        bind().type(CombatEventPacket::class).encoder(EncoderPlaceholder)
        bind().type(TabListPacket::class).encoder(TabListEncoder)
        bind().types(PlayerFaceAtPacket.Entity::class, PlayerFaceAtPacket.Position::class).encoder(PlayerFaceAtEncoder)
        bind().type(PlayerPositionAndLookPacket::class).encoder(PlayerPositionAndLookEncoder)
        bind().types(UnlockRecipesPacket.Add::class,
                UnlockRecipesPacket.Init::class,
                UnlockRecipesPacket.Remove::class
        ).encoder(UnlockRecipesEncoder)
        bind().type(DestroyEntitiesPacket::class).encoder(DestroyEntitiesEncoder)
        bind().type(RemovePotionEffectPacket::class).encoder(RemovePotionEffectEncoder)
        bind().type(SetResourcePackPacket::class).encoder(SetResourcePackEncoder)
        bind().type(PlayerRespawnPacket::class).encoder(PlayerRespawnEncoder)
        bind().type(EntityHeadLookPacket::class).encoder(EntityHeadLookEncoder)
        bind().type(SetActiveAdvancementTreePacket::class).encoder(SetActiveAdvancementTreeEncoder)
        bind().types(WorldBorderPacket.Init::class,
                WorldBorderPacket.UpdateCenter::class,
                WorldBorderPacket.UpdateDiameter::class,
                WorldBorderPacket.UpdateLerpedDiameter::class,
                WorldBorderPacket.UpdateWarningDistance::class,
                WorldBorderPacket.UpdateWarningTime::class
        ).encoder(WorldBorderEncoder)
        bind().type(SetCameraPacket::class).encoder(SetCameraEncoder)
        bind().type(PlayerHeldItemChangePacket::class).encoder(PlayerHeldItemChangeEncoder)
        bind().type(UpdateViewPositionPacket::class).encoder(UpdateViewPositionEncoder)
        bind().type(UpdateViewDistancePacket::class).encoder(UpdateViewDistanceEncoder)
        bind().type(PlayerSpawnPositionPacket::class).encoder(PlayerSpawnPositionEncoder)
        bind().type(SetActiveScoreboardObjectivePacket::class).encoder(SetActiveScoreboardObjectiveEncoder)
        bind().type(EntityMetadataPacket::class).encoder(EntityMetadataEncoder)
        bind().type(AttachEntityPacket::class).encoder(EncoderPlaceholder)
        bind().type(EntityVelocityPacket::class).encoder(EntityVelocityEncoder)
        bind().type(EntityEquipmentPacket::class).encoder(EntityEquipmentEncoder)
        bind().type(SetExperiencePacket::class).encoder(SetExperienceEncoder)
        bind().type(PlayerHealthPacket::class).encoder(PlayerHealthEncoder)
        bind().types(ScoreboardObjectivePacket.Create::class,
                ScoreboardObjectivePacket.Remove::class,
                ScoreboardObjectivePacket.Update::class
        ).encoder(ScoreboardObjectiveEncoder)
        bind().type(SetEntityPassengersPacket::class).encoder(SetEntityPassengersCodec)
        bind().types(TeamPacket.AddMembers::class,
                TeamPacket.Create::class,
                TeamPacket.Update::class,
                TeamPacket.Remove::class,
                TeamPacket.RemoveMembers::class
        ).encoder(TeamEncoder)
        bind().types(ScoreboardScorePacket.CreateOrUpdate::class,
                ScoreboardScorePacket.Remove::class
        ).encoder(ScoreboardScoreEncoder)
        bind().type(WorldTimePacket::class).encoder(WorldTimeEncoder)
        bind().types(TitlePacket.SetActionbarTitle::class,
                TitlePacket.SetSubtitle::class,
                TitlePacket.SetTitle::class,
                TitlePacket.SetTimes::class,
                TitlePacket.Clear::class,
                TitlePacket.Reset::class
        ).encoder(TitleEncoder)
        bind().type(EntitySoundEffectPacket::class).encoder(EntitySoundEffectEncoder)
        bind().type(SoundEffectPacket::class).encoder(SoundEffectEncoder)
        bind().type(StopSoundsPacket::class).encoder(StopSoundsEncoder)
        bind().type(TabListHeaderAndFooterPacket::class).encoder(TabListHeaderAndFooterEncoder)
        bind().type(DataResponsePacket::class).encoder(DataResponseEncoder)
        bind().type(EntityCollectItemPacket::class).encoder(EntityCollectItemEncoder)
        bind().type(EntityTeleportPacket::class).encoder(EntityTeleportEncoder)
        bind().type(AdvancementsPacket::class).encoder(AdvancementsEncoder)
        bind().type(EntityPropertiesPacket::class).encoder(EncoderPlaceholder)
        bind().type(AddPotionEffectPacket::class).encoder(AddPotionEffectEncoder)
        bind().type(SetRecipesPacket::class).encoder(SetRecipesEncoder)
        bind().type(TagsPacket::class).encoder(TagsEncoder)
    }
}
