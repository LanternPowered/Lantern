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
package org.lanternpowered.server.network.buffer.contextual;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.data.io.store.ObjectStore;
import org.lanternpowered.server.data.io.store.ObjectStoreRegistry;
import org.lanternpowered.server.data.io.store.item.ItemStackStore;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.objects.RawItemStack;
import org.lanternpowered.server.text.LanternTexts;
import org.lanternpowered.server.text.translation.TranslationContext;
import org.lanternpowered.server.text.translation.TranslationHelper;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public final class ItemStackContextualValueType implements ContextualValueType<ItemStack> {

    private static final ObjectStore<LanternItemStack> store = ObjectStoreRegistry.get().get(LanternItemStack.class).get();

    private static final DataQuery ACTUAL_NAME = DataQuery.of("ActualName");
    private static final DataQuery ACTUAL_LORE = DataQuery.of("ActualLore");

    public static void deserializeTextFromNetwork(DataView dataView) {
        dataView.getView(ItemStackStore.TAG.then(ItemStackStore.DISPLAY)).ifPresent(displayView -> {
            displayView.get(ACTUAL_NAME).ifPresent(value -> {
                displayView.set(ItemStackStore.NAME, value);
                displayView.remove(ACTUAL_NAME);
            });
            displayView.get(ACTUAL_LORE).ifPresent(value -> {
                displayView.set(ItemStackStore.LORE, value);
                displayView.remove(ACTUAL_LORE);
            });
        });
    }

    public static void serializeTextForNetwork(DataView dataView, ItemStack itemStack) {
        dataView.getView(ItemStackStore.TAG.then(ItemStackStore.DISPLAY)).ifPresent(displayView -> {
            displayView.get(ItemStackStore.NAME).ifPresent(value -> {
                final Text name = itemStack.get(Keys.DISPLAY_NAME).get();
                // Check if we need to translate the text for the client
                // TODO: currently forced to true, until 1.13, which adds json support for item names
                if (true || TranslationHelper.containsNonMinecraftTranslation(name)) {
                    displayView.set(ACTUAL_NAME, value);
                    try (TranslationContext ignored = TranslationContext.enter()
                            .enableForcedTranslations()) {
                        // displayView.set(ItemStackStore.NAME, TextSerializers.JSON.serialize(name)); // TODO: 1.13
                        displayView.set(ItemStackStore.NAME, LanternTexts.toLegacy(name));
                    }
                }
            });
            displayView.get(ItemStackStore.LORE).ifPresent(value -> {
                final List<Text> lore = itemStack.get(Keys.ITEM_LORE).get();
                // TODO: currently forced to true, until 1.13, which adds json support for item names
                if (true || TranslationHelper.containsNonMinecraftTranslation(lore)) {
                    displayView.set(ACTUAL_LORE, value);
                    try (TranslationContext ignored = TranslationContext.enter()
                            .enableForcedTranslations()) {
                        // TODO: 1.13
                        /*
                        displayView.set(ItemStackStore.LORE, lore.stream()
                                .map(TextSerializers.JSON::serialize)
                                .collect(Collectors.toList()));
                        */
                        displayView.set(ItemStackStore.LORE, lore.stream()
                                .map(LanternTexts::toLegacy)
                                .collect(Collectors.toList()));
                    }
                }
            });
        });
    }

    ItemStackContextualValueType() {
    }

    @Override
    public void write(CodecContext ctx, @Nullable ItemStack object, ByteBuffer buf) throws CodecException {
        if (object == null) {
            buf.writeRawItemStack(null);
        } else {
            final DataView dataView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
            try (TranslationContext ignored = TranslationContext.enter()
                    .disableForcedTranslation()) {
                store.serialize((LanternItemStack) object, dataView);
            }
            try (TranslationContext ignored = TranslationContext.enter()
                    .locale(ctx.getSession().getLocale())) {
                serializeTextForNetwork(dataView, object);
            }
            buf.writeRawItemStack(new RawItemStack(ItemRegistryModule.get().getInternalId(object.getType()),
                    dataView.getShort(ItemStackStore.DATA).orElse((short) 0), object.getQuantity(),
                    dataView.getView(ItemStackStore.TAG).orElse(null)));
        }
    }

    @Override
    public ItemStack read(CodecContext ctx, ByteBuffer buf) throws CodecException {
        final RawItemStack rawItemStack = buf.readRawItemStack();
        if (rawItemStack == null) {
            return null;
        }
        final ItemType itemType = ItemRegistryModule.get().getTypeByInternalId(rawItemStack.getItemType()).orElse(null);
        if (itemType == null) {
            return null;
        }
        final LanternItemStack itemStack = new LanternItemStack(itemType, rawItemStack.getAmount());
        final DataView dataView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        dataView.set(ItemStackStore.DATA, rawItemStack.getData());
        dataView.set(ItemStackStore.QUANTITY, rawItemStack.getAmount());
        final DataView tag = rawItemStack.getDataView();
        if (tag != null) {
            dataView.set(ItemStackStore.TAG, tag);
        }
        deserializeTextFromNetwork(dataView);
        store.deserialize(itemStack, dataView);
        return itemStack;
    }
}
