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
package org.lanternpowered.server.data.io.store.item;

import static org.lanternpowered.server.data.DataHelper.getOrCreateView;

import org.lanternpowered.server.data.io.store.ObjectSerializer;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.data.io.store.data.DataHolderStore;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.data.CoalTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.CookedFishRegistryModule;
import org.lanternpowered.server.game.registry.type.data.DirtTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.DyeColorRegistryModule;
import org.lanternpowered.server.game.registry.type.data.FishRegistryModule;
import org.lanternpowered.server.game.registry.type.data.GoldenAppleRegistryModule;
import org.lanternpowered.server.game.registry.type.data.PlantTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.SandTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.SandstoneTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.ShrubTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.SkullTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.SlabTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.StoneTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.TreeTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.item.EnchantmentTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.item.enchantment.LanternEnchantmentType;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.SandstoneType;
import org.spongepowered.api.data.type.SlabType;
import org.spongepowered.api.data.type.TreeType;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class ItemStackStore extends DataHolderStore<LanternItemStack> implements ObjectSerializer<LanternItemStack> {

    public static final ItemStackStore INSTANCE = new ItemStackStore();

    private static final DataQuery IDENTIFIER = DataQuery.of("id");
    public static final DataQuery QUANTITY = DataQuery.of("Count");
    public static final DataQuery DATA = DataQuery.of("Damage");
    public static final DataQuery TAG = DataQuery.of("tag");

    public static final DataQuery DISPLAY = DataQuery.of("display");
    public static final DataQuery NAME = DataQuery.of("Name");
    public static final DataQuery LORE = DataQuery.of("Lore");
    private static final DataQuery UNBREAKABLE = DataQuery.of("Unbreakable");
    private static final DataQuery CAN_DESTROY = DataQuery.of("CanDestroy");
    private static final DataQuery ENCHANTMENTS = DataQuery.of("ench");
    private static final DataQuery ENCHANTMENT_ID = DataQuery.of("id");
    private static final DataQuery ENCHANTMENT_LEVEL = DataQuery.of("lvl");
    private static final DataQuery STORED_ENCHANTMENTS = DataQuery.of("StoredEnchantments");

    private final Map<ItemType, ItemTypeObjectSerializer> itemTypeSerializers = new HashMap<>();

    {
        final DataValueItemTypeObjectSerializer<TreeType> treeTypeSerializer =
                new DataValueItemTypeObjectSerializer<>(Keys.TREE_TYPE, TreeTypeRegistryModule.get());
        add(BlockTypes.LOG, treeTypeSerializer);
        add(BlockTypes.WOODEN_SLAB, treeTypeSerializer);
        add(BlockTypes.DOUBLE_WOODEN_SLAB, treeTypeSerializer);
        add(BlockTypes.PLANKS, treeTypeSerializer);
        add(BlockTypes.LEAVES, treeTypeSerializer);
        add(BlockTypes.SAPLING, treeTypeSerializer);
        final DataValueItemTypeObjectSerializer<TreeType> treeType2Serializer =
                new DataValueItemTypeObjectSerializer<>(Keys.TREE_TYPE, TreeTypeRegistryModule.get(),
                        dataValue -> dataValue + 4, internalId -> internalId - 4);
        add(BlockTypes.LOG2, treeType2Serializer);
        add(BlockTypes.LEAVES2, treeType2Serializer);
        final DataValueItemTypeObjectSerializer<SlabType> stoneSlab1Serializer =
                new DataValueItemTypeObjectSerializer<>(Keys.SLAB_TYPE, SlabTypeRegistryModule.get());
        add(BlockTypes.STONE_SLAB, stoneSlab1Serializer);
        add(BlockTypes.DOUBLE_STONE_SLAB, stoneSlab1Serializer);
        final DataValueItemTypeObjectSerializer<SlabType> stoneSlab2Serializer =
                new DataValueItemTypeObjectSerializer<>(Keys.SLAB_TYPE, SlabTypeRegistryModule.get(),
                        dataValue -> dataValue + 8, internalId -> internalId - 8);
        add(BlockTypes.STONE_SLAB2, stoneSlab2Serializer);
        add(BlockTypes.DOUBLE_STONE_SLAB2, stoneSlab2Serializer);
        add(BlockTypes.QUARTZ_BLOCK, new QuartzItemTypeSerializer());
        final DataValueItemTypeObjectSerializer<SandstoneType> sandstoneTypeSerializer =
                new DataValueItemTypeObjectSerializer<>(Keys.SANDSTONE_TYPE, SandstoneTypeRegistryModule.get());
        add(BlockTypes.SANDSTONE, sandstoneTypeSerializer);
        add(BlockTypes.RED_SANDSTONE, sandstoneTypeSerializer);
        final DataValueItemTypeObjectSerializer<DyeColor> dyeColorSerializer =
                new DataValueItemTypeObjectSerializer<>(Keys.DYE_COLOR, DyeColorRegistryModule.get());
        add(BlockTypes.WOOL, dyeColorSerializer);
        add(BlockTypes.CARPET, dyeColorSerializer);
        add(BlockTypes.STAINED_HARDENED_CLAY, dyeColorSerializer);
        add(BlockTypes.STAINED_GLASS, dyeColorSerializer);
        add(BlockTypes.STAINED_GLASS_PANE, dyeColorSerializer);
        final ShulkerBoxItemObjectSerializer shulkerBoxSerializer = new ShulkerBoxItemObjectSerializer();
        add(BlockTypes.BLACK_SHULKER_BOX, shulkerBoxSerializer);
        add(BlockTypes.BLUE_SHULKER_BOX, shulkerBoxSerializer);
        add(BlockTypes.BROWN_SHULKER_BOX, shulkerBoxSerializer);
        add(BlockTypes.CYAN_SHULKER_BOX, shulkerBoxSerializer);
        add(BlockTypes.GRAY_SHULKER_BOX, shulkerBoxSerializer);
        add(BlockTypes.GREEN_SHULKER_BOX, shulkerBoxSerializer);
        add(BlockTypes.LIGHT_BLUE_SHULKER_BOX, shulkerBoxSerializer);
        add(BlockTypes.LIME_SHULKER_BOX, shulkerBoxSerializer);
        add(BlockTypes.MAGENTA_SHULKER_BOX, shulkerBoxSerializer);
        add(BlockTypes.ORANGE_SHULKER_BOX, shulkerBoxSerializer);
        add(BlockTypes.PINK_SHULKER_BOX, shulkerBoxSerializer);
        add(BlockTypes.PURPLE_SHULKER_BOX, shulkerBoxSerializer);
        add(BlockTypes.RED_SHULKER_BOX, shulkerBoxSerializer);
        add(BlockTypes.SILVER_SHULKER_BOX, shulkerBoxSerializer);
        add(BlockTypes.WHITE_SHULKER_BOX, shulkerBoxSerializer);
        add(BlockTypes.YELLOW_SHULKER_BOX, shulkerBoxSerializer);
        add(BlockTypes.DIRT, new DataValueItemTypeObjectSerializer<>(Keys.DIRT_TYPE, DirtTypeRegistryModule.get()));
        add(BlockTypes.STONE, new DataValueItemTypeObjectSerializer<>(Keys.STONE_TYPE, StoneTypeRegistryModule.get()));
        add(BlockTypes.SAND, new DataValueItemTypeObjectSerializer<>(Keys.SAND_TYPE, SandTypeRegistryModule.get()));
        add(BlockTypes.SPONGE, new SpongeItemTypeObjectSerializer());
        add(BlockTypes.TALLGRASS, new DataValueItemTypeObjectSerializer<>(Keys.SHRUB_TYPE, ShrubTypeRegistryModule.get()));
        add(BlockTypes.YELLOW_FLOWER, new DataValueItemTypeObjectSerializer<>(Keys.PLANT_TYPE, PlantTypeRegistryModule.get()));
        add(BlockTypes.RED_FLOWER, new DataValueItemTypeObjectSerializer<>(Keys.PLANT_TYPE, PlantTypeRegistryModule.get(),
                dataValue -> dataValue + 16, internalId -> internalId - 16));

        add(ItemTypes.COAL, new DataValueItemTypeObjectSerializer<>(Keys.COAL_TYPE, CoalTypeRegistryModule.get()));
        add(ItemTypes.FIREWORK_CHARGE, new FireworkChargeItemTypeObjectSerializer());
        add(ItemTypes.FIREWORKS, new FireworksItemTypeObjectSerializer());
        add(ItemTypes.GOLDEN_APPLE, new DataValueItemTypeObjectSerializer<>(Keys.GOLDEN_APPLE_TYPE, GoldenAppleRegistryModule.get()));
        add(ItemTypes.FISH, new DataValueItemTypeObjectSerializer<>(Keys.FISH_TYPE, FishRegistryModule.get()));
        add(ItemTypes.COOKED_FISH, new DataValueItemTypeObjectSerializer<>(Keys.COOKED_FISH, CookedFishRegistryModule.get()));
        add(ItemTypes.DYE, new DataValueItemTypeObjectSerializer<>(Keys.DYE_COLOR, DyeColorRegistryModule.get(),
                dataValue -> 15 - dataValue, internalId -> 15 - internalId));
        add(ItemTypes.SKULL, new DataValueItemTypeObjectSerializer<>(Keys.SKULL_TYPE, SkullTypeRegistryModule.get()));
        add(ItemTypes.WRITABLE_BOOK, new WritableBookItemTypeObjectSerializer());
        add(ItemTypes.WRITTEN_BOOK, new WrittenBookItemTypeObjectSerializer());
        final PotionEffectsItemTypeObjectSerializer potionEffectsSerializer = new PotionEffectsItemTypeObjectSerializer();
        add(ItemTypes.POTION, potionEffectsSerializer);
        add(ItemTypes.SPLASH_POTION, potionEffectsSerializer);
        add(ItemTypes.LINGERING_POTION, potionEffectsSerializer);
        add(ItemTypes.TIPPED_ARROW, potionEffectsSerializer);
        final DurableItemObjectObjectSerializer durableSerializer = new DurableItemObjectObjectSerializer();
        add(ItemTypes.WOODEN_SWORD, durableSerializer);
        add(ItemTypes.WOODEN_PICKAXE, durableSerializer);
        add(ItemTypes.WOODEN_AXE, durableSerializer);
        add(ItemTypes.WOODEN_HOE, durableSerializer);
        add(ItemTypes.WOODEN_SHOVEL, durableSerializer);
        add(ItemTypes.STONE_SWORD, durableSerializer);
        add(ItemTypes.STONE_PICKAXE, durableSerializer);
        add(ItemTypes.STONE_AXE, durableSerializer);
        add(ItemTypes.STONE_HOE, durableSerializer);
        add(ItemTypes.STONE_SHOVEL, durableSerializer);
        add(ItemTypes.CHAINMAIL_BOOTS, durableSerializer);
        add(ItemTypes.CHAINMAIL_LEGGINGS, durableSerializer);
        add(ItemTypes.CHAINMAIL_CHESTPLATE, durableSerializer);
        add(ItemTypes.CHAINMAIL_HELMET, durableSerializer);
        add(ItemTypes.IRON_SWORD, durableSerializer);
        add(ItemTypes.IRON_PICKAXE, durableSerializer);
        add(ItemTypes.IRON_AXE, durableSerializer);
        add(ItemTypes.IRON_HOE, durableSerializer);
        add(ItemTypes.IRON_SHOVEL, durableSerializer);
        add(ItemTypes.IRON_BOOTS, durableSerializer);
        add(ItemTypes.IRON_LEGGINGS, durableSerializer);
        add(ItemTypes.IRON_CHESTPLATE, durableSerializer);
        add(ItemTypes.IRON_HELMET, durableSerializer);
        add(ItemTypes.GOLDEN_SWORD, durableSerializer);
        add(ItemTypes.GOLDEN_PICKAXE, durableSerializer);
        add(ItemTypes.GOLDEN_AXE, durableSerializer);
        add(ItemTypes.GOLDEN_HOE, durableSerializer);
        add(ItemTypes.GOLDEN_SHOVEL, durableSerializer);
        add(ItemTypes.GOLDEN_BOOTS, durableSerializer);
        add(ItemTypes.GOLDEN_LEGGINGS, durableSerializer);
        add(ItemTypes.GOLDEN_CHESTPLATE, durableSerializer);
        add(ItemTypes.GOLDEN_HELMET, durableSerializer);
        add(ItemTypes.DIAMOND_SWORD, durableSerializer);
        add(ItemTypes.DIAMOND_PICKAXE, durableSerializer);
        add(ItemTypes.DIAMOND_AXE, durableSerializer);
        add(ItemTypes.DIAMOND_HOE, durableSerializer);
        add(ItemTypes.DIAMOND_SHOVEL, durableSerializer);
        add(ItemTypes.DIAMOND_BOOTS, durableSerializer);
        add(ItemTypes.DIAMOND_LEGGINGS, durableSerializer);
        add(ItemTypes.DIAMOND_CHESTPLATE, durableSerializer);
        add(ItemTypes.DIAMOND_HELMET, durableSerializer);
        add(ItemTypes.BOW, durableSerializer);
        add(ItemTypes.FLINT_AND_STEEL, durableSerializer);
        add(ItemTypes.FISHING_ROD, durableSerializer);
        add(ItemTypes.SHEARS, durableSerializer);
        add(ItemTypes.ELYTRA, durableSerializer);
        add(ItemTypes.SHIELD, durableSerializer);
        final ColoredLeatherItemTypeObjectSerializer leatherSerializer = new ColoredLeatherItemTypeObjectSerializer();
        add(ItemTypes.LEATHER_BOOTS, leatherSerializer);
        add(ItemTypes.LEATHER_CHESTPLATE, leatherSerializer);
        add(ItemTypes.LEATHER_HELMET, leatherSerializer);
        add(ItemTypes.LEATHER_LEGGINGS, leatherSerializer);
        add(ItemTypes.BANNER, new BannerItemTypeSerializer());
    }

    private void add(ItemType itemType, ItemTypeObjectSerializer serializer) {
        this.itemTypeSerializers.put(itemType, serializer);
    }

    private void add(BlockType blockType, ItemTypeObjectSerializer serializer) {
        this.itemTypeSerializers.put(blockType.getItem().get(), serializer);
    }

    @Override
    public LanternItemStack deserialize(DataView dataView) throws InvalidDataException {
        final String identifier = dataView.getString(IDENTIFIER).get();
        final ItemType itemType = ItemRegistryModule.get().get(CatalogKey.resolve(identifier)).orElseThrow(
                () -> new InvalidDataException("There is no item type with the id: " + identifier));
        final LanternItemStack itemStack = new LanternItemStack(itemType);
        deserialize(itemStack, dataView);
        return itemStack;
    }

    @Override
    public DataView serialize(LanternItemStack object) {
        final DataContainer dataContainer = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        dataContainer.set(IDENTIFIER, object.getType().getKey());
        serialize(object, dataContainer);
        return dataContainer;
    }

    @Override
    public void deserialize(LanternItemStack object, DataView dataView) {
        object.setQuantity(dataView.getInt(QUANTITY).get());
        // All the extra data we will handle will be stored in the tag
        final DataView tag = dataView.getView(TAG).orElseGet(() -> DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED));
        tag.set(ItemTypeObjectSerializer.DATA_VALUE, dataView.getShort(DATA).get());
        super.deserialize(object, tag);
    }

    @Override
    public void serialize(LanternItemStack object, DataView dataView) {
        dataView.set(QUANTITY, (byte) object.getQuantity());
        final DataView tag = dataView.createView(TAG);
        super.serialize(object, tag);
        dataView.set(DATA, tag.getShort(ItemTypeObjectSerializer.DATA_VALUE).orElse((short) 0));
        tag.remove(ItemTypeObjectSerializer.DATA_VALUE);
        if (tag.isEmpty()) {
            dataView.remove(TAG);
        }
    }

    @Override
    public void serializeValues(LanternItemStack object, SimpleValueContainer valueContainer, DataView dataView) {
        final ItemTypeObjectSerializer serializer = this.itemTypeSerializers.get(object.getType());
        if (serializer != null) {
            serializer.serializeValues(object, valueContainer, dataView);
        }
        DataView displayView = null;
        final Optional<Text> optDisplayName = valueContainer.remove(Keys.DISPLAY_NAME);
        if (optDisplayName.isPresent()) {
            displayView = getOrCreateView(dataView, DISPLAY);
            displayView.set(NAME, TextSerializers.JSON.serialize(optDisplayName.get()));
        }
        final Optional<List<Text>> optLore = valueContainer.remove(Keys.ITEM_LORE);
        if (optLore.isPresent() && !optLore.get().isEmpty()) {
            if (displayView == null) {
                displayView = getOrCreateView(dataView, DISPLAY);
            }
            displayView.set(LORE, optLore.get().stream().map(TextSerializers.JSON::serialize).collect(Collectors.toList()));
        }
        if (valueContainer.remove(Keys.UNBREAKABLE).orElse(false)) {
            dataView.set(UNBREAKABLE, (byte) 1);
        }
        final Optional<Set<BlockType>> optBlockTypes = valueContainer.remove(Keys.BREAKABLE_BLOCK_TYPES);
        if (optBlockTypes.isPresent() && !optBlockTypes.get().isEmpty()) {
            dataView.set(CAN_DESTROY, optBlockTypes.get().stream().map(CatalogType::getKey).collect(Collectors.toSet()));
        }
        valueContainer.remove(Keys.ITEM_ENCHANTMENTS).ifPresent(list -> serializeEnchantments(dataView, ENCHANTMENTS, list));
        valueContainer.remove(Keys.STORED_ENCHANTMENTS).ifPresent(list -> serializeEnchantments(dataView, STORED_ENCHANTMENTS, list));
        super.serializeValues(object, valueContainer, dataView);
    }

    @Override
    public void deserializeValues(LanternItemStack object, SimpleValueContainer valueContainer, DataView dataView) {
        final ItemTypeObjectSerializer serializer = this.itemTypeSerializers.get(object.getType());
        if (serializer != null) {
            serializer.deserializeValues(object, valueContainer, dataView);
        }
        dataView.getView(DISPLAY).ifPresent(displayView -> {
            displayView.getString(NAME).ifPresent(json ->
                    valueContainer.set(Keys.DISPLAY_NAME, TextSerializers.JSON.deserializeUnchecked(json)));
            dataView.getStringList(LORE).ifPresent(lore -> {
                if (!lore.isEmpty()) {
                    valueContainer.set(Keys.ITEM_LORE,
                            lore.stream().map(TextSerializers.JSON::deserializeUnchecked).collect(Collectors.toList()));
                }
            });
        });
        dataView.getStringList(CAN_DESTROY).ifPresent(types -> {
            if (!types.isEmpty()) {
                final Set<BlockType> blockTypes = new HashSet<>();
                types.forEach(type -> BlockRegistryModule.get().get(CatalogKey.resolve(type)).ifPresent(blockTypes::add));
                valueContainer.set(Keys.BREAKABLE_BLOCK_TYPES, blockTypes);
            }
        });
        deserializeEnchantments(dataView, ENCHANTMENTS, Keys.ITEM_ENCHANTMENTS, valueContainer);
        deserializeEnchantments(dataView, STORED_ENCHANTMENTS, Keys.STORED_ENCHANTMENTS, valueContainer);
        super.deserializeValues(object, valueContainer, dataView);
    }

    private void serializeEnchantments(DataView dataView, DataQuery query, List<Enchantment> enchantments) {
        if (enchantments.isEmpty()) {
            return;
        }
        final List<DataView> dataViews = new ArrayList<>();
        for (Enchantment enchantment : enchantments) {
            final DataView enchantmentView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
            enchantmentView.set(ENCHANTMENT_ID, (short) ((LanternEnchantmentType) enchantment.getType()).getInternalId());
            enchantmentView.set(ENCHANTMENT_LEVEL, (short) enchantment.getLevel());
            dataViews.add(enchantmentView);
        }
        dataView.set(query, dataViews);
    }

    private void deserializeEnchantments(DataView dataView, DataQuery query, Key<ListValue<Enchantment>> key,
            SimpleValueContainer valueContainer) {
        dataView.getViewList(query).ifPresent(views -> {
            if (!views.isEmpty()) {
                final List<Enchantment> enchantments = new ArrayList<>();
                views.forEach(view -> {
                    final Optional<EnchantmentType> enchantmentType = EnchantmentTypeRegistryModule.get()
                            .getByInternalId(view.getInt(ENCHANTMENT_ID).get());
                    if (enchantmentType.isPresent()) {
                        final int level = view.getInt(ENCHANTMENT_LEVEL).get();
                        enchantments.add(Enchantment.of(enchantmentType.get(), level));
                    } else {
                        Lantern.getLogger().warn("Attempted to deserialize a enchantment with unknown id: {}", view.getInt(ENCHANTMENT_ID).get());
                    }
                });
                valueContainer.set(key, enchantments);
            }
        });
    }
}
