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
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeMapKeyWithKeyAndValue;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeNextEntityToSpawnKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeOptionalKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makePatternListKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeSetKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeSingleKey;
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
    public void registerDefaults() {
        this.register(makeSetKey(Achievement.class, of("Achievements"), "sponge:achievements"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("AffectsSpawning"), "sponge:affects_spawning"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("Age"), "sponge:age"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("AIEnabled"), "sponge:ai_enabled"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("Anger"), "sponge:anger"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ArmorStandHasArms"), "sponge:armor_stand_has_arms"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ArmorStandHasBasePlate"), "sponge:armor_stand_has_base_plate"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ArmorStandHasGravity"), "sponge:armor_stand_has_gravity"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ArmorStandIsSmall"), "sponge:armor_stand_is_small"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ArmorStandMarker"), "sponge:armor_stand_marker"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Angry"), "sponge:angry"));
        this.register(makeSingleKey(Art.class, Value.class, of("Art"), "sponge:art"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Attached"), "sponge:attached"));
        this.register(makeSingleKey(Double.class, MutableBoundedValue.class, of("AttackDamage"), "sponge:attack_damage"));
        this.register(makeSingleKey(Axis.class, Value.class, of("Axis"), "sponge:axis"));
        this.register(makeSingleKey(DyeColor.class, Value.class, of("BannerBaseColor"), "sponge:banner_base_color"));
        this.register(makePatternListKey(of("BannerPatterns"), "sponge:banner_patterns"));
        this.register(makeSingleKey(Float.class, MutableBoundedValue.class, of("BaseSize"), "sponge:base_size"));
        this.register(makeSingleKey(EntitySnapshot.class, Value.class, of("BaseVehicle"), "sponge:base_vehicle"));
        this.register(makeOptionalKey(PotionEffectType.class, of("BeaconPrimaryEffect"), "sponge:beacon_primary_effect"));
        this.register(makeOptionalKey(PotionEffectType.class, of("BeaconSecondaryEffect"), "sponge:beacon_secondary_effect"));
        this.register(makeSingleKey(BigMushroomType.class, Value.class, of("BigMushroomType"), "sponge:big_mushroom_type"));
        this.register(makeMapKeyWithKeyAndValue(BodyPart.class, Vector3d.class, of("BodyRotations"), "sponge:body_rotations"));
        this.register(makeSingleKey(Text.class, Value.class, of("BookAuthor"), "sponge:book_author"));
        this.register(makeListKey(Text.class, of("BookPages"), "sponge:book_pages"));
        this.register(makeSetKey(BlockType.class, of("BreakableBlockTypes"), "sponge:breakable_block_types"));
        this.register(makeSingleKey(BrickType.class, Value.class, of("BrickType"), "sponge:brick_type"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("CanBreed"), "sponge:can_breed"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("CanDropAsItem"), "sponge:can_drop_as_item"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("CanFly"), "sponge:can_fly"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("CanGrief"), "sponge:can_grief"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("CanPlaceAsBlock"), "sponge:can_place_as_block"));
        this.register(makeSingleKey(Career.class, Value.class, of("Career"), "sponge:career"));
        this.register(makeSingleKey(Vector3d.class, Value.class, of("ChestRotation"), "sponge:chest_rotation"));
        this.register(makeSingleKey(CoalType.class, Value.class, of("CoalType"), "sponge:coal_type"));
        this.register(makeSingleKey(Color.class, Value.class, of("Color"), "sponge:color"));
        this.register(makeSingleKey(String.class, Value.class, of("Command"), "sponge:command"));
        this.register(makeSingleKey(ComparatorType.class, Value.class, of("ComparatorType"), "sponge:comparator_type"));
        this.register(makeSetKey(Direction.class, of("ConnectedDirections"), "sponge:connected_directions"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ConnectedEast"), "sponge:connected_east"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ConnectedNorth"), "sponge:connected_north"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ConnectedSouth"), "sponge:connected_south"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ConnectedWest"), "sponge:connected_west"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("ContainedExperience"), "sponge:contained_experience"));
        this.register(makeSingleKey(CookedFish.class, Value.class, of("CookedFish"), "sponge:cooked_fish"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("Cooldown"), "sponge:cooldown"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("CreeperCharged"), "sponge:creeper_charged"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("CriticalHit"), "sponge:critical_hit"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("CustomNameVisible"), "sponge:custom_name_visible"));
        this.register(makeMapKeyWithKeyAndValue(EntityType.class, Double.class, of("EntityDamageMap"), "sponge:damage_entity_map"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Decayable"), "sponge:decayable"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("Delay"), "sponge:delay"));
        this.register(makeSingleKey(Direction.class, Value.class, of("Direction"), "sponge:direction"));
        this.register(makeSingleKey(DirtType.class, Value.class, of("DirtType"), "sponge:dirt_type"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Disarmed"), "sponge:disarmed"));
        this.register(makeSingleKey(DisguisedBlockType.class, Value.class, of("DisguisedBlockType"), "sponge:disguised_block_type"));
        this.register(makeSingleKey(Text.class, Value.class, of("DisplayName"), "sponge:display_name"));
        this.register(makeSingleKey(HandType.class, Value.class, of("DominantHand"), "sponge:dominant_hand"));
        this.register(makeSingleKey(DoublePlantType.class, Value.class, of("DoublePlantType"), "sponge:double_plant_type"));
        this.register(makeSingleKey(DyeColor.class, Value.class, of("DyeColor"), "sponge:dye_color"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ElderGuardian"), "sponge:elder_guardian"));
        this.register(makeSingleKey(Double.class, MutableBoundedValue.class, of("Exhaustion"), "sponge:exhaustion"));
        this.register(makeSingleKey(Integer.class, ImmutableBoundedValue.class, of("ExperienceFromStartOfLevel"), "sponge:experience_from_start_of_level"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("ExperienceLevel"), "sponge:experience_level"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("ExperienceSinceLevel"), "sponge:experience_since_level"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("ExpirationTicks"), "sponge:expiration_ticks"));
        this.register(makeSingleKey(Integer.class, OptionalValue.class, of("ExplosionRadius"), "sponge:explosion_radius"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Extended"), "sponge:extended"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("FallingBlockCanHurtEntities"), "sponge:falling_block_can_hurt_entities"));
        this.register(makeSingleKey(BlockState.class, Value.class, of("FallingBlockState"), "sponge:falling_block_state"));
        this.register(makeSingleKey(Double.class, MutableBoundedValue.class, of("FallDamagePerBlock"), "sponge:fall_damage_per_block"));
        this.register(makeSingleKey(Float.class, MutableBoundedValue.class, of("FallDistance"), "sponge:fall_distance"));
        this.register(makeSingleKey(Integer.class, Value.class, of("FallTime"), "sponge:fall_time"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Filled"), "sponge:filled"));
        this.register(makeListKey(FireworkEffect.class, of("FireworkEffects"), "sponge:firework_effects"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("FireworkFlightModifier"), "sponge:firework_flight_modifier"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("FireDamageDelay"), "sponge:fire_damage_delay"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("FireTicks"), "sponge:fire_ticks"));
        this.register(makeSingleKey(Instant.class, Value.class, of("FirstDatePlayed"), "sponge:first_date_played"));
        this.register(makeSingleKey(Fish.class, Value.class, of("FishType"), "sponge:fish_type"));
        this.register(makeSingleKey(FluidStackSnapshot.class, Value.class, of("FluidItemStack"), "sponge:fluid_item_stack"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("FluidLevel"), "sponge:fluid_level"));
        this.register(makeMapKeyWithKeyAndValue(Direction.class, List.class, of("FluidTankContents"), "sponge:fluid_tank_contents"));
        this.register(makeSingleKey(Double.class, Value.class, of("FlyingSpeed"), "sponge:flying_speed"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("FoodLevel"), "sponge:food_level"));
        this.register(makeSingleKey(Integer.class, Value.class, of("FuseDuration"), "sponge:fuse_duration"));
        this.register(makeSingleKey(GameMode.class, Value.class, of("GameMode"), "sponge:game_mode"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("Generation"), "sponge:generation"));
        this.register(makeSingleKey(GoldenApple.class, Value.class, of("GoldenAppleType"), "sponge:golden_apple_type"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("GrowthStage"), "sponge:growth_stage"));
        this.register(makeSingleKey(Vector3d.class, Value.class, of("HeadRotation"), "sponge:head_rotation"));
        this.register(makeSingleKey(Double.class, MutableBoundedValue.class, of("Health"), "sponge:health"));
        this.register(makeSingleKey(Double.class, MutableBoundedValue.class, of("HealthScale"), "sponge:health_scale"));
        this.register(makeSingleKey(Float.class, MutableBoundedValue.class, of("Height"), "sponge:height"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("HeldExperience"), "sponge:held_experience"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("HideAttributes"), "sponge:hide_attributes"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("HideCanDestroy"), "sponge:hide_can_destroy"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("HideCanPlace"), "sponge:hide_can_place"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("HideEnchantments"), "sponge:hide_enchantments"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("HideMiscellaneous"), "sponge:hide_miscellaneous"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("HideUnbreakable"), "sponge:hide_unbreakable"));
        this.register(makeSingleKey(Hinge.class, Value.class, of("HingePosition"), "sponge:hinge_position"));
        this.register(makeSingleKey(HorseColor.class, Value.class, of("HorseColor"), "sponge:horse_color"));
        this.register(makeSingleKey(HorseStyle.class, Value.class, of("HorseStyle"), "sponge:horse_style"));
        this.register(makeSingleKey(HorseVariant.class, Value.class, of("HorseVariant"), "sponge:horse_variant"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("InvisibilityIgnoresCollision"), "sponge:invisibility_ignores_collision"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("InvisibilityPreventsTargeting"), "sponge:invisibility_prevents_targeting"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Invisible"), "sponge:invisible"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("InvulnerabilityTicks"), "sponge:invulnerability_ticks"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("InWall"), "sponge:in_wall"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsAflame"), "sponge:is_aflame"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsFlying"), "sponge:is_flying"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsPlaying"), "sponge:is_playing"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsScreaming"), "sponge:is_screaming"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsSheared"), "sponge:is_sheared"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsSilent"), "sponge:is_silent"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsSitting"), "sponge:is_sitting"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsSleeping"), "sponge:is_sleeping"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsSneaking"), "sponge:is_sneaking"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsSprinting"), "sponge:is_sprinting"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsWet"), "sponge:is_wet"));
        this.register(makeSingleKey(BlockState.class, Value.class, of("ItemBlockState"), "sponge:item_blockstate"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("ItemDurability"), "sponge:item_durability"));
        this.register(makeListKey(ItemEnchantment.class, of("ItemEnchantments"), "sponge:item_enchantments"));
        this.register(makeListKey(Text.class, of("ItemLore"), "sponge:item_lore"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("KnockbackStrength"), "sponge:knockback_strength"));
        this.register(makeOptionalKey(Living.class, of("LastAttacker"), "sponge:last_attacker"));
        this.register(makeOptionalKey(Text.class, of("LastCommandOutput"), "sponge:last_command_output"));
        this.register(makeOptionalKey(Double.class, of("LastDamage"), "sponge:last_damage"));
        this.register(makeSingleKey(Instant.class, Value.class, of("LastDatePlayed"), "sponge:last_date_played"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("Layer"), "sponge:layer"));
        this.register(makeSingleKey(EntitySnapshot.class, Value.class, of("LeashHolder"), "sponge:leash_holder"));
        this.register(makeSingleKey(Vector3d.class, Value.class, of("LeftArmRotation"), "sponge:left_arm_rotation"));
        this.register(makeSingleKey(Vector3d.class, Value.class, of("LeftLegRotation"), "sponge:left_leg_rotation"));
        this.register(makeSingleKey(String.class, Value.class, of("LockToken"), "sponge:lock_token"));
        this.register(makeSingleKey(LogAxis.class, Value.class, of("LogAxis"), "sponge:log_axis"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("MaxAir"), "sponge:max_air"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("MaxBurnTime"), "sponge:max_burn_time"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("MaxCookTime"), "sponge:max_cook_time"));
        this.register(makeSingleKey(Double.class, MutableBoundedValue.class, of("MaxFallDamage"), "sponge:max_fall_damage"));
        this.register(makeSingleKey(Double.class, MutableBoundedValue.class, of("MaxHealth"), "sponge:max_health"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("Moisture"), "sponge:moisture"));
        this.register(makeSingleKey(NotePitch.class, Value.class, of("NotePitch"), "sponge:note_pitch"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Occupied"), "sponge:occupied"));
        this.register(makeSingleKey(OcelotType.class, Value.class, of("OcelotType"), "sponge:ocelot_type"));
        this.register(makeSingleKey(Integer.class, Value.class, of("Offset"), "sponge:offset"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Open"), "sponge:open"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("PassedBurnTime"), "sponge:passed_burn_time"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("PassedCookTime"), "sponge:passed_cook_time"));
        this.register(makeListKey(EntitySnapshot.class, of("Passengers"), "sponge:passengers"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Persists"), "sponge:persists"));
        this.register(makeSingleKey(PickupRule.class, Value.class, of("PickupRule"), "sponge:pickup_rule"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("PigSaddle"), "sponge:pig_saddle"));
        this.register(makeSingleKey(PistonType.class, Value.class, of("PistonType"), "sponge:piston_type"));
        this.register(makeSetKey(BlockType.class, of("PlaceableBlocks"), "sponge:placeable_blocks"));
        this.register(makeSingleKey(PlantType.class, Value.class, of("PlantType"), "sponge:plant_type"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("PlayerCreated"), "sponge:player_created"));
        this.register(makeSingleKey(PortionType.class, Value.class, of("PortionType"), "sponge:portion_type"));
        this.register(makeListKey(PotionEffect.class, of("PotionEffects"), "sponge:potion_effects"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("Power"), "sponge:power"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Powered"), "sponge:powered"));
        this.register(makeSingleKey(PrismarineType.class, Value.class, of("PrismarineType"), "sponge:prismarine_type"));
        this.register(makeSingleKey(QuartzType.class, Value.class, of("QuartzType"), "sponge:quartz_type"));
        this.register(makeSingleKey(RabbitType.class, Value.class, of("RabbitType"), "sponge:rabbit_type"));
        this.register(makeSingleKey(RailDirection.class, Value.class, of("RailDirection"), "sponge:rail_direction"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("RemainingAir"), "sponge:remaining_air"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("RemainingBrewTime"), "sponge:remaining_brew_time"));
        this.register(makeSingleKey(BlockState.class, Value.class, of("RepresentedBlock"), "sponge:represented_block"));
        this.register(makeSingleKey(ItemStackSnapshot.class, Value.class, of("RepresentedItem"), "sponge:represented_item"));
        this.register(makeSingleKey(GameProfile.class, Value.class, of("RepresentedPlayer"), "sponge:represented_player"));
        this.register(makeMapKeyWithKeyAndValue(UUID.class, RespawnLocation.class, of("RespawnLocations"), "sponge:respawn_locations"));
        this.register(makeSingleKey(Vector3d.class, Value.class, of("RightArmRotation"), "sponge:right_arm_rotation"));
        this.register(makeSingleKey(Vector3d.class, Value.class, of("RightLegRotation"), "sponge:right_leg_rotation"));
        this.register(makeSingleKey(Rotation.class, Value.class, of("Rotation"), "sponge:rotation"));
        this.register(makeSingleKey(SandstoneType.class, Value.class, of("SandstoneType"), "sponge:sandstone_type"));
        this.register(makeSingleKey(SandType.class, Value.class, of("SandType"), "sponge:sand_type"));
        this.register(makeSingleKey(Double.class, MutableBoundedValue.class, of("Saturation"), "sponge:saturation"));
        this.register(makeSingleKey(Float.class, MutableBoundedValue.class, of("Scale"), "sponge:scale"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Seamless"), "sponge:seamless"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ShouldDrop"), "sponge:should_drop"));
        this.register(makeSingleKey(ShrubType.class, Value.class, of("ShrubType"), "sponge:shrub_type"));
        this.register(makeListKey(Text.class, of("SignLines"), "sponge:sign_lines"));
        this.register(makeSingleKey(SkeletonType.class, Value.class, of("SkeletonType"), "sponge:skeleton_type"));
        this.register(makeSingleKey(UUID.class, Value.class, of("SkinUniqueId"), "sponge:skin_unique_id"));
        this.register(makeSingleKey(SkullType.class, Value.class, of("SkullType"), "sponge:skull_type"));
        this.register(makeSingleKey(SlabType.class, Value.class, of("SlabType"), "sponge:slab_type"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("SlimeSize"), "sponge:slime_size"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Snowed"), "sponge:snowed"));
        this.register(makeSingleKey(EntityType.class, Value.class, of("SpawnableEntityType"), "sponge:spawnable_entity_type"));
        this.register(makeWeightedCollectionKey(EntitySnapshot.class, of("SpawnerEntities"), "sponge:spawner_entities"));
        this.register(makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerMaximumDelay"), "sponge:spawner_maximum_delay"));
        this.register(makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerMaximumNearbyEntities"), "sponge:spawner_maximum_nearby_entities"));
        this.register(makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerMinimumDelay"), "sponge:spawner_minimum_delay"));
        this.register(makeNextEntityToSpawnKey(of("SpawnerNextEntityToSpawn"), "sponge:spawner_next_entity_to_spawn"));
        this.register(makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerRemainingDelay"), "sponge:spawner_remaining_delay"));
        this.register(makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerRequiredPlayerRange"), "sponge:spawner_required_player_range"));
        this.register(makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerSpawnCount"), "sponge:spawner_spawn_count"));
        this.register(makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerSpawnRange"), "sponge:spawner_spawn_range"));
        this.register(makeSingleKey(StairShape.class, Value.class, of("StairShape"), "sponge:stair_shape"));
        this.register(makeMapKeyWithKeyAndValue(Statistic.class, Long.class, of("Statistics"), "sponge:statistics"));
        this.register(makeSingleKey(StoneType.class, Value.class, of("StoneType"), "sponge:stone_type"));
        this.register(makeListKey(ItemEnchantment.class, of("StoredEnchantments"), "sponge:stored_enchantments"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("StuckArrows"), "sponge:stuck_arrows"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("SuccessCount"), "sponge:success_count"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Suspended"), "sponge:suspended"));
        this.register(makeOptionalKey(UUID.class, of("TamedOwner"), "sponge:tamed_owner"));
        this.register(makeSingleKey(Vector3d.class, Value.class, of("TargetedLocation"), "sponge:targeted_location"));
        this.register(makeSingleKey(Integer.class, Value.class, of("TicksRemaining"), "sponge:ticks_remaining"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("TotalExperience"), "sponge:total_experience"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("TracksOutput"), "sponge:tracks_output"));
        this.register(makeListKey(TradeOffer.class, of("TradeOffers"), "sponge:trade_offers"));
        this.register(makeSingleKey(TreeType.class, Value.class, of("TreeType"), "sponge:tree_type"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Unbreakable"), "sponge:unbreakable"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Vanish"), "sponge:vanish"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("VanishIgnoresCollision"), "sponge:vanish_ignores_collision"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("VanishPreventsTargeting"), "sponge:vanish_prevents_targeting"));
        this.register(makeSingleKey(EntitySnapshot.class, Value.class, of("Vehicle"), "sponge:vehicle"));
        this.register(makeSingleKey(Vector3d.class, Value.class, of("Velocity"), "sponge:velocity"));
        this.register(makeSingleKey(Profession.class, Value.class, of("VillagerZombieProfession"), "sponge:villager_zombie_profession"));
        this.register(makeSingleKey(Double.class, Value.class, of("WalkingSpeed"), "sponge:walking_speed"));
        this.register(makeSingleKey(WallType.class, Value.class, of("WallType"), "sponge:wall_type"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("WillShatter"), "sponge:will_shatter"));
        this.register(makeMapKeyWithKeyAndValue(Direction.class, WireAttachmentType.class, of("WireAttachments"), "sponge:wire_attachments"));
        this.register(makeSingleKey(WireAttachmentType.class, Value.class, of("WireAttachmentEast"), "sponge:wire_attachment_east"));
        this.register(makeSingleKey(WireAttachmentType.class, Value.class, of("WireAttachmentNorth"), "sponge:wire_attachment_north"));
        this.register(makeSingleKey(WireAttachmentType.class, Value.class, of("WireAttachmentSouth"), "sponge:wire_attachment_south"));
        this.register(makeSingleKey(WireAttachmentType.class, Value.class, of("WireAttachmentWest"), "sponge:wire_attachment_west"));

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
