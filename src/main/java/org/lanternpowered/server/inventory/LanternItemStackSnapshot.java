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

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.data.AdditionalContainerCollection;
import org.lanternpowered.server.data.AdditionalContainerHolder;
import org.lanternpowered.server.data.DataQueries;
import org.lanternpowered.server.data.IImmutableDataHolder;
import org.lanternpowered.server.data.IValueContainer;
import org.lanternpowered.server.data.KeyRegistration;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.processor.Processor;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.item.LanternItemType;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class LanternItemStackSnapshot implements ItemStackSnapshot, IImmutableDataHolder<ItemStackSnapshot>,
        AbstractPropertyHolder, AdditionalContainerHolder<ImmutableDataManipulator<?,?>> {

    private final ValueCollection valueCollection;
    private final AdditionalContainerCollection<ImmutableDataManipulator<?,?>> dataManipulators;
    private final ItemType itemType;
    // TODO: Hmm, inconsistency the the name with itemstack?
    private final int quantity;

    public LanternItemStackSnapshot(ItemType itemType, int quantity) {
        this(itemType, quantity, ValueCollection.create(), AdditionalContainerCollection.create());
        ((LanternItemType) itemType).getKeysProvider().accept(getValueCollection());
    }

    LanternItemStackSnapshot(ItemType itemType, int quantity, ValueCollection valueCollection,
            AdditionalContainerCollection<ImmutableDataManipulator<?,?>> dataManipulators) {
        this.valueCollection = valueCollection;
        this.dataManipulators = dataManipulators;
        this.itemType = itemType;
        this.quantity = quantity;
    }

    @Override
    public ValueCollection getValueCollection() {
        return this.valueCollection;
    }

    @Override
    public AdditionalContainerCollection<ImmutableDataManipulator<?,?>> getAdditionalContainers() {
        return this.dataManipulators;
    }

    @Override
    public ItemType getType() {
        return this.itemType;
    }

    @Override
    public int getCount() {
        return this.quantity;
    }

    @Override
    public boolean isEmpty() {
        return this.itemType == ItemTypes.NONE || this.quantity <= 0;
    }

    @Override
    public ItemStack createStack() {
        return new LanternItemStack(this.itemType, this.quantity, getValueCollection().copy(),
                this.dataManipulators.mapAndAsConcurrent(ImmutableDataManipulator::asMutable));
    }

    @Override
    public GameDictionary.Entry createGameDictionaryEntry() {
        throw new UnsupportedOperationException("The GameDictionary isn't supported, check first if Game#getGameDictionary is present.");
    }

    @Override
    public DataContainer toContainer() {
        return IImmutableDataHolder.super.toContainer()
                .set(DataQueries.ITEM_TYPE, getType())
                .set(DataQueries.QUANTITY, getCount());
    }

    @Override
    public <E> Optional<ItemStackSnapshot> transform(Key<? extends BaseValue<E>> key, Function<E, E> function) {
        return null;
    }

    @Override
    public <E> Optional<ItemStackSnapshot> with(Key<? extends BaseValue<E>> key, E value) {
        return null;
    }

    @Override
    public Optional<ItemStackSnapshot> with(ImmutableDataManipulator<?, ?> valueContainer) {
        return null;
    }

    @Override public Optional<ItemStackSnapshot> with(Iterable<ImmutableDataManipulator<?, ?>> valueContainers) {
        return null;
    }

    @Override
    public Optional<ItemStackSnapshot> without(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        return null;
    }

    @Override
    public ItemStackSnapshot merge(ItemStackSnapshot that) {
        return null;
    }

    @Override
    public ItemStackSnapshot merge(ItemStackSnapshot that, MergeFunction function) {
        return null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", this.itemType.getId())
                .add("quantity", this.quantity)
                .add("data", LanternItemStack.valuesToString(getValues()))
                .toString();
    }

    public boolean isSimilar(ItemStackSnapshot that) {
        checkNotNull(that, "that");
        return this.itemType == that.getType() && compareRawDataMaps(this, (IValueContainer) that);
    }

    @SuppressWarnings("unchecked")
    static boolean compareRawDataMaps(IValueContainer container1, IValueContainer container2) {
        final ValueCollection valueCollection1 = container1.getValueCollection();
        final ValueCollection valueCollection2 = container2.getValueCollection();
        for (KeyRegistration<?,?> registration1 : valueCollection1.getAll()) {
            if (!valueCollection2.has(registration1.getKey())) {
                return false;
            }
            final KeyRegistration registration2 = (KeyRegistration) valueCollection2.get((Key) registration1.getKey()).get();
            final Object value1 = ((Processor) registration1).getFrom(container1);
            final Object value2 = ((Processor) registration2).getFrom(container2);
            if (!Objects.equals(value1, value2)) {
                return false;
            }
        }
        return true;
    }
}
