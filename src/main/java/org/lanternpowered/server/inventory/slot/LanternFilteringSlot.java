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
package org.lanternpowered.server.inventory.slot;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.slot.FilteringSlot;
import org.spongepowered.api.text.translation.Translation;

import javax.annotation.Nullable;

public class LanternFilteringSlot extends LanternSlot implements FilteringSlot {

    @Nullable protected final ItemFilter itemFilter;

    public LanternFilteringSlot(@Nullable Inventory parent) {
        this(parent, null, null);
    }

    public LanternFilteringSlot(@Nullable Inventory parent, @Nullable Translation name) {
        this(parent, name, null);
    }

    public LanternFilteringSlot(@Nullable Inventory parent, @Nullable ItemFilter itemFilter) {
        this(parent, null, itemFilter);
    }

    public LanternFilteringSlot(@Nullable Inventory parent, @Nullable Translation name, @Nullable ItemFilter itemFilter) {
        super(parent, name);
        this.itemFilter = itemFilter;
    }

    @Override
    public boolean isValidItem(ItemStack stack) {
        checkNotNull(stack, "stack");
        if (this.itemFilter != null && !this.itemFilter.isValidItem(stack)) {
            return false;
        }
        return super.isValidItem(stack);
    }

    @Override
    public boolean isValidItem(ItemType type) {
        checkNotNull(type, "type");
        if (this.itemFilter != null && !this.itemFilter.isValidItem(type)) {
            return false;
        }
        return doesAllowItem(type);
    }
}
