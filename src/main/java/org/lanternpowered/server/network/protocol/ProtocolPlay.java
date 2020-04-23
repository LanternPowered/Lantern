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
import org.lanternpowered.server.network.message.CodecRegistration;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.MessageRegistry;
import org.lanternpowered.server.network.vanilla.message.codec.connection.KeepAliveCodec;
import org.lanternpowered.server.network.vanilla.message.codec.connection.DisconnectCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInAcceptBeaconEffects;
import org.lanternpowered.server.network.vanilla.message.codec.play.ClientAdvancementTreeCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.ClientItemRenameCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.ClientChangeTradeOfferCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInChangeSign;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInChatMessage;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInClickRecipe;
import org.lanternpowered.server.network.vanilla.message.codec.play.ClientClickWindowCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.ClientSettingsCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInClientStatus;
import org.lanternpowered.server.network.vanilla.message.codec.play.ClientRecipeBookCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInCreativeWindowAction;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInDataRequestBlock;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInDataRequestEntity;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInEditCommandBlockBlock;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInEditCommandBlockEntity;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInEnchantItem;
import org.lanternpowered.server.network.vanilla.message.codec.play.ClientLocksDifficultyCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInModifyBook;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInOutCloseWindow;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInOutConfirmWindowTransaction;
import org.lanternpowered.server.network.vanilla.message.codec.play.ChannelPayloadCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInOutHeldItemChange;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPickItem;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPlayerAbilities;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPlayerAction;
import org.lanternpowered.server.network.vanilla.message.codec.play.ClientBlockPlacementCodec;
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
import org.lanternpowered.server.network.vanilla.message.codec.play.ClientSetDifficultyCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInSpectate;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInTabComplete;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInTeleportConfirm;
import org.lanternpowered.server.network.vanilla.message.codec.play.UpdateJigsawBlockMessageCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInUseEntity;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutAddPotionEffect;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutAdvancements;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutBlockAction;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutBlockBreakAnimation;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutBlockChange;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutBossBar;
import org.lanternpowered.server.network.vanilla.message.codec.play.ChangeGameStateCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutChatMessage;
import org.lanternpowered.server.network.vanilla.message.codec.play.ChunkCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutDataResponse;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutDefineCommands;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutDefineRecipes;
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
import org.lanternpowered.server.network.vanilla.message.codec.play.EntitySoundEffectCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityStatus;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityTeleport;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityVelocity;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutFaceAt;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutMultiBlockChange;
import org.lanternpowered.server.network.vanilla.message.codec.play.NamedSoundEffectCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.OpenBookCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.OpenHorseWindowCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutOpenSign;
import org.lanternpowered.server.network.vanilla.message.codec.play.OpenWindowCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerAbilities;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerHealthUpdate;
import org.lanternpowered.server.network.vanilla.message.codec.play.PlayerJoinCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerPositionAndLook;
import org.lanternpowered.server.network.vanilla.message.codec.play.PlayerRespawnCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerSpawnPosition;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutRemovePotionEffect;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutScoreboardDisplayObjective;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutScoreboardObjective;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutScoreboardScore;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSelectAdvancementTree;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSendResourcePack;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSetCamera;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSetCooldown;
import org.lanternpowered.server.network.vanilla.message.codec.play.SetDifficultyCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSetEntityPassengers;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSetExperience;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSetWindowSlot;
import org.lanternpowered.server.network.vanilla.message.codec.play.SoundEffectCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSpawnExperienceOrb;
import org.lanternpowered.server.network.vanilla.message.codec.play.SpawnMobCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSpawnObject;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSpawnPainting;
import org.lanternpowered.server.network.vanilla.message.codec.play.SpawnParticleCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.SpawnPlayerCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSpawnThunderbolt;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutStatistics;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutStopSounds;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutTabComplete;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutTabListHeaderAndFooter;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutTags;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutTeams;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutTitle;
import org.lanternpowered.server.network.vanilla.message.codec.play.SetWindowTradeOffersCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutUnloadChunk;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutUnlockRecipes;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutUpdateBlockEntity;
import org.lanternpowered.server.network.vanilla.message.codec.play.UpdateLightCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.UpdateViewDistanceCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.UpdateViewPositionCodec;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutWindowItems;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutWindowProperty;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutWorldBorder;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutWorldTime;
import org.lanternpowered.server.network.vanilla.message.handler.play.ClientLockDifficultyHandler;
import org.lanternpowered.server.network.vanilla.message.handler.play.ClientSetDifficultyHandler;
import org.lanternpowered.server.network.vanilla.message.handler.play.ClientAdvancementTreeHandler;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInChangeSign;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInChannelPayload;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInChatMessage;
import org.lanternpowered.server.network.vanilla.message.handler.play.ClientSettingsHandler;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInContainerSessionForwarding;
import org.lanternpowered.server.network.vanilla.message.handler.play.ClientRecipeBookStatesHandler;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInEditBook;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInFinishUsingItem;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInPlayerAbilities;
import org.lanternpowered.server.network.vanilla.message.handler.play.ClientBlockPlacementHandler;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInPlayerDigging;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInPlayerLook;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInPlayerMovement;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInPlayerMovementAndLook;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInPlayerMovementInput;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInPlayerOnGroundState;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInPlayerSneak;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInPlayerSprint;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInPlayerSwingArm;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInPlayerUseItem;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInPlayerVehicleMovement;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInRequestStatistics;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInResourcePackStatus;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInSignBook;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInStartElytraFlying;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInSwapHandItems;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInTabComplete;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInUnregisterChannels;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInUseEntityAttack;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInUseEntityInteract;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.message.processor.play.SetGameModeProcessor;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.message.processor.play.TheEndMessageProcessor;
import org.lanternpowered.server.network.vanilla.message.processor.play.UpdateWorldSkyProcessor;
import org.lanternpowered.server.network.vanilla.message.type.KeepAliveMessage;
import org.lanternpowered.server.network.vanilla.message.type.DisconnectMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInAcceptBeaconEffects;
import org.lanternpowered.server.network.vanilla.message.type.play.ClientAdvancementTreeMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.ClientItemRenameMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.ClientChangeTradeOfferMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeSign;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChatMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClickRecipe;
import org.lanternpowered.server.network.vanilla.message.type.play.ClientClickWindowMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.ClientSettingsMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInCreativeWindowAction;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInDataRequest;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInDisplayedRecipe;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInDropHeldItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInEditCommandBlock;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInEnchantItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInLeaveBed;
import org.lanternpowered.server.network.vanilla.message.type.play.ClientLockDifficultyMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInModifyBook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutBrand;
import org.lanternpowered.server.network.vanilla.message.type.play.ChannelPayloadMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutCloseWindow;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutConfirmWindowTransaction;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutFinishUsingItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutHeldItemChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutUnregisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPerformRespawn;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPickItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerAbilities;
import org.lanternpowered.server.network.vanilla.message.type.play.ClientBlockPlacementMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerDigging;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerMovement;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerMovementAndLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerMovementInput;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerOnGroundState;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSneak;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSprint;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSwingArm;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerUseItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerVehicleJump;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerVehicleMovement;
import org.lanternpowered.server.network.vanilla.message.type.play.ClientRecipeBookStatesMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInRequestStatistics;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInResourcePackStatus;
import org.lanternpowered.server.network.vanilla.message.type.play.ClientSetDifficultyMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInSpectate;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInStartElytraFlying;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInSwapHandItems;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInTabComplete;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInTeleportConfirm;
import org.lanternpowered.server.network.vanilla.message.type.play.NamedSoundEffectMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.UpdateJigsawBlockMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInUseEntity;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutAddPotionEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutAdvancements;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockAction;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockBreakAnimation;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockEntity;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBossBar;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChatMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.ChunkMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDataResponse;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDefineCommands;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDefineRecipes;
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
import org.lanternpowered.server.network.vanilla.message.type.play.EntitySoundEffectMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityStatus;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityTeleport;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityVelocity;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutFaceAt;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutMultiBlockChange;
import org.lanternpowered.server.network.vanilla.message.type.play.OpenBookMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.OpenHorseWindowMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenSign;
import org.lanternpowered.server.network.vanilla.message.type.play.OpenWindowMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerAbilities;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerHealthUpdate;
import org.lanternpowered.server.network.vanilla.message.type.play.PlayerJoinMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerPositionAndLook;
import org.lanternpowered.server.network.vanilla.message.type.play.PlayerRespawnMessage;
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
import org.lanternpowered.server.network.vanilla.message.type.play.SetDifficultyMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetEntityPassengers;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetExperience;
import org.lanternpowered.server.network.vanilla.message.type.play.SetGameModeMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetOpLevel;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetReducedDebug;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetWindowSlot;
import org.lanternpowered.server.network.vanilla.message.type.play.SoundEffectMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnExperienceOrb;
import org.lanternpowered.server.network.vanilla.message.type.play.SpawnMobMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnObject;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnPainting;
import org.lanternpowered.server.network.vanilla.message.type.play.SpawnParticleMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.SpawnPlayerMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnThunderbolt;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutStatistics;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutStopSounds;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabComplete;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListHeaderAndFooter;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTags;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTeams;
import org.lanternpowered.server.network.vanilla.message.type.play.TheEndMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTitle;
import org.lanternpowered.server.network.vanilla.message.type.play.SetWindowTradeOffersMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutUnloadChunk;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutUnlockRecipes;
import org.lanternpowered.server.network.vanilla.message.type.play.UpdateLightMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.UpdateViewDistanceMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.UpdateViewPositionMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWindowItems;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWindowProperty;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldBorder;
import org.lanternpowered.server.network.vanilla.message.type.play.UpdateWorldSkyMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldTime;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.ChangeGameStateMessage;

@SuppressWarnings("ALL")
final class ProtocolPlay extends ProtocolBase {

    ProtocolPlay() {
        final MessageRegistry inbound = inbound();
        final MessageRegistry outbound = outbound();

        // Register the processors
        outbound.bindProcessor(TheEndMessage.class, new TheEndMessageProcessor());
        outbound.bindProcessor(MessagePlayOutParticleEffect.class, new ProcessorPlayOutParticleEffect());
        outbound.bindProcessor(SetGameModeMessage.class, new SetGameModeProcessor());
        outbound.bindProcessor(UpdateWorldSkyMessage.class, new UpdateWorldSkyProcessor());
        outbound.bindProcessor(MessagePlayOutTabListEntries.class, new ProcessorPlayOutTabListEntries());

        // Register the codecs and handlers of the default messages
        inbound.bind(CodecPlayInTeleportConfirm.class, MessagePlayInTeleportConfirm.class); // TODO: Handler
        inbound.bind(CodecPlayInDataRequestBlock.class, MessagePlayInDataRequest.Block.class); // TODO: Handler
        inbound.bind(ClientSetDifficultyCodec.class, ClientSetDifficultyMessage.class)
                .bindHandler(new ClientSetDifficultyHandler());
        inbound.bind(CodecPlayInChatMessage.class, MessagePlayInChatMessage.class)
                .bindHandler(new HandlerPlayInChatMessage());
        inbound.bind(CodecPlayInClientStatus.class);
        inbound.bind(ClientSettingsCodec.class, ClientSettingsMessage.class)
                .bindHandler(new ClientSettingsHandler());
        inbound.bind(CodecPlayInTabComplete.class, MessagePlayInTabComplete.class)
                .bindHandler(new HandlerPlayInTabComplete());
        inbound.bind(CodecPlayInOutConfirmWindowTransaction.class, MessagePlayInOutConfirmWindowTransaction.class); // TODO: Handler
        inbound.bind(CodecPlayInEnchantItem.class, MessagePlayInEnchantItem.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleEnchantItem));
        inbound.bind(ClientClickWindowCodec.class, ClientClickWindowMessage.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleWindowClick));
        inbound.bind(CodecPlayInOutCloseWindow.class, MessagePlayInOutCloseWindow.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleWindowClose));
        inbound.bind(ChannelPayloadCodec.class);
        inbound.bind(CodecPlayInModifyBook.class);
        inbound.bind(CodecPlayInDataRequestEntity.class, MessagePlayInDataRequest.Entity.class); // TODO: Handler
        inbound.bind(CodecPlayInUseEntity.class);
        inbound.bind(KeepAliveCodec.class, KeepAliveMessage.class);
        inbound.bind(ClientLocksDifficultyCodec.class, ClientLockDifficultyMessage.class)
                .bindHandler(new ClientLockDifficultyHandler());
        inbound.bind(CodecPlayInPlayerMovement.class, MessagePlayInPlayerMovement.class)
                .bindHandler(new HandlerPlayInPlayerMovement());
        inbound.bind(CodecPlayInPlayerMovementAndLook.class, MessagePlayInPlayerMovementAndLook.class)
                .bindHandler(new HandlerPlayInPlayerMovementAndLook());
        inbound.bind(CodecPlayInPlayerLook.class, MessagePlayInPlayerLook.class)
                .bindHandler(new HandlerPlayInPlayerLook());
        inbound.bind(CodecPlayInPlayerOnGroundState.class, MessagePlayInPlayerOnGroundState.class)
                .bindHandler(new HandlerPlayInPlayerOnGroundState());
        inbound.bind(CodecPlayInPlayerVehicleMovement.class, MessagePlayInPlayerVehicleMovement.class)
                .bindHandler(new HandlerPlayInPlayerVehicleMovement());
        inbound.bind(); // TODO: Steer Boat
        inbound.bind(CodecPlayInPickItem.class, MessagePlayInPickItem.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handlePickItem));
        inbound.bind(CodecPlayInClickRecipe.class, MessagePlayInClickRecipe.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleRecipeClick));
        inbound.bind(CodecPlayInPlayerAbilities.class, MessagePlayInPlayerAbilities.class)
                .bindHandler(new HandlerPlayInPlayerAbilities());
        inbound.bind(CodecPlayInPlayerDigging.class);
        inbound.bind(CodecPlayInPlayerAction.class);
        inbound.bind(CodecPlayInPlayerVehicleControls.class);
        inbound.bind(ClientRecipeBookCodec.class);
        inbound.bind(ClientItemRenameCodec.class, ClientItemRenameMessage.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleItemRename));
        inbound.bind(CodecPlayInResourcePackStatus.class, MessagePlayInResourcePackStatus.class)
                .bindHandler(new HandlerPlayInResourcePackStatus());
        inbound.bind(ClientAdvancementTreeCodec.class);
        inbound.bind(ClientChangeTradeOfferCodec.class, ClientChangeTradeOfferMessage.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleOfferChange));
        inbound.bind(CodecPlayInAcceptBeaconEffects.class, MessagePlayInAcceptBeaconEffects.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleAcceptBeaconEffects));
        inbound.bind(CodecPlayInOutHeldItemChange.class, MessagePlayInOutHeldItemChange.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleHeldItemChange));
        inbound.bind(CodecPlayInEditCommandBlockBlock.class, MessagePlayInEditCommandBlock.Block.class);
        inbound.bind(CodecPlayInEditCommandBlockEntity.class, MessagePlayInEditCommandBlock.Entity.class);
        inbound.bind(CodecPlayInCreativeWindowAction.class, MessagePlayInCreativeWindowAction.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleWindowCreativeClick));
        inbound.bind(UpdateJigsawBlockMessageCodec.class, UpdateJigsawBlockMessage.class); // TODO: Handler
        inbound.bind(); // TODO: Structure blocks
        inbound.bind(CodecPlayInChangeSign.class, MessagePlayInChangeSign.class)
                .bindHandler(new HandlerPlayInChangeSign());
        inbound.bind(CodecPlayInPlayerSwingArm.class, MessagePlayInPlayerSwingArm.class)
                .bindHandler(new HandlerPlayInPlayerSwingArm());
        inbound.bind(CodecPlayInSpectate.class, MessagePlayInSpectate.class); // TODO: Handler
        inbound.bind(ClientBlockPlacementCodec.class, ClientBlockPlacementMessage.class)
                .bindHandler(new ClientBlockPlacementHandler());
        inbound.bind(CodecPlayInPlayerUseItem.class, MessagePlayInPlayerUseItem.class)
                .bindHandler(new HandlerPlayInPlayerUseItem());

        // Provided by CodecPlayInOutCustomPayload
        inbound.bindMessage(MessagePlayInOutBrand.class); // TODO: Handler
        inbound.bindMessage(MessagePlayInModifyBook.Edit.class)
                .bindHandler(new HandlerPlayInEditBook());
        inbound.bindMessage(MessagePlayInModifyBook.Sign.class)
                .bindHandler(new HandlerPlayInSignBook());
        inbound.bindMessage(ChannelPayloadMessage.class)
                .bindHandler(new HandlerPlayInChannelPayload());
        inbound.bindMessage(MessagePlayInOutRegisterChannels.class)
                .bindHandler(new HandlerPlayInRegisterChannels());
        inbound.bindMessage(MessagePlayInOutUnregisterChannels.class)
                .bindHandler(new HandlerPlayInUnregisterChannels());
        // Provided by CodecPlayInUseEntity
        inbound.bindMessage(MessagePlayInUseEntity.Attack.class)
                .bindHandler(new HandlerPlayInUseEntityAttack());
        inbound.bindMessage(MessagePlayInUseEntity.Interact.class)
                .bindHandler(new HandlerPlayInUseEntityInteract());
        // Provided by CodecPlayInPlayerDigging
        inbound.bindMessage(MessagePlayInPlayerDigging.class)
                .bindHandler(new HandlerPlayInPlayerDigging());
        inbound.bindMessage(MessagePlayInDropHeldItem.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleItemDrop));
        inbound.bindMessage(MessagePlayInOutFinishUsingItem.class)
                .bindHandler(new HandlerPlayInFinishUsingItem());
        inbound.bindMessage(MessagePlayInSwapHandItems.class)
                .bindHandler(new HandlerPlayInSwapHandItems());
        inbound.bindMessage(MessagePlayInPerformRespawn.class)
                .bindHandler((context, message) -> context.getSession().getPlayer().handleRespawn());
        inbound.bindMessage(MessagePlayInRequestStatistics.class)
                .bindHandler(new HandlerPlayInRequestStatistics());
        // Provided by CodecPlayInPlayerAction
        inbound.bindMessage(MessagePlayInLeaveBed.class); // TODO: Handler
        inbound.bindMessage(MessagePlayInStartElytraFlying.class)
                .bindHandler(new HandlerPlayInStartElytraFlying());
        // Provided by CodecPlayInPlayerVehicleControls or CodecPlayInPlayerAction
        inbound.bindMessage(MessagePlayInPlayerSneak.class)
                .bindHandler(new HandlerPlayInPlayerSneak());
        inbound.bindMessage(MessagePlayInPlayerSprint.class)
                .bindHandler(new HandlerPlayInPlayerSprint());
        inbound.bindMessage(MessagePlayInPlayerVehicleJump.class); // TODO: Handler
        // Provided by CodecPlayInPlayerVehicleControls
        inbound.bindMessage(MessagePlayInPlayerMovementInput.class)
                .bindHandler(new HandlerPlayInPlayerMovementInput());
        // Provided by CodecPlayInCraftingBookData
        inbound.bindMessage(MessagePlayInDisplayedRecipe.class)
                .bindHandler(new HandlerPlayInContainerSessionForwarding<>(PlayerContainerSession::handleDisplayedRecipe));
        inbound.bindMessage(ClientRecipeBookStatesMessage.class)
                .bindHandler(new ClientRecipeBookStatesHandler());
        // Provided by CodecPlayInAdvancementTab
        final ClientAdvancementTreeHandler handlerPlayInAdvancementTree = new ClientAdvancementTreeHandler();
        inbound.bindMessage(ClientAdvancementTreeMessage.Close.class)
                .bindHandler(handlerPlayInAdvancementTree);
        inbound.bindMessage(ClientAdvancementTreeMessage.Open.class)
                .bindHandler(handlerPlayInAdvancementTree);

        outbound.bind(CodecPlayOutSpawnObject.class, MessagePlayOutSpawnObject.class);
        outbound.bind(CodecPlayOutSpawnExperienceOrb.class, MessagePlayOutSpawnExperienceOrb.class);
        outbound.bind(CodecPlayOutSpawnThunderbolt.class, MessagePlayOutSpawnThunderbolt.class);
        outbound.bind(SpawnMobCodec.class, SpawnMobMessage.class);
        outbound.bind(CodecPlayOutSpawnPainting.class, MessagePlayOutSpawnPainting.class);
        outbound.bind(SpawnPlayerCodec.class, SpawnPlayerMessage.class);
        outbound.bind(CodecPlayOutEntityAnimation.class, MessagePlayOutEntityAnimation.class);
        outbound.bind(CodecPlayOutStatistics.class, MessagePlayOutStatistics.class);
        outbound.bind(CodecPlayOutBlockBreakAnimation.class, MessagePlayOutBlockBreakAnimation.class);
        outbound.bind(CodecPlayOutUpdateBlockEntity.class, MessagePlayOutBlockEntity.class);
        outbound.bind(CodecPlayOutBlockAction.class, MessagePlayOutBlockAction.class);
        outbound.bind(CodecPlayOutBlockChange.class, MessagePlayOutBlockChange.class);
        final CodecRegistration<MessagePlayOutBossBar, CodecPlayOutBossBar> codecPlayOutBossBar = outbound.bind(CodecPlayOutBossBar.class);
        codecPlayOutBossBar.bind(MessagePlayOutBossBar.Add.class);
        codecPlayOutBossBar.bind(MessagePlayOutBossBar.Remove.class);
        codecPlayOutBossBar.bind(MessagePlayOutBossBar.UpdatePercent.class);
        codecPlayOutBossBar.bind(MessagePlayOutBossBar.UpdateStyle.class);
        codecPlayOutBossBar.bind(MessagePlayOutBossBar.UpdateTitle.class);
        codecPlayOutBossBar.bind(MessagePlayOutBossBar.UpdateMisc.class);
        outbound.bind(SetDifficultyCodec.class, SetDifficultyMessage.class);
        outbound.bind(CodecPlayOutChatMessage.class, MessagePlayOutChatMessage.class);
        outbound.bind(CodecPlayOutMultiBlockChange.class, MessagePlayOutMultiBlockChange.class);
        outbound.bind(CodecPlayOutTabComplete.class, MessagePlayOutTabComplete.class);
        outbound.bind(CodecPlayOutDefineCommands.class, MessagePlayOutDefineCommands.class);
        outbound.bind(CodecPlayInOutConfirmWindowTransaction.class, MessagePlayInOutConfirmWindowTransaction.class);
        outbound.bind(CodecPlayInOutCloseWindow.class, MessagePlayInOutCloseWindow.class);
        outbound.bind(CodecPlayOutWindowItems.class, MessagePlayOutWindowItems.class);
        outbound.bind(CodecPlayOutWindowProperty.class, MessagePlayOutWindowProperty.class);
        outbound.bind(CodecPlayOutSetWindowSlot.class, MessagePlayOutSetWindowSlot.class);
        outbound.bind(CodecPlayOutSetCooldown.class, MessagePlayOutSetCooldown.class);
        final CodecRegistration<Message, ChannelPayloadCodec> codecPlayInOutCustomPayload = outbound.bind(
                ChannelPayloadCodec.class);
        codecPlayInOutCustomPayload.bind(ChannelPayloadMessage.class);
        codecPlayInOutCustomPayload.bind(MessagePlayInOutBrand.class);
        outbound.bind(NamedSoundEffectMessage.class, NamedSoundEffectCodec.class);
        outbound.bind(DisconnectCodec.class, DisconnectMessage.class);
        final CodecRegistration<Message, CodecPlayOutEntityStatus> codecPlayOutEntityStatus = outbound.bind(CodecPlayOutEntityStatus.class);
        codecPlayOutEntityStatus.bind(MessagePlayOutEntityStatus.class);
        codecPlayOutEntityStatus.bind(MessagePlayOutSetOpLevel.class);
        codecPlayOutEntityStatus.bind(MessagePlayOutSetReducedDebug.class);
        codecPlayOutEntityStatus.bind(MessagePlayInOutFinishUsingItem.class);
        outbound.bind(); // TODO: Explosion
        outbound.bind(CodecPlayOutUnloadChunk.class, MessagePlayOutUnloadChunk.class);
        outbound.bind(ChangeGameStateCodec.class, ChangeGameStateMessage.class);
        outbound.bind(OpenHorseWindowCodec.class, OpenHorseWindowMessage.class);
        outbound.bind(KeepAliveCodec.class, KeepAliveMessage.class);
        final CodecRegistration<ChunkMessage, ChunkCodec> codecPlayOutChunkData = outbound.bind(ChunkCodec.class);
        codecPlayOutChunkData.bind(ChunkMessage.Init.class);
        codecPlayOutChunkData.bind(ChunkMessage.Update.class);
        final CodecRegistration<Message, CodecPlayOutEffect> codecPlayOutEntityEffect = outbound.bind(CodecPlayOutEffect.class);
        codecPlayOutEntityEffect.bind(MessagePlayOutEffect.class);
        codecPlayOutEntityEffect.bind(MessagePlayOutRecord.class);
        outbound.bind(SpawnParticleCodec.class, SpawnParticleMessage.class);
        outbound.bind(UpdateLightCodec.class, UpdateLightMessage.class);
        outbound.bind(PlayerJoinCodec.class, PlayerJoinMessage.class);
        outbound.bind(); // TODO: Map
        outbound.bind(SetWindowTradeOffersCodec.class, SetWindowTradeOffersMessage.class);
        outbound.bind(CodecPlayOutEntityRelativeMove.class, MessagePlayOutEntityRelativeMove.class);
        outbound.bind(CodecPlayOutEntityLookAndRelativeMove.class, MessagePlayOutEntityLookAndRelativeMove.class);
        outbound.bind(CodecPlayOutEntityLook.class, MessagePlayOutEntityLook.class);
        outbound.bind(); // Entity
        outbound.bind(); // TODO: Vehicle Move
        outbound.bind(CodecPlayOutOpenSign.class, MessagePlayOutOpenSign.class);
        outbound.bind(OpenWindowCodec.class, OpenWindowMessage.class);
        outbound.bind(OpenBookCodec.class, OpenBookMessage.class);
        outbound.bind(CodecPlayOutDisplayRecipe.class, MessagePlayOutDisplayRecipe.class);
        outbound.bind(CodecPlayOutPlayerAbilities.class, MessagePlayOutPlayerAbilities.class);
        outbound.bind(); // TODO: Combat Event
        outbound.bind(CodecPlayOutTabListEntries.class, MessagePlayOutTabListEntries.class);
        final CodecRegistration<MessagePlayOutFaceAt, CodecPlayOutFaceAt> codecPlayOutFaceAt =
                outbound.bind(CodecPlayOutFaceAt.class);
        codecPlayOutFaceAt.bind(MessagePlayOutFaceAt.Entity.class);
        codecPlayOutFaceAt.bind(MessagePlayOutFaceAt.Position.class);
        outbound.bind(CodecPlayOutPlayerPositionAndLook.class, MessagePlayOutPlayerPositionAndLook.class);
        final CodecRegistration<MessagePlayOutUnlockRecipes, CodecPlayOutUnlockRecipes> codecPlayOutUnlockRecipes =
                outbound.bind(CodecPlayOutUnlockRecipes.class);
        codecPlayOutUnlockRecipes.bind(MessagePlayOutUnlockRecipes.Add.class);
        codecPlayOutUnlockRecipes.bind(MessagePlayOutUnlockRecipes.Init.class);
        codecPlayOutUnlockRecipes.bind(MessagePlayOutUnlockRecipes.Remove.class);
        outbound.bind(CodecPlayOutDestroyEntities.class, MessagePlayOutDestroyEntities.class);
        outbound.bind(CodecPlayOutRemovePotionEffect.class, MessagePlayOutRemovePotionEffect.class);
        outbound.bind(CodecPlayOutSendResourcePack.class, MessagePlayOutSendResourcePack.class);
        outbound.bind(PlayerRespawnCodec.class, PlayerRespawnMessage.class);
        outbound.bind(CodecPlayOutEntityHeadLook.class, MessagePlayOutEntityHeadLook.class);
        outbound.bind(CodecPlayOutSelectAdvancementTree.class, MessagePlayOutSelectAdvancementTree.class);
        final CodecRegistration<MessagePlayOutWorldBorder, CodecPlayOutWorldBorder> codecPlayOutWorldBorder =
                outbound.bind(CodecPlayOutWorldBorder.class);
        codecPlayOutWorldBorder.bind(MessagePlayOutWorldBorder.Initialize.class);
        codecPlayOutWorldBorder.bind(MessagePlayOutWorldBorder.UpdateCenter.class);
        codecPlayOutWorldBorder.bind(MessagePlayOutWorldBorder.UpdateDiameter.class);
        codecPlayOutWorldBorder.bind(MessagePlayOutWorldBorder.UpdateLerpedDiameter.class);
        codecPlayOutWorldBorder.bind(MessagePlayOutWorldBorder.UpdateWarningDistance.class);
        codecPlayOutWorldBorder.bind(MessagePlayOutWorldBorder.UpdateWarningTime.class);
        outbound.bind(CodecPlayOutSetCamera.class, MessagePlayOutSetCamera.class);
        outbound.bind(CodecPlayInOutHeldItemChange.class, MessagePlayInOutHeldItemChange.class);
        outbound.bind(UpdateViewPositionCodec.class, UpdateViewPositionMessage.class);
        outbound.bind(UpdateViewDistanceCodec.class, UpdateViewDistanceMessage.class);
        outbound.bind(CodecPlayOutScoreboardDisplayObjective.class, MessagePlayOutScoreboardDisplayObjective.class);
        outbound.bind(CodecPlayOutEntityMetadata.class, MessagePlayOutEntityMetadata.class);
        outbound.bind(); // TODO: Attach Entity
        outbound.bind(CodecPlayOutEntityVelocity.class, MessagePlayOutEntityVelocity.class);
        outbound.bind(CodecPlayOutEntityEquipment.class, MessagePlayOutEntityEquipment.class);
        outbound.bind(CodecPlayOutSetExperience.class, MessagePlayOutSetExperience.class);
        outbound.bind(CodecPlayOutPlayerHealthUpdate.class, MessagePlayOutPlayerHealthUpdate.class);
        final CodecRegistration<MessagePlayOutScoreboardObjective, CodecPlayOutScoreboardObjective> codecPlayOutScoreboardObjective = outbound.bind(
                CodecPlayOutScoreboardObjective.class);
        codecPlayOutScoreboardObjective.bind(MessagePlayOutScoreboardObjective.Create.class);
        codecPlayOutScoreboardObjective.bind(MessagePlayOutScoreboardObjective.Update.class);
        codecPlayOutScoreboardObjective.bind(MessagePlayOutScoreboardObjective.Remove.class);
        outbound.bind(CodecPlayOutSetEntityPassengers.class, MessagePlayOutSetEntityPassengers.class);
        final CodecRegistration<MessagePlayOutTeams, CodecPlayOutTeams> codecPlayOutTeams = outbound.bind(
                CodecPlayOutTeams.class);
        codecPlayOutTeams.bind(MessagePlayOutTeams.AddMembers.class);
        codecPlayOutTeams.bind(MessagePlayOutTeams.Create.class);
        codecPlayOutTeams.bind(MessagePlayOutTeams.Update.class);
        codecPlayOutTeams.bind(MessagePlayOutTeams.Remove.class);
        codecPlayOutTeams.bind(MessagePlayOutTeams.RemoveMembers.class);
        final CodecRegistration<MessagePlayOutScoreboardScore, CodecPlayOutScoreboardScore> codecPlayOutScoreboardScore = outbound.bind(
                CodecPlayOutScoreboardScore.class);
        codecPlayOutScoreboardScore.bind(MessagePlayOutScoreboardScore.CreateOrUpdate.class);
        codecPlayOutScoreboardScore.bind(MessagePlayOutScoreboardScore.Remove.class);
        outbound.bind(CodecPlayOutPlayerSpawnPosition.class, MessagePlayOutPlayerSpawnPosition.class);
        outbound.bind(CodecPlayOutWorldTime.class, MessagePlayOutWorldTime.class);
        final CodecRegistration<MessagePlayOutTitle, CodecPlayOutTitle> codecPlayOutTitle = outbound.bind(CodecPlayOutTitle.class);
        codecPlayOutTitle.bind(MessagePlayOutTitle.Clear.class);
        codecPlayOutTitle.bind(MessagePlayOutTitle.Reset.class);
        codecPlayOutTitle.bind(MessagePlayOutTitle.SetSubtitle.class);
        codecPlayOutTitle.bind(MessagePlayOutTitle.SetActionbarTitle.class);
        codecPlayOutTitle.bind(MessagePlayOutTitle.SetTimes.class);
        codecPlayOutTitle.bind(MessagePlayOutTitle.SetTitle.class);
        outbound.bind(EntitySoundEffectCodec.class, EntitySoundEffectMessage.class);
        outbound.bind(SoundEffectCodec.class, SoundEffectMessage.class);
        outbound.bind(CodecPlayOutStopSounds.class, MessagePlayOutStopSounds.class);
        outbound.bind(CodecPlayOutTabListHeaderAndFooter.class, MessagePlayOutTabListHeaderAndFooter.class);
        outbound.bind(CodecPlayOutDataResponse.class, MessagePlayOutDataResponse.class);
        outbound.bind(CodecPlayOutEntityCollectItem.class, MessagePlayOutEntityCollectItem.class);
        outbound.bind(CodecPlayOutEntityTeleport.class, MessagePlayOutEntityTeleport.class);
        outbound.bind(CodecPlayOutAdvancements.class, MessagePlayOutAdvancements.class);
        outbound.bind(); // TODO: Entity Properties
        outbound.bind(CodecPlayOutAddPotionEffect.class, MessagePlayOutAddPotionEffect.class);
        outbound.bind(CodecPlayOutDefineRecipes.class, MessagePlayOutDefineRecipes.class);
        outbound.bind(CodecPlayOutTags.class, MessagePlayOutTags.class);
    }
}
