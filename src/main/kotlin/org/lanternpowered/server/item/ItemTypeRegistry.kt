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
package org.lanternpowered.server.item

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.NamespacedKeys.minecraft
import org.lanternpowered.api.ext.itemStackOf
import org.lanternpowered.api.effect.potion.potionEffectOf
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.ItemTypes
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.registry.CatalogRegistry
import org.lanternpowered.api.registry.require
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.server.data.type.LanternDyeColor
import org.lanternpowered.server.effect.potion.LanternPotionType
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule
import org.lanternpowered.server.game.registry.type.data.MusicDiscRegistryModule
import org.lanternpowered.server.game.registry.type.data.ToolTypeRegistryModule
import org.lanternpowered.server.game.registry.type.item.inventory.equipment.EquipmentTypeRegistryModule
import org.lanternpowered.server.item.behavior.vanilla.ArmorQuickEquipInteractionBehavior
import org.lanternpowered.server.item.behavior.vanilla.ConsumableInteractionBehavior
import org.lanternpowered.server.item.behavior.vanilla.OpenHeldBookBehavior
import org.lanternpowered.server.item.behavior.vanilla.ShieldInteractionBehavior
import org.lanternpowered.server.item.behavior.vanilla.WallOrStandingPlacementBehavior
import org.lanternpowered.server.item.behavior.vanilla.consumable.MilkConsumer
import org.lanternpowered.server.item.property.BowProjectile
import org.lanternpowered.server.network.item.NetworkItemTypeRegistry
import org.lanternpowered.server.text.translation.TranslationHelper.tr
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.type.ArmorType
import org.spongepowered.api.data.type.ArmorTypes
import org.spongepowered.api.data.type.DyeColor
import org.spongepowered.api.data.type.DyeColors
import org.spongepowered.api.data.type.ToolType
import org.spongepowered.api.data.type.ToolTypes
import org.spongepowered.api.data.type.WoodType
import org.spongepowered.api.data.type.WoodTypes
import org.spongepowered.api.effect.potion.PotionEffectTypes
import org.spongepowered.api.effect.sound.music.MusicDisc
import org.spongepowered.api.effect.sound.music.MusicDiscs
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.projectile.Projectile
import org.spongepowered.api.entity.vehicle.minecart.MinecartEntity
import org.spongepowered.api.fluid.FluidStackSnapshot
import org.spongepowered.api.fluid.FluidType
import org.spongepowered.api.fluid.FluidTypes
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes
import org.spongepowered.api.registry.util.RegistrationDependency
import java.util.Collections
import java.util.function.Supplier
import kotlin.random.Random

@RegistrationDependency(
        ArmorTypeRegistryModule::class,
        BlockRegistryModule::class,
        EquipmentTypeRegistryModule::class,
        ToolTypeRegistryModule::class,
        PotionEffectTypeRegistryModule::class,
        MusicDiscRegistryModule::class,
        EquipmentTypeRegistryModule::class,
        DyeColorRegistryModule::class
)
object ItemTypeRegistry : AdditionalPluginCatalogRegistryModule<ItemType>(ItemTypes::class) {

    public override fun <A : ItemType> register(catalogType: A): A {
        return super.register(catalogType).also {
            NetworkItemTypeRegistry.register(catalogType)
        }
    }

    private fun register(key: NamespacedKey, fn: ItemTypeBuilder.() -> Unit = {}): ItemType {
        return itemTypeOf(key, fn)//.also { register(it) }
    }

    private fun ItemTypeBuilder.woodenTool(useLimit: Int = 60) {
        tool(useLimit, ToolTypes.WOOD)
    }

    private fun ItemTypeBuilder.stoneTool(useLimit: Int = 132) {
        tool(useLimit, ToolTypes.STONE)
    }

    private fun ItemTypeBuilder.ironTool(useLimit: Int = 251) {
        tool(useLimit, ToolTypes.IRON)
    }

    private fun ItemTypeBuilder.goldenTool(useLimit: Int = 33) {
        tool(useLimit, ToolTypes.GOLD)
    }

    private fun ItemTypeBuilder.diamondTool(useLimit: Int = 1562) {
        tool(useLimit, ToolTypes.DIAMOND)
    }

    private fun ItemTypeBuilder.tool(useLimit: Int, toolType: Supplier<out ToolType>) {
        tool(useLimit, toolType.get())
    }

    private fun ItemTypeBuilder.tool(useLimit: Int, toolType: ToolType) {
        durable(useLimit)
        keys {
            toolType(toolType)
            dualWieldable(true)
            register()
        }
    }

    private fun ItemTypeBuilder.leatherArmor(useLimit: Int, equipmentType: Supplier<out EquipmentType>) {
        armor(useLimit, ArmorTypes.LEATHER, equipmentType)
        stackKeys {
            register(Keys.COLOR)
        }
    }

    private fun ItemTypeBuilder.ironArmor(useLimit: Int, equipmentType: Supplier<out EquipmentType>) {
        armor(useLimit, ArmorTypes.IRON, equipmentType)
    }

    private fun ItemTypeBuilder.chainmailArmor(useLimit: Int, equipmentType: Supplier<out EquipmentType>) {
        armor(useLimit, ArmorTypes.CHAIN, equipmentType)
    }

    private fun ItemTypeBuilder.goldenArmor(useLimit: Int, equipmentType: Supplier<out EquipmentType>) {
        armor(useLimit, ArmorTypes.GOLD, equipmentType)
    }

    private fun ItemTypeBuilder.diamondArmor(useLimit: Int, equipmentType: Supplier<out EquipmentType>) {
        armor(useLimit, ArmorTypes.DIAMOND, equipmentType)
    }

    private fun ItemTypeBuilder.armor(useLimit: Int, armorType: ArmorType, equipmentType: Supplier<out EquipmentType>) {
        durable(useLimit)
        keys {
            armorType(armorType)
            register(Keys.EQUIPMENT_TYPE, equipmentType)
        }
        behaviors {
            add(ArmorQuickEquipInteractionBehavior())
        }
    }

    private fun ItemTypeBuilder.durable(useLimit: Int) {
        maxStackQuantity(1)
        keys {
            useLimit(useLimit)
        }
        stackKeys {
            register(Keys.ITEM_DURABILITY, 0)
            register(Keys.IS_UNBREAKABLE, true) // TODO: True until durability is implemented
        }
    }

    private fun ItemTypeBuilder.food(replenishedFood: Double, saturation: Double,
                                     consumeDuration: Int = 32,
                                     consumeBehavior: ConsumableInteractionBehavior.() -> Unit = {}) {
        keys {
            useDuration(consumeDuration)
            replenishedFood(replenishedFood)
            saturation(saturation)
        }
        behaviors {
            add(ConsumableInteractionBehavior().apply(consumeBehavior))
        }
    }

    private fun ItemTypeBuilder.fluidBucket(fluidType: FluidType) {
        maxStackQuantity(1)
        stackKeys {
            register(Keys.FLUID_ITEM_STACK, FluidStackSnapshot.builder()
                    .fluid(fluidType)
                    .volume(1000)
                    .build())
        }
    }

    private fun ItemTypeBuilder.dyeColor(dyeColor: DyeColor) {
        keys {
            register(ItemKeys.DYE_COLOR, dyeColor)
        }
    }

    override fun registerDefaults() {
        register(minecraft("air"))

        register(minecraft("iron_sword")) {
            ironTool()
        }
        register(minecraft("iron_shovel")) {
            ironTool()
        }
        register(minecraft("iron_pickaxe")) {
            ironTool()
        }
        register(minecraft("iron_axe")) {
            ironTool()
        }
        register(minecraft("iron_hoe")) {
            ironTool()
        }

        register(minecraft("wooden_sword")) {
            woodenTool()
        }
        register(minecraft("wooden_shovel")) {
            woodenTool()
        }
        register(minecraft("wooden_pickaxe")) {
            woodenTool()
        }
        register(minecraft("wooden_axe")) {
            woodenTool()
        }
        register(minecraft("wooden_hoe")) {
            woodenTool()
        }

        register(minecraft("stone_sword")) {
            stoneTool()
        }
        register(minecraft("stone_shovel")) {
            stoneTool()
        }
        register(minecraft("stone_pickaxe")) {
            stoneTool()
        }
        register(minecraft("stone_axe")) {
            stoneTool()
        }
        register(minecraft("stone_hoe")) {
            stoneTool()
        }

        register(minecraft("diamond_sword")) {
            diamondTool()
        }
        register(minecraft("diamond_shovel")) {
            diamondTool()
        }
        register(minecraft("diamond_pickaxe")) {
            diamondTool()
        }
        register(minecraft("diamond_axe")) {
            diamondTool()
        }
        register(minecraft("diamond_hoe")) {
            diamondTool()
        }

        register(minecraft("golden_sword")) {
            goldenTool()
        }
        register(minecraft("golden_shovel")) {
            goldenTool()
        }
        register(minecraft("golden_pickaxe")) {
            goldenTool()
        }
        register(minecraft("golden_axe")) {
            goldenTool()
        }
        register(minecraft("golden_hoe")) {
            goldenTool()
        }

        register(minecraft("leather_helmet")) {
            leatherArmor(useLimit = 56, equipmentType = EquipmentTypes.HEADWEAR)
        }
        register(minecraft("leather_chestplate")) {
            leatherArmor(useLimit = 81, equipmentType = EquipmentTypes.CHESTPLATE)
        }
        register(minecraft("leather_leggings")) {
            leatherArmor(useLimit = 76, equipmentType = EquipmentTypes.LEGGINGS)
        }
        register(minecraft("leather_boots")) {
            leatherArmor(useLimit = 56, equipmentType = EquipmentTypes.BOOTS)
        }

        register(minecraft("chainmail_helmet")) {
            chainmailArmor(useLimit = 166, equipmentType = EquipmentTypes.HEADWEAR)
        }
        register(minecraft("chainmail_chestplate")) {
            chainmailArmor(useLimit = 241, equipmentType = EquipmentTypes.CHESTPLATE)
        }
        register(minecraft("chainmail_leggings")) {
            chainmailArmor(useLimit = 226, equipmentType = EquipmentTypes.LEGGINGS)
        }
        register(minecraft("chainmail_boots")) {
            chainmailArmor(useLimit = 196, equipmentType = EquipmentTypes.BOOTS)
        }

        register(minecraft("iron_helmet")) {
            ironArmor(useLimit = 166, equipmentType = EquipmentTypes.HEADWEAR)
        }
        register(minecraft("iron_chestplate")) {
            ironArmor(useLimit = 241, equipmentType = EquipmentTypes.CHESTPLATE)
        }
        register(minecraft("iron_leggings")) {
            ironArmor(useLimit = 226, equipmentType = EquipmentTypes.LEGGINGS)
        }
        register(minecraft("iron_boots")) {
            ironArmor(useLimit = 196, equipmentType = EquipmentTypes.BOOTS)
        }

        register(minecraft("diamond_helmet")) {
            diamondArmor(useLimit = 364, equipmentType = EquipmentTypes.HEADWEAR)
        }
        register(minecraft("diamond_chestplate")) {
            diamondArmor(useLimit = 529, equipmentType = EquipmentTypes.CHESTPLATE)
        }
        register(minecraft("diamond_leggings")) {
            diamondArmor(useLimit = 496, equipmentType = EquipmentTypes.LEGGINGS)
        }
        register(minecraft("diamond_boots")) {
            diamondArmor(useLimit = 430, equipmentType = EquipmentTypes.BOOTS)
        }

        register(minecraft("golden_helmet")) {
            goldenArmor(useLimit = 78, equipmentType = EquipmentTypes.HEADWEAR)
        }
        register(minecraft("golden_chestplate")) {
            goldenArmor(useLimit = 113, equipmentType = EquipmentTypes.CHESTPLATE)
        }
        register(minecraft("golden_leggings")) {
            goldenArmor(useLimit = 76, equipmentType = EquipmentTypes.LEGGINGS)
        }
        register(minecraft("golden_boots")) {
            goldenArmor(useLimit = 66, equipmentType = EquipmentTypes.BOOTS)
        }

        register(minecraft("flint_and_steel")) {
            durable(65)
        }

        register(minecraft("fishing_rod")) {
            durable(65)
        }

        register(minecraft("shears")) {
            durable(238)
        }

        register(minecraft("bow")) {
            durable(385)
            keys {
                useDuration(0..72000)
            }
        }

        register(minecraft("oak_sign")) {
            maxStackQuantity(16)
            behaviors {
                add(WallOrStandingPlacementBehavior.ofTypes(BlockTypes::OAK_WALL_SIGN, BlockTypes::OAK_SIGN))
            }
        }

        register(minecraft("bucket")) {
            maxStackQuantity(16)
        }

        register(minecraft("water_bucket")) {
            fluidBucket(FluidTypes.WATER)
        }

        register(minecraft("lava_bucket")) {
            fluidBucket(FluidTypes.LAVA)
        }

        register(minecraft("saddle")) {
            maxStackQuantity(1)
        }

        register(minecraft("snowball")) {
            maxStackQuantity(16)
        }

        register(minecraft("egg")) {
            maxStackQuantity(16)
        }

        register(minecraft("compass")) {
            maxStackQuantity(1)
        }

        register(minecraft("clock")) {
            maxStackQuantity(1)
        }

        register(minecraft("oak_boat")) {
            maxStackQuantity(1)
        }

        register(minecraft("cake")) {
            maxStackQuantity(1)
        }

        register(minecraft("filled_map")) {
            maxStackQuantity(1)
        }

        register(minecraft("carrot_on_a_stick")) {
            maxStackQuantity(1)
        }

        register(minecraft("cookie")) {
            food(replenishedFood = 2.0, saturation = 0.4)
        }

        register(minecraft("melon_slice")) {
            food(replenishedFood = 2.0, saturation = 1.2)
        }

        register(minecraft("apple")) {
            food(replenishedFood = 3.0, saturation = 2.4)
        }

        register(minecraft("golden_apple")) {
            food(replenishedFood = 4.0, saturation = 9.6)
            keys {
                applicablePotionEffects(
                        potionEffectOf(PotionEffectTypes.REGENERATION, amplifier = 1, duration = 100),
                        potionEffectOf(PotionEffectTypes.ABSORPTION, amplifier = 0, duration = 2400)
                )
                alwaysConsumable(true)
            }
        }

        register(minecraft("enchanted_golden_apple")) {
            food(replenishedFood = 4.0, saturation = 9.6)
            keys {
                applicablePotionEffects(
                        potionEffectOf(PotionEffectTypes.REGENERATION, amplifier = 1, duration = 400),
                        potionEffectOf(PotionEffectTypes.RESISTANCE, amplifier = 0, duration = 6000),
                        potionEffectOf(PotionEffectTypes.FIRE_RESISTANCE, amplifier = 0, duration = 6000),
                        potionEffectOf(PotionEffectTypes.ABSORPTION, amplifier = 3, duration = 2400)
                )
                alwaysConsumable(true)
            }
        }

        register(minecraft("mushroom_stew")) {
            maxStackQuantity(1)
            food(replenishedFood = 6.0, saturation = 7.2)
        }

        register(minecraft("bread")) {
            food(replenishedFood = 5.0, saturation = 6.0)
        }

        register(minecraft("porkchop")) {
            food(replenishedFood = 6.0, saturation = 0.3)
        }

        register(minecraft("cooked_porkchop")) {
            food(replenishedFood = 8.0, saturation = 12.8)
        }

        register(minecraft("beef")) {
            food(replenishedFood = 3.0, saturation = 1.8)
        }

        register(minecraft("cooked_beef")) {
            food(replenishedFood = 8.0, saturation = 12.8)
        }

        register(minecraft("mutton")) {
            food(replenishedFood = 2.0, saturation = 1.2)
        }

        register(minecraft("cooked_mutton")) {
            food(replenishedFood = 6.0, saturation = 9.6)
        }

        register(minecraft("rabbit")) {
            food(replenishedFood = 3.0, saturation = 1.8)
        }

        register(minecraft("cooked_rabbit")) {
            food(replenishedFood = 5.0, saturation = 6.0)
        }

        register(minecraft("rabbit_stew")) {
            maxStackQuantity(1)
            food(replenishedFood = 10.0, saturation = 12.0) {
                restItem { itemStackOf(ItemTypes.BOWL) }
            }
        }

        register(minecraft("chicken")) {
            food(replenishedFood = 2.0, saturation = 1.2) {
                val hungerEffect = potionEffectOf(PotionEffectTypes.HUNGER, amplifier = 0, duration = 600)
                consumer { player, _, _ ->
                    if (Random.nextInt(100) < 40) { // 40% chance of getting hunger effect
                        player.offerSingle(Keys.POTION_EFFECTS, hungerEffect)
                    }
                }
            }
        }

        register(minecraft("cooked_chicken")) {
            food(replenishedFood = 6.0, saturation = 7.2)
        }

        register(minecraft("rotten_flesh")) {
            food(replenishedFood = 4.0, saturation = 0.8) {
                val hungerEffect = potionEffectOf(PotionEffectTypes.HUNGER, amplifier = 0, duration = 600)
                consumer { player, _, _ ->
                    if (Random.nextInt(100) < 80) { // 80% chance of getting hunger effect
                        player.offerSingle(Keys.POTION_EFFECTS, hungerEffect)
                    }
                }
            }
        }

        register(minecraft("cod")) {
            food(replenishedFood = 2.0, saturation = 0.4)
        }

        register(minecraft("cooked_cod")) {
            food(replenishedFood = 5.0, saturation = 6.0)
        }

        register(minecraft("salmon")) {
            food(replenishedFood = 2.0, saturation = 0.4)
        }

        register(minecraft("cooked_salmon")) {
            food(replenishedFood = 6.0, saturation = 9.6)
        }

        register(minecraft("tropical_fish")) {
            food(replenishedFood = 1.0, saturation = 0.2)
        }

        register(minecraft("pufferfish")) {
            food(replenishedFood = 1.0, saturation = 0.2)
            keys {
                applicablePotionEffects(
                        potionEffectOf(PotionEffectTypes.POISON, amplifier = 3, duration = 1200),
                        potionEffectOf(PotionEffectTypes.HUNGER, amplifier = 2, duration = 300),
                        potionEffectOf(PotionEffectTypes.NAUSEA, amplifier = 1, duration = 300)
                )
            }
        }

        register(minecraft("spider_eye")) {
            food(replenishedFood = 2.0, saturation = 3.2)
            keys {
                applicablePotionEffects(
                        potionEffectOf(PotionEffectTypes.POISON, amplifier = 0, duration = 100)
                )
            }
        }

        register(minecraft("carrot")) {
            food(replenishedFood = 3.0, saturation = 3.6)
        }

        register(minecraft("potato")) {
            food(replenishedFood = 1.0, saturation = 0.6)
        }

        register(minecraft("baked_potato")) {
            food(replenishedFood = 5.0, saturation = 6.0)
        }

        register(minecraft("poisonous_potato")) {
            food(replenishedFood = 2.0, saturation = 1.2)
            keys {
                applicablePotionEffects(
                        potionEffectOf(PotionEffectTypes.POISON, amplifier = 0, duration = 100)
                )
            }
        }

        register(minecraft("beetroot")) {
            maxStackQuantity(1)
            food(replenishedFood = 1.0, saturation = 1.2)
        }

        register(minecraft("beetroot_soup")) {
            food(replenishedFood = 6.0, saturation = 7.2) {
                restItem { itemStackOf(ItemTypes.BOWL) }
            }
        }

        register(minecraft("golden_carrot")) {
            food(replenishedFood = 6.0, saturation = 14.4)
        }

        register(minecraft("pumpkin_pie")) {
            food(replenishedFood = 8.0, saturation = 4.8)
        }

        register(minecraft("chorus_fruit")) {
            food(replenishedFood = 4.0, saturation = 2.4) {
                // TODO: Add random teleport consumer behavior
            }
            keys {
                alwaysConsumable(true)
            }
        }

        register(minecraft("milk_bucket")) {
            maxStackQuantity(1)
            keys {
                useDuration(32)
                alwaysConsumable(true)
            }
            behaviors {
                add(ConsumableInteractionBehavior().apply {
                    consumer(MilkConsumer())
                    restItem { itemStackOf(ItemTypes.BUCKET) }
                })
            }
        }

        register(minecraft("shield")) {
            durable(336)
            behaviors {
                add(ShieldInteractionBehavior())
            }
        }

        register(minecraft("elytra")) {
            durable(432)
            keys {
                register(Keys.EQUIPMENT_TYPE, EquipmentTypes.CHESTPLATE)
            }
            behaviors {
                add(ArmorQuickEquipInteractionBehavior())
            }
        }

        register(minecraft("totem_of_undying")) {
            maxStackQuantity(1)
        }

        register(minecraft("coal"))
        register(minecraft("charcoal"))
        register(minecraft("diamond"))
        register(minecraft("iron_ingot"))
        register(minecraft("gold_ingot"))
        register(minecraft("stick"))
        register(minecraft("bowl"))
        register(minecraft("string"))
        register(minecraft("feather"))
        register(minecraft("gunpowder"))
        register(minecraft("wheat_seeds"))
        register(minecraft("wheat"))
        register(minecraft("flint"))
        register(minecraft("painting"))
        register(minecraft("iron_door"))
        register(minecraft("redstone"))
        register(minecraft("leather"))
        register(minecraft("brick"))
        register(minecraft("clay_ball"))
        register(minecraft("paper"))
        register(minecraft("book"))
        register(minecraft("slime_ball"))
        register(minecraft("glowstone_dust"))
        register(minecraft("inc_sac"))
        register(minecraft("lapis_lazuli"))
        register(minecraft("cocoa_beans"))
        register(minecraft("bone_meal"))
        register(minecraft("bone"))
        register(minecraft("sugar"))
        register(minecraft("repeater"))
        register(minecraft("pumpkin_seeds"))
        register(minecraft("melon_seeds"))
        register(minecraft("ender_pearl"))
        register(minecraft("blaze_rod"))
        register(minecraft("ghast_tear"))
        register(minecraft("gold_nugget"))
        register(minecraft("nether_wart"))
        register(minecraft("glass_bottle"))
        register(minecraft("fermented_spider_eye"))
        register(minecraft("blaze_powder"))
        register(minecraft("magma_cream"))
        register(minecraft("brewing_stand"))
        register(minecraft("cauldron"))
        register(minecraft("ender_eye"))
        register(minecraft("glistering_melon_slice"))
        register(minecraft("experience_bottle"))
        register(minecraft("fire_charge"))
        register(minecraft("emerald"))
        register(minecraft("item_frame"))
        register(minecraft("flower_pot"))
        register(minecraft("map"))
        register(minecraft("nether_star"))
        register(minecraft("comparator"))
        register(minecraft("nether_brick"))
        register(minecraft("quartz"))
        register(minecraft("prismarine_shard"))
        register(minecraft("prismarine_crystals"))
        register(minecraft("rabbit_foot"))
        register(minecraft("rabbit_hide"))
        register(minecraft("armor_stand"))
        register(minecraft("lead"))
        register(minecraft("name_tag"))
        register(minecraft("end_crystal"))
        register(minecraft("popped_chorus_fruit"))
        register(minecraft("beetroot_seeds"))
        register(minecraft("dragon_breath"))
        register(minecraft("shulker_shell"))
        register(minecraft("iron_nugget"))
        register(minecraft("debug_stick"))

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

    private fun registerMinecarts() {
        fun registerMinecart(key: NamespacedKey, entityType: () -> Supplier<out EntityType<out MinecartEntity>>) {
            register(key) {
                maxStackQuantity(1)
            }
        }

        registerMinecart(minecraft("minecart"), EntityTypes::MINECART)
        registerMinecart(minecraft("chest_minecart"), EntityTypes::CHEST_MINECART)
        registerMinecart(minecraft("furnace_minecart"), EntityTypes::FURNACE_MINECART)
        registerMinecart(minecraft("tnt_minecart"), EntityTypes::TNT_MINECART)
        registerMinecart(minecraft("hopper_minecart"), EntityTypes::HOPPER_MINECART)
        registerMinecart(minecraft("command_block_minecart"), EntityTypes::COMMAND_BLOCK_MINECART)
        registerMinecart(minecraft("spawner_minecart"), EntityTypes::COMMAND_BLOCK_MINECART)
    }

    private fun registerBanners() {
        for (dyeColor in LanternDyeColor.values()) {
            val dyeId = dyeColor.key.value
            register(minecraft("${dyeId}_banner")) {
                maxStackQuantity(16)
                stackKeys {
                    register(Keys.BANNER_BASE_COLOR, DyeColors.WHITE)
                    register(Keys.BANNER_PATTERN_LAYERS, listOf())
                }
                behaviors {
                    val wallType = lazy { CatalogRegistry.require<BlockType>(minecraft("${dyeId}_wall_banner")) }
                    val standingType = lazy { CatalogRegistry.require<BlockType>(minecraft("${dyeId}_banner")) }
                    add(WallOrStandingPlacementBehavior.ofTypes(wallType, standingType))
                }
            }
        }
    }

    private fun registerDyeColors() {
        for (dyeColor in LanternDyeColor.values()) {
            register(minecraft("${dyeColor.key.value}_dye")) {
                keys {
                    register(ItemKeys.DYE_COLOR, dyeColor)
                }
            }
        }
    }

    private fun ItemTypeBuilder.potionEffects(translation: LanternPotionType.() -> Translation) {
        val defaultTranslation = tr("item.potion.name")
        name {
            get(Keys.POTION_TYPE).map { translation(it as LanternPotionType) }.orElse(defaultTranslation)
        }
        stackKeys {
            register(Keys.COLOR)
            register(Keys.POTION_EFFECTS)
            register(Keys.POTION_TYPE)
        }
    }

    private fun registerPotions() {
        register(minecraft("potion")) {
            potionEffects { this.translation }
            maxStackQuantity(1)
            keys {
                useDuration(32)
                alwaysConsumable(true)
                forStack {
                    registerProvider(Properties.APPLICABLE_POTION_EFFECTS) {
                        get(PotionEffectsProvider)
                    }
                }
            }
            behaviors {
                add(ConsumableInteractionBehavior().apply {
                    restItem { itemStackOf(ItemTypes.GLASS_BOTTLE) }
                })
            }
        }
        register(minecraft("splash_potion")) {
            potionEffects { this.splashTranslation }
            maxStackQuantity(1)
        }
        register(minecraft("lingering_potion")) {
            potionEffects { this.lingeringTranslation }
            maxStackQuantity(1)
        }
    }

    private fun registerBooks() {
        register(minecraft("writable_book")) {
            maxStackQuantity(1)
            stackKeys {
                register(Keys.PLAIN_BOOK_PAGES)
            }
        }
        register(minecraft("written_book")) {
            maxStackQuantity(1)
            stackKeys {
                register(Keys.BOOK_PAGES)
                register(Keys.BOOK_AUTHOR)
                register(Keys.GENERATION)
            }
            behaviors {
                add(OpenHeldBookBehavior())
            }
        }
        register(minecraft("enchanted_book")) {
            maxStackQuantity(1)
            stackKeys {
                register(Keys.STORED_ENCHANTMENTS)
            }
        }
        register(minecraft("knowledge_book"))
    }

    private fun ItemTypeBuilder.headwear() {
        keys {
            register(Keys.EQUIPMENT_TYPE, EquipmentTypes.HEADWEAR)
        }
    }

    private fun registerSkullsAndHeads() {
        register(minecraft("skeleton_skull")) {
            headwear()
        }
        register(minecraft("wither_skeleton_skull")) {
            headwear()
        }
        register(minecraft("player_head")) {
            headwear()
            stackKeys {
                register(Keys.SKIN)
            }
        }
        register(minecraft("creeper_head")) {
            headwear()
        }
        register(minecraft("dragon_head")) {
            headwear()
        }
    }

    private fun registerFireworks() {
        register(minecraft("firework_rocket")) {
            stackKeys {
                register(Keys.FIREWORK_EFFECTS, Collections.emptyList())
                register(Keys.FIREWORK_FLIGHT_MODIFIER, 1)
            }
        }
        register(minecraft("firework_star")) {
            maxStackQuantity(1)
            stackKeys {
                register(Keys.FIREWORK_EFFECTS, Collections.emptyList())
            }
        }
    }

    private fun registerHorseArmor() {
        register(minecraft("iron_horse_armor")) {
            maxStackQuantity(1)
        }
        register(minecraft("golden_horse_armor")) {
            maxStackQuantity(1)
        }
        register(minecraft("diamond_horse_armor")) {
            maxStackQuantity(1)
        }
    }

    private fun registerDoors() {
        fun registerWoodenDoor(key: NamespacedKey, woodType: Supplier<out WoodType>) {
            register(key) {
                keys {
                    register(ItemKeys.WOOD_TYPE, woodType)
                }
            }
        }

        registerWoodenDoor(minecraft("oak_door"), WoodTypes.OAK)
        registerWoodenDoor(minecraft("spruce_door"), WoodTypes.SPRUCE)
        registerWoodenDoor(minecraft("birch_door"), WoodTypes.BIRCH)
        registerWoodenDoor(minecraft("jungle_door"), WoodTypes.JUNGLE)
        registerWoodenDoor(minecraft("acacia_door"), WoodTypes.ACACIA)
        registerWoodenDoor(minecraft("dark_oak_door"), WoodTypes.DARK_OAK)
    }

    private fun registerBoats() {
        fun registerBoat(key: NamespacedKey, woodType: Supplier<out WoodType>) {
            register(key) {
                maxStackQuantity(1)
                keys {
                    register(ItemKeys.WOOD_TYPE, woodType)
                }
            }
        }

        registerBoat(minecraft("oak_boat"), WoodTypes.OAK)
        registerBoat(minecraft("spruce_boat"), WoodTypes.SPRUCE)
        registerBoat(minecraft("birch_boat"), WoodTypes.BIRCH)
        registerBoat(minecraft("jungle_boat"), WoodTypes.JUNGLE)
        registerBoat(minecraft("acacia_boat"), WoodTypes.ACACIA)
        registerBoat(minecraft("dark_oak_boat"), WoodTypes.DARK_OAK)
    }

    private fun <P : Projectile> ItemTypeBuilder.arrow(
            entityType: Supplier<out EntityType<P>>, fn: P.(itemStack: ItemStack) -> Unit = {}) {
        keys {
            register(ItemKeys.BOW_PROJECTILE_PROVIDER, BowProjectile(entityType.get(), fn))
        }
    }

    private fun registerArrows() {
        register(minecraft("arrow")) {
            arrow(EntityTypes.ARROW)
        }
        register(minecraft("tipped_arrow")) {
            arrow(EntityTypes.ARROW) { stack ->
                copyFrom(stack)
            }
            potionEffects { this.tippedArrowTranslation }
        }
        register(minecraft("spectral_arrow")) {
            arrow(EntityTypes.SPECTRAL_ARROW)
        }
    }

    private fun registerMusicDiscs() {
        fun registerMusicDisc(key: NamespacedKey, musicDisc: Supplier<out MusicDisc>) {
            register(key) {
                maxStackQuantity(1)
                keys {
                    // register(Properties.MUSIC_DISC, musicDisc) TODO
                }
            }
        }

        registerMusicDisc(minecraft("music_disc_13"), MusicDiscs.THIRTEEN)
        registerMusicDisc(minecraft("music_disc_cat"), MusicDiscs.CAT)
        registerMusicDisc(minecraft("music_disc_blocks"), MusicDiscs.BLOCKS)
        registerMusicDisc(minecraft("music_disc_chirp"), MusicDiscs.CHIRP)
        registerMusicDisc(minecraft("music_disc_far"), MusicDiscs.FAR)
        registerMusicDisc(minecraft("music_disc_mall"), MusicDiscs.MALL)
        registerMusicDisc(minecraft("music_disc_mellohi"), MusicDiscs.MELLOHI)
        registerMusicDisc(minecraft("music_disc_stal"), MusicDiscs.STAL)
        registerMusicDisc(minecraft("music_disc_strad"), MusicDiscs.STRAD)
        registerMusicDisc(minecraft("music_disc_ward"), MusicDiscs.WARD)
        registerMusicDisc(minecraft("music_disc_11"), MusicDiscs.ELEVEN)
        registerMusicDisc(minecraft("music_disc_wait"), MusicDiscs.WAIT)
    }
}
