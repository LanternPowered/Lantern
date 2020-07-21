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

import org.checkerframework.checker.nullness.qual.Nullable;

public final class PacketPlayOutSetWindowSlot implements Packet {

    private final int index;
    private final int window;

    @Nullable private final Object itemStack;

    public PacketPlayOutSetWindowSlot(int window, int index, @Nullable Object itemStack) {
        this.itemStack = itemStack;
        this.window = window;
        this.index = index;
    }

    /**
     * Gets the item stack that should be set at the index.
     * 
     * @return the item stack
     */
    @Nullable
    public Object getItem() {
        return this.itemStack;
    }

    /**
     * Gets the index of the slot.
     * 
     * @return the index
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Gets the window id.
     * 
     * @return the window id
     */
    public int getWindow() {
        return this.window;
    }

}
