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
package org.lanternpowered.server.game.registry.type.data;

import static org.lanternpowered.server.data.key.LanternKeyFactory.makeImmutableBoundedValueKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeListKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeMapKeyWithKeyAndValue;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeMutableBoundedValueKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeNextEntityToSpawnKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeOptionalKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makePatternListKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeSetKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeValueKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeWeightedCollectionKey;
import static org.spongepowered.api.data.DataQuery.of;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.key.Key;
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
import org.spongepowered.api.data.type.HandPreference;
import org.spongepowered.api.data.type.Hinge;
import org.spongepowered.api.data.type.HorseColor;
import org.spongepowered.api.data.type.HorseStyle;
import org.spongepowered.api.data.type.HorseVariant;
import org.spongepowered.api.data.type.LlamaVariant;
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
import org.spongepowered.api.data.type.ZombieType;
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
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Axis;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.RespawnLocation;
import org.spongepowered.api.util.rotation.Rotation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class KeyRegistryModule extends AdditionalPluginCatalogRegistryModule<Key> {

    public static KeyRegistryModule get() {
        return Holder.INSTANCE;
    }

    private KeyRegistryModule() {
        super(Keys.class);
    }

    @Override
    public void registerDefaults() {
        register(makeSetKey(Achievement.class, of("Achievements"), "sponge:achievements"));
        register(makeValueKey(Boolean.class, of("AffectsSpawning"), "sponge:affects_spawning"));
        register(makeMutableBoundedValueKey(Integer.class, of("Age"), "sponge:age"));
        register(makeValueKey(Boolean.class, of("AIEnabled"), "sponge:ai_enabled"));
        register(makeMutableBoundedValueKey(Integer.class, of("Anger"), "sponge:anger"));
        register(makeValueKey(Boolean.class, of("ArmorStandHasArms"), "sponge:armor_stand_has_arms"));
        register(makeValueKey(Boolean.class, of("ArmorStandHasBasePlate"), "sponge:armor_stand_has_base_plate"));
        register(makeValueKey(Boolean.class, of("ArmorStandIsSmall"), "sponge:armor_stand_is_small"));
        register(makeValueKey(Boolean.class, of("ArmorStandMarker"), "sponge:armor_stand_marker"));
        register(makeValueKey(Boolean.class, of("Angry"), "sponge:angry"));
        register(makeValueKey(Art.class, of("Art"), "sponge:art"));
        register(makeValueKey(Boolean.class, of("Attached"), "sponge:attached"));
        register(makeMutableBoundedValueKey(Double.class, of("AttackDamage"), "sponge:attack_damage"));
        register(makeValueKey(Axis.class, of("Axis"), "sponge:axis"));
        register(makeValueKey(DyeColor.class, of("BannerBaseColor"), "sponge:banner_base_color"));
        register(makePatternListKey(of("BannerPatterns"), "sponge:banner_patterns"));
        register(makeMutableBoundedValueKey(Float.class, of("BaseSize"), "sponge:base_size"));
        register(makeValueKey(EntitySnapshot.class, of("BaseVehicle"), "sponge:base_vehicle"));
        register(makeOptionalKey(PotionEffectType.class, of("BeaconPrimaryEffect"), "sponge:beacon_primary_effect"));
        register(makeOptionalKey(PotionEffectType.class, of("BeaconSecondaryEffect"), "sponge:beacon_secondary_effect"));
        register(makeValueKey(BigMushroomType.class, of("BigMushroomType"), "sponge:big_mushroom_type"));
        register(makeMapKeyWithKeyAndValue(BodyPart.class, Vector3d.class, of("BodyRotations"), "sponge:body_rotations"));
        register(makeValueKey(Text.class, of("BookAuthor"), "sponge:book_author"));
        register(makeListKey(Text.class, of("BookPages"), "sponge:book_pages"));
        register(makeSetKey(BlockType.class, of("BreakableBlockTypes"), "sponge:breakable_block_types"));
        register(makeValueKey(BrickType.class, of("BrickType"), "sponge:brick_type"));
        register(makeValueKey(Boolean.class, of("CanBreed"), "sponge:can_breed"));
        register(makeValueKey(Boolean.class, of("CanDropAsItem"), "sponge:can_drop_as_item"));
        register(makeValueKey(Boolean.class, of("CanFly"), "sponge:can_fly"));
        register(makeValueKey(Boolean.class, of("CanGrief"), "sponge:can_grief"));
        register(makeValueKey(Boolean.class, of("CanPlaceAsBlock"), "sponge:can_place_as_block"));
        register(makeValueKey(Career.class, of("Career"), "sponge:career"));
        register(makeValueKey(Vector3d.class, of("ChestRotation"), "sponge:chest_rotation"));
        register(makeValueKey(CoalType.class, of("CoalType"), "sponge:coal_type"));
        register(makeValueKey(Color.class, of("Color"), "sponge:color"));
        register(makeValueKey(String.class, of("Command"), "sponge:command"));
        register(makeValueKey(ComparatorType.class, of("ComparatorType"), "sponge:comparator_type"));
        register(makeSetKey(Direction.class, of("ConnectedDirections"), "sponge:connected_directions"));
        register(makeValueKey(Boolean.class, of("ConnectedEast"), "sponge:connected_east"));
        register(makeValueKey(Boolean.class, of("ConnectedNorth"), "sponge:connected_north"));
        register(makeValueKey(Boolean.class, of("ConnectedSouth"), "sponge:connected_south"));
        register(makeValueKey(Boolean.class, of("ConnectedWest"), "sponge:connected_west"));
        register(makeMutableBoundedValueKey(Integer.class, of("ContainedExperience"), "sponge:contained_experience"));
        register(makeValueKey(CookedFish.class, of("CookedFish"), "sponge:cooked_fish"));
        register(makeMutableBoundedValueKey(Integer.class, of("Cooldown"), "sponge:cooldown"));
        register(makeValueKey(Boolean.class, of("CreeperCharged"), "sponge:creeper_charged"));
        register(makeValueKey(Boolean.class, of("CriticalHit"), "sponge:critical_hit"));
        register(makeValueKey(Boolean.class, of("CustomNameVisible"), "sponge:custom_name_visible"));
        register(makeMapKeyWithKeyAndValue(EntityType.class, Double.class, of("EntityDamageMap"), "sponge:damage_entity_map"));
        register(makeValueKey(Boolean.class, of("Decayable"), "sponge:decayable"));
        register(makeMutableBoundedValueKey(Integer.class, of("Delay"), "sponge:delay"));
        register(makeMutableBoundedValueKey(Integer.class, of("DespawnDelay"), "sponge:despawn_delay"));
        register(makeValueKey(Direction.class, of("Direction"), "sponge:direction"));
        register(makeValueKey(DirtType.class, of("DirtType"), "sponge:dirt_type"));
        register(makeValueKey(Boolean.class, of("Disarmed"), "sponge:disarmed"));
        register(makeValueKey(DisguisedBlockType.class, of("DisguisedBlockType"), "sponge:disguised_block_type"));
        register(makeValueKey(Text.class, of("DisplayName"), "sponge:display_name"));
        register(makeValueKey(HandPreference.class, of("DominantHand"), "sponge:dominant_hand"));
        register(makeValueKey(DoublePlantType.class, of("DoublePlantType"), "sponge:double_plant_type"));
        register(makeValueKey(DyeColor.class, of("DyeColor"), "sponge:dye_color"));
        register(makeValueKey(Boolean.class, of("ElderGuardian"), "sponge:elder_guardian"));
        register(makeMutableBoundedValueKey(Double.class, of("Exhaustion"), "sponge:exhaustion"));
        register(makeImmutableBoundedValueKey(Integer.class, of("ExperienceFromStartOfLevel"), "sponge:experience_from_start_of_level"));
        register(makeMutableBoundedValueKey(Integer.class, of("ExperienceLevel"), "sponge:experience_level"));
        register(makeMutableBoundedValueKey(Integer.class, of("ExperienceSinceLevel"), "sponge:experience_since_level"));
        register(makeMutableBoundedValueKey(Integer.class, of("ExpirationTicks"), "sponge:expiration_ticks"));
        register(makeOptionalKey(Integer.class, of("ExplosionRadius"), "sponge:explosion_radius"));
        register(makeValueKey(Boolean.class, of("Extended"), "sponge:extended"));
        register(makeValueKey(Boolean.class, of("FallingBlockCanHurtEntities"), "sponge:falling_block_can_hurt_entities"));
        register(makeValueKey(BlockState.class, of("FallingBlockState"), "sponge:falling_block_state"));
        register(makeMutableBoundedValueKey(Double.class, of("FallDamagePerBlock"), "sponge:fall_damage_per_block"));
        register(makeMutableBoundedValueKey(Float.class, of("FallDistance"), "sponge:fall_distance"));
        register(makeValueKey(Integer.class, of("FallTime"), "sponge:fall_time"));
        register(makeValueKey(Boolean.class, of("Filled"), "sponge:filled"));
        register(makeListKey(FireworkEffect.class, of("FireworkEffects"), "sponge:firework_effects"));
        register(makeMutableBoundedValueKey(Integer.class, of("FireworkFlightModifier"), "sponge:firework_flight_modifier"));
        register(makeMutableBoundedValueKey(Integer.class, of("FireDamageDelay"), "sponge:fire_damage_delay"));
        register(makeMutableBoundedValueKey(Integer.class, of("FireTicks"), "sponge:fire_ticks"));
        register(makeValueKey(Instant.class, of("FirstDatePlayed"), "sponge:first_date_played"));
        register(makeValueKey(Fish.class, of("FishType"), "sponge:fish_type"));
        register(makeValueKey(FluidStackSnapshot.class, of("FluidItemStack"), "sponge:fluid_item_stack"));
        register(makeMutableBoundedValueKey(Integer.class, of("FluidLevel"), "sponge:fluid_level"));
        register(makeMapKeyWithKeyAndValue(Direction.class, List.class, of("FluidTankContents"), "sponge:fluid_tank_contents"));
        register(makeValueKey(Double.class, of("FlyingSpeed"), "sponge:flying_speed"));
        register(makeMutableBoundedValueKey(Integer.class, of("FoodLevel"), "sponge:food_level"));
        register(makeValueKey(Integer.class, of("FuseDuration"), "sponge:fuse_duration"));
        register(makeValueKey(GameMode.class, of("GameMode"), "sponge:game_mode"));
        register(makeMutableBoundedValueKey(Integer.class, of("Generation"), "sponge:generation"));
        register(makeValueKey(Boolean.class, of("Glowing"), "sponge:glowing"));
        register(makeValueKey(GoldenApple.class, of("GoldenAppleType"), "sponge:golden_apple_type"));
        register(makeMutableBoundedValueKey(Integer.class, of("GrowthStage"), "sponge:growth_stage"));
        register(makeValueKey(Boolean.class, of("HasGravity"), "sponge:has_gravity"));
        register(makeValueKey(Vector3d.class, of("HeadRotation"), "sponge:head_rotation"));
        register(makeMutableBoundedValueKey(Double.class, of("Health"), "sponge:health"));
        register(makeMutableBoundedValueKey(Double.class, of("HealthScale"), "sponge:health_scale"));
        register(makeMutableBoundedValueKey(Float.class, of("Height"), "sponge:height"));
        register(makeMutableBoundedValueKey(Integer.class, of("HeldExperience"), "sponge:held_experience"));
        register(makeValueKey(Boolean.class, of("HideAttributes"), "sponge:hide_attributes"));
        register(makeValueKey(Boolean.class, of("HideCanDestroy"), "sponge:hide_can_destroy"));
        register(makeValueKey(Boolean.class, of("HideCanPlace"), "sponge:hide_can_place"));
        register(makeValueKey(Boolean.class, of("HideEnchantments"), "sponge:hide_enchantments"));
        register(makeValueKey(Boolean.class, of("HideMiscellaneous"), "sponge:hide_miscellaneous"));
        register(makeValueKey(Boolean.class, of("HideUnbreakable"), "sponge:hide_unbreakable"));
        register(makeValueKey(Hinge.class, of("HingePosition"), "sponge:hinge_position"));
        register(makeValueKey(HorseColor.class, of("HorseColor"), "sponge:horse_color"));
        register(makeValueKey(HorseStyle.class, of("HorseStyle"), "sponge:horse_style"));
        register(makeValueKey(HorseVariant.class, of("HorseVariant"), "sponge:horse_variant"));
        register(makeValueKey(Boolean.class, of("InfiniteDespawnDelay"), "sponge:infinite_despawn_delay"));
        register(makeValueKey(Boolean.class, of("InfinitePickupDelay"), "sponge:infinite_pickup_delay"));
        register(makeValueKey(Boolean.class, of("InvisibilityIgnoresCollision"), "sponge:invisibility_ignores_collision"));
        register(makeValueKey(Boolean.class, of("InvisibilityPreventsTargeting"), "sponge:invisibility_prevents_targeting"));
        register(makeValueKey(Boolean.class, of("Invisible"), "sponge:invisible"));
        register(makeMutableBoundedValueKey(Integer.class, of("InvulnerabilityTicks"), "sponge:invulnerability_ticks"));
        register(makeValueKey(Boolean.class, of("InWall"), "sponge:in_wall"));
        register(makeValueKey(Boolean.class, of("IsAflame"), "sponge:is_aflame"));
        register(makeValueKey(Boolean.class, of("IsFlying"), "sponge:is_flying"));
        register(makeValueKey(Boolean.class, of("IsPlaying"), "sponge:is_playing"));
        register(makeValueKey(Boolean.class, of("IsScreaming"), "sponge:is_screaming"));
        register(makeValueKey(Boolean.class, of("IsSheared"), "sponge:is_sheared"));
        register(makeValueKey(Boolean.class, of("IsSilent"), "sponge:is_silent"));
        register(makeValueKey(Boolean.class, of("IsSitting"), "sponge:is_sitting"));
        register(makeValueKey(Boolean.class, of("IsSleeping"), "sponge:is_sleeping"));
        register(makeValueKey(Boolean.class, of("IsSneaking"), "sponge:is_sneaking"));
        register(makeValueKey(Boolean.class, of("IsSprinting"), "sponge:is_sprinting"));
        register(makeValueKey(Boolean.class, of("IsWet"), "sponge:is_wet"));
        register(makeValueKey(BlockState.class, of("ItemBlockState"), "sponge:item_blockstate"));
        register(makeMutableBoundedValueKey(Integer.class, of("ItemDurability"), "sponge:item_durability"));
        register(makeListKey(ItemEnchantment.class, of("ItemEnchantments"), "sponge:item_enchantments"));
        register(makeListKey(Text.class, of("ItemLore"), "sponge:item_lore"));
        register(makeValueKey(Boolean.class, of("JohnnyVindicator"), "sponge:johnny_vindicator"));
        register(makeMutableBoundedValueKey(Integer.class, of("KnockbackStrength"), "sponge:knockback_strength"));
        register(makeOptionalKey(Living.class, of("LastAttacker"), "sponge:last_attacker"));
        register(makeOptionalKey(Text.class, of("LastCommandOutput"), "sponge:last_command_output"));
        register(makeOptionalKey(Double.class, of("LastDamage"), "sponge:last_damage"));
        register(makeValueKey(Instant.class, of("LastDatePlayed"), "sponge:last_date_played"));
        register(makeValueKey(Integer.class, of("Layer"), "sponge:layer"));
        register(makeValueKey(EntitySnapshot.class, of("LeashHolder"), "sponge:leash_holder"));
        register(makeValueKey(Vector3d.class, of("LeftArmRotation"), "sponge:left_arm_rotation"));
        register(makeValueKey(Vector3d.class, of("LeftLegRotation"), "sponge:left_leg_rotation"));
        register(makeMutableBoundedValueKey(Integer.class, of("LlamaStrength"), "sponge:llama_strength"));
        register(makeValueKey(LlamaVariant.class, of("LlamaVariant"), "sponge:llama_variant"));
        register(makeValueKey(String.class, of("LockToken"), "sponge:lock_token"));
        register(makeValueKey(LogAxis.class, of("LogAxis"), "sponge:log_axis"));
        register(makeMutableBoundedValueKey(Integer.class, of("MaxAir"), "sponge:max_air"));
        register(makeMutableBoundedValueKey(Integer.class, of("MaxBurnTime"), "sponge:max_burn_time"));
        register(makeMutableBoundedValueKey(Integer.class, of("MaxCookTime"), "sponge:max_cook_time"));
        register(makeMutableBoundedValueKey(Double.class, of("MaxFallDamage"), "sponge:max_fall_damage"));
        register(makeMutableBoundedValueKey(Double.class, of("MaxHealth"), "sponge:max_health"));
        register(makeMutableBoundedValueKey(Integer.class, of("Moisture"), "sponge:moisture"));
        register(makeValueKey(NotePitch.class, of("NotePitch"), "sponge:note_pitch"));
        register(makeValueKey(Boolean.class, of("Occupied"), "sponge:occupied"));
        register(makeValueKey(OcelotType.class, of("OcelotType"), "sponge:ocelot_type"));
        register(makeValueKey(Integer.class, of("Offset"), "sponge:offset"));
        register(makeValueKey(Boolean.class, of("Open"), "sponge:open"));
        register(makeMutableBoundedValueKey(Integer.class, of("PassedBurnTime"), "sponge:passed_burn_time"));
        register(makeMutableBoundedValueKey(Integer.class, of("PassedCookTime"), "sponge:passed_cook_time"));
        register(makeListKey(EntitySnapshot.class, of("Passengers"), "sponge:passengers"));
        register(makeValueKey(Boolean.class, of("Persists"), "sponge:persists"));
        register(makeMutableBoundedValueKey(Integer.class, of("PickupDelay"), "sponge:pickup_delay"));
        register(makeValueKey(PickupRule.class, of("PickupRule"), "sponge:pickup_rule"));
        register(makeValueKey(Boolean.class, of("PigSaddle"), "sponge:pig_saddle"));
        register(makeValueKey(PistonType.class, of("PistonType"), "sponge:piston_type"));
        register(makeSetKey(BlockType.class, of("PlaceableBlocks"), "sponge:placeable_blocks"));
        register(makeValueKey(PlantType.class, of("PlantType"), "sponge:plant_type"));
        register(makeValueKey(Boolean.class, of("PlayerCreated"), "sponge:player_created"));
        register(makeValueKey(PortionType.class, of("PortionType"), "sponge:portion_type"));
        register(makeListKey(PotionEffect.class, of("PotionEffects"), "sponge:potion_effects"));
        register(makeValueKey(Integer.class, of("Power"), "sponge:power"));
        register(makeValueKey(Boolean.class, of("Powered"), "sponge:powered"));
        register(makeValueKey(PrismarineType.class, of("PrismarineType"), "sponge:prismarine_type"));
        register(makeValueKey(QuartzType.class, of("QuartzType"), "sponge:quartz_type"));
        register(makeValueKey(RabbitType.class, of("RabbitType"), "sponge:rabbit_type"));
        register(makeValueKey(RailDirection.class, of("RailDirection"), "sponge:rail_direction"));
        register(makeMutableBoundedValueKey(Integer.class, of("RemainingAir"), "sponge:remaining_air"));
        register(makeMutableBoundedValueKey(Integer.class, of("RemainingBrewTime"), "sponge:remaining_brew_time"));
        register(makeValueKey(BlockState.class, of("RepresentedBlock"), "sponge:represented_block"));
        register(makeValueKey(ItemStackSnapshot.class, of("RepresentedItem"), "sponge:represented_item"));
        register(makeValueKey(GameProfile.class, of("RepresentedPlayer"), "sponge:represented_player"));
        register(makeMapKeyWithKeyAndValue(UUID.class, RespawnLocation.class, of("RespawnLocations"), "sponge:respawn_locations"));
        register(makeValueKey(Vector3d.class, of("RightArmRotation"), "sponge:right_arm_rotation"));
        register(makeValueKey(Vector3d.class, of("RightLegRotation"), "sponge:right_leg_rotation"));
        register(makeValueKey(Rotation.class, of("Rotation"), "sponge:rotation"));
        register(makeValueKey(SandstoneType.class, of("SandstoneType"), "sponge:sandstone_type"));
        register(makeValueKey(SandType.class, of("SandType"), "sponge:sand_type"));
        register(makeMutableBoundedValueKey(Double.class, of("Saturation"), "sponge:saturation"));
        register(makeMutableBoundedValueKey(Float.class, of("Scale"), "sponge:scale"));
        register(makeValueKey(Boolean.class, of("Seamless"), "sponge:seamless"));
        register(makeValueKey(Boolean.class, of("ShouldDrop"), "sponge:should_drop"));
        register(makeValueKey(ShrubType.class, of("ShrubType"), "sponge:shrub_type"));
        register(makeListKey(Text.class, of("SignLines"), "sponge:sign_lines"));
        register(makeValueKey(SkeletonType.class, of("SkeletonType"), "sponge:skeleton_type"));
        register(makeValueKey(UUID.class, of("SkinUniqueId"), "sponge:skin_unique_id"));
        register(makeValueKey(SkullType.class, of("SkullType"), "sponge:skull_type"));
        register(makeValueKey(SlabType.class, of("SlabType"), "sponge:slab_type"));
        register(makeMutableBoundedValueKey(Integer.class, of("SlimeSize"), "sponge:slime_size"));
        register(makeValueKey(Boolean.class, of("Snowed"), "sponge:snowed"));
        register(makeValueKey(EntityType.class, of("SpawnableEntityType"), "sponge:spawnable_entity_type"));
        register(makeWeightedCollectionKey(EntitySnapshot.class, of("SpawnerEntities"), "sponge:spawner_entities"));
        register(makeMutableBoundedValueKey(Short.class, of("SpawnerMaximumDelay"), "sponge:spawner_maximum_delay"));
        register(makeMutableBoundedValueKey(Short.class, of("SpawnerMaximumNearbyEntities"), "sponge:spawner_maximum_nearby_entities"));
        register(makeMutableBoundedValueKey(Short.class, of("SpawnerMinimumDelay"), "sponge:spawner_minimum_delay"));
        register(makeNextEntityToSpawnKey(of("SpawnerNextEntityToSpawn"), "sponge:spawner_next_entity_to_spawn"));
        register(makeMutableBoundedValueKey(Short.class, of("SpawnerRemainingDelay"), "sponge:spawner_remaining_delay"));
        register(makeMutableBoundedValueKey(Short.class, of("SpawnerRequiredPlayerRange"), "sponge:spawner_required_player_range"));
        register(makeMutableBoundedValueKey(Short.class, of("SpawnerSpawnCount"), "sponge:spawner_spawn_count"));
        register(makeMutableBoundedValueKey(Short.class, of("SpawnerSpawnRange"), "sponge:spawner_spawn_range"));
        register(makeValueKey(StairShape.class, of("StairShape"), "sponge:stair_shape"));
        register(makeMapKeyWithKeyAndValue(Statistic.class, Long.class, of("Statistics"), "sponge:statistics"));
        register(makeValueKey(StoneType.class, of("StoneType"), "sponge:stone_type"));
        register(makeListKey(ItemEnchantment.class, of("StoredEnchantments"), "sponge:stored_enchantments"));
        register(makeMutableBoundedValueKey(Integer.class, of("StuckArrows"), "sponge:stuck_arrows"));
        register(makeMutableBoundedValueKey(Integer.class, of("SuccessCount"), "sponge:success_count"));
        register(makeValueKey(Boolean.class, of("Suspended"), "sponge:suspended"));
        register(makeOptionalKey(UUID.class, of("TamedOwner"), "sponge:tamed_owner"));
        register(makeValueKey(Vector3d.class, of("TargetedLocation"), "sponge:targeted_location"));
        register(makeValueKey(Integer.class, of("TicksRemaining"), "sponge:ticks_remaining"));
        register(makeMutableBoundedValueKey(Integer.class, of("TotalExperience"), "sponge:total_experience"));
        register(makeValueKey(Boolean.class, of("TracksOutput"), "sponge:tracks_output"));
        register(makeListKey(TradeOffer.class, of("TradeOffers"), "sponge:trade_offers"));
        register(makeValueKey(TreeType.class, of("TreeType"), "sponge:tree_type"));
        register(makeValueKey(Boolean.class, of("Unbreakable"), "sponge:unbreakable"));
        register(makeValueKey(Boolean.class, of("Vanish"), "sponge:vanish"));
        register(makeValueKey(Boolean.class, of("VanishIgnoresCollision"), "sponge:vanish_ignores_collision"));
        register(makeValueKey(Boolean.class, of("VanishPreventsTargeting"), "sponge:vanish_prevents_targeting"));
        register(makeValueKey(EntitySnapshot.class, of("Vehicle"), "sponge:vehicle"));
        register(makeValueKey(Vector3d.class, of("Velocity"), "sponge:velocity"));
        register(makeOptionalKey(Profession.class, of("VillagerZombieProfession"), "sponge:villager_zombie_profession"));
        register(makeValueKey(Double.class, of("WalkingSpeed"), "sponge:walking_speed"));
        register(makeValueKey(WallType.class, of("WallType"), "sponge:wall_type"));
        register(makeValueKey(Boolean.class, of("WillShatter"), "sponge:will_shatter"));
        register(makeMapKeyWithKeyAndValue(Direction.class, WireAttachmentType.class, of("WireAttachments"), "sponge:wire_attachments"));
        register(makeValueKey(WireAttachmentType.class, of("WireAttachmentEast"), "sponge:wire_attachment_east"));
        register(makeValueKey(WireAttachmentType.class, of("WireAttachmentNorth"), "sponge:wire_attachment_north"));
        register(makeValueKey(WireAttachmentType.class, of("WireAttachmentSouth"), "sponge:wire_attachment_south"));
        register(makeValueKey(WireAttachmentType.class, of("WireAttachmentWest"), "sponge:wire_attachment_west"));
        register(makeValueKey(ZombieType.class, of("ZombieType"), "sponge:zombie_type"));

        // Register the lantern keys
        for (Field field : LanternKeys.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                final Object object;
                try {
                    object = field.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if (object instanceof Key) {
                    register((Key) object);
                }
            }
        }
    }

    private final static class Holder {
        private static final KeyRegistryModule INSTANCE = new KeyRegistryModule();
    }
}
