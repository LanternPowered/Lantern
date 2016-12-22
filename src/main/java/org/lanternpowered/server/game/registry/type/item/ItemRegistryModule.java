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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.item.PropertyProviders.armorType;
import static org.lanternpowered.server.item.PropertyProviders.equipmentType;
import static org.lanternpowered.server.item.PropertyProviders.toolType;
import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.property.LanternPropertyRegistry;
import org.lanternpowered.server.data.type.LanternDyeColor;
import org.lanternpowered.server.effect.potion.PotionType;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.data.ArmorTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.CookedFishRegistryModule;
import org.lanternpowered.server.game.registry.type.data.FishRegistryModule;
import org.lanternpowered.server.game.registry.type.data.ToolTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.item.inventory.equipment.EquipmentTypeRegistryModule;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.item.ItemTypeBuilder;
import org.lanternpowered.server.item.ItemTypeBuilderImpl;
import org.lanternpowered.server.item.LanternItemType;
import org.lanternpowered.server.item.TranslationProvider;
import org.lanternpowered.server.item.behavior.vanilla.ArmorQuickEquipInteractionBehavior;
import org.lanternpowered.server.item.behavior.vanilla.OpenHeldBookBehavior;
import org.lanternpowered.server.util.ReflectionHelper;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.ArmorType;
import org.spongepowered.api.data.type.ArmorTypes;
import org.spongepowered.api.data.type.CoalTypes;
import org.spongepowered.api.data.type.CookedFishes;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.type.Fishes;
import org.spongepowered.api.data.type.GoldenApples;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.data.type.ToolType;
import org.spongepowered.api.data.type.ToolTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.text.translation.Translation;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

@RegistrationDependency({
        ArmorTypeRegistryModule.class,
        BlockRegistryModule.class,
        EquipmentTypeRegistryModule.class,
        ToolTypeRegistryModule.class,
        FishRegistryModule.class,
        CookedFishRegistryModule.class
})
public final class ItemRegistryModule extends AdditionalPluginCatalogRegistryModule<ItemType> implements ItemRegistry {

    private static class Holder {

        private static final ItemRegistryModule INSTANCE = new ItemRegistryModule();
    }

    public static ItemRegistryModule get() {
        return Holder.INSTANCE;
    }

    private final Int2ObjectMap<ItemType> itemTypeByInternalId = new Int2ObjectOpenHashMap<>();
    private final Object2IntMap<ItemType> internalIdByItemType = new Object2IntOpenHashMap<>();

    private ItemRegistryModule() {
        super(ItemTypes.class);
        this.internalIdByItemType.defaultReturnValue(-1);
    }

    /**
     * Registers a {@link ItemType} with the specified internal id.
     *
     * @param internalId The internal id
     * @param itemType The item type
     */
    public void register(int internalId, ItemType itemType) {
        checkState(!this.itemTypeByInternalId.containsKey(internalId), "The internal id is already used: %s", internalId);
        super.register(itemType);
        this.internalIdByItemType.put(itemType, internalId);
        this.itemTypeByInternalId.put(internalId, itemType);
        LanternPropertyRegistry.getInstance().registerItemPropertyStores(((LanternItemType) itemType).getPropertyProviderCollection());
    }

    @Override
    public int getInternalId(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return this.internalIdByItemType.getInt(itemType);
    }

    @Override
    public Optional<ItemType> getTypeByInternalId(int internalId) {
        return Optional.ofNullable(this.itemTypeByInternalId.get(internalId));
    }

    @Override
    public void registerDefaults() {
        final LanternItemType none = builder().build("minecraft", "none");
        register(0, none);
        ///////////////////
        /// Iron Shovel ///
        ///////////////////
        register(256, toolBuilder(ToolTypes.IRON)
                .translation("item.shovelIron.name")
                .build("minecraft", "iron_shovel"));
        ////////////////////
        /// Iron Pickaxe ///
        ////////////////////
        register(257, toolBuilder(ToolTypes.IRON)
                .translation("item.pickaxeIron.name")
                .build("minecraft", "iron_pickaxe"));
        ////////////////////
        ///   Iron Axe   ///
        ////////////////////
        register(258, toolBuilder(ToolTypes.IRON)
                .translation("item.hatchetIron.name")
                .build("minecraft", "iron_axe"));
        ///////////////////////
        /// Flint and Steel ///
        ///////////////////////
        register(259, durableBuilder()
                .translation("item.flintAndSteel.name")
                .build("minecraft", "flint_and_steel"));
        ///////////////////
        ///    Apple    ///
        ///////////////////
        register(260, builder()
                .translation("item.apple.name")
                .build("minecraft", "apple"));
        ///////////////////
        ///     Bow     ///
        ///////////////////
        register(261, durableBuilder()
                .translation("item.bow.name")
                .build("minecraft", "bow"));
        ///////////////////
        ///    Arrow    ///
        ///////////////////
        register(262, builder()
                .translation("item.arrow.name")
                .build("minecraft", "arrow"));
        ///////////////////
        ///    Coal     ///
        ///////////////////
        register(263, builder()
                .translation(TranslationProvider.of(CoalTypes.COAL, Keys.COAL_TYPE))
                .keysProvider(valueContainer -> valueContainer
                        .registerKey(Keys.COAL_TYPE, CoalTypes.COAL)
                )
                .build("minecraft", "coal"));
        ///////////////////
        ///   Diamond   ///
        ///////////////////
        register(264, builder()
                .translation("item.diamond.name")
                .build("minecraft", "diamond"));
        ///////////////////
        ///  Iron Ingot ///
        ///////////////////
        register(265, builder()
                .translation("item.ingotIron.name")
                .build("minecraft", "iron_ingot"));
        ///////////////////
        ///  Gold Ingot ///
        ///////////////////
        register(266, builder()
                .translation("item.ingotGold.name")
                .build("minecraft", "gold_ingot"));
        ///////////////////
        ///  Iron Sword ///
        ///////////////////
        register(267, toolBuilder(ToolTypes.IRON)
                .translation("item.swordIron.name")
                .build("minecraft", "iron_sword"));
        ////////////////////
        /// Wooden Sword ///
        ////////////////////
        register(268, toolBuilder(ToolTypes.WOOD)
                .translation("item.swordWood.name")
                .build("minecraft", "wooden_sword"));
        /////////////////////
        /// Wooden Shovel ///
        /////////////////////
        register(269, toolBuilder(ToolTypes.WOOD)
                .translation("item.shovelWood.name")
                .build("minecraft", "wooden_shovel"));
        //////////////////////
        /// Wooden Pickaxe ///
        //////////////////////
        register(270, toolBuilder(ToolTypes.WOOD)
                .translation("item.pickaxeWood.name")
                .build("minecraft", "wooden_pickaxe"));
        //////////////////////
        ///   Wooden Axe   ///
        //////////////////////
        register(271, toolBuilder(ToolTypes.WOOD)
                .translation("item.hatchetWood.name")
                .build("minecraft", "wooden_axe"));
        ////////////////////
        ///  Stone Sword ///
        ////////////////////
        register(272, toolBuilder(ToolTypes.STONE)
                .translation("item.swordStone.name")
                .build("minecraft", "stone_sword"));
        /////////////////////
        ///  Stone Shovel ///
        /////////////////////
        register(273, toolBuilder(ToolTypes.STONE)
                .translation("item.shovelStone.name")
                .build("minecraft", "stone_shovel"));
        //////////////////////
        ///  Stone Pickaxe ///
        //////////////////////
        register(274, toolBuilder(ToolTypes.STONE)
                .translation("item.pickaxeStone.name")
                .build("minecraft", "stone_pickaxe"));
        //////////////////////
        ///    Stone Axe   ///
        //////////////////////
        register(275, toolBuilder(ToolTypes.STONE)
                .translation("item.hatchetStone.name")
                .build("minecraft", "stone_axe"));
        /////////////////////
        /// Diamond Sword ///
        /////////////////////
        register(276, toolBuilder(ToolTypes.DIAMOND)
                .translation("item.swordDiamond.name")
                .build("minecraft", "diamond_sword"));
        //////////////////////
        /// Diamond Shovel ///
        //////////////////////
        register(277, toolBuilder(ToolTypes.DIAMOND)
                .translation("item.shovelDiamond.name")
                .build("minecraft", "diamond_shovel"));
        ///////////////////////
        /// Diamond Pickaxe ///
        ///////////////////////
        register(278, toolBuilder(ToolTypes.DIAMOND)
                .translation("item.pickaxeDiamond.name")
                .build("minecraft", "diamond_pickaxe"));
        ///////////////////////
        ///   Diamond Axe   ///
        ///////////////////////
        register(279, toolBuilder(ToolTypes.DIAMOND)
                .translation("item.hatchetDiamond.name")
                .build("minecraft", "diamond_axe"));
        ///////////////////////
        ///      Stick      ///
        ///////////////////////
        register(280, builder()
                .translation("item.stick.name")
                .build("minecraft", "stick"));
        //////////////////////
        ///      Bowl      ///
        //////////////////////
        register(281, builder()
                .translation("item.bowl.name")
                .build("minecraft", "bowl"));
        //////////////////////
        ///  Mushroom Stew ///
        //////////////////////
        register(282, builder()
                .translation("item.mushroomStew.name")
                .build("minecraft", "mushroom_stew"));
        /////////////////////
        ///  Golden Sword ///
        /////////////////////
        register(283, toolBuilder(ToolTypes.GOLD)
                .translation("item.swordGold.name")
                .build("minecraft", "golden_sword"));
        //////////////////////
        ///  Golden Shovel ///
        //////////////////////
        register(284, toolBuilder(ToolTypes.GOLD)
                .translation("item.shovelGold.name")
                .build("minecraft", "golden_shovel"));
        ///////////////////////
        ///  Golden Pickaxe ///
        ///////////////////////
        register(285, toolBuilder(ToolTypes.GOLD)
                .translation("item.pickaxeGold.name")
                .build("minecraft", "golden_pickaxe"));
        ///////////////////////
        ///    Golden Axe   ///
        ///////////////////////
        register(286, toolBuilder(ToolTypes.GOLD)
                .translation("item.hatchetGold.name")
                .build("minecraft", "golden_axe"));
        ///////////////////////
        ///      String     ///
        ///////////////////////
        register(287, builder()
                .translation("item.string.name")
                .build("minecraft", "string"));
        ///////////////////////
        ///      Feather    ///
        ///////////////////////
        register(288, builder()
                .translation("item.feather.name")
                .build("minecraft", "feather"));
        ///////////////////////
        ///     Gunpowder   ///
        ///////////////////////
        register(289, builder()
                .translation("item.sulphur.name")
                .build("minecraft", "gunpowder"));
        ///////////////////////
        ///    Wooden Hoe   ///
        ///////////////////////
        register(290, toolBuilder(ToolTypes.WOOD)
                .translation("item.hoeWood.name")
                .build("minecraft", "wooden_hoe"));
        ///////////////////////
        ///     Stone Hoe   ///
        ///////////////////////
        register(291, toolBuilder(ToolTypes.STONE)
                .translation("item.hoeStone.name")
                .build("minecraft", "stone_hoe"));
        ///////////////////////
        ///     Iron Hoe    ///
        ///////////////////////
        register(292, toolBuilder(ToolTypes.IRON)
                .translation("item.hoeIron.name")
                .build("minecraft", "iron_hoe"));
        ///////////////////////
        ///   Diamond Hoe   ///
        ///////////////////////
        register(293, toolBuilder(ToolTypes.DIAMOND)
                .translation("item.hoeDiamond.name")
                .build("minecraft", "diamond_hoe"));
        ///////////////////////
        ///    Golden Hoe   ///
        ///////////////////////
        register(294, toolBuilder(ToolTypes.GOLD)
                .translation("item.hoeGold.name")
                .build("minecraft", "golden_hoe"));
        ///////////////////////
        ///   Wheat Seeds   ///
        ///////////////////////
        register(295, builder()
                .translation("item.seeds.name")
                .build("minecraft", "wheat_seeds"));
        ///////////////////////
        ///      Wheat      ///
        ///////////////////////
        register(296, builder()
                .translation("item.wheat.name")
                .build("minecraft", "wheat"));
        ///////////////////////
        ///      Bread      ///
        ///////////////////////
        register(297, builder()
                .translation("item.bread.name")
                .build("minecraft", "bread"));
        //////////////////////////
        ///   Leather Helmet   ///
        //////////////////////////
        register(298, leatherArmorBuilder(EquipmentTypes.HEADWEAR)
                .translation("item.helmetCloth.name")
                .build("minecraft", "leather_helmet"));
        //////////////////////////
        /// Leather Chestplate ///
        //////////////////////////
        register(299, leatherArmorBuilder(EquipmentTypes.CHESTPLATE)
                .translation("item.chestplateCloth.name")
                .build("minecraft", "leather_chestplate"));
        //////////////////////////
        ///  Leather Leggings  ///
        //////////////////////////
        register(300, leatherArmorBuilder(EquipmentTypes.LEGGINGS)
                .translation("item.leggingsCloth.name")
                .build("minecraft", "leather_leggings"));
        //////////////////////////
        ///    Leather Boots   ///
        //////////////////////////
        register(301, leatherArmorBuilder(EquipmentTypes.BOOTS)
                .translation("item.bootsCloth.name")
                .build("minecraft", "leather_boots"));
        ////////////////////////////
        ///   Chainmail Helmet   ///
        ////////////////////////////
        register(302, armorBuilder(ArmorTypes.CHAIN, EquipmentTypes.HEADWEAR)
                .translation("item.helmetChain.name")
                .build("minecraft", "chainmail_helmet"));
        ////////////////////////////
        /// Chainmail Chestplate ///
        ////////////////////////////
        register(303, armorBuilder(ArmorTypes.CHAIN, EquipmentTypes.CHESTPLATE)
                .translation("item.chestplateChain.name")
                .build("minecraft", "chainmail_chestplate"));
        ////////////////////////////
        ///  Chainmail Leggings  ///
        ////////////////////////////
        register(304, armorBuilder(ArmorTypes.CHAIN, EquipmentTypes.LEGGINGS)
                .translation("item.leggingsChain.name")
                .build("minecraft", "chainmail_leggings"));
        ////////////////////////////
        ///    Chainmail Boots   ///
        ////////////////////////////
        register(305, armorBuilder(ArmorTypes.CHAIN, EquipmentTypes.BOOTS)
                .translation("item.bootsChain.name")
                .build("minecraft", "chainmail_boots"));
        ///////////////////////
        ///   Iron Helmet   ///
        ///////////////////////
        register(306, armorBuilder(ArmorTypes.IRON, EquipmentTypes.HEADWEAR)
                .translation("item.helmetIron.name")
                .build("minecraft", "iron_helmet"));
        ///////////////////////
        /// Iron Chestplate ///
        ///////////////////////
        register(307, armorBuilder(ArmorTypes.IRON, EquipmentTypes.CHESTPLATE)
                .translation("item.chestplateIron.name")
                .build("minecraft", "iron_chestplate"));
        ///////////////////////
        ///  Iron Leggings  ///
        ///////////////////////
        register(308, armorBuilder(ArmorTypes.IRON, EquipmentTypes.LEGGINGS)
                .translation("item.leggingsIron.name")
                .build("minecraft", "iron_leggings"));
        ///////////////////////
        ///    Iron Boots   ///
        ///////////////////////
        register(309, armorBuilder(ArmorTypes.IRON, EquipmentTypes.BOOTS)
                .translation("item.bootsIron.name")
                .build("minecraft", "iron_boots"));
        //////////////////////////
        ///   Diamond Helmet   ///
        //////////////////////////
        register(310, armorBuilder(ArmorTypes.DIAMOND, EquipmentTypes.HEADWEAR)
                .translation("item.helmetDiamond.name")
                .build("minecraft", "diamond_helmet"));
        //////////////////////////
        /// Diamond Chestplate ///
        //////////////////////////
        register(311, armorBuilder(ArmorTypes.DIAMOND, EquipmentTypes.CHESTPLATE)
                .translation("item.chestplateDiamond.name")
                .build("minecraft", "diamond_chestplate"));
        //////////////////////////
        ///  Diamond Leggings  ///
        //////////////////////////
        register(312, armorBuilder(ArmorTypes.DIAMOND, EquipmentTypes.LEGGINGS)
                .translation("item.leggingsDiamond.name")
                .build("minecraft", "diamond_leggings"));
        //////////////////////////
        ///    Diamond Boots   ///
        //////////////////////////
        register(313, armorBuilder(ArmorTypes.DIAMOND, EquipmentTypes.BOOTS)
                .translation("item.bootsDiamond.name")
                .build("minecraft", "diamond_boots"));
        /////////////////////////
        ///   Golden Helmet   ///
        /////////////////////////
        register(314, armorBuilder(ArmorTypes.GOLD, EquipmentTypes.HEADWEAR)
                .translation("item.helmetGold.name")
                .build("minecraft", "golden_helmet"));
        /////////////////////////
        /// Golden Chestplate ///
        /////////////////////////
        register(315, armorBuilder(ArmorTypes.GOLD, EquipmentTypes.CHESTPLATE)
                .translation("item.chestplateGold.name")
                .build("minecraft", "golden_chestplate"));
        /////////////////////////
        ///  Golden Leggings  ///
        /////////////////////////
        register(316, armorBuilder(ArmorTypes.GOLD, EquipmentTypes.LEGGINGS)
                .translation("item.leggingsGold.name")
                .build("minecraft", "golden_leggings"));
        /////////////////////////
        ///    Golden Boots   ///
        /////////////////////////
        register(317, armorBuilder(ArmorTypes.GOLD, EquipmentTypes.BOOTS)
                .translation("item.bootsGold.name")
                .build("minecraft", "golden_boots"));
        ///////////////////////
        ///      Flint      ///
        ///////////////////////
        register(318, builder()
                .translation("item.flint.name")
                .build("minecraft", "flint"));
        ///////////////////////
        ///     Porkchop    ///
        ///////////////////////
        register(319, builder()
                .translation("item.porkchopRaw.name")
                .build("minecraft", "porkchop"));
        /////////////////////////
        ///  Cooked Porkchop  ///
        /////////////////////////
        register(320, builder()
                .translation("item.porkchopCooked.name")
                .build("minecraft", "cooked_porkchop"));
        /////////////////////////
        ///      Painting     ///
        /////////////////////////
        register(321, builder()
                .translation("item.painting.name")
                .build("minecraft", "painting"));
        ////////////////////
        /// Golden Apple ///
        ////////////////////
        register(322, builder()
                .translation("item.appleGold.name")
                .keysProvider(valueContainer -> valueContainer
                        .registerKey(Keys.GOLDEN_APPLE_TYPE, GoldenApples.GOLDEN_APPLE)
                )
                .build("minecraft", "golden_apple"));
        /////////////////////
        ///      Sign     ///
        /////////////////////
        register(323, builder()
                .translation("item.sign.name")
                .maxStackQuantity(16)
                .build("minecraft", "sign"));
        ///////////////////////
        ///   Wooden Door   ///
        ///////////////////////
        register(324, builder()
                .translation("item.doorOak.name")
                .build("minecraft", "wooden_door"));
        /////////////////////
        ///     Bucket    ///
        /////////////////////
        register(325, builder()
                .translation("item.bucket.name")
                .maxStackQuantity(16)
                .build("minecraft", "bucket"));
        ////////////////////////
        ///   Water Bucket   ///
        ////////////////////////
        register(326, builder()
                .translation("item.bucketWater.name")
                .maxStackQuantity(1)
                .build("minecraft", "water_bucket"));
        ///////////////////////
        ///   Lava Bucket   ///
        ///////////////////////
        register(327, builder()
                .translation("item.bucketLava.name")
                .maxStackQuantity(1)
                .build("minecraft", "lava_bucket"));
        ////////////////////
        ///   Minecart   ///
        ////////////////////
        register(328, builder()
                .translation("item.minecart.name")
                .maxStackQuantity(1)
                .build("minecraft", "minecart"));
        //////////////////
        ///   Saddle   ///
        //////////////////
        register(329, builder()
                .translation("item.saddle.name")
                .build("minecraft", "saddle"));
        /////////////////////
        ///   Iron Door   ///
        /////////////////////
        register(330, builder()
                .translation("item.doorIron.name")
                .build("minecraft", "iron_door"));
        ////////////////////
        ///   Redstone   ///
        ////////////////////
        register(331, builder()
                .translation("item.redstone.name")
                .build("minecraft", "redstone"));
        ////////////////////
        ///   Snowball   ///
        ////////////////////
        register(332, builder()
                .translation("item.snowball.name")
                .build("minecraft", "snowball"));
        ////////////////////
        ///   Oak Boat   ///
        ////////////////////
        register(333, builder()
                .translation("item.boat.oak.name")
                .build("minecraft", "boat"));
        ///////////////////
        ///   Leather   ///
        ///////////////////
        register(334, builder()
                .translation("item.leather.name")
                .build("minecraft", "leather"));
        ///////////////////////
        ///   Milk Bucket   ///
        ///////////////////////
        register(335, builder()
                .translation("item.milk.name")
                .maxStackQuantity(1)
                .build("minecraft", "milk_bucket"));
        /////////////////
        ///   Brick   ///
        /////////////////
        register(336, builder()
                .translation("item.brick.name")
                .build("minecraft", "brick"));
        /////////////////////
        ///   Clay Ball   ///
        /////////////////////
        register(337, builder()
                .translation("item.clay.name")
                .build("minecraft", "clay_ball"));
        /////////////////
        ///   Reeds   ///
        /////////////////
        register(338, builder()
                .translation("item.reeds.name")
                .build("minecraft", "reeds"));
        /////////////////
        ///   Paper   ///
        /////////////////
        register(339, builder()
                .translation("item.paper.name")
                .build("minecraft", "paper"));
        ////////////////
        ///   Book   ///
        ////////////////
        register(340, builder()
                .translation("item.book.name")
                .build("minecraft", "book"));
        //////////////////////
        ///   Slime Ball   ///
        //////////////////////
        register(341, builder()
                .translation("item.slimeball.name")
                .build("minecraft", "slime_ball"));
        //////////////////////////
        ///   Chest Minecart   ///
        //////////////////////////
        register(342, builder()
                .translation("item.minecartChest.name")
                .maxStackQuantity(1)
                .build("minecraft", "chest_minecart"));
        ////////////////////////////
        ///   Furnace Minecart   ///
        ////////////////////////////
        register(343, builder()
                .translation("item.minecartFurnace.name")
                .maxStackQuantity(1)
                .build("minecraft", "furnace_minecart"));
        ///////////////
        ///   Egg   ///
        ///////////////
        register(344, builder()
                .translation("item.egg.name")
                .maxStackQuantity(16)
                .build("minecraft", "egg"));
        ///////////////////
        ///   Compass   ///
        ///////////////////
        register(345, builder()
                .translation("item.compass.name")
                .build("minecraft", "compass"));
        ///////////////////////
        ///   Fishing Rod   ///
        ///////////////////////
        register(346, builder()
                .translation("item.fishingRod.name")
                .build("minecraft", "fishing_rod"));
        /////////////////
        ///   Clock   ///
        /////////////////
        register(347, builder()
                .translation("item.clock.name")
                .build("minecraft", "clock"));
        //////////////////////////
        ///   Glowstone Dust   ///
        //////////////////////////
        register(348, builder()
                .translation("item.yellowDust.name")
                .build("minecraft", "glowstone_dust"));
        ////////////////
        ///   Fish   ///
        ////////////////
        register(349, builder()
                .keysProvider(valueContainer -> valueContainer
                        .registerKey(Keys.FISH_TYPE, Fishes.COD).notRemovable())
                .translation(TranslationProvider.of(Fishes.COD, Keys.FISH_TYPE))
                .build("minecraft", "fish"));
        ///////////////////////
        ///   Cooked Fish   ///
        ///////////////////////
        register(350, builder()
                .keysProvider(valueContainer -> valueContainer
                        .registerKey(Keys.COOKED_FISH, CookedFishes.COD).notRemovable())
                .translation(TranslationProvider.of(CookedFishes.COD, Keys.COOKED_FISH))
                .build("minecraft", "cooked_fish"));
        ///////////////
        ///   Dye   ///
        ///////////////
        register(351, builder()
                .keysProvider(valueContainer -> valueContainer
                        .registerKey(Keys.DYE_COLOR, DyeColors.WHITE).notRemovable())
                .translation(coloredTranslation("item.dyePowder.%s.name", DyeColors.WHITE))
                .build("minecraft", "dye"));
        ////////////////
        ///   Bone   ///
        ////////////////
        register(352, builder()
                .translation("item.bone.name")
                .build("minecraft", "bone"));
        /////////////////
        ///   Sugar   ///
        /////////////////
        register(353, builder()
                .translation("item.sugar.name")
                .build("minecraft", "sugar"));
        ////////////////
        ///   Cake   ///
        ////////////////
        register(354, builder()
                .translation("item.cake.name")
                .build("minecraft", "cake"));
        ///////////////
        ///   Bed   ///
        ///////////////
        register(355, builder()
                .translation("item.bed.name")
                .build("minecraft", "bed"));
        ////////////////////
        ///   Repeater   ///
        ////////////////////
        register(356, builder()
                .translation("item.diode.name")
                .build("minecraft", "repeater"));
        //////////////////
        ///   Cookie   ///
        //////////////////
        register(357, builder()
                .translation("item.cookie.name")
                .build("minecraft", "cookie"));
        //////////////////////
        ///   Filled Map   ///
        //////////////////////
        register(358, builder()
                .translation("item.map.name")
                .build("minecraft", "filled_map"));
        //////////////////
        ///   Shears   ///
        //////////////////
        register(359, builder()
                .translation("item.shears.name")
                .build("minecraft", "shears"));
        /////////////////
        ///   Melon   ///
        /////////////////
        register(360, builder()
                .translation("item.melon.name")
                .build("minecraft", "melon"));
        /////////////////////////
        ///   Pumpkin Seeds   ///
        /////////////////////////
        register(361, builder()
                .translation("item.seeds_pumpkin.name")
                .build("minecraft", "pumpkin_seeds"));
        ///////////////////////
        ///   Melon Seeds   ///
        ///////////////////////
        register(362, builder()
                .translation("item.seeds_melon.name")
                .build("minecraft", "melon_seeds"));
        ////////////////
        ///   Beef   ///
        ////////////////
        register(363, builder()
                .translation("item.beefRaw.name")
                .build("minecraft", "beef"));
        ///////////////////////
        ///   Cooked Beef   ///
        ///////////////////////
        register(364, builder()
                .translation("item.beefCooked.name")
                .build("minecraft", "cooked_beef"));
        ///////////////////
        ///   Chicken   ///
        ///////////////////
        register(365, builder()
                .translation("item.chickenRaw.name")
                .build("minecraft", "chicken"));
        //////////////////////////
        ///   Cooked Chicken   ///
        //////////////////////////
        register(366, builder()
                .translation("item.chickenCooked.name")
                .build("minecraft", "cooked_chicken"));
        ////////////////////////
        ///   Rotten Flesh   ///
        ////////////////////////
        register(367, builder()
                .translation("item.rottenFlesh.name")
                .build("minecraft", "rotten_flesh"));
        ///////////////////////
        ///   Ender Pearl   ///
        ///////////////////////
        register(368, builder()
                .translation("item.enderPearl.name")
                .build("minecraft", "ender_pearl"));
        /////////////////////
        ///   Blaze Rod   ///
        /////////////////////
        register(369, builder()
                .translation("item.blazeRod.name")
                .build("minecraft", "blaze_rod"));
        //////////////////////
        ///   Ghast Tear   ///
        //////////////////////
        register(370, builder()
                .translation("item.ghastTear.name")
                .build("minecraft", "ghast_tear"));
        ///////////////////////
        ///   Gold Nugget   ///
        ///////////////////////
        register(371, builder()
                .translation("item.goldNugget.name")
                .build("minecraft", "gold_nugget"));
        ///////////////////////
        ///   Nether Wart   ///
        ///////////////////////
        register(372, builder()
                .translation("item.netherStalkSeeds.name")
                .build("minecraft", "nether_wart"));
        //////////////////
        ///   Potion   ///
        //////////////////
        register(373, potionEffectsBuilder(PotionType::getTranslation)
                .maxStackQuantity(1)
                .build("minecraft", "potion"));
        ////////////////////////
        ///   Glass Bottle   ///
        ////////////////////////
        register(374, builder()
                .translation("item.glassBottle.name")
                .build("minecraft", "glass_bottle"));
        //////////////////////
        ///   Spider Eye   ///
        //////////////////////
        register(375, builder()
                .translation("item.spiderEye.name")
                .build("minecraft", "spider_eye"));
        ////////////////////////////////
        ///   Fermented Spider Eye   ///
        ////////////////////////////////
        register(376, builder()
                .translation("item.fermentedSpiderEye.name")
                .build("minecraft", "fermented_spider_eye"));
        ////////////////////////
        ///   Blaze Powder   ///
        ////////////////////////
        register(377, builder()
                .translation("item.blazePowder.name")
                .build("minecraft", "blaze_powder"));
        ///////////////////////
        ///   Magma Cream   ///
        ///////////////////////
        register(378, builder()
                .translation("item.magmaCream.name")
                .build("minecraft", "magma_cream"));
        /////////////////////////
        ///   Brewing Stand   ///
        /////////////////////////
        register(379, builder()
                .translation("item.brewingStand.name")
                .build("minecraft", "brewing_stand"));
        ////////////////////
        ///   Cauldron   ///
        ////////////////////
        register(380, builder()
                .translation("item.cauldron.name")
                .build("minecraft", "cauldron"));
        /////////////////////
        ///   Ender Eye   ///
        /////////////////////
        register(381, builder()
                .translation("item.eyeOfEnder.name")
                .build("minecraft", "ender_eye"));
        //////////////////////////
        ///   Speckled Melon   ///
        //////////////////////////
        register(382, builder()
                .translation("item.speckledMelon.name")
                .build("minecraft", "speckled_melon"));
        /////////////////////
        ///   Spawn Egg   ///
        /////////////////////
        register(383, builder()
                .translation("item.monsterPlacer.name")
                .build("minecraft", "spawn_egg"));
        /////////////////////////////
        ///   Experience Bottle   ///
        /////////////////////////////
        register(384, builder()
                .translation("item.expBottle.name")
                .build("minecraft", "experience_bottle"));
        ///////////////////////
        ///   Fire Charge   ///
        ///////////////////////
        register(385, builder()
                .translation("item.fireball.name")
                .build("minecraft", "fire_charge"));
        /////////////////////////
        ///   Writable Book   ///
        /////////////////////////
        register(386, builder()
                .translation("item.writingBook.name")
                .keysProvider(valueContainer -> valueContainer
                        .registerKey(Keys.BOOK_PAGES, null))
                .build("minecraft", "writable_book"));
        ////////////////////////
        ///   Written Book   ///
        ////////////////////////
        register(387, builder()
                .translation("item.writtenBook.name")
                .keysProvider(valueContainer -> {
                    valueContainer.registerKey(Keys.BOOK_PAGES, null);
                    valueContainer.registerKey(Keys.BOOK_AUTHOR, null);
                    valueContainer.registerKey(Keys.GENERATION, null);
                })
                .behaviors(pipeline -> pipeline
                        .add(new OpenHeldBookBehavior()))
                .build("minecraft", "written_book"));
        ///////////////////
        ///   Emerald   ///
        ///////////////////
        register(388, builder()
                .translation("item.emerald.name")
                .build("minecraft", "emerald"));
        //////////////////////
        ///   Item Frame   ///
        //////////////////////
        register(389, builder()
                .translation("item.frame.name")
                .build("minecraft", "item_frame"));
        //////////////////////
        ///   Flower Pot   ///
        //////////////////////
        register(390, builder()
                .translation("item.flowerPot.name")
                .build("minecraft", "flower_pot"));
        //////////////////
        ///   Carrot   ///
        //////////////////
        register(391, builder()
                .translation("item.carrots.name")
                .build("minecraft", "carrot"));
        //////////////////
        ///   Potato   ///
        //////////////////
        register(392, builder()
                .translation("item.potato.name")
                .build("minecraft", "potato"));
        ////////////////////////
        ///   Baked Potato   ///
        ////////////////////////
        register(393, builder()
                .translation("item.potatoBaked.name")
                .build("minecraft", "baked_potato"));
        ////////////////////////////
        ///   Poisonous Potato   ///
        ////////////////////////////
        register(394, builder()
                .translation("item.potatoPoisonous.name")
                .build("minecraft", "poisonous_potato"));
        ///////////////
        ///   Map   ///
        ///////////////
        register(395, builder()
                .translation("item.emptyMap.name")
                .build("minecraft", "map"));
        /////////////////////////
        ///   Golden Carrot   ///
        /////////////////////////
        register(396, builder()
                .translation("item.carrotGolden.name")
                .build("minecraft", "golden_carrot"));
        /////////////////
        ///   Skull   ///
        /////////////////
        register(397, builder()
                .translation(TranslationProvider.of(SkullTypes.SKELETON, Keys.SKULL_TYPE))
                .keysProvider(valueContainer -> valueContainer
                        .registerKey(Keys.SKULL_TYPE, SkullTypes.SKELETON))
                .properties(builder -> builder
                        .add(equipmentType(EquipmentTypes.HEADWEAR)))
                .build("minecraft", "skull"));
        /////////////////////////////
        ///   Carrot On A Stick   ///
        /////////////////////////////
        register(398, builder()
                .translation("item.carrotOnAStick.name")
                .build("minecraft", "carrot_on_a_stick"));
        ///////////////////////
        ///   Nether Star   ///
        ///////////////////////
        register(399, builder()
                .translation("item.netherStar.name")
                .build("minecraft", "nether_star"));
        ///////////////////////
        ///   Pumpkin Pie   ///
        ///////////////////////
        register(400, builder()
                .translation("item.pumpkinPie.name")
                .build("minecraft", "pumpkin_pie"));
        ////////////////////
        ///  Fireworks   ///
        ////////////////////
        register(401, builder()
                .translation("item.fireworks.name")
                .keysProvider(valueContainer -> {
                    valueContainer.registerKey(Keys.FIREWORK_EFFECTS, Collections.emptyList());
                    valueContainer.registerKey(Keys.FIREWORK_FLIGHT_MODIFIER, 1);
                })
                .build("minecraft", "fireworks"));
        ///////////////////////
        /// Firework Charge ///
        ///////////////////////
        register(402, builder()
                .translation("item.fireworksCharge.name")
                .keysProvider(valueContainer -> valueContainer
                        .registerKey(Keys.FIREWORK_EFFECTS, Collections.emptyList())
                )
                .maxStackQuantity(1)
                .build("minecraft", "firework_charge"));
        //////////////////////////
        ///   Enchanted Book   ///
        //////////////////////////
        register(403, builder()
                .translation("item.enchantedBook.name")
                .keysProvider(valueContainer -> valueContainer
                        .registerKey(Keys.STORED_ENCHANTMENTS, null))
                .maxStackQuantity(1)
                .build("minecraft", "enchanted_book"));
        //////////////////////
        ///   Comparator   ///
        //////////////////////
        register(404, builder()
                .translation("item.comparator.name")
                .build("minecraft", "comparator"));
        ///////////////////////
        ///   Netherbrick   ///
        ///////////////////////
        register(405, builder()
                .translation("item.netherbrick.name")
                .build("minecraft", "netherbrick"));
        //////////////////
        ///   Quartz   ///
        //////////////////
        register(406, builder()
                .translation("item.netherquartz.name")
                .build("minecraft", "quartz"));
        ////////////////////////
        ///   TNT Minecart   ///
        ////////////////////////
        register(407, builder()
                .translation("item.minecartTnt.name")
                .maxStackQuantity(1)
                .build("minecraft", "tnt_minecart"));
        ///////////////////////////
        ///   Hopper Minecart   ///
        ///////////////////////////
        register(408, builder()
                .translation("item.minecartHopper.name")
                .maxStackQuantity(1)
                .build("minecraft", "hopper_minecart"));
        ////////////////////////////
        ///   Prismarine Shard   ///
        ////////////////////////////
        register(409, builder()
                .translation("item.prismarineShard.name")
                .build("minecraft", "prismarine_shard"));
        ///////////////////////////////
        ///   Prismarine Crystals   ///
        ///////////////////////////////
        register(410, builder()
                .translation("item.prismarineCrystals.name")
                .build("minecraft", "prismarine_crystals"));
        //////////////////
        ///   Rabbit   ///
        //////////////////
        register(411, builder()
                .translation("item.rabbitRaw.name")
                .build("minecraft", "rabbit"));
        /////////////////////////
        ///   Cooked Rabbit   ///
        /////////////////////////
        register(412, builder()
                .translation("item.rabbitCooked.name")
                .build("minecraft", "cooked_rabbit"));
        ///////////////////////
        ///   Rabbit Stew   ///
        ///////////////////////
        register(413, builder()
                .translation("item.rabbitStew.name")
                .build("minecraft", "rabbit_stew"));
        ///////////////////////
        ///   Rabbit Foot   ///
        ///////////////////////
        register(414, builder()
                .translation("item.rabbitFoot.name")
                .build("minecraft", "rabbit_foot"));
        ///////////////////////
        ///   Rabbit Hide   ///
        ///////////////////////
        register(415, builder()
                .translation("item.rabbitHide.name")
                .build("minecraft", "rabbit_hide"));
        ///////////////////////
        ///   Armor Stand   ///
        ///////////////////////
        register(416, builder()
                .translation("item.armorStand.name")
                .build("minecraft", "armor_stand"));
        ////////////////////////////
        ///   Iron Horse Armor   ///
        ////////////////////////////
        register(417, builder()
                .translation("item.horsearmormetal.name")
                .build("minecraft", "iron_horse_armor"));
        //////////////////////////////
        ///   Golden Horse Armor   ///
        //////////////////////////////
        register(418, builder()
                .translation("item.horsearmorgold.name")
                .build("minecraft", "golden_horse_armor"));
        ///////////////////////////////
        ///   Diamond Horse Armor   ///
        ///////////////////////////////
        register(419, builder()
                .translation("item.horsearmordiamond.name")
                .build("minecraft", "diamond_horse_armor"));
        ////////////////
        ///   Lead   ///
        ////////////////
        register(420, builder()
                .translation("item.leash.name")
                .build("minecraft", "lead"));
        ////////////////////
        ///   Name Tag   ///
        ////////////////////
        register(421, builder()
                .translation("item.nameTag.name")
                .build("minecraft", "name_tag"));
        //////////////////////////////////
        ///   Command Block Minecart   ///
        //////////////////////////////////
        register(422, builder()
                .translation("item.minecartCommandBlock.name")
                .maxStackQuantity(1)
                .build("minecraft", "command_block_minecart"));
        //////////////////
        ///   Mutton   ///
        //////////////////
        register(423, builder()
                .translation("item.muttonRaw.name")
                .build("minecraft", "mutton"));
        /////////////////////////
        ///   Cooked Mutton   ///
        /////////////////////////
        register(424, builder()
                .translation("item.muttonCooked.name")
                .build("minecraft", "cooked_mutton"));
        //////////////////
        ///   Banner   ///
        //////////////////
        register(425, builder()
                .translation(coloredTranslation("item.banner.%s.name", DyeColors.WHITE))
                .maxStackQuantity(16)
                .build("minecraft", "banner"));
        ///////////////////////
        ///   End Crystal   ///
        ///////////////////////
        register(426, builder()
                .translation("item.end_crystal.name")
                .build("minecraft", "end_crystal"));
        ///////////////////////
        ///   Spruce Door   ///
        ///////////////////////
        register(427, builder()
                .translation("item.doorSpruce.name")
                .build("minecraft", "spruce_door"));
        //////////////////////
        ///   Bitch Door   ///
        //////////////////////
        register(428, builder()
                .translation("item.doorBirch.name")
                .build("minecraft", "birch_door"));
        ///////////////////////
        ///   Jungle Door   ///
        ///////////////////////
        register(429, builder()
                .translation("item.doorJungle.name")
                .build("minecraft", "jungle_door"));
        ///////////////////////
        ///   Acacia Door   ///
        ///////////////////////
        register(430, builder()
                .translation("item.doorAcacia.name")
                .build("minecraft", "acacia_door"));
        /////////////////////////
        ///   Dark Oak Door   ///
        /////////////////////////
        register(431, builder()
                .translation("item.doorDarkOak.name")
                .build("minecraft", "dark_oak_door"));
        ////////////////////////
        ///   Chorus Fruit   ///
        ////////////////////////
        register(432, builder()
                .translation("item.chorusFruit.name")
                .build("minecraft", "chorus_fruit"));
        ///////////////////////////////
        ///   Chorus Fruit Popped   ///
        ///////////////////////////////
        register(433, builder()
                .translation("item.chorusFruitPopped.name")
                .build("minecraft", "chorus_fruit_popped"));
        ////////////////////
        ///   Beetroot   ///
        ////////////////////
        register(434, builder()
                .translation("item.beetroot.name")
                .build("minecraft", "beetroot"));
        //////////////////////////
        ///   Beetroot Seeds   ///
        //////////////////////////
        register(435, builder()
                .translation("item.beetroot_seeds.name")
                .build("minecraft", "beetroot_seeds"));
        /////////////////////////
        ///   Beetroot Soup   ///
        /////////////////////////
        register(436, builder()
                .translation("item.beetroot_soup.name")
                .build("minecraft", "beetroot_soup"));
        /////////////////////////
        ///   Dragon Breath   ///
        /////////////////////////
        register(437, builder()
                .translation("item.dragon_breath.name")
                .build("minecraft", "dragon_breath"));
        /////////////////////////
        ///   Splash Potion   ///
        /////////////////////////
        register(438, potionEffectsBuilder(PotionType::getSplashTranslation)
                .translation("item.splash_potion.name")
                .maxStackQuantity(1)
                .build("minecraft", "splash_potion"));
        //////////////////////////
        ///   Spectral Arrow   ///
        //////////////////////////
        register(439, builder()
                .translation("item.spectral_arrow.name")
                .build("minecraft", "spectral_arrow"));
        ////////////////////////
        ///   Tipped Arrow   ///
        ////////////////////////
        register(440, potionEffectsBuilder(PotionType::getTippedArrowTranslation)
                .translation("item.tipped_arrow.name")
                .build("minecraft", "tipped_arrow"));
        ////////////////////////////
        ///   Lingering Potion   ///
        ////////////////////////////
        register(441, potionEffectsBuilder(PotionType::getLingeringTranslation)
                .translation("item.lingering_potion.name")
                .maxStackQuantity(1)
                .build("minecraft", "lingering_potion"));
        //////////////////
        ///   Shield   ///
        //////////////////
        register(442, durableBuilder()
                .translation("item.shield.name")
                .build("minecraft", "shield"));
        //////////////////
        ///   Elytra   ///
        //////////////////
        register(443, durableBuilder()
                .translation("item.elytra.name")
                .properties(builder -> builder
                        .add(equipmentType(EquipmentTypes.CHESTPLATE)))
                .behaviors(pipeline -> pipeline
                        .add(new ArmorQuickEquipInteractionBehavior()))
                .build("minecraft", "elytra"));
        ///////////////////////
        ///   Spruce Boat   ///
        ///////////////////////
        register(444, builder()
                .translation("item.boat.spruce.name")
                .maxStackQuantity(1)
                .build("minecraft", "spruce_boat"));
        //////////////////////
        ///   Birch Boat   ///
        //////////////////////
        register(445, builder()
                .translation("item.boat.birch.name")
                .maxStackQuantity(1)
                .build("minecraft", "birch_boat"));
        ///////////////////////
        ///   Jungle Boat   ///
        ///////////////////////
        register(446, builder()
                .translation("item.boat.jungle.name")
                .maxStackQuantity(1)
                .build("minecraft", "jungle_boat"));
        ///////////////////////
        ///   Acacia Boat   ///
        ///////////////////////
        register(447, builder()
                .translation("item.boat.acacia.name")
                .maxStackQuantity(1)
                .build("minecraft", "acacia_boat"));
        /////////////////////////
        ///   Dark Oak Boat   ///
        /////////////////////////
        register(448, builder()
                .translation("item.boat.dark_oak.name")
                .maxStackQuantity(1)
                .build("minecraft", "dark_oak_boat"));
        /////////////////
        ///   Totem   ///
        /////////////////
        register(449, builder()
                .translation("item.totem.name")
                .maxStackQuantity(1)
                .build("minecraft", "totem_of_undying"));
        /////////////////////////
        ///   Shulker Shell   ///
        /////////////////////////
        register(450, builder()
                .translation("item.shulkerShell.name")
                .build("minecraft", "shulker_shell"));
        ///////////////////////
        ///   Iron Nugget   ///
        ///////////////////////
        register(452, builder()
                .translation("item.ironNugget.name")
                .build("minecraft", "iron_nugget"));
        /////////////////////
        ///   Record 13   ///
        /////////////////////
        register(2256, recordBuilder("13")
                .build("minecraft", "record_13"));
        //////////////////////
        ///   Record Cat   ///
        //////////////////////
        register(2257, recordBuilder("cat")
                .build("minecraft", "record_cat"));
        /////////////////////////
        ///   Record Blocks   ///
        /////////////////////////
        register(2258, recordBuilder("blocks")
                .build("minecraft", "record_blocks"));
        ////////////////////////
        ///   Record Chirp   ///
        ////////////////////////
        register(2259, recordBuilder("chirp")
                .build("minecraft", "record_chirp"));
        //////////////////////
        ///   Record Far   ///
        //////////////////////
        register(2260, recordBuilder("far")
                .build("minecraft", "record_far"));
        ///////////////////////
        ///   Record Mall   ///
        ///////////////////////
        register(2261, recordBuilder("mall")
                .build("minecraft", "record_mall"));
        //////////////////////////
        ///   Record Mellohi   ///
        //////////////////////////
        register(2262, recordBuilder("mellohi")
                .build("minecraft", "record_mellohi"));
        ///////////////////////
        ///   Record Stal   ///
        ///////////////////////
        register(2263, recordBuilder("stal")
                .build("minecraft", "record_stal"));
        /////////////////////
        ///   Record Strad   ///
        /////////////////////
        register(2264, recordBuilder("strad")
                .build("minecraft", "record_strad"));
        ///////////////////////
        ///   Record Ward   ///
        ///////////////////////
        register(2265, recordBuilder("ward")
                .build("minecraft", "record_ward"));
        /////////////////////
        ///   Record 11   ///
        /////////////////////
        register(2266, recordBuilder("11")
                .build("minecraft", "record_11"));
        ///////////////////////
        ///   Record Wait   ///
        ///////////////////////
        register(2267, recordBuilder("wait")
                .build("minecraft", "record_wait"));
        try {
            ReflectionHelper.setField(ItemStackSnapshot.class.getDeclaredField("NONE"), null,
                    new LanternItemStack(none, 0).createSnapshot());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ItemTypeBuilder potionEffectsBuilder(Function<PotionType, Translation> translationFunction) {
        return builder()
                .translation((itemType, itemStack) -> {
                    if (itemStack != null) {
                        final PotionType potionType = itemStack.get(LanternKeys.POTION_TYPE).orElse(null);
                        if (potionType != null) {
                            return translationFunction.apply(potionType);
                        }
                    }
                    return tr("item.potion.name");
                })
                .keysProvider(valueContainer -> {
                    valueContainer.registerKey(Keys.COLOR, null);
                    valueContainer.registerKey(Keys.POTION_EFFECTS, null);
                    valueContainer.registerKey(LanternKeys.POTION_TYPE, null);
                });
    }

    private ItemTypeBuilder recordBuilder(String name) {
        return builder()
                .maxStackQuantity(1)
                .translation(String.format("item.record.%s.desc", name));
    }

    private TranslationProvider coloredTranslation(String pattern, DyeColor defaultColor) {
        return (itemType, itemStack) -> tr(String.format(pattern,
                ((LanternDyeColor) (itemStack == null ? defaultColor : itemStack.get(Keys.DYE_COLOR).get())).getTranslationPart()));
    }

    private ItemTypeBuilder durableBuilder() {
        return builder()
                .maxStackQuantity(1)
                .keysProvider(valueContainer -> {
                    valueContainer.registerKey(Keys.ITEM_DURABILITY, 0);
                    valueContainer.registerKey(Keys.UNBREAKABLE, true); // True until durability is implemented
                });
    }

    private ItemTypeBuilder toolBuilder(ToolType toolType) {
        return durableBuilder()
                .properties(builder -> builder
                        .add(toolType(toolType)));
    }

    private ItemTypeBuilder leatherArmorBuilder(EquipmentType equipmentType) {
        return armorBuilder(ArmorTypes.LEATHER, equipmentType)
                .keysProvider(valueContainer -> valueContainer
                        .registerKey(Keys.COLOR, null));
    }

    private ItemTypeBuilder armorBuilder(ArmorType armorType, EquipmentType equipmentType) {
        return durableBuilder()
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
