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
package org.lanternpowered.server.item.appearance;

import org.spongepowered.api.item.ItemType;

/**
 * Represents the appearance of an {@link ItemType}.
 */
public class ItemAppearance {

    private final String itemTypeId;

    /**
     * Construct a new {@link ItemAppearance}.
     *
     * @param itemTypeId The vanilla/modded item type id
     */
    public ItemAppearance(String itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    /**
     * Gets the vanilla/modded item type id.
     *
     * @return The item type id
     */
    public String getItemTypeId() {
        return this.itemTypeId;
    }
}
