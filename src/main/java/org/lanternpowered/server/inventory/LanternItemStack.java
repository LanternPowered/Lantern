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
import java.util.function.Consumer;

import javax.annotation.Nullable;

@SuppressWarnings({"ConstantConditions", "SimplifiableConditionalExpression"})
public class LanternItemStack implements ItemStack, AbstractPropertyHolder, IAdditionalDataHolder {

    private static final LanternItemStack empty = null;

    /**
     * Gets a empty {@link ItemStack} if the specified {@link ItemStack}
     * is {@code null}. Otherwise returns the item stack itself.
     *
     * @param itemStack The item stack
     * @return A empty or the provided item stack
     */
    public static LanternItemStack orEmpty(@Nullable ItemStack itemStack) {
        return itemStack == null ? empty : (LanternItemStack) itemStack;
    }

    /**
     * Gets a empty {@link ItemStack}.
     *
     * <p>A empty item stack will always have the item type
     * {@link ItemTypes#NONE} and a quantity of {@code 0}.
     * And any data offered to it will be rejected.</p>
     *
     * @return The empty item stack
     */
    public static LanternItemStack empty() {
        return empty;
    }

    private final ValueCollection valueCollection;
    private final AdditionalContainerCollection<DataManipulator<?, ?>> additionalContainers;
    private final ItemType itemType;

    private int quantity;

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
        // Use empty containers for the none item type
        this(itemType, quantity, ValueCollection.create(),
                itemType == ItemTypes.NONE ? AdditionalContainerCollection.empty() : AdditionalContainerCollection.createConcurrent());
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
        return ((LanternItemType) getType()).getTranslationProvider().get(this.itemType, this);
    }

    @Override
    public ItemType getType() {
        return this.quantity == 0 ? ItemTypes.NONE : this.itemType;
    }

    @Override
    public int getQuantity() {
        return this.itemType == ItemTypes.NONE ? 0 : this.quantity;
    }

    @Override
    public void setQuantity(int quantity) throws IllegalArgumentException {
        checkArgument(quantity >= 0, "quantity may not be negative");
        this.quantity = quantity;
    }

    /**
     * Clears this {@link ItemStack}, this sets the quantity to {@code 0}.
     */
    public void clear() {
        setQuantity(0);
    }

    @Override
    public int getMaxStackQuantity() {
        return getType().getMaxStackQuantity();
    }

    @Override
    public ItemStackSnapshot createSnapshot() {
        if (isEmpty()) {
            return ItemStackSnapshot.NONE;
        }
        return new LanternItemStackSnapshot(copy());
    }

    public ItemStackSnapshot toSnapshot() {
        return createSnapshot();
    }

    public ItemStackSnapshot toWrappedSnapshot() {
        if (isEmpty() && empty != null) {
            return ItemStackSnapshot.NONE;
        }
        return new LanternItemStackSnapshot(this);
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
        // Just return the empty instance
        if (isEmpty()) {
            return empty;
        }
        return new LanternItemStack(this.itemType, this.quantity, getValueCollection().copy(), this.additionalContainers.copy());
    }

    /**
     * Gets whether this item stack is filled. (non empty)
     *
     * @return Is filled
     */
    public boolean isFilled() {
        return !isEmpty();
    }

    /**
     * Applies the {@link Consumer} when this item stack isn't empty.
     *
     * @param consumer The consumer to accept
     * @see #isEmpty()
     */
    public void ifFilled(Consumer<LanternItemStack> consumer) {
        if (isFilled()) {
            consumer.accept(this);
        }
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
        final boolean emptyA = isEmpty();
        final boolean emptyB = that.isEmpty();
        if (emptyA != emptyB) {
            return emptyA && emptyB;
        }
        return getType() == that.getType() && IValueContainer.matchContents(this, (IValueContainer) that);
    }

    public static boolean isEmpty(@Nullable ItemStack itemStack) {
        return itemStack == null || itemStack.isEmpty();
    }

    public static boolean areSimilar(@Nullable ItemStack itemStackA, @Nullable ItemStack itemStackB) {
        return itemStackA == itemStackB ? true : itemStackA == null || itemStackB == null ? false :
                ((LanternItemStack) itemStackA).similarTo(itemStackB);
    }

    public static boolean areSimilar(@Nullable ItemStack itemStackA, @Nullable ItemStackSnapshot itemStackB) {
        return itemStackA == ((LanternItemStackSnapshot) itemStackB).itemStack ? true : itemStackA == null || itemStackB == null ? false :
                ((LanternItemStack) itemStackA).similarTo(itemStackB);
    }

    public static boolean areSimilar(@Nullable ItemStackSnapshot itemStackA, @Nullable ItemStack itemStackB) {
        return itemStackB == ((LanternItemStackSnapshot) itemStackA).itemStack ? true : itemStackA == null || itemStackB == null ? false :
                ((LanternItemStack) itemStackB).similarTo(itemStackA);
    }

    public static boolean areSimilar(@Nullable ItemStackSnapshot itemStackA, @Nullable ItemStackSnapshot itemStackB) {
        return ((LanternItemStackSnapshot) itemStackA).itemStack == ((LanternItemStackSnapshot) itemStackA).itemStack ? true :
                itemStackA == null || itemStackB == null ? false : ((LanternItemStackSnapshot) itemStackB).itemStack.similarTo(itemStackA);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", getType().getKey())
                .add("quantity", getQuantity())
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
