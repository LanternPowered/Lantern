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
package org.lanternpowered.server.data.manipulator;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.data.DataHelper;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.manipulator.gen.DataManipulatorGenerator;
import org.lanternpowered.server.data.manipulator.immutable.block.LanternImmutableBigMushroomPoresData;
import org.lanternpowered.server.data.manipulator.immutable.block.LanternImmutableConnectedDirectionData;
import org.lanternpowered.server.data.manipulator.immutable.block.LanternImmutableWireAttachmentData;
import org.lanternpowered.server.data.manipulator.immutable.entity.LanternImmutableBodyPartRotationalData;
import org.lanternpowered.server.data.manipulator.immutable.entity.LanternImmutableRespawnLocationData;
import org.lanternpowered.server.data.manipulator.immutable.entity.LanternImmutableStatisticData;
import org.lanternpowered.server.data.manipulator.immutable.fluid.LanternImmutableFluidTankData;
import org.lanternpowered.server.data.manipulator.immutable.item.LanternImmutableInventoryItemData;
import org.lanternpowered.server.data.manipulator.immutable.tileentity.LanternImmutableBeaconData;
import org.lanternpowered.server.data.manipulator.mutable.block.LanternBigMushroomPoresData;
import org.lanternpowered.server.data.manipulator.mutable.block.LanternConnectedDirectionData;
import org.lanternpowered.server.data.manipulator.mutable.block.LanternWireAttachmentData;
import org.lanternpowered.server.data.manipulator.mutable.entity.LanternBodyPartRotationalData;
import org.lanternpowered.server.data.manipulator.mutable.entity.LanternRespawnLocationData;
import org.lanternpowered.server.data.manipulator.mutable.entity.LanternStatisticData;
import org.lanternpowered.server.data.manipulator.mutable.fluid.LanternFluidTankData;
import org.lanternpowered.server.data.manipulator.mutable.item.LanternInventoryItemData;
import org.lanternpowered.server.data.manipulator.mutable.tileentity.LanternBeaconData;
import org.lanternpowered.server.entity.LanternEntitySnapshot;
import org.lanternpowered.server.fluid.LanternFluidStack;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.plugin.InternalPluginsInfo;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.profile.LanternProfileProperty;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.immutable.ImmutableAttachedData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableAxisData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableBigMushroomPoresData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableChestAttachmentData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableColoredData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableCommandData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableComparatorData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableConnectedDirectionData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDecayableData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDelayableData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDirectionalData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDisarmedData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDisplayNameData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDropData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDyeableData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableEntitySizeData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableExtendedData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFilledData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFireworkEffectData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFireworkRocketData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFluidItemData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFluidTankData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableGrowthData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableHingeData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableInWallData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableInstrumentData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableLayeredData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableListData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableLitData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableMoistureData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableNoteData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableOccupiedData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableOpenData;
import org.spongepowered.api.data.manipulator.immutable.ImmutablePortionData;
import org.spongepowered.api.data.manipulator.immutable.ImmutablePotionEffectData;
import org.spongepowered.api.data.manipulator.immutable.ImmutablePoweredData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableRailDirectionData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableRedstonePoweredData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableRepresentedItemData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableRepresentedPlayerData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableRotationalData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableSlabPortionData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableSnowedData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableStairShapeData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableSurfaceAttachmentData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableTargetedLocationData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableVariantData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableWetData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableAbsorptionData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableActiveItemData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableAffectsSpawningData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableAgeableData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableAgentData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableAggressiveData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableAngerableData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableAreaEffectCloudData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableArmorStandData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableArtData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableBodyPartRotationalData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableBreathingData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableBreedableData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableCareerData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableChargedData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableCriticalHitData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableCustomNameVisibleData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDamagingData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDespawnDelayData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDominantHandData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableElytraFlyingData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableExpOrbData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableExpirableData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableExplosionRadiusData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFallDistanceData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFallingBlockData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFlammableData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFlyingAbilityData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFlyingData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFoodData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFuseData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableGameModeData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableGlowingData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableGravityData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableGriefingData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableHealthData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableHealthScalingData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableHorseData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableIgniteableData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableInvisibilityData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableInvulnerabilityData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableJohnnyData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableJoinData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableKnockbackData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableMinecartBlockData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableMovementSpeedData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableOcelotData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableParrotData;
import org.spongepowered.api.data.manipulator.immutable.ImmutablePassengerData;
import org.spongepowered.api.data.manipulator.immutable.ImmutablePersistingData;
import org.spongepowered.api.data.manipulator.immutable.ImmutablePickupDelayData;
import org.spongepowered.api.data.manipulator.immutable.ImmutablePickupRuleData;
import org.spongepowered.api.data.manipulator.immutable.ImmutablePigSaddleData;
import org.spongepowered.api.data.manipulator.immutable.ImmutablePlayerCreatedData;
import org.spongepowered.api.data.manipulator.immutable.ImmutablePlayingData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableRabbitData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableRespawnLocation;
import org.spongepowered.api.data.manipulator.immutable.ImmutableScreamingData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableShatteringData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableShearedData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableSilentData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableSittingData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableSkinData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableSleepingData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableSlimeData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableSneakingData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableSprintData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableStatisticData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableStuckArrowsData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableTagData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableTameableData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableTradeOfferData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableVehicleData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableVelocityData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableAuthorData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableBlockItemData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableBreakableData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDurabilityData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableEnchantmentData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableGenerationData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableHideData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableInventoryItemData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableLoreData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableMapItemData;
import org.spongepowered.api.data.manipulator.immutable.ImmutablePagedData;
import org.spongepowered.api.data.manipulator.immutable.ImmutablePlaceableData;
import org.spongepowered.api.data.manipulator.immutable.ImmutablePlainPagedData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableSpawnableData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableStoredEnchantmentData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableBannerData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableBeaconData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableBrewingStandData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableCooldownData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableEndGatewayData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFurnaceData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableLockableData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableSignData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableStructureData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableWireAttachmentData;
import org.spongepowered.api.data.manipulator.mutable.ColoredData;
import org.spongepowered.api.data.manipulator.mutable.CommandData;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.DyeableData;
import org.spongepowered.api.data.manipulator.mutable.EntitySizeData;
import org.spongepowered.api.data.manipulator.mutable.FireworkEffectData;
import org.spongepowered.api.data.manipulator.mutable.FireworkRocketData;
import org.spongepowered.api.data.manipulator.mutable.FluidItemData;
import org.spongepowered.api.data.manipulator.mutable.FluidTankData;
import org.spongepowered.api.data.manipulator.mutable.ListData;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.manipulator.mutable.RepresentedItemData;
import org.spongepowered.api.data.manipulator.mutable.RepresentedPlayerData;
import org.spongepowered.api.data.manipulator.mutable.RotationalData;
import org.spongepowered.api.data.manipulator.mutable.TargetedLocationData;
import org.spongepowered.api.data.manipulator.mutable.VariantData;
import org.spongepowered.api.data.manipulator.mutable.WetData;
import org.spongepowered.api.data.manipulator.mutable.AttachedData;
import org.spongepowered.api.data.manipulator.mutable.AxisData;
import org.spongepowered.api.data.manipulator.mutable.BigMushroomPoresData;
import org.spongepowered.api.data.manipulator.mutable.ChestAttachmentData;
import org.spongepowered.api.data.manipulator.mutable.ComparatorData;
import org.spongepowered.api.data.manipulator.mutable.ConnectedDirectionData;
import org.spongepowered.api.data.manipulator.mutable.DecayableData;
import org.spongepowered.api.data.manipulator.mutable.DelayableData;
import org.spongepowered.api.data.manipulator.mutable.DirectionalData;
import org.spongepowered.api.data.manipulator.mutable.DisarmedData;
import org.spongepowered.api.data.manipulator.mutable.DropData;
import org.spongepowered.api.data.manipulator.mutable.ExtendedData;
import org.spongepowered.api.data.manipulator.mutable.FilledData;
import org.spongepowered.api.data.manipulator.mutable.GrowthData;
import org.spongepowered.api.data.manipulator.mutable.HingeData;
import org.spongepowered.api.data.manipulator.mutable.InWallData;
import org.spongepowered.api.data.manipulator.mutable.InstrumentData;
import org.spongepowered.api.data.manipulator.mutable.LayeredData;
import org.spongepowered.api.data.manipulator.mutable.LitData;
import org.spongepowered.api.data.manipulator.mutable.MoistureData;
import org.spongepowered.api.data.manipulator.mutable.NoteData;
import org.spongepowered.api.data.manipulator.mutable.OccupiedData;
import org.spongepowered.api.data.manipulator.mutable.OpenData;
import org.spongepowered.api.data.manipulator.mutable.PortionData;
import org.spongepowered.api.data.manipulator.mutable.PoweredData;
import org.spongepowered.api.data.manipulator.mutable.RailDirectionData;
import org.spongepowered.api.data.manipulator.mutable.RedstonePoweredData;
import org.spongepowered.api.data.manipulator.mutable.SlabPortionData;
import org.spongepowered.api.data.manipulator.mutable.SnowedData;
import org.spongepowered.api.data.manipulator.mutable.StairShapeData;
import org.spongepowered.api.data.manipulator.mutable.SurfaceAttachmentData;
import org.spongepowered.api.data.manipulator.mutable.WireAttachmentData;
import org.spongepowered.api.data.manipulator.mutable.AbsorptionData;
import org.spongepowered.api.data.manipulator.mutable.ActiveItemData;
import org.spongepowered.api.data.manipulator.mutable.AffectsSpawningData;
import org.spongepowered.api.data.manipulator.mutable.AgeableData;
import org.spongepowered.api.data.manipulator.mutable.AgentData;
import org.spongepowered.api.data.manipulator.mutable.AggressiveData;
import org.spongepowered.api.data.manipulator.mutable.AngerableData;
import org.spongepowered.api.data.manipulator.mutable.AreaEffectCloudData;
import org.spongepowered.api.data.manipulator.mutable.ArmorStandData;
import org.spongepowered.api.data.manipulator.mutable.ArtData;
import org.spongepowered.api.data.manipulator.mutable.BodyPartRotationalData;
import org.spongepowered.api.data.manipulator.mutable.BreathingData;
import org.spongepowered.api.data.manipulator.mutable.BreedableData;
import org.spongepowered.api.data.manipulator.mutable.CareerData;
import org.spongepowered.api.data.manipulator.mutable.ChargedData;
import org.spongepowered.api.data.manipulator.mutable.CriticalHitData;
import org.spongepowered.api.data.manipulator.mutable.CustomNameVisibleData;
import org.spongepowered.api.data.manipulator.mutable.DamagingData;
import org.spongepowered.api.data.manipulator.mutable.DespawnDelayData;
import org.spongepowered.api.data.manipulator.mutable.DominantHandData;
import org.spongepowered.api.data.manipulator.mutable.ElytraFlyingData;
import org.spongepowered.api.data.manipulator.mutable.ExpOrbData;
import org.spongepowered.api.data.manipulator.mutable.ExpirableData;
import org.spongepowered.api.data.manipulator.mutable.ExplosionRadiusData;
import org.spongepowered.api.data.manipulator.mutable.FallDistanceData;
import org.spongepowered.api.data.manipulator.mutable.FallingBlockData;
import org.spongepowered.api.data.manipulator.mutable.FlammableData;
import org.spongepowered.api.data.manipulator.mutable.FlyingAbilityData;
import org.spongepowered.api.data.manipulator.mutable.FlyingData;
import org.spongepowered.api.data.manipulator.mutable.FoodData;
import org.spongepowered.api.data.manipulator.mutable.FuseData;
import org.spongepowered.api.data.manipulator.mutable.GameModeData;
import org.spongepowered.api.data.manipulator.mutable.GlowingData;
import org.spongepowered.api.data.manipulator.mutable.GravityData;
import org.spongepowered.api.data.manipulator.mutable.GriefingData;
import org.spongepowered.api.data.manipulator.mutable.HealthData;
import org.spongepowered.api.data.manipulator.mutable.HealthScalingData;
import org.spongepowered.api.data.manipulator.mutable.HorseData;
import org.spongepowered.api.data.manipulator.mutable.IgniteableData;
import org.spongepowered.api.data.manipulator.mutable.InvisibilityData;
import org.spongepowered.api.data.manipulator.mutable.InvulnerabilityData;
import org.spongepowered.api.data.manipulator.mutable.JohnnyData;
import org.spongepowered.api.data.manipulator.mutable.JoinData;
import org.spongepowered.api.data.manipulator.mutable.KnockbackData;
import org.spongepowered.api.data.manipulator.mutable.MinecartBlockData;
import org.spongepowered.api.data.manipulator.mutable.MovementSpeedData;
import org.spongepowered.api.data.manipulator.mutable.OcelotData;
import org.spongepowered.api.data.manipulator.mutable.ParrotData;
import org.spongepowered.api.data.manipulator.mutable.PassengerData;
import org.spongepowered.api.data.manipulator.mutable.PersistingData;
import org.spongepowered.api.data.manipulator.mutable.PickupDelayData;
import org.spongepowered.api.data.manipulator.mutable.PickupRuleData;
import org.spongepowered.api.data.manipulator.mutable.PigSaddleData;
import org.spongepowered.api.data.manipulator.mutable.PlayerCreatedData;
import org.spongepowered.api.data.manipulator.mutable.PlayingData;
import org.spongepowered.api.data.manipulator.mutable.RabbitData;
import org.spongepowered.api.data.manipulator.mutable.RespawnLocationData;
import org.spongepowered.api.data.manipulator.mutable.ScreamingData;
import org.spongepowered.api.data.manipulator.mutable.ShatteringData;
import org.spongepowered.api.data.manipulator.mutable.ShearedData;
import org.spongepowered.api.data.manipulator.mutable.SilentData;
import org.spongepowered.api.data.manipulator.mutable.SittingData;
import org.spongepowered.api.data.manipulator.mutable.SkinData;
import org.spongepowered.api.data.manipulator.mutable.SleepingData;
import org.spongepowered.api.data.manipulator.mutable.SlimeData;
import org.spongepowered.api.data.manipulator.mutable.SneakingData;
import org.spongepowered.api.data.manipulator.mutable.SprintData;
import org.spongepowered.api.data.manipulator.mutable.StatisticData;
import org.spongepowered.api.data.manipulator.mutable.StuckArrowsData;
import org.spongepowered.api.data.manipulator.mutable.TagData;
import org.spongepowered.api.data.manipulator.mutable.TameableData;
import org.spongepowered.api.data.manipulator.mutable.TradeOfferData;
import org.spongepowered.api.data.manipulator.mutable.VehicleData;
import org.spongepowered.api.data.manipulator.mutable.VelocityData;
import org.spongepowered.api.data.manipulator.mutable.AuthorData;
import org.spongepowered.api.data.manipulator.mutable.BlockItemData;
import org.spongepowered.api.data.manipulator.mutable.BreakableData;
import org.spongepowered.api.data.manipulator.mutable.DurabilityData;
import org.spongepowered.api.data.manipulator.mutable.EnchantmentData;
import org.spongepowered.api.data.manipulator.mutable.GenerationData;
import org.spongepowered.api.data.manipulator.mutable.HideData;
import org.spongepowered.api.data.manipulator.mutable.InventoryItemData;
import org.spongepowered.api.data.manipulator.mutable.LoreData;
import org.spongepowered.api.data.manipulator.mutable.MapItemData;
import org.spongepowered.api.data.manipulator.mutable.PagedData;
import org.spongepowered.api.data.manipulator.mutable.PlaceableData;
import org.spongepowered.api.data.manipulator.mutable.PlainPagedData;
import org.spongepowered.api.data.manipulator.mutable.SpawnableData;
import org.spongepowered.api.data.manipulator.mutable.StoredEnchantmentData;
import org.spongepowered.api.data.manipulator.mutable.BannerData;
import org.spongepowered.api.data.manipulator.mutable.BeaconData;
import org.spongepowered.api.data.manipulator.mutable.BrewingStandData;
import org.spongepowered.api.data.manipulator.mutable.CooldownData;
import org.spongepowered.api.data.manipulator.mutable.EndGatewayData;
import org.spongepowered.api.data.manipulator.mutable.FurnaceData;
import org.spongepowered.api.data.manipulator.mutable.LockableData;
import org.spongepowered.api.data.manipulator.mutable.SignData;
import org.spongepowered.api.data.manipulator.mutable.StructureData;
import org.spongepowered.api.data.type.Arts;
import org.spongepowered.api.data.type.Careers;
import org.spongepowered.api.data.type.ChestAttachmentTypes;
import org.spongepowered.api.data.type.ComparatorTypes;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.type.HandPreferences;
import org.spongepowered.api.data.type.Hinges;
import org.spongepowered.api.data.type.HorseColors;
import org.spongepowered.api.data.type.HorseStyles;
import org.spongepowered.api.data.type.InstrumentTypes;
import org.spongepowered.api.data.type.NotePitches;
import org.spongepowered.api.data.type.OcelotTypes;
import org.spongepowered.api.data.type.ParrotVariants;
import org.spongepowered.api.data.type.PickupRules;
import org.spongepowered.api.data.type.PortionTypes;
import org.spongepowered.api.data.type.RabbitTypes;
import org.spongepowered.api.data.type.RailDirections;
import org.spongepowered.api.data.type.SlabPortions;
import org.spongepowered.api.data.type.StairShapes;
import org.spongepowered.api.data.type.StructureModes;
import org.spongepowered.api.data.type.Surfaces;
import org.spongepowered.api.data.type.WireAttachmentTypes;
import org.spongepowered.api.data.value.ListValue;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.fluid.FluidTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Axis;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.TemporalUnits;
import org.spongepowered.api.util.rotation.Rotations;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public class DataManipulatorRegistry {

    private static final DataManipulatorRegistry INSTANCE = new DataManipulatorRegistry();

    public static DataManipulatorRegistry get() {
        return INSTANCE;
    }

    private final Map<Class, DataManipulatorRegistration> registrationByClass = new HashMap<>();
    private final DataManipulatorGenerator dataManipulatorGenerator = new DataManipulatorGenerator();

    {
        /////////////////////
        // General Package //
        /////////////////////

        /// normal containers
        register(ColoredData.class, ImmutableColoredData.class,
                c -> c.register(Keys.COLOR, Color.WHITE));
        register(CommandData.class, ImmutableCommandData.class,
                c -> {
                    c.register(Keys.COMMAND, "");
                    c.register(Keys.SUCCESS_COUNT, 0);
                    c.register(Keys.TRACKS_OUTPUT, true);
                    c.register(Keys.LAST_COMMAND_OUTPUT, Optional.empty());
                });
        register(DisplayNameData.class, ImmutableDisplayNameData.class,
                c -> c.register(Keys.DISPLAY_NAME, Text.empty()));
        register(FireworkRocketData.class, ImmutableFireworkRocketData.class,
                c -> c.register(Keys.FIREWORK_FLIGHT_MODIFIER, 0));
        register(RepresentedItemData.class, ImmutableRepresentedItemData.class,
                c -> c.register(Keys.REPRESENTED_ITEM, ItemStackSnapshot.NONE));
        register(RepresentedPlayerData.class, ImmutableRepresentedPlayerData.class,
                c -> c.register(Keys.REPRESENTED_PLAYER, LanternGameProfile.UNKNOWN));
        register(TargetedLocationData.class, ImmutableTargetedLocationData.class,
                c -> c.register(Keys.TARGETED_LOCATION, Vector3d.ZERO));
        register(WetData.class, ImmutableWetData.class,
                c -> c.register(Keys.IS_WET, false));

        /// variant containers
        registerVariant(DyeableData.class, ImmutableDyeableData.class, Keys.DYE_COLOR, DyeColors.WHITE);
        registerVariant(RotationalData.class, ImmutableRotationalData.class, Keys.ROTATION, Rotations.LEFT);

        /// list containers
        registerList(FireworkEffectData.class, ImmutableFireworkEffectData.class, Keys.FIREWORK_EFFECTS);
        registerList(PotionEffectData.class, ImmutablePotionEffectData.class, Keys.POTION_EFFECTS);

        ///////////////////
        // Block Package //
        ///////////////////

        /// normal containers
        register(AttachedData.class, ImmutableAttachedData.class,
                c -> c.register(Keys.ATTACHED, false));
        register(ConnectedDirectionData.class, ImmutableConnectedDirectionData.class, LanternConnectedDirectionData.class, LanternImmutableConnectedDirectionData.class,
                c -> {
                    c.register(Keys.CONNECTED_WEST, false);
                    c.register(Keys.CONNECTED_EAST, false);
                    c.register(Keys.CONNECTED_SOUTH, false);
                    c.register(Keys.CONNECTED_NORTH, false);
                });
        register(BigMushroomPoresData.class, ImmutableBigMushroomPoresData.class, LanternBigMushroomPoresData.class, LanternImmutableBigMushroomPoresData.class,
                c -> {
                    c.register(Keys.BIG_MUSHROOM_PORES_DOWN, false);
                    c.register(Keys.BIG_MUSHROOM_PORES_EAST, false);
                    c.register(Keys.BIG_MUSHROOM_PORES_NORTH, false);
                    c.register(Keys.BIG_MUSHROOM_PORES_SOUTH, false);
                    c.register(Keys.BIG_MUSHROOM_PORES_UP, false);
                    c.register(Keys.BIG_MUSHROOM_PORES_WEST, false);
                });
        register(DecayableData.class, ImmutableDecayableData.class,
                c -> {
                    c.register(Keys.DECAY_DISTANCE, 6, 1, 7);
                    c.register(Keys.PERSISTENT, false);
                });
        register(DelayableData.class, ImmutableDelayableData.class,
                c -> c.register(Keys.DELAY, 1, 0, Integer.MAX_VALUE));
        register(DirectionalData.class, ImmutableDirectionalData.class,
                c -> c.register(Keys.DIRECTION, Direction.NORTH));
        register(DisarmedData.class, ImmutableDisarmedData.class,
                c -> c.register(Keys.DISARMED, false));
        register(DropData.class, ImmutableDropData.class,
                c -> c.register(Keys.SHOULD_DROP, true));
        register(ExtendedData.class, ImmutableExtendedData.class,
                c -> c.register(Keys.EXTENDED, false));
        register(FilledData.class, ImmutableFilledData.class,
                c -> c.register(Keys.FILLED, false));
        register(GrowthData.class, ImmutableGrowthData.class,
                c -> c.register(Keys.GROWTH_STAGE, 0));
        register(InWallData.class, ImmutableInWallData.class,
                c -> c.register(Keys.IN_WALL, false));
        register(LayeredData.class, ImmutableLayeredData.class,
                c -> c.register(Keys.LAYER, 1));
        register(LitData.class, ImmutableLitData.class,
                c -> c.register(Keys.LIT, false));
        register(MoistureData.class, ImmutableMoistureData.class,
                c -> c.register(Keys.MOISTURE, 0));
        register(OccupiedData.class, ImmutableOccupiedData.class,
                c -> c.register(Keys.OCCUPIED, false));
        register(OpenData.class, ImmutableOpenData.class,
                c -> c.register(Keys.OPEN, false));
        register(PoweredData.class, ImmutablePoweredData.class,
                c -> c.register(Keys.POWERED, false));
        register(RedstonePoweredData.class, ImmutableRedstonePoweredData.class,
                c -> c.register(Keys.POWER, 0));
        register(SnowedData.class, ImmutableSnowedData.class,
                c -> c.register(Keys.SNOWED, false));
        register(WireAttachmentData.class, ImmutableWireAttachmentData.class, LanternWireAttachmentData.class, LanternImmutableWireAttachmentData.class,
                c -> {
                    c.register(Keys.WIRE_ATTACHMENT_WEST, WireAttachmentTypes.NONE);
                    c.register(Keys.WIRE_ATTACHMENT_EAST, WireAttachmentTypes.NONE);
                    c.register(Keys.WIRE_ATTACHMENT_SOUTH, WireAttachmentTypes.NONE);
                    c.register(Keys.WIRE_ATTACHMENT_NORTH, WireAttachmentTypes.NONE);
                });

        /// variant containers
        registerVariant(AxisData.class, ImmutableAxisData.class, Keys.AXIS, Axis.X);
        registerVariant(ChestAttachmentData.class, ImmutableChestAttachmentData.class, Keys.CHEST_ATTACHMENT, ChestAttachmentTypes.NONE);
        registerVariant(ComparatorData.class, ImmutableComparatorData.class, Keys.COMPARATOR_TYPE, ComparatorTypes.COMPARE);
        registerVariant(HingeData.class, ImmutableHingeData.class, Keys.HINGE_POSITION, Hinges.LEFT);
        registerVariant(InstrumentData.class, ImmutableInstrumentData.class, Keys.INSTRUMENT, InstrumentTypes.HARP);
        registerVariant(PortionData.class, ImmutablePortionData.class, Keys.PORTION_TYPE, PortionTypes.BOTTOM);
        registerVariant(RailDirectionData.class, ImmutableRailDirectionData.class, Keys.RAIL_DIRECTION, RailDirections.NORTH_SOUTH);
        registerVariant(SlabPortionData.class, ImmutableSlabPortionData.class, Keys.SLAB_PORTION, SlabPortions.BOTTOM);
        registerVariant(StairShapeData.class, ImmutableStairShapeData.class, Keys.STAIR_SHAPE, StairShapes.STRAIGHT);
        registerVariant(SurfaceAttachmentData.class, ImmutableSurfaceAttachmentData.class, Keys.ATTACHMENT_SURFACE, Surfaces.FLOOR);

        /// list containers

        ////////////////////
        // Entity Package //
        ////////////////////

        /// normal containers
        register(AbsorptionData.class, ImmutableAbsorptionData.class,
                c -> c.register(Keys.ABSORPTION, 0.0));
        register(ActiveItemData.class, ImmutableActiveItemData.class,
                c -> c.register(Keys.ACTIVE_ITEM, ItemStackSnapshot.NONE));
        register(AffectsSpawningData.class, ImmutableAffectsSpawningData.class,
                c -> c.register(Keys.AFFECTS_SPAWNING, false));
        register(AgeableData.class, ImmutableAgeableData.class,
                c -> {
                    c.register(Keys.AGE, 0);
                    c.register(Keys.IS_ADULT, true);
                });
        register(AgentData.class, ImmutableAgentData.class,
                c -> c.register(Keys.AI_ENABLED, true));
        register(AggressiveData.class, ImmutableAggressiveData.class,
                c -> c.register(Keys.ANGRY, false));
        register(AngerableData.class, ImmutableAngerableData.class,
                c -> c.register(Keys.ANGER, 0));
        register(AreaEffectCloudData.class, ImmutableAreaEffectCloudData.class,
                c -> {
                    c.register(Keys.AREA_EFFECT_CLOUD_COLOR, Color.WHITE);
                    c.register(Keys.AREA_EFFECT_CLOUD_AGE, 0);
                    c.register(Keys.AREA_EFFECT_CLOUD_DURATION, 100);
                    c.register(Keys.AREA_EFFECT_CLOUD_DURATION_ON_USE, 0);
                    c.register(Keys.AREA_EFFECT_CLOUD_PARTICLE_TYPE, ParticleTypes.ENTITY_EFFECT);
                    c.register(Keys.AREA_EFFECT_CLOUD_RADIUS, 10.0);
                    c.register(Keys.AREA_EFFECT_CLOUD_RADIUS_ON_USE, 0.0);
                    c.register(Keys.AREA_EFFECT_CLOUD_RADIUS_PER_TICK, 0.1);
                    c.register(Keys.AREA_EFFECT_CLOUD_REAPPLICATION_DELAY, 20);
                    c.register(Keys.AREA_EFFECT_CLOUD_WAIT_TIME, 20);
                    c.register(Keys.POTION_EFFECTS, new ArrayList<>());
                });
        register(ArmorStandData.class, ImmutableArmorStandData.class,
                c -> {
                    c.register(Keys.ARMOR_STAND_MARKER, false);
                    c.register(Keys.ARMOR_STAND_IS_SMALL, false);
                    c.register(Keys.ARMOR_STAND_HAS_ARMS, false);
                    c.register(Keys.ARMOR_STAND_HAS_BASE_PLATE, true);
                });
        register(BodyPartRotationalData.class, ImmutableBodyPartRotationalData.class, LanternBodyPartRotationalData.class, LanternImmutableBodyPartRotationalData.class,
                c -> {
                    c.register(Keys.HEAD_ROTATION, Vector3d.ZERO);
                    c.register(Keys.CHEST_ROTATION, Vector3d.ZERO);
                    c.register(Keys.LEFT_ARM_ROTATION, Vector3d.ZERO);
                    c.register(Keys.LEFT_LEG_ROTATION, Vector3d.ZERO);
                    c.register(Keys.RIGHT_ARM_ROTATION, Vector3d.ZERO);
                    c.register(Keys.RIGHT_LEG_ROTATION, Vector3d.ZERO);
                });
        register(BreathingData.class, ImmutableBreathingData.class,
                c -> {
                    c.register(Keys.REMAINING_AIR, 400);
                    c.register(Keys.MAX_AIR, 400);
                });
        register(BreedableData.class, ImmutableBreedableData.class,
                c -> c.register(Keys.CAN_BREED, true));
        register(ChargedData.class, ImmutableChargedData.class,
                c -> c.register(Keys.CREEPER_CHARGED, false));
        register(CriticalHitData.class, ImmutableCriticalHitData.class,
                c -> c.register(Keys.CRITICAL_HIT, false));
        register(CustomNameVisibleData.class, ImmutableCustomNameVisibleData.class,
                c -> c.register(Keys.CUSTOM_NAME_VISIBLE, true));
        register(DamagingData.class, ImmutableDamagingData.class,
                c -> {
                    c.register(Keys.ATTACK_DAMAGE, 1.0);
                    c.register(Keys.DAMAGE_ENTITY_MAP, new HashMap<>());
                });
        register(DespawnDelayData.class, ImmutableDespawnDelayData.class,
                c -> c.register(Keys.DESPAWN_DELAY, 2000));
        register(ElytraFlyingData.class, ImmutableElytraFlyingData.class,
                c -> c.register(Keys.IS_ELYTRA_FLYING, false));
        // TODO: ExperienceHolderData
        register(ExpirableData.class, ImmutableExpirableData.class,
                c -> c.register(Keys.EXPIRATION_DURATION, Duration.of(200, TemporalUnits.MINECRAFT_TICKS)));
        register(ExplosionRadiusData.class, ImmutableExplosionRadiusData.class,
                c -> c.register(Keys.EXPLOSION_RADIUS, Optional.empty()));
        register(ExpOrbData.class, ImmutableExpOrbData.class,
                c -> c.register(Keys.CONTAINED_EXPERIENCE, 1));
        register(FallDistanceData.class, ImmutableFallDistanceData.class,
                c -> c.register(Keys.FALL_DISTANCE, 0f));
        register(FallingBlockData.class, ImmutableFallingBlockData.class,
                c -> {
                    c.register(Keys.FALL_DAMAGE_PER_BLOCK, 0.3);
                    c.register(Keys.FALLING_BLOCK_STATE, BlockTypes.SAND.getDefaultState());
                    c.register(Keys.FALLING_BLOCK_CAN_HURT_ENTITIES, true);
                    c.register(Keys.MAX_FALL_DAMAGE, Double.MAX_VALUE);
                    c.register(Keys.CAN_PLACE_AS_BLOCK, true);
                    c.register(Keys.CAN_DROP_AS_ITEM, true);
                    c.register(Keys.FALL_TIME, 1);
                });
        register(FlammableData.class, ImmutableFlammableData.class,
                c -> c.register(Keys.IS_AFLAME, false));
        register(FlyingAbilityData.class, ImmutableFlyingAbilityData.class,
                c -> c.register(Keys.CAN_FLY, false));
        register(FlyingData.class, ImmutableFlyingData.class,
                c -> c.register(Keys.IS_FLYING, false));
        register(FoodData.class, ImmutableFoodData.class,
                c -> {
                    c.register(LanternKeys.MAX_EXHAUSTION, 40.0, 0.0, Double.MAX_VALUE);
                    c.register(Keys.EXHAUSTION, 0.0, 0.0, LanternKeys.MAX_EXHAUSTION);
                    c.register(LanternKeys.MAX_FOOD_LEVEL, 20, 0, Integer.MAX_VALUE);
                    c.register(Keys.FOOD_LEVEL, 20, 0, LanternKeys.MAX_FOOD_LEVEL);
                    c.registerWithSuppliedMax(Keys.SATURATION, 5.0, 0.0,
                            container -> container.get(Keys.FOOD_LEVEL).orElse(20).doubleValue());
                });
        register(FuseData.class, ImmutableFuseData.class,
                c -> c.register(Keys.FUSE_DURATION, 20));
        register(GlowingData.class, ImmutableGlowingData.class,
                c -> c.register(Keys.GLOWING, false));
        register(GravityData.class, ImmutableGravityData.class,
                c -> c.register(Keys.HAS_GRAVITY, true));
        register(GriefingData.class, ImmutableGriefingData.class,
                c -> c.register(Keys.CAN_GRIEF, true));
        register(HealthData.class, ImmutableHealthData.class,
                c -> {
                    c.register(Keys.HEALTH, 20.0, 0.0, Keys.MAX_HEALTH);
                    c.register(Keys.MAX_HEALTH, 20.0, 0.0, Double.MAX_VALUE);
                });
        register(HealthScalingData.class, ImmutableHealthScalingData.class,
                c -> c.register(Keys.HEALTH_SCALE, 1.0));
        register(HorseData.class, ImmutableHorseData.class,
                c -> {
                    c.register(Keys.HORSE_STYLE, HorseStyles.NONE);
                    c.register(Keys.HORSE_COLOR, HorseColors.WHITE);
                });
        register(IgniteableData.class, ImmutableIgniteableData.class,
                c -> {
                    c.register(Keys.FIRE_TICKS, 0, 0, Integer.MAX_VALUE);
                    c.register(Keys.FIRE_DAMAGE_DELAY, 0, 0, Integer.MAX_VALUE);
                });
        register(InvisibilityData.class, ImmutableInvisibilityData.class,
                c -> c.register(Keys.INVISIBLE, false));
        register(JoinData.class, ImmutableJoinData.class,
                c -> {
                    c.register(Keys.FIRST_DATE_PLAYED, Instant.now());
                    c.register(Keys.LAST_DATE_PLAYED, Instant.now());
                });
        register(KnockbackData.class, ImmutableKnockbackData.class,
                c -> c.register(Keys.KNOCKBACK_STRENGTH, 0));
        // TODO: LeashData?
        register(MinecartBlockData.class, ImmutableMinecartBlockData.class,
                c -> {
                    c.register(Keys.REPRESENTED_BLOCK, BlockTypes.STONE.getDefaultState());
                    c.register(Keys.OFFSET, 0);
                });
        register(MovementSpeedData.class, ImmutableMovementSpeedData.class,
                c -> {
                    c.register(Keys.WALKING_SPEED, 0.1);
                    c.register(Keys.FLYING_SPEED, 0.2);
                });
        register(PersistingData.class, ImmutablePersistingData.class,
                c -> c.register(Keys.PERSISTENT, false));
        register(PickupDelayData.class, ImmutablePickupDelayData.class,
                c -> {
                    c.register(Keys.PICKUP_DELAY, 0);
                    c.register(Keys.INFINITE_PICKUP_DELAY, false);
                });
        register(PickupRuleData.class, ImmutablePickupRuleData.class,
                c -> c.register(Keys.PICKUP_RULE, PickupRules.ALLOWED));
        register(PigSaddleData.class, ImmutablePigSaddleData.class,
                c -> c.register(Keys.PIG_SADDLE, false));
        register(PlayerCreatedData.class, ImmutablePlayerCreatedData.class,
                c -> c.register(Keys.PLAYER_CREATED, false));
        register(PlayingData.class, ImmutablePlayingData.class,
                c -> c.register(LanternKeys.ARE_PLAYING, false));
        register(ScreamingData.class, ImmutableScreamingData.class,
                c -> c.register(Keys.IS_SCREAMING, false));
        register(ShatteringData.class, ImmutableShatteringData.class,
                c -> c.register(Keys.WILL_SHATTER, false));
        register(ShearedData.class, ImmutableShearedData.class,
                c -> c.register(Keys.IS_SHEARED, false));
        register(SilentData.class, ImmutableSilentData.class,
                c -> c.register(Keys.IS_SILENT, false));
        register(SittingData.class, ImmutableSittingData.class,
                c -> c.register(Keys.IS_SITTING, false));
        register(EntitySizeData.class, ImmutableEntitySizeData.class,
                c -> {
                    c.register(Keys.BASE_SIZE, 1f);
                    c.register(Keys.HEIGHT, 1f);
                    c.register(Keys.SCALE, 1f);
                });
        register(SkinData.class, ImmutableSkinData.class,
                c -> {
                    c.register(Keys.SKIN, LanternProfileProperty.EMPTY_TEXTURES);
                    c.register(Keys.UPDATE_GAME_PROFILE, true);
                });
        register(SleepingData.class, ImmutableSleepingData.class,
                c -> c.register(Keys.IS_SLEEPING, false));
        register(SlimeData.class, ImmutableSlimeData.class,
                c -> c.register(Keys.SLIME_SIZE, 1));
        register(SneakingData.class, ImmutableSneakingData.class,
                c -> c.register(Keys.IS_SNEAKING, false));
        register(SprintData.class, ImmutableSprintData.class,
                c -> c.register(Keys.IS_SPRINTING, false));
        register(StuckArrowsData.class, ImmutableStuckArrowsData.class,
                c -> c.register(Keys.STUCK_ARROWS, 0));
        register(TameableData.class, ImmutableTameableData.class,
                c -> c.register(Keys.TAMED_OWNER, Optional.empty()));
        // TODO
        // final EntitySnapshot noneEntitySnapshot = EntitySnapshot.builder().type(EntityTypes.UNKNOWN).build();
        EntitySnapshot noneEntitySnapshot = new LanternEntitySnapshot();
        register(VehicleData.class, ImmutableVehicleData.class,
                c -> {
                    c.register(Keys.BASE_VEHICLE, noneEntitySnapshot);
                    c.register(Keys.VEHICLE, noneEntitySnapshot);
                });
        register(VelocityData.class, ImmutableVelocityData.class,
                c -> c.register(Keys.VELOCITY, Vector3d.ZERO));
        register(InvulnerabilityData.class, ImmutableInvulnerabilityData.class,
                c -> {
                    c.register(Keys.INVULNERABILITY_TICKS, 0);
                    c.register(Keys.INVULNERABLE, false);
                });
        register(JohnnyData.class, ImmutableJohnnyData.class,
                c -> c.register(Keys.IS_JOHNNY, false));
        register(TagData.class, ImmutableTagData.class,
                c -> c.register(Keys.TAGS, new HashSet<>()));

        /// variant containers
        registerVariant(ArtData.class, ImmutableArtData.class, Keys.ART, Arts.AZTEC);
        registerVariant(CareerData.class, ImmutableCareerData.class, Keys.CAREER, Careers.FARMER);
        registerVariant(DominantHandData.class, ImmutableDominantHandData.class, Keys.DOMINANT_HAND, HandPreferences.RIGHT);
        registerVariant(GameModeData.class, ImmutableGameModeData.class, Keys.GAME_MODE, GameModes.NOT_SET);
        registerVariant(OcelotData.class, ImmutableOcelotData.class, Keys.OCELOT_TYPE, OcelotTypes.WILD_OCELOT);
        registerVariant(ParrotData.class, ImmutableParrotData.class, Keys.PARROT_VARIANT, ParrotVariants.RED);
        registerVariant(RabbitData.class, ImmutableRabbitData.class, Keys.RABBIT_TYPE, RabbitTypes.WHITE);

        /// list containers
        registerList(PassengerData.class, ImmutablePassengerData.class, Keys.PASSENGERS);
        registerList(TradeOfferData.class, ImmutableTradeOfferData.class, Keys.TRADE_OFFERS);

        /// map containers
        register(StatisticData.class, LanternStatisticData::new, LanternStatisticData::new, LanternStatisticData::new,
                ImmutableStatisticData.class, LanternImmutableStatisticData::new, LanternImmutableStatisticData::new);
        register(RespawnLocationData.class, LanternRespawnLocationData::new, LanternRespawnLocationData::new, LanternRespawnLocationData::new,
                ImmutableRespawnLocation.class, LanternImmutableRespawnLocationData::new, LanternImmutableRespawnLocationData::new);

        ///////////////////
        // Fluid Package //
        ///////////////////

        /// normal containers
        register(FluidItemData.class, ImmutableFluidItemData.class,
                c -> c.register(Keys.FLUID_ITEM_STACK, new LanternFluidStack(FluidTypes.WATER, 0).createSnapshot()));

        /// variant containers

        /// list containers

        /// map containers
        register(FluidTankData.class, LanternFluidTankData::new, LanternFluidTankData::new, LanternFluidTankData::new,
                ImmutableFluidTankData.class, LanternImmutableFluidTankData::new, LanternImmutableFluidTankData::new);

        //////////////////
        // Item Package //
        //////////////////

        /// normal containers
        register(AuthorData.class, ImmutableAuthorData.class,
                c -> c.register(Keys.BOOK_AUTHOR, Text.empty()));
        register(BlockItemData.class, ImmutableBlockItemData.class,
                c -> c.register(Keys.ITEM_BLOCKSTATE, BlockTypes.AIR.getDefaultState()));
        register(BreakableData.class, ImmutableBreakableData.class,
                c -> c.register(Keys.BREAKABLE_BLOCK_TYPES, new HashSet<>()));
        register(DurabilityData.class, ImmutableDurabilityData.class,
                c -> {
                    c.register(Keys.ITEM_DURABILITY, 100);
                    c.register(Keys.UNBREAKABLE, false);
                });
        register(GenerationData.class, ImmutableGenerationData.class,
                c -> c.register(Keys.GENERATION, 0, 0, Integer.MAX_VALUE));
        register(HideData.class, ImmutableHideData.class,
                c -> {
                    c.register(Keys.HIDE_ENCHANTMENTS, false);
                    c.register(Keys.HIDE_ATTRIBUTES, false);
                    c.register(Keys.HIDE_UNBREAKABLE, false);
                    c.register(Keys.HIDE_CAN_DESTROY, false);
                    c.register(Keys.HIDE_CAN_PLACE, false);
                    c.register(Keys.HIDE_MISCELLANEOUS, false);
                });
        register(MapItemData.class, ImmutableMapItemData.class,
                c -> {});
        register(PlaceableData.class, ImmutablePlaceableData.class,
                c -> c.register(Keys.PLACEABLE_BLOCKS, new HashSet<>()));

        /// variant containers
        registerVariant(SpawnableData.class, ImmutableSpawnableData.class, Keys.SPAWNABLE_ENTITY_TYPE, EntityTypes.CHICKEN);

        /// list containers
        registerList(EnchantmentData.class, ImmutableEnchantmentData.class, Keys.ITEM_ENCHANTMENTS);
        registerList(LoreData.class, ImmutableLoreData.class, Keys.ITEM_LORE);
        registerList(PagedData.class, ImmutablePagedData.class, Keys.BOOK_PAGES);
        registerList(PlainPagedData.class, ImmutablePlainPagedData.class, Keys.PLAIN_BOOK_PAGES);
        registerList(StoredEnchantmentData.class, ImmutableStoredEnchantmentData.class, Keys.STORED_ENCHANTMENTS);

        /// containers with special behavior
        register(InventoryItemData.class, LanternInventoryItemData::new, LanternInventoryItemData::new, LanternInventoryItemData::new,
                ImmutableInventoryItemData.class, LanternImmutableInventoryItemData::new, LanternImmutableInventoryItemData::new);

        /////////////////////////
        // Tile Entity Package //
        /////////////////////////

        /// normal containers
        register(BannerData.class, ImmutableBannerData.class,
                c -> {
                    c.register(Keys.BANNER_BASE_COLOR, DyeColors.WHITE);
                    c.register(Keys.BANNER_PATTERNS, new ArrayList<>());
                });
        register(BeaconData.class, ImmutableBeaconData.class, LanternBeaconData.class, LanternImmutableBeaconData.class,
                c -> {
                    c.register(Keys.BEACON_PRIMARY_EFFECT, Optional.empty());
                    c.register(Keys.BEACON_SECONDARY_EFFECT, Optional.empty());
                });
        register(BrewingStandData.class, ImmutableBrewingStandData.class,
                c -> c.register(Keys.REMAINING_BREW_TIME, 0, 0, Integer.MAX_VALUE));
        register(CooldownData.class, ImmutableCooldownData.class,
                c -> c.register(Keys.COOLDOWN, 0, 0, Integer.MAX_VALUE));
        register(EndGatewayData.class, ImmutableEndGatewayData.class,
                c -> {
                    c.register(Keys.EXIT_POSITION, Vector3i.ZERO);
                    c.register(Keys.EXACT_TELEPORT, false);
                    c.register(Keys.END_GATEWAY_AGE, 0L);
                    c.register(Keys.END_GATEWAY_TELEPORT_COOLDOWN, 0);
                });
        register(FurnaceData.class, ImmutableFurnaceData.class,
                c -> {
                    c.register(Keys.MAX_BURN_TIME, 0, 0, Integer.MAX_VALUE);
                    c.register(Keys.PASSED_BURN_TIME, 0, 0, Keys.MAX_BURN_TIME);
                    c.register(Keys.MAX_COOK_TIME, 0, 0, Integer.MAX_VALUE);
                    c.register(Keys.PASSED_COOK_TIME, 0, 0, Keys.MAX_COOK_TIME);
                });
        register(LockableData.class, ImmutableLockableData.class,
                c -> c.register(Keys.LOCK_TOKEN, ""));
        register(NoteData.class, ImmutableNoteData.class,
                c -> c.register(Keys.NOTE_PITCH, NotePitches.A1));
        register(StructureData.class, ImmutableStructureData.class,
                c -> {
                    c.register(Keys.STRUCTURE_AUTHOR, "");
                    c.register(Keys.STRUCTURE_IGNORE_ENTITIES, false);
                    c.register(Keys.STRUCTURE_INTEGRITY, 1f);
                    c.register(Keys.STRUCTURE_MODE, StructureModes.DATA);
                    c.register(Keys.STRUCTURE_POSITION, Vector3i.ZERO);
                    c.register(Keys.STRUCTURE_POWERED, false);
                    c.register(Keys.STRUCTURE_SEED, 0L);
                    c.register(Keys.STRUCTURE_SHOW_AIR, false);
                    c.register(Keys.STRUCTURE_SHOW_BOUNDING_BOX, false);
                    c.register(Keys.STRUCTURE_SIZE, Vector3i.ZERO);
                });

        /// variant containers

        /// list containers
        registerList(SignData.class, ImmutableSignData.class, Keys.SIGN_LINES);
    }

    private static final class RegistrationInfo {

        public static RegistrationInfo build(Class<?> manipulatorType) {
            checkNotNull(manipulatorType, "manipulatorType");

            final String fullName = manipulatorType.getName();
            final String plugin;
            if (fullName.startsWith("org.spongepowered.")) {
                plugin = InternalPluginsInfo.SpongePlatform.IDENTIFIER;
            } else if (fullName.startsWith("org.lanternpowered.")) {
                plugin = InternalPluginsInfo.Implementation.IDENTIFIER;
            } else {
                final String[] parts = fullName.split(".");
                if (parts.length > 1) {
                    plugin = parts[1];
                } else if (parts.length == 1) {
                    plugin = parts[0];
                } else {
                    plugin = "unknown";
                }
            }

            final PluginContainer pluginContainer = Lantern.getGame().getPluginManager().getPlugin(plugin)
                    .orElseThrow(() -> new IllegalStateException("The plugin " + plugin + " does not exist!"));
            return new RegistrationInfo(pluginContainer, DataHelper.camelToSnake(fullName.substring(fullName.lastIndexOf('.') + 1)),
                    manipulatorType.getCanonicalName());
        }

        private final PluginContainer pluginContainer;
        private final String id;
        private final String name;

        private RegistrationInfo(PluginContainer pluginContainer, String id, String name) {
            this.pluginContainer = pluginContainer;
            this.name = name;
            this.id = id;
        }
    }

    @SuppressWarnings("unchecked")
    public <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> DataManipulatorRegistration<M, I> register(
            Class<M> manipulatorType, Supplier<M> manipulatorSupplier, Function<M, M> manipulatorCopyFunction, Function<I, M> immutableToMutableFunction,
            Class<I> immutableManipulatorType, Supplier<I> immutableManipulatorSupplier, Function<M, I> mutableToImmutableFunction) {
        final RegistrationInfo registrationInfo = RegistrationInfo.build(manipulatorType);
        return register(registrationInfo.pluginContainer, registrationInfo.id, registrationInfo.name, manipulatorType, manipulatorSupplier,
                manipulatorCopyFunction, immutableToMutableFunction, immutableManipulatorType, immutableManipulatorSupplier,
                mutableToImmutableFunction);
    }

    @SuppressWarnings("unchecked")
    public <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> DataManipulatorRegistration<M, I> register(
            PluginContainer pluginContainer, String id, String name,
            Class<M> manipulatorType, Supplier<M> manipulatorSupplier, Function<M, M> manipulatorCopyFunction, Function<I, M> immutableToMutableFunction,
            Class<I> immutableManipulatorType, Supplier<I> immutableManipulatorSupplier, Function<M, I> mutableToImmutableFunction) {
        final DataManipulatorRegistration<M, I> registration = new SimpleDataManipulatorRegistration<>(pluginContainer, id, name,
                manipulatorType, manipulatorSupplier, manipulatorCopyFunction, immutableToMutableFunction,
                immutableManipulatorType, immutableManipulatorSupplier, mutableToImmutableFunction, manipulatorSupplier.get().getKeys());
        return register(registration);
    }

    public <M extends VariantData<E, M, I>, I extends ImmutableVariantData<E, I, M>, E> DataManipulatorRegistration<M, I> registerVariant(
            Class<M> manipulatorType, Class<I> immutableManipulatorType, Key<Value<E>> key, E defaultValue) {
        final RegistrationInfo registrationInfo = RegistrationInfo.build(manipulatorType);
        return registerVariant(registrationInfo.pluginContainer, registrationInfo.id, registrationInfo.name,
                manipulatorType, immutableManipulatorType, key, defaultValue);
    }

    public <M extends VariantData<E, M, I>, I extends ImmutableVariantData<E, I, M>, E> DataManipulatorRegistration<M, I> registerVariant(
            PluginContainer pluginContainer, String id, String name, Class<M> manipulatorType, Class<I> immutableManipulatorType,
            Key<Value<E>> key, E defaultValue) {
        final DataManipulatorRegistration<M, I> registration = this.dataManipulatorGenerator.newVariantRegistrationFor(
                pluginContainer, id, name, manipulatorType, immutableManipulatorType, key, defaultValue);
        return register(registration);
    }

    public <M extends ListData<E, M, I>, I extends ImmutableListData<E, I, M>, E> DataManipulatorRegistration<M, I> registerList(
            Class<M> manipulatorType, Class<I> immutableManipulatorType, Key<ListValue<E>> key) {
        return registerList(manipulatorType, immutableManipulatorType, key, ArrayList::new);
    }

    public <M extends ListData<E, M, I>, I extends ImmutableListData<E, I, M>, E> DataManipulatorRegistration<M, I> registerList(
            Class<M> manipulatorType, Class<I> immutableManipulatorType,
            Key<ListValue<E>> key, Supplier<List<E>> listSupplier) {
        final RegistrationInfo registrationInfo = RegistrationInfo.build(manipulatorType);
        return registerList(registrationInfo.pluginContainer, registrationInfo.id, registrationInfo.name,
                manipulatorType, immutableManipulatorType, key, listSupplier);
    }

    public <M extends ListData<E, M, I>, I extends ImmutableListData<E, I, M>, E> DataManipulatorRegistration<M, I> registerList(
            PluginContainer pluginContainer, String id, String name, Class<M> manipulatorType, Class<I> immutableManipulatorType, Key<ListValue<E>> key) {
        return registerList(pluginContainer, id, name, manipulatorType, immutableManipulatorType, key, ArrayList::new);
    }

    public <M extends ListData<E, M, I>, I extends ImmutableListData<E, I, M>, E> DataManipulatorRegistration<M, I> registerList(
            PluginContainer pluginContainer, String id, String name, Class<M> manipulatorType, Class<I> immutableManipulatorType,
            Key<ListValue<E>> key, Supplier<List<E>> listSupplier) {
        final DataManipulatorRegistration<M, I> registration = this.dataManipulatorGenerator.newListRegistrationFor(
                pluginContainer, id, name, manipulatorType, immutableManipulatorType, key, listSupplier);
        return register(registration);
    }

    public <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> DataManipulatorRegistration<M, I> register(
            Class<M> manipulatorType, Class<I> immutableManipulatorType, @Nullable Consumer<ValueCollection> registrationConsumer) {
        return register(manipulatorType, immutableManipulatorType, null, null, registrationConsumer);
    }

    public <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> DataManipulatorRegistration<M, I> register(
            Class<M> manipulatorType, Class<I> immutableManipulatorType,
            @Nullable Class<? extends M> mutableExpansion, @Nullable Class<? extends I> immutableExpansion,
            @Nullable Consumer<ValueCollection> registrationConsumer) {
        final RegistrationInfo registrationInfo = RegistrationInfo.build(manipulatorType);
        return register(registrationInfo.pluginContainer, registrationInfo.id, registrationInfo.name,
                manipulatorType, immutableManipulatorType, mutableExpansion, immutableExpansion, registrationConsumer);
    }

    public <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> DataManipulatorRegistration<M, I> register(
            PluginContainer pluginContainer, String id, String name, Class<M> manipulatorType, Class<I> immutableManipulatorType) {
        return register(pluginContainer, id, name, manipulatorType, immutableManipulatorType, null, null, null);
    }

    public <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> DataManipulatorRegistration<M, I> register(
            PluginContainer pluginContainer, String id, String name, Class<M> manipulatorType, Class<I> immutableManipulatorType,
            @Nullable Class<? extends M> mutableExpansion, @Nullable Class<? extends I> immutableExpansion,
            @Nullable Consumer<ValueCollection> registrationConsumer) {
        final DataManipulatorRegistration<M, I> registration = this.dataManipulatorGenerator.newRegistrationFor(
                pluginContainer, id, name, manipulatorType, immutableManipulatorType, mutableExpansion, immutableExpansion, registrationConsumer);
        return register(registration);
    }

    @SuppressWarnings("unchecked")
    private <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> DataManipulatorRegistration<M, I> register(
            DataManipulatorRegistration<M, I> registration) {
        checkNotNull(registration, "registration");
        this.registrationByClass.put(registration.getManipulatorClass(), registration);
        this.registrationByClass.put(registration.getImmutableManipulatorClass(), registration);
        this.registrationByClass.put(registration.createMutable().getClass(), registration);
        this.registrationByClass.put(registration.createImmutable().getClass(), registration);
        ((AbstractDataManipulatorRegistration) registration).register();
        return registration;
    }

    public Collection<DataManipulatorRegistration> getAll() {
        return Collections.unmodifiableCollection(this.registrationByClass.values());
    }

    @SuppressWarnings("unchecked")
    public Optional<DataManipulatorRegistration> getBy(Class manipulatorType) {
        return Optional.ofNullable(this.registrationByClass.get(checkNotNull(manipulatorType, "manipulatorType")));
    }

    @SuppressWarnings("unchecked")
    public <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> Optional<DataManipulatorRegistration<M, I>> getByMutable(
            Class<M> manipulatorType) {
        return Optional.ofNullable(this.registrationByClass.get(checkNotNull(manipulatorType, "manipulatorType")));
    }

    @SuppressWarnings("unchecked")
    public <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> Optional<DataManipulatorRegistration<M, I>> getByImmutable(
            Class<I> immutableManipulatorType) {
        return Optional.ofNullable(this.registrationByClass.get(checkNotNull(immutableManipulatorType, "immutableManipulatorType")));
    }
}
