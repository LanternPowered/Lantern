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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeListKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeMapKeyWithKeyAndValue;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeBoundedValueKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeOptionalKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeSetKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeValueKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeWeightedCollectionKey;
import static org.lanternpowered.server.util.UncheckedThrowables.doUnchecked;
import static org.spongepowered.api.data.DataQuery.of;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.data.key.LanternKey;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.type.ArtType;
import org.spongepowered.api.data.type.BodyPart;
import org.spongepowered.api.data.type.CatType;
import org.spongepowered.api.data.type.ChestAttachmentType;
import org.spongepowered.api.data.type.ComparatorType;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.HandPreference;
import org.spongepowered.api.data.type.Hinge;
import org.spongepowered.api.data.type.HorseColor;
import org.spongepowered.api.data.type.HorseStyle;
import org.spongepowered.api.data.type.InstrumentType;
import org.spongepowered.api.data.type.LlamaType;
import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.data.type.ParrotType;
import org.spongepowered.api.data.type.PickupRule;
import org.spongepowered.api.data.type.PortionType;
import org.spongepowered.api.data.type.Profession;
import org.spongepowered.api.data.type.RabbitType;
import org.spongepowered.api.data.type.RailDirection;
import org.spongepowered.api.data.type.SlabPortion;
import org.spongepowered.api.data.type.StairShape;
import org.spongepowered.api.data.type.StructureMode;
import org.spongepowered.api.data.type.Surface;
import org.spongepowered.api.data.type.WireAttachmentType;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.fluid.FluidStackSnapshot;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.item.potion.PotionType;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Axis;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.RespawnLocation;
import org.spongepowered.api.util.rotation.Rotation;
import org.spongepowered.api.util.weighted.WeightedSerializableObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class KeyRegistryModule extends AdditionalPluginCatalogRegistryModule<Key> {

    public static KeyRegistryModule get() {
        return Holder.INSTANCE;
    }

    private final Map<DataQuery, Key> byQuery = new HashMap<>();

    private KeyRegistryModule() {
        super(Keys.class);
    }

    @Override
    protected void doRegistration(Key key, boolean disallowInbuiltPluginIds) {
        super.doRegistration(key, disallowInbuiltPluginIds);
        this.byQuery.putIfAbsent(key.getQuery(), key);
        final LanternKey optionalUnwrappedKey = ((LanternKey) key).getOptionalUnwrappedKey();
        if (optionalUnwrappedKey != null) {
            doRegistration(optionalUnwrappedKey, disallowInbuiltPluginIds);
        }
    }

    public Optional<Key> getByQuery(DataQuery query) {
        checkNotNull(query, "query");
        return Optional.ofNullable(this.byQuery.get(query));
    }

    @Override
    public void registerDefaults() {
        final CauseStack causeStack = CauseStack.current();
        causeStack.pushCause(Lantern.getSpongePlugin());

        register(makeBoundedValueKey(Double.class, DataQuery.of("Absorption"), "absorption"));
        register(makeValueKey(ItemStackSnapshot.class, of("ActiveItem"), "active_item"));
        register(makeValueKey(Boolean.class, of("AffectsSpawning"), "affects_spawning"));
        register(makeBoundedValueKey(Integer.class, of("Age"), "age"));
        register(makeValueKey(Boolean.class, of("AIEnabled"), "ai_enabled"));
        register(makeBoundedValueKey(Integer.class, of("Anger"), "anger"));
        register(makeBoundedValueKey(Integer.class, DataQuery.of("AreaEffectCloudAge"), "area_effect_cloud_age"));
        register(makeValueKey(Color.class, DataQuery.of("AreaEffectCloudColor"), "area_effect_cloud_color"));
        register(makeBoundedValueKey(Integer.class, DataQuery.of("AreaEffectCloudDuration"), "area_effect_cloud_duration"));
        register(makeBoundedValueKey(Integer.class, DataQuery.of("AreaEffectCloudDurationOnUse"), "area_effect_cloud_duration_on_use"));
        register(makeValueKey(Color.class, DataQuery.of("AreaEffectCloudParticleType"), "area_effect_cloud_particle_type"));
        register(makeBoundedValueKey(Double.class, DataQuery.of("AreaEffectCloudRadius"), "area_effect_cloud_radius"));
        register(makeBoundedValueKey(Double.class, DataQuery.of("AreaEffectCloudRadiusOnUse"), "area_effect_cloud_radius_on_use"));
        register(makeBoundedValueKey(Double.class, DataQuery.of("AreaEffectCloudRadiusPerTick"), "area_effect_cloud_radius_per_tick"));
        register(makeBoundedValueKey(Integer.class, DataQuery.of("AreaEffectCloudRadiusReapplicationDelay"), "area_effect_cloud_reapplication_delay"));
        register(makeBoundedValueKey(Integer.class, DataQuery.of("AreaEffectCloudWaitTime"), "area_effect_cloud_wait_time"));
        register(makeValueKey(Boolean.class, of("ArmorStandHasArms"), "armor_stand_has_arms"));
        register(makeValueKey(Boolean.class, of("ArmorStandHasBasePlate"), "armor_stand_has_base_plate"));
        register(makeValueKey(Boolean.class, of("ArmorStandIsSmall"), "armor_stand_is_small"));
        register(makeValueKey(Boolean.class, of("ArmorStandMarker"), "armor_stand_marker"));
        register(makeValueKey(Boolean.class, of("Angry"), "angry"));
        register(makeValueKey(ArtType.class, of("ArtType"), "art_type"));
        register(makeValueKey(Boolean.class, of("Attached"), "attached"));
        register(makeValueKey(Surface.class, of("AttachmentSurface"), "attachment_surface"));
        register(makeBoundedValueKey(Double.class, of("AttackDamage"), "attack_damage"));
        register(makeValueKey(Axis.class, of("Axis"), "axis"));
        register(makeValueKey(DyeColor.class, of("BannerBaseColor"), "banner_base_color"));
        register(makeListKey(PatternLayer.class, of("BannerPatterns"), "banner_patterns"));
        register(makeBoundedValueKey(Float.class, of("BaseSize"), "base_size"));
        register(makeValueKey(EntitySnapshot.class, of("BaseVehicle"), "base_vehicle"));
        register(makeOptionalKey(PotionEffectType.class, of("BeaconPrimaryEffect"), "beacon_primary_effect"));
        register(makeOptionalKey(PotionEffectType.class, of("BeaconSecondaryEffect"), "beacon_secondary_effect"));
        register(makeMapKeyWithKeyAndValue(BodyPart.class, Vector3d.class, of("BodyRotations"), "body_rotations"));
        register(makeValueKey(Text.class, of("BookAuthor"), "book_author"));
        register(makeListKey(Text.class, of("BookPages"), "book_pages"));
        register(makeSetKey(Direction.class, of("BigMushroomPores"), "big_mushroom_pores"));
        register(makeValueKey(Boolean.class, of("BigMushroomPoresDown"), "big_mushroom_pores_down"));
        register(makeValueKey(Boolean.class, of("BigMushroomPoresEast"), "big_mushroom_pores_east"));
        register(makeValueKey(Boolean.class, of("BigMushroomPoresNorth"), "big_mushroom_pores_north"));
        register(makeValueKey(Boolean.class, of("BigMushroomPoresSouth"), "big_mushroom_pores_south"));
        register(makeValueKey(Boolean.class, of("BigMushroomPoresUp"), "big_mushroom_pores_up"));
        register(makeValueKey(Boolean.class, of("BigMushroomPoresWest"), "big_mushroom_pores_west"));
        register(makeSetKey(BlockType.class, of("BreakableBlockTypes"), "breakable_block_types"));
        register(makeValueKey(Boolean.class, of("CanBreed"), "can_breed"));
        register(makeValueKey(Boolean.class, of("CanDropAsItem"), "can_drop_as_item"));
        register(makeValueKey(Boolean.class, of("CanFly"), "can_fly"));
        register(makeValueKey(Boolean.class, of("CanGrief"), "can_grief"));
        register(makeValueKey(Boolean.class, of("CanPlaceAsBlock"), "can_place_as_block"));
        register(makeValueKey(CatType.class, of("CatType"), "cat_type"));
        register(makeValueKey(ChestAttachmentType.class, of("ChestAttachment"), "chest_attachment"));
        register(makeValueKey(Vector3d.class, of("ChestRotation"), "chest_rotation"));
        register(makeValueKey(Color.class, of("Color"), "color"));
        register(makeValueKey(String.class, of("Command"), "command"));
        register(makeValueKey(ComparatorType.class, of("ComparatorType"), "comparator_type"));
        register(makeSetKey(Direction.class, of("ConnectedDirections"), "connected_directions"));
        register(makeValueKey(Boolean.class, of("ConnectedEast"), "connected_east"));
        register(makeValueKey(Boolean.class, of("ConnectedNorth"), "connected_north"));
        register(makeValueKey(Boolean.class, of("ConnectedSouth"), "connected_south"));
        register(makeValueKey(Boolean.class, of("ConnectedWest"), "connected_west"));
        register(makeBoundedValueKey(Integer.class, of("ContainedExperience"), "contained_experience"));
        register(makeBoundedValueKey(Integer.class, of("Cooldown"), "cooldown"));
        register(makeValueKey(Boolean.class, of("CreeperCharged"), "creeper_charged"));
        register(makeValueKey(Boolean.class, of("CriticalHit"), "critical_hit"));
        register(makeValueKey(Boolean.class, of("CustomNameVisible"), "custom_name_visible"));
        register(makeMapKeyWithKeyAndValue(EntityType.class, Double.class, of("EntityDamageMap"), "damage_entity_map"));
        register(makeBoundedValueKey(Integer.class, of("DecayDistance"), "decay_distance"));
        register(makeBoundedValueKey(Integer.class, of("Delay"), "delay"));
        register(makeBoundedValueKey(Integer.class, of("DespawnDelay"), "despawn_delay"));
        register(makeValueKey(Direction.class, of("Direction"), "direction"));
        register(makeValueKey(Boolean.class, of("Disarmed"), "disarmed"));
        register(makeValueKey(Text.class, of("DisplayName"), "display_name"));
        register(makeValueKey(HandPreference.class, of("DominantHand"), "dominant_hand"));
        register(makeValueKey(DyeColor.class, of("DyeColor"), "dye_color"));
        register(makeValueKey(Boolean.class, of("ElderGuardian"), "elder_guardian"));
        register(makeValueKey(Boolean.class, of("EndGatewayAge"), "end_gateway_age"));
        register(makeValueKey(Boolean.class, of("EndGatewayTeleportCooldown"), "end_gateway_teleport_cooldown"));
        register(makeBoundedValueKey(Double.class, of("Exhaustion"), "exhaustion"));
        register(makeValueKey(Boolean.class, of("ExactTeleport"), "exact_teleport"));
        register(makeValueKey(Vector3i.class, of("ExitPosition"), "exit_position"));
        register(makeBoundedValueKey(Integer.class, of("ExperienceFromStartOfLevel"), "experience_from_start_of_level"));
        register(makeBoundedValueKey(Integer.class, of("ExperienceLevel"), "experience_level"));
        register(makeBoundedValueKey(Integer.class, of("ExperienceSinceLevel"), "experience_since_level"));
        register(makeBoundedValueKey(Integer.class, of("ExpirationTicks"), "expiration_ticks"));
        register(makeOptionalKey(Integer.class, of("ExplosionRadius"), "explosion_radius"));
        register(makeValueKey(Boolean.class, of("Extended"), "extended"));
        register(makeValueKey(Boolean.class, of("FallingBlockCanHurtEntities"), "falling_block_can_hurt_entities"));
        register(makeValueKey(BlockState.class, of("FallingBlockState"), "falling_block_state"));
        register(makeBoundedValueKey(Double.class, of("FallDamagePerBlock"), "fall_damage_per_block"));
        register(makeBoundedValueKey(Float.class, of("FallDistance"), "fall_distance"));
        register(makeValueKey(Integer.class, of("FallTime"), "fall_time"));
        register(makeValueKey(Boolean.class, of("Filled"), "filled"));
        register(makeListKey(FireworkEffect.class, of("FireworkEffects"), "firework_effects"));
        register(makeBoundedValueKey(Integer.class, of("FireworkFlightModifier"), "firework_flight_modifier"));
        register(makeBoundedValueKey(Integer.class, of("FireDamageDelay"), "fire_damage_delay"));
        register(makeBoundedValueKey(Integer.class, of("FireTicks"), "fire_ticks"));
        register(makeValueKey(Instant.class, of("FirstDatePlayed"), "first_date_played"));
        register(makeValueKey(FluidStackSnapshot.class, of("FluidItemStack"), "fluid_item_stack"));
        register(makeBoundedValueKey(Integer.class, of("FluidLevel"), "fluid_level"));
        register(makeMapKeyWithKeyAndValue(Direction.class, List.class, of("FluidTankContents"), "fluid_tank_contents"));
        register(makeValueKey(Double.class, of("FlyingSpeed"), "flying_speed"));
        register(makeBoundedValueKey(Integer.class, of("FoodLevel"), "food_level"));
        register(makeValueKey(Integer.class, of("FuseDuration"), "fuse_duration"));
        register(makeValueKey(GameMode.class, of("GameMode"), "game_mode"));
        register(makeBoundedValueKey(Integer.class, of("Generation"), "generation"));
        register(makeValueKey(Boolean.class, of("Glowing"), "glowing"));
        register(makeBoundedValueKey(Integer.class, of("GrowthStage"), "growth_stage"));
        register(makeValueKey(Boolean.class, of("HasGravity"), "has_gravity"));
        register(makeValueKey(Vector3d.class, of("HeadRotation"), "head_rotation"));
        register(makeBoundedValueKey(Double.class, of("Health"), "health"));
        register(makeBoundedValueKey(Double.class, of("HealthScale"), "health_scale"));
        register(makeBoundedValueKey(Float.class, of("Height"), "height"));
        register(makeValueKey(Boolean.class, of("HideAttributes"), "hide_attributes"));
        register(makeValueKey(Boolean.class, of("HideCanDestroy"), "hide_can_destroy"));
        register(makeValueKey(Boolean.class, of("HideCanPlace"), "hide_can_place"));
        register(makeValueKey(Boolean.class, of("HideEnchantments"), "hide_enchantments"));
        register(makeValueKey(Boolean.class, of("HideMiscellaneous"), "hide_miscellaneous"));
        register(makeValueKey(Boolean.class, of("HideUnbreakable"), "hide_unbreakable"));
        register(makeValueKey(Hinge.class, of("HingePosition"), "hinge_position"));
        register(makeValueKey(HorseColor.class, of("HorseColor"), "horse_color"));
        register(makeValueKey(HorseStyle.class, of("HorseStyle"), "horse_style"));
        register(makeValueKey(Boolean.class, of("InfiniteDespawnDelay"), "infinite_despawn_delay"));
        register(makeValueKey(Boolean.class, of("InfinitePickupDelay"), "infinite_pickup_delay"));
        register(makeValueKey(InstrumentType.class, of("Instrument"), "instrument"));
        register(makeValueKey(Boolean.class, of("InvisibilityIgnoresCollision"), "invisibility_ignores_collision"));
        register(makeValueKey(Boolean.class, of("InvisibilityPreventsTargeting"), "invisibility_prevents_targeting"));
        register(makeValueKey(Boolean.class, of("Invisible"), "invisible"));
        register(makeBoundedValueKey(Integer.class, of("InvulnerabilityTicks"), "invulnerability_ticks"));
        register(makeValueKey(Boolean.class, of("Invulnerable"), "invulnerable"));
        register(makeValueKey(Boolean.class, of("InWall"), "in_wall"));
        register(makeValueKey(Boolean.class, of("IsAdult"), "is_adult"));
        register(makeValueKey(Boolean.class, of("IsAflame"), "is_aflame"));
        register(makeValueKey(Boolean.class, of("IsElytraFlying"), "is_elytra_flying"));
        register(makeValueKey(Boolean.class, of("IsFlying"), "is_flying"));
        register(makeValueKey(Boolean.class, of("IsJohnny"), "is_johnny"));
        register(makeValueKey(Boolean.class, of("IsPlaying"), "is_playing"));
        register(makeValueKey(Boolean.class, of("IsScreaming"), "is_screaming"));
        register(makeValueKey(Boolean.class, of("IsSheared"), "is_sheared"));
        register(makeValueKey(Boolean.class, of("IsSilent"), "is_silent"));
        register(makeValueKey(Boolean.class, of("IsSitting"), "is_sitting"));
        register(makeValueKey(Boolean.class, of("IsSleeping"), "is_sleeping"));
        register(makeValueKey(Boolean.class, of("IsSneaking"), "is_sneaking"));
        register(makeValueKey(Boolean.class, of("IsSprinting"), "is_sprinting"));
        register(makeValueKey(Boolean.class, of("IsWet"), "is_wet"));
        register(makeValueKey(BlockState.class, of("ItemBlockState"), "item_blockstate"));
        register(makeBoundedValueKey(Integer.class, of("ItemDurability"), "item_durability"));
        register(makeListKey(Enchantment.class, of("ItemEnchantments"), "item_enchantments"));
        register(makeListKey(Text.class, of("ItemLore"), "item_lore"));
        register(makeValueKey(Boolean.class, of("JohnnyVindicator"), "johnny_vindicator"));
        register(makeBoundedValueKey(Integer.class, of("KnockbackStrength"), "knockback_strength"));
        register(makeOptionalKey(EntitySnapshot.class, of("LastAttacker"), "last_attacker"));
        register(makeOptionalKey(Text.class, of("LastCommandOutput"), "last_command_output"));
        register(makeOptionalKey(Double.class, of("LastDamage"), "last_damage"));
        register(makeValueKey(Instant.class, of("LastDatePlayed"), "last_date_played"));
        register(makeValueKey(Integer.class, of("Layer"), "layer"));
        register(makeValueKey(EntitySnapshot.class, of("LeashHolder"), "leash_holder"));
        register(makeValueKey(Vector3d.class, of("LeftArmRotation"), "left_arm_rotation"));
        register(makeValueKey(Vector3d.class, of("LeftLegRotation"), "left_leg_rotation"));
        register(makeValueKey(Boolean.class, of("Lit"), "lit"));
        register(makeBoundedValueKey(Integer.class, of("LlamaStrength"), "llama_strength"));
        register(makeValueKey(LlamaType.class, of("LlamaType"), "llama_type"));
        register(makeValueKey(String.class, of("LockToken"), "lock_token"));
        register(makeBoundedValueKey(Integer.class, of("MaxAir"), "max_air"));
        register(makeBoundedValueKey(Integer.class, of("MaxBurnTime"), "max_burn_time"));
        register(makeBoundedValueKey(Integer.class, of("MaxCookTime"), "max_cook_time"));
        register(makeBoundedValueKey(Double.class, of("MaxFallDamage"), "max_fall_damage"));
        register(makeBoundedValueKey(Double.class, of("MaxHealth"), "max_health"));
        register(makeBoundedValueKey(Integer.class, of("Moisture"), "moisture"));
        register(makeValueKey(NotePitch.class, of("NotePitch"), "note_pitch"));
        register(makeValueKey(Boolean.class, of("Occupied"), "occupied"));
        register(makeValueKey(Integer.class, of("Offset"), "offset"));
        register(makeValueKey(Boolean.class, of("Open"), "open"));
        register(makeValueKey(ParrotType.class, of("ParrotType"), "parrot_type"));
        register(makeBoundedValueKey(Integer.class, of("PassedBurnTime"), "passed_burn_time"));
        register(makeBoundedValueKey(Integer.class, of("PassedCookTime"), "passed_cook_time"));
        register(makeListKey(UUID.class, of("Passengers"), "passengers"));
        register(makeValueKey(Boolean.class, of("Persistent"), "persistent"));
        register(makeBoundedValueKey(Integer.class, of("PickupDelay"), "pickup_delay"));
        register(makeValueKey(PickupRule.class, of("PickupRule"), "pickup_rule"));
        register(makeValueKey(Boolean.class, of("PigSaddle"), "pig_saddle"));
        register(makeSetKey(BlockType.class, of("PlaceableBlocks"), "placeable_blocks"));
        register(makeListKey(String.class, of("PlainBookPages"), "plain_book_pages"));
        register(makeValueKey(Boolean.class, of("PlayerCreated"), "player_created"));
        register(makeValueKey(PortionType.class, of("PortionType"), "portion_type"));
        register(makeValueKey(PotionType.class, of("PotionType"), "potion_type"));
        register(makeListKey(PotionEffect.class, of("PotionEffects"), "potion_effects"));
        register(makeValueKey(Integer.class, of("Power"), "power"));
        register(makeValueKey(Boolean.class, of("Powered"), "powered"));
        register(makeValueKey(RabbitType.class, of("RabbitType"), "rabbit_type"));
        register(makeValueKey(RailDirection.class, of("RailDirection"), "rail_direction"));
        register(makeBoundedValueKey(Integer.class, of("RemainingAir"), "remaining_air"));
        register(makeBoundedValueKey(Integer.class, of("RemainingBrewTime"), "remaining_brew_time"));
        register(makeValueKey(BlockState.class, of("RepresentedBlock"), "represented_block"));
        register(makeValueKey(ItemStackSnapshot.class, of("RepresentedItem"), "represented_item"));
        register(makeValueKey(GameProfile.class, of("RepresentedPlayer"), "represented_player"));
        register(makeMapKeyWithKeyAndValue(UUID.class, RespawnLocation.class, of("RespawnLocations"), "respawn_locations"));
        register(makeValueKey(Vector3d.class, of("RightArmRotation"), "right_arm_rotation"));
        register(makeValueKey(Vector3d.class, of("RightLegRotation"), "right_leg_rotation"));
        register(makeValueKey(Rotation.class, of("Rotation"), "rotation"));
        register(makeBoundedValueKey(Double.class, of("Saturation"), "saturation"));
        register(makeBoundedValueKey(Float.class, of("Scale"), "scale"));
        register(makeValueKey(Boolean.class, of("ShouldDrop"), "should_drop"));
        register(makeListKey(Text.class, of("SignLines"), "sign_lines"));
        register(makeValueKey(ProfileProperty.class, of("Skin"), "skin"));
        register(makeValueKey(SlabPortion.class, of("SlabPortion"), "sponge:slab_portion"));
        register(makeBoundedValueKey(Integer.class, of("SlimeSize"), "slime_size"));
        register(makeValueKey(Boolean.class, of("Snowed"), "snowed"));
        register(makeValueKey(EntityType.class, of("SpawnableEntityType"), "spawnable_entity_type"));
        register(makeWeightedCollectionKey(EntityArchetype.class, of("SpawnerEntities"), "spawner_entities"));
        register(makeBoundedValueKey(Short.class, of("SpawnerMaximumDelay"), "spawner_maximum_delay"));
        register(makeBoundedValueKey(Short.class, of("SpawnerMaximumNearbyEntities"), "spawner_maximum_nearby_entities"));
        register(makeBoundedValueKey(Short.class, of("SpawnerMinimumDelay"), "spawner_minimum_delay"));
        register(makeValueKey(new TypeToken<WeightedSerializableObject<EntityArchetype>>() {}, of("SpawnerNextEntityToSpawn"), "spawner_next_entity_to_spawn"));
        register(makeBoundedValueKey(Short.class, of("SpawnerRemainingDelay"), "spawner_remaining_delay"));
        register(makeBoundedValueKey(Short.class, of("SpawnerRequiredPlayerRange"), "spawner_required_player_range"));
        register(makeBoundedValueKey(Short.class, of("SpawnerSpawnCount"), "spawner_spawn_count"));
        register(makeBoundedValueKey(Short.class, of("SpawnerSpawnRange"), "spawner_spawn_range"));
        register(makeValueKey(StairShape.class, of("StairShape"), "stair_shape"));
        register(makeMapKeyWithKeyAndValue(Statistic.class, Long.class, of("Statistics"), "statistics"));
        register(makeListKey(Enchantment.class, of("StoredEnchantments"), "stored_enchantments"));
        register(makeValueKey(String.class, of("StructureAuthor"), "structure_author"));
        register(makeValueKey(Boolean.class, of("StructureIgnoreEntities"), "structure_ignore_entities"));
        register(makeValueKey(Float.class, of("StructureIntegrity"), "structure_integrity"));
        register(makeValueKey(StructureMode.class, of("StructureMode"), "structure_mode"));
        register(makeValueKey(Vector3i.class, of("StructurePosition"), "structure_position"));
        register(makeValueKey(Boolean.class, of("StructurePowered"), "structure_powered"));
        register(makeValueKey(Long.class, of("StructureSeed"), "structure_seed"));
        register(makeValueKey(Boolean.class, of("StructureShowAir"), "structure_show_air"));
        register(makeValueKey(Boolean.class, of("StructureShowBoundingBox"), "structure_show_bounding_box"));
        register(makeValueKey(Vector3i.class, of("StructureSize"), "structure_size"));
        register(makeBoundedValueKey(Integer.class, of("StuckArrows"), "stuck_arrows"));
        register(makeBoundedValueKey(Integer.class, of("SuccessCount"), "success_count"));
        register(makeValueKey(Boolean.class, of("Suspended"), "suspended"));
        register(makeOptionalKey(UUID.class, of("TamedOwner"), "tamed_owner"));
        register(makeSetKey(String.class, of("Tags"), "tags"));
        register(makeValueKey(Vector3d.class, of("TargetedLocation"), "targeted_location"));
        register(makeValueKey(Integer.class, of("TicksRemaining"), "ticks_remaining"));
        register(makeBoundedValueKey(Integer.class, of("TotalExperience"), "total_experience"));
        register(makeValueKey(Boolean.class, of("TracksOutput"), "tracks_output"));
        register(makeListKey(TradeOffer.class, of("TradeOffers"), "trade_offers"));
        register(makeValueKey(Boolean.class, of("Unbreakable"), "unbreakable"));
        register(makeValueKey(Boolean.class, of("UpdateGameProfile"), "update_game_profile"));
        register(makeValueKey(Boolean.class, of("Vanish"), "vanish"));
        register(makeValueKey(Boolean.class, of("VanishIgnoresCollision"), "vanish_ignores_collision"));
        register(makeValueKey(Boolean.class, of("VanishPreventsTargeting"), "vanish_prevents_targeting"));
        register(makeValueKey(EntitySnapshot.class, of("Vehicle"), "vehicle"));
        register(makeValueKey(Vector3d.class, of("Velocity"), "velocity"));
        register(makeOptionalKey(Profession.class, of("VillagerZombieProfession"), "villager_zombie_profession"));
        register(makeValueKey(Double.class, of("WalkingSpeed"), "walking_speed"));
        register(makeValueKey(Boolean.class, of("WillShatter"), "will_shatter"));
        register(makeMapKeyWithKeyAndValue(Direction.class, WireAttachmentType.class, of("WireAttachments"), "wire_attachments"));
        register(makeValueKey(WireAttachmentType.class, of("WireAttachmentEast"), "wire_attachment_east"));
        register(makeValueKey(WireAttachmentType.class, of("WireAttachmentNorth"), "wire_attachment_north"));
        register(makeValueKey(WireAttachmentType.class, of("WireAttachmentSouth"), "wire_attachment_south"));
        register(makeValueKey(WireAttachmentType.class, of("WireAttachmentWest"), "wire_attachment_west"));

        causeStack.popCause();
        causeStack.pushCause(Lantern.getImplementationPlugin());

        // Register the lantern keys
        for (Field field : LanternKeys.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                final Object object = doUnchecked(() -> field.get(null));
                if (object instanceof Key) {
                    register((Key) object);
                }
            }
        }

        causeStack.popCause();

        // Warn about keys that are not registered
        for (Field field : Key.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                try {
                    final Object object = field.get(null);
                    if (object instanceof Key) {
                        ((Key) object).getKey(); // Trigger a exception if it's a dummy
                    }
                } catch (Exception e) {
                    Lantern.getLogger().warn("No key registered for field: " + field.getName());
                }
            }
        }
    }

    private final static class Holder {
        private static final KeyRegistryModule INSTANCE = new KeyRegistryModule();
    }
}
