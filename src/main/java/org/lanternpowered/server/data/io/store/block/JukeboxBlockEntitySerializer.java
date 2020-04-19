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
