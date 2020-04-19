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

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.item.inventory.ItemStack;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class MessagePlayInCreativeWindowAction implements Message {

    private final int slot;
    @Nullable private final ItemStack itemStack;

    public MessagePlayInCreativeWindowAction(int slot, ItemStack itemStack) {
        this.itemStack = itemStack;
        this.slot = slot;
    }

    public int getSlot() {
        return this.slot;
    }

    @Nullable
    public ItemStack getItemStack() {
        return this.itemStack;
    }
}
