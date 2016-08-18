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
import org.lanternpowered.server.data.key.LanternKey;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
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
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Axis;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.RespawnLocation;
import org.spongepowered.api.util.rotation.Rotation;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class KeyRegistryModule extends PluginCatalogRegistryModule<LanternKey> {

    public static KeyRegistryModule get() {
        return Holder.INSTANCE;
    }

    private KeyRegistryModule() {
        super(Keys.class);
    }

    @Override
    public void registerDefaults() {
        this.register(makeSetKey(Achievement.class, of("Achievements"), "achievements"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("AffectsSpawning"), "affects_spawning"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("Age"), "age"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("AIEnabled"), "ai_enabled"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("Anger"), "anger"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ArmorStandHasArms"), "armor_stand_has_arms"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ArmorStandHasBasePlate"), "armor_stand_has_base_plate"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ArmorStandHasGravity"), "armor_stand_has_gravity"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ArmorStandIsSmall"), "armor_stand_is_small"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ArmorStandMarker"), "armor_stand_marker"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Angry"), "angry"));
        this.register(makeSingleKey(Art.class, Value.class, of("Art"), "art"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Attached"), "attached"));
        this.register(makeSingleKey(Double.class, MutableBoundedValue.class, of("AttackDamage"), "attack_damage"));
        this.register(makeSingleKey(Axis.class, Value.class, of("Axis"), "axis"));
        this.register(makeSingleKey(DyeColor.class, Value.class, of("BannerBaseColor"), "banner_base_color"));
        this.register(makePatternListKey(of("BannerPatterns"), "banner_patterns"));
        this.register(makeSingleKey(Float.class, MutableBoundedValue.class, of("BaseSize"), "base_size"));
        this.register(makeSingleKey(EntitySnapshot.class, Value.class, of("BaseVehicle"), "base_vehicle"));
        this.register(makeOptionalKey(PotionEffectType.class, of("BeaconPrimaryEffect"), "beacon_primary_effect"));
        this.register(makeOptionalKey(PotionEffectType.class, of("BeaconSecondaryEffect"), "beacon_secondary_effect"));
        this.register(makeSingleKey(BigMushroomType.class, Value.class, of("BigMushroomType"), "big_mushroom_type"));
        this.register(makeMapKey(BodyPart.class, Vector3d.class, of("BodyRotations"), "body_rotations"));
        this.register(makeSingleKey(Text.class, Value.class, of("BookAuthor"), "book_author"));
        this.register(makeListKey(Text.class, of("BookPages"), "book_pages"));
        this.register(makeSetKey(BlockType.class, of("BreakableBlockTypes"), "breakable_block_types"));
        this.register(makeSingleKey(BrickType.class, Value.class, of("BrickType"), "brick_type"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("CanBreed"), "can_breed"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("CanDropAsItem"), "can_drop_as_item"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("CanFly"), "can_fly"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("CanGrief"), "can_grief"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("CanPlaceAsBlock"), "can_place_as_block"));
        this.register(makeSingleKey(Career.class, Value.class, of("Career"), "career"));
        this.register(makeSingleKey(Vector3d.class, Value.class, of("ChestRotation"), "chest_rotation"));
        this.register(makeSingleKey(CoalType.class, Value.class, of("CoalType"), "coal_type"));
        this.register(makeSingleKey(Color.class, Value.class, of("Color"), "color"));
        this.register(makeSingleKey(String.class, Value.class, of("Command"), "command"));
        this.register(makeSingleKey(ComparatorType.class, Value.class, of("ComparatorType"), "comparator_type"));
        this.register(makeSetKey(Direction.class, of("ConnectedDirections"), "connected_directions"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ConnectedEast"), "connected_east"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ConnectedNorth"), "connected_north"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ConnectedSouth"), "connected_south"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ConnectedWest"), "connected_west"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("ContainedExperience"), "contained_experience"));
        this.register(makeSingleKey(CookedFish.class, Value.class, of("CookedFish"), "cooked_fish"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("Cooldown"), "cooldown"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("CreeperCharged"), "creeper_charged"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("CriticalHit"), "critical_hit"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("CustomNameVisible"), "custom_name_visible"));
        this.register(makeMapKey(EntityType.class, Double.class, of("EntityDamageMap"), "damage_entity_map"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Decayable"), "decayable"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("Delay"), "delay"));
        this.register(makeSingleKey(Direction.class, Value.class, of("Direction"), "direction"));
        this.register(makeSingleKey(DirtType.class, Value.class, of("DirtType"), "dirt_type"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Disarmed"), "disarmed"));
        this.register(makeSingleKey(DisguisedBlockType.class, Value.class, of("DisguisedBlockType"), "disguised_block_type"));
        this.register(makeSingleKey(Text.class, Value.class, of("DisplayName"), "display_name"));
        this.register(makeSingleKey(HandType.class, Value.class, of("DominantHand"), "dominant_hand"));
        this.register(makeSingleKey(DoublePlantType.class, Value.class, of("DoublePlantType"), "double_plant_type"));
        this.register(makeSingleKey(DyeColor.class, Value.class, of("DyeColor"), "dye_color"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ElderGuardian"), "elder_guardian"));
        this.register(makeSingleKey(Double.class, MutableBoundedValue.class, of("Exhaustion"), "exhaustion"));
        this.register(makeSingleKey(Integer.class, ImmutableBoundedValue.class, of("ExperienceFromStartOfLevel"), "experience_from_start_of_level"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("ExperienceLevel"), "experience_level"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("ExperienceSinceLevel"), "experience_since_level"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("ExpirationTicks"), "expiration_ticks"));
        this.register(makeSingleKey(Integer.class, OptionalValue.class, of("ExplosionRadius"), "explosion_radius"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Extended"), "extended"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("FallingBlockCanHurtEntities"), "falling_block_can_hurt_entities"));
        this.register(makeSingleKey(BlockState.class, Value.class, of("FallingBlockState"), "falling_block_state"));
        this.register(makeSingleKey(Double.class, MutableBoundedValue.class, of("FallDamagePerBlock"), "fall_damage_per_block"));
        this.register(makeSingleKey(Float.class, MutableBoundedValue.class, of("FallDistance"), "fall_distance"));
        this.register(makeSingleKey(Integer.class, Value.class, of("FallTime"), "fall_time"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Filled"), "filled"));
        this.register(makeListKey(FireworkEffect.class, of("FireworkEffects"), "firework_effects"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("FireworkFlightModifier"), "firework_flight_modifier"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("FireDamageDelay"), "fire_damage_delay"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("FireTicks"), "fire_ticks"));
        this.register(makeSingleKey(Instant.class, Value.class, of("FirstDatePlayed"), "first_date_played"));
        this.register(makeSingleKey(Fish.class, Value.class, of("FishType"), "fish_type"));
        this.register(makeSingleKey(FluidStackSnapshot.class, Value.class, of("FluidItemStack"), "fluid_item_stack"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("FluidLevel"), "fluid_level"));
        this.register(makeMapKey(Direction.class, List.class, of("FluidTankContents"), "fluid_tank_contents"));
        this.register(makeSingleKey(Double.class, Value.class, of("FlyingSpeed"), "flying_speed"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("FoodLevel"), "food_level"));
        this.register(makeSingleKey(Integer.class, Value.class, of("FuseDuration"), "fuse_duration"));
        this.register(makeSingleKey(GameMode.class, Value.class, of("GameMode"), "game_mode"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("Generation"), "generation"));
        this.register(makeSingleKey(GoldenApple.class, Value.class, of("GoldenAppleType"), "golden_apple_type"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("GrowthStage"), "growth_stage"));
        this.register(makeSingleKey(Vector3d.class, Value.class, of("HeadRotation"), "head_rotation"));
        this.register(makeSingleKey(Double.class, MutableBoundedValue.class, of("Health"), "health"));
        this.register(makeSingleKey(Double.class, MutableBoundedValue.class, of("HealthScale"), "health_scale"));
        this.register(makeSingleKey(Float.class, MutableBoundedValue.class, of("Height"), "height"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("HeldExperience"), "held_experience"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("HideAttributes"), "hide_attributes"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("HideCanDestroy"), "hide_can_destroy"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("HideCanPlace"), "hide_can_place"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("HideEnchantments"), "hide_enchantments"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("HideMiscellaneous"), "hide_miscellaneous"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("HideUnbreakable"), "hide_unbreakable"));
        this.register(makeSingleKey(Hinge.class, Value.class, of("HingePosition"), "hinge_position"));
        this.register(makeSingleKey(HorseColor.class, Value.class, of("HorseColor"), "horse_color"));
        this.register(makeSingleKey(HorseStyle.class, Value.class, of("HorseStyle"), "horse_style"));
        this.register(makeSingleKey(HorseVariant.class, Value.class, of("HorseVariant"), "horse_variant"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("InvisibilityIgnoresCollision"), "invisibility_ignores_collision"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("InvisibilityPreventsTargeting"), "invisibility_prevents_targeting"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Invisible"), "invisible"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("InvulnerabilityTicks"), "invulnerability_ticks"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("InWall"), "in_wall"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsAflame"), "is_aflame"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsFlying"), "is_flying"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsPlaying"), "is_playing"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsScreaming"), "is_screaming"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsSheared"), "is_sheared"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsSilent"), "is_silent"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsSitting"), "is_sitting"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsSleeping"), "is_sleeping"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsSneaking"), "is_sneaking"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsSprinting"), "is_sprinting"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("IsWet"), "is_wet"));
        this.register(makeSingleKey(BlockState.class, Value.class, of("ItemBlockState"), "item_blockstate"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("ItemDurability"), "item_durability"));
        this.register(makeListKey(ItemEnchantment.class, of("ItemEnchantments"), "item_enchantments"));
        this.register(makeListKey(Text.class, of("ItemLore"), "item_lore"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("KnockbackStrength"), "knockback_strength"));
        this.register(makeOptionalKey(Living.class, of("LastAttacker"), "last_attacker"));
        this.register(makeOptionalKey(Text.class, of("LastCommandOutput"), "last_command_output"));
        this.register(makeOptionalKey(Double.class, of("LastDamage"), "last_damage"));
        this.register(makeSingleKey(Instant.class, Value.class, of("LastDatePlayed"), "last_date_played"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("Layer"), "layer"));
        this.register(makeSingleKey(EntitySnapshot.class, Value.class, of("LeashHolder"), "leash_holder"));
        this.register(makeSingleKey(Vector3d.class, Value.class, of("LeftArmRotation"), "left_arm_rotation"));
        this.register(makeSingleKey(Vector3d.class, Value.class, of("LeftLegRotation"), "left_leg_rotation"));
        this.register(makeSingleKey(String.class, Value.class, of("LockToken"), "lock_token"));
        this.register(makeSingleKey(LogAxis.class, Value.class, of("LogAxis"), "log_axis"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("MaxAir"), "max_air"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("MaxBurnTime"), "max_burn_time"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("MaxCookTime"), "max_cook_time"));
        this.register(makeSingleKey(Double.class, MutableBoundedValue.class, of("MaxFallDamage"), "max_fall_damage"));
        this.register(makeSingleKey(Double.class, MutableBoundedValue.class, of("MaxHealth"), "max_health"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("Moisture"), "moisture"));
        this.register(makeSingleKey(NotePitch.class, Value.class, of("NotePitch"), "note_pitch"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Occupied"), "occupied"));
        this.register(makeSingleKey(OcelotType.class, Value.class, of("OcelotType"), "ocelot_type"));
        this.register(makeSingleKey(Integer.class, Value.class, of("Offset"), "offset"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Open"), "open"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("PassedBurnTime"), "passed_burn_time"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("PassedCookTime"), "passed_cook_time"));
        this.register(makeListKey(EntitySnapshot.class, of("Passengers"), "passengers"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Persists"), "persists"));
        this.register(makeSingleKey(PickupRule.class, Value.class, of("PickupRule"), "pickup_rule"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("PigSaddle"), "pig_saddle"));
        this.register(makeSingleKey(PistonType.class, Value.class, of("PistonType"), "piston_type"));
        this.register(makeSetKey(BlockType.class, of("PlaceableBlocks"), "placeable_blocks"));
        this.register(makeSingleKey(PlantType.class, Value.class, of("PlantType"), "plant_type"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("PlayerCreated"), "player_created"));
        this.register(makeSingleKey(PortionType.class, Value.class, of("PortionType"), "portion_type"));
        this.register(makeListKey(PotionEffect.class, of("PotionEffects"), "potion_effects"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("Power"), "power"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Powered"), "powered"));
        this.register(makeSingleKey(PrismarineType.class, Value.class, of("PrismarineType"), "prismarine_type"));
        this.register(makeSingleKey(QuartzType.class, Value.class, of("QuartzType"), "quartz_type"));
        this.register(makeSingleKey(RabbitType.class, Value.class, of("RabbitType"), "rabbit_type"));
        this.register(makeSingleKey(RailDirection.class, Value.class, of("RailDirection"), "rail_direction"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("RemainingAir"), "remaining_air"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("RemainingBrewTime"), "remaining_brew_time"));
        this.register(makeSingleKey(BlockState.class, Value.class, of("RepresentedBlock"), "represented_block"));
        this.register(makeSingleKey(ItemStackSnapshot.class, Value.class, of("RepresentedItem"), "represented_item"));
        this.register(makeSingleKey(GameProfile.class, Value.class, of("RepresentedPlayer"), "represented_player"));
        this.register(makeMapKey(UUID.class, RespawnLocation.class, of("RespawnLocations"), "respawn_locations"));
        this.register(makeSingleKey(Vector3d.class, Value.class, of("RightArmRotation"), "right_arm_rotation"));
        this.register(makeSingleKey(Vector3d.class, Value.class, of("RightLegRotation"), "right_leg_rotation"));
        this.register(makeSingleKey(Rotation.class, Value.class, of("Rotation"), "rotation"));
        this.register(makeSingleKey(SandstoneType.class, Value.class, of("SandstoneType"), "sandstone_type"));
        this.register(makeSingleKey(SandType.class, Value.class, of("SandType"), "sand_type"));
        this.register(makeSingleKey(Double.class, MutableBoundedValue.class, of("Saturation"), "saturation"));
        this.register(makeSingleKey(Float.class, MutableBoundedValue.class, of("Scale"), "scale"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Seamless"), "seamless"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("ShouldDrop"), "should_drop"));
        this.register(makeSingleKey(ShrubType.class, Value.class, of("ShrubType"), "shrub_type"));
        this.register(makeListKey(Text.class, of("SignLines"), "sign_lines"));
        this.register(makeSingleKey(SkeletonType.class, Value.class, of("SkeletonType"), "skeleton_type"));
        this.register(makeSingleKey(UUID.class, Value.class, of("SkinUniqueId"), "skin_unique_id"));
        this.register(makeSingleKey(SkullType.class, Value.class, of("SkullType"), "skull_type"));
        this.register(makeSingleKey(SlabType.class, Value.class, of("SlabType"), "slab_type"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("SlimeSize"), "slime_size"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Snowed"), "snowed"));
        this.register(makeSingleKey(EntityType.class, Value.class, of("SpawnableEntityType"), "spawnable_entity_type"));
        this.register(makeWeightedCollectionKey(EntitySnapshot.class, of("SpawnerEntities"), "spawner_entities"));
        this.register(makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerMaximumDelay"), "spawner_maximum_delay"));
        this.register(makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerMaximumNearbyEntities"), "spawner_maximum_nearby_entities"));
        this.register(makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerMinimumDelay"), "spawner_minimum_delay"));
        this.register(makeNextEntityToSpawnKey(of("SpawnerNextEntityToSpawn"), "spawner_next_entity_to_spawn"));
        this.register(makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerRemainingDelay"), "spawner_remaining_delay"));
        this.register(makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerRequiredPlayerRange"), "spawner_required_player_range"));
        this.register(makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerSpawnCount"), "spawner_spawn_count"));
        this.register(makeSingleKey(Short.class, MutableBoundedValue.class, of("SpawnerSpawnRange"), "spawner_spawn_range"));
        this.register(makeSingleKey(StairShape.class, Value.class, of("StairShape"), "stair_shape"));
        this.register(makeMapKey(Statistic.class, Long.class, of("Statistics"), "statistics"));
        this.register(makeSingleKey(StoneType.class, Value.class, of("StoneType"), "stone_type"));
        this.register(makeListKey(ItemEnchantment.class, of("StoredEnchantments"), "stored_enchantments"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("StuckArrows"), "stuck_arrows"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("SuccessCount"), "success_count"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Suspended"), "suspended"));
        this.register(makeOptionalKey(UUID.class, of("TamedOwner"), "tamed_owner"));
        this.register(makeSingleKey(Vector3d.class, Value.class, of("TargetedLocation"), "targeted_location"));
        this.register(makeSingleKey(Integer.class, Value.class, of("TicksRemaining"), "ticks_remaining"));
        this.register(makeSingleKey(Integer.class, MutableBoundedValue.class, of("TotalExperience"), "total_experience"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("TracksOutput"), "tracks_output"));
        this.register(makeListKey(TradeOffer.class, of("TradeOffers"), "trade_offers"));
        this.register(makeSingleKey(TreeType.class, Value.class, of("TreeType"), "tree_type"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Unbreakable"), "unbreakable"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("Vanish"), "vanish"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("VanishIgnoresCollision"), "vanish_ignores_collision"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("VanishPreventsTargeting"), "vanish_prevents_targeting"));
        this.register(makeSingleKey(EntitySnapshot.class, Value.class, of("Vehicle"), "vehicle"));
        this.register(makeSingleKey(Vector3d.class, Value.class, of("Velocity"), "velocity"));
        this.register(makeSingleKey(Profession.class, Value.class, of("VillagerZombieProfession"), "villager_zombie_profession"));
        this.register(makeSingleKey(Double.class, Value.class, of("WalkingSpeed"), "walking_speed"));
        this.register(makeSingleKey(WallType.class, Value.class, of("WallType"), "wall_type"));
        this.register(makeSingleKey(Boolean.class, Value.class, of("WillShatter"), "will_shatter"));
        this.register(makeMapKey(Direction.class, WireAttachmentType.class, of("WireAttachments"), "wire_attachments"));
        this.register(makeSingleKey(WireAttachmentType.class, Value.class, of("WireAttachmentEast"), "wire_attachment_east"));
        this.register(makeSingleKey(WireAttachmentType.class, Value.class, of("WireAttachmentNorth"), "wire_attachment_north"));
        this.register(makeSingleKey(WireAttachmentType.class, Value.class, of("WireAttachmentSouth"), "wire_attachment_south"));
        this.register(makeSingleKey(WireAttachmentType.class, Value.class, of("WireAttachmentWest"), "wire_attachment_west"));
    }

    private final static class Holder {
        private static final KeyRegistryModule INSTANCE = new KeyRegistryModule();
    }
}
