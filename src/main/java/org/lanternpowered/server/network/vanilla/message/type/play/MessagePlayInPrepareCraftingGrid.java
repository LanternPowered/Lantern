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
package org.lanternpowered.server.network.vanilla.message.type.play;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.util.collect.Lists2;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

public final class MessagePlayInPrepareCraftingGrid implements Message {

    private final int windowId;
    private final int transactionId;

    private final List<SlotUpdate> preparedItems;
    private final List<SlotUpdate> returnedItems;

    public MessagePlayInPrepareCraftingGrid(int windowId, int transactionId,
            List<SlotUpdate> preparedItems, List<SlotUpdate> returnedItems) {
        this.preparedItems = ImmutableList.copyOf(preparedItems);
        this.returnedItems = ImmutableList.copyOf(returnedItems);
        this.transactionId = transactionId;
        this.windowId = windowId;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public List<SlotUpdate> getPreparedItems() {
        return this.preparedItems;
    }

    public List<SlotUpdate> getReturnedItems() {
        return this.returnedItems;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("windowId", this.windowId)
                .add("transactionId", this.transactionId)
                .add("preparedItems", Lists2.toString(this.preparedItems))
                .add("returnedItems", Lists2.toString(this.returnedItems))
                .toString();
    }

    public static final class SlotUpdate {

        private final ItemStack itemStack;
        private final int craftingSlot;
        private final int playerSlot;

        public SlotUpdate(ItemStack itemStack, int craftingSlot, int playerSlot) {
            this.itemStack = itemStack;
            this.craftingSlot = craftingSlot;
            this.playerSlot = playerSlot;
        }

        public ItemStack getItemStack() {
            return this.itemStack;
        }

        public int getCraftingSlot() {
            return this.craftingSlot;
        }

        public int getPlayerSlot() {
            return this.playerSlot;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("itemStack", this.itemStack)
                    .add("craftingSlot", this.craftingSlot)
                    .add("playerSlot", this.playerSlot)
                    .toString();
        }
    }
}
