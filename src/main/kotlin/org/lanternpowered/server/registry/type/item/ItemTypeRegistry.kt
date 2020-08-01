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
package org.lanternpowered.server.registry.type.item

import org.lanternpowered.api.block.BlockTypes
import org.lanternpowered.api.effect.potion.PotionEffectTypes
import org.lanternpowered.api.effect.potion.potionEffectOf
import org.lanternpowered.api.entity.EntityType
import org.lanternpowered.api.entity.EntityTypes
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.ItemTypes
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.itemStackOf
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.data.key.registerApplicablePotionEffects
import org.lanternpowered.server.data.key.registerUseDuration
import org.lanternpowered.server.data.type.LanternDyeColor
import org.lanternpowered.server.effect.potion.LanternPotionType
import org.lanternpowered.server.item.ItemKeys
import org.lanternpowered.server.item.ItemTypeBuilder
import org.lanternpowered.server.item.behavior.vanilla.ArmorQuickEquipInteractionBehavior
import org.lanternpowered.server.item.behavior.vanilla.ConsumableInteractionBehavior
import org.lanternpowered.server.item.behavior.vanilla.OpenHeldBookBehavior
import org.lanternpowered.server.item.behavior.vanilla.ShieldInteractionBehavior
import org.lanternpowered.server.item.behavior.vanilla.WallOrStandingPlacementBehavior
import org.lanternpowered.server.item.behavior.vanilla.consumable.MilkConsumer
import org.lanternpowered.server.item.behavior.vanilla.consumable.PotionEffectsProvider
import org.lanternpowered.server.item.itemTypeOf
import org.lanternpowered.server.item.property.BowProjectile
import org.lanternpowered.server.registry.type.block.BlockTypeRegistry
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.type.ArmorMaterial
import org.spongepowered.api.data.type.ArmorMaterials
import org.spongepowered.api.data.type.DyeColor
import org.spongepowered.api.data.type.DyeColors
import org.spongepowered.api.data.type.ToolType
import org.spongepowered.api.data.type.ToolTypes
import org.spongepowered.api.data.type.WoodType
import org.spongepowered.api.data.type.WoodTypes
import org.spongepowered.api.effect.sound.music.MusicDisc
import org.spongepowered.api.effect.sound.music.MusicDiscs
import org.spongepowered.api.entity.projectile.Projectile
import org.spongepowered.api.entity.vehicle.minecart.MinecartEntity
import org.spongepowered.api.fluid.FluidStackSnapshot
import org.spongepowered.api.fluid.FluidType
import org.spongepowered.api.fluid.FluidTypes
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes
import java.util.Collections
import java.util.function.Supplier
import kotlin.random.Random

val ItemTypeRegistry = catalogTypeRegistry<ItemType> {
    fun register(key: NamespacedKey, fn: ItemTypeBuilder.() -> Unit = {}) =
            register(itemTypeOf(key, fn))

    fun ItemTypeBuilder.durable(maxDurability: Int) {
        maxStackQuantity(1)
        keys {
            register(Keys.MAX_DURABILITY, maxDurability)
        }
        stackKeys {
            register(Keys.ITEM_DURABILITY, 0)
            register(Keys.IS_UNBREAKABLE, true) // TODO: True until durability is implemented
        }
    }

    fun ItemTypeBuilder.tool(useLimit: Int, toolType: ToolType) {
        durable(useLimit)
        keys {
            register(Keys.TOOL_TYPE, toolType)
            register(LanternKeys.IS_DUAL_WIELDABLE, true)
        }
    }

    fun ItemTypeBuilder.tool(useLimit: Int, toolType: Supplier<out ToolType>) {
        tool(useLimit, toolType.get())
    }

    fun ItemTypeBuilder.woodenTool(useLimit: Int = 60) {
        tool(useLimit, ToolTypes.WOOD)
    }

    fun ItemTypeBuilder.stoneTool(useLimit: Int = 132) {
        tool(useLimit, ToolTypes.STONE)
    }

    fun ItemTypeBuilder.ironTool(useLimit: Int = 251) {
        tool(useLimit, ToolTypes.IRON)
    }

    fun ItemTypeBuilder.goldenTool(useLimit: Int = 33) {
        tool(useLimit, ToolTypes.GOLD)
    }

    fun ItemTypeBuilder.diamondTool(useLimit: Int = 1562) {
        tool(useLimit, ToolTypes.DIAMOND)
    }

    fun ItemTypeBuilder.armor(
            useLimit: Int,
            armorMaterial: Supplier<out ArmorMaterial>,
            equipmentType: Supplier<out EquipmentType>) {
        durable(useLimit)
        keys {
            register(Keys.ARMOR_MATERIAL, armorMaterial)
            register(Keys.EQUIPMENT_TYPE, equipmentType)
        }
        behaviors {
            add(ArmorQuickEquipInteractionBehavior())
        }
    }

    fun ItemTypeBuilder.leatherArmor(useLimit: Int, equipmentType: Supplier<out EquipmentType>) {
        armor(useLimit, ArmorMaterials.LEATHER, equipmentType)
        stackKeys {
            register(Keys.COLOR)
        }
    }

    fun ItemTypeBuilder.ironArmor(useLimit: Int, equipmentType: Supplier<out EquipmentType>) {
        armor(useLimit, ArmorMaterials.IRON, equipmentType)
    }

    fun ItemTypeBuilder.chainmailArmor(useLimit: Int, equipmentType: Supplier<out EquipmentType>) {
        armor(useLimit, ArmorMaterials.CHAINMAIL, equipmentType)
    }

    fun ItemTypeBuilder.goldenArmor(useLimit: Int, equipmentType: Supplier<out EquipmentType>) {
        armor(useLimit, ArmorMaterials.GOLD, equipmentType)
    }

    fun ItemTypeBuilder.diamondArmor(useLimit: Int, equipmentType: Supplier<out EquipmentType>) {
        armor(useLimit, ArmorMaterials.DIAMOND, equipmentType)
    }

    fun ItemTypeBuilder.food(food: Int, saturation: Double,
                                     consumeDuration: Int = 32,
                                     consumeBehavior: ConsumableInteractionBehavior.() -> Unit = {}) {
        keys {
            registerUseDuration(consumeDuration)
            register(Keys.REPLENISHED_FOOD, food)
            register(Keys.REPLENISHED_SATURATION, saturation)
        }
        behaviors {
            add(ConsumableInteractionBehavior().apply(consumeBehavior))
        }
    }

    fun ItemTypeBuilder.fluidBucket(fluidType: FluidType) {
        maxStackQuantity(1)
        stackKeys {
            register(Keys.FLUID_ITEM_STACK, FluidStackSnapshot.builder()
                    .fluid(fluidType)
                    .volume(1000)
                    .build())
        }
    }

    fun ItemTypeBuilder.fluidBucket(fluidType: Supplier<out FluidType>) =
            fluidBucket(fluidType.get())

    fun ItemTypeBuilder.dyeColor(dyeColor: DyeColor) {
        keys {
            register(ItemKeys.DYE_COLOR, dyeColor)
        }
    }

    fun registerMinecarts() {
        fun registerMinecart(key: NamespacedKey, entityType: Supplier<out EntityType<out MinecartEntity>>) {
            register(key) {
                maxStackQuantity(1)
            }
        }

        registerMinecart(minecraftKey("minecart"), EntityTypes.MINECART)
        registerMinecart(minecraftKey("chest_minecart"), EntityTypes.CHEST_MINECART)
        registerMinecart(minecraftKey("furnace_minecart"), EntityTypes.FURNACE_MINECART)
        registerMinecart(minecraftKey("tnt_minecart"), EntityTypes.TNT_MINECART)
        registerMinecart(minecraftKey("hopper_minecart"), EntityTypes.HOPPER_MINECART)
        registerMinecart(minecraftKey("command_block_minecart"), EntityTypes.COMMAND_BLOCK_MINECART)
        registerMinecart(minecraftKey("spawner_minecart"), EntityTypes.COMMAND_BLOCK_MINECART)
    }

    fun registerBanners() {
        for (dyeColor in LanternDyeColor.values()) {
            val dyeId = dyeColor.key.value
            register(minecraftKey("${dyeId}_banner")) {
                maxStackQuantity(16)
                stackKeys {
                    register(Keys.DYE_COLOR, DyeColors.WHITE)
                    register(Keys.BANNER_PATTERN_LAYERS, listOf())
                }
                behaviors {
                    val wallType = { BlockTypeRegistry.require(minecraftKey("${dyeId}_wall_banner")) }
                    val standingType = { BlockTypeRegistry.require(minecraftKey("${dyeId}_banner")) }
                    add(WallOrStandingPlacementBehavior.ofTypes(wallType, standingType))
                }
            }
        }
    }

    fun registerDyeColors() {
        for (dyeColor in LanternDyeColor.values()) {
            register(minecraftKey("${dyeColor.key.value}_dye")) {
                keys {
                    register(ItemKeys.DYE_COLOR, dyeColor)
                }
            }
        }
    }

    fun ItemTypeBuilder.potionEffects(translation: LanternPotionType.() -> Text) {
        val defaultTranslation = translatableTextOf("item.potion.name")
        name {
            get(Keys.POTION_TYPE).map { translation(it as LanternPotionType) }.orElse(defaultTranslation)
        }
        stackKeys {
            register(Keys.COLOR)
            register(Keys.POTION_EFFECTS)
            register(Keys.POTION_TYPE)
        }
    }

    fun registerPotions() {
        register(minecraftKey("potion")) {
            potionEffects { this.normalTranslation }
            maxStackQuantity(1)
            keys {
                registerUseDuration(32)
                register(ItemKeys.IS_ALWAYS_CONSUMABLE, true)
            }
            stackKeys {
                registerApplicablePotionEffects(PotionEffectsProvider)
            }
            behaviors {
                add(ConsumableInteractionBehavior().apply {
                    restItem { itemStackOf(ItemTypes.GLASS_BOTTLE) }
                })
            }
        }
        register(minecraftKey("splash_potion")) {
            potionEffects { this.splashTranslation }
            maxStackQuantity(1)
        }
        register(minecraftKey("lingering_potion")) {
            potionEffects { this.lingeringTranslation }
            maxStackQuantity(1)
        }
    }

    fun registerBooks() {
        register(minecraftKey("writable_book")) {
            maxStackQuantity(1)
            stackKeys {
                register(Keys.PLAIN_PAGES)
            }
        }
        register(minecraftKey("written_book")) {
            maxStackQuantity(1)
            stackKeys {
                register(Keys.PAGES)
                register(Keys.AUTHOR)
                register(Keys.GENERATION)
            }
            behaviors {
                add(OpenHeldBookBehavior())
            }
        }
        register(minecraftKey("enchanted_book")) {
            maxStackQuantity(1)
            stackKeys {
                register(Keys.STORED_ENCHANTMENTS)
            }
        }
        register(minecraftKey("knowledge_book"))
    }

    fun ItemTypeBuilder.headwear() {
        keys {
            register(Keys.EQUIPMENT_TYPE, EquipmentTypes.HEAD)
        }
    }

    fun registerSkullsAndHeads() {
        register(minecraftKey("skeleton_skull")) {
            headwear()
        }
        register(minecraftKey("wither_skeleton_skull")) {
            headwear()
        }
        register(minecraftKey("player_head")) {
            headwear()
            stackKeys {
                register(Keys.SKIN_PROFILE_PROPERTY)
            }
        }
        register(minecraftKey("creeper_head")) {
            headwear()
        }
        register(minecraftKey("dragon_head")) {
            headwear()
        }
    }

    fun registerFireworks() {
        register(minecraftKey("firework_rocket")) {
            stackKeys {
                register(Keys.FIREWORK_EFFECTS, Collections.emptyList())
                register(Keys.FIREWORK_FLIGHT_MODIFIER, 1)
            }
        }
        register(minecraftKey("firework_star")) {
            maxStackQuantity(1)
            stackKeys {
                register(Keys.FIREWORK_EFFECTS, Collections.emptyList())
            }
        }
    }

    fun registerHorseArmor() {
        register(minecraftKey("iron_horse_armor")) {
            maxStackQuantity(1)
        }
        register(minecraftKey("golden_horse_armor")) {
            maxStackQuantity(1)
        }
        register(minecraftKey("diamond_horse_armor")) {
            maxStackQuantity(1)
        }
    }

    fun registerDoors() {
        fun registerWoodenDoor(key: NamespacedKey, woodType: Supplier<out WoodType>) {
            register(key) {
                keys {
                    register(ItemKeys.WOOD_TYPE, woodType)
                }
            }
        }

        registerWoodenDoor(minecraftKey("oak_door"), WoodTypes.OAK)
        registerWoodenDoor(minecraftKey("spruce_door"), WoodTypes.SPRUCE)
        registerWoodenDoor(minecraftKey("birch_door"), WoodTypes.BIRCH)
        registerWoodenDoor(minecraftKey("jungle_door"), WoodTypes.JUNGLE)
        registerWoodenDoor(minecraftKey("acacia_door"), WoodTypes.ACACIA)
        registerWoodenDoor(minecraftKey("dark_oak_door"), WoodTypes.DARK_OAK)
    }

    fun registerBoats() {
        fun registerBoat(key: NamespacedKey, woodType: Supplier<out WoodType>) {
            register(key) {
                maxStackQuantity(1)
                keys {
                    register(ItemKeys.WOOD_TYPE, woodType)
                }
            }
        }

        registerBoat(minecraftKey("oak_boat"), WoodTypes.OAK)
        registerBoat(minecraftKey("spruce_boat"), WoodTypes.SPRUCE)
        registerBoat(minecraftKey("birch_boat"), WoodTypes.BIRCH)
        registerBoat(minecraftKey("jungle_boat"), WoodTypes.JUNGLE)
        registerBoat(minecraftKey("acacia_boat"), WoodTypes.ACACIA)
        registerBoat(minecraftKey("dark_oak_boat"), WoodTypes.DARK_OAK)
    }

    fun <P : Projectile> ItemTypeBuilder.arrow(
            entityType: Supplier<out EntityType<P>>, fn: P.(itemStack: ItemStack) -> Unit = {}) {
        keys {
            register(ItemKeys.BOW_PROJECTILE_PROVIDER, BowProjectile(entityType.get(), fn))
        }
    }

    fun registerArrows() {
        register(minecraftKey("arrow")) {
            arrow(EntityTypes.ARROW)
        }
        register(minecraftKey("tipped_arrow")) {
            arrow(EntityTypes.ARROW) { stack ->
                copyFrom(stack)
            }
            potionEffects { this.tippedArrowTranslation }
        }
        register(minecraftKey("spectral_arrow")) {
            arrow(EntityTypes.SPECTRAL_ARROW)
        }
    }

    fun registerMusicDiscs() {
        fun registerMusicDisc(key: NamespacedKey, musicDisc: Supplier<out MusicDisc>) {
            register(key) {
                maxStackQuantity(1)
                keys {
                    register(Keys.MUSIC_DISC, musicDisc)
                }
            }
        }

        registerMusicDisc(minecraftKey("music_disc_13"), MusicDiscs.THIRTEEN)
        registerMusicDisc(minecraftKey("music_disc_cat"), MusicDiscs.CAT)
        registerMusicDisc(minecraftKey("music_disc_blocks"), MusicDiscs.BLOCKS)
        registerMusicDisc(minecraftKey("music_disc_chirp"), MusicDiscs.CHIRP)
        registerMusicDisc(minecraftKey("music_disc_far"), MusicDiscs.FAR)
        registerMusicDisc(minecraftKey("music_disc_mall"), MusicDiscs.MALL)
        registerMusicDisc(minecraftKey("music_disc_mellohi"), MusicDiscs.MELLOHI)
        registerMusicDisc(minecraftKey("music_disc_stal"), MusicDiscs.STAL)
        registerMusicDisc(minecraftKey("music_disc_strad"), MusicDiscs.STRAD)
        registerMusicDisc(minecraftKey("music_disc_ward"), MusicDiscs.WARD)
        registerMusicDisc(minecraftKey("music_disc_11"), MusicDiscs.ELEVEN)
        registerMusicDisc(minecraftKey("music_disc_wait"), MusicDiscs.WAIT)
    }

    register(minecraftKey("air"))

    register(minecraftKey("iron_sword")) {
        ironTool()
    }
    register(minecraftKey("iron_shovel")) {
        ironTool()
    }
    register(minecraftKey("iron_pickaxe")) {
        ironTool()
    }
    register(minecraftKey("iron_axe")) {
        ironTool()
    }
    register(minecraftKey("iron_hoe")) {
        ironTool()
    }

    register(minecraftKey("wooden_sword")) {
        woodenTool()
    }
    register(minecraftKey("wooden_shovel")) {
        woodenTool()
    }
    register(minecraftKey("wooden_pickaxe")) {
        woodenTool()
    }
    register(minecraftKey("wooden_axe")) {
        woodenTool()
    }
    register(minecraftKey("wooden_hoe")) {
        woodenTool()
    }

    register(minecraftKey("stone_sword")) {
        stoneTool()
    }
    register(minecraftKey("stone_shovel")) {
        stoneTool()
    }
    register(minecraftKey("stone_pickaxe")) {
        stoneTool()
    }
    register(minecraftKey("stone_axe")) {
        stoneTool()
    }
    register(minecraftKey("stone_hoe")) {
        stoneTool()
    }

    register(minecraftKey("diamond_sword")) {
        diamondTool()
    }
    register(minecraftKey("diamond_shovel")) {
        diamondTool()
    }
    register(minecraftKey("diamond_pickaxe")) {
        diamondTool()
    }
    register(minecraftKey("diamond_axe")) {
        diamondTool()
    }
    register(minecraftKey("diamond_hoe")) {
        diamondTool()
    }

    register(minecraftKey("golden_sword")) {
        goldenTool()
    }
    register(minecraftKey("golden_shovel")) {
        goldenTool()
    }
    register(minecraftKey("golden_pickaxe")) {
        goldenTool()
    }
    register(minecraftKey("golden_axe")) {
        goldenTool()
    }
    register(minecraftKey("golden_hoe")) {
        goldenTool()
    }

    register(minecraftKey("leather_helmet")) {
        leatherArmor(useLimit = 56, equipmentType = EquipmentTypes.HEAD)
    }
    register(minecraftKey("leather_chestplate")) {
        leatherArmor(useLimit = 81, equipmentType = EquipmentTypes.CHEST)
    }
    register(minecraftKey("leather_leggings")) {
        leatherArmor(useLimit = 76, equipmentType = EquipmentTypes.LEGS)
    }
    register(minecraftKey("leather_boots")) {
        leatherArmor(useLimit = 56, equipmentType = EquipmentTypes.FEET)
    }

    register(minecraftKey("chainmail_helmet")) {
        chainmailArmor(useLimit = 166, equipmentType = EquipmentTypes.HEAD)
    }
    register(minecraftKey("chainmail_chestplate")) {
        chainmailArmor(useLimit = 241, equipmentType = EquipmentTypes.CHEST)
    }
    register(minecraftKey("chainmail_leggings")) {
        chainmailArmor(useLimit = 226, equipmentType = EquipmentTypes.LEGS)
    }
    register(minecraftKey("chainmail_boots")) {
        chainmailArmor(useLimit = 196, equipmentType = EquipmentTypes.FEET)
    }

    register(minecraftKey("iron_helmet")) {
        ironArmor(useLimit = 166, equipmentType = EquipmentTypes.HEAD)
    }
    register(minecraftKey("iron_chestplate")) {
        ironArmor(useLimit = 241, equipmentType = EquipmentTypes.CHEST)
    }
    register(minecraftKey("iron_leggings")) {
        ironArmor(useLimit = 226, equipmentType = EquipmentTypes.LEGS)
    }
    register(minecraftKey("iron_boots")) {
        ironArmor(useLimit = 196, equipmentType = EquipmentTypes.FEET)
    }

    register(minecraftKey("diamond_helmet")) {
        diamondArmor(useLimit = 364, equipmentType = EquipmentTypes.HEAD)
    }
    register(minecraftKey("diamond_chestplate")) {
        diamondArmor(useLimit = 529, equipmentType = EquipmentTypes.CHEST)
    }
    register(minecraftKey("diamond_leggings")) {
        diamondArmor(useLimit = 496, equipmentType = EquipmentTypes.LEGS)
    }
    register(minecraftKey("diamond_boots")) {
        diamondArmor(useLimit = 430, equipmentType = EquipmentTypes.FEET)
    }

    register(minecraftKey("golden_helmet")) {
        goldenArmor(useLimit = 78, equipmentType = EquipmentTypes.HEAD)
    }
    register(minecraftKey("golden_chestplate")) {
        goldenArmor(useLimit = 113, equipmentType = EquipmentTypes.CHEST)
    }
    register(minecraftKey("golden_leggings")) {
        goldenArmor(useLimit = 76, equipmentType = EquipmentTypes.LEGS)
    }
    register(minecraftKey("golden_boots")) {
        goldenArmor(useLimit = 66, equipmentType = EquipmentTypes.FEET)
    }

    register(minecraftKey("flint_and_steel")) {
        durable(65)
    }

    register(minecraftKey("fishing_rod")) {
        durable(65)
    }

    register(minecraftKey("shears")) {
        durable(238)
    }

    register(minecraftKey("bow")) {
        durable(385)
        keys {
            registerUseDuration(0..72000)
        }
    }

    register(minecraftKey("oak_sign")) {
        maxStackQuantity(16)
        behaviors {
            add(WallOrStandingPlacementBehavior.ofTypes(BlockTypes.OAK_WALL_SIGN, BlockTypes.OAK_SIGN))
        }
    }

    register(minecraftKey("bucket")) {
        maxStackQuantity(16)
    }

    register(minecraftKey("water_bucket")) {
        fluidBucket(FluidTypes.WATER)
    }

    register(minecraftKey("lava_bucket")) {
        fluidBucket(FluidTypes.LAVA)
    }

    register(minecraftKey("saddle")) {
        maxStackQuantity(1)
    }

    register(minecraftKey("snowball")) {
        maxStackQuantity(16)
    }

    register(minecraftKey("egg")) {
        maxStackQuantity(16)
    }

    register(minecraftKey("compass")) {
        maxStackQuantity(1)
    }

    register(minecraftKey("clock")) {
        maxStackQuantity(1)
    }

    register(minecraftKey("oak_boat")) {
        maxStackQuantity(1)
    }

    register(minecraftKey("cake")) {
        maxStackQuantity(1)
    }

    register(minecraftKey("filled_map")) {
        maxStackQuantity(1)
    }

    register(minecraftKey("carrot_on_a_stick")) {
        maxStackQuantity(1)
    }

    register(minecraftKey("cookie")) {
        food(food = 2, saturation = 0.4)
    }

    register(minecraftKey("melon_slice")) {
        food(food = 2, saturation = 1.2)
    }

    register(minecraftKey("apple")) {
        food(food = 3, saturation = 2.4)
    }

    register(minecraftKey("golden_apple")) {
        food(food = 4, saturation = 9.6)
        keys {
            registerApplicablePotionEffects(
                    potionEffectOf(PotionEffectTypes.REGENERATION, amplifier = 1, duration = 100),
                    potionEffectOf(PotionEffectTypes.ABSORPTION, amplifier = 0, duration = 2400)
            )
            register(ItemKeys.IS_ALWAYS_CONSUMABLE, true)
        }
    }

    register(minecraftKey("enchanted_golden_apple")) {
        food(food = 4, saturation = 9.6)
        keys {
            registerApplicablePotionEffects(
                    potionEffectOf(PotionEffectTypes.REGENERATION, amplifier = 1, duration = 400),
                    potionEffectOf(PotionEffectTypes.RESISTANCE, amplifier = 0, duration = 6000),
                    potionEffectOf(PotionEffectTypes.FIRE_RESISTANCE, amplifier = 0, duration = 6000),
                    potionEffectOf(PotionEffectTypes.ABSORPTION, amplifier = 3, duration = 2400)
            )
            register(ItemKeys.IS_ALWAYS_CONSUMABLE, true)
        }
    }

    register(minecraftKey("mushroom_stew")) {
        maxStackQuantity(1)
        food(food = 6, saturation = 7.2)
    }

    register(minecraftKey("bread")) {
        food(food = 5, saturation = 6.0)
    }

    register(minecraftKey("porkchop")) {
        food(food = 6, saturation = 0.3)
    }

    register(minecraftKey("cooked_porkchop")) {
        food(food = 8, saturation = 12.8)
    }

    register(minecraftKey("beef")) {
        food(food = 3, saturation = 1.8)
    }

    register(minecraftKey("cooked_beef")) {
        food(food = 8, saturation = 12.8)
    }

    register(minecraftKey("mutton")) {
        food(food = 2, saturation = 1.2)
    }

    register(minecraftKey("cooked_mutton")) {
        food(food = 6, saturation = 9.6)
    }

    register(minecraftKey("rabbit")) {
        food(food = 3, saturation = 1.8)
    }

    register(minecraftKey("cooked_rabbit")) {
        food(food = 5, saturation = 6.0)
    }

    register(minecraftKey("rabbit_stew")) {
        maxStackQuantity(1)
        food(food = 10, saturation = 12.0) {
            restItem { itemStackOf(ItemTypes.BOWL) }
        }
    }

    register(minecraftKey("chicken")) {
        food(food = 2, saturation = 1.2) {
            val hungerEffect = potionEffectOf(PotionEffectTypes.HUNGER, amplifier = 0, duration = 600)
            consumer { player, _, _ ->
                if (Random.nextInt(100) < 40) { // 40% chance of getting hunger effect
                    player.offerSingle(Keys.POTION_EFFECTS, hungerEffect)
                }
            }
        }
    }

    register(minecraftKey("cooked_chicken")) {
        food(food = 6, saturation = 7.2)
    }

    register(minecraftKey("rotten_flesh")) {
        food(food = 4, saturation = 0.8) {
            val hungerEffect = potionEffectOf(PotionEffectTypes.HUNGER, amplifier = 0, duration = 600)
            consumer { player, _, _ ->
                if (Random.nextInt(100) < 80) { // 80% chance of getting hunger effect
                    player.offerSingle(Keys.POTION_EFFECTS, hungerEffect)
                }
            }
        }
    }

    register(minecraftKey("cod")) {
        food(food = 2, saturation = 0.4)
    }

    register(minecraftKey("cooked_cod")) {
        food(food = 5, saturation = 6.0)
    }

    register(minecraftKey("salmon")) {
        food(food = 2, saturation = 0.4)
    }

    register(minecraftKey("cooked_salmon")) {
        food(food = 6, saturation = 9.6)
    }

    register(minecraftKey("tropical_fish")) {
        food(food = 1, saturation = 0.2)
    }

    register(minecraftKey("pufferfish")) {
        food(food = 1, saturation = 0.2)
        keys {
            registerApplicablePotionEffects(
                    potionEffectOf(PotionEffectTypes.POISON, amplifier = 3, duration = 1200),
                    potionEffectOf(PotionEffectTypes.HUNGER, amplifier = 2, duration = 300),
                    potionEffectOf(PotionEffectTypes.NAUSEA, amplifier = 1, duration = 300)
            )
        }
    }

    register(minecraftKey("spider_eye")) {
        food(food = 2, saturation = 3.2)
        keys {
            registerApplicablePotionEffects(
                    potionEffectOf(PotionEffectTypes.POISON, amplifier = 0, duration = 100)
            )
        }
    }

    register(minecraftKey("carrot")) {
        food(food = 3, saturation = 3.6)
    }

    register(minecraftKey("potato")) {
        food(food = 1, saturation = 0.6)
    }

    register(minecraftKey("baked_potato")) {
        food(food = 5, saturation = 6.0)
    }

    register(minecraftKey("poisonous_potato")) {
        food(food = 2, saturation = 1.2)
        keys {
            registerApplicablePotionEffects(
                    potionEffectOf(PotionEffectTypes.POISON, amplifier = 0, duration = 100)
            )
        }
    }

    register(minecraftKey("beetroot")) {
        maxStackQuantity(1)
        food(food = 1, saturation = 1.2)
    }

    register(minecraftKey("beetroot_soup")) {
        food(food = 6, saturation = 7.2) {
            restItem { itemStackOf(ItemTypes.BOWL) }
        }
    }

    register(minecraftKey("golden_carrot")) {
        food(food = 6, saturation = 14.4)
    }

    register(minecraftKey("pumpkin_pie")) {
        food(food = 8, saturation = 4.8)
    }

    register(minecraftKey("chorus_fruit")) {
        food(food = 4, saturation = 2.4) {
            // TODO: Add random teleport consumer behavior
        }
        keys {
            register(ItemKeys.IS_ALWAYS_CONSUMABLE, true)
        }
    }

    register(minecraftKey("milk_bucket")) {
        maxStackQuantity(1)
        keys {
            registerUseDuration(32)
            register(ItemKeys.IS_ALWAYS_CONSUMABLE, true)
        }
        behaviors {
            add(ConsumableInteractionBehavior().apply {
                consumer(MilkConsumer())
                restItem { itemStackOf(ItemTypes.BUCKET) }
            })
        }
    }

    register(minecraftKey("shield")) {
        durable(336)
        behaviors {
            add(ShieldInteractionBehavior())
        }
    }

    register(minecraftKey("elytra")) {
        durable(432)
        keys {
            register(Keys.EQUIPMENT_TYPE, EquipmentTypes.CHEST)
        }
        behaviors {
            add(ArmorQuickEquipInteractionBehavior())
        }
    }

    register(minecraftKey("totem_of_undying")) {
        maxStackQuantity(1)
    }

    register(minecraftKey("coal"))
    register(minecraftKey("charcoal"))
    register(minecraftKey("diamond"))
    register(minecraftKey("iron_ingot"))
    register(minecraftKey("gold_ingot"))
    register(minecraftKey("stick"))
    register(minecraftKey("bowl"))
    register(minecraftKey("string"))
    register(minecraftKey("feather"))
    register(minecraftKey("gunpowder"))
    register(minecraftKey("wheat_seeds"))
    register(minecraftKey("wheat"))
    register(minecraftKey("flint"))
    register(minecraftKey("painting"))
    register(minecraftKey("iron_door"))
    register(minecraftKey("redstone"))
    register(minecraftKey("leather"))
    register(minecraftKey("brick"))
    register(minecraftKey("clay_ball"))
    register(minecraftKey("paper"))
    register(minecraftKey("book"))
    register(minecraftKey("slime_ball"))
    register(minecraftKey("glowstone_dust"))
    register(minecraftKey("inc_sac"))
    register(minecraftKey("lapis_lazuli"))
    register(minecraftKey("cocoa_beans"))
    register(minecraftKey("bone_meal"))
    register(minecraftKey("bone"))
    register(minecraftKey("sugar"))
    register(minecraftKey("repeater"))
    register(minecraftKey("pumpkin_seeds"))
    register(minecraftKey("melon_seeds"))
    register(minecraftKey("ender_pearl"))
    register(minecraftKey("blaze_rod"))
    register(minecraftKey("ghast_tear"))
    register(minecraftKey("gold_nugget"))
    register(minecraftKey("nether_wart"))
    register(minecraftKey("glass_bottle"))
    register(minecraftKey("fermented_spider_eye"))
    register(minecraftKey("blaze_powder"))
    register(minecraftKey("magma_cream"))
    register(minecraftKey("brewing_stand"))
    register(minecraftKey("cauldron"))
    register(minecraftKey("ender_eye"))
    register(minecraftKey("glistering_melon_slice"))
    register(minecraftKey("experience_bottle"))
    register(minecraftKey("fire_charge"))
    register(minecraftKey("emerald"))
    register(minecraftKey("item_frame"))
    register(minecraftKey("flower_pot"))
    register(minecraftKey("map"))
    register(minecraftKey("nether_star"))
    register(minecraftKey("comparator"))
    register(minecraftKey("nether_brick"))
    register(minecraftKey("quartz"))
    register(minecraftKey("prismarine_shard"))
    register(minecraftKey("prismarine_crystals"))
    register(minecraftKey("rabbit_foot"))
    register(minecraftKey("rabbit_hide"))
    register(minecraftKey("armor_stand"))
    register(minecraftKey("lead"))
    register(minecraftKey("name_tag"))
    register(minecraftKey("end_crystal"))
    register(minecraftKey("popped_chorus_fruit"))
    register(minecraftKey("beetroot_seeds"))
    register(minecraftKey("dragon_breath"))
    register(minecraftKey("shulker_shell"))
    register(minecraftKey("iron_nugget"))
    register(minecraftKey("debug_stick"))

    registerMinecarts()
    registerDyeColors()
    registerPotions()
    registerBooks()
    registerSkullsAndHeads()
    registerFireworks()
    registerBanners()
    registerHorseArmor()
    registerDoors()
    registerBoats()
    registerArrows()
    registerMusicDiscs()
}
