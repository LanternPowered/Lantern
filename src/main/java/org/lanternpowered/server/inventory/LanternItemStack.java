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
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.translation.Translation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class LanternItemStack implements ItemStack, AbstractPropertyHolder, IAdditionalDataHolder {

    public static ItemStack orEmpty(@Nullable ItemStack itemStack) {
        return itemStack == null ? ItemStack.empty() : itemStack;
    }

    private final ValueCollection valueCollection;
    private final AdditionalContainerCollection<DataManipulator<?, ?>> additionalContainers;
    private final ItemType itemType;

    private int quantity;

    /**
     * Constructs a new {@link LanternItemStack} for the specified {@link BlockType},
     * a {@link IllegalArgumentException} will be thrown if {@link BlockType#getItem()}
     * returns an empty {@link Optional}.
     *
     * @param blockType The block type
     */
    public LanternItemStack(BlockType blockType) {
        this(blockType, 1);
    }

    /**
     * Constructs a new {@link LanternItemStack} for the specified {@link BlockType},
     * a {@link IllegalArgumentException} will be thrown if {@link BlockType#getItem()}
     * returns an empty {@link Optional}.
     *
     * @param blockType The block type
     * @param quantity The quantity
     */
    public LanternItemStack(BlockType blockType, int quantity) {
        this(blockType.getItem().orElseThrow(() -> new IllegalArgumentException("That BlockType doesn't have a ItemType.")), quantity);
    }

    /**
     * Constructs a new {@link LanternItemStack} for the specified {@link ItemType}.
     *
     * @param itemType The item type
     */
    public LanternItemStack(ItemType itemType) {
        this(itemType, 1);
    }

    /**
     * Constructs a new {@link LanternItemStack} for the specified {@link ItemType}.
     *
     * @param itemType The item type
     * @param quantity The quantity
     */
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
                .set(DataQueries.ITEM_TYPE, getType())
                .set(DataQueries.QUANTITY, getQuantity());
    }

    @Override
    public Translation getTranslation() {
        return ((LanternItemType) this.itemType).getTranslationProvider().get(this.itemType, this);
    }

    @Override
    public ItemType getType() {
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
        return new LanternItemStackSnapshot(copy());
    }

    @Override
    public boolean equalTo(ItemStack that) {
        return similarTo(that) && getQuantity() == that.getQuantity();
    }

    /**
     * Similar to {@link #equalTo(ItemStack)}, but matches this
     * {@link ItemStack} with a {@link ItemStackSnapshot}.
     *
     * @param that The other snapshot
     * @return Is equal
     */
    public boolean equalTo(ItemStackSnapshot that) {
        return similarTo(that) && getQuantity() == that.getQuantity();
    }

    @Override
    public boolean isEmpty() {
        return this.itemType == ItemTypes.NONE || this.quantity <= 0;
    }

    @Override
    public LanternItemStack copy() {
        return new LanternItemStack(this.itemType, this.quantity, getValueCollection().copy(), this.additionalContainers.copy());
    }

    /**
     * Gets whether the specified {@link ItemStackSnapshot} is similar
     * to this {@link ItemStack}. The {@link ItemType} and all
     * the applied data must match.
     *
     * @param that The other snapshot
     * @return Is similar
     */
    public boolean similarTo(ItemStackSnapshot that) {
        checkNotNull(that, "that");
        return similarTo(((LanternItemStackSnapshot) that).itemStack);
    }
    /**
     *
     * Gets whether the specified {@link ItemStack} is similar
     * to this {@link ItemStack}. The {@link ItemType} and all
     * the applied data must match.
     *
     * @param that The other snapshot
     * @return Is similar
     */
    public boolean similarTo(ItemStack that) {
        checkNotNull(that, "that");
        return getType() == that.getType() && IValueContainer.matchContents(this, (IValueContainer) that);
    }

    public static boolean isEmpty(@Nullable ItemStack itemStack) {
        return itemStack == null || itemStack.isEmpty();
    }

    public static boolean areSimilar(@Nullable ItemStack itemStackA, @Nullable ItemStack itemStackB) {
        //noinspection SimplifiableConditionalExpression
        return itemStackA == itemStackB ? true : itemStackA == null || itemStackB == null ? false :
                ((LanternItemStack) itemStackA).similarTo(itemStackB);
    }

    public static boolean areSimilar(@Nullable ItemStack itemStackA, @Nullable ItemStackSnapshot itemStackB) {
        //noinspection SimplifiableConditionalExpression
        return itemStackA == ((LanternItemStackSnapshot) itemStackB).itemStack ? true : itemStackA == null || itemStackB == null ? false :
                ((LanternItemStack) itemStackA).similarTo(itemStackB);
    }

    public static boolean areSimilar(@Nullable ItemStackSnapshot itemStackA, @Nullable ItemStack itemStackB) {
        //noinspection SimplifiableConditionalExpression
        return itemStackB == ((LanternItemStackSnapshot) itemStackA).itemStack ? true : itemStackA == null || itemStackB == null ? false :
                ((LanternItemStack) itemStackB).similarTo(itemStackA);
    }

    public static boolean areSimilar(@Nullable ItemStackSnapshot itemStackA, @Nullable ItemStackSnapshot itemStackB) {
        //noinspection SimplifiableConditionalExpression
        return ((LanternItemStackSnapshot) itemStackA).itemStack == ((LanternItemStackSnapshot) itemStackA).itemStack ? true :
                itemStackA == null || itemStackB == null ? false : ((LanternItemStackSnapshot) itemStackB).itemStack.similarTo(itemStackA);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", this.itemType.getId())
                .add("quantity", this.quantity)
                .add("data", IValueContainer.valuesToString(this))
                .toString();
    }

    @Nullable
    public static LanternItemStack toNullable(@Nullable ItemStackSnapshot itemStackSnapshot) {
        return itemStackSnapshot == null || itemStackSnapshot.isEmpty() ? null :  (LanternItemStack) itemStackSnapshot.createStack();
    }

    @Nullable
    public static LanternItemStack toNullable(@Nullable ItemStack itemStack) {
        return itemStack == null || itemStack.isEmpty() ? null : (LanternItemStack) itemStack;
    }

    public static ItemStackSnapshot toSnapshot(@Nullable ItemStack itemStack) {
        return itemStack == null || itemStack.isEmpty() ? ItemStackSnapshot.NONE : itemStack.createSnapshot();
    }
}
