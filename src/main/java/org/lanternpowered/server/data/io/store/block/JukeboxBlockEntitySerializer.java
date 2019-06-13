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
package org.lanternpowered.server.data.io.store.block;

import org.lanternpowered.server.block.entity.vanilla.LanternJukebox;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.data.io.store.item.ItemStackStore;
import org.lanternpowered.server.inventory.IInventory;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;

import java.util.Optional;

public class JukeboxBlockEntitySerializer<T extends LanternJukebox> extends BlockEntityObjectStore<T> {

    private static final DataQuery RECORD = DataQuery.of("Record");
    private static final DataQuery RECORD_ITEM = DataQuery.of("RecordItem");

    @Override
    public void deserializeValues(T object, SimpleValueContainer valueContainer, DataView dataView) {
        super.deserializeValues(object, valueContainer, dataView);
        final Optional<DataView> optMusicDisc = dataView.getView(RECORD_ITEM);
        if (optMusicDisc.isPresent()) {
            object.insert(ItemStackStore.INSTANCE.deserialize(optMusicDisc.get()));
        } else {/*
            dataView.getInt(RECORD).ifPresent(record -> ItemRegistryModule.get().getTypeByInternalId(record)
                    .ifPresent(itemType -> object.insertRecord(ItemStack.of(itemType, 1))));*/
            //TODO:Update
        }
    }

    @Override
    public void serializeValues(T object, SimpleValueContainer valueContainer, DataView dataView) {
        super.serializeValues(object, valueContainer, dataView);
        ((IInventory) object.getInventory()).peek().ifNotEmpty(stack -> dataView
                .set(RECORD_ITEM, ItemStackStore.INSTANCE.serialize(stack)));
    }
}
