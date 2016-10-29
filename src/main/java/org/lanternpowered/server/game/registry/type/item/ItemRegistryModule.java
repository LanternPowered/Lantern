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

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.data.ArmorTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.ToolTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.item.inventory.equipment.EquipmentTypeRegistryModule;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.item.ItemTypeBuilder;
import org.lanternpowered.server.item.ItemTypeBuilderImpl;
import org.lanternpowered.server.item.LanternItemType;
import org.lanternpowered.server.item.TranslationProvider;
import org.lanternpowered.server.util.ReflectionHelper;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.ArmorType;
import org.spongepowered.api.data.type.ArmorTypes;
import org.spongepowered.api.data.type.CoalTypes;
import org.spongepowered.api.data.type.GoldenApples;
import org.spongepowered.api.data.type.ToolType;
import org.spongepowered.api.data.type.ToolTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.util.Color;

import java.util.Collections;
import java.util.Optional;

@RegistrationDependency({ BlockRegistryModule.class, ToolTypeRegistryModule.class, ArmorTypeRegistryModule.class, EquipmentTypeRegistryModule.class })
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
    }

    @Override
    public int getInternalId(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        if (this.internalIdByItemType.containsKey(itemType)) {
            return this.internalIdByItemType.get(itemType);
        }
        return -1;
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
                .build("minecraft", "firework_charge"));
        try {
            ReflectionHelper.setField(ItemStackSnapshot.class.getDeclaredField("NONE"), null,
                    new LanternItemStack(none, 0).createSnapshot());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        final Color leatherColor = Color.ofRgb(10511680);
        return armorBuilder(ArmorTypes.LEATHER, equipmentType)
                .keysProvider(valueContainer -> valueContainer
                        .registerKey(Keys.COLOR, leatherColor));
    }

    private ItemTypeBuilder armorBuilder(ArmorType armorType, EquipmentType equipmentType) {
        return durableBuilder()
                .properties(builder -> builder
                        .add(armorType(armorType))
                        .add(equipmentType(equipmentType)));
    }

    private ItemTypeBuilder builder() {
        return new ItemTypeBuilderImpl();
    }
}
