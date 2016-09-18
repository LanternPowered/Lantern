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

import static org.lanternpowered.server.data.key.LanternKeyFactory.makeImmutableBoundedValueKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeListKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeMapKeyWithKeyAndValue;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeMutableBoundedValueKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeNextEntityToSpawnKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeOptionalKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makePatternListKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeSetKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeSingleKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeValueKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeWeightedCollectionKey;
import static org.spongepowered.api.data.DataQuery.of;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
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

public final class KeyRegistryModule extends PluginCatalogRegistryModule<Key> {

    public static KeyRegistryModule get() {
        return Holder.INSTANCE;
    }

    private KeyRegistryModule() {
        super(Keys.class);
    }

    @Override
    public void registerDefaults() {this.register(makeSetKey(Achievement.class, of("Achievements"), "sponge:achievements"));
        this.register(makeValueKey(Boolean.class, of("AffectsSpawning"), "sponge:affects_spawning"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("Age"), "sponge:age"));
        this.register(makeValueKey(Boolean.class, of("AIEnabled"), "sponge:ai_enabled"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("Anger"), "sponge:anger"));
        this.register(makeValueKey(Boolean.class, of("ArmorStandHasArms"), "sponge:armor_stand_has_arms"));
        this.register(makeValueKey(Boolean.class, of("ArmorStandHasBasePlate"), "sponge:armor_stand_has_base_plate"));
        this.register(makeValueKey(Boolean.class, of("ArmorStandHasGravity"), "sponge:armor_stand_has_gravity"));
        this.register(makeValueKey(Boolean.class, of("ArmorStandIsSmall"), "sponge:armor_stand_is_small"));
        this.register(makeValueKey(Boolean.class, of("ArmorStandMarker"), "sponge:armor_stand_marker"));
        this.register(makeValueKey(Boolean.class, of("Angry"), "sponge:angry"));
        this.register(makeValueKey(Art.class, of("Art"), "sponge:art"));
        this.register(makeValueKey(Boolean.class, of("Attached"), "sponge:attached"));
        this.register(makeMutableBoundedValueKey(Double.class, of("AttackDamage"), "sponge:attack_damage"));
        this.register(makeValueKey(Axis.class, of("Axis"), "sponge:axis"));
        this.register(makeValueKey(DyeColor.class, of("BannerBaseColor"), "sponge:banner_base_color"));
        this.register(makePatternListKey(of("BannerPatterns"), "sponge:banner_patterns"));
        this.register(makeMutableBoundedValueKey(Float.class, of("BaseSize"), "sponge:base_size"));
        this.register(makeValueKey(EntitySnapshot.class, of("BaseVehicle"), "sponge:base_vehicle"));
        this.register(makeOptionalKey(PotionEffectType.class, of("BeaconPrimaryEffect"), "sponge:beacon_primary_effect"));
        this.register(makeOptionalKey(PotionEffectType.class, of("BeaconSecondaryEffect"), "sponge:beacon_secondary_effect"));
        this.register(makeValueKey(BigMushroomType.class, of("BigMushroomType"), "sponge:big_mushroom_type"));
        this.register(makeMapKeyWithKeyAndValue(BodyPart.class, Vector3d.class, of("BodyRotations"), "sponge:body_rotations"));
        this.register(makeValueKey(Text.class, of("BookAuthor"), "sponge:book_author"));
        this.register(makeListKey(Text.class, of("BookPages"), "sponge:book_pages"));
        this.register(makeSetKey(BlockType.class, of("BreakableBlockTypes"), "sponge:breakable_block_types"));
        this.register(makeValueKey(BrickType.class, of("BrickType"), "sponge:brick_type"));
        this.register(makeValueKey(Boolean.class, of("CanBreed"), "sponge:can_breed"));
        this.register(makeValueKey(Boolean.class, of("CanDropAsItem"), "sponge:can_drop_as_item"));
        this.register(makeValueKey(Boolean.class, of("CanFly"), "sponge:can_fly"));
        this.register(makeValueKey(Boolean.class, of("CanGrief"), "sponge:can_grief"));
        this.register(makeValueKey(Boolean.class, of("CanPlaceAsBlock"), "sponge:can_place_as_block"));
        this.register(makeValueKey(Career.class, of("Career"), "sponge:career"));
        this.register(makeValueKey(Vector3d.class, of("ChestRotation"), "sponge:chest_rotation"));
        this.register(makeValueKey(CoalType.class, of("CoalType"), "sponge:coal_type"));
        this.register(makeValueKey(Color.class, of("Color"), "sponge:color"));
        this.register(makeValueKey(String.class, of("Command"), "sponge:command"));
        this.register(makeValueKey(ComparatorType.class, of("ComparatorType"), "sponge:comparator_type"));
        this.register(makeSetKey(Direction.class, of("ConnectedDirections"), "sponge:connected_directions"));
        this.register(makeValueKey(Boolean.class, of("ConnectedEast"), "sponge:connected_east"));
        this.register(makeValueKey(Boolean.class, of("ConnectedNorth"), "sponge:connected_north"));
        this.register(makeValueKey(Boolean.class, of("ConnectedSouth"), "sponge:connected_south"));
        this.register(makeValueKey(Boolean.class, of("ConnectedWest"), "sponge:connected_west"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("ContainedExperience"), "sponge:contained_experience"));
        this.register(makeValueKey(CookedFish.class, of("CookedFish"), "sponge:cooked_fish"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("Cooldown"), "sponge:cooldown"));
        this.register(makeValueKey(Boolean.class, of("CreeperCharged"), "sponge:creeper_charged"));
        this.register(makeValueKey(Boolean.class, of("CriticalHit"), "sponge:critical_hit"));
        this.register(makeValueKey(Boolean.class, of("CustomNameVisible"), "sponge:custom_name_visible"));
        this.register(makeMapKeyWithKeyAndValue(EntityType.class, Double.class, of("EntityDamageMap"), "sponge:damage_entity_map"));
        this.register(makeValueKey(Boolean.class, of("Decayable"), "sponge:decayable"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("Delay"), "sponge:delay"));
        this.register(makeValueKey(Direction.class, of("Direction"), "sponge:direction"));
        this.register(makeValueKey(DirtType.class, of("DirtType"), "sponge:dirt_type"));
        this.register(makeValueKey(Boolean.class, of("Disarmed"), "sponge:disarmed"));
        this.register(makeValueKey(DisguisedBlockType.class, of("DisguisedBlockType"), "sponge:disguised_block_type"));
        this.register(makeValueKey(Text.class, of("DisplayName"), "sponge:display_name"));
        this.register(makeValueKey(HandType.class, of("DominantHand"), "sponge:dominant_hand"));
        this.register(makeValueKey(DoublePlantType.class, of("DoublePlantType"), "sponge:double_plant_type"));
        this.register(makeValueKey(DyeColor.class, of("DyeColor"), "sponge:dye_color"));
        this.register(makeValueKey(Boolean.class, of("ElderGuardian"), "sponge:elder_guardian"));
        this.register(makeMutableBoundedValueKey(Double.class, of("Exhaustion"), "sponge:exhaustion"));
        this.register(makeImmutableBoundedValueKey(Integer.class, of("ExperienceFromStartOfLevel"), "sponge:experience_from_start_of_level"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("ExperienceLevel"), "sponge:experience_level"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("ExperienceSinceLevel"), "sponge:experience_since_level"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("ExpirationTicks"), "sponge:expiration_ticks"));
        this.register(makeOptionalKey(Integer.class, of("ExplosionRadius"), "sponge:explosion_radius"));
        this.register(makeValueKey(Boolean.class, of("Extended"), "sponge:extended"));
        this.register(makeValueKey(Boolean.class, of("FallingBlockCanHurtEntities"), "sponge:falling_block_can_hurt_entities"));
        this.register(makeValueKey(BlockState.class, of("FallingBlockState"), "sponge:falling_block_state"));
        this.register(makeMutableBoundedValueKey(Double.class, of("FallDamagePerBlock"), "sponge:fall_damage_per_block"));
        this.register(makeMutableBoundedValueKey(Float.class, of("FallDistance"), "sponge:fall_distance"));
        this.register(makeValueKey(Integer.class, of("FallTime"), "sponge:fall_time"));
        this.register(makeValueKey(Boolean.class, of("Filled"), "sponge:filled"));
        this.register(makeListKey(FireworkEffect.class, of("FireworkEffects"), "sponge:firework_effects"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("FireworkFlightModifier"), "sponge:firework_flight_modifier"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("FireDamageDelay"), "sponge:fire_damage_delay"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("FireTicks"), "sponge:fire_ticks"));
        this.register(makeValueKey(Instant.class, of("FirstDatePlayed"), "sponge:first_date_played"));
        this.register(makeValueKey(Fish.class, of("FishType"), "sponge:fish_type"));
        this.register(makeValueKey(FluidStackSnapshot.class, of("FluidItemStack"), "sponge:fluid_item_stack"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("FluidLevel"), "sponge:fluid_level"));
        this.register(makeMapKeyWithKeyAndValue(Direction.class, List.class, of("FluidTankContents"), "sponge:fluid_tank_contents"));
        this.register(makeValueKey(Double.class, of("FlyingSpeed"), "sponge:flying_speed"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("FoodLevel"), "sponge:food_level"));
        this.register(makeValueKey(Integer.class, of("FuseDuration"), "sponge:fuse_duration"));
        this.register(makeValueKey(GameMode.class, of("GameMode"), "sponge:game_mode"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("Generation"), "sponge:generation"));
        this.register(makeValueKey(GoldenApple.class, of("GoldenAppleType"), "sponge:golden_apple_type"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("GrowthStage"), "sponge:growth_stage"));
        this.register(makeValueKey(Vector3d.class, of("HeadRotation"), "sponge:head_rotation"));
        this.register(makeMutableBoundedValueKey(Double.class, of("Health"), "sponge:health"));
        this.register(makeMutableBoundedValueKey(Double.class, of("HealthScale"), "sponge:health_scale"));
        this.register(makeMutableBoundedValueKey(Float.class, of("Height"), "sponge:height"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("HeldExperience"), "sponge:held_experience"));
        this.register(makeValueKey(Boolean.class, of("HideAttributes"), "sponge:hide_attributes"));
        this.register(makeValueKey(Boolean.class, of("HideCanDestroy"), "sponge:hide_can_destroy"));
        this.register(makeValueKey(Boolean.class, of("HideCanPlace"), "sponge:hide_can_place"));
        this.register(makeValueKey(Boolean.class, of("HideEnchantments"), "sponge:hide_enchantments"));
        this.register(makeValueKey(Boolean.class, of("HideMiscellaneous"), "sponge:hide_miscellaneous"));
        this.register(makeValueKey(Boolean.class, of("HideUnbreakable"), "sponge:hide_unbreakable"));
        this.register(makeValueKey(Hinge.class, of("HingePosition"), "sponge:hinge_position"));
        this.register(makeValueKey(HorseColor.class, of("HorseColor"), "sponge:horse_color"));
        this.register(makeValueKey(HorseStyle.class, of("HorseStyle"), "sponge:horse_style"));
        this.register(makeValueKey(HorseVariant.class, of("HorseVariant"), "sponge:horse_variant"));
        this.register(makeValueKey(Boolean.class, of("InvisibilityIgnoresCollision"), "sponge:invisibility_ignores_collision"));
        this.register(makeValueKey(Boolean.class, of("InvisibilityPreventsTargeting"), "sponge:invisibility_prevents_targeting"));
        this.register(makeValueKey(Boolean.class, of("Invisible"), "sponge:invisible"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("InvulnerabilityTicks"), "sponge:invulnerability_ticks"));
        this.register(makeValueKey(Boolean.class, of("InWall"), "sponge:in_wall"));
        this.register(makeValueKey(Boolean.class, of("IsAflame"), "sponge:is_aflame"));
        this.register(makeValueKey(Boolean.class, of("IsFlying"), "sponge:is_flying"));
        this.register(makeValueKey(Boolean.class, of("IsPlaying"), "sponge:is_playing"));
        this.register(makeValueKey(Boolean.class, of("IsScreaming"), "sponge:is_screaming"));
        this.register(makeValueKey(Boolean.class, of("IsSheared"), "sponge:is_sheared"));
        this.register(makeValueKey(Boolean.class, of("IsSilent"), "sponge:is_silent"));
        this.register(makeValueKey(Boolean.class, of("IsSitting"), "sponge:is_sitting"));
        this.register(makeValueKey(Boolean.class, of("IsSleeping"), "sponge:is_sleeping"));
        this.register(makeValueKey(Boolean.class, of("IsSneaking"), "sponge:is_sneaking"));
        this.register(makeValueKey(Boolean.class, of("IsSprinting"), "sponge:is_sprinting"));
        this.register(makeValueKey(Boolean.class, of("IsWet"), "sponge:is_wet"));
        this.register(makeValueKey(BlockState.class, of("ItemBlockState"), "sponge:item_blockstate"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("ItemDurability"), "sponge:item_durability"));
        this.register(makeListKey(ItemEnchantment.class, of("ItemEnchantments"), "sponge:item_enchantments"));
        this.register(makeListKey(Text.class, of("ItemLore"), "sponge:item_lore"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("KnockbackStrength"), "sponge:knockback_strength"));
        this.register(makeOptionalKey(Living.class, of("LastAttacker"), "sponge:last_attacker"));
        this.register(makeOptionalKey(Text.class, of("LastCommandOutput"), "sponge:last_command_output"));
        this.register(makeOptionalKey(Double.class, of("LastDamage"), "sponge:last_damage"));
        this.register(makeValueKey(Instant.class, of("LastDatePlayed"), "sponge:last_date_played"));
        this.register(makeValueKey(Integer.class, of("Layer"), "sponge:layer"));
        this.register(makeValueKey(EntitySnapshot.class, of("LeashHolder"), "sponge:leash_holder"));
        this.register(makeValueKey(Vector3d.class, of("LeftArmRotation"), "sponge:left_arm_rotation"));
        this.register(makeValueKey(Vector3d.class, of("LeftLegRotation"), "sponge:left_leg_rotation"));
        this.register(makeValueKey(String.class, of("LockToken"), "sponge:lock_token"));
        this.register(makeValueKey(LogAxis.class, of("LogAxis"), "sponge:log_axis"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("MaxAir"), "sponge:max_air"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("MaxBurnTime"), "sponge:max_burn_time"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("MaxCookTime"), "sponge:max_cook_time"));
        this.register(makeMutableBoundedValueKey(Double.class, of("MaxFallDamage"), "sponge:max_fall_damage"));
        this.register(makeMutableBoundedValueKey(Double.class, of("MaxHealth"), "sponge:max_health"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("Moisture"), "sponge:moisture"));
        this.register(makeValueKey(NotePitch.class, of("NotePitch"), "sponge:note_pitch"));
        this.register(makeValueKey(Boolean.class, of("Occupied"), "sponge:occupied"));
        this.register(makeValueKey(OcelotType.class, of("OcelotType"), "sponge:ocelot_type"));
        this.register(makeValueKey(Integer.class, of("Offset"), "sponge:offset"));
        this.register(makeValueKey(Boolean.class, of("Open"), "sponge:open"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("PassedBurnTime"), "sponge:passed_burn_time"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("PassedCookTime"), "sponge:passed_cook_time"));
        this.register(makeListKey(EntitySnapshot.class, of("Passengers"), "sponge:passengers"));
        this.register(makeValueKey(Boolean.class, of("Persists"), "sponge:persists"));
        this.register(makeValueKey(PickupRule.class, of("PickupRule"), "sponge:pickup_rule"));
        this.register(makeValueKey(Boolean.class, of("PigSaddle"), "sponge:pig_saddle"));
        this.register(makeValueKey(PistonType.class, of("PistonType"), "sponge:piston_type"));
        this.register(makeSetKey(BlockType.class, of("PlaceableBlocks"), "sponge:placeable_blocks"));
        this.register(makeValueKey(PlantType.class, of("PlantType"), "sponge:plant_type"));
        this.register(makeValueKey(Boolean.class, of("PlayerCreated"), "sponge:player_created"));
        this.register(makeValueKey(PortionType.class, of("PortionType"), "sponge:portion_type"));
        this.register(makeListKey(PotionEffect.class, of("PotionEffects"), "sponge:potion_effects"));
        this.register(makeValueKey(Integer.class, of("Power"), "sponge:power"));
        this.register(makeValueKey(Boolean.class, of("Powered"), "sponge:powered"));
        this.register(makeValueKey(PrismarineType.class, of("PrismarineType"), "sponge:prismarine_type"));
        this.register(makeValueKey(QuartzType.class, of("QuartzType"), "sponge:quartz_type"));
        this.register(makeValueKey(RabbitType.class, of("RabbitType"), "sponge:rabbit_type"));
        this.register(makeValueKey(RailDirection.class, of("RailDirection"), "sponge:rail_direction"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("RemainingAir"), "sponge:remaining_air"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("RemainingBrewTime"), "sponge:remaining_brew_time"));
        this.register(makeValueKey(BlockState.class, of("RepresentedBlock"), "sponge:represented_block"));
        this.register(makeValueKey(ItemStackSnapshot.class, of("RepresentedItem"), "sponge:represented_item"));
        this.register(makeValueKey(GameProfile.class, of("RepresentedPlayer"), "sponge:represented_player"));
        this.register(makeMapKeyWithKeyAndValue(UUID.class, RespawnLocation.class, of("RespawnLocations"), "sponge:respawn_locations"));
        this.register(makeValueKey(Vector3d.class, of("RightArmRotation"), "sponge:right_arm_rotation"));
        this.register(makeValueKey(Vector3d.class, of("RightLegRotation"), "sponge:right_leg_rotation"));
        this.register(makeValueKey(Rotation.class, of("Rotation"), "sponge:rotation"));
        this.register(makeValueKey(SandstoneType.class, of("SandstoneType"), "sponge:sandstone_type"));
        this.register(makeValueKey(SandType.class, of("SandType"), "sponge:sand_type"));
        this.register(makeMutableBoundedValueKey(Double.class, of("Saturation"), "sponge:saturation"));
        this.register(makeMutableBoundedValueKey(Float.class, of("Scale"), "sponge:scale"));
        this.register(makeValueKey(Boolean.class, of("Seamless"), "sponge:seamless"));
        this.register(makeValueKey(Boolean.class, of("ShouldDrop"), "sponge:should_drop"));
        this.register(makeValueKey(ShrubType.class, of("ShrubType"), "sponge:shrub_type"));
        this.register(makeListKey(Text.class, of("SignLines"), "sponge:sign_lines"));
        this.register(makeValueKey(SkeletonType.class, of("SkeletonType"), "sponge:skeleton_type"));
        this.register(makeValueKey(UUID.class, of("SkinUniqueId"), "sponge:skin_unique_id"));
        this.register(makeValueKey(SkullType.class, of("SkullType"), "sponge:skull_type"));
        this.register(makeValueKey(SlabType.class, of("SlabType"), "sponge:slab_type"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("SlimeSize"), "sponge:slime_size"));
        this.register(makeValueKey(Boolean.class, of("Snowed"), "sponge:snowed"));
        this.register(makeValueKey(EntityType.class, of("SpawnableEntityType"), "sponge:spawnable_entity_type"));
        this.register(makeWeightedCollectionKey(EntitySnapshot.class, of("SpawnerEntities"), "sponge:spawner_entities"));
        this.register(makeMutableBoundedValueKey(Short.class, of("SpawnerMaximumDelay"), "sponge:spawner_maximum_delay"));
        this.register(makeMutableBoundedValueKey(Short.class, of("SpawnerMaximumNearbyEntities"), "sponge:spawner_maximum_nearby_entities"));
        this.register(makeMutableBoundedValueKey(Short.class, of("SpawnerMinimumDelay"), "sponge:spawner_minimum_delay"));
        this.register(makeNextEntityToSpawnKey(of("SpawnerNextEntityToSpawn"), "sponge:spawner_next_entity_to_spawn"));
        this.register(makeMutableBoundedValueKey(Short.class, of("SpawnerRemainingDelay"), "sponge:spawner_remaining_delay"));
        this.register(makeMutableBoundedValueKey(Short.class, of("SpawnerRequiredPlayerRange"), "sponge:spawner_required_player_range"));
        this.register(makeMutableBoundedValueKey(Short.class, of("SpawnerSpawnCount"), "sponge:spawner_spawn_count"));
        this.register(makeMutableBoundedValueKey(Short.class, of("SpawnerSpawnRange"), "sponge:spawner_spawn_range"));
        this.register(makeValueKey(StairShape.class, of("StairShape"), "sponge:stair_shape"));
        this.register(makeMapKeyWithKeyAndValue(Statistic.class, Long.class, of("Statistics"), "sponge:statistics"));
        this.register(makeValueKey(StoneType.class, of("StoneType"), "sponge:stone_type"));
        this.register(makeListKey(ItemEnchantment.class, of("StoredEnchantments"), "sponge:stored_enchantments"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("StuckArrows"), "sponge:stuck_arrows"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("SuccessCount"), "sponge:success_count"));
        this.register(makeValueKey(Boolean.class, of("Suspended"), "sponge:suspended"));
        this.register(makeOptionalKey(UUID.class, of("TamedOwner"), "sponge:tamed_owner"));
        this.register(makeValueKey(Vector3d.class, of("TargetedLocation"), "sponge:targeted_location"));
        this.register(makeValueKey(Integer.class, of("TicksRemaining"), "sponge:ticks_remaining"));
        this.register(makeMutableBoundedValueKey(Integer.class, of("TotalExperience"), "sponge:total_experience"));
        this.register(makeValueKey(Boolean.class, of("TracksOutput"), "sponge:tracks_output"));
        this.register(makeListKey(TradeOffer.class, of("TradeOffers"), "sponge:trade_offers"));
        this.register(makeValueKey(TreeType.class, of("TreeType"), "sponge:tree_type"));
        this.register(makeValueKey(Boolean.class, of("Unbreakable"), "sponge:unbreakable"));
        this.register(makeValueKey(Boolean.class, of("Vanish"), "sponge:vanish"));
        this.register(makeValueKey(Boolean.class, of("VanishIgnoresCollision"), "sponge:vanish_ignores_collision"));
        this.register(makeValueKey(Boolean.class, of("VanishPreventsTargeting"), "sponge:vanish_prevents_targeting"));
        this.register(makeValueKey(EntitySnapshot.class, of("Vehicle"), "sponge:vehicle"));
        this.register(makeValueKey(Vector3d.class, of("Velocity"), "sponge:velocity"));
        this.register(makeValueKey(Profession.class, of("VillagerZombieProfession"), "sponge:villager_zombie_profession"));
        this.register(makeValueKey(Double.class, of("WalkingSpeed"), "sponge:walking_speed"));
        this.register(makeValueKey(WallType.class, of("WallType"), "sponge:wall_type"));
        this.register(makeValueKey(Boolean.class, of("WillShatter"), "sponge:will_shatter"));
        this.register(makeMapKeyWithKeyAndValue(Direction.class, WireAttachmentType.class, of("WireAttachments"), "sponge:wire_attachments"));
        this.register(makeValueKey(WireAttachmentType.class, of("WireAttachmentEast"), "sponge:wire_attachment_east"));
        this.register(makeValueKey(WireAttachmentType.class, of("WireAttachmentNorth"), "sponge:wire_attachment_north"));
        this.register(makeValueKey(WireAttachmentType.class, of("WireAttachmentSouth"), "sponge:wire_attachment_south"));
        this.register(makeValueKey(WireAttachmentType.class, of("WireAttachmentWest"), "sponge:wire_attachment_west"));

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
                    this.register((Key) object);
                }
            }
        }
    }

    private final static class Holder {
        private static final KeyRegistryModule INSTANCE = new KeyRegistryModule();
    }
}
