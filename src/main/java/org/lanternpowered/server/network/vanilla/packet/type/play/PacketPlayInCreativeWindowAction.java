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
package org.lanternpowered.server.network.vanilla.packet.type.play;

import org.lanternpowered.server.network.packet.Packet;
import org.spongepowered.api.item.inventory.ItemStack;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class PacketPlayInCreativeWindowAction implements Packet {

    private final int slot;
    @Nullable private final ItemStack itemStack;

    public PacketPlayInCreativeWindowAction(int slot, ItemStack itemStack) {
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
