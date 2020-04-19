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
package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.item.inventory.ItemStack;

public final class MessagePlayOutWindowItems implements Message {

    private final int windowId;
    private final ItemStack[] items;

    public MessagePlayOutWindowItems(int windowId, ItemStack[] items) {
        this.items = checkNotNull(items, "items");
        this.windowId = windowId;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public ItemStack[] getItems() {
        return this.items;
    }
}
