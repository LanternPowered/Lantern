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

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.data.AdditionalContainerCollection;
import org.lanternpowered.server.data.DataQueries;
import org.lanternpowered.server.data.IAdditionalDataHolder;
import org.lanternpowered.server.data.IValueContainer;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.item.LanternItemType;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.translation.Translation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class LanternItemStack implements ItemStack, AbstractPropertyHolder, IAdditionalDataHolder {

    private final ValueCollection valueCollection;
    private final AdditionalContainerCollection<DataManipulator<?, ?>> additionalContainers;
    private final ItemType itemType;

    private int quantity;
    private int tempMaxQuantity;

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
        this(itemType, quantity, ValueCollection.create(), AdditionalContainerCollection.createConcurrent());
        registerKeys();
    }

    private LanternItemStack(ItemType itemType, int quantity, ValueCollection valueCollection,
            AdditionalContainerCollection<DataManipulator<?, ?>> additionalContainers) {
        checkArgument(quantity >= 0, "quantity may not be negative");
        checkNotNull(itemType, "itemType");
        this.additionalContainers = additionalContainers;
        this.valueCollection = valueCollection;
        this.quantity = quantity;
        this.itemType = itemType;
    }

    private void registerKeys() {
        final ValueCollection c = getValueCollection();
        ((LanternItemType) this.itemType).getKeysProvider().accept(c);
        c.register(Keys.DISPLAY_NAME, null);
        c.register(Keys.ITEM_LORE, Collections.emptyList());
        c.register(Keys.BREAKABLE_BLOCK_TYPES, new HashSet<>());
        c.register(Keys.ITEM_ENCHANTMENTS, Collections.emptyList());
    }

    @Override
    public AdditionalContainerCollection<DataManipulator<?, ?>> getAdditionalContainers() {
        return this.additionalContainers;
    }

    @Override
    public ValueCollection getValueCollection() {
        return this.valueCollection;
    }

    @Override
    public boolean validateRawData(DataView dataView) {
        return dataView.contains(DataQueries.ITEM_TYPE);
    }

    @Override
    public void setRawData(DataView dataView) throws InvalidDataException {
        checkNotNull(dataView, "dataView");
        dataView.remove(DataQueries.ITEM_TYPE);
        this.quantity = dataView.getInt(DataQueries.QUANTITY).orElse(1);
        IAdditionalDataHolder.super.setRawData(dataView);
    }

    @Override
    public DataContainer toContainer() {
        return IAdditionalDataHolder.super.toContainer()
                .set(DataQueries.ITEM_TYPE, getItem())
                .set(DataQueries.QUANTITY, getQuantity());
    }

    @Override
    public Translation getTranslation() {
        return ((LanternItemType) this.itemType).getTranslationProvider().get(this.itemType, this);
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
        if (this.tempMaxQuantity != 0) {
            return this.tempMaxQuantity;
        }
        return this.itemType.getMaxStackQuantity();
    }

    void setTempMaxQuantity(int maxQuantity) {
        this.tempMaxQuantity = maxQuantity;
    }

    @Override
    public ItemStackSnapshot createSnapshot() {
        return new LanternItemStackSnapshot(copy());
    }

    @Override
    public boolean equalTo(ItemStack that) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.itemType == ItemTypes.NONE || this.quantity <= 0;
    }

    @Override
    public LanternItemStack copy() {
        final LanternItemStack itemStack = new LanternItemStack(this.itemType, this.quantity, getValueCollection().copy(),
                this.additionalContainers.copy());
        itemStack.tempMaxQuantity = this.tempMaxQuantity;
        return itemStack;
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
    public boolean isSimilar(ItemStack that) {
        checkNotNull(that, "that");
        return this.itemType == that.getItem() && IValueContainer.matchContents(this, (IValueContainer) that);
    }

    public static boolean isSimilar(@Nullable ItemStack itemStackA, @Nullable ItemStack itemStackB) {
        //noinspection SimplifiableConditionalExpression
        return itemStackA == itemStackB ? true : itemStackA == null || itemStackB == null ? false :
                ((LanternItemStack) itemStackA).isSimilar(itemStackB);
    }

    static String valuesToString(Set<ImmutableValue<?>> values) {
        return Arrays.toString(values.stream()
                .map(e -> MoreObjects.toStringHelper("")
                        .add("key", e.getKey().getId())
                        .add("value", e.get())
                        .toString())
                .toArray());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", this.itemType.getId())
                .add("quantity", this.quantity)
                .add("data", valuesToString(getValues()))
                .toString();
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
        return itemStack == null ? ItemStackSnapshot.NONE : itemStack.createSnapshot();
    }
}
