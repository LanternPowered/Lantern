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
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.manipulator.gen.DataManipulatorGenerator;
import org.lanternpowered.server.data.manipulator.immutable.block.LanternImmutableConnectedDirectionData;
import org.lanternpowered.server.data.manipulator.immutable.block.LanternImmutableWireAttachmentData;
import org.lanternpowered.server.data.manipulator.immutable.entity.LanternImmutableBodyPartRotationalData;
import org.lanternpowered.server.data.manipulator.immutable.entity.LanternImmutableRespawnLocationData;
import org.lanternpowered.server.data.manipulator.immutable.entity.LanternImmutableStatisticData;
import org.lanternpowered.server.data.manipulator.immutable.fluid.LanternImmutableFluidTankData;
import org.lanternpowered.server.data.manipulator.immutable.item.LanternImmutableInventoryItemData;
import org.lanternpowered.server.data.manipulator.immutable.tileentity.LanternImmutableBeaconData;
import org.lanternpowered.server.data.manipulator.mutable.block.LanternConnectedDirectionData;
import org.lanternpowered.server.data.manipulator.mutable.block.LanternWireAttachmentData;
import org.lanternpowered.server.data.manipulator.mutable.entity.LanternBodyPartRotationalData;
import org.lanternpowered.server.data.manipulator.mutable.entity.LanternRespawnLocationData;
import org.lanternpowered.server.data.manipulator.mutable.entity.LanternStatisticData;
import org.lanternpowered.server.data.manipulator.mutable.fluid.LanternFluidTankData;
import org.lanternpowered.server.data.manipulator.mutable.item.LanternInventoryItemData;
import org.lanternpowered.server.data.manipulator.mutable.tileentity.LanternBeaconData;
import org.lanternpowered.server.data.value.IValueContainer;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.plugin.InternalPluginsInfo;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.immutable.ImmutableColoredData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableCommandData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDisplayNameData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDyeableData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFireworkEffectData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFireworkRocketData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableListData;
import org.spongepowered.api.data.manipulator.immutable.ImmutablePotionEffectData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableRepresentedItemData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableRepresentedPlayerData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableRotationalData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableSkullData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableTargetedLocationData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableVariantData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableWetData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableAttachedData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableAxisData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableBigMushroomData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableBrickData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableComparatorData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableConnectedDirectionData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableDecayableData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableDelayableData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableDirectionalData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableDirtData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableDisarmedData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableDisguisedBlockData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableDoublePlantData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableDropData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableExtendedData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableFilledData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableGrowthData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableHingeData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableInWallData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableLayeredData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableLogAxisData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableMoistureData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableOccupiedData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableOpenData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutablePistonData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutablePlantData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutablePortionData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutablePoweredData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutablePrismarineData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableQuartzData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableRailDirectionData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableRedstonePoweredData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableSandData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableSandstoneData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableSeamlessData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableShrubData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableSlabData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableSnowedData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableStairShapeData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableStoneData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableTreeData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableWallData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableWireAttachmentData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableAbsorptionData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableAffectsSpawningData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableAgeableData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableAgentData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableAggressiveData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableAngerableData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableAreaEffectCloudData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableArmorStandData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableArtData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableBodyPartRotationalData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableBreathingData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableBreedableData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableCareerData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableChargedData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableCriticalHitData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableCustomNameVisibleData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableDamageableData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableDamagingData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableDespawnDelayData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableDominantHandData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableExpOrbData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableExpirableData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableExplosionRadiusData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableFallDistanceData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableFallingBlockData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableFlammableData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableFlyingAbilityData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableFlyingData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableFoodData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableFuseData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableGameModeData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableGlowingData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableGravityData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableGriefingData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableHealthData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableHealthScalingData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableHorseData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableIgniteableData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableInvisibilityData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableJoinData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableKnockbackData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableMinecartBlockData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableMovementSpeedData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableOcelotData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableParrotData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutablePassengerData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutablePersistingData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutablePickupDelayData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutablePickupRuleData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutablePigSaddleData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutablePlayerCreatedData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutablePlayingData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableRabbitData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableRespawnLocation;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableScreamingData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableShatteringData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableShearedData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableSilentData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableSittingData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableSizeData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableSkinData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableSleepingData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableSlimeData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableSneakingData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableSprintData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableStatisticData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableStuckArrowsData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableTameableData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableTradeOfferData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableVehicleData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableVelocityData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableAuthorData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableBlockItemData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableBreakableData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableCoalData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableCookedFishData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableDurabilityData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableEnchantmentData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableFishData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableGenerationData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableGoldenAppleData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableHideData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableInventoryItemData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableLoreData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableMapItemData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutablePagedData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutablePlaceableData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableSpawnableData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableStoredEnchantmentData;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableBannerData;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableBeaconData;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableBedData;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableBrewingStandData;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableCooldownData;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableEndGatewayData;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableFurnaceData;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableLockableData;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableNoteData;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableSignData;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableStructureData;
import org.spongepowered.api.data.manipulator.mutable.ColoredData;
import org.spongepowered.api.data.manipulator.mutable.CommandData;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.DyeableData;
import org.spongepowered.api.data.manipulator.mutable.FireworkEffectData;
import org.spongepowered.api.data.manipulator.mutable.FireworkRocketData;
import org.spongepowered.api.data.manipulator.mutable.ListData;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.manipulator.mutable.RepresentedItemData;
import org.spongepowered.api.data.manipulator.mutable.RepresentedPlayerData;
import org.spongepowered.api.data.manipulator.mutable.RotationalData;
import org.spongepowered.api.data.manipulator.mutable.SkullData;
import org.spongepowered.api.data.manipulator.mutable.TargetedLocationData;
import org.spongepowered.api.data.manipulator.mutable.VariantData;
import org.spongepowered.api.data.manipulator.mutable.WetData;
import org.spongepowered.api.data.manipulator.mutable.block.AttachedData;
import org.spongepowered.api.data.manipulator.mutable.block.AxisData;
import org.spongepowered.api.data.manipulator.mutable.block.BigMushroomData;
import org.spongepowered.api.data.manipulator.mutable.block.BrickData;
import org.spongepowered.api.data.manipulator.mutable.block.ComparatorData;
import org.spongepowered.api.data.manipulator.mutable.block.ConnectedDirectionData;
import org.spongepowered.api.data.manipulator.mutable.block.DecayableData;
import org.spongepowered.api.data.manipulator.mutable.block.DelayableData;
import org.spongepowered.api.data.manipulator.mutable.block.DirectionalData;
import org.spongepowered.api.data.manipulator.mutable.block.DirtData;
import org.spongepowered.api.data.manipulator.mutable.block.DisarmedData;
import org.spongepowered.api.data.manipulator.mutable.block.DisguisedBlockData;
import org.spongepowered.api.data.manipulator.mutable.block.DoublePlantData;
import org.spongepowered.api.data.manipulator.mutable.block.DropData;
import org.spongepowered.api.data.manipulator.mutable.block.ExtendedData;
import org.spongepowered.api.data.manipulator.mutable.block.FilledData;
import org.spongepowered.api.data.manipulator.mutable.block.FluidLevelData;
import org.spongepowered.api.data.manipulator.mutable.block.GrowthData;
import org.spongepowered.api.data.manipulator.mutable.block.HingeData;
import org.spongepowered.api.data.manipulator.mutable.block.InWallData;
import org.spongepowered.api.data.manipulator.mutable.block.LayeredData;
import org.spongepowered.api.data.manipulator.mutable.block.LogAxisData;
import org.spongepowered.api.data.manipulator.mutable.block.MoistureData;
import org.spongepowered.api.data.manipulator.mutable.block.OccupiedData;
import org.spongepowered.api.data.manipulator.mutable.block.OpenData;
import org.spongepowered.api.data.manipulator.mutable.block.PistonData;
import org.spongepowered.api.data.manipulator.mutable.block.PlantData;
import org.spongepowered.api.data.manipulator.mutable.block.PortionData;
import org.spongepowered.api.data.manipulator.mutable.block.PoweredData;
import org.spongepowered.api.data.manipulator.mutable.block.PrismarineData;
import org.spongepowered.api.data.manipulator.mutable.block.QuartzData;
import org.spongepowered.api.data.manipulator.mutable.block.RailDirectionData;
import org.spongepowered.api.data.manipulator.mutable.block.RedstonePoweredData;
import org.spongepowered.api.data.manipulator.mutable.block.SandData;
import org.spongepowered.api.data.manipulator.mutable.block.SandstoneData;
import org.spongepowered.api.data.manipulator.mutable.block.SeamlessData;
import org.spongepowered.api.data.manipulator.mutable.block.ShrubData;
import org.spongepowered.api.data.manipulator.mutable.block.SlabData;
import org.spongepowered.api.data.manipulator.mutable.block.SnowedData;
import org.spongepowered.api.data.manipulator.mutable.block.StairShapeData;
import org.spongepowered.api.data.manipulator.mutable.block.StoneData;
import org.spongepowered.api.data.manipulator.mutable.block.TreeData;
import org.spongepowered.api.data.manipulator.mutable.block.WallData;
import org.spongepowered.api.data.manipulator.mutable.block.WireAttachmentData;
import org.spongepowered.api.data.manipulator.mutable.entity.AbsorptionData;
import org.spongepowered.api.data.manipulator.mutable.entity.AffectsSpawningData;
import org.spongepowered.api.data.manipulator.mutable.entity.AgeableData;
import org.spongepowered.api.data.manipulator.mutable.entity.AgentData;
import org.spongepowered.api.data.manipulator.mutable.entity.AggressiveData;
import org.spongepowered.api.data.manipulator.mutable.entity.AngerableData;
import org.spongepowered.api.data.manipulator.mutable.entity.AreaEffectCloudData;
import org.spongepowered.api.data.manipulator.mutable.entity.ArmorStandData;
import org.spongepowered.api.data.manipulator.mutable.entity.ArtData;
import org.spongepowered.api.data.manipulator.mutable.entity.BodyPartRotationalData;
import org.spongepowered.api.data.manipulator.mutable.entity.BreathingData;
import org.spongepowered.api.data.manipulator.mutable.entity.BreedableData;
import org.spongepowered.api.data.manipulator.mutable.entity.CareerData;
import org.spongepowered.api.data.manipulator.mutable.entity.ChargedData;
import org.spongepowered.api.data.manipulator.mutable.entity.CriticalHitData;
import org.spongepowered.api.data.manipulator.mutable.entity.CustomNameVisibleData;
import org.spongepowered.api.data.manipulator.mutable.entity.DamageableData;
import org.spongepowered.api.data.manipulator.mutable.entity.DamagingData;
import org.spongepowered.api.data.manipulator.mutable.entity.DespawnDelayData;
import org.spongepowered.api.data.manipulator.mutable.entity.DominantHandData;
import org.spongepowered.api.data.manipulator.mutable.entity.ExpOrbData;
import org.spongepowered.api.data.manipulator.mutable.entity.ExpirableData;
import org.spongepowered.api.data.manipulator.mutable.entity.ExplosionRadiusData;
import org.spongepowered.api.data.manipulator.mutable.entity.FallDistanceData;
import org.spongepowered.api.data.manipulator.mutable.entity.FallingBlockData;
import org.spongepowered.api.data.manipulator.mutable.entity.FlammableData;
import org.spongepowered.api.data.manipulator.mutable.entity.FlyingAbilityData;
import org.spongepowered.api.data.manipulator.mutable.entity.FlyingData;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import org.spongepowered.api.data.manipulator.mutable.entity.FuseData;
import org.spongepowered.api.data.manipulator.mutable.entity.GameModeData;
import org.spongepowered.api.data.manipulator.mutable.entity.GlowingData;
import org.spongepowered.api.data.manipulator.mutable.entity.GravityData;
import org.spongepowered.api.data.manipulator.mutable.entity.GriefingData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthScalingData;
import org.spongepowered.api.data.manipulator.mutable.entity.HorseData;
import org.spongepowered.api.data.manipulator.mutable.entity.IgniteableData;
import org.spongepowered.api.data.manipulator.mutable.entity.InvisibilityData;
import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import org.spongepowered.api.data.manipulator.mutable.entity.KnockbackData;
import org.spongepowered.api.data.manipulator.mutable.entity.MinecartBlockData;
import org.spongepowered.api.data.manipulator.mutable.entity.MovementSpeedData;
import org.spongepowered.api.data.manipulator.mutable.entity.OcelotData;
import org.spongepowered.api.data.manipulator.mutable.entity.ParrotData;
import org.spongepowered.api.data.manipulator.mutable.entity.PassengerData;
import org.spongepowered.api.data.manipulator.mutable.entity.PersistingData;
import org.spongepowered.api.data.manipulator.mutable.entity.PickupDelayData;
import org.spongepowered.api.data.manipulator.mutable.entity.PickupRuleData;
import org.spongepowered.api.data.manipulator.mutable.entity.PigSaddleData;
import org.spongepowered.api.data.manipulator.mutable.entity.PlayerCreatedData;
import org.spongepowered.api.data.manipulator.mutable.entity.PlayingData;
import org.spongepowered.api.data.manipulator.mutable.entity.RabbitData;
import org.spongepowered.api.data.manipulator.mutable.entity.RespawnLocationData;
import org.spongepowered.api.data.manipulator.mutable.entity.ScreamingData;
import org.spongepowered.api.data.manipulator.mutable.entity.ShatteringData;
import org.spongepowered.api.data.manipulator.mutable.entity.ShearedData;
import org.spongepowered.api.data.manipulator.mutable.entity.SilentData;
import org.spongepowered.api.data.manipulator.mutable.entity.SittingData;
import org.spongepowered.api.data.manipulator.mutable.entity.SizeData;
import org.spongepowered.api.data.manipulator.mutable.entity.SkinData;
import org.spongepowered.api.data.manipulator.mutable.entity.SleepingData;
import org.spongepowered.api.data.manipulator.mutable.entity.SlimeData;
import org.spongepowered.api.data.manipulator.mutable.entity.SneakingData;
import org.spongepowered.api.data.manipulator.mutable.entity.SprintData;
import org.spongepowered.api.data.manipulator.mutable.entity.StatisticData;
import org.spongepowered.api.data.manipulator.mutable.entity.StuckArrowsData;
import org.spongepowered.api.data.manipulator.mutable.entity.TameableData;
import org.spongepowered.api.data.manipulator.mutable.entity.TradeOfferData;
import org.spongepowered.api.data.manipulator.mutable.entity.VehicleData;
import org.spongepowered.api.data.manipulator.mutable.entity.VelocityData;
import org.spongepowered.api.data.manipulator.mutable.item.AuthorData;
import org.spongepowered.api.data.manipulator.mutable.item.BlockItemData;
import org.spongepowered.api.data.manipulator.mutable.item.BreakableData;
import org.spongepowered.api.data.manipulator.mutable.item.CoalData;
import org.spongepowered.api.data.manipulator.mutable.item.CookedFishData;
import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.manipulator.mutable.item.FishData;
import org.spongepowered.api.data.manipulator.mutable.item.GenerationData;
import org.spongepowered.api.data.manipulator.mutable.item.GoldenAppleData;
import org.spongepowered.api.data.manipulator.mutable.item.HideData;
import org.spongepowered.api.data.manipulator.mutable.item.InventoryItemData;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.data.manipulator.mutable.item.MapItemData;
import org.spongepowered.api.data.manipulator.mutable.item.PagedData;
import org.spongepowered.api.data.manipulator.mutable.item.PlaceableData;
import org.spongepowered.api.data.manipulator.mutable.item.SpawnableData;
import org.spongepowered.api.data.manipulator.mutable.item.StoredEnchantmentData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.BannerData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.BeaconData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.BedData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.BrewingStandData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.CooldownData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.EndGatewayData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.FurnaceData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.LockableData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.NoteData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.StructureData;
import org.spongepowered.api.data.type.Arts;
import org.spongepowered.api.data.type.BigMushroomTypes;
import org.spongepowered.api.data.type.BrickTypes;
import org.spongepowered.api.data.type.Careers;
import org.spongepowered.api.data.type.CoalTypes;
import org.spongepowered.api.data.type.ComparatorTypes;
import org.spongepowered.api.data.type.CookedFishes;
import org.spongepowered.api.data.type.DirtTypes;
import org.spongepowered.api.data.type.DisguisedBlockTypes;
import org.spongepowered.api.data.type.DoublePlantTypes;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.type.Fishes;
import org.spongepowered.api.data.type.GoldenApples;
import org.spongepowered.api.data.type.HandPreferences;
import org.spongepowered.api.data.type.Hinges;
import org.spongepowered.api.data.type.HorseColors;
import org.spongepowered.api.data.type.HorseStyles;
import org.spongepowered.api.data.type.LogAxes;
import org.spongepowered.api.data.type.NotePitches;
import org.spongepowered.api.data.type.OcelotTypes;
import org.spongepowered.api.data.type.ParrotVariants;
import org.spongepowered.api.data.type.PickupRules;
import org.spongepowered.api.data.type.PistonTypes;
import org.spongepowered.api.data.type.PlantTypes;
import org.spongepowered.api.data.type.PortionTypes;
import org.spongepowered.api.data.type.PrismarineTypes;
import org.spongepowered.api.data.type.QuartzTypes;
import org.spongepowered.api.data.type.RabbitTypes;
import org.spongepowered.api.data.type.RailDirections;
import org.spongepowered.api.data.type.SandTypes;
import org.spongepowered.api.data.type.SandstoneTypes;
import org.spongepowered.api.data.type.ShrubTypes;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.data.type.SlabTypes;
import org.spongepowered.api.data.type.StairShapes;
import org.spongepowered.api.data.type.StoneTypes;
import org.spongepowered.api.data.type.StructureModes;
import org.spongepowered.api.data.type.TreeTypes;
import org.spongepowered.api.data.type.WallTypes;
import org.spongepowered.api.data.type.WireAttachmentTypes;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.extra.fluid.data.manipulator.immutable.ImmutableFluidItemData;
import org.spongepowered.api.extra.fluid.data.manipulator.immutable.ImmutableFluidTankData;
import org.spongepowered.api.extra.fluid.data.manipulator.mutable.FluidItemData;
import org.spongepowered.api.extra.fluid.data.manipulator.mutable.FluidTankData;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Axis;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.rotation.Rotations;

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
                c -> c.registerKey(Keys.COLOR, Color.WHITE));
        register(CommandData.class, ImmutableCommandData.class,
                c -> {
                    c.registerKey(Keys.COMMAND, "");
                    c.registerKey(Keys.SUCCESS_COUNT, 0);
                    c.registerKey(Keys.TRACKS_OUTPUT, true);
                    c.registerKey(Keys.LAST_COMMAND_OUTPUT, Optional.empty());
                });
        register(DisplayNameData.class, ImmutableDisplayNameData.class,
                c -> c.registerKey(Keys.DISPLAY_NAME, Text.EMPTY));
        register(FireworkRocketData.class, ImmutableFireworkRocketData.class,
                c -> c.registerKey(Keys.FIREWORK_FLIGHT_MODIFIER, 0));
        register(RepresentedItemData.class, ImmutableRepresentedItemData.class,
                c -> c.registerKey(Keys.REPRESENTED_ITEM, ItemStackSnapshot.NONE));
        register(RepresentedPlayerData.class, ImmutableRepresentedPlayerData.class,
                c -> c.registerKey(Keys.REPRESENTED_PLAYER, LanternGameProfile.UNKNOWN));
        register(TargetedLocationData.class, ImmutableTargetedLocationData.class,
                c -> c.registerKey(Keys.TARGETED_LOCATION, Vector3d.ZERO));
        register(WetData.class, ImmutableWetData.class,
                c -> c.registerKey(Keys.IS_WET, false));

        /// variant containers
        registerVariant(DyeableData.class, ImmutableDyeableData.class, Keys.DYE_COLOR, DyeColors.WHITE);
        registerVariant(RotationalData.class, ImmutableRotationalData.class, Keys.ROTATION, Rotations.LEFT);
        registerVariant(SkullData.class, ImmutableSkullData.class, Keys.SKULL_TYPE, SkullTypes.SKELETON);

        /// list containers
        registerList(FireworkEffectData.class, ImmutableFireworkEffectData.class, Keys.FIREWORK_EFFECTS);
        registerList(PotionEffectData.class, ImmutablePotionEffectData.class, Keys.POTION_EFFECTS);

        ///////////////////
        // Block Package //
        ///////////////////

        /// normal containers
        register(AttachedData.class, ImmutableAttachedData.class,
                c -> c.registerKey(Keys.ATTACHED, false));
        register(ConnectedDirectionData.class, ImmutableConnectedDirectionData.class, LanternConnectedDirectionData.class, LanternImmutableConnectedDirectionData.class,
                c -> {
                    c.registerKey(Keys.CONNECTED_WEST, false);
                    c.registerKey(Keys.CONNECTED_EAST, false);
                    c.registerKey(Keys.CONNECTED_SOUTH, false);
                    c.registerKey(Keys.CONNECTED_NORTH, false);
                });
        register(DecayableData.class, ImmutableDecayableData.class,
                c -> c.registerKey(Keys.DECAYABLE, true));
        register(DelayableData.class, ImmutableDelayableData.class,
                c -> c.registerKey(Keys.DELAY, 1, 0, Integer.MAX_VALUE));
        register(DirectionalData.class, ImmutableDirectionalData.class,
                c -> c.registerKey(Keys.DIRECTION, Direction.NORTH));
        register(DisarmedData.class, ImmutableDisarmedData.class,
                c -> c.registerKey(Keys.DISARMED, false));
        register(DropData.class, ImmutableDropData.class,
                c -> c.registerKey(Keys.SHOULD_DROP, true));
        register(ExtendedData.class, ImmutableExtendedData.class,
                c -> c.registerKey(Keys.EXTENDED, false));
        register(FilledData.class, ImmutableFilledData.class,
                c -> c.registerKey(Keys.FILLED, false));
        register(GrowthData.class, ImmutableGrowthData.class,
                c -> c.registerKey(Keys.GROWTH_STAGE, 0));
        register(InWallData.class, ImmutableInWallData.class,
                c -> c.registerKey(Keys.IN_WALL, false));
        register(LayeredData.class, ImmutableLayeredData.class,
                c -> c.registerKey(Keys.LAYER, 1));
        register(MoistureData.class, ImmutableMoistureData.class,
                c -> c.registerKey(Keys.MOISTURE, 0));
        register(OccupiedData.class, ImmutableOccupiedData.class,
                c -> c.registerKey(Keys.OCCUPIED, false));
        register(OpenData.class, ImmutableOpenData.class,
                c -> c.registerKey(Keys.OPEN, false));
        register(PoweredData.class, ImmutablePoweredData.class,
                c -> c.registerKey(Keys.POWERED, false));
        register(RedstonePoweredData.class, ImmutableRedstonePoweredData.class,
                c -> c.registerKey(Keys.POWER, 0));
        register(SeamlessData.class, ImmutableSeamlessData.class,
                c -> c.registerKey(Keys.SEAMLESS, false));
        register(SnowedData.class, ImmutableSnowedData.class,
                c -> c.registerKey(Keys.SNOWED, false));
        register(WireAttachmentData.class, ImmutableWireAttachmentData.class, LanternWireAttachmentData.class, LanternImmutableWireAttachmentData.class,
                c -> {
                    c.registerKey(Keys.WIRE_ATTACHMENT_WEST, WireAttachmentTypes.NONE);
                    c.registerKey(Keys.WIRE_ATTACHMENT_EAST, WireAttachmentTypes.NONE);
                    c.registerKey(Keys.WIRE_ATTACHMENT_SOUTH, WireAttachmentTypes.NONE);
                    c.registerKey(Keys.WIRE_ATTACHMENT_NORTH, WireAttachmentTypes.NONE);
                });

        /// variant containers
        registerVariant(AxisData.class, ImmutableAxisData.class, Keys.AXIS, Axis.X);
        registerVariant(BigMushroomData.class, ImmutableBigMushroomData.class, Keys.BIG_MUSHROOM_TYPE, BigMushroomTypes.CENTER);
        registerVariant(BrickData.class, ImmutableBrickData.class, Keys.BRICK_TYPE, BrickTypes.DEFAULT);
        registerVariant(ComparatorData.class, ImmutableComparatorData.class, Keys.COMPARATOR_TYPE, ComparatorTypes.COMPARE);
        registerVariant(DirtData.class, ImmutableDirtData.class, Keys.DIRT_TYPE, DirtTypes.DIRT);
        registerVariant(DisguisedBlockData.class, ImmutableDisguisedBlockData.class, Keys.DISGUISED_BLOCK_TYPE, DisguisedBlockTypes.STONE);
        registerVariant(DoublePlantData.class, ImmutableDoublePlantData.class, Keys.DOUBLE_PLANT_TYPE, DoublePlantTypes.GRASS);
        registerVariant(HingeData.class, ImmutableHingeData.class, Keys.HINGE_POSITION, Hinges.LEFT);
        registerVariant(LogAxisData.class, ImmutableLogAxisData.class, Keys.LOG_AXIS, LogAxes.X);
        registerVariant(PistonData.class, ImmutablePistonData.class, Keys.PISTON_TYPE, PistonTypes.NORMAL);
        registerVariant(PlantData.class, ImmutablePlantData.class, Keys.PLANT_TYPE, PlantTypes.POPPY);
        registerVariant(PortionData.class, ImmutablePortionData.class, Keys.PORTION_TYPE, PortionTypes.BOTTOM);
        registerVariant(PrismarineData.class, ImmutablePrismarineData.class, Keys.PRISMARINE_TYPE, PrismarineTypes.BRICKS);
        registerVariant(QuartzData.class, ImmutableQuartzData.class, Keys.QUARTZ_TYPE, QuartzTypes.DEFAULT);
        registerVariant(RailDirectionData.class, ImmutableRailDirectionData.class, Keys.RAIL_DIRECTION, RailDirections.NORTH_SOUTH);
        registerVariant(SandData.class, ImmutableSandData.class, Keys.SAND_TYPE, SandTypes.NORMAL);
        registerVariant(SandstoneData.class, ImmutableSandstoneData.class, Keys.SANDSTONE_TYPE, SandstoneTypes.DEFAULT);
        registerVariant(ShrubData.class, ImmutableShrubData.class, Keys.SHRUB_TYPE, ShrubTypes.DEAD_BUSH);
        registerVariant(SlabData.class, ImmutableSlabData.class, Keys.SLAB_TYPE, SlabTypes.WOOD);
        registerVariant(StairShapeData.class, ImmutableStairShapeData.class, Keys.STAIR_SHAPE, StairShapes.STRAIGHT);
        registerVariant(StoneData.class, ImmutableStoneData.class, Keys.STONE_TYPE, StoneTypes.STONE);
        registerVariant(TreeData.class, ImmutableTreeData.class, Keys.TREE_TYPE, TreeTypes.OAK);
        registerVariant(WallData.class, ImmutableWallData.class, Keys.WALL_TYPE, WallTypes.NORMAL);

        /// list containers

        ////////////////////
        // Entity Package //
        ////////////////////

        /// normal containers
        register(AbsorptionData.class, ImmutableAbsorptionData.class,
                c -> c.registerKey(Keys.ABSORPTION, 0.0));
        register(AffectsSpawningData.class, ImmutableAffectsSpawningData.class,
                c -> c.registerKey(Keys.AFFECTS_SPAWNING, false));
        register(AgeableData.class, ImmutableAgeableData.class,
                c -> {
                    c.registerKey(Keys.AGE, 0);
                    c.registerKey(Keys.IS_ADULT, true);
                });
        register(AgentData.class, ImmutableAgentData.class,
                c -> c.registerKey(Keys.AI_ENABLED, true));
        register(AggressiveData.class, ImmutableAggressiveData.class,
                c -> c.registerKey(Keys.ANGRY, false));
        register(AngerableData.class, ImmutableAngerableData.class,
                c -> c.registerKey(Keys.ANGER, 0));
        register(AreaEffectCloudData.class, ImmutableAreaEffectCloudData.class,
                c -> {
                    c.registerKey(Keys.AREA_EFFECT_CLOUD_COLOR, Color.WHITE);
                    c.registerKey(Keys.AREA_EFFECT_CLOUD_AGE, 0);
                    c.registerKey(Keys.AREA_EFFECT_CLOUD_DURATION, 100);
                    c.registerKey(Keys.AREA_EFFECT_CLOUD_DURATION_ON_USE, 0);
                    c.registerKey(Keys.AREA_EFFECT_CLOUD_PARTICLE_TYPE, ParticleTypes.SPLASH_POTION);
                    c.registerKey(Keys.AREA_EFFECT_CLOUD_RADIUS, 10.0);
                    c.registerKey(Keys.AREA_EFFECT_CLOUD_RADIUS_ON_USE, 0.0);
                    c.registerKey(Keys.AREA_EFFECT_CLOUD_RADIUS_PER_TICK, 0.1);
                    c.registerKey(Keys.AREA_EFFECT_CLOUD_REAPPLICATION_DELAY, 20);
                    c.registerKey(Keys.AREA_EFFECT_CLOUD_WAIT_TIME, 20);
                    c.registerKey(Keys.POTION_EFFECTS, new ArrayList<>());
                });
        register(ArmorStandData.class, ImmutableArmorStandData.class,
                c -> {
                    c.registerKey(Keys.ARMOR_STAND_MARKER, false);
                    c.registerKey(Keys.ARMOR_STAND_IS_SMALL, false);
                    c.registerKey(Keys.ARMOR_STAND_HAS_ARMS, false);
                    c.registerKey(Keys.ARMOR_STAND_HAS_BASE_PLATE, true);
                });
        register(BodyPartRotationalData.class, ImmutableBodyPartRotationalData.class, LanternBodyPartRotationalData.class, LanternImmutableBodyPartRotationalData.class,
                c -> {
                    c.registerKey(Keys.HEAD_ROTATION, Vector3d.ZERO);
                    c.registerKey(Keys.CHEST_ROTATION, Vector3d.ZERO);
                    c.registerKey(Keys.LEFT_ARM_ROTATION, Vector3d.ZERO);
                    c.registerKey(Keys.LEFT_LEG_ROTATION, Vector3d.ZERO);
                    c.registerKey(Keys.RIGHT_ARM_ROTATION, Vector3d.ZERO);
                    c.registerKey(Keys.RIGHT_LEG_ROTATION, Vector3d.ZERO);
                });
        register(BreathingData.class, ImmutableBreathingData.class,
                c -> {
                    c.registerKey(Keys.REMAINING_AIR, 400);
                    c.registerKey(Keys.MAX_AIR, 400);
                });
        register(BreedableData.class, ImmutableBreedableData.class,
                c -> c.registerKey(Keys.CAN_BREED, true));
        register(ChargedData.class, ImmutableChargedData.class,
                c -> c.registerKey(Keys.CREEPER_CHARGED, false));
        register(CriticalHitData.class, ImmutableCriticalHitData.class,
                c -> c.registerKey(Keys.CRITICAL_HIT, false));
        register(CustomNameVisibleData.class, ImmutableCustomNameVisibleData.class,
                c -> c.registerKey(Keys.CUSTOM_NAME_VISIBLE, true));
        register(DamageableData.class, ImmutableDamageableData.class,
                c -> {
                    c.registerKey(Keys.LAST_ATTACKER, Optional.empty());
                    c.registerKey(Keys.LAST_DAMAGE, Optional.empty());
                });
        register(DamagingData.class, ImmutableDamagingData.class,
                c -> {
                    c.registerKey(Keys.ATTACK_DAMAGE, 1.0);
                    c.registerKey(Keys.DAMAGE_ENTITY_MAP, new HashMap<>());
                });
        register(DespawnDelayData.class, ImmutableDespawnDelayData.class,
                c -> c.registerKey(Keys.DESPAWN_DELAY, 2000));
        // TODO: ExperienceHolderData
        register(ExpirableData.class, ImmutableExpirableData.class,
                c -> c.registerKey(Keys.EXPIRATION_TICKS, 200));
        register(ExplosionRadiusData.class, ImmutableExplosionRadiusData.class,
                c -> c.registerKey(Keys.EXPLOSION_RADIUS, Optional.empty()));
        register(ExpOrbData.class, ImmutableExpOrbData.class,
                c -> c.registerKey(Keys.CONTAINED_EXPERIENCE, 1));
        register(FallDistanceData.class, ImmutableFallDistanceData.class,
                c -> c.registerKey(Keys.FALL_DISTANCE, 0f));
        register(FallingBlockData.class, ImmutableFallingBlockData.class,
                c -> {
                    c.registerKey(Keys.FALL_DAMAGE_PER_BLOCK, 0.3);
                    c.registerKey(Keys.FALLING_BLOCK_STATE, BlockTypes.SAND.getDefaultState());
                    c.registerKey(Keys.FALLING_BLOCK_CAN_HURT_ENTITIES, true);
                    c.registerKey(Keys.MAX_FALL_DAMAGE, Double.MAX_VALUE);
                    c.registerKey(Keys.CAN_PLACE_AS_BLOCK, true);
                    c.registerKey(Keys.CAN_DROP_AS_ITEM, true);
                    c.registerKey(Keys.FALL_TIME, 1);
                });
        register(FlammableData.class, ImmutableFlammableData.class,
                c -> c.registerKey(Keys.IS_AFLAME, false));
        register(FlyingAbilityData.class, ImmutableFlyingAbilityData.class,
                c -> c.registerKey(Keys.CAN_FLY, false));
        register(FlyingData.class, ImmutableFlyingData.class,
                c -> c.registerKey(Keys.IS_FLYING, false));
        register(FoodData.class, ImmutableFoodData.class,
                c -> {
                    c.registerKey(Keys.FOOD_LEVEL, 20, 0, LanternKeys.MAX_FOOD_LEVEL);
                    c.registerKey(LanternKeys.MAX_FOOD_LEVEL, 20, 0, Integer.MAX_VALUE);
                    c.registerKey(Keys.EXHAUSTION, 0.0, 0.0, 40.0);
                    c.registerKey(Keys.SATURATION, 0.0, 0.0, 5.0);
                });
        register(FuseData.class, ImmutableFuseData.class,
                c -> c.registerKey(Keys.FUSE_DURATION, 20));
        register(GlowingData.class, ImmutableGlowingData.class,
                c -> c.registerKey(Keys.GLOWING, false));
        register(GravityData.class, ImmutableGravityData.class,
                c -> c.registerKey(Keys.HAS_GRAVITY, true));
        register(GriefingData.class, ImmutableGriefingData.class,
                c -> c.registerKey(Keys.CAN_GRIEF, true));
        register(HealthData.class, ImmutableHealthData.class,
                c -> {
                    c.registerKey(Keys.HEALTH, 20.0, 0.0, Keys.MAX_HEALTH);
                    c.registerKey(Keys.MAX_HEALTH, 20.0, 0.0, Double.MAX_VALUE);
                });
        register(HealthScalingData.class, ImmutableHealthScalingData.class,
                c -> c.registerKey(Keys.HEALTH_SCALE, 1.0));
        register(HorseData.class, ImmutableHorseData.class,
                c -> {
                    c.registerKey(Keys.HORSE_STYLE, HorseStyles.NONE);
                    c.registerKey(Keys.HORSE_COLOR, HorseColors.WHITE);
                });
        register(IgniteableData.class, ImmutableIgniteableData.class,
                c -> {
                    c.registerKey(Keys.FIRE_TICKS, 0, 0, Integer.MAX_VALUE);
                    c.registerKey(Keys.FIRE_DAMAGE_DELAY, 0, 0, Integer.MAX_VALUE);
                });
        register(InvisibilityData.class, ImmutableInvisibilityData.class,
                c -> c.registerKey(Keys.INVISIBLE, false));
        register(JoinData.class, ImmutableJoinData.class,
                c -> {
                    c.registerKey(Keys.FIRST_DATE_PLAYED, Instant.now());
                    c.registerKey(Keys.LAST_DATE_PLAYED, Instant.now());
                });
        register(KnockbackData.class, ImmutableKnockbackData.class,
                c -> c.registerKey(Keys.KNOCKBACK_STRENGTH, 0));
        // TODO: LeashData?
        register(MinecartBlockData.class, ImmutableMinecartBlockData.class,
                c -> {
                    c.registerKey(Keys.REPRESENTED_BLOCK, BlockTypes.STONE.getDefaultState());
                    c.registerKey(Keys.OFFSET, 0);
                });
        register(MovementSpeedData.class, ImmutableMovementSpeedData.class,
                c -> {
                    c.registerKey(Keys.WALKING_SPEED, 0.1);
                    c.registerKey(Keys.FLYING_SPEED, 0.2);
                });
        register(PersistingData.class, ImmutablePersistingData.class,
                c -> c.registerKey(Keys.PERSISTS, false));
        register(PickupDelayData.class, ImmutablePickupDelayData.class,
                c -> {
                    c.registerKey(Keys.PICKUP_DELAY, 0);
                    c.registerKey(Keys.INFINITE_PICKUP_DELAY, false);
                });
        register(PickupRuleData.class, ImmutablePickupRuleData.class,
                c -> c.registerKey(Keys.PICKUP_RULE, PickupRules.ALLOWED));
        register(PigSaddleData.class, ImmutablePigSaddleData.class,
                c -> c.registerKey(Keys.PIG_SADDLE, false));
        register(PlayerCreatedData.class, ImmutablePlayerCreatedData.class,
                c -> c.registerKey(Keys.PLAYER_CREATED, false));
        register(PlayingData.class, ImmutablePlayingData.class,
                c -> c.registerKey(LanternKeys.ARE_PLAYING, false));
        register(ScreamingData.class, ImmutableScreamingData.class,
                c -> c.registerKey(Keys.IS_SCREAMING, false));
        register(ShatteringData.class, ImmutableShatteringData.class,
                c -> c.registerKey(Keys.WILL_SHATTER, false));
        register(ShearedData.class, ImmutableShearedData.class,
                c -> c.registerKey(Keys.IS_SHEARED, false));
        register(SilentData.class, ImmutableSilentData.class,
                c -> c.registerKey(Keys.IS_SILENT, false));
        register(SittingData.class, ImmutableSittingData.class,
                c -> c.registerKey(Keys.IS_SITTING, false));
        register(SizeData.class, ImmutableSizeData.class,
                c -> {
                    c.registerKey(Keys.BASE_SIZE, 1f);
                    c.registerKey(Keys.HEIGHT, 1f);
                    c.registerKey(Keys.SCALE, 1f);
                });
        register(SkinData.class, ImmutableSkinData.class,
                c -> c.registerKey(Keys.SKIN_UNIQUE_ID, LanternGameProfile.UNKNOWN_UUID));
        register(SleepingData.class, ImmutableSleepingData.class,
                c -> c.registerKey(Keys.IS_SLEEPING, false));
        register(SlimeData.class, ImmutableSlimeData.class,
                c -> c.registerKey(Keys.SLIME_SIZE, 1));
        register(SneakingData.class, ImmutableSneakingData.class,
                c -> c.registerKey(Keys.IS_SNEAKING, false));
        register(SprintData.class, ImmutableSprintData.class,
                c -> c.registerKey(Keys.IS_SPRINTING, false));
        register(StuckArrowsData.class, ImmutableStuckArrowsData.class,
                c -> c.registerKey(Keys.STUCK_ARROWS, 0));
        register(TameableData.class, ImmutableTameableData.class,
                c -> c.registerKey(Keys.TAMED_OWNER, Optional.empty()));
        // TODO
        // final EntitySnapshot noneEntitySnapshot = EntitySnapshot.builder().type(EntityTypes.UNKNOWN).build();
        EntitySnapshot noneEntitySnapshot = null;
        register(VehicleData.class, ImmutableVehicleData.class,
                c -> {
                    c.registerKey(Keys.BASE_VEHICLE, noneEntitySnapshot);
                    c.registerKey(Keys.VEHICLE, noneEntitySnapshot);
                });
        register(VelocityData.class, ImmutableVelocityData.class,
                c -> c.registerKey(Keys.VELOCITY, Vector3d.ZERO));

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
                c -> c.registerKey(Keys.FLUID_ITEM_STACK, null)); // TODO

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
                c -> c.registerKey(Keys.BOOK_AUTHOR, Text.EMPTY));
        register(BlockItemData.class, ImmutableBlockItemData.class,
                c -> c.registerKey(Keys.ITEM_BLOCKSTATE, BlockTypes.AIR.getDefaultState()));
        register(BreakableData.class, ImmutableBreakableData.class,
                c -> c.registerKey(Keys.BREAKABLE_BLOCK_TYPES, new HashSet<>()));
        register(DurabilityData.class, ImmutableDurabilityData.class,
                c -> {
                    c.registerKey(Keys.ITEM_DURABILITY, 100);
                    c.registerKey(Keys.UNBREAKABLE, false);
                });
        register(GenerationData.class, ImmutableGenerationData.class,
                c -> c.registerKey(Keys.GENERATION, 0, 0, Integer.MAX_VALUE));
        register(HideData.class, ImmutableHideData.class,
                c -> {
                    c.registerKey(Keys.HIDE_ENCHANTMENTS, false);
                    c.registerKey(Keys.HIDE_ATTRIBUTES, false);
                    c.registerKey(Keys.HIDE_UNBREAKABLE, false);
                    c.registerKey(Keys.HIDE_CAN_DESTROY, false);
                    c.registerKey(Keys.HIDE_CAN_PLACE, false);
                    c.registerKey(Keys.HIDE_MISCELLANEOUS, false);
                });
        register(MapItemData.class, ImmutableMapItemData.class,
                c -> {});
        register(PlaceableData.class, ImmutablePlaceableData.class,
                c -> c.registerKey(Keys.PLACEABLE_BLOCKS, new HashSet<>()));

        /// variant containers
        registerVariant(CoalData.class, ImmutableCoalData.class, Keys.COAL_TYPE, CoalTypes.COAL);
        registerVariant(CookedFishData.class, ImmutableCookedFishData.class, Keys.COOKED_FISH, CookedFishes.COD);
        registerVariant(FishData.class, ImmutableFishData.class, Keys.FISH_TYPE, Fishes.COD);
        registerVariant(GoldenAppleData.class, ImmutableGoldenAppleData.class, Keys.GOLDEN_APPLE_TYPE, GoldenApples.GOLDEN_APPLE);
        registerVariant(SpawnableData.class, ImmutableSpawnableData.class, Keys.SPAWNABLE_ENTITY_TYPE, EntityTypes.CHICKEN);

        /// list containers
        registerList(EnchantmentData.class, ImmutableEnchantmentData.class, Keys.ITEM_ENCHANTMENTS);
        registerList(LoreData.class, ImmutableLoreData.class, Keys.ITEM_LORE);
        registerList(PagedData.class, ImmutablePagedData.class, Keys.BOOK_PAGES);
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
                    c.registerKey(Keys.BANNER_BASE_COLOR, DyeColors.WHITE);
                    c.registerKey(Keys.BANNER_PATTERNS, new ArrayList<>());
                });
        register(BeaconData.class, ImmutableBeaconData.class, LanternBeaconData.class, LanternImmutableBeaconData.class,
                c -> {
                    c.registerKey(Keys.BEACON_PRIMARY_EFFECT, Optional.empty());
                    c.registerKey(Keys.BEACON_SECONDARY_EFFECT, Optional.empty());
                });
        register(BedData.class, ImmutableBedData.class,
                c -> c.registerKey(Keys.DYE_COLOR, DyeColors.WHITE));
        register(BrewingStandData.class, ImmutableBrewingStandData.class,
                c -> c.registerKey(Keys.REMAINING_BREW_TIME, 0, 0, Integer.MAX_VALUE));
        register(CooldownData.class, ImmutableCooldownData.class,
                c -> c.registerKey(Keys.COOLDOWN, 0, 0, Integer.MAX_VALUE));
        register(EndGatewayData.class, ImmutableEndGatewayData.class,
                c -> {
                    c.registerKey(Keys.EXIT_POSITION, Vector3i.ZERO);
                    c.registerKey(Keys.EXACT_TELEPORT, false);
                    c.registerKey(Keys.END_GATEWAY_AGE, 0L);
                    c.registerKey(Keys.END_GATEWAY_TELEPORT_COOLDOWN, 0);
                });
        register(FurnaceData.class, ImmutableFurnaceData.class,
                c -> {
                    c.registerKey(Keys.MAX_BURN_TIME, 0, 0, Integer.MAX_VALUE);
                    c.registerKey(Keys.PASSED_BURN_TIME, 0, 0, Keys.MAX_BURN_TIME);
                    c.registerKey(Keys.MAX_COOK_TIME, 0, 0, Integer.MAX_VALUE);
                    c.registerKey(Keys.PASSED_COOK_TIME, 0, 0, Keys.MAX_COOK_TIME);
                });
        register(LockableData.class, ImmutableLockableData.class,
                c -> c.registerKey(Keys.LOCK_TOKEN, ""));
        register(NoteData.class, ImmutableNoteData.class,
                c -> c.registerKey(Keys.NOTE_PITCH, NotePitches.A1));
        register(StructureData.class, ImmutableStructureData.class,
                c -> {
                    c.registerKey(Keys.STRUCTURE_AUTHOR, "");
                    c.registerKey(Keys.STRUCTURE_IGNORE_ENTITIES, false);
                    c.registerKey(Keys.STRUCTURE_INTEGRITY, 1f);
                    c.registerKey(Keys.STRUCTURE_MODE, StructureModes.DATA);
                    c.registerKey(Keys.STRUCTURE_POSITION, Vector3i.ZERO);
                    c.registerKey(Keys.STRUCTURE_POWERED, false);
                    c.registerKey(Keys.STRUCTURE_SEED, 0L);
                    c.registerKey(Keys.STRUCTURE_SHOW_AIR, false);
                    c.registerKey(Keys.STRUCTURE_SHOW_BOUNDING_BOX, false);
                    c.registerKey(Keys.STRUCTURE_SIZE, Vector3i.ZERO);
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
            return new RegistrationInfo(pluginContainer, ManipulatorHelper.camelToSnake(fullName.substring(fullName.lastIndexOf('.') + 1)),
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
            Class<M> manipulatorType, Class<I> immutableManipulatorType, @Nullable Consumer<IValueContainer<?>> registrationConsumer) {
        return register(manipulatorType, immutableManipulatorType, null, null, registrationConsumer);
    }

    public <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> DataManipulatorRegistration<M, I> register(
            Class<M> manipulatorType, Class<I> immutableManipulatorType,
            @Nullable Class<? extends M> mutableExpansion, @Nullable Class<? extends I> immutableExpansion,
            @Nullable Consumer<IValueContainer<?>> registrationConsumer) {
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
            @Nullable Consumer<IValueContainer<?>> registrationConsumer) {
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
