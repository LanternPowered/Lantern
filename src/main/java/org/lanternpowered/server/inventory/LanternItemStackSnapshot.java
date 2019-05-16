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
import com.google.common.collect.Streams;
import org.lanternpowered.server.data.AdditionalContainerCollection;
import org.lanternpowered.server.data.AdditionalContainerHolder;
import org.lanternpowered.server.data.DataQueries;
import org.lanternpowered.server.data.IImmutableDataHolder;
import org.lanternpowered.server.data.IValueContainer;
import org.lanternpowered.server.data.MutableToImmutableManipulatorCollection;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.property.IStorePropertyHolder;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.translation.Translation;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public final class LanternItemStackSnapshot implements ItemStackSnapshot, IImmutableDataHolder<ItemStackSnapshot>,
        IStorePropertyHolder, AdditionalContainerHolder<ImmutableDataManipulator<?,?>> {

    /**
     * Gets the {@link ItemStackSnapshot.empty()} as a {@link LanternItemStackSnapshot}.
     *
     * @return The none item stack snapshot
     */
    public static LanternItemStackSnapshot none() {
        return (LanternItemStackSnapshot) ItemStackSnapshot.empty();
    }

    /**
     * Creates {@link LanternItemStackSnapshot} by wrapping the {@link ItemStack},
     * this DOES NOT COPY the {@link ItemStack}. Use {@link ItemStack#createSnapshot()}
     * in that case. This method may only be used with extra care, only when the
     * {@link ItemStack} you are working with won't change anymore, is "final".
     *
     * @param itemStack The item stack
     * @return The item stack snapshot
     */
    public static LanternItemStackSnapshot wrap(ItemStack itemStack) {
        checkNotNull(itemStack, "itemStack");
        // Reuse the none item stack snapshot if possible
        if (itemStack.isEmpty()) {
            return (LanternItemStackSnapshot) ItemStackSnapshot.empty();
        }
        return new LanternItemStackSnapshot((LanternItemStack) itemStack);
    }

    final LanternItemStack itemStack;
    @Nullable private AdditionalContainerCollection<ImmutableDataManipulator<?, ?>> additionalContainers;

    LanternItemStackSnapshot(LanternItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public ValueCollection getValueCollection() {
        return this.itemStack.getValueCollection();
    }

    @Override
    public AdditionalContainerCollection<ImmutableDataManipulator<?,?>> getAdditionalContainers() {
        if (this.additionalContainers == null) {
            this.additionalContainers = new MutableToImmutableManipulatorCollection(this.itemStack.getAdditionalContainers());
        }
        return this.additionalContainers;
    }

    @Override
    public ItemType getType() {
        return this.itemStack.getType();
    }

    @Override
    public int getQuantity() {
        return this.itemStack.getQuantity();
    }

    @Override
    public boolean isEmpty() {
        return this.itemStack.isEmpty();
    }

    @Override
    public ItemStack createStack() {
        return this.itemStack.copy();
    }

    @Override
    public Translation getTranslation() {
        return this.itemStack.getTranslation();
    }

    @Override
    public DataContainer toContainer() {
        return IImmutableDataHolder.super.toContainer()
                .set(DataQueries.ITEM_TYPE, getType())
                .set(DataQueries.QUANTITY, getQuantity());
    }

    @Override
    public <E> Optional<ItemStackSnapshot> transform(Key<? extends Value<E>> key, Function<E, E> function) {
        final LanternItemStack copy = this.itemStack.copy();
        if (copy.transformFast(key, function)) {
            return Optional.of(new LanternItemStackSnapshot(copy));
        }
        return Optional.empty();
    }

    @Override
    public <E> Optional<ItemStackSnapshot> with(Key<? extends Value<E>> key, E value) {
        final LanternItemStack copy = this.itemStack.copy();
        if (copy.offerFast(key, value)) {
            return Optional.of(new LanternItemStackSnapshot(copy));
        }
        return Optional.empty();
    }

    @Override
    public Optional<ItemStackSnapshot> with(ImmutableDataManipulator<?, ?> valueContainer) {
        final LanternItemStack copy = this.itemStack.copy();
        if (copy.offerFast(valueContainer.asMutable())) {
            return Optional.of(new LanternItemStackSnapshot(copy));
        }
        return Optional.empty();
    }

    @Override
    public Optional<ItemStackSnapshot> with(Iterable<ImmutableDataManipulator<?, ?>> valueContainers) {
        final LanternItemStack copy = this.itemStack.copy();
        if (copy.offerFast(Streams.stream(valueContainers)
                .map(ImmutableDataManipulator::asMutable)
                .collect(Collectors.toList()))) {
            return Optional.of(new LanternItemStackSnapshot(copy));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<ItemStackSnapshot> without(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        final LanternItemStack copy = this.itemStack.copy();
        final DataRegistration registration = Lantern.getGame().getDataManager().get(containerClass)
                .orElseThrow(() -> new IllegalStateException("The container class " + containerClass.getName() + " isn't registered."));
        if (copy.removeFast(registration.getManipulatorClass())) {
            return Optional.of(new LanternItemStackSnapshot(copy));
        }
        return Optional.empty();
    }

    @Override
    public ItemStackSnapshot merge(ItemStackSnapshot that, MergeFunction function) {
        final LanternItemStack copy = this.itemStack.copy();
        copy.copyFromNoEvents(((LanternItemStackSnapshot) that).itemStack, function);
        return new LanternItemStackSnapshot(copy);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof LanternItemStackSnapshot)) {
            return false;
        }
        final LanternItemStackSnapshot o = (LanternItemStackSnapshot) other;
        return o.itemStack.equalTo(this.itemStack);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", getType().getKey())
                .add("quantity", getQuantity())
                .add("data", IValueContainer.valuesToString(this.itemStack))
                .toString();
    }

    /**
     * Gets whether the specified {@link ItemStackSnapshot} is similar
     * to this {@link ItemStackSnapshot}. The {@link ItemType} and all
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
     * to this {@link ItemStackSnapshot}. The {@link ItemType} and all
     * the applied data must match.
     *
     * @param that The other snapshot
     * @return Is similar
     */
    public boolean similarTo(ItemStack that) {
        checkNotNull(that, "that");
        return getType() == that.getType() && IValueContainer.matchContents(this.itemStack, (IValueContainer) that);
    }

    /**
     * Gets the internal {@link LanternItemStack},
     * internal use only to avoid copying. The returned
     * stack may never me modified.
     *
     * @return The internal stack
     */
    public LanternItemStack unwrap() {
        return this.itemStack;
    }
}
