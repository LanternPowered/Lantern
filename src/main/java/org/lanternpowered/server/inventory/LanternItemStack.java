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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.data.AbstractDataHolder;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.data.value.KeyRegistration;
import org.lanternpowered.server.item.LanternItemType;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.translation.Translation;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class LanternItemStack implements ItemStack, AbstractPropertyHolder, AbstractDataHolder {

    private final Map<Key<?>, KeyRegistration> rawValueMap;
    private final ItemType itemType;

    private int quantity;

    public LanternItemStack(BlockType blockType) {
        this(blockType, 1);
    }

    public LanternItemStack(BlockType blockType, int quantity) {
        this(blockType.getItem().orElseThrow(() -> new IllegalArgumentException("That BlockType doesn't have a ItemType.")), quantity);
    }

    public LanternItemStack(ItemType itemType) {
        this(itemType, 1);
    }

    public LanternItemStack(ItemType itemType, int quantity) {
        this(itemType, quantity, new HashMap<>());
        ((LanternItemType) itemType).registerKeysFor(this);
    }

    LanternItemStack(ItemType itemType, int quantity, Map<Key<?>, KeyRegistration> rawValueMap) {
        checkArgument(quantity >= 0, "quantity may not be negative");
        checkNotNull(itemType, "itemType");
        this.rawValueMap = rawValueMap;
        this.quantity = quantity;
        this.itemType = itemType;
    }

    @Override
    public boolean validateRawData(DataView dataView) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setRawData(DataView dataView) throws InvalidDataException {
        // TODO Auto-generated method stub

    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<Key<?>, KeyRegistration> getRawValueMap() {
        return this.rawValueMap;
    }

    @Override
    public Translation getTranslation() {
        return ((LanternItemType) this.itemType).getTranslationFor(this);
    }

    @Override
    public ItemType getItem() {
        return this.itemType;
    }

    @Override
    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public void setQuantity(int quantity) throws IllegalArgumentException {
        checkArgument(quantity >= 0, "quantity may not be negative");
        this.quantity = quantity;
    }

    @Override
    public int getMaxStackQuantity() {
        return this.itemType.getMaxStackQuantity();
    }

    @Override
    public ItemStackSnapshot createSnapshot() {
        return new LanternItemStackSnapshot(this.itemType, this.quantity, copyRawValueMap());
    }

    @Override
    public boolean equalTo(ItemStack that) {
        return false;
    }

    @Override
    public LanternItemStack copy() {
        return new LanternItemStack(this.itemType, this.quantity, copyRawValueMap());
    }

    /**
     * Gets whether this item stack is equal to the other item stack
     * except for the stack size, making it possible to merge the
     * items.
     *
     * Shouldn't be confused with {@link #equalTo(ItemStack)}, which I
     * think has a poor name for what it actually does.
     *
     * @param that The other item stack
     * @return Whether the item stacks are equal
     */
    public boolean isEqualToOther(ItemStack that) {
        checkNotNull(that, "that");
        if (!that.getItem().equals(this.itemType)) {
            return false;
        }
        // TODO: Match data
        return true;
    }

    @Nullable
    public static LanternItemStack toNullable(@Nullable ItemStackSnapshot itemStackSnapshot) {
        if (itemStackSnapshot == null || itemStackSnapshot.getType() == ItemTypes.NONE ||
                itemStackSnapshot.getCount() <= 0) {
            return null;
        }
        return (LanternItemStack) itemStackSnapshot.createStack();
    }

    @Nullable
    public static LanternItemStack toNullable(@Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.getItem() == ItemTypes.NONE ||
                itemStack.getQuantity() <= 0) {
            return null;
        }
        return (LanternItemStack) itemStack;
    }

    public static ItemStackSnapshot toSnapshot(@Nullable ItemStack itemStack) {
        itemStack = toNullable(itemStack);
        //noinspection ConstantConditions
        return itemStack == null ? ItemStackSnapshot.NONE : itemStack.createSnapshot();
    }
}
