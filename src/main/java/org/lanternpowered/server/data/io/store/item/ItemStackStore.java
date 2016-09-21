/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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

import org.lanternpowered.server.data.io.store.ObjectSerializer;
import org.lanternpowered.server.data.io.store.data.DataHolderStore;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.network.buffer.objects.Types;
import org.lanternpowered.server.text.LanternTexts;
import org.lanternpowered.server.text.gson.JsonTextTranslatableSerializer;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.BookView;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemStackStore extends DataHolderStore<LanternItemStack> implements ObjectSerializer<LanternItemStack> {

    private static final DataQuery IDENTIFIER = DataQuery.of("id");
    private static final DataQuery QUANTITY = DataQuery.of("Count");
    private static final DataQuery DATA = DataQuery.of("Damage");
    private static final DataQuery TAG = DataQuery.of("tag");

    private static final DataQuery AUTHOR = DataQuery.of("author");
    private static final DataQuery TITLE = DataQuery.of("title");
    private static final DataQuery PAGES = DataQuery.of("pages");

    @Override
    public LanternItemStack deserialize(DataView dataView) throws InvalidDataException {
        final String identifier = dataView.getString(IDENTIFIER).get();
        final ItemType itemType = ItemRegistryModule.get().getById(identifier).orElseThrow(
                () -> new InvalidDataException("There is no item type with the id: " + identifier));
        return new LanternItemStack(itemType);
    }

    @Override
    public DataView serialize(LanternItemStack object) {
        final DataContainer dataContainer = new MemoryDataContainer(DataView.SafetyMode.NO_DATA_CLONED);
        dataContainer.set(IDENTIFIER, object.getItem().getId());
        return dataContainer;
    }

    @Override
    public void deserialize(LanternItemStack object, DataView dataView) {
        object.setQuantity(dataView.getInt(QUANTITY).get());
        // TODO: Handle cases of data?
        // All the extra data we will handle will be stored in the tag
        final Optional<DataView> optTag = dataView.getView(TAG);
        if (optTag.isPresent()) {
            super.deserialize(object, optTag.get());
        }
    }

    @Override
    public void serialize(LanternItemStack object, DataView dataView) {
        dataView.set(QUANTITY, (byte) object.getQuantity());
        // TODO: Handle cases of data?
        dataView.set(DATA, (short) 0);
        super.serialize(object, dataView.createView(TAG));
    }

    public static void writeBookData(DataView dataView, BookView bookView, Locale locale) {
        dataView.set(AUTHOR, LanternTexts.toLegacy(bookView.getAuthor()));
        dataView.set(TITLE, LanternTexts.toLegacy(bookView.getTitle()));
        JsonTextTranslatableSerializer.setCurrentLocale(locale);
        dataView.set(PAGES, bookView.getPages().stream().map(Types.TEXT_GSON::toJson).collect(Collectors.toList()));
        JsonTextTranslatableSerializer.removeCurrentLocale();
    }
}
