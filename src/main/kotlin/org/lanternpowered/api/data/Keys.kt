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
package org.lanternpowered.api.data

import org.lanternpowered.api.block.BlockSoundGroup
import org.lanternpowered.api.block.BlockState
import org.lanternpowered.api.boss.BossBar
import org.lanternpowered.api.key.lanternKey
import org.lanternpowered.api.key.spongeKey
import org.lanternpowered.api.text.Text
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.data.meta.BannerPatternLayer
import org.spongepowered.api.data.type.ArmorMaterial
import org.spongepowered.api.data.type.ArtType
import org.spongepowered.api.data.type.AttachmentSurface
import org.spongepowered.api.data.type.BoatType
import org.spongepowered.api.data.type.BodyPart
import org.spongepowered.api.data.type.CatType
import org.spongepowered.api.data.type.ChestAttachmentType
import org.spongepowered.api.data.type.ComparatorMode
import org.spongepowered.api.data.type.DoorHinge
import org.spongepowered.api.data.type.DyeColor
import org.spongepowered.api.data.type.FoxType
import org.spongepowered.api.data.type.HandPreference
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.data.type.HorseColor
import org.spongepowered.api.data.type.HorseStyle
import org.spongepowered.api.data.type.InstrumentType
import org.spongepowered.api.data.type.LlamaType
import org.spongepowered.api.data.type.MatterState
import org.spongepowered.api.data.type.MooshroomType
import org.spongepowered.api.data.type.NotePitch
import org.spongepowered.api.data.type.PandaGene
import org.spongepowered.api.data.type.ParrotType
import org.spongepowered.api.data.type.PhantomPhase
import org.spongepowered.api.data.type.PickupRule
import org.spongepowered.api.data.type.PistonType
import org.spongepowered.api.data.type.PortionType
import org.spongepowered.api.data.type.ProfessionType
import org.spongepowered.api.data.type.RabbitType
import org.spongepowered.api.data.type.RailDirection
import org.spongepowered.api.data.type.SkinPart
import org.spongepowered.api.data.type.SlabPortion
import org.spongepowered.api.data.type.SpellType
import org.spongepowered.api.data.type.StairShape
import org.spongepowered.api.data.type.StructureMode
import org.spongepowered.api.data.type.ToolType
import org.spongepowered.api.data.type.TropicalFishShape
import org.spongepowered.api.data.type.VillagerType
import org.spongepowered.api.data.type.WireAttachmentType
import org.spongepowered.api.data.type.WoodType
import org.spongepowered.api.data.value.ListValue
import org.spongepowered.api.data.value.MapValue
import org.spongepowered.api.data.value.SetValue
import org.spongepowered.api.data.Keys as SpongeKeys
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.WeightedCollectionValue
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.potion.PotionEffect
import org.spongepowered.api.effect.potion.PotionEffectType
import org.spongepowered.api.effect.sound.music.MusicDisc
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityArchetype
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.entity.explosive.EnderCrystal
import org.spongepowered.api.entity.living.Living
import org.spongepowered.api.entity.living.animal.Sheep
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.fluid.FluidStackSnapshot
import org.spongepowered.api.item.FireworkEffect
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.enchantment.Enchantment
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import org.spongepowered.api.item.merchant.TradeOffer
import org.spongepowered.api.item.potion.PotionType
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.profile.property.ProfileProperty
import org.spongepowered.api.projectile.source.ProjectileSource
import org.spongepowered.api.raid.RaidWave
import org.spongepowered.api.statistic.Statistic
import org.spongepowered.api.util.Axis
import org.spongepowered.api.util.Color
import org.spongepowered.api.util.Direction
import org.spongepowered.api.util.RespawnLocation
import org.spongepowered.api.util.rotation.Rotation
import org.spongepowered.api.util.weighted.WeightedSerializableObject
import org.spongepowered.math.vector.Vector2i
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3i
import org.spongepowered.plugin.PluginContainer
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration

/**
 * All the known keys in lantern.
 */
object Keys {

    // region Lantern Keys

    /**
     * The acceleration of a [DamagingProjectile]. The unit is in meters
     * per second ^ 2 (m/s^2).
     *
     * This key isn't the same as [SpongeKeys.ACCELERATION], which uses
     * meters per tick ^ 2 (m/t^2).
     */
    val ACCELERATION: Key<Value<Vector3d>> = valueKeyOf(lanternKey("acceleration"))

    /**
     * Represents the score of something, primarily used for the score
     * on a player death screen.
     */
    val SCORE: Key<Value<Int>> = valueKeyOf(lanternKey("score"))

    /**
     * The mass of something, e.g. [Entity]s. The unit is in kilograms (kg).
     */
    val MASS: Key<Value<Double>> = valueKeyOf(lanternKey("mass"))

    /**
     * Represents the velocity of an object, primarily used for entities. The
     * unit is in meters per second (m/s).
     *
     * This key isn't the same as [SpongeKeys.VELOCITY], which uses
     * meters per tick (m/t).
     */
    val VELOCITY: Key<Value<Vector3d>> = valueKeyOf(lanternKey("velocity"))

    /**
     * The gravitational acceleration that is applied to something, e.g. [Entity]s. The
     * unit is in meters per second ^ 2 (m/s^2).
     */
    val GRAVITATIONAL_ACCELERATION: Key<Value<Double>> = valueKeyOf(lanternKey("gravitational_acceleration"))

    /**
     * Whether something is affected by gravity, e.g. [Entity]s and [BlockState]s.
     */
    val IS_GRAVITY_AFFECTED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_gravity_affected"))

    /**
     * The age of an entity. e.g. The age of an [AreaEffectCloud].
     *
     * Note that in vanilla this value is not persisted for most entities.
     */
    val AGE: Key<Value<Duration>> = valueKeyOf(lanternKey("age"))

    /**
     * The potential max speed of a [Minecart]. The unit is in meters per second (m/s).
     *
     * This key isn't the same as [SpongeKeys.POTENTIAL_MAX_SPEED], which uses
     * meters per tick (m/t).
     */
    val POTENTIAL_MAX_SPEED: Key<Value<Double>> = valueKeyOf(lanternKey("potential_max_speed"))

    /**
     * The remaining food of a [Humanoid].
     *
     * For a [Humanoid], food level has health effects, depending on game difficulty and
     * hunger levels. If the food level is high enough, the humanoid may heal. If the food level is at 0,
     * the humanoid may starve.
     */
    val FOOD: Key<Value<Double>> = valueKeyOf(lanternKey("food"))

    /**
     * The maximum food of a [Humanoid].
     *
     * Defaults to 20 in vanilla for players.
     */
    val MAX_FOOD: Key<Value<Double>> = valueKeyOf(lanternKey("max_food"))

    /**
     * The maximum exhaustion of an entity.
     */
    val MAX_EXHAUSTION: Key<Value<Double>> = valueKeyOf<Value<Double>>(lanternKey("max_exhaustion"))

    /**
     * The amount of food a food [ItemStack] restores when eaten.
     */
    val REPLENISHED_FOOD: Key<Value<Double>> = valueKeyOf(lanternKey("replenished_food"))

    /**
     * The amount of health a something restores. E.g. when eating certain food/drinking potions.
     */
    val RESTORED_HEALTH: Key<Value<Double>> = valueKeyOf(lanternKey("restored_health"))

    /**
     * Whether an [ItemStack] can always be consumed, even if e.g. food has reached its maximum.
     */
    val IS_ALWAYS_CONSUMABLE: Key<Value<Boolean>> = valueKeyOf(lanternKey("is_always_consumable"))

    /**
     * Whether an [Entity] is invulnerable.
     *
     * This does not protect from the void, players in creative mode,
     * and manual killing like the /kill command.
     */
    val IS_INVULNERABLE: Key<Value<Boolean>> = valueKeyOf(lanternKey("is_invulnerable"))

    /**
     * The block sound group of a block.
     */
    val BLOCK_SOUND_GROUP: Key<Value<BlockSoundGroup>> = valueKeyOf(lanternKey("block_sound_group"))

    /**
     * The hand of a entity that is currently active with an interaction.
     */
    val ACTIVE_HAND: Key<Value<HandType>> = valueKeyOf(lanternKey("active_hand"))

    /**
     * The pickup delay of an [Item].
     */
    val PICKUP_DELAY: Key<Value<Duration>> = valueKeyOf(lanternKey("pickup_delay"))

    /**
     * The despawn delay of a [Item], [Endermite], [Weather] [TraderLlama] or [EyeOfEnder].
     */
    val DESPAWN_DELAY: Key<Value<Duration>> = valueKeyOf(lanternKey("despawn_delay"))

    /**
     * A set with all the parts of a skin that should be displayed.
     */
    val DISPLAYED_SKIN_PARTS: Key<SetValue<SkinPart>> = valueKeyOf(lanternKey("displayed_skin_parts"))

    /**
     * Whether a snowman has a pumpkin head.
     */
    val HAS_PUMPKIN_HEAD: Key<Value<Boolean>> = valueKeyOf(lanternKey("has_pumpkin_head"))

    /**
     * Whether an entity is a baby.
     */
    val IS_BABY: Key<Value<Boolean>> = valueKeyOf(lanternKey("is_baby"))

    /**
     * Whether something is holding its hands up, usually related to zombies.
     */
    val ARE_HANDS_UP: Key<Value<Boolean>> = valueKeyOf(lanternKey("are_hands_up"))

    // endregion

    // region Sponge Keys

    /**
     * The [PotionEffectTypes.ABSORPTION] amount of a [Living] entity.
     */
    val ABSORPTION: Key<Value<Double>> = valueKeyOf(spongeKey("absorption"))

    /**
     * The item a [Living] entity is currently using.
     * For example a player eating a food or blocking with a shield.
     *
     *
     * If there is no item, the snapshot will be empty. You can check this
     * with [ItemStackSnapshot.isEmpty].
     */
    val ACTIVE_ITEM: Key<Value<ItemStackSnapshot>> = valueKeyOf(spongeKey("active_item"))

    /**
     * Whether a [Player]s affects spawning.
     *
     * A [Player] who does not affect spawning will be treated as a
     * spectator in regards to spawning. A [MobSpawner] will not be
     * activated by his presence and mobs around him may naturally despawn
     * if there is no other Player around who affects spawning.
     */
    val AFFECTS_SPAWNING: Key<Value<Boolean>> = valueKeyOf(spongeKey("affects_spawning"))

    /**
     * The modifier to [Keys.VELOCITY] of a [Minecart] while airborne.
     */
    val AIRBORNE_VELOCITY_MODIFIER: Key<Value<Vector3d>> = valueKeyOf(spongeKey("airborne_velocity_modifier"))

    /**
     * The anger level of a [ZombiePigman].
     *
     * Unlike [Keys.IS_ANGRY], the aggressiveness represented by this key may
     * fade over time and the entity will become peaceful again once its anger
     * reaches its minimum.
     */
    val ANGER_LEVEL: Key<Value<Int>> = valueKeyOf(spongeKey("anger_level"))

    /**
     * The set of [PotionEffect]s applied on use of an [ItemStack].
     * Readonly
     */
    val APPLICABLE_POTION_EFFECTS: Key<WeightedCollectionValue<PotionEffect>> = valueKeyOf(spongeKey("applicable_potion_effects"))

    /**
     * The enchantments applied to an [ItemStack].
     *
     * This data is usually applicable to all types of armor, weapons and
     * tools. Enchantments that are only stored on an item stack in order to
     * be transferred to another item (like on [ItemTypes.ENCHANTED_BOOK]s)
     * use the [STORED_ENCHANTMENTS] key instead.)
     */
    val APPLIED_ENCHANTMENTS: Key<ListValue<Enchantment>> = valueKeyOf(spongeKey("applied_enchantments"))

    /**
     * The [ArmorMaterial] of an armor [ItemStack].
     * Readonly
     */
    val ARMOR_MATERIAL: Key<Value<ArmorMaterial>> = valueKeyOf(spongeKey("armor_material"))

    /**
     * The type of [ArtType] shown by [Painting]s.
     */
    val ART_TYPE: Key<Value<ArtType>> = valueKeyOf(spongeKey("art_type"))

    /**
     * The attachment [AttachmentSurface] of a button or lever [BlockState]
     */
    val ATTACHMENT_SURFACE: Key<Value<AttachmentSurface>> = valueKeyOf(spongeKey("attachment_surface"))

    /**
     * The damage dealt by an [ArrowEntity] on impact.
     */
    val ATTACK_DAMAGE: Key<Value<Double>> = valueKeyOf(spongeKey("attack_damage"))

    /**
     * The time of a [Ravager] is considered attacking.
     */
    val ATTACK_TIME: Key<Value<Int>> = valueKeyOf(spongeKey("attack_time"))

    /**
     * The author of a [ItemTypes.WRITTEN_BOOK] [ItemStack].
     */
    val AUTHOR: Key<Value<Text>> = valueKeyOf(spongeKey("author"))

    /**
     * The [Axis] direction of a [BlockState].
     */
    val AXIS: Key<Value<Axis>> = valueKeyOf(spongeKey("axis"))

    /**
     * The ticks until a [Ageable] turns into an adult.
     */
    val BABY_TICKS: Key<Value<Int>> = valueKeyOf(spongeKey("baby_ticks"))

    /**
     * The [BannerPatternLayer]s of a [Banner].
     */
    val BANNER_PATTERN_LAYERS: Key<ListValue<BannerPatternLayer>> = valueKeyOf(spongeKey("banner_pattern_layers"))

    /**
     * The width of the physical form of an [Entity].
     *
     *
     * Together with [.HEIGHT] and [.SCALE] this defines
     * the size of an [Entity].
     * Readonly
     */
    val BASE_SIZE: Key<Value<Double>> = valueKeyOf(spongeKey("base_size"))

    /**
     * The base vehicle a passenger is riding at the moment.
     * This may be different from [Keys.VEHICLE] as the
     * vehicle an [Entity] is riding may itself be the passenger of
     * another vehicle.
     * Readonly
     */
    val BASE_VEHICLE: Key<Value<Entity>> = valueKeyOf(spongeKey("base_vehicle"))

    /**
     * The target entity of a [Guardian] beam.
     */
    val BEAM_TARGET_ENTITY: Key<Value<Living>> = valueKeyOf(spongeKey("beam_target_entity"))

    /**
     * The default temperature of a biome at a specific [ServerLocation].
     * For the exact block temperature see [.BLOCK_TEMPERATURE].
     * Readonly
     */
    val BIOME_TEMPERATURE: Key<Value<Double>> = valueKeyOf(spongeKey("biome_temperature"))

    /**
     * The blast resistance of a [BlockState].
     * Readonly
     */
    val BLAST_RESISTANCE: Key<Value<Double>> = valueKeyOf(spongeKey("blast_resistance"))

    /**
     * The amount of light that is emitted by the surrounding blocks at a block [ServerLocation].
     * The value scales normally from 0 to 1.
     *
     * In vanilla minecraft is this value in steps of 1/15 from 0 to 1.
     *
     * For the skylight see [.SKY_LIGHT].
     * Readonly
     */
    val BLOCK_LIGHT: Key<Value<Int>> = valueKeyOf(spongeKey("block_light"))

    /**
     * The [BlockState] of a [BlockOccupiedMinecart] or [FallingBlock].
     */
    val BLOCK_STATE: Key<Value<BlockState>> = valueKeyOf(spongeKey("block_state"))

    /**
     * The temperature at a specific [ServerLocation].
     * For the default biome temperature see [.BIOME_TEMPERATURE].
     * Readonly
     */
    val BLOCK_TEMPERATURE: Key<Value<Double>> = valueKeyOf(spongeKey("block_temperature"))

    /**
     * The type of the boat
     */
    var BOAT_TYPE: Key<Value<BoatType>> = valueKeyOf(spongeKey("boat_type"))

    /**
     * The rotation of specific body parts of a [ArmorStand] or [Living].
     *
     *
     * This value provides a mapping, effectively combining the data
     * referenced by [.HEAD_ROTATION], [.CHEST_ROTATION],
     * [.RIGHT_ARM_ROTATION], [.LEFT_ARM_ROTATION],
     * [.RIGHT_LEG_ROTATION], and [.LEFT_LEG_ROTATION].
     */
    val BODY_ROTATIONS: Key<MapValue<BodyPart, Vector3d>> = valueKeyOf(spongeKey("body_rotations"))

    /**
     * The [BossBar] displayed to the client by a [Boss].
     * Readonly but mutable?
     */
    val BOSS_BAR: Key<Value<BossBar>> = valueKeyOf(spongeKey("boss_bar"))

    /**
     * The [BlockType]s able to be broken by an [ItemStack].
     */
    val BREAKABLE_BLOCK_TYPES: Key<SetValue<BlockType>> = valueKeyOf(spongeKey("breakable_block_types"))

    /**
     * The current breeder of an [Animal], usually a [Player]s UUID.
     */
    val BREEDER: Key<Value<UUID>> = valueKeyOf(spongeKey("breeder"))

    /**
     * The ticks until an [Animal] can breed again. Also see [.CAN_BREED].
     */
    val BREEDING_COOLDOWN: Key<Value<Int>> = valueKeyOf(spongeKey("breeding_cooldown"))

    /**
     * The burntime of an [ItemStack] fuel in a furnace.
     * See [.FUEL] for the time
     * Readonly
     */
    val BURN_TIME: Key<Value<Int>> = valueKeyOf(spongeKey("burn_time"))

    /**
     * Whether an [Animal] can breed.
     * In Vanilla, animals can breed if their [Keys.BREEDING_COOLDOWN] is equal to 0.
     */
    val CAN_BREED: Key<Value<Boolean>> = valueKeyOf(spongeKey("can_breed"))

    /**
     * Whether a [FallingBlock] can drop as an item.
     */
    val CAN_DROP_AS_ITEM: Key<Value<Boolean>> = valueKeyOf(spongeKey("can_drop_as_item"))

    /**
     * Whether a [Humanoid] can fly.
     *
     * For a [Player] this means they are able to toggle flight mode
     * by double-tapping the jump button.
     */
    val CAN_FLY: Key<Value<Boolean>> = valueKeyOf(spongeKey("can_fly"))

    /**
     * Whether a [Living] entity may change blocks.
     * This mostly applies to [Enderman] or
     * [Creeper]s, but also to some projectiles like [FireballEntity]s or [WitherSkull].
     */
    val CAN_GRIEF: Key<Value<Boolean>> = valueKeyOf(spongeKey("can_grief"))

    /**
     * The set of harvestable [BlockType]s with an [ItemStack]. [.EFFICIENCY]
     * Readonly
     */
    val CAN_HARVEST: Key<SetValue<BlockType>> = valueKeyOf(spongeKey("can_harvest"))

    /**
     * Whether a [FallingBlock] will damage an [Entity] it lands on.
     */
    val CAN_HURT_ENTITIES: Key<Value<Boolean>> = valueKeyOf(spongeKey("can_hurt_entities"))

    /**
     * Whether a [Raider] can join a raid.
     */
    val CAN_JOIN_RAID: Key<Value<Boolean>> = valueKeyOf(spongeKey("can_join_raid"))

    /**
     * Whether a [Boat] can move on land.
     */
    val CAN_MOVE_ON_LAND: Key<Value<Boolean>> = valueKeyOf(spongeKey("can_move_on_land"))

    /**
     * Whether a [FallingBlock] will place itself upon landing.
     */
    val CAN_PLACE_AS_BLOCK: Key<Value<Boolean>> = valueKeyOf(spongeKey("can_place_as_block"))

    /**
     * The current casting time of a [Spellcaster].
     */
    val CASTING_TIME: Key<Value<Int>> = valueKeyOf(spongeKey("casting_time"))

    /**
     * The type of a [Cat].
     */
    val CAT_TYPE: Key<Value<CatType>> = valueKeyOf(spongeKey("cat_type"))

    /**
     * The attachment of a [BlockTypes.CHEST] or [BlockTypes.TRAPPED_CHEST] [BlockState].
     */
    val CHEST_ATTACHMENT_TYPE: Key<Value<ChestAttachmentType>> = valueKeyOf(spongeKey("chest_attachment_type"))

    /**
     * The rotation of the [BodyParts.CHEST].
     */
    val CHEST_ROTATION: Key<Value<Vector3d>> = valueKeyOf(spongeKey("chest_rotation"))

    /**
     * The [Color] of an [ItemStack]
     *
     * e.g. [ItemTypes.LEATHER_CHESTPLATE] or [ItemTypes.POTION] custom color
     *
     * or an [AreaEffectCloud].
     */
    val COLOR: Key<Value<Color>> = valueKeyOf(spongeKey("color"))

    /**
     * A command stored in a [CommandBlock] or [CommandBlockMinecart].
     */
    val COMMAND: Key<Value<String>> = valueKeyOf(spongeKey("command"))

    /**
     * The [ComparatorMode] of a [BlockTypes.COMPARATOR] [BlockState].
     */
    val COMPARATOR_MODE: Key<Value<ComparatorMode>> = valueKeyOf(spongeKey("comparator_mode"))

    /**
     * The connected directions of a [BlockState].
     *
     * e.g. [BlockTypes.GLASS_PANE], [BlockTypes.IRON_BARS], [BlockTypes.CHEST],
     */
    val CONNECTED_DIRECTIONS: Key<SetValue<Direction>> = valueKeyOf(spongeKey("connected_directions"))

    /**
     * The container [ItemType] of an [ItemStack].
     * e.g. [ItemTypes.BUCKET] for a [ItemTypes.WATER_BUCKET] stack.
     * Readonly
     */
    val CONTAINER_ITEM: Key<Value<ItemType>> = valueKeyOf(spongeKey("container_item"))

    /**
     * The amount of ticks a [Hopper] has to wait before transferring the next item. (in Vanilla this is 8 ticks)
     * or
     * The amount of ticks a [EndGateway] has to wait for the next teleportation.
     */
    val COOLDOWN: Key<Value<Int>> = valueKeyOf(spongeKey("cooldown"))

    /**
     * The creator, usually of an [Entity]. It is up to the implementation to define.
     */
    val CREATOR: Key<Value<UUID>> = valueKeyOf(spongeKey("creator"))

    /**
     * The current [SpellType] a [Spellcaster] is casting.
     */
    val CURRENT_SPELL: Key<Value<SpellType>> = valueKeyOf(spongeKey("current_spell"))

    /**
     * The damage dealt towards entities of a specific [EntityType] by a [DamagingProjectile].
     *
     *
     * Note that in events, the damage defined for the provided
     * [EntityType] will take priority over the "default" damage as
     * defined from [DamagingProjectile.attackDamage].
     *
     *
     * Types not present in this mapping will be
     * dealt damage to according to [.ATTACK_DAMAGE].
     */
    val CUSTOM_ATTACK_DAMAGE: Key<MapValue<EntityType<*>, Double>> = valueKeyOf(spongeKey("custom_attack_damage"))

    /**
     * The damage absorbed by an armor [ItemStack].
     * Readonly
     */
    val DAMAGE_ABSORPTION: Key<Value<Double>> = valueKeyOf(spongeKey("damage_absorption"))

    /**
     * How much damage a [FallingBlock] deals to [Living] entities
     * it hits per block fallen.
     *
     * This damage is capped by [MAX_FALL_DAMAGE].
     */
    val DAMAGE_PER_BLOCK: Key<Value<Double>> = valueKeyOf(spongeKey("damage_per_block"))

    /**
     * The distance at which a [BlockState] will decay.
     * This usually applies to leaves, for example [BlockTypes.OAK_LEAVES].
     */
    val DECAY_DISTANCE: Key<Value<Int>> = valueKeyOf(spongeKey("decay_distance"))

    /**
     * The modifier to [Keys.VELOCITY] of a [Minecart] while derailed.
     */
    val DERAILED_VELOCITY_MODIFIER: Key<Value<Vector3d>> = valueKeyOf(spongeKey("derailed_velocity_modifier"))

    /**
     * The detonator of a [PrimedTNT].
     */
    val DETONATOR: Key<Value<Living>> = valueKeyOf(spongeKey("detonator"))

    /**
     * The [Direction] a [BlockState], [Hanging], or [Shulker] is facing or the
     * heading of a [ShulkerBullet].
     */
    val DIRECTION: Key<Value<Direction>> = valueKeyOf(spongeKey("direction"))

    /**
     * The display name of an [Entity], [ItemStack] or [BlockEntity].
     *
     * On a [ItemTypes.WRITTEN_BOOK] item this will also set the title
     * of the book.
     */
    val DISPLAY_NAME: Key<Value<Text>> = valueKeyOf(spongeKey("display_name"))

    /**
     * The dominant [HandPreference] of an [Agent] entity.
     *
     * *NOTE:* For [Player]s is this key read-only, the
     * [HandPreference] of a player can not be changed server-side.
     */
    val DOMINANT_HAND: Key<Value<HandPreference>> = valueKeyOf(spongeKey("dominant_hand"))

    /**
     * The [DoorHinge] of a door [BlockState].
     */
    val DOOR_HINGE: Key<Value<DoorHinge>> = valueKeyOf(spongeKey("door_hinge"))

    /**
     * Whether exact teleport location should be used with a [EndGateway].
     */
    val DO_EXACT_TELEPORT: Key<Value<Boolean>> = valueKeyOf(spongeKey("do_exact_teleport"))

    /**
     * The remaining duration (in ticks) of an [AreaEffectCloud].
     */
    val DURATION: Key<Value<Int>> = valueKeyOf(spongeKey("duration"))

    /**
     * The amount of ticks the duration of an [AreaEffectCloud]
     * is increased or reduced when it applies its effect.
     */
    val DURATION_ON_USE: Key<Value<Int>> = valueKeyOf(spongeKey("duration_on_use"))

    /**
     * The color of a dyeable [BlockState], [ItemStack] or entity like [Cat]s.
     * or
     * The base [DyeColor] of a [Banner] or [TropicalFish].
     */
    val DYE_COLOR: Key<Value<DyeColor>> = valueKeyOf(spongeKey("dye_color"))

    /**
     * The time a [Panda] has been eating (in ticks)
     */
    val EATING_TIME: Key<Value<Int>> = valueKeyOf(spongeKey("eating_time"))

    /**
     * The efficiency of an [ItemStack] tool. Affects mining speed of supported materials. [.CAN_HARVEST]
     * Readonly
     */
    val EFFICIENCY: Key<Value<Double>> = valueKeyOf(spongeKey("efficiency"))

    /**
     * The time (in ticks) until a [Chicken] lays an [ItemTypes.EGG].
     *
     * Vanilla will calculate the egg timer by taking a random value between
     * 0 (inclusive) and 6000 (exclusive) and then add that by another 6000.
     * This unit ends up being in ticks. Once the chicken lays the egg, this
     * calculation is ran again.
     */
    val EGG_TIME: Key<Value<Int>> = valueKeyOf(spongeKey("egg_time"))

    /**
     * The age (in ticks) of an [EndGateway]
     */
    val END_GATEWAY_AGE: Key<Value<Long>> = valueKeyOf(spongeKey("end_gateway_age"))

    /**
     * The [EquipmentType] that the target inventory supports. This usually applies to [EquipmentSlot]s.
     * or
     * The [EquipmentType] of an [ItemStack]
     * Readonly
     */
    val EQUIPMENT_TYPE: Key<Value<EquipmentType>> = valueKeyOf(spongeKey("equipment_type"))

    /**
     * The current level of exhaustion of a [Humanoid].
     *
     *
     * When the exhaustion level reaches 0, saturation is usually diminished
     * such that saturation is decreased and then exhaustion is reset to the
     * maximum. This type of effect occurs over time and can be modified by
     * movements and actions performed by the [Humanoid].
     */
    val EXHAUSTION: Key<Value<Double>> = valueKeyOf(spongeKey("exhaustion"))

    /**
     * The amount of experience a [Player] has or an [ExperienceOrb] contains.
     */
    val EXPERIENCE: Key<Value<Int>> = valueKeyOf(spongeKey("experience"))

    /**
     * The total experience a [Player] requires to advance from his current level to the next one.
     * Readonly
     */
    val EXPERIENCE_FROM_START_OF_LEVEL: Key<Value<Int>> = valueKeyOf(spongeKey("experience_from_start_of_level"))

    /**
     * The current level a [Player] has.
     */
    val EXPERIENCE_LEVEL: Key<Value<Int>> = valueKeyOf(spongeKey("experience_level"))

    /**
     * The amount of experience a [Player] has collected towards the next level.
     */
    val EXPERIENCE_SINCE_LEVEL: Key<Value<Int>> = valueKeyOf(spongeKey("experience_since_level"))

    /**
     * The radius of the [Explosion] to be created by detonating an [Explosive].
     *
     *
     * May be absent if the explosion radius is unknown because it is either
     * determined randomly at the time of the explosion or computed from the
     * context in which the [Explosive] explodes.
     */
    val EXPLOSION_RADIUS: Key<Value<Int>> = valueKeyOf(spongeKey("explosion_radius"))

    /**
     * The eye height of an [Entity].
     * Readonly
     */
    val EYE_HEIGHT: Key<Value<Double>> = valueKeyOf(spongeKey("eye_height"))

    /**
     * The eye position of an [Entity].
     * Readonly
     */
    val EYE_POSITION: Key<Value<Vector3d>> = valueKeyOf(spongeKey("eye_position"))

    /**
     * The distance an [Entity] has fallen.
     */
    val FALL_DISTANCE: Key<Value<Double>> = valueKeyOf(spongeKey("fall_distance"))

    /**
     * The amount of ticks a [FallingBlock] has been falling for.
     */
    val FALL_TIME: Key<Value<Int>> = valueKeyOf(spongeKey("fall_time"))

    /**
     * The [FireworkEffect]s of a
     * [ItemTypes.FIREWORK_STAR], [ItemTypes.FIREWORK_ROCKET] [ItemStack] or a
     * [FireworkRocket].
     */
    val FIREWORK_EFFECTS: Key<ListValue<FireworkEffect>> = valueKeyOf(spongeKey("firework_effects"))

    /**
     * The flight duration of a [FireworkRocket]
     *
     *
     * The duration is tiered and will stay partially random. A rocket will
     * fly for roughly `modifier * 10 + (random number from 0 to 13)`
     * ticks in Vanilla Minecraft.
     */
    val FIREWORK_FLIGHT_MODIFIER: Key<Value<Int>> = valueKeyOf(spongeKey("firework_flight_modifier"))

    /**
     * The delay in ticks until the [Entity] will be damaged by the fire.
     */
    val FIRE_DAMAGE_DELAY: Key<Value<Int>> = valueKeyOf(spongeKey("fire_damage_delay"))

    /**
     * The amount of ticks an [Entity] is still burning.
     */
    val FIRE_TICKS: Key<Value<Int>> = valueKeyOf(spongeKey("fire_ticks"))

    /**
     * The time a [User] first joined on the Server.
     */
    val FIRST_DATE_JOINED: Key<Value<Instant>> = valueKeyOf(spongeKey("first_date_joined"))

    /**
     * A [fox&#39;s][Fox] first trusted [UUID], usually a [Player].
     */
    val FIRST_TRUSTED: Key<Value<UUID>> = valueKeyOf(spongeKey("first_trusted"))

    /**
     * The [FluidStackSnapshot] contained within an item container.
     * Item containers may include buckets and other mod added items.
     * See [.CONTAINER_ITEM]
     */
    val FLUID_ITEM_STACK: Key<Value<FluidStackSnapshot>> = valueKeyOf(spongeKey("fluid_item_stack"))

    /**
     * The fluid level of a liquid [BlockState].
     */
    val FLUID_LEVEL: Key<Value<Int>> = valueKeyOf(spongeKey("fluid_level"))

    /**
     * The directional tank information.
     * TODO dataholder? cauldron blockstate? modded?
     */
    val FLUID_TANK_CONTENTS: Key<MapValue<Direction, List<FluidStackSnapshot>>> = valueKeyOf(spongeKey("fluid_tank_contents"))

    /**
     * The speed at which an [Player] flies.
     */
    val FLYING_SPEED: Key<Value<Double>> = valueKeyOf(spongeKey("flying_speed"))

    /**
     * The type of a [Fox].
     */
    val FOX_TYPE: Key<Value<FoxType>> = valueKeyOf(spongeKey("fox_type"))

    /**
     * Represents the [Key] for the amount of fuel left in a [BrewingStand] or [FurnaceBlockEntity] or [FurnaceMinecart]
     *
     *
     * One [ItemTypes.BLAZE_POWDER] adds 20 fuel to the brewing stand.
     *
     * The fuel value corresponds with the number of batches of potions that can be brewed.
     *
     *
     * See [.BURN_TIME] for the burn time added by a fuel [ItemStack] to a furnace
     */
    val FUEL: Key<Value<Int>> = valueKeyOf(spongeKey("fuel"))

    /**
     * The time (in ticks) a [FusedExplosive]'s fuse will burn before the explosion.
     */
    val FUSE_DURATION: Key<Value<Int>> = valueKeyOf(spongeKey("fuse_duration"))

    /**
     * The [GameMode] a [Humanoid] has.
     */
    val GAME_MODE: Key<Value<GameMode>> = valueKeyOf(spongeKey("game_mode"))

    /**
     * The player represented by a
     * [BlockTypes.PLAYER_HEAD] (and [BlockTypes.PLAYER_WALL_HEAD]) [BlockState]
     * or a [ItemTypes.PLAYER_HEAD] [ItemStack].
     */
    val GAME_PROFILE: Key<Value<GameProfile>> = valueKeyOf(spongeKey("game_profile"))

    /**
     * The generation of a [ItemTypes.WRITTEN_BOOK] [ItemStack].
     * Depending on the book's generation it may be impossible to copy it.
     */
    val GENERATION: Key<Value<Int>> = valueKeyOf(spongeKey("generation"))

    /**
     * The "growth stage" state of a [BlockState].
     * e.g. [BlockTypes.CACTUS] or [BlockTypes.WHEAT] etc.
     */
    val GROWTH_STAGE: Key<Value<Int>> = valueKeyOf(spongeKey("growth_stage"))

    /**
     * The hardness of a [BlockState]s [BlockType].
     * Readonly
     */
    val HARDNESS: Key<Value<Double>> = valueKeyOf(spongeKey("hardness"))

    /**
     * Whether an [ArmorStand]'s arms are visible.
     */
    val HAS_ARMS: Key<Value<Boolean>> = valueKeyOf(spongeKey("has_arms"))

    /**
     * Whether an [ArmorStand] has a visible base plate.
     */
    val HAS_BASE_PLATE: Key<Value<Boolean>> = valueKeyOf(spongeKey("has_base_plate"))

    /**
     * Whether a [PackHorse] has a chest.
     */
    val HAS_CHEST: Key<Value<Boolean>> = valueKeyOf(spongeKey("has_chest"))

    /**
     * Whether a [Turtle] currently has an egg.
     */
    val HAS_EGG: Key<Value<Boolean>> = valueKeyOf(spongeKey("has_egg"))

    /**
     * Whether a [Dolphin] has a fish.
     *
     *
     * Dolphins will navigate to a treasure (if a structure that provides one is nearby)
     * if they have been given a fish.
     *
     */
    val HAS_FISH: Key<Value<Boolean>> = valueKeyOf(spongeKey("has_fish"))

    /**
     * Whether an [ArmorStand] is a "marker" stand.
     *
     *
     * If `true`, the armor stand's bounding box is near
     * impossible to see, and the armor stand can no longer be
     * interacted with.
     */
    val HAS_MARKER: Key<Value<Boolean>> = valueKeyOf(spongeKey("has_marker"))

    /**
     * Whether a giant mushroom [BlockState] has pores on the [Direction.DOWN] direction. See [.PORES].
     */
    val HAS_PORES_DOWN: Key<Value<Boolean>> = valueKeyOf(spongeKey("has_pores_down"))

    /**
     * Whether a giant mushroom [BlockState] has pores on the [Direction.EAST] direction. See [.PORES].
     */
    val HAS_PORES_EAST: Key<Value<Boolean>> = valueKeyOf(spongeKey("has_pores_east"))

    /**
     * Whether a giant mushroom [BlockState] has pores on the [Direction.NORTH] direction. See [.PORES].
     */
    val HAS_PORES_NORTH: Key<Value<Boolean>> = valueKeyOf(spongeKey("has_pores_north"))

    /**
     * Whether a giant mushroom [BlockState] has pores on the [Direction.SOUTH] direction. See [.PORES].
     */
    val HAS_PORES_SOUTH: Key<Value<Boolean>> = valueKeyOf(spongeKey("has_pores_south"))

    /**
     * Whether a giant mushroom [BlockState] has pores on the [Direction.UP] direction. See [.PORES].
     */
    val HAS_PORES_UP: Key<Value<Boolean>> = valueKeyOf(spongeKey("has_pores_up"))

    /**
     * Whether a giant mushroom [BlockState] has pores on the [Direction.WEST] direction. See [.PORES].
     */
    val HAS_PORES_WEST: Key<Value<Boolean>> = valueKeyOf(spongeKey("has_pores_west"))

    /**
     * Whether a server player has viewed the credits.
     *
     *
     * The credits are displayed the first time a player returns to the overworld safely using an end portal.
     */
    val HAS_VIEWED_CREDITS: Key<Value<Boolean>> = valueKeyOf(spongeKey("has_viewed_credits"))

    /**
     * The rotation of a [Living]'s or [ArmorStand]'s head.
     *
     *
     * The format of the rotation is represented by:
     *
     *  * `x -&gt; pitch</code></li><li> <code>y -&gt; yaw</code></li><li><code>z -&gt; roll
    ` *
     *
     *
     * Note that the pitch will be the same x value returned by
     * [Entity.getRotation] and Minecraft does not currently support
     * head roll so the z value will always be zero.
     */
    val HEAD_ROTATION: Key<Value<Vector3d>> = valueKeyOf(spongeKey("head_rotation"))

    /**
     * The [EnderCrystal] currently healing an [EnderDragon].
     */
    val HEALING_CRYSTAL: Key<Value<EnderCrystal>> = valueKeyOf(spongeKey("healing_crystal"))

    /**
     * A [Living]'s or [EnderCrystal]'s current health.
     *
     *
     * The range of the health depends on the object on which this
     * method is defined. For [Players][Player] in Minecraft, the nominal range is
     * between 0 and 20, inclusive, but the range can be adjusted.
     *
     *
     * Convention dictates that health does not fall below 0 but this
     * convention may be broken.
     */
    val HEALTH: Key<Value<Double>> = valueKeyOf(spongeKey("health"))

    /**
     * How much health a half-heart on a [Player]'s GUI will stand for.
     * TODO wrong javadocs @gabizou?
     */
    val HEALTH_SCALE: Key<Value<Double>> = valueKeyOf(spongeKey("health_scale"))

    /**
     * The height of the physical form of an [Entity].
     *
     *
     * Together with [.BASE_SIZE] and [.SCALE] this defines the size of an
     * [Entity].
     * Readonly
     */
    val HEIGHT: Key<Value<Double>> = valueKeyOf(spongeKey("height"))

    /**
     * The [ItemType] a [BlockState] represents.
     * Readonly
     */
    val HELD_ITEM: Key<Value<ItemType>> = valueKeyOf(spongeKey("held_item"))

    /**
     * The hidden [gene][PandaGene] of a [Panda].
     */
    val HIDDEN_GENE: Key<Value<PandaGene>> = valueKeyOf(spongeKey("hidden_gene"))

    /**
     * Whether the attributes of an [ItemStack] are hidden.
     */
    val HIDE_ATTRIBUTES: Key<Value<Boolean>> = valueKeyOf(spongeKey("hide_attributes"))

    /**
     * Whether the [.BREAKABLE_BLOCK_TYPES] of an [ItemStack] are hidden.
     */
    val HIDE_CAN_DESTROY: Key<Value<Boolean>> = valueKeyOf(spongeKey("hide_can_destroy"))

    /**
     * Whether the [.PLACEABLE_BLOCK_TYPES] of an [ItemStack] are hidden.
     */
    val HIDE_CAN_PLACE: Key<Value<Boolean>> = valueKeyOf(spongeKey("hide_can_place"))

    /**
     * Whether the [.APPLIED_ENCHANTMENTS] of an [ItemStack] are hidden.
     */
    val HIDE_ENCHANTMENTS: Key<Value<Boolean>> = valueKeyOf(spongeKey("hide_enchantments"))

    /**
     * Whether miscellaneous values of an [ItemStack] are hidden.
     * e.g. potion effects or shield pattern info
     */
    val HIDE_MISCELLANEOUS: Key<Value<Boolean>> = valueKeyOf(spongeKey("hide_miscellaneous"))

    /**
     * Whether [.IS_UNBREAKABLE] state of an [ItemStack] is hidden.
     */
    val HIDE_UNBREAKABLE: Key<Value<Boolean>> = valueKeyOf(spongeKey("hide_unbreakable"))

    /**
     * The [position][Vector3i] where a [Turtle] lays [eggs][BlockTypes.TURTLE_EGG].
     */
    val HOME_POSITION: Key<Value<Vector3i>> = valueKeyOf(spongeKey("home_position"))

    /**
     * The [HorseColor] of a [Horse].
     */
    val HORSE_COLOR: Key<Value<HorseColor>> = valueKeyOf(spongeKey("horse_color"))

    /**
     * The [HorseStyle] of a [Horse].
     */
    val HORSE_STYLE: Key<Value<HorseStyle>> = valueKeyOf(spongeKey("horse_style"))

    /**
     * Whether an [Item] will not despawn for an infinite time.
     */
    val INFINITE_DESPAWN_DELAY: Key<Value<Boolean>> = valueKeyOf(spongeKey("infinite_despawn_delay"))

    /**
     * Whether an [Item] has an infinite pickup delay.
     */
    val INFINITE_PICKUP_DELAY: Key<Value<Boolean>> = valueKeyOf(spongeKey("infinite_pickup_delay"))

    /**
     * The [InstrumentType] of a [BlockTypes.NOTE_BLOCK] [BlockState].
     */
    val INSTRUMENT_TYPE: Key<Value<InstrumentType>> = valueKeyOf(spongeKey("instrument_type"))

    /**
     * Whether a [BlockTypes.DAYLIGHT_DETECTOR] [BlockState] is inverted.
     */
    val INVERTED: Key<Value<Boolean>> = valueKeyOf(spongeKey("inverted"))

    /**
     * The amount of ticks an [Entity] will remain invulnerable for.
     */
    val INVULNERABILITY_TICKS: Key<Value<Int>> = valueKeyOf(spongeKey("invulnerability_ticks"))

    /**
     * Whether a fence gate [BlockState] is in a wall.
     */
    val IN_WALL: Key<Value<Boolean>> = valueKeyOf(spongeKey("in_wall"))

    /**
     * Whether an [Ageable] is considered an adult.
     */
    val IS_ADULT: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_adult"))

    /**
     * Whether a [Blaze] is currently burning.
     *
     *
     * Unlike [Keys.FIRE_TICKS], the burning effect will not damage
     * the burning entity.
     */
    val IS_AFLAME: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_aflame"))

    /**
     * Whether an [Agent]s AI is enabled.
     */
    val IS_AI_ENABLED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_ai_enabled"))

    /**
     * Whether an entity is currently aggressive.
     * e.g. [wolves][Wolf] or [ZombiePigman]
     */
    val IS_ANGRY: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_angry"))

    /**
     * Whether a [BlockState] is "attached" to another block.
     */
    val IS_ATTACHED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_attached"))

    /**
     * Whether an entity is begging for food.
     * e.g. [cats][Cat] or tamed [wolves][Wolf]
     */
    val IS_BEGGING_FOR_FOOD: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_begging_for_food"))

    /**
     * Whether [Raider]s are currently celebrating.
     */
    val IS_CELEBRATING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_celebrating"))

    /**
     * Whether a [Creeper] is charged.
     */
    val IS_CHARGED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_charged"))

    /**
     * Whether a [Pillager] is charging it's crossbow.
     */
    val IS_CHARGING_CROSSBOW: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_charging_crossbow"))

    /**
     * Whether a [Spider] is currently climbing.
     */
    val IS_CLIMBING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_climbing"))

    /**
     * Whether a [BlockState] is connected to the [Direction.EAST].
     * Also see [.CONNECTED_DIRECTIONS].
     */
    val IS_CONNECTED_EAST: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_connected_east"))

    /**
     * Whether a [BlockState] is connected to the [Direction.NORTH].
     * Also see [.CONNECTED_DIRECTIONS].
     */
    val IS_CONNECTED_NORTH: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_connected_north"))

    /**
     * Whether a [BlockState] is connected to the [Direction.SOUTH].
     * Also see [.CONNECTED_DIRECTIONS].
     */
    val IS_CONNECTED_SOUTH: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_connected_south"))

    /**
     * Whether a [BlockState] is connected to the [Direction.UP].
     * Also see [.CONNECTED_DIRECTIONS].
     */
    val IS_CONNECTED_UP: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_connected_up"))

    /**
     * Whether a [BlockState] is connected to the [Direction.WEST].
     * Also see [.CONNECTED_DIRECTIONS].
     */
    val IS_CONNECTED_WEST: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_connected_west"))

    /**
     * Whether an [Arrow] will cause a critical hit.
     */
    val IS_CRITICAL_HIT: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_critical_hit"))

    /**
     * Whether a [Fox] is currently crouching.
     */
    val IS_CROUCHING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_crouching"))

    /**
     * Whether a custom name is visible on an [Entity].
     */
    val IS_CUSTOM_NAME_VISIBLE: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_custom_name_visible"))

    /**
     * Whether a [Fox] is currently defending.
     */
    val IS_DEFENDING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_defending"))

    /**
     * Whether a [BlockState] is disarmed.
     * e.g. [BlockTypes.TRIPWIRE]s and [BlockTypes.TRIPWIRE_HOOK]s.
     */
    val IS_DISARMED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_disarmed"))

    /**
     * Whether an entity is eating.
     * e.g. [Panda]
     */
    val IS_EATING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_eating"))

    /**
     * Whether a [WeatherEffect] like [LightningBolt] is harmful to other [entities][Entity].
     * Readonly
     */
    val IS_EFFECT_ONLY: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_effect_only"))

    /**
     * Whether a [Player] is flying with an [ItemTypes.ELYTRA].
     */
    val IS_ELYTRA_FLYING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_elytra_flying"))

    /**
     * Whether a piston [BlockState] is currently extended.
     * TODO [Piston]?
     */
    val IS_EXTENDED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_extended"))

    /**
     * Whether a [Fox] is currently faceplanted.
     */
    val IS_FACEPLANTED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_faceplanted"))

    /**
     * Whether a [BlockState] is filled.
     *
     * e.g. [BlockTypes.END_PORTAL_FRAME]s.
     */
    val IS_FILLED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_filled"))

    /**
     * Whether a [BlockState] is flammable.
     * Readonly
     */
    val IS_FLAMMABLE: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_flammable"))

    /**
     * Whether an [Entity] is flying. TODO only player?
     *
     *
     * This key only tells whether an entity is flying at the moment. On a
     * [Player] it does not necessarily mean that the player may toggle
     * freely between flying and walking. To check whether a player may switch
     * his flying state, check [.CAN_FLY].
     */
    val IS_FLYING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_flying"))

    /**
     * Whether an entity is frightened.
     *
     *
     * In vanilla, [Panda]s that have a [Panda.knownGene]
     * of [PandaGenes.WORRIED] and are in a [world][ServerWorld] whose [Weather] is currently a
     * [Weathers.THUNDER] are considered "frightened".
     */
    val IS_FRIGHTENED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_frightened"))

    /**
     * Whether the block at the [ServerLocation] is a full block.
     */
    val IS_FULL_BLOCK: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_full_block"))

    /**
     * Whether an [Entity] has a glowing outline.
     */
    val IS_GLOWING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_glowing"))

    /**
     * Whether [Turtle] is proceeding to it's [home position][Vector3i].
     */
    val IS_GOING_HOME: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_going_home"))

    /**
     * Whether a [Cat] is hissing.
     */
    val IS_HISSING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_hissing"))

    /**
     * Whether a [Ravager] is immobilized.
     * Readonly
     */
    val IS_IMMOBILIZED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_immobilized"))

    /**
     * Whether a [ServerLocation] is indirectly powered.
     * Readonly
     */
    val IS_INDIRECTLY_POWERED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_indirectly_powered"))

    /**
     * Whether a [Fox] is currently interested in something.
     */
    val IS_INTERESTED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_interested"))

    /**
     * Whether an [Entity] is currently invisible.
     * This will only simply render the entity as vanished,
     * but not prevent any entity updates being sent to clients.
     * To fully "vanish" an [Entity], use [.VANISH].
     */
    val IS_INVISIBLE: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_invisible"))

    /**
     * Whether a [Boat] is currently in [BlockTypes.WATER].
     * Readonly
     */
    val IS_IN_WATER: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_in_water"))

    /**
     * Whether a [Vindicator] is exhibiting "johnny" behavior.
     *
     * @see [
     * The Minecraft Wiki for more information about "johnny" behavior](https://minecraft.gamepedia.com/Vindicator.Behavior)
     */
    val IS_JOHNNY: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_johnny"))

    /**
     * Whether a [Turtle] is currently digging to lay an egg.
     */
    val IS_LAYING_EGG: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_laying_egg"))

    /**
     * Whether a [Patroller] is the leader.
     */
    val IS_LEADER: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_leader"))

    /**
     * Whether a [BlockState] is lit.
     * e.g. [BlockTypes.FURNACE], [BlockTypes.CAMPFIRE]
     * or [BlockTypes.REDSTONE_TORCH].
     */
    val IS_LIT: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_lit"))

    /**
     * Whether a [Cat] is lying down.
     */
    val IS_LYING_DOWN: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_lying_down"))

    /**
     * Whether a [Panda] is lying on it's back.
     */
    val IS_LYING_ON_BACK: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_lying_on_back"))

    /**
     * Whether a bed [BlockState] is occupied.
     * e.g. [BlockTypes.WHITE_BED].
     */
    val IS_OCCUPIED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_occupied"))

    /**
     * Whether a [Minecart] is on it's rail
     * Readonly
     */
    val IS_ON_RAIL: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_on_rail"))

    /**
     * Whether a door/fencegate/trapdoor [BlockState] is open.
     */
    val IS_OPEN: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_open"))

    /**
     * Whether a [BlockState] is passable (can be walked through).
     * Readonly
     */
    val IS_PASSABLE: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_passable"))

    /**
     * Whether a [Patroller] is currently patrolling.
     */
    val IS_PATROLLING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_patrolling"))

    /**
     * Whether an [Entity] or leaves [BlockState] will
     * be prevented from despawning/decaying.
     *
     *
     * In Vanilla, entities may despawn if the player moves too far from
     * them. A persisting entity will not be removed due to no players being
     * near it.
     */
    val IS_PERSISTENT: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_persistent"))

    /**
     * Whether players are prevented from placing
     * items from an equipment slot on an [ArmorStand]
     */
    val IS_PLACING_DISABLED: Key<MapValue<EquipmentType, Boolean>> = valueKeyOf(spongeKey("is_placing_disabled"))

    /**
     * Whether a [IronGolem] has been created by a [Player].
     */
    val IS_PLAYER_CREATED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_player_created"))

    /**
     * Whether a [Fox] is currently pouncing.
     */
    val IS_POUNCING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_pouncing"))

    /**
     * Whether a [BlockState] is powered.
     *
     *
     * Applies to blocks that may be powered in order to emit a
     * Redstone signal of consistently maximum strength, such as
     * [BlockTypes.LEVER], [BlockTypes.OAK_BUTTON],
     * [BlockTypes.OAK_PRESSURE_PLATE], and their stone
     * counterparts.
     */
    val IS_POWERED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_powered"))

    /**
     * Whether a [FusedExplosive] is currently primed.
     * Readonly
     */
    val IS_PRIMED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_primed"))

    /**
     * Whether a [Cat] is purring.
     */
    val IS_PURRING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_purring"))

    /**
     * Whether a [Cat] is relaxed.
     */
    val IS_RELAXED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_relaxed"))

    /**
     * Whether a [BlockState] can be replaced by a player without breaking it first.
     * e.g. [BlockTypes.WATER]
     * Readonly
     */
    val IS_REPLACEABLE: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_replaceable"))

    /**
     * Whether a [Ravager] is roaring.
     * Readonly
     */
    val IS_ROARING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_roaring"))

    /**
     * Whether a [Panda] is rolling around.
     */
    val IS_ROLLING_AROUND: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_rolling_around"))

    /**
     * Whether an entity is saddled.
     * e.g. [Horse]s and [Pig]s
     */
    val IS_SADDLED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_saddled"))

    /**
     * Whether an [Enderman] is screaming.
     */
    val IS_SCREAMING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_screaming"))

    /**
     * Whether a [Sheep] is sheared.
     */
    val IS_SHEARED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_sheared"))

    /**
     * Whether an [Entity] is silent.
     *
     *
     * A silent entity will not emit sounds or make noises.
     */
    val IS_SILENT: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_silent"))

    /**
     * Whether a [Wolf], [Cat], [Panda], or [Fox] is sitting.
     */
    val IS_SITTING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_sitting"))

    /**
     * Whether a [Bat], [Fox] or [Player] is sleeping.
     *
     *
     * If a player is considered sleeping as per this data value, the player does
     * not need to be in bed in order for the other players to be able to
     * advance through the night by going to bed.
     * Readonly(Player.class)
     */
    val IS_SLEEPING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_sleeping"))

    /**
     * Whether a [Player&#39;s][Player] sleeping status is ignored when checking whether to
     * skip the night due to players sleeping. The time in a world will be
     * advanced to day if all players in it either are sleeping or are set to ignore.
     */
    val IS_SLEEPING_IGNORED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_sleeping_ignored"))

    /**
     * Whether an [ArmorStand] is small.
     */
    val IS_SMALL: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_small"))

    /**
     * Whether an [Entity] is sneaking.
     *
     *
     * Sneaking entities generally move slower and do not make walking
     * sounds.
     */
    val IS_SNEAKING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_sneaking"))

    /**
     * Whether a [Panda] is sneezing.
     */
    val IS_SNEEZING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_sneezing"))

    /**
     * Whether a [BlockTypes.DIRT] [BlockState] is snowy.
     */
    val IS_SNOWY: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_snowy"))

    /**
     * Whether a [BlockState] is solid.
     * Readonly
     */
    val IS_SOLID: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_solid"))

    /**
     * Whether an [Entity] is sprinting.
     */
    val IS_SPRINTING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_sprinting"))

    /**
     * Whether a [PolarBear] is currently standing.
     */
    val IS_STANDING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_standing"))

    /**
     * Whether a [Ravager] is stunned.
     * Readonly
     */
    val IS_STUNNED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_stunned"))

    /**
     * Whether a [BlockState] is a surrogate block for a block that was provided in an environment
     * (almost always modded), that the block type provider no longer exists.
     * If true this may indicate that the surrogate block functions differently than the original block.
     * Readonly
     */
    val IS_SURROGATE_BLOCK: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_surrogate_block"))

    /**
     * Whether players are prevented from taking
     * items from an equipment slot on an [ArmorStand]
     */
    val IS_TAKING_DISABLED: Key<MapValue<EquipmentType, Boolean>> = valueKeyOf(spongeKey("is_taking_disabled"))

    /**
     * Whether a [TameableAnimal] is currently tamed
     */
    val IS_TAMED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_tamed"))

    /**
     * Whether a [Trader] is currently trading with a [Player].
     * Readonly
     */
    val IS_TRADING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_trading"))

    /**
     * Whether a [Turtle] is currently traveling.
     */
    val IS_TRAVELING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_traveling"))

    /**
     * Whether an [Ocelot] is currently trusting of [Player]s.
     */
    val IS_TRUSTING: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_trusting"))

    /**
     * Whether an [ItemStack] or [BlockState] is unbreakable.
     *
     *
     * Setting this to `true` will prevent the item stack's
     * [.ITEM_DURABILITY] from changing.
     * Readonly(BlockState.class)
     */
    val IS_UNBREAKABLE: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_unbreakable"))

    /**
     * Whether a [Panda] is unhappy.
     */
    val IS_UNHAPPY: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_unhappy"))

    /**
     * Whehter a [BlockState] is waterlogged.
     */
    val IS_WATERLOGGED: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_waterlogged"))

    /**
     * Whether an [Entity] like [Wolf] is wet.
     * Readonly(Entity.class) except Wolf
     */
    val IS_WET: Key<Value<Boolean>> = valueKeyOf(spongeKey("is_wet"))

    /**
     * The durability of an [ItemStack]. [.MAX_DURABILITY]
     */
    val ITEM_DURABILITY: Key<Value<Int>> = valueKeyOf(spongeKey("item_durability"))

    /**
     * The [item][ItemStackSnapshot] in an
     * [Item], [ItemFrame], [Jukebox], [Lectern] or
     * [Potion].
     */
    val ITEM_STACK_SNAPSHOT: Key<Value<ItemStackSnapshot>> = valueKeyOf(spongeKey("item_stack_snapshot"))

    /**
     * The knockback strength applied by an [ArrowEntity].
     *
     *
     * For the knockback provided by hits with a weapon according to the
     * enchantment of the same name, see [.APPLIED_ENCHANTMENTS].
     */
    val KNOCKBACK_STRENGTH: Key<Value<Double>> = valueKeyOf(spongeKey("knockback_strength"))

    /**
     * The known [gene][PandaGene] of a [Panda].
     */
    val KNOWN_GENE: Key<Value<PandaGene>> = valueKeyOf(spongeKey("known_gene"))

    /**
     * The last attacking [Entity] of a [Living].
     */
    val LAST_ATTACKER: Key<Value<Entity>> = valueKeyOf(spongeKey("last_attacker"))

    /**
     * The output yielded by the last command of a [CommandBlock] or [CommandBlockMinecart].
     */
    val LAST_COMMAND_OUTPUT: Key<Value<Text>> = valueKeyOf(spongeKey("last_command_output"))

    /**
     * The last damage a [Living] received.
     */
    val LAST_DAMAGE_RECEIVED: Key<Value<Double>> = valueKeyOf(spongeKey("last_damage_received"))

    /**
     * The last time a [User] joined on the server.
     */
    val LAST_DATE_JOINED: Key<Value<Instant>> = valueKeyOf(spongeKey("last_date_joined"))

    /**
     * The last time a [User] has been playing on the server.
     */
    val LAST_DATE_PLAYED: Key<Value<Instant>> = valueKeyOf(spongeKey("last_date_played"))

    /**
     * The amount of layers a [BlockState] has.
     * e.g. [BlockTypes.SNOW], [BlockTypes.CAKE]
     */
    val LAYER: Key<Value<Int>> = valueKeyOf(spongeKey("layer"))

    /**
     * The holder of a leashed [Agent]
     * e.g. a [Player] or [LeashKnot].
     *
     * Usually, a [LeashKnot] will always exist so long as there is
     * a leashed [Entity] attached. If the leash is broken, the leash
     * hitch is removed.
     */
    val LEASH_HOLDER: Key<Value<Entity>> = valueKeyOf(spongeKey("leash_holder"))

    /**
     * The rotation of an [ArmorStand]'s left arm.
     */
    val LEFT_ARM_ROTATION: Key<Value<Vector3d>> = valueKeyOf(spongeKey("left_arm_rotation"))

    /**
     * The rotation of an [ArmorStand]'s left leg.
     */
    val LEFT_LEG_ROTATION: Key<Value<Vector3d>> = valueKeyOf(spongeKey("left_leg_rotation"))

    /**
     * The amount of ticks till a [Vex] starts
     * taking damage due to living too long.
     *
     *
     * When this value hits 0 or lower, the Vex will receive damage and
     * then the value will set back to 20 until the Vex dies.
     *
     *
     * If the Vex was summoned by a player, this value will be pegged at 0
     * and the Vex will not take any damage.
     */
    val LIFE_TICKS: Key<Value<Int>> = valueKeyOf(spongeKey("life_ticks"))

    /**
     * The amount of light that emitted by a [BlockState].
     * Readonly
     */
    val LIGHT_EMISSION: Key<Value<Int>> = valueKeyOf(spongeKey("light_emission"))

    /**
     * A [Llama]'s [LlamaType].
     */
    val LLAMA_TYPE: Key<Value<LlamaType>> = valueKeyOf(spongeKey("llama_type"))

    /**
     * The token used to lock a [CarrierBlockEntity]. Or the token on an [ItemStack] to unlock it.
     */
    val LOCK_TOKEN: Key<Value<String>> = valueKeyOf(spongeKey("lock_token"))

    /**
     * The displayed description ("lore") text of an [ItemStack].
     *
     *
     * The lore text is usually displayed when the player hovers his cursor
     * over the stack. For the contents of a book see [.PAGES]
     * instead.
     */
    val LORE: Key<ListValue<Text>> = valueKeyOf(spongeKey("lore"))

    /**
     * The matter state of a [BlockState]
     * Readonly
     */
    val MATTER_STATE: Key<Value<MatterState>> = valueKeyOf(spongeKey("matter_state"))

    /**
     * The maximum air supply a [Living] may have.
     *
     *
     * For the current amount of air, check [.REMAINING_AIR].
     */
    val MAX_AIR: Key<Value<Int>> = valueKeyOf(spongeKey("max_air"))

    /**
     * The maximum amount of ticks a [FurnaceBlockEntity]
     * can burn with the currently used fuel item.
     */
    val MAX_BURN_TIME: Key<Value<Int>> = valueKeyOf(spongeKey("max_burn_time"))

    /**
     * The total time the current [ItemStack] in a
     * [FurnaceBlockEntity] has to be cooked.
     */
    val MAX_COOK_TIME: Key<Value<Int>> = valueKeyOf(spongeKey("max_cook_time"))

    /**
     * The maximum durability of an [ItemStack]. [.ITEM_DURABILITY]
     * Readonly
     */
    val MAX_DURABILITY: Key<Value<Int>> = valueKeyOf(spongeKey("max_durability"))

    /**
     * The maximum damage a [FallingBlock] can deal.
     */
    val MAX_FALL_DAMAGE: Key<Value<Double>> = valueKeyOf(spongeKey("max_fall_damage"))

    /**
     * The maximum health of a [Living].
     *
     *
     * The maximum health set here may affect the attribute increasing
     * health points. The base health should be minded that it may be lower
     * than the total maximum health of the entity.
     */
    val MAX_HEALTH: Key<Value<Double>> = valueKeyOf(spongeKey("max_health"))

    /**
     * The maximum number of entities around a [MobSpawner].
     * A spawner will not spawn entities if there are more
     * entities around than this value permits.
     */
    val MAX_NEARBY_ENTITIES: Key<Value<Int>> = valueKeyOf(spongeKey("max_nearby_entities"))

    /**
     * The maximum amount of ticks between two
     * batches of entities spawned by a [MobSpawner].
     */
    val MAX_SPAWN_DELAY: Key<Value<Int>> = valueKeyOf(spongeKey("max_spawn_delay"))

    /**
     * The max speed of a [Boat]. In vanilla, this is 0.4
     */
    val MAX_SPEED: Key<Value<Double>> = valueKeyOf(spongeKey("max_speed"))

    /**
     * The maximum stack size of slots in an inventory. For most vanilla inventories this is 64.
     * Readonly
     */
    val MAX_STACK_SIZE: Key<Value<Int>> = valueKeyOf(spongeKey("max_stack_size"))

    /**
     * The represented block's offset of a [MinecartEntity].
     */
    val MINECART_BLOCK_OFFSET: Key<Value<Int>> = valueKeyOf(spongeKey("minecart_block_offset"))

    /**
     * The minimum amount of ticks between two
     * batches of entities spawned by a [MobSpawner].
     */
    val MIN_SPAWN_DELAY: Key<Value<Int>> = valueKeyOf(spongeKey("min_spawn_delay"))

    /**
     * The moisture value of a [BlockTypes.FARMLAND] [BlockState].
     */
    val MOISTURE: Key<Value<Int>> = valueKeyOf(spongeKey("moisture"))

    /**
     * The type of a [Mooshroom].
     */
    val MOOSHROOM_TYPE: Key<Value<MooshroomType>> = valueKeyOf(spongeKey("mooshroom_type"))

    /**
     * The type of [MusicDisc] an [ItemStack] holds.
     */
    val MUSIC_DISC: Key<Value<MusicDisc>> = valueKeyOf(spongeKey("music_disc"))

    /**
     * The next entity that will be spawned by a [MobSpawner].
     *
     *
     * Normally the entities to be spawned are determined by a random value
     * applied to the [.SPAWNABLE_ENTITIES] weighted collection. If this
     * value exists, it will override the random spawn with a definite one.
     */
    val NEXT_ENTITY_TO_SPAWN: Key<Value<WeightedSerializableObject<EntityArchetype>>> = valueKeyOf(spongeKey("next_entity_to_spawn"))

    /**
     * The pitch of a [BlockTypes.NOTE_BLOCK] [BlockState].
     */
    val NOTE_PITCH: Key<Value<NotePitch>> = valueKeyOf(spongeKey("note_pitch"))

    /**
     * The notifier, usually of an [Entity]. It is up to the implementation to define.
     */
    val NOTIFIER: Key<Value<UUID>> = valueKeyOf(spongeKey("notifier"))

    /**
     * The deceleration a [Boat] while it has [Keys.PASSENGERS].
     */
    val OCCUPIED_DECELERATION: Key<Value<Double>> = valueKeyOf(spongeKey("occupied_deceleration"))

    /**
     * Whether an [Entity] is currently considered to be on the ground.
     * Readonly
     */
    val ON_GROUND: Key<Value<Boolean>> = valueKeyOf(spongeKey("on_ground"))

    /**
     * The content of a [ItemTypes.WRITTEN_BOOK] [ItemStack].
     *
     * Use [Keys.PLAIN_PAGES] if you wish to inspect the contents
     * of a [ItemTypes.WRITABLE_BOOK].
     */
    val PAGES: Key<ListValue<Text>> = valueKeyOf(spongeKey("pages"))

    /**
     * The [type][ParrotType] of a [Parrot].
     */
    val PARROT_TYPE: Key<Value<ParrotType>> = valueKeyOf(spongeKey("parrot_type"))

    /**
     * The particle type of an [AreaEffectCloud].
     *
     *
     * Only a few [ParticleOption]s will be usable for this
     * effect for specific [ParticleType]s and not every
     * [ParticleType] will be applicable.
     */
    val PARTICLE_EFFECT: Key<Value<ParticleEffect>> = valueKeyOf(spongeKey("particle_effect"))

    /**
     * The amount of ticks a [FurnaceBlockEntity] has
     * been cooking the current item for.
     *
     *
     * Once this value reaches the [.MAX_COOK_TIME], the
     * item will be finished cooking.
     */
    val PASSED_COOK_TIME: Key<Value<Int>> = valueKeyOf(spongeKey("passed_cook_time"))

    /**
     * The entities that act as passengers for an [Entity].
     *
     *
     * For example, a [Player] riding on a [Horse] or a
     * [Pig] would be considered its passenger.
     */
    val PASSENGERS: Key<ListValue<Entity>> = valueKeyOf(spongeKey("passengers"))

    /**
     * A [TropicalFish]'s pattern color.
     */
    val PATTERN_COLOR: Key<Value<DyeColor>> = valueKeyOf(spongeKey("pattern_color"))

    /**
     * The [phase][PhantomPhase] of a [Phantom].
     */
    val PHANTOM_PHASE: Key<Value<PhantomPhase>> = valueKeyOf(spongeKey("phantom_phase"))

    /**
     * The [PickupRule] of an [ArrowEntity].
     */
    val PICKUP_RULE: Key<Value<PickupRule>> = valueKeyOf(spongeKey("pickup_rule"))

    /**
     * The piston type of a piston [BlockState] TODO dataholder [Piston].
     */
    val PISTON_TYPE: Key<Value<PistonType>> = valueKeyOf(spongeKey("piston_type"))

    /**
     * The block types an [ItemStack] may be placed on.
     */
    val PLACEABLE_BLOCK_TYPES: Key<SetValue<BlockType>> = valueKeyOf(spongeKey("placeable_block_types"))

    /**
     * The content of a [ItemTypes.WRITABLE_BOOK] [ItemStack].
     *
     *
     * Use [Keys.PAGES] if you wish to get the contents of a
     * [ItemTypes.WRITTEN_BOOK]
     */
    val PLAIN_PAGES: Key<ListValue<String>> = valueKeyOf(spongeKey("plain_pages"))

    /**
     * The plugin that created an [Inventory]
     */
    val PLUGIN_CONTAINER: Key<Value<PluginContainer>> = valueKeyOf(spongeKey("plugin_container"))

    /**
     * The pore sides of a [BlockTypes.BROWN_MUSHROOM_BLOCK] or
     * [BlockTypes.RED_MUSHROOM_BLOCK] [BlockState].
     * See [.HAS_PORES_UP], [.HAS_PORES_DOWN], [.HAS_PORES_NORTH], [.HAS_PORES_EAST], [.HAS_PORES_SOUTH], [.HAS_PORES_WEST].
     */
    val PORES: Key<SetValue<Direction>> = valueKeyOf(spongeKey("pores"))

    /**
     * The [PortionType] of a [BlockState].
     * e.g. [BlockTypes.OAK_DOOR], [BlockTypes.ROSE_BUSH] or [BlockTypes.WHITE_BED]
     * For slabs use [.SLAB_PORTION] instead
     */
    val PORTION_TYPE: Key<Value<PortionType>> = valueKeyOf(spongeKey("portion_type"))

    /**
     * The potion effects that are present on an [Entity]
     *
     * or applied by an [AreaEffectCloud] or [ArrowEntity]
     *
     * or stored on an [ItemStack].
     */
    val POTION_EFFECTS: Key<ListValue<PotionEffect>> = valueKeyOf(spongeKey("potion_effects"))

    /**
     * The potion type of an [ItemStack].
     */
    val POTION_TYPE: Key<Value<PotionType>> = valueKeyOf(spongeKey("potion_type"))

    /**
     * The signal power of a [BlockState].
     *
     *
     * Applies to blocks that may emit a Redstone signal of variable
     * strength, such as [BlockTypes.REDSTONE_WIRE],
     * [BlockTypes.DAYLIGHT_DETECTOR],
     * [BlockTypes.LIGHT_WEIGHTED_PRESSURE_PLATE] etc.
     */
    val POWER: Key<Value<Int>> = valueKeyOf(spongeKey("power"))

    /**
     * A [Beacon]'s primary effect.
     */
    val PRIMARY_POTION_EFFECT_TYPE: Key<Value<PotionEffectType>> = valueKeyOf(spongeKey("primary_potion_effect_type"))

    /**
     * The [Villager] or [ZombieVillager]'s [ProfessionType].
     */
    val PROFESSION_TYPE: Key<Value<ProfessionType>> = valueKeyOf(spongeKey("profession_type"))

    /**
     * The [Villager] or [ZombieVillager]'s [ProfessionType] level.
     */
    val PROFESSION_LEVEL: Key<Value<Int>> = valueKeyOf(spongeKey("profession_level"))

    /**
     * The type of a [Rabbit].
     */
    val RABBIT_TYPE: Key<Value<RabbitType>> = valueKeyOf(spongeKey("rabbit_type"))

    /**
     * The radius of an [AreaEffectCloud].
     */
    val RADIUS: Key<Value<Double>> = valueKeyOf(spongeKey("radius"))

    /**
     * The amount the radius of an
     * [AreaEffectCloud] grows or shrinks each time it applies its
     * effect.
     */
    val RADIUS_ON_USE: Key<Value<Double>> = valueKeyOf(spongeKey("radius_on_use"))

    /**
     * The amount the radius of an
     * [AreaEffectCloud] grows or shrinks per tick.
     */
    val RADIUS_PER_TICK: Key<Value<Double>> = valueKeyOf(spongeKey("radius_per_tick"))

    /**
     * The wave number of a raid a [Raider] is in.
     * Readonly but mutable
     */
    val RAID_WAVE: Key<Value<RaidWave>> = valueKeyOf(spongeKey("raid_wave"))

    /**
     * The [RailDirection] of a [BlockState].
     */
    val RAIL_DIRECTION: Key<Value<RailDirection>> = valueKeyOf(spongeKey("rail_direction"))

    /**
     * The delay (in ticks) after which an
     * [AreaEffectCloud] will reapply its effect on a previously
     * affected [Entity].
     */
    val REAPPLICATION_DELAY: Key<Value<Int>> = valueKeyOf(spongeKey("reapplication_delay"))

    /**
     * The redstone delay on a [BlockTypes.REPEATER] [BlockState].
     */
    val REDSTONE_DELAY: Key<Value<Int>> = valueKeyOf(spongeKey("redstone_delay"))

    /**
     * The amount of air a [Living] has left.
     */
    val REMAINING_AIR: Key<Value<Int>> = valueKeyOf(spongeKey("remaining_air"))

    /**
     * The remaining amount of ticks the current brewing
     * process of a [BrewingStand] will take.
     *
     *
     * If nothing is being brewed, the remaining brew time will be 0.
     */
    val REMAINING_BREW_TIME: Key<Value<Int>> = valueKeyOf(spongeKey("remaining_brew_time"))

    /**
     * Represents the [Key] for the remaining number of ticks to pass
     * before another attempt to spawn entities is made by a [MobSpawner].
     */
    val REMAINING_SPAWN_DELAY: Key<Value<Int>> = valueKeyOf(spongeKey("remaining_spawn_delay"))

    /**
     * The amount of saturation a food [ItemStack] provides when eaten.
     * Readonly
     */
    val REPLENISHED_SATURATION: Key<Value<Double>> = valueKeyOf(spongeKey("replenished_saturation"))

    /**
     * The [InstrumentType] of a [BlockState] when placed under a [BlockTypes.NOTE_BLOCK].
     * Readonly
     */
    val REPRESENTED_INSTRUMENT: Key<Value<InstrumentType>> = valueKeyOf(spongeKey("represented_instrument"))

    /**
     * How close a [Player] has to be around the [MobSpawner]
     * in order for it to attempt to spawn entities.
     */
    val REQUIRED_PLAYER_RANGE: Key<Value<Double>> = valueKeyOf(spongeKey("required_player_range"))

    /**
     * The spawn locations a [Player]
     * may have for various worlds based on [UUID] of the world.
     */
    val RESPAWN_LOCATIONS: Key<MapValue<ResourceKey, RespawnLocation>> = valueKeyOf(spongeKey("respawn_locations"))

    /**
     * The rotation of an [ArmorStand]'s right arm.
     */
    val RIGHT_ARM_ROTATION: Key<Value<Vector3d>> = valueKeyOf(spongeKey("right_arm_rotation"))

    /**
     * The rotation of an [ArmorStand]'s right leg.
     */
    val RIGHT_LEG_ROTATION: Key<Value<Vector3d>> = valueKeyOf(spongeKey("right_leg_rotation"))

    /**
     * The time a [Ravager] is roaring.
     */
    val ROARING_TIME: Key<Value<Int>> = valueKeyOf(spongeKey("attack_time"))

    /**
     * The [Rotation] of an [ItemFrame].
     */
    val ROTATION: Key<Value<Rotation>> = valueKeyOf(spongeKey("rotation"))

    /**
     * The current saturation of a [Player].
     *
     *
     * When the saturation level reaches 0, the food level is usually
     * diminished such that the food level is decreased by 1, then
     * saturation is reset to the maximum value. This type of effect occurs
     * over time and can be modified by movements and actions performed by the
     * [Player].
     */
    val SATURATION: Key<Value<Double>> = valueKeyOf(spongeKey("saturation"))

    /**
     * The "scale" for the size of an [Entity].
     *
     *
     * Together with [.BASE_SIZE] and [.HEIGHT] this defines the size of an [Entity].
     */
    val SCALE: Key<Value<Double>> = valueKeyOf(spongeKey("scale"))

    /**
     * The scoreboard tags applied to an [Entity].
     *
     * @see [
     * https://minecraft.gamepedia.com/Scoreboard.Tags](https://minecraft.gamepedia.com/Scoreboard.Tags)
     */
    val SCOREBOARD_TAGS: Key<SetValue<String>> = valueKeyOf(spongeKey("scoreboard_tags"))

    /**
     * A [Beacon]'s secondary effect.
     */
    val SECONDARY_POTION_EFFECT_TYPE: Key<Value<PotionEffectType>> = valueKeyOf(spongeKey("secondary_potion_effect_type"))

    /**
     * A [fox&#39;s][Fox] second trusted [UUID], usually a [Player].
     */
    val SECOND_TRUSTED: Key<Value<UUID>> = valueKeyOf(spongeKey("second_trusted"))

    /**
     * The shooter of a [Projectile].
     */
    val SHOOTER: Key<Value<ProjectileSource>> = valueKeyOf(spongeKey("shooter"))

    /**
     * Whether a [EnderCrystal] should show it's bottom bedrock platform.
     */
    val SHOW_BOTTOM: Key<Value<Boolean>> = valueKeyOf(spongeKey("show_bottom"))

    /**
     * The lines displayed on a [Sign].
     */
    val SIGN_LINES: Key<ListValue<Text>> = valueKeyOf(spongeKey("sign_lines"))

    /**
     * The size of a [Slime].
     * or
     * The size of a [Phantom]. In vanilla, this ranges between 0 and 64.
     */
    val SIZE: Key<Value<Int>> = valueKeyOf(spongeKey("size"))

    /**
     * The skin of a [Humanoid].
     *
     * Skins can only be manipulated by supplying the UUID of a player
     * having that skin. The binary skin data is signed by Mojang so fully
     * customized skins are not possible.
     * Readonly (Player)
     */
    val SKIN_PROFILE_PROPERTY: Key<Value<ProfileProperty>> = valueKeyOf(spongeKey("skin_profile_property"))

    /**
     * The "moisture" state of a [Dolphin].
     *
     * Vanilla sets the dolphin's skin moisture to 2400 so long as the entity
     * is in water, being rained on, or in a bubble column. If not, the dolphin
     * will loose 1 moisture per tick. Once this value is 0 or below, the dolphin
     * will be damaged via [DamageSources.DRYOUT] with a value of 1 per tick
     * until death.
     */
    val SKIN_MOISTURE: Key<Value<Int>> = valueKeyOf(spongeKey("skin_moisture"))

    /**
     * The skylight value at a [ServerLocation].
     * For the blocklight see [.BLOCK_LIGHT].
     * Readonly
     */
    val SKY_LIGHT: Key<Value<Int>> = valueKeyOf(spongeKey("sky_light"))

    /**
     * The [SlabPortion] of a [BlockState].
     */
    val SLAB_PORTION: Key<Value<SlabPortion>> = valueKeyOf(spongeKey("slab_portion"))

    /**
     * The sleep timer of a [Player].
     */
    val SLEEP_TIMER: Key<Value<Int>> = valueKeyOf(spongeKey("sleep_timer"))

    /**
     * The index of a [Slot] in an [Inventory]
     * Readonly
     */
    val SLOT_INDEX: Key<Value<Int>> = valueKeyOf(spongeKey("slot_index"))

    /**
     * The position of a [Slot] within a [GridInventory].
     * Readonly
     */
    val SLOT_POSITION: Key<Value<Vector2i>> = valueKeyOf(spongeKey("slot_position"))

    /**
     * The side of a particular [Slot], for use in querying "sided inventories".
     * Readonly
     */
    val SLOT_SIDE: Key<Value<Direction>> = valueKeyOf(spongeKey("slot_side"))

    /**
     * Whether a [Minecart] slows down when it has no [Keys.PASSENGERS].
     */
    val SLOWS_UNOCCUPIED: Key<Value<Boolean>> = valueKeyOf(spongeKey("slows_unoccupied"))

    /**
     * The time a [Panda] has been sneezing (in ticks)
     */
    val SNEEZING_TIME: Key<Value<Int>> = valueKeyOf(spongeKey("sneezing_time"))

    /**
     * The list of [EntityArchetype]s able to be spawned by a [MobSpawner].
     */
    val SPAWNABLE_ENTITIES: Key<WeightedCollectionValue<EntityArchetype>> = valueKeyOf(spongeKey("spawnable_entities"))

    /**
     * How many entities a [MobSpawner] has spawned so far.
     */
    val SPAWN_COUNT: Key<Value<Int>> = valueKeyOf(spongeKey("spawn_count"))

    /**
     * How far away from the [MobSpawner] the entities spawned by it may appear.
     */
    val SPAWN_RANGE: Key<Value<Double>> = valueKeyOf(spongeKey("spawn_range"))

    /**
     * The [target][Entity] of the spectator camera of a [Player].
     */
    val SPECTATOR_TARGET: Key<Value<Entity>> = valueKeyOf(spongeKey("spectator_target"))

    /**
     * The [StairShape] of a [BlockState].
     */
    val STAIR_SHAPE: Key<Value<StairShape>> = valueKeyOf(spongeKey("stair_shape"))

    /**
     * The [Statistic]s of a [Player].
     */
    val STATISTICS: Key<MapValue<Statistic, Long>> = valueKeyOf(spongeKey("statistics"))

    /**
     * The enchantments stored on an [ItemStack].
     *
     *
     * Stored enchantments are meant to be transferred. Usually this key
     * applies to [ItemTypes.ENCHANTED_BOOK] [ItemStack]s. Enchantments
     * affecting the item stack are retrieved via [.APPLIED_ENCHANTMENTS]
     * instead.
     */
    val STORED_ENCHANTMENTS: Key<ListValue<Enchantment>> = valueKeyOf(spongeKey("stored_enchantments"))

    /**
     * A [Llama]s carrying strength. The higher the strength,
     * the more items it can carry (effectively the size of inventory).
     */
    val STRENGTH: Key<Value<Int>> = valueKeyOf(spongeKey("strength"))

    /**
     * The author of a structure from a [StructureBlock].
     */
    val STRUCTURE_AUTHOR: Key<Value<String>> = valueKeyOf(spongeKey("structure_author"))

    /**
     * Whether a [StructureBlock] should
     * ignore entities when saving a structure.
     */
    val STRUCTURE_IGNORE_ENTITIES: Key<Value<Boolean>> = valueKeyOf(spongeKey("structure_ignore_entities"))

    /**
     * The integrity of a [StructureBlock].
     */
    val STRUCTURE_INTEGRITY: Key<Value<Double>> = valueKeyOf(spongeKey("structure_integrity"))

    /**
     * The mode of a [StructureBlock].
     */
    val STRUCTURE_MODE: Key<Value<StructureMode>> = valueKeyOf(spongeKey("structure_mode"))

    /**
     * The position of a [StructureBlock].
     */
    val STRUCTURE_POSITION: Key<Value<Vector3i>> = valueKeyOf(spongeKey("structure_position"))

    /**
     * Whether a [StructureBlock] is powered.
     */
    val STRUCTURE_POWERED: Key<Value<Boolean>> = valueKeyOf(spongeKey("structure_powered"))

    /**
     * The seed of a [StructureBlock]
     */
    val STRUCTURE_SEED: Key<Value<Long>> = valueKeyOf(spongeKey("structure_seed"))

    /**
     * Whether a
     * [StructureBlock] should make all [BlockTypes.AIR],
     * [BlockTypes.CAVE_AIR], [BlockTypes.STRUCTURE_VOID] visible.
     */
    val STRUCTURE_SHOW_AIR: Key<Value<Boolean>> = valueKeyOf(spongeKey("structure_show_air"))

    /**
     * Whether a [StructureBlock] shows the bounding box.
     */
    val STRUCTURE_SHOW_BOUNDING_BOX: Key<Value<Boolean>> = valueKeyOf(spongeKey("structure_show_bounding_box"))

    /**
     * The size of a [StructureBlock]s structure.
     */
    val STRUCTURE_SIZE: Key<Value<Vector3i>> = valueKeyOf(spongeKey("structure_size"))

    /**
     * The amount of "stuck arrows" in a [Living].
     */
    val STUCK_ARROWS: Key<Value<Int>> = valueKeyOf(spongeKey("stuck_arrows"))

    /**
     * The time (in ticks) a [Ravager] is stunned.
     */
    val STUNNED_TIME: Key<Value<Int>> = valueKeyOf(spongeKey("stunned_time"))

    /**
     * The amount of successful executions of a command
     * stored in a [CommandBlock] or [CommandBlockMinecart].
     */
    val SUCCESS_COUNT: Key<Value<Int>> = valueKeyOf(spongeKey("success_count"))

    /**
     * Whether a [BlockState] is suspended.
     */
    val SUSPENDED: Key<Value<Boolean>> = valueKeyOf(spongeKey("suspended"))

    /**
     * The swiftness of an [Entity] e.g. [Minecart]s.
     *
     * This is equivalent to the magnitude of the [.VELOCITY] vector
     */
    val SWIFTNESS: Key<Value<Double>> = valueKeyOf(spongeKey("swiftness"))

    /**
     * The tamer of a [TameableAnimal] or [HorseEntity].
     */
    val TAMER: Key<Value<UUID>> = valueKeyOf(spongeKey("tamer"))

    /**
     * The targeted entity either by an [Agent] and it's
     * [GoalExecutorTypes.TARGET] selector or by a [FishingBobber] or [ShulkerBullet].
     */
    val TARGET_ENTITY: Key<Value<Entity>> = valueKeyOf(spongeKey("target_entity"))

    /**
     * A target location.
     * e.g. An [EyeOfEnder] target or a [Player]'s compass.
     */
    val TARGET_LOCATION: Key<Value<Vector3d>> = valueKeyOf(spongeKey("target_location"))

    /**
     * A target block position.
     * e.g. A [Patroller]'s patrol target,
     * the travel position of a [Turtle],
     * the exit portal position of a [EndGateway] or
     * an [EnderCrystal]'s beam target.
     */
    val TARGET_POSITION: Key<Value<Vector3i>> = valueKeyOf(spongeKey("target_position"))

    /**
     * The remaining fuse time in ticks of a [FusedExplosive].
     * This value may be set to an arbitrary value
     * if the explosive is not primed.
     */
    val TICKS_REMAINING: Key<Value<Int>> = valueKeyOf(spongeKey("ticks_remaining"))

    /**
     * The [ToolType] of an [ItemStack] tool.
     * Readonly
     */
    val TOOL_TYPE: Key<Value<ToolType>> = valueKeyOf(spongeKey("tool_type"))

    /**
     * Whether a [CommandBlock] does track its output.
     *
     *
     * If this is set, the output of the most recent execution can be
     * retrieved using [.LAST_COMMAND_OUTPUT].
     */
    val TRACKS_OUTPUT: Key<Value<Boolean>> = valueKeyOf(spongeKey("tracks_output"))

    /**
     * Tge [TradeOffer]s offered by a [Trader].
     */
    val TRADE_OFFERS: Key<ListValue<TradeOffer>> = valueKeyOf(spongeKey("trade_offers"))

    /**
     * Whether an [Entity] is transient.
     * This prevents the entity from being saved to disk.
     * The rules for this are as follows...
     * If the entity type says that it isn't transient then this key is readonly.
     * If the entity type says that it is transient, then this key dictates the current state.
     */
    val TRANSIENT: Key<Value<Boolean>> = valueKeyOf(spongeKey("transient"))

    /**
     * A [TropicalFish]'s shape.
     */
    val TROPICAL_FISH_SHAPE: Key<Value<TropicalFishShape>> = valueKeyOf(spongeKey("tropical_fish_shape"))

    /**
     * The time a [Panda] has been unhappy (in ticks)
     */
    val UNHAPPY_TIME: Key<Value<Int>> = valueKeyOf(spongeKey("unhappy_time"))

    /**
     * The [UUID] of a custom inventory.
     */
    val UNIQUE_ID: Key<Value<UUID>> = valueKeyOf(spongeKey("unique_id"))

    /**
     * The deceleration a [Boat] while it does not have [Keys.PASSENGERS].
     */
    val UNOCCUPIED_DECELERATION: Key<Value<Double>> = valueKeyOf(spongeKey("unoccupied_deceleration"))

    /**
     * Whether a [BlockTypes.TNT] [BlockState] is unstable.
     */
    val UNSTABLE: Key<Value<Boolean>> = valueKeyOf(spongeKey("unstable"))

    /**
     * Whether changes to [Keys.SKIN_PROFILE_PROPERTY] should
     * be reflected in an entitie's [GameProfile].
     */
    val UPDATE_GAME_PROFILE: Key<Value<Boolean>> = valueKeyOf(spongeKey("update_game_profile"))

    /**
     * Whether an [Entity] is vanished.
     *
     *
     * The presence of a vanished entity will not be made known to a client;
     * no packets pertaining to this entity are sent. Client-side, this entity
     * will cease to exist. Server-side it may still be targeted by hostile
     * entities or collide with other entities.
     *
     *
     * Vanishing an [Entity] ridden by other entities (see
     * [.PASSENGERS] will cause problems.
     *
     *
     */
    val VANISH: Key<Value<Boolean>> = valueKeyOf(spongeKey("vanish"))

    /**
     * Whether an [Entity] ignores collision with other entities.
     *
     *
     * This state will be ignored if the [Entity] is not also
     * vanished as per [.VANISH].
     */
    val VANISH_IGNORES_COLLISION: Key<Value<Boolean>> = valueKeyOf(spongeKey("vanish_ignores_collision"))

    /**
     * Whether an [Entity] can be targeted for attack by another entity.
     * This prevents neither [Player]s from attacking the entity nor
     * will it be protected from untargeted damage like fire or explosions.
     *
     *
     * This state will be ignored if the [Entity] is not also
     * vanished as per [.VANISH].}.
     */
    val VANISH_PREVENTS_TARGETING: Key<Value<Boolean>> = valueKeyOf(spongeKey("vanish_prevents_targeting"))

    /**
     * The vehicle an [Entity] is riding.
     *
     *
     * Vehicles may be nested as a vehicle might itself ride another entity.
     * To get the vehicle on bottom, use [Keys.BASE_VEHICLE].
     */
    val VEHICLE: Key<Value<Entity>> = valueKeyOf(spongeKey("vehicle"))

    /**
     * The type of a [Villager] or [ZombieVillager].
     */
    val VILLAGER_TYPE: Key<Value<VillagerType>> = valueKeyOf(spongeKey("villager_type"))

    /**
     * The duration in ticks after which an
     * [AreaEffectCloud] will begin to apply its effect to entities.
     */
    val WAIT_TIME: Key<Value<Int>> = valueKeyOf(spongeKey("wait_time"))

    /**
     * The base speed at which a [Player] or [Living] walks.
     */
    val WALKING_SPEED: Key<Value<Double>> = valueKeyOf(spongeKey("walking_speed"))

    /**
     * Whether a thrown [EyeOfEnder] will shatter.
     */
    val WILL_SHATTER: Key<Value<Boolean>> = valueKeyOf(spongeKey("will_shatter"))

    /**
     * The [WireAttachmentType]s of a [BlockTypes.REDSTONE_WIRE] [BlockState] for its neighboring blocks.
     */
    val WIRE_ATTACHMENTS: Key<MapValue<Direction, WireAttachmentType>> = valueKeyOf(spongeKey("wire_attachments"))

    /**
     * The [WireAttachmentType] of a [BlockTypes.REDSTONE_WIRE] [BlockState]
     * for its neighboring block to the [Direction.EAST].
     */
    val WIRE_ATTACHMENT_EAST: Key<Value<WireAttachmentType>> = valueKeyOf(spongeKey("wire_attachment_east"))

    /**
     * The [WireAttachmentType] of a [BlockTypes.REDSTONE_WIRE] [BlockState]
     * for its neighboring block to the [Direction.NORTH].
     */
    val WIRE_ATTACHMENT_NORTH: Key<Value<WireAttachmentType>> = valueKeyOf(spongeKey("wire_attachment_north"))

    /**
     * The [WireAttachmentType] of a [BlockTypes.REDSTONE_WIRE] [BlockState]
     * for its neighboring block to the [Direction.SOUTH].
     */
    val WIRE_ATTACHMENT_SOUTH: Key<Value<WireAttachmentType>> = valueKeyOf(spongeKey("wire_attachment_south"))

    /**
     * The [WireAttachmentType] of a [BlockTypes.REDSTONE_WIRE] [BlockState]
     * for its neighboring block to the [Direction.WEST].
     */
    val WIRE_ATTACHMENT_WEST: Key<Value<WireAttachmentType>> = valueKeyOf(spongeKey("wire_attachment_west"))

    /**
     * The entities targeted by the three [Wither] heads. In vanilla the wither only targets [Living]. `null` for no target entity.
     */
    val WITHER_TARGETS: Key<ListValue<Entity>> = valueKeyOf(spongeKey("wither_targets"))

    /**
     * The [Sheep] who is being targeted by the [SpellTypes.WOLOLO]
     * spell being casted by an [Evoker]
     */
    val WOLOLO_TARGET: Key<Value<Sheep>> = valueKeyOf(spongeKey("wololo_target"))

    /**
     * The [WoodType] of a [Boat].
     */
    val WOOD_TYPE: Key<Value<WoodType>> = valueKeyOf(spongeKey("wood_type"))

    // endregion
}
