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
package org.lanternpowered.server.game.registry.type.item;

import static org.lanternpowered.server.item.PropertyProviders.alwaysConsumable;
import static org.lanternpowered.server.item.PropertyProviders.applicableEffects;
import static org.lanternpowered.server.item.PropertyProviders.armorType;
import static org.lanternpowered.server.item.PropertyProviders.dualWield;
import static org.lanternpowered.server.item.PropertyProviders.equipmentType;
import static org.lanternpowered.server.item.PropertyProviders.maximumUseDuration;
import static org.lanternpowered.server.item.PropertyProviders.musicDisc;
import static org.lanternpowered.server.item.PropertyProviders.replenishedFood;
import static org.lanternpowered.server.item.PropertyProviders.saturation;
import static org.lanternpowered.server.item.PropertyProviders.toolType;
import static org.lanternpowered.server.item.PropertyProviders.useDuration;
import static org.lanternpowered.server.item.PropertyProviders.useLimit;
import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import org.lanternpowered.server.data.type.LanternDyeColor;
import org.lanternpowered.server.effect.potion.LanternPotionType;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.data.ArmorTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.DyeColorRegistryModule;
import org.lanternpowered.server.game.registry.type.data.MusicDiscRegistryModule;
import org.lanternpowered.server.game.registry.type.data.ToolTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.PotionEffectTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.item.inventory.equipment.EquipmentTypeRegistryModule;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.item.ItemTypeBuilder;
import org.lanternpowered.server.item.ItemTypeBuilderImpl;
import org.lanternpowered.server.item.LanternItemType;
import org.lanternpowered.server.item.behavior.vanilla.ArmorQuickEquipInteractionBehavior;
import org.lanternpowered.server.item.behavior.vanilla.ConsumableInteractionBehavior;
import org.lanternpowered.server.item.behavior.vanilla.OpenHeldBookBehavior;
import org.lanternpowered.server.item.behavior.vanilla.ShieldInteractionBehavior;
import org.lanternpowered.server.item.behavior.vanilla.WallOrStandingPlacementBehavior;
import org.lanternpowered.server.item.behavior.vanilla.consumable.MilkConsumer;
import org.lanternpowered.server.item.behavior.vanilla.consumable.PotionEffectsProvider;
import org.lanternpowered.server.network.item.NetworkItemTypeRegistry;
import org.lanternpowered.server.util.LazySupplier;
import org.lanternpowered.server.util.ReflectionHelper;
import org.lanternpowered.server.util.UncheckedThrowables;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.ArmorType;
import org.spongepowered.api.data.type.ArmorTypes;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.type.ToolType;
import org.spongepowered.api.data.type.ToolTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.effect.sound.music.MusicDisc;
import org.spongepowered.api.effect.sound.music.MusicDiscs;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.potion.PotionType;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

@RegistrationDependency({
        ArmorTypeRegistryModule.class,
        BlockRegistryModule.class,
        EquipmentTypeRegistryModule.class,
        ToolTypeRegistryModule.class,
        PotionEffectTypeRegistryModule.class,
        MusicDiscRegistryModule.class,
        EquipmentTypeRegistryModule.class,
        DyeColorRegistryModule.class,
})
public final class ItemRegistryModule extends AdditionalPluginCatalogRegistryModule<ItemType> {

    private static class Holder {

        private static final ItemRegistryModule INSTANCE = new ItemRegistryModule();
    }

    public static ItemRegistryModule get() {
        return Holder.INSTANCE;
    }

    private ItemRegistryModule() {
        super(ItemTypes.class);
    }

    @Override
    public <A extends ItemType> A register(A catalogType) {
        super.register(catalogType);
        NetworkItemTypeRegistry.register(catalogType);
        Lantern.getGame().getPropertyRegistry().registerItemPropertyStores(
                ((LanternItemType) catalogType).getPropertyProviderCollection());
        return catalogType;
    }

    @Override
    public void registerDefaults() {
        final LanternItemType none = builder().build("minecraft", "air");
        register(none);
        ///////////////////
        /// Iron Shovel ///
        ///////////////////
        register(toolBuilder(251, ToolTypes.IRON)
                .build("minecraft", "iron_shovel"));
        ////////////////////
        /// Iron Pickaxe ///
        ////////////////////
        register(toolBuilder(251, ToolTypes.IRON)
                .build("minecraft", "iron_pickaxe"));
        ////////////////////
        ///   Iron Axe   ///
        ////////////////////
        register(toolBuilder(251, ToolTypes.IRON)
                .build("minecraft", "iron_axe"));
        ///////////////////////
        /// Flint and Steel ///
        ///////////////////////
        register(durableBuilder(65)
                .build("minecraft", "flint_and_steel"));
        ///////////////////
        ///    Apple    ///
        ///////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(3))
                        .add(saturation(2.4)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "apple"));
        ///////////////////
        ///     Bow     ///
        ///////////////////
        register(durableBuilder(385)
                .properties(builder -> builder.add(maximumUseDuration(72000)))
                .build("minecraft", "bow"));
        ///////////////////
        ///    Arrow    ///
        ///////////////////
        register(builder()
                .build("minecraft", "arrow"));
        ///////////////////
        ///    Coal     ///
        ///////////////////
        register(builder()
                .build("minecraft", "coal"));
        ///////////////////////
        ///    Charcoal     ///
        ///////////////////////
        register(builder()
                .build("minecraft", "charcoal"));
        ///////////////////
        ///   Diamond   ///
        ///////////////////
        register(builder()
                .build("minecraft", "diamond"));
        ///////////////////
        ///  Iron Ingot ///
        ///////////////////
        register(builder()
                .build("minecraft", "iron_ingot"));
        ///////////////////
        ///  Gold Ingot ///
        ///////////////////
        register(builder()
                .build("minecraft", "gold_ingot"));
        ///////////////////
        ///  Iron Sword ///
        ///////////////////
        register(toolBuilder(251, ToolTypes.IRON)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "iron_sword"));
        ////////////////////
        /// Wooden Sword ///
        ////////////////////
        register(toolBuilder(60, ToolTypes.WOOD)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "wooden_sword"));
        /////////////////////
        /// Wooden Shovel ///
        /////////////////////
        register(toolBuilder(60, ToolTypes.WOOD)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "wooden_shovel"));
        //////////////////////
        /// Wooden Pickaxe ///
        //////////////////////
        register(toolBuilder(60, ToolTypes.WOOD)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "wooden_pickaxe"));
        //////////////////////
        ///   Wooden Axe   ///
        //////////////////////
        register(toolBuilder(60, ToolTypes.WOOD)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "wooden_axe"));
        ////////////////////
        ///  Stone Sword ///
        ////////////////////
        register(toolBuilder(132, ToolTypes.STONE)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "stone_sword"));
        /////////////////////
        ///  Stone Shovel ///
        /////////////////////
        register(toolBuilder(132, ToolTypes.STONE)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "stone_shovel"));
        //////////////////////
        ///  Stone Pickaxe ///
        //////////////////////
        register(toolBuilder(132, ToolTypes.STONE)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "stone_pickaxe"));
        //////////////////////
        ///    Stone Axe   ///
        //////////////////////
        register(toolBuilder(132, ToolTypes.STONE)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "stone_axe"));
        /////////////////////
        /// Diamond Sword ///
        /////////////////////
        register(toolBuilder(1562, ToolTypes.DIAMOND)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "diamond_sword"));
        //////////////////////
        /// Diamond Shovel ///
        //////////////////////
        register(toolBuilder(1562, ToolTypes.DIAMOND)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "diamond_shovel"));
        ///////////////////////
        /// Diamond Pickaxe ///
        ///////////////////////
        register(toolBuilder(1562, ToolTypes.DIAMOND)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "diamond_pickaxe"));
        ///////////////////////
        ///   Diamond Axe   ///
        ///////////////////////
        register(toolBuilder(1562, ToolTypes.DIAMOND)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "diamond_axe"));
        ///////////////////////
        ///      Stick      ///
        ///////////////////////
        register(builder()
                .build("minecraft", "stick"));
        //////////////////////
        ///      Bowl      ///
        //////////////////////
        register(builder()
                .build("minecraft", "bowl"));
        //////////////////////
        ///  Mushroom Stew ///
        //////////////////////
        register(builder()
                .maxStackQuantity(1)
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(6))
                        .add(saturation(7.2)))
                .behaviors(pipeline -> pipeline.add(
                        new ConsumableInteractionBehavior().restItem(() -> new LanternItemStack(ItemTypes.BOWL))))
                .build("minecraft", "mushroom_stew"));
        /////////////////////
        ///  Golden Sword ///
        /////////////////////
        register(toolBuilder(33, ToolTypes.GOLD)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "golden_sword"));
        //////////////////////
        ///  Golden Shovel ///
        //////////////////////
        register(toolBuilder(33, ToolTypes.GOLD)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "golden_shovel"));
        ///////////////////////
        ///  Golden Pickaxe ///
        ///////////////////////
        register(toolBuilder(33, ToolTypes.GOLD)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "golden_pickaxe"));
        ///////////////////////
        ///    Golden Axe   ///
        ///////////////////////
        register(toolBuilder(33, ToolTypes.GOLD)
                .properties(builder -> builder.add(dualWield(true)))
                .build("minecraft", "golden_axe"));
        ///////////////////////
        ///      String     ///
        ///////////////////////
        register(builder()
                .build("minecraft", "string"));
        ///////////////////////
        ///      Feather    ///
        ///////////////////////
        register(builder()
                .build("minecraft", "feather"));
        ///////////////////////
        ///     Gunpowder   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "gunpowder"));
        ///////////////////////
        ///    Wooden Hoe   ///
        ///////////////////////
        register(toolBuilder(60, ToolTypes.WOOD)
                .build("minecraft", "wooden_hoe"));
        ///////////////////////
        ///     Stone Hoe   ///
        ///////////////////////
        register(toolBuilder(132, ToolTypes.STONE)
                .build("minecraft", "stone_hoe"));
        ///////////////////////
        ///     Iron Hoe    ///
        ///////////////////////
        register(toolBuilder(251, ToolTypes.IRON)
                .build("minecraft", "iron_hoe"));
        ///////////////////////
        ///   Diamond Hoe   ///
        ///////////////////////
        register(toolBuilder(1562, ToolTypes.DIAMOND)
                .build("minecraft", "diamond_hoe"));
        ///////////////////////
        ///    Golden Hoe   ///
        ///////////////////////
        register(toolBuilder(33, ToolTypes.GOLD)
                .build("minecraft", "golden_hoe"));
        ///////////////////////
        ///   Wheat Seeds   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "wheat_seeds"));
        ///////////////////////
        ///      Wheat      ///
        ///////////////////////
        register(builder()
                .build("minecraft", "wheat"));
        ///////////////////////
        ///      Bread      ///
        ///////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(5))
                        .add(saturation(6.0)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "bread"));
        //////////////////////////
        ///   Leather Helmet   ///
        //////////////////////////
        register(leatherArmorBuilder(56, EquipmentTypes.HEADWEAR)
                .build("minecraft", "leather_helmet"));
        //////////////////////////
        /// Leather Chestplate ///
        //////////////////////////
        register(leatherArmorBuilder(81, EquipmentTypes.CHESTPLATE)
                .build("minecraft", "leather_chestplate"));
        //////////////////////////
        ///  Leather Leggings  ///
        //////////////////////////
        register(leatherArmorBuilder(76, EquipmentTypes.LEGGINGS)
                .build("minecraft", "leather_leggings"));
        //////////////////////////
        ///    Leather Boots   ///
        //////////////////////////
        register(leatherArmorBuilder(66, EquipmentTypes.BOOTS)
                .build("minecraft", "leather_boots"));
        ////////////////////////////
        ///   Chainmail Helmet   ///
        ////////////////////////////
        register(armorBuilder(166, ArmorTypes.CHAIN, EquipmentTypes.HEADWEAR)
                .build("minecraft", "chainmail_helmet"));
        ////////////////////////////
        /// Chainmail Chestplate ///
        ////////////////////////////
        register(armorBuilder(241, ArmorTypes.CHAIN, EquipmentTypes.CHESTPLATE)
                .build("minecraft", "chainmail_chestplate"));
        ////////////////////////////
        ///  Chainmail Leggings  ///
        ////////////////////////////
        register(armorBuilder(226, ArmorTypes.CHAIN, EquipmentTypes.LEGGINGS)
                .build("minecraft", "chainmail_leggings"));
        ////////////////////////////
        ///    Chainmail Boots   ///
        ////////////////////////////
        register(armorBuilder(196, ArmorTypes.CHAIN, EquipmentTypes.BOOTS)
                .build("minecraft", "chainmail_boots"));
        ///////////////////////
        ///   Iron Helmet   ///
        ///////////////////////
        register(armorBuilder(166, ArmorTypes.IRON, EquipmentTypes.HEADWEAR)
                .build("minecraft", "iron_helmet"));
        ///////////////////////
        /// Iron Chestplate ///
        ///////////////////////
        register(armorBuilder(241, ArmorTypes.IRON, EquipmentTypes.CHESTPLATE)
                .build("minecraft", "iron_chestplate"));
        ///////////////////////
        ///  Iron Leggings  ///
        ///////////////////////
        register(armorBuilder(226, ArmorTypes.IRON, EquipmentTypes.LEGGINGS)
                .build("minecraft", "iron_leggings"));
        ///////////////////////
        ///    Iron Boots   ///
        ///////////////////////
        register(armorBuilder(196, ArmorTypes.IRON, EquipmentTypes.BOOTS)
                .build("minecraft", "iron_boots"));
        //////////////////////////
        ///   Diamond Helmet   ///
        //////////////////////////
        register(armorBuilder(364, ArmorTypes.DIAMOND, EquipmentTypes.HEADWEAR)
                .build("minecraft", "diamond_helmet"));
        //////////////////////////
        /// Diamond Chestplate ///
        //////////////////////////
        register(armorBuilder(529, ArmorTypes.DIAMOND, EquipmentTypes.CHESTPLATE)
                .build("minecraft", "diamond_chestplate"));
        //////////////////////////
        ///  Diamond Leggings  ///
        //////////////////////////
        register(armorBuilder(496, ArmorTypes.DIAMOND, EquipmentTypes.LEGGINGS)
                .build("minecraft", "diamond_leggings"));
        //////////////////////////
        ///    Diamond Boots   ///
        //////////////////////////
        register(armorBuilder(430, ArmorTypes.DIAMOND, EquipmentTypes.BOOTS)
                .build("minecraft", "diamond_boots"));
        /////////////////////////
        ///   Golden Helmet   ///
        /////////////////////////
        register(armorBuilder(78, ArmorTypes.GOLD, EquipmentTypes.HEADWEAR)
                .build("minecraft", "golden_helmet"));
        /////////////////////////
        /// Golden Chestplate ///
        /////////////////////////
        register(armorBuilder(113, ArmorTypes.GOLD, EquipmentTypes.CHESTPLATE)
                .build("minecraft", "golden_chestplate"));
        /////////////////////////
        ///  Golden Leggings  ///
        /////////////////////////
        register(armorBuilder(76, ArmorTypes.GOLD, EquipmentTypes.LEGGINGS)
                .build("minecraft", "golden_leggings"));
        /////////////////////////
        ///    Golden Boots   ///
        /////////////////////////
        register(armorBuilder(66, ArmorTypes.GOLD, EquipmentTypes.BOOTS)
                .build("minecraft", "golden_boots"));
        ///////////////////////
        ///      Flint      ///
        ///////////////////////
        register(builder()
                .build("minecraft", "flint"));
        ///////////////////////
        ///     Porkchop    ///
        ///////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(3))
                        .add(saturation(0.3)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "porkchop"));
        /////////////////////////
        ///  Cooked Porkchop  ///
        /////////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(8))
                        .add(saturation(12.8)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "cooked_porkchop"));
        /////////////////////////
        ///      Painting     ///
        /////////////////////////
        register(builder()
                .build("minecraft", "painting"));
        ////////////////////
        /// Golden Apple ///
        ////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(4))
                        .add(saturation(9.6))
                        .add(applicableEffects(
                                PotionEffect.of(PotionEffectTypes.REGENERATION, 1, 100),
                                PotionEffect.of(PotionEffectTypes.ABSORPTION, 0, 2400)))
                        .add(alwaysConsumable(true)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "golden_apple"));
        //////////////////////////////
        /// Enchanted Golden Apple ///
        //////////////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(4))
                        .add(saturation(9.6))
                        .add(applicableEffects(
                                PotionEffect.of(PotionEffectTypes.REGENERATION, 1, 400),
                                PotionEffect.of(PotionEffectTypes.RESISTANCE, 0, 6000),
                                PotionEffect.of(PotionEffectTypes.FIRE_RESISTANCE, 0, 6000),
                                PotionEffect.of(PotionEffectTypes.ABSORPTION, 3, 2400)))
                        .add(alwaysConsumable(true)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "enchanted_golden_apple"));
        /////////////////////
        ///      Sign     ///
        /////////////////////
        register(builder()
                .maxStackQuantity(16)
                .behaviors(pipeline -> pipeline
                        .add(WallOrStandingPlacementBehavior.ofTypes(
                                () -> BlockTypes.OAK_WALL_SIGN,
                                () -> BlockTypes.OAK_SIGN)))
                .build("minecraft", "oak_sign"));
        ////////////////////
        ///   Oak Door   ///
        ////////////////////
        register(builder()
                .build("minecraft", "oak_door"));
        /////////////////////
        ///     Bucket    ///
        /////////////////////
        register(builder()
                .maxStackQuantity(16)
                .build("minecraft", "bucket"));
        ////////////////////////
        ///   Water Bucket   ///
        ////////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "water_bucket"));
        ///////////////////////
        ///   Lava Bucket   ///
        ///////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "lava_bucket"));
        ////////////////////
        ///   Minecart   ///
        ////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "minecart"));
        //////////////////
        ///   Saddle   ///
        //////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "saddle"));
        /////////////////////
        ///   Iron Door   ///
        /////////////////////
        register(builder()
                .build("minecraft", "iron_door"));
        ////////////////////
        ///   Redstone   ///
        ////////////////////
        register(builder()
                .build("minecraft", "redstone"));
        ////////////////////
        ///   Snowball   ///
        ////////////////////
        register(builder()
                .maxStackQuantity(16)
                .build("minecraft", "snowball"));
        ////////////////////
        ///   Oak Boat   ///
        ////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "oak_boat"));
        ///////////////////
        ///   Leather   ///
        ///////////////////
        register(builder()
                .build("minecraft", "leather"));
        ///////////////////////
        ///   Milk Bucket   ///
        ///////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(alwaysConsumable(true)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior().consumer(new MilkConsumer())
                                .restItem(() -> new LanternItemStack(ItemTypes.BUCKET))))
                .maxStackQuantity(1)
                .build("minecraft", "milk_bucket"));
        /////////////////
        ///   Brick   ///
        /////////////////
        register(builder()
                .build("minecraft", "brick"));
        /////////////////////
        ///   Clay Ball   ///
        /////////////////////
        register(builder()
                .build("minecraft", "clay_ball"));
        /////////////////
        ///   Paper   ///
        /////////////////
        register(builder()
                .build("minecraft", "paper"));
        ////////////////
        ///   Book   ///
        ////////////////
        register(builder()
                .build("minecraft", "book"));
        //////////////////////
        ///   Slime Ball   ///
        //////////////////////
        register(builder()
                .build("minecraft", "slime_ball"));
        //////////////////////////
        ///   Chest Minecart   ///
        //////////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "chest_minecart"));
        ////////////////////////////
        ///   Furnace Minecart   ///
        ////////////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "furnace_minecart"));
        ///////////////
        ///   Egg   ///
        ///////////////
        register(builder()
                .maxStackQuantity(16)
                .build("minecraft", "egg"));
        ///////////////////
        ///   Compass   ///
        ///////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "compass"));
        ///////////////////////
        ///   Fishing Rod   ///
        ///////////////////////
        register(durableBuilder(65)
                .build("minecraft", "fishing_rod"));
        /////////////////
        ///   Clock   ///
        /////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "clock"));
        //////////////////////////
        ///   Glowstone Dust   ///
        //////////////////////////
        register(builder()
                .build("minecraft", "glowstone_dust"));
        ///////////////
        ///   Cod   ///
        ///////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(2))
                        .add(saturation(0.4)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "cod"));
        //////////////////
        ///   Salmon   ///
        //////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(2))
                        .add(saturation(0.4)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "salmon"));
        /////////////////////////
        ///   Tropical fish   ///
        /////////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(1))
                        .add(saturation(0.2)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "tropical_fish"));
        //////////////////////
        ///   Pufferfish   ///
        //////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(1))
                        .add(saturation(0.2))
                        .add(applicableEffects(
                                PotionEffect.of(PotionEffectTypes.POISON, 3, 1200),
                                PotionEffect.of(PotionEffectTypes.HUNGER, 2, 300),
                                PotionEffect.of(PotionEffectTypes.NAUSEA, 1, 300))))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "pufferfish"));
        //////////////////////
        ///   Cooked Cod   ///
        //////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(5))
                        .add(saturation(6.0)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "cooked_cod"));
        /////////////////////////
        ///   Cooked Salmon   ///
        /////////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(6))
                        .add(saturation(9.6)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "cooked_salmon"));
        ///////////////////
        ///   Ink Sac   ///
        ///////////////////
        register(builder()
                .build("minecraft", "ink_sac"));
        ////////////////////////
        ///   Lapis Lazuli   ///
        ////////////////////////
        register(builder()
                .build("minecraft", "lapis_lazuli"));
        ///////////////////////
        ///   Cocoa Beans   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "cocoa_beans"));
        /////////////////////
        ///   Black Dye   ///
        /////////////////////
        register(builder()
                .build("minecraft", "black_dye"));
        ////////////////////
        ///   Red Dye   ///
        ////////////////////
        register(builder()
                .build("minecraft", "red_dye"));
        ////////////////////
        ///   Green Dye  ///
        ////////////////////
        register(builder()
                .build("minecraft", "green_dye"));
        /////////////////////
        ///   Brown Dye   ///
        /////////////////////
        register(builder()
                .build("minecraft", "brown_dye"));
        ////////////////////
        ///   Blue Dye   ///
        ////////////////////
        register(builder()
                .build("minecraft", "blue_dye"));
        //////////////////////
        ///   Purple Dye   ///
        //////////////////////
        register(builder()
                .build("minecraft", "purple_dye"));
        ////////////////////
        ///   Cyan Dye   ///
        ////////////////////
        register(builder()
                .build("minecraft", "cyan_dye"));
        //////////////////////////
        ///   Light Gray Dye   ///
        //////////////////////////
        register(builder()
                .build("minecraft", "light_gray_dye"));
        ////////////////////
        ///   Gray Dye   ///
        ////////////////////
        register(builder()
                .build("minecraft", "gray_dye"));
        ////////////////////
        ///   Pink Dye   ///
        ////////////////////
        register(builder()
                .build("minecraft", "pink_dye"));
        ////////////////////
        ///   Lime Dye   ///
        ////////////////////
        register(builder()
                .build("minecraft", "lime_dye"));
        //////////////////////
        ///   Yellow Dye   ///
        //////////////////////
        register(builder()
                .build("minecraft", "yellow_dye"));
        //////////////////////////
        ///   Light Blue Dye   ///
        //////////////////////////
        register(builder()
                .build("minecraft", "light_blue_dye"));
        ///////////////////////
        ///   Magenta Dye   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "magenta_dye"));
        //////////////////////
        ///   Orange Dye   ///
        //////////////////////
        register(builder()
                .build("minecraft", "orange_dye"));
        /////////////////////
        ///   White Dye   ///
        /////////////////////
        register(builder()
                .build("minecraft", "white_dye"));
        /////////////////////
        ///   Bone Meal   ///
        /////////////////////
        register(builder()
                .build("minecraft", "bone_meal"));
        ////////////////
        ///   Bone   ///
        ////////////////
        register(builder()
                .build("minecraft", "bone"));
        /////////////////
        ///   Sugar   ///
        /////////////////
        register(builder()
                .build("minecraft", "sugar"));
        ////////////////
        ///   Cake   ///
        ////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "cake"));
        ////////////////////
        ///   Repeater   ///
        ////////////////////
        register(builder()
                .build("minecraft", "repeater"));
        //////////////////
        ///   Cookie   ///
        //////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(2))
                        .add(saturation(0.4)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "cookie"));
        //////////////////////
        ///   Filled Map   ///
        //////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "filled_map"));
        //////////////////
        ///   Shears   ///
        //////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "shears"));
        ///////////////////////
        ///   Melon Slice   ///
        ///////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(2))
                        .add(saturation(1.2)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "melon_slice"));
        /////////////////////////
        ///   Pumpkin Seeds   ///
        /////////////////////////
        register(builder()
                .build("minecraft", "pumpkin_seeds"));
        ///////////////////////
        ///   Melon Seeds   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "melon_seeds"));
        ////////////////
        ///   Beef   ///
        ////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(3))
                        .add(saturation(1.8)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "beef"));
        ///////////////////////
        ///   Cooked Beef   ///
        ///////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(8))
                        .add(saturation(12.8)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "cooked_beef"));
        ///////////////////
        ///   Chicken   ///
        ///////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(2))
                        .add(saturation(1.2))
                        .add(applicableEffects(PotionEffect.of(PotionEffectTypes.HUNGER, 0, 600))))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "chicken"));
        //////////////////////////
        ///   Cooked Chicken   ///
        //////////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(6))
                        .add(saturation(7.2)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "cooked_chicken"));
        ////////////////////////
        ///   Rotten Flesh   ///
        ////////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(4))
                        .add(saturation(0.8))
                        .add(applicableEffects(PotionEffect.of(PotionEffectTypes.HUNGER, 0, 600))))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "rotten_flesh"));
        ///////////////////////
        ///   Ender Pearl   ///
        ///////////////////////
        register(builder()
                .maxStackQuantity(16)
                .build("minecraft", "ender_pearl"));
        /////////////////////
        ///   Blaze Rod   ///
        /////////////////////
        register(builder()
                .build("minecraft", "blaze_rod"));
        //////////////////////
        ///   Ghast Tear   ///
        //////////////////////
        register(builder()
                .build("minecraft", "ghast_tear"));
        ///////////////////////
        ///   Gold Nugget   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "gold_nugget"));
        ///////////////////////
        ///   Nether Wart   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "nether_wart"));
        //////////////////
        ///   Potion   ///
        //////////////////
        register(potionEffectsBuilder(LanternPotionType::getTranslation)
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(applicableEffects(new PotionEffectsProvider()))
                        .add(alwaysConsumable(true)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()
                                .restItem(() -> new LanternItemStack(ItemTypes.GLASS_BOTTLE))))
                .maxStackQuantity(1)
                .build("minecraft", "potion"));
        ////////////////////////
        ///   Glass Bottle   ///
        ////////////////////////
        register(builder()
                .build("minecraft", "glass_bottle"));
        //////////////////////
        ///   Spider Eye   ///
        //////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(2))
                        .add(saturation(3.2))
                        .add(applicableEffects(PotionEffect.of(PotionEffectTypes.POISON, 0, 100))))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "spider_eye"));
        ////////////////////////////////
        ///   Fermented Spider Eye   ///
        ////////////////////////////////
        register(builder()
                .build("minecraft", "fermented_spider_eye"));
        ////////////////////////
        ///   Blaze Powder   ///
        ////////////////////////
        register(builder()
                .build("minecraft", "blaze_powder"));
        ///////////////////////
        ///   Magma Cream   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "magma_cream"));
        /////////////////////////
        ///   Brewing Stand   ///
        /////////////////////////
        register(builder()
                .build("minecraft", "brewing_stand"));
        ////////////////////
        ///   Cauldron   ///
        ////////////////////
        register(builder()
                .build("minecraft", "cauldron"));
        /////////////////////
        ///   Ender Eye   ///
        /////////////////////
        register(builder()
                .build("minecraft", "ender_eye"));
        //////////////////////////////////
        ///   Glistering Melon Slice   ///
        //////////////////////////////////
        register(builder()
                .build("minecraft", "glistering_melon_slice"));
        /*
        /////////////////////
        ///   Spawn Egg   ///
        /////////////////////
        register(builder()
                .build("minecraft", "spawn_egg"));
                */
        /////////////////////////////
        ///   Experience Bottle   ///
        /////////////////////////////
        register(builder()
                .build("minecraft", "experience_bottle"));
        ///////////////////////
        ///   Fire Charge   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "fire_charge"));
        /////////////////////////
        ///   Writable Book   ///
        /////////////////////////
        register(builder()
                .maxStackQuantity(1)
                .keysProvider(c -> c
                        .register(Keys.PLAIN_BOOK_PAGES, null))
                .build("minecraft", "writable_book"));
        ////////////////////////
        ///   Written Book   ///
        ////////////////////////
        register(builder()
                .maxStackQuantity(1)
                .keysProvider(c -> {
                    c.register(Keys.BOOK_PAGES, null);
                    c.register(Keys.BOOK_AUTHOR, null);
                    c.register(Keys.GENERATION, null);
                })
                .behaviors(pipeline -> pipeline
                        .add(new OpenHeldBookBehavior()))
                .build("minecraft", "written_book"));
        ///////////////////
        ///   Emerald   ///
        ///////////////////
        register(builder()
                .build("minecraft", "emerald"));
        //////////////////////
        ///   Item Frame   ///
        //////////////////////
        register(builder()
                .build("minecraft", "item_frame"));
        //////////////////////
        ///   Flower Pot   ///
        //////////////////////
        register(builder()
                .build("minecraft", "flower_pot"));
        //////////////////
        ///   Carrot   ///
        //////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(3))
                        .add(saturation(3.6)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "carrot"));
        //////////////////
        ///   Potato   ///
        //////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(1))
                        .add(saturation(0.6)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "potato"));
        ////////////////////////
        ///   Baked Potato   ///
        ////////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(5))
                        .add(saturation(6.0)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "baked_potato"));
        ////////////////////////////
        ///   Poisonous Potato   ///
        ////////////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(2))
                        .add(saturation(1.2))
                        .add(applicableEffects(PotionEffect.of(PotionEffectTypes.POISON, 0, 100))))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "poisonous_potato"));
        ///////////////
        ///   Map   ///
        ///////////////
        register(builder()
                .build("minecraft", "map"));
        /////////////////////////
        ///   Golden Carrot   ///
        /////////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(6))
                        .add(saturation(14.4)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "golden_carrot"));
        //////////////////////////
        ///   Skeleton Skull   ///
        //////////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(equipmentType(EquipmentTypes.HEADWEAR)))
                .build("minecraft", "skeleton_skull"));
        /////////////////////////////////
        ///   Wither Skeleton Skull   ///
        /////////////////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(equipmentType(EquipmentTypes.HEADWEAR)))
                .build("minecraft", "wither_skeleton_skull"));
        ///////////////////////
        ///   Zombie Head   ///
        ///////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(equipmentType(EquipmentTypes.HEADWEAR)))
                .build("minecraft", "zombie_head"));
        ///////////////////////
        ///   Player Head   ///
        ///////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(equipmentType(EquipmentTypes.HEADWEAR)))
                .build("minecraft", "player_head"));
        ////////////////////////
        ///   Creeper Head   ///
        ////////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(equipmentType(EquipmentTypes.HEADWEAR)))
                .build("minecraft", "creeper_head"));
        ///////////////////////
        ///   Dragon Head   ///
        ///////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(equipmentType(EquipmentTypes.HEADWEAR)))
                .build("minecraft", "dragon_head"));
        /////////////////////////////
        ///   Carrot On A Stick   ///
        /////////////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "carrot_on_a_stick"));
        ///////////////////////
        ///   Nether Star   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "nether_star"));
        ///////////////////////
        ///   Pumpkin Pie   ///
        ///////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(8))
                        .add(saturation(4.8)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "pumpkin_pie"));
        //////////////////////////
        ///  Firework Rocket   ///
        //////////////////////////
        register(builder()
                .keysProvider(c -> {
                    c.register(Keys.FIREWORK_EFFECTS, Collections.emptyList());
                    c.register(Keys.FIREWORK_FLIGHT_MODIFIER, 1);
                })
                .build("minecraft", "firework_rocket"));
        /////////////////////
        /// Firework Star ///
        /////////////////////
        register(builder()
                .keysProvider(c -> c
                        .register(Keys.FIREWORK_EFFECTS, Collections.emptyList())
                )
                .maxStackQuantity(1)
                .build("minecraft", "firework_star"));
        //////////////////////////
        ///   Enchanted Book   ///
        //////////////////////////
        register(builder()
                .keysProvider(c -> c
                        .register(Keys.STORED_ENCHANTMENTS, null))
                .maxStackQuantity(1)
                .build("minecraft", "enchanted_book"));
        //////////////////////
        ///   Comparator   ///
        //////////////////////
        register(builder()
                .build("minecraft", "comparator"));
        ////////////////////////
        ///   Nether Brick   ///
        ////////////////////////
        register(builder()
                .build("minecraft", "nether_brick"));
        //////////////////
        ///   Quartz   ///
        //////////////////
        register(builder()
                .build("minecraft", "quartz"));
        ////////////////////////
        ///   TNT Minecart   ///
        ////////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "tnt_minecart"));
        ///////////////////////////
        ///   Hopper Minecart   ///
        ///////////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "hopper_minecart"));
        ////////////////////////////
        ///   Prismarine Shard   ///
        ////////////////////////////
        register(builder()
                .build("minecraft", "prismarine_shard"));
        ///////////////////////////////
        ///   Prismarine Crystals   ///
        ///////////////////////////////
        register(builder()
                .build("minecraft", "prismarine_crystals"));
        //////////////////
        ///   Rabbit   ///
        //////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(3))
                        .add(saturation(1.8)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "rabbit"));
        /////////////////////////
        ///   Cooked Rabbit   ///
        /////////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(5))
                        .add(saturation(6.0)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "cooked_rabbit"));
        ///////////////////////
        ///   Rabbit Stew   ///
        ///////////////////////
        register(builder()
                .maxStackQuantity(1)
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(10))
                        .add(saturation(12.0)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()
                                .restItem(() -> new LanternItemStack(ItemTypes.BOWL))))
                .build("minecraft", "rabbit_stew"));
        ///////////////////////
        ///   Rabbit Foot   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "rabbit_foot"));
        ///////////////////////
        ///   Rabbit Hide   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "rabbit_hide"));
        ///////////////////////
        ///   Armor Stand   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "armor_stand"));
        ////////////////////////////
        ///   Iron Horse Armor   ///
        ////////////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "iron_horse_armor"));
        //////////////////////////////
        ///   Golden Horse Armor   ///
        //////////////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "golden_horse_armor"));
        ///////////////////////////////
        ///   Diamond Horse Armor   ///
        ///////////////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "diamond_horse_armor"));
        ////////////////
        ///   Lead   ///
        ////////////////
        register(builder()
                .build("minecraft", "lead"));
        ////////////////////
        ///   Name Tag   ///
        ////////////////////
        register(builder()
                .build("minecraft", "name_tag"));
        //////////////////////////////////
        ///   Command Block Minecart   ///
        //////////////////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "command_block_minecart"));
        //////////////////
        ///   Mutton   ///
        //////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(2))
                        .add(saturation(1.2)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "mutton"));
        /////////////////////////
        ///   Cooked Mutton   ///
        /////////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(6))
                        .add(saturation(9.6)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "cooked_mutton"));
        //////////////////
        ///   Banners  ///
        //////////////////
        for (LanternDyeColor dyeColor : LanternDyeColor.values()) {
            final BlockRegistryModule blockRegistry = BlockRegistryModule.get();
            final String colorName = dyeColor.getKey().getValue();
            register(bannerBuilder(
                    LazySupplier.of(() -> blockRegistry.get(CatalogKey.minecraft(colorName + "_wall_banner")).get()),
                    LazySupplier.of(() -> blockRegistry.get(CatalogKey.minecraft(colorName + "_banner")).get())
            ).build("minecraft", colorName + "_banner"));
        }
        ///////////////////////
        ///   End Crystal   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "end_crystal"));
        ///////////////////////
        ///   Spruce Door   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "spruce_door"));
        //////////////////////
        ///   Bitch Door   ///
        //////////////////////
        register(builder()
                .build("minecraft", "birch_door"));
        ///////////////////////
        ///   Jungle Door   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "jungle_door"));
        ///////////////////////
        ///   Acacia Door   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "acacia_door"));
        /////////////////////////
        ///   Dark Oak Door   ///
        /////////////////////////
        register(builder()
                .build("minecraft", "dark_oak_door"));
        ////////////////////////
        ///   Chorus Fruit   ///
        ////////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(4))
                        .add(saturation(2.4))
                        .add(alwaysConsumable(true)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                // TODO: Add random teleport consumer behavior
                .build("minecraft", "chorus_fruit"));
        ///////////////////////////////
        ///   Chorus Fruit Popped   ///
        ///////////////////////////////
        register(builder()
                .build("minecraft", "popped_chorus_fruit"));
        ////////////////////
        ///   Beetroot   ///
        ////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(1))
                        .add(saturation(1.2)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()))
                .build("minecraft", "beetroot"));
        //////////////////////////
        ///   Beetroot Seeds   ///
        //////////////////////////
        register(builder()
                .build("minecraft", "beetroot_seeds"));
        /////////////////////////
        ///   Beetroot Soup   ///
        /////////////////////////
        register(builder()
                .properties(builder -> builder
                        .add(useDuration(32))
                        .add(replenishedFood(6))
                        .add(saturation(7.2)))
                .behaviors(pipeline -> pipeline
                        .add(new ConsumableInteractionBehavior()
                                .restItem(() -> new LanternItemStack(ItemTypes.BOWL))))
                .maxStackQuantity(1)
                .build("minecraft", "beetroot_soup"));
        /////////////////////////
        ///   Dragon Breath   ///
        /////////////////////////
        register(builder()
                .build("minecraft", "dragon_breath"));
        /////////////////////////
        ///   Splash Potion   ///
        /////////////////////////
        register(potionEffectsBuilder(LanternPotionType::getSplashTranslation)
                .maxStackQuantity(1)
                .build("minecraft", "splash_potion"));
        //////////////////////////
        ///   Spectral Arrow   ///
        //////////////////////////
        register(builder()
                .build("minecraft", "spectral_arrow"));
        ////////////////////////
        ///   Tipped Arrow   ///
        ////////////////////////
        register(potionEffectsBuilder(LanternPotionType::getTippedArrowTranslation)
                .build("minecraft", "tipped_arrow"));
        ////////////////////////////
        ///   Lingering Potion   ///
        ////////////////////////////
        register(potionEffectsBuilder(LanternPotionType::getLingeringTranslation)
                .maxStackQuantity(1)
                .build("minecraft", "lingering_potion"));
        //////////////////
        ///   Shield   ///
        //////////////////
        register(durableBuilder(336)
                .behaviors(pipeline -> pipeline
                        .add(new ShieldInteractionBehavior()))
                .build("minecraft", "shield"));
        //////////////////
        ///   Elytra   ///
        //////////////////
        register(durableBuilder(432)
                .properties(builder -> builder
                        .add(equipmentType(EquipmentTypes.CHESTPLATE)))
                .behaviors(pipeline -> pipeline
                        .add(new ArmorQuickEquipInteractionBehavior()))
                .build("minecraft", "elytra"));
        ///////////////////////
        ///   Spruce Boat   ///
        ///////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "spruce_boat"));
        //////////////////////
        ///   Birch Boat   ///
        //////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "birch_boat"));
        ///////////////////////
        ///   Jungle Boat   ///
        ///////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "jungle_boat"));
        ///////////////////////
        ///   Acacia Boat   ///
        ///////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "acacia_boat"));
        /////////////////////////
        ///   Dark Oak Boat   ///
        /////////////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "dark_oak_boat"));
        /////////////////
        ///   Totem   ///
        /////////////////
        register(builder()
                .maxStackQuantity(1)
                .build("minecraft", "totem_of_undying"));
        /////////////////////////
        ///   Shulker Shell   ///
        /////////////////////////
        register(builder()
                .build("minecraft", "shulker_shell"));
        ///////////////////////
        ///   Iron Nugget   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "iron_nugget"));
        //////////////////////////
        ///   Knowledge Book   ///
        //////////////////////////
        register(builder()
                .build("minecraft", "knowledge_book"));
        /////////////////////////
        ///   Music Disc 13   ///
        /////////////////////////
        register(musicDiscBuilder(MusicDiscs.THIRTEEN)
                .build("minecraft", "music_disc_13"));
        //////////////////////////
        ///   Music Disc Cat   ///
        //////////////////////////
        register(musicDiscBuilder(MusicDiscs.CAT)
                .build("minecraft", "music_disc_cat"));
        /////////////////////////////
        ///   Music Disc Blocks   ///
        /////////////////////////////
        register(musicDiscBuilder(MusicDiscs.BLOCKS)
                .build("minecraft", "music_disc_blocks"));
        ////////////////////////////
        ///   Music Disc Chirp   ///
        ////////////////////////////
        register(musicDiscBuilder(MusicDiscs.CHIRP)
                .build("minecraft", "music_disc_chirp"));
        //////////////////////////
        ///   Music Disc Far   ///
        //////////////////////////
        register(musicDiscBuilder(MusicDiscs.FAR)
                .build("minecraft", "music_disc_far"));
        ///////////////////////////
        ///   Music Disc Mall   ///
        ///////////////////////////
        register(musicDiscBuilder(MusicDiscs.MALL)
                .build("minecraft", "music_disc_mall"));
        //////////////////////////////
        ///   Music Disc Mellohi   ///
        //////////////////////////////
        register(musicDiscBuilder(MusicDiscs.MELLOHI)
                .build("minecraft", "music_disc_mellohi"));
        ///////////////////////////
        ///   Music Disc Stal   ///
        ///////////////////////////
        register(musicDiscBuilder(MusicDiscs.STAL)
                .build("minecraft", "music_disc_stal"));
        ////////////////////////////
        ///   Music Disc Strad   ///
        ////////////////////////////
        register(musicDiscBuilder(MusicDiscs.STRAD)
                .build("minecraft", "music_disc_strad"));
        ///////////////////////////
        ///   Music Disc Ward   ///
        ///////////////////////////
        register(musicDiscBuilder(MusicDiscs.WARD)
                .build("minecraft", "music_disc_ward"));
        /////////////////////////
        ///   Music Disc 11   ///
        /////////////////////////
        register(musicDiscBuilder(MusicDiscs.ELEVEN)
                .build("minecraft", "music_disc_11"));
        ///////////////////////////
        ///   Music Disc Wait   ///
        ///////////////////////////
        register(musicDiscBuilder(MusicDiscs.WAIT)
                .build("minecraft", "music_disc_wait"));
        ///////////////////////
        ///   Debug Stick   ///
        ///////////////////////
        register(builder()
                .build("minecraft", "debug_stick"));

        // Initialize empty stacks
        final LanternItemStack emptyStack = new LanternItemStack(none, 0);
        final ItemStackSnapshot emptySnapshot = emptyStack.toWrappedSnapshot();

        try {
            ReflectionHelper.setField(LanternItemStack.class.getDeclaredField("empty"), null, emptyStack);
            ReflectionHelper.setField(ItemStackSnapshot.class.getField("NONE"), null, emptySnapshot);
        } catch (Throwable t) {
            throw UncheckedThrowables.throwUnchecked(t);
        }
    }

    private ItemTypeBuilder potionEffectsBuilder(Function<LanternPotionType, Translation> translationFunction) {
        return builder()
                .translation((itemType, itemStack) -> {
                    if (itemStack != null) {
                        final PotionType potionType = itemStack.get(Keys.POTION_TYPE).orElse(null);
                        if (potionType != null) {
                            return translationFunction.apply((LanternPotionType) potionType);
                        }
                    }
                    return tr("item.potion.name");
                })
                .keysProvider(c -> {
                    c.register(Keys.COLOR, null);
                    c.register(Keys.POTION_EFFECTS, null);
                    c.register(Keys.POTION_TYPE, null);
                });
    }

    private void registerBanner(String namespace, String color) {
        register(
                bannerBuilder(
                        LazySupplier.of(() -> BlockRegistryModule.get().get(CatalogKey.of(namespace, color + "_wall_banner")).get()),
                        LazySupplier.of(() -> BlockRegistryModule.get().get(CatalogKey.of(namespace, color + "_banner")).get()))
                        .build(namespace, color + "_banner"));
    }

    private ItemTypeBuilder bannerBuilder(Supplier<BlockType> wallTypeSupplier, Supplier<BlockType> standingTypeSupplier) {
        return builder()
                .maxStackQuantity(16)
                .keysProvider(c -> {
                    c.register(Keys.BANNER_BASE_COLOR, DyeColors.WHITE);
                    c.register(Keys.BANNER_PATTERNS, new ArrayList<>());
                })
                .behaviors(pipeline -> pipeline
                        .add(WallOrStandingPlacementBehavior.ofTypes(wallTypeSupplier, standingTypeSupplier)));
    }

    private ItemTypeBuilder musicDiscBuilder(MusicDisc musicDisc) {
        return builder()
                .maxStackQuantity(1)
                .properties(builder -> builder
                        .add(musicDisc(musicDisc)));
    }

    private ItemTypeBuilder durableBuilder(int useLimit) {
        return builder()
                .maxStackQuantity(1)
                .properties(builder -> builder
                        .add(useLimit(useLimit)))
                .keysProvider(c -> {
                    c.register(Keys.ITEM_DURABILITY, 0);
                    c.register(Keys.UNBREAKABLE, true); // TODO: True until durability is implemented
                });
    }

    private ItemTypeBuilder toolBuilder(int useLimit, ToolType toolType) {
        return durableBuilder(useLimit)
                .properties(builder -> builder
                        .add(toolType(toolType)));
    }

    private ItemTypeBuilder leatherArmorBuilder(int useLimit, EquipmentType equipmentType) {
        return armorBuilder(useLimit, ArmorTypes.LEATHER, equipmentType)
                .keysProvider(c -> c
                        .register(Keys.COLOR, null));
    }

    private ItemTypeBuilder armorBuilder(int useLimit, ArmorType armorType, EquipmentType equipmentType) {
        return durableBuilder(useLimit)
                .properties(builder -> builder
                        .add(armorType(armorType))
                        .add(equipmentType(equipmentType)))
                .behaviors(pipeline -> pipeline
                        .add(new ArmorQuickEquipInteractionBehavior()));
    }

    private ItemTypeBuilder builder() {
        return new ItemTypeBuilderImpl();
    }
}
