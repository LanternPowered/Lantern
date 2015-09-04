package org.lanternpowered.server.item;

import javax.annotation.Nullable;

import org.lanternpowered.server.catalog.LanternCatalogTypeRegistry;
import org.spongepowered.api.item.ItemType;

public class LanternItemRegistry extends LanternCatalogTypeRegistry<ItemType> {

    @Nullable
    public ItemType getById(int internalId) {
        if (internalId < 0 || internalId >= Short.MAX_VALUE) {
            return null;
        }
        // TODO
        return null;
    }

    @Nullable
    public Short getId(ItemType itemType) {
        // TODO
        return null;
    }
}
