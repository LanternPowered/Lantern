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
package org.lanternpowered.server.block.trait;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Iterables;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.lanternpowered.server.util.ToStringHelper;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.Value;

import java.util.function.Predicate;

import javax.annotation.Nullable;

@SuppressWarnings({"unchecked","rawtypes"})
public abstract class LanternBlockTrait<T extends Comparable<T>, V> extends DefaultCatalogType implements BlockTrait<T> {

    private final static KeyTraitValueTransformer DEFAULT_TRANSFORMER = new KeyTraitValueTransformer() {
        @Override
        public Object toKeyValue(Comparable traitValue) {
            return traitValue;
        }
        @Override
        public Comparable toTraitValue(Object keyValue) {
            return (Comparable) keyValue;
        }
    };

    private final Key<? extends Value<V>> valueKey;
    private final ImmutableCollection<T> possibleValues;
    private final Class<T> valueClass;
    private final KeyTraitValueTransformer<T, V> keyTraitValueTransformer;

    LanternBlockTrait(CatalogKey key, Key<? extends Value<V>> valueKey, Class<T> valueClass, ImmutableCollection<T> possibleValues,
            @Nullable KeyTraitValueTransformer<T, V> keyTraitValueTransformer) {
        super(key);
        this.keyTraitValueTransformer = keyTraitValueTransformer == null ? DEFAULT_TRANSFORMER : keyTraitValueTransformer;
        this.possibleValues = possibleValues;
        this.valueClass = valueClass;
        this.valueKey = valueKey;
    }

    /**
     * Gets the block trait key.
     * 
     * @return the block trait key
     */
    public Key<? extends Value.Mutable<V>> getValueKey() {
        return this.valueKey;
    }

    public KeyTraitValueTransformer<T, V> getKeyTraitValueTransformer() {
        return this.keyTraitValueTransformer;
    }

    @Override
    public ImmutableCollection<T> getPossibleValues() {
        return this.possibleValues;
    }

    @Override
    public Class<T> getValueClass() {
        return this.valueClass;
    }

    @Override
    public Predicate<T> getPredicate() {
        return this.possibleValues::contains;
    }

    @Override
    public ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("valueClass", this.valueClass)
                .add("possibleValues", Iterables.toString(this.possibleValues));
    }
}
