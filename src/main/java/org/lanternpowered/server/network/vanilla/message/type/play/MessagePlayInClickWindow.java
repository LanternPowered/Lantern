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

public final class MessagePlayInClickWindow implements Message {

    private final int windowId;
    private final int slot;
    private final int mode;
    private final int button;
    private final int transaction;
    @Nullable private final ItemStack itemStack;

    public MessagePlayInClickWindow(int windowId, int slot, int mode, int button,
            int transaction, @Nullable ItemStack itemStack) {
        this.windowId = windowId;
        this.slot = slot;
        this.mode = mode;
        this.button = button;
        this.transaction = transaction;
        this.itemStack = itemStack;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getSlot() {
        return this.slot;
    }

    public int getMode() {
        return this.mode;
    }

    public int getButton() {
        return this.button;
    }

    public int getTransaction() {
        return this.transaction;
    }

    @Nullable
    public ItemStack getClickedItem() {
        return this.itemStack;
    }
}
