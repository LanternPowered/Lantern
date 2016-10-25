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

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import org.lanternpowered.server.data.io.store.ObjectSerializer;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.data.io.store.data.DataHolderStore;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.item.EnchantmentRegistryModule;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.item.enchantment.LanternEnchantment;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.item.Enchantment;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TranslatableText;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemStackStore extends DataHolderStore<LanternItemStack> implements ObjectSerializer<LanternItemStack> {

    private static final DataQuery IDENTIFIER = DataQuery.of("id");
    public static final DataQuery QUANTITY = DataQuery.of("Count");
    public static final DataQuery DATA = DataQuery.of("Damage");
    public static final DataQuery TAG = DataQuery.of("tag");

    private static final DataQuery DISPLAY = DataQuery.of("display");
    private static final DataQuery NAME = DataQuery.of("Name");
    private static final DataQuery LOCALIZED_NAME = DataQuery.of("LocName");
    private static final DataQuery LORE = DataQuery.of("Lore");
    private static final DataQuery UNBREAKABLE = DataQuery.of("Unbreakable");
    private static final DataQuery CAN_DESTROY = DataQuery.of("CanDestroy");
    private static final DataQuery ENCHANTMENTS = DataQuery.of("ench");
    private static final DataQuery ENCHANTMENT_ID = DataQuery.of("id");
    private static final DataQuery ENCHANTMENT_LEVEL = DataQuery.of("lvl");
    private static final DataQuery STORED_ENCHANTMENTS = DataQuery.of("StoredEnchantments");

    private final Map<ItemType, ItemTypeObjectSerializer> itemTypeSerializers = new HashMap<>();

    {
        add(BlockTypes.LOG, new Log1BlockItemTypeObjectSerializer());
        add(BlockTypes.LOG2, new Log2BlockItemTypeObjectSerializer());

        add(ItemTypes.COAL, new CoalItemTypeObjectSerializer());
        add(ItemTypes.FIREWORK_CHARGE, new FireworkChargeItemTypeObjectSerializer());
        add(ItemTypes.FIREWORKS, new FireworksItemTypeObjectSerializer());
        add(ItemTypes.GOLDEN_APPLE, new GoldenAppleItemTypeObjectSerializer());
    }

    private void add(ItemType itemType, ItemTypeObjectSerializer serializer) {
        this.itemTypeSerializers.put(itemType, serializer);
    }

    private void add(BlockType blockType, ItemTypeObjectSerializer serializer) {
        this.itemTypeSerializers.put(blockType.getItem().get(), serializer);
    }

    @Override
    public LanternItemStack deserialize(DataView dataView) throws InvalidDataException {
        String identifier = dataView.getString(IDENTIFIER).get();
        // Fix a identifier type in the mc server
        if (identifier.equals("minecraft:cooked_fished")) {
            identifier = "minecraft:cooked_fish";
        }
        final String identifier1 = identifier;
        final ItemType itemType = ItemRegistryModule.get().getById(identifier).orElseThrow(
                () -> new InvalidDataException("There is no item type with the id: " + identifier1));
        final LanternItemStack itemStack = new LanternItemStack(itemType);
        deserialize(itemStack, dataView);
        return itemStack;
    }

    @Override
    public DataView serialize(LanternItemStack object) {
        final DataContainer dataContainer = new MemoryDataContainer(DataView.SafetyMode.NO_DATA_CLONED);
        dataContainer.set(IDENTIFIER, object.getItem().getId());
        serialize(object, dataContainer);
        return dataContainer;
    }

    @Override
    public void deserialize(LanternItemStack object, DataView dataView) {
        object.setQuantity(dataView.getInt(QUANTITY).get());
        // All the extra data we will handle will be stored in the tag
        final DataView tag = dataView.getView(TAG).orElseGet(() -> new MemoryDataContainer(DataView.SafetyMode.NO_DATA_CLONED));
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
        final ItemTypeObjectSerializer serializer = this.itemTypeSerializers.get(object.getItem());
        if (serializer != null) {
            serializer.serializeValues(object, valueContainer, dataView);
        }
        DataView displayView = null;
        final Optional<Text> optDisplayName = valueContainer.get(Keys.DISPLAY_NAME);
        if (optDisplayName.isPresent()) {
            displayView = dataView.createView(DISPLAY);
            final Text displayName = optDisplayName.get();
            if (displayName instanceof TranslatableText) {
                final TranslatableText name1 = (TranslatableText) displayName;
                // We can only do this for translatable text without any formatting
                if (name1.getArguments().isEmpty() && name1.getChildren().isEmpty() &&
                        name1.getStyle().isEmpty() && name1.getColor() == TextColors.NONE) {
                    displayView.set(LOCALIZED_NAME, name1.getTranslation().getId());
                } else {
                    displayView.set(NAME, LanternTexts.toLegacy(displayName));
                }
            } else {
                displayView.set(NAME, LanternTexts.toLegacy(displayName));
            }
        }
        final Optional<List<Text>> optLore = valueContainer.get(Keys.ITEM_LORE);
        if (optLore.isPresent() && !optLore.get().isEmpty()) {
            if (displayView == null) {
                displayView = dataView.createView(DISPLAY);
            }
            displayView.set(LORE, optLore.get().stream().map(LanternTexts::toLegacy).collect(Collectors.toList()));
        }
        if (valueContainer.get(Keys.UNBREAKABLE).orElse(false)) {
            dataView.set(UNBREAKABLE, (byte) 1);
        }
        final Optional<Set<BlockType>> optBlockTypes = valueContainer.get(Keys.BREAKABLE_BLOCK_TYPES);
        if (optBlockTypes.isPresent() && !optBlockTypes.get().isEmpty()) {
            dataView.set(CAN_DESTROY, optBlockTypes.get().stream().map(CatalogType::getId).collect(Collectors.toSet()));
        }
        valueContainer.get(Keys.ITEM_ENCHANTMENTS).ifPresent(list -> serializeEnchantments(dataView, ENCHANTMENTS, list));
        valueContainer.get(Keys.STORED_ENCHANTMENTS).ifPresent(list -> serializeEnchantments(dataView, STORED_ENCHANTMENTS, list));
        super.serializeValues(object, valueContainer, dataView);
    }

    @Override
    public void deserializeValues(LanternItemStack object, SimpleValueContainer valueContainer, DataView dataView) {
        final ItemTypeObjectSerializer serializer = this.itemTypeSerializers.get(object.getItem());
        if (serializer != null) {
            serializer.deserializeValues(object, valueContainer, dataView);
        }
        final Optional<DataView> optDisplayView = dataView.getView(DISPLAY);
        if (optDisplayView.isPresent()) {
            final DataView displayView = optDisplayView.get();
            if (!valueContainer.get(Keys.DISPLAY_NAME).isPresent()) {
                Optional<String> name = displayView.getString(NAME);
                if (name.isPresent()) {
                    valueContainer.set(Keys.DISPLAY_NAME, LanternTexts.fromLegacy(name.get()));
                } else if ((name = displayView.getString(LOCALIZED_NAME)).isPresent()) {
                    valueContainer.set(Keys.DISPLAY_NAME, t(name.get()));
                }
            }
            dataView.getStringList(LORE).ifPresent(lore -> {
                if (!lore.isEmpty()) {
                    valueContainer.set(Keys.ITEM_LORE,
                            lore.stream().map(LanternTexts::fromLegacy).collect(Collectors.toList()));
                }
            });
        }
        dataView.getStringList(CAN_DESTROY).ifPresent(types -> {
            if (!types.isEmpty()) {
                final Set<BlockType> blockTypes = new HashSet<>();
                types.forEach(type -> BlockRegistryModule.get().getById(type).ifPresent(blockTypes::add));
                valueContainer.set(Keys.BREAKABLE_BLOCK_TYPES, blockTypes);
            }
        });
        deserializeEnchantments(dataView, ENCHANTMENTS, Keys.ITEM_ENCHANTMENTS, valueContainer);
        deserializeEnchantments(dataView, STORED_ENCHANTMENTS, Keys.STORED_ENCHANTMENTS, valueContainer);
        super.deserializeValues(object, valueContainer, dataView);
    }

    private void serializeEnchantments(DataView dataView, DataQuery query, List<ItemEnchantment> enchantments) {
        if (enchantments.isEmpty()) {
            return;
        }
        final List<DataView> dataViews = new ArrayList<>();
        for (ItemEnchantment enchantment : enchantments) {
            final DataView enchantmentView = new MemoryDataContainer(DataView.SafetyMode.NO_DATA_CLONED);
            enchantmentView.set(ENCHANTMENT_ID, (short) ((LanternEnchantment) enchantment.getEnchantment()).getInternalId());
            enchantmentView.set(ENCHANTMENT_LEVEL, (short) enchantment.getLevel());
            dataViews.add(enchantmentView);
        }
        dataView.set(query, dataViews);
    }

    private void deserializeEnchantments(DataView dataView, DataQuery query, Key<ListValue<ItemEnchantment>> key,
            SimpleValueContainer valueContainer) {
        dataView.getViewList(query).ifPresent(views -> {
            if (!views.isEmpty()) {
                final List<ItemEnchantment> itemEnchantments = new ArrayList<>();
                views.forEach(view -> {
                    final Optional<Enchantment> enchantment = EnchantmentRegistryModule.get().getByInternalId(view.getInt(ENCHANTMENT_ID).get());
                    if (enchantment.isPresent()) {
                        final int level = view.getInt(ENCHANTMENT_LEVEL).get();
                        itemEnchantments.add(new ItemEnchantment(enchantment.get(), level));
                    } else {
                        Lantern.getLogger().warn("Attempted to deserialize a enchantment with unknown id: {}", view.getInt(ENCHANTMENT_ID).get());
                    }
                });
                valueContainer.set(key, itemEnchantments);
            }
        });
    }
}
