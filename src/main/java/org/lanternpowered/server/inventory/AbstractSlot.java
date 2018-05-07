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
package org.lanternpowered.server.inventory;

import org.spongepowered.api.item.inventory.ItemStack;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractSlot extends AbstractMutableInventory implements ISlot {

    public static final int DEFAULT_MAX_STACK_SIZE = 64;

    /**
     * Constructs a new {@link AbstractInventorySlot.Builder}.
     *
     * @return The builder
     */
    public static AbstractInventorySlot.Builder<AbstractInventorySlot> builder() {
        return AbstractInventorySlot.builder();
    }

    /**
     * Gets the raw {@link LanternItemStack}. Does not make a copy.
     *
     * @return The raw item stack
     */
    @Nullable
    public abstract LanternItemStack getRawItemStack();

    /**
     * Sets the raw {@link LanternItemStack}. Does not make a copy.
     *
     * @param itemStack The raw item stack
     */
    public abstract void setRawItemStack(@Nullable ItemStack itemStack);

    @Override
    public AbstractInventorySlot transform() {
        return (AbstractInventorySlot) transform(Type.INVENTORY);
    }

    AbstractSlot() {
    }
}
