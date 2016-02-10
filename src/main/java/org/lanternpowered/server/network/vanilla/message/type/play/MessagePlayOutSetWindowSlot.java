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
package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.annotation.Nullable;

public final class MessagePlayOutSetWindowSlot implements Message {

    private final int index;
    private final int window;

    @Nullable private final ItemStack itemStack;

    public MessagePlayOutSetWindowSlot(int window, int index, @Nullable ItemStack itemStack) {
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
    public ItemStack getItem() {
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
