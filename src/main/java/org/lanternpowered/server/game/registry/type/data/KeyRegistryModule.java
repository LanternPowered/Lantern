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
package org.lanternpowered.server.game.registry.type.data;

import static org.lanternpowered.server.data.key.LanternKeyFactory.makeListKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeMapKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeNextEntityToSpawnKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeOptionalKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makePatternListKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeSetKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeSingleKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeWeightedCollectionKey;
import static org.spongepowered.api.data.DataQuery.of;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.game.registry.CatalogMappingData;
import org.lanternpowered.server.game.registry.CatalogMappingDataHolder;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.type.Art;
import org.spongepowered.api.data.type.BigMushroomType;
import org.spongepowered.api.data.type.BodyPart;
import org.spongepowered.api.data.type.BrickType;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.CoalType;
import org.spongepowered.api.data.type.ComparatorType;
import org.spongepowered.api.data.type.CookedFish;
import org.spongepowered.api.data.type.DirtType;
import org.spongepowered.api.data.type.DisguisedBlockType;
import org.spongepowered.api.data.type.DoublePlantType;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.Fish;
import org.spongepowered.api.data.type.GoldenApple;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.Hinge;
import org.spongepowered.api.data.type.HorseColor;
import org.spongepowered.api.data.type.HorseStyle;
import org.spongepowered.api.data.type.HorseVariant;
import org.spongepowered.api.data.type.LogAxis;
import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.data.type.OcelotType;
import org.spongepowered.api.data.type.PickupRule;
import org.spongepowered.api.data.type.PistonType;
import org.spongepowered.api.data.type.PlantType;
import org.spongepowered.api.data.type.PortionType;
import org.spongepowered.api.data.type.PrismarineType;
import org.spongepowered.api.data.type.Profession;
import org.spongepowered.api.data.type.QuartzType;
import org.spongepowered.api.data.type.RabbitType;
import org.spongepowered.api.data.type.RailDirection;
import org.spongepowered.api.data.type.SandType;
import org.spongepowered.api.data.type.SandstoneType;
import org.spongepowered.api.data.type.ShrubType;
import org.spongepowered.api.data.type.SkeletonType;
import org.spongepowered.api.data.type.SkullType;
import org.spongepowered.api.data.type.SlabType;
import org.spongepowered.api.data.type.StairShape;
import org.spongepowered.api.data.type.StoneType;
import org.spongepowered.api.data.type.TreeType;
import org.spongepowered.api.data.type.WallType;
import org.spongepowered.api.data.type.WireAttachmentType;
import org.spongepowered.api.data.value.immutable.ImmutableBoundedValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.extra.fluid.FluidStackSnapshot;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.registry.RegistryModule;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Axis;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.RespawnLocation;
import org.spongepowered.api.util.rotation.Rotation;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class KeyRegistryModule implements RegistryModule, CatalogMappingDataHolder {

    @Override
    public List<CatalogMappingData> getCatalogMappings() {
        return Collections.singletonList(new CatalogMappingData(Keys.class, ImmutableMap.<String, Object>builder()
                .put("achievements", makeSetKey(Achievement.class, of("Achievements")))
                .put("affects_spawning", makeSingleKey(Boolean.class, Value.class, of("AffectsSpawning")))
                .put("age", makeSingleKey(Integer.class, MutableBoundedValue.class, of("Age")))
                .put("ai_enabled", makeSingleKey(Boolean.class, Value.class, of("AIEnabled")))
                .put("anger", makeSingleKey(Integer.class, MutableBoundedValue.class, of("Anger")))
                .put("armor_stand_has_arms", makeSingleKey(Boolean.class, Value.class, of("ArmorStandHasArms")))
                .put("armor_stand_has_base_plate", makeSingleKey(Boolean.class, Value.class, of("ArmorStandHasBasePlate")))
                .put("armor_stand_has_gravity", makeSingleKey(Boolean.class, Value.class, of("ArmorStandHasGravity")))
                .put("armor_stand_is_small", makeSingleKey(Boolean.class, Value.class, of("ArmorStandIsSmall")))
                .put("armor_stand_marker", makeSingleKey(Boolean.class, Value.class, of("ArmorStandMarker")))
                .put("angry", makeSingleKey(Boolean.class, Value.class, of("Angry")))
                .put("art", makeSingleKey(Art.class, Value.class, of("Art")))
                .put("attached", makeSingleKey(Boolean.class, Value.class, of("Attached")))
                .put("attack_damage", makeSingleKey(Double.class, MutableBoundedValue.class, of("AttackDamage")))
                .put("axis", makeSingleKey(Axis.class, Value.class, of("Axis")))
                .put("banner_base_color", makeSingleKey(DyeColor.class, Value.class, of("BannerBaseColor")))
                .put("banner_patterns", makePatternListKey(of("BannerPatterns")))
                .put("base_size", makeSingleKey(Float.class, MutableBoundedValue.class, of("BaseSize")))
                .put("base_vehicle", makeSingleKey(EntitySnapshot.class, Value.class, of("BaseVehicle")))
                .put("beacon_primary_effect", makeOptionalKey(PotionEffectType.class, of("BeaconPrimaryEffect")))
                .put("beacon_secondary_effect", makeOptionalKey(PotionEffectType.class, of("BeaconSecondaryEffect")))
                .put("big_mushroom_type", makeSingleKey(BigMushroomType.class, Value.class, of("BigMushroomType")))
                .put("body_rotations", makeMapKey(BodyPart.class, Vector3d.class, of("BodyRotations")))
                .put("book_author", makeSingleKey(Text.class, Value.class, of("BookAuthor")))
                .put("book_pages", makeListKey(Text.class, of("BookPages")))
                .put("breakable_block_types", makeSetKey(BlockType.class, of("BreakableBlockTypes")))
                .put("brick_type", makeSingleKey(BrickType.class, Value.class, of("BrickType")))
                .put("can_breed", makeSingleKey(Boolean.class, Value.class, of("CanBreed")))
                .put("can_drop_as_item", makeSingleKey(Boolean.class, Value.class, of("CanDropAsItem")))
                .put("can_fly", makeSingleKey(Boolean.class, Value.class, of("CanFly")))
                .put("can_grief", makeSingleKey(Boolean.class, Value.class, of("CanGrief")))
                .put("can_place_as_block", makeSingleKey(Boolean.class, Value.class, of("CanPlaceAsBlock")))
                .put("career", makeSingleKey(Career.class, Value.class, of("Career")))
                .put("chest_rotation", makeSingleKey(Vector3d.class, Value.class, of("ChestRotation")))
                .put("coal_type", makeSingleKey(CoalType.class, Value.class, of("CoalType")))
                .put("color", makeSingleKey(Color.class, Value.class, of("Color")))
                .put("command", makeSingleKey(String.class, Value.class, of("Command")))
                .put("comparator_type", makeSingleKey(ComparatorType.class, Value.class, of("ComparatorType")))
                .put("connected_directions", makeSetKey(Direction.class, of("ConnectedDirections")))
                .put("connected_east", makeSingleKey(Boolean.class, Value.class, of("ConnectedEast")))
                .put("connected_north", makeSingleKey(Boolean.class, Value.class, of("ConnectedNorth")))
                .put("connected_south", makeSingleKey(Boolean.class, Value.class, of("ConnectedSouth")))
                .put("connected_west", makeSingleKey(Boolean.class, Value.class, of("ConnectedWest")))
                .put("contained_experience", makeSingleKey(Integer.class, MutableBoundedValue.class, of("ContainedExperience")))
                .put("cooked_fish", makeSingleKey(CookedFish.class, Value.class, of("CookedFish")))
                .put("cooldown", makeSingleKey(Integer.class, MutableBoundedValue.class, of("Cooldown")))
                .put("creeper_charged", makeSingleKey(Boolean.class, Value.class, of("CreeperCharged")))
                .put("critical_hit", makeSingleKey(Boolean.class, Value.class, of("CriticalHit")))
                .put("custom_name_visible", makeSingleKey(Boolean.class, Value.class, of("CustomNameVisible")))
                .put("damage_entity_map", makeMapKey(EntityType.class, Double.class, of("EntityDamageMap")))
                .put("decayable", makeSingleKey(Boolean.class, Value.class, of("Decayable")))
                .put("delay", makeSingleKey(Integer.class, MutableBoundedValue.class, of("Delay")))
                .put("direction", makeSingleKey(Direction.class, Value.class, of("Direction")))
                .put("dirt_type", makeSingleKey(DirtType.class, Value.class, of("DirtType")))
                .put("disarmed", makeSingleKey(Boolean.class, Value.class, of("Disarmed")))
                .put("disguised_block_type", makeSingleKey(DisguisedBlockType.class, Value.class, of("DisguisedBlockType")))
                .put("display_name", makeSingleKey(Text.class, Value.class, of("DisplayName")))
                .put("dominant_hand", makeSingleKey(HandType.class, Value.class, of("DominantHand")))
                .put("double_plant_type", makeSingleKey(DoublePlantType.class, Value.class, of("DoublePlantType")))
                .put("dye_color", makeSingleKey(DyeColor.class, Value.class, of("DyeColor")))
                .put("elder_guardian", makeSingleKey(Boolean.class, Value.class, of("ElderGuardian")))
                .put("exhaustion", makeSingleKey(Double.class, MutableBoundedValue.class, of("Exhaustion")))
                .put("experience_from_start_of_level", makeSingleKey(Integer.class, ImmutableBoundedValue.class, of("ExperienceFromStartOfLevel")))
                .put("experience_level", makeSingleKey(Integer.class, MutableBoundedValue.class, of("ExperienceLevel")))
                .put("experience_since_level", makeSingleKey(Integer.class, MutableBoundedValue.class, of("ExperienceSinceLevel")))
                .put("expiration_ticks", makeSingleKey(Integer.class, MutableBoundedValue.class, of("ExpirationTicks")))
                .put("explosion_radius", makeSingleKey(Integer.class, OptionalValue.class, of("ExplosionRadius")))
                .put("extended", makeSingleKey(Boolean.class, Value.class, of("Extended")))
                .put("falling_block_can_hurt_entities", makeSingleKey(Boolean.class, Value.class, of("FallingBlockCanHurtEntities")))
                .put("falling_block_state", makeSingleKey(BlockState.class, Value.class, of("FallingBlockState")))
                .put("fall_damage_per_block", makeSingleKey(Double.class, MutableBoundedValue.class, of("FallDamagePerBlock")))
                .put("fall_distance", makeSingleKey(Float.class, MutableBoundedValue.class, of("FallDistance")))
                .put("fall_time", makeSingleKey(Integer.class, Value.class, of("FallTime")))
                .put("filled", makeSingleKey(Boolean.class, Value.class, of("Filled")))
                .put("firework_effects", makeListKey(FireworkEffect.class, of("FireworkEffects")))
                .put("firework_flight_modifier", makeSingleKey(Integer.class, MutableBoundedValue.class, of("FireworkFlightModifier")))
                .put("fire_damage_delay", makeSingleKey(Integer.class, MutableBoundedValue.class, of("FireDamageDelay")))
                .put("fire_ticks", makeSingleKey(Integer.class, MutableBoundedValue.class, of("FireTicks")))
                .put("first_date_played", makeSingleKey(Instant.class, Value.class, of("FirstDatePlayed")))
                .put("fish_type", makeSingleKey(Fish.class, Value.class, of("FishType")))
                .put("fluid_item_stack", makeSingleKey(FluidStackSnapshot.class, Value.class, of("FluidItemStack")))
                .put("fluid_level", makeSingleKey(Integer.class, MutableBoundedValue.class, of("FluidLevel")))
                .put("fluid_tank_contents", makeMapKey(Direction.class, List.class, of("FluidTankContents")))
                .put("flying_speed", makeSingleKey(Double.class, Value.class, of("FlyingSpeed")))
                .put("food_level", makeSingleKey(Integer.class, MutableBoundedValue.class, of("FoodLevel")))
                .put("fuse_duration", makeSingleKey(Integer.class, Value.class, of("FuseDuration")))
                .put("game_mode", makeSingleKey(GameMode.class, Value.class, of("GameMode")))
                .put("generation", makeSingleKey(Integer.class, MutableBoundedValue.class, of("Generation")))
                .put("golden_apple_type", makeSingleKey(GoldenApple.class, Value.class, of("GoldenAppleType")))
                .put("growth_stage", makeSingleKey(Integer.class, MutableBoundedValue.class, of("GrowthStage")))
                .put("head_rotation", makeSingleKey(Vector3d.class, Value.class, of("HeadRotation")))
                .put("health", makeSingleKey(Double.class, MutableBoundedValue.class, of("Health")))
                .put("health_scale", makeSingleKey(Double.class, MutableBoundedValue.class, of("HealthScale")))
                .put("height", makeSingleKey(Float.class, MutableBoundedValue.class, of("Height")))
                .put("held_experience", makeSingleKey(Integer.class, MutableBoundedValue.class, of("HeldExperience")))
                .put("hide_attributes", makeSingleKey(Boolean.class, Value.class, of("HideAttributes")))
                .put("hide_can_destroy", makeSingleKey(Boolean.class, Value.class, of("HideCanDestroy")))
                .put("hide_can_place", makeSingleKey(Boolean.class, Value.class, of("HideCanPlace")))
                .put("hide_enchantments", makeSingleKey(Boolean.class, Value.class, of("HideEnchantments")))
                .put("hide_miscellaneous", makeSingleKey(Boolean.class, Value.class, of("HideMiscellaneous")))
                .put("hide_unbreakable", makeSingleKey(Boolean.class, Value.class, of("HideUnbreakable")))
                .put("hinge_position", makeSingleKey(Hinge.class, Value.class, of("HingePosition")))
                .put("horse_color", makeSingleKey(HorseColor.class, Value.class, of("HorseColor")))
                .put("horse_style", makeSingleKey(HorseStyle.class, Value.class, of("HorseStyle")))
                .put("horse_variant", makeSingleKey(HorseVariant.class, Value.class, of("HorseVariant")))
                .put("invisibility_ignores_collision", makeSingleKey(Boolean.class, Value.class, of("InvisibilityIgnoresCollision")))
                .put("invisibility_prevents_targeting", makeSingleKey(Boolean.class, Value.class, of("InvisibilityPreventsTargeting")))
                .put("invisible", makeSingleKey(Boolean.class, Value.class, of("Invisible")))
                .put("invulnerability_ticks", makeSingleKey(Integer.class, MutableBoundedValue.class, of("InvulnerabilityTicks")))
                .put("in_wall", makeSingleKey(Boolean.class, Value.class, of("InWall")))
                .put("is_aflame", makeSingleKey(Boolean.class, Value.class, of("IsAflame")))
                .put("is_flying", makeSingleKey(Boolean.class, Value.class, of("IsFlying")))
                .put("is_playing", makeSingleKey(Boolean.class, Value.class, of("IsPlaying")))
                .put("is_screaming", makeSingleKey(Boolean.class, Value.class, of("IsScreaming")))
                .put("is_sheared", makeSingleKey(Boolean.class, Value.class, of("IsSheared")))
                .put("is_silent", makeSingleKey(Boolean.class, Value.class, of("IsSilent")))
                .put("is_sitting", makeSingleKey(Boolean.class, Value.class, of("IsSitting")))
                .put("is_sleeping", makeSingleKey(Boolean.class, Value.class, of("IsSleeping")))
                .put("is_sneaking", makeSingleKey(Boolean.class, Value.class, of("IsSneaking")))
                .put("is_sprinting", makeSingleKey(Boolean.class, Value.class, of("IsSprinting")))
                .put("is_wet", makeSingleKey(Boolean.class, Value.class, of("IsWet")))
                .put("item_blockstate", makeSingleKey(BlockState.class, Value.class, of("ItemBlockState")))
                .put("item_durability", makeSingleKey(Integer.class, MutableBoundedValue.class, of("ItemDurability")))
                .put("item_enchantments", makeListKey(ItemEnchantment.class, of("ItemEnchantments")))
                .put("item_lore", makeListKey(Text.class, of("ItemLore")))
                .put("knockback_strength", makeSingleKey(Integer.class, MutableBoundedValue.class, of("KnockbackStrength")))
                .put("last_attacker", makeOptionalKey(Living.class, of("LastAttacker")))
                .put("last_command_output", makeOptionalKey(Text.class, of("LastCommandOutput")))
                .put("last_damage", makeOptionalKey(Double.class, of("LastDamage")))
                .put("last_date_played", makeSingleKey(Instant.class, Value.class, of("LastDatePlayed")))
                .put("layer", makeSingleKey(Integer.class, MutableBoundedValue.class, of("Layer")))
                .put("leash_holder", makeSingleKey(EntitySnapshot.class, Value.class, of("LeashHolder")))
                .put("left_arm_rotation", makeSingleKey(Vector3d.class, Value.class, of("LeftArmRotation")))
                .put("left_leg_rotation", makeSingleKey(Vector3d.class, Value.class, of("LeftLegRotation")))
                .put("lock_token", makeSingleKey(String.class, Value.class, of("LockToken")))
                .put("log_axis", makeSingleKey(LogAxis.class, Value.class, of("LogAxis")))
                .put("max_air", makeSingleKey(Integer.class, MutableBoundedValue.class, of("MaxAir")))
                .put("max_burn_time", makeSingleKey(Integer.class, MutableBoundedValue.class, of("MaxBurnTime")))
                .put("max_cook_time", makeSingleKey(Integer.class, MutableBoundedValue.class, of("MaxCookTime")))
                .put("max_fall_damage", makeSingleKey(Double.class, MutableBoundedValue.class, of("MaxFallDamage")))
                .put("max_health", makeSingleKey(Double.class, MutableBoundedValue.class, of("MaxHealth")))
                .put("moisture", makeSingleKey(Integer.class, MutableBoundedValue.class, of("Moisture")))
                .put("note_pitch", makeSingleKey(NotePitch.class, Value.class, of("NotePitch")))
                .put("occupied", makeSingleKey(Boolean.class, Value.class, of("Occupied")))
                .put("ocelot_type", makeSingleKey(OcelotType.class, Value.class, of("OcelotType")))
                .put("offset", makeSingleKey(Integer.class, Value.class, of("Offset")))
                .put("open", makeSingleKey(Boolean.class, Value.class, of("Open")))
                .put("passed_burn_time", makeSingleKey(Integer.class, MutableBoundedValue.class, of("PassedBurnTime")))
                .put("passed_cook_time", makeSingleKey(Integer.class, MutableBoundedValue.class, of("PassedCookTime")))
                .put("passengers", makeListKey(EntitySnapshot.class, of("Passengers")))
                .put("persists", makeSingleKey(Boolean.class, Value.class, of("Persists")))
                .put("pickup_rule", makeSingleKey(PickupRule.class, Value.class, of("PickupRule")))
                .put("pig_saddle", makeSingleKey(Boolean.class, Value.class, of("PigSaddle")))
                .put("piston_type", makeSingleKey(PistonType.class, Value.class, of("PistonType")))
                .put("placeable_blocks", makeSetKey(BlockType.class, of("PlaceableBlocks")))
                .put("plant_type", makeSingleKey(PlantType.class, Value.class, of("PlantType")))
                .put("player_created", makeSingleKey(Boolean.class, Value.class, of("PlayerCreated")))
                .put("portion_type", makeSingleKey(PortionType.class, Value.class, of("PortionType")))
                .put("potion_effects", makeListKey(PotionEffect.class, of("PotionEffects")))
                .put("power", makeSingleKey(Integer.class, MutableBoundedValue.class, of("Power")))
                .put("powered", makeSingleKey(Boolean.class, Value.class, of("Powered")))
                .put("prismarine_type", makeSingleKey(PrismarineType.class, Value.class, of("PrismarineType")))
                .put("quartz_type", makeSingleKey(QuartzType.class, Value.class, of("QuartzType")))
                .put("rabbit_type", makeSingleKey(RabbitType.class, Value.class, of("RabbitType")))
                .put("rail_direction", makeSingleKey(RailDirection.class, Value.class, of("RailDirection")))
                .put("remaining_air", makeSingleKey(Integer.class, MutableBoundedValue.class, of("RemainingAir")))
                .put("remaining_brew_time", makeSingleKey(Integer.class, MutableBoundedValue.class, of("RemainingBrewTime")))
                .put("represented_block", makeSingleKey(BlockState.class, Value.class, of("RepresentedBlock")))
                .put("represented_item", makeSingleKey(ItemStackSnapshot.class, Value.class, of("RepresentedItem")))
                .put("represented_player", makeSingleKey(GameProfile.class, Value.class, of("RepresentedPlayer")))
                .put("respawn_locations", makeMapKey(UUID.class, RespawnLocation.class, of("RespawnLocations")))
                .put("right_arm_rotation", makeSingleKey(Vector3d.class, Value.class, of("RightArmRotation")))
                .put("right_leg_rotation", makeSingleKey(Vector3d.class, Value.class, of("RightLegRotation")))
                .put("rotation", makeSingleKey(Rotation.class, Value.class, of("Rotation")))
                .put("sandstone_type", makeSingleKey(SandstoneType.class, Value.class, of("SandstoneType")))
                .put("sand_type", makeSingleKey(SandType.class, Value.class, of("SandType")))
                .put("saturation", makeSingleKey(Double.class, MutableBoundedValue.class, of("Saturation")))
                .put("scale", makeSingleKey(Float.class, MutableBoundedValue.class, of("Scale")))
                .put("seamless", makeSingleKey(Boolean.class, Value.class, of("Seamless")))
                .put("should_drop", makeSingleKey(Boolean.class, Value.class, of("ShouldDrop")))
                .put("shrub_type", makeSingleKey(ShrubType.class, Value.class, of("ShrubType")))
                .put("sign_lines", makeListKey(Text.class, of("SignLines")))
                .put("skeleton_type", makeSingleKey(SkeletonType.class, Value.class, of("SkeletonType")))
                .put("skin_unique_id", makeSingleKey(UUID.class, Value.class, of("SkinUniqueId")))
                .put("skull_type", makeSingleKey(SkullType.class, Value.class, of("SkullType")))
                .put("slab_type", makeSingleKey(SlabType.class, Value.class, of("SlabType")))
                .put("slime_size", makeSingleKey(Integer.class, MutableBoundedValue.class, of("SlimeSize")))
                .put("snowed", makeSingleKey(Boolean.class, Value.class, of("Snowed")))
                .put("spawnable_entity_type", makeSingleKey(EntityType.class, Value.class, of("SpawnableEntityType")))
                .put("spawner_entities", makeWeightedCollectionKey(EntitySnapshot.class, of("SpawnerEntities")))
                .put("spawner_maximum_delay", makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerMaximumDelay")))
                .put("spawner_maximum_nearby_entities", makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerMaximumNearbyEntities")))
                .put("spawner_minimum_delay", makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerMinimumDelay")))
                .put("spawner_next_entity_to_spawn", makeNextEntityToSpawnKey(of("SpawnerNextEntityToSpawn")))
                .put("spawner_remaining_delay", makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerRemainingDelay")))
                .put("spawner_required_player_range", makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerRequiredPlayerRange")))
                .put("spawner_spawn_count", makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerSpawnCount")))
                .put("spawner_spawn_range", makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerSpawnRange")))
                .put("stair_shape", makeSingleKey(StairShape.class, Value.class, of("StairShape")))
                .put("statistics", makeMapKey(Statistic.class, Long.class, of("Statistics")))
                .put("stone_type", makeSingleKey(StoneType.class, Value.class, of("StoneType")))
                .put("stored_enchantments", makeListKey(ItemEnchantment.class, of("StoredEnchantments")))
                .put("stuck_arrows", makeSingleKey(Integer.class, MutableBoundedValue.class, of("StuckArrows")))
                .put("success_count", makeSingleKey(Integer.class, MutableBoundedValue.class, of("SuccessCount")))
                .put("suspended", makeSingleKey(Boolean.class, Value.class, of("Suspended")))
                .put("tamed_owner", makeOptionalKey(UUID.class, of("TamedOwner")))
                .put("targeted_location", makeSingleKey(Vector3d.class, Value.class, of("TargetedLocation")))
                .put("ticks_remaining", makeSingleKey(Integer.class, Value.class, of("TicksRemaining")))
                .put("total_experience", makeSingleKey(Integer.class, MutableBoundedValue.class, of("TotalExperience")))
                .put("tracks_output", makeSingleKey(Boolean.class, Value.class, of("TracksOutput")))
                .put("trade_offers", makeListKey(TradeOffer.class, of("TradeOffers")))
                .put("tree_type", makeSingleKey(TreeType.class, Value.class, of("TreeType")))
                .put("unbreakable", makeSingleKey(Boolean.class, Value.class, of("Unbreakable")))
                .put("vanish", makeSingleKey(Boolean.class, Value.class, of("Vanish")))
                .put("vanish_ignores_collision", makeSingleKey(Boolean.class, Value.class, of("VanishIgnoresCollision")))
                .put("vanish_prevents_targeting", makeSingleKey(Boolean.class, Value.class, of("VanishPreventsTargeting")))
                .put("vehicle", makeSingleKey(EntitySnapshot.class, Value.class, of("Vehicle")))
                .put("velocity", makeSingleKey(Vector3d.class, Value.class, of("Velocity")))
                .put("villager_zombie_profession", makeSingleKey(Profession.class, Value.class, of("VillagerZombieProfession")))
                .put("walking_speed", makeSingleKey(Double.class, Value.class, of("WalkingSpeed")))
                .put("wall_type", makeSingleKey(WallType.class, Value.class, of("WallType")))
                .put("will_shatter", makeSingleKey(Boolean.class, Value.class, of("WillShatter")))
                .put("wire_attachments", makeMapKey(Direction.class, WireAttachmentType.class, of("WireAttachments")))
                .put("wire_attachment_east", makeSingleKey(WireAttachmentType.class, Value.class, of("WireAttachmentEast")))
                .put("wire_attachment_north", makeSingleKey(WireAttachmentType.class, Value.class, of("WireAttachmentNorth")))
                .put("wire_attachment_south", makeSingleKey(WireAttachmentType.class, Value.class, of("WireAttachmentSouth")))
                .put("wire_attachment_west", makeSingleKey(WireAttachmentType.class, Value.class, of("WireAttachmentWest")))
                .build()));
    }
}
