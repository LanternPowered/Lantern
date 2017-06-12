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
import org.lanternpowered.server.data.AbstractImmutableDataHolder;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.data.value.AbstractValueContainer;
import org.lanternpowered.server.data.value.ElementHolder;
import org.lanternpowered.server.data.value.KeyRegistration;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.annotation.Nullable;

public class LanternItemStackSnapshot implements ItemStackSnapshot, AbstractImmutableDataHolder<ItemStackSnapshot>, AbstractPropertyHolder {

    private final Map<Key<?>, KeyRegistration> rawValueMap;
    private final Map<Class<?>, ImmutableDataManipulator<?, ?>> rawAdditionalManipulators;
    private final ItemType itemType;
    // TODO: Hmm, inconsistency the the name with itemstack?
    private final int quantity;

    public LanternItemStackSnapshot(ItemType itemType, int quantity) {
        this(itemType, quantity, new HashMap<>(), new HashMap<>());
        ((LanternItemType) itemType).getKeysProvider().accept(this);
    }

    LanternItemStackSnapshot(ItemType itemType, int quantity, Map<Key<?>, KeyRegistration> rawValueMap,
            Map<Class<?>, ImmutableDataManipulator<?, ?>> rawAdditionalManipulators) {
        this.rawAdditionalManipulators = rawAdditionalManipulators;
        this.rawValueMap = rawValueMap;
        this.itemType = itemType;
        this.quantity = quantity;
    }

    @Nullable
    @Override
    public Map<Class<?>, ImmutableDataManipulator<?,?>> getRawAdditionalContainers() {
        return this.rawAdditionalManipulators;
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
        //noinspection ConstantConditions,Convert2MethodRef
        return new LanternItemStack(this.itemType, this.quantity, copyRawValueMap(),
                copyConvertedRawAdditionalManipulators(ImmutableDataManipulator::asMutable, () -> new ConcurrentHashMap<>()));
    }

    @Override
    public GameDictionary.Entry createGameDictionaryEntry() {
        throw new UnsupportedOperationException("The GameDictionary isn't supported, check first if Game#getGameDictionary is present.");
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        return null;
    }

    @Override
    public Map<Key<?>, KeyRegistration> getRawValueMap() {
        return this.rawValueMap;
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
    public Optional<ItemStackSnapshot> with(BaseValue<?> value) {
        return null;
    }

    @Override
    public Optional<ItemStackSnapshot> with(ImmutableDataManipulator<?, ?> valueContainer) {
        return null;
    }

    @Override
    public Optional<ItemStackSnapshot> with(Iterable<ImmutableDataManipulator<?, ?>> valueContainers) {
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
        return this.itemType == that.getType() && compareRawDataMaps(this, (AbstractValueContainer) that);
    }

    @SuppressWarnings("unchecked")
    static boolean compareRawDataMaps(AbstractValueContainer container1, AbstractValueContainer container2) {
        final Map<Key<?>, KeyRegistration> rawValueMap1 = container1.getRawValueMap();
        final Map<Key<?>, KeyRegistration> rawValueMap2 = container2.getRawValueMap();
        for (Map.Entry<Key<?>, KeyRegistration> entry : rawValueMap1.entrySet()) {
            if (!rawValueMap2.containsKey(entry.getKey())) {
                return false;
            }
            final Object value1;
            final KeyRegistration keyRegistration1 = entry.getValue();
            if (!(keyRegistration1 instanceof ElementHolder)) {
                //noinspection unchecked
                value1 = container1.get((Key) entry.getKey());
            } else {
                value1 = ((ElementHolder) keyRegistration1).get();
            }
            final Object value2;
            final KeyRegistration keyRegistration2 = rawValueMap2.get(entry.getKey());
            if (!(keyRegistration2 instanceof ElementHolder)) {
                //noinspection unchecked
                value2 = container2.get((Key) entry.getKey());
            } else {
                value2 = ((ElementHolder) keyRegistration2).get();
            }
            if (!Objects.equals(value1, value2)) {
                return false;
            }
        }
        return true;
    }
}
