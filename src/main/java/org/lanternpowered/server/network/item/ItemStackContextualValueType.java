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
package org.lanternpowered.server.network.item;

import static org.lanternpowered.server.data.DataHelper.getOrCreateView;

import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import org.lanternpowered.server.data.io.store.item.ItemStackStore;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueType;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.text.translation.TranslationContext;
import org.lanternpowered.server.text.translation.TranslationHelper;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class ItemStackContextualValueType implements ContextualValueType<ItemStack> {

    private final static DataQuery INTERNAL_ID = DataQuery.of("_%$iid");
    private final static DataQuery UNIQUE_ID = DataQuery.of("_%$uid");
    private final static DataQuery TEMP_NAME = DataQuery.of("_%$name");
    private final static DataQuery TEMP_LORE = DataQuery.of("_%$lore");

    public static ItemStack deserializeFromNetwork(DataView dataView) {
        deserializeFromNetwork0(dataView, true);
        return ItemStackStore.INSTANCE.deserialize(dataView);
    }

    private static void deserializeFromNetwork0(DataView dataView, boolean json) {
        dataView.getView(ItemStackStore.TAG).ifPresent(tagView -> {
            if (json) {
                tagView.getInt(INTERNAL_ID).ifPresent(id -> {
                    dataView.set(ItemStackStore.IDENTIFIER,
                            NetworkItemTypeRegistry.networkIdToItemType.get(id.intValue()));
                    tagView.remove(INTERNAL_ID);
                });
            }
            tagView.getView(ItemStackStore.DISPLAY).ifPresent(displayView -> {
                displayView.get(TEMP_NAME).ifPresent(value -> {
                    displayView.set(ItemStackStore.NAME, value);
                    displayView.remove(TEMP_NAME);
                });
                displayView.get(TEMP_LORE).ifPresent(value -> {
                    displayView.set(ItemStackStore.LORE, value);
                    displayView.remove(TEMP_LORE);
                });
            });
            tagView.remove(UNIQUE_ID);
        });
    }

    public static DataView serializeForNetwork(ItemStack itemStack) {
        final int[] ids = NetworkItemTypeRegistry.itemTypeToInternalAndNetworkId.get(itemStack.getType());
        if (ids == null) {
            throw new IllegalStateException("Invalid vanilla/modded item type id: " + itemStack.getType().getKey());
        }
        final DataView dataView = serializeForNetwork(itemStack, ids);
        // Remap the item stack identifier, used in the json format, will be omitted in other messages
        dataView.set(ItemStackStore.IDENTIFIER, NetworkItemTypeRegistry.serverModdedToClientId.get(itemStack.getType().getKey().toString()));
        return dataView;
    }

    private static DataView serializeForNetwork(ItemStack itemStack, int[] ids) {
        final DataView dataView = ItemStackStore.INSTANCE.serialize((LanternItemStack) itemStack);
        final DataView tagView = getOrCreateView(dataView, ItemStackStore.TAG);
        // Add our custom data
        // Add the server assigned internal id
        tagView.set(INTERNAL_ID, ids[0]);
        // Add a unique id to the stack to prevent it from stacking
        if (itemStack.getMaxStackQuantity() == 1) {
            tagView.set(UNIQUE_ID, ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
        }
        tagView.getView(ItemStackStore.DISPLAY).ifPresent(displayView -> {
            displayView.get(ItemStackStore.NAME).ifPresent(value -> {
                final Text name = itemStack.get(Keys.DISPLAY_NAME).get();
                // Check if we need to translate the text for the client
                if (TranslationHelper.containsNonMinecraftTranslation(name)) {
                    displayView.set(TEMP_NAME, value);
                    try (TranslationContext ignored = TranslationContext.enter()
                            .enableForcedTranslations()) {
                        displayView.set(ItemStackStore.NAME, TextSerializers.JSON.serialize(name));
                    }
                }
            });
            displayView.get(ItemStackStore.LORE).ifPresent(value -> {
                final List<Text> lore = itemStack.get(Keys.ITEM_LORE).get();
                if (lore.stream().anyMatch(TranslationHelper::containsNonMinecraftTranslation)) {
                    displayView.set(TEMP_LORE, value);
                    try (TranslationContext ignored = TranslationContext.enter()
                            .enableForcedTranslations()) {
                        displayView.set(ItemStackStore.LORE, lore.stream()
                                .map(TextSerializers.JSON::serialize)
                                .collect(Collectors.toList()));
                    }
                }
            });
        });
        return dataView;
    }

    public ItemStackContextualValueType() {
    }

    @Override
    public void write(CodecContext ctx, @Nullable ItemStack object, ByteBuffer buf) throws CodecException {
        if (object == null || object.isEmpty()) {
            buf.writeBoolean(false);
        } else {
            final int[] ids = NetworkItemTypeRegistry.itemTypeToInternalAndNetworkId.get(object.getType());
            if (ids == null) {
                throw new EncoderException("Invalid vanilla/modded item type id: " + object.getType().getKey());
            }
            final DataView dataView;
            try (TranslationContext ignored = TranslationContext.enter()
                    .locale(ctx.getSession().getLocale())) {
                dataView = serializeForNetwork(object, ids);
            }
            buf.writeBoolean(true);
            buf.writeVarInt(ids[1]); // Network id
            buf.writeByte((byte) object.getQuantity());
            buf.writeDataView(dataView.getView(ItemStackStore.TAG).orElse(null));
        }
    }

    @Nullable
    @Override
    public ItemStack read(CodecContext ctx, ByteBuffer buf) throws CodecException {
        final boolean isPresent = buf.readBoolean();
        if (!isPresent) {
            return null;
        }
        final int networkId = buf.readVarInt();
        if (networkId == -1) {
            return null;
        }
        final int amount = buf.readByte();
        final DataView tag = buf.readDataView();
        ItemType itemType = null;
        if (tag != null) {
            final int internalId = tag.getInt(INTERNAL_ID).orElse(-1);
            if (internalId != -1) {
                itemType = NetworkItemTypeRegistry.internalIdToItemType.get(internalId);
                if (itemType == null) {
                    throw new DecoderException("Received ItemStack with unknown internal id: " + internalId);
                }
            }
            tag.remove(INTERNAL_ID);
            tag.remove(UNIQUE_ID);
        }
        if (itemType == null) {
            itemType = NetworkItemTypeRegistry.networkIdToItemType.get(networkId);
            if (itemType == null) {
                // We know the id, but it's not implemented yet
                if (NetworkItemTypeRegistry.networkIdToNormal.containsKey(networkId)) {
                    return null;
                } else {
                    throw new DecoderException("Received ItemStack with unknown network id: " + networkId);
                }
            }
        }
        final LanternItemStack itemStack = new LanternItemStack(itemType, amount);
        final DataView dataView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        dataView.set(ItemStackStore.QUANTITY, amount);
        if (tag != null) {
            dataView.set(ItemStackStore.TAG, tag);
        }
        deserializeFromNetwork0(dataView, false);
        ItemStackStore.INSTANCE.deserialize(itemStack, dataView);
        return itemStack;
    }
}
