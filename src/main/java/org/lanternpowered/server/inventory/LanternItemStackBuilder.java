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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class LanternItemStackBuilder extends AbstractDataBuilder<ItemStack> implements ItemStack.Builder {

    @Nullable private LanternItemStack itemStack;
    private boolean itemTypeSet;

    public LanternItemStackBuilder() {
        super(ItemStack.class, 1);
    }

    private LanternItemStack itemStack(@Nullable ItemType itemType) {
        if (itemType != null) {
            if (this.itemStack == null) {
                this.itemStack = new LanternItemStack(itemType);
            } else if (this.itemStack.getType() != itemType) {
                final LanternItemStack old = this.itemStack;
                this.itemStack = new LanternItemStack(itemType);
                this.itemStack.setQuantity(old.getQuantity());
                this.itemStack.copyFromNoEvents(old, MergeFunction.IGNORE_ALL);
            }
            this.itemTypeSet = true;
        } else if (this.itemStack == null) {
            this.itemStack = new LanternItemStack(ItemTypes.APPLE);
        }
        return this.itemStack;
    }

    @Override
    public ItemStack.Builder itemType(ItemType itemType) {
        itemStack(checkNotNull(itemType, "itemType"));
        return this;
    }

    @Override
    public ItemType getCurrentItem() {
        return this.itemTypeSet ? itemStack(null).getType() : ItemTypes.AIR;
    }

    @Override
    public ItemStack.Builder quantity(int quantity) {
        itemStack(null).setQuantity(quantity);
        return this;
    }

    @Override
    public ItemStack.Builder itemData(DataManipulator<?, ?> itemData) throws IllegalArgumentException {
        itemStack(null).offerFastNoEvents(itemData, MergeFunction.IGNORE_ALL);
        return this;
    }

    @Override
    public ItemStack.Builder itemData(ImmutableDataManipulator<?, ?> itemData) throws IllegalArgumentException {
        itemStack(null).offerFastNoEvents(itemData.asMutable(), MergeFunction.IGNORE_ALL);
        return this;
    }

    @Override
    public <V> ItemStack.Builder add(Key<? extends BaseValue<V>> key, V value) throws IllegalArgumentException {
        itemStack(null).offerFastNoEvents(key, value);
        return this;
    }

    @Override
    public ItemStack.Builder remove(Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        itemStack(null).removeFast(manipulatorClass);
        return this;
    }

    @Override
    public ItemStack.Builder from(ItemStack value) {
        this.itemStack = (LanternItemStack) value.copy();
        return this;
    }

    @Override
    public ItemStack.Builder fromSnapshot(ItemStackSnapshot snapshot) {
        return from(((LanternItemStackSnapshot) snapshot).itemStack);
    }

    @Override
    public ItemStack.Builder fromItemStack(ItemStack itemStack) {
        return from(itemStack);
    }

    @Override
    public ItemStack.Builder fromContainer(DataView container) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public ItemStack.Builder fromBlockSnapshot(BlockSnapshot blockSnapshot) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public ItemStack build() throws IllegalStateException {
        checkState(this.itemTypeSet, "The item type must be set");
        return itemStack(null).copy();
    }

    @Override
    protected Optional<ItemStack> buildContent(DataView container) throws InvalidDataException {
        throw new UnsupportedOperationException("TODO");
    }
}
