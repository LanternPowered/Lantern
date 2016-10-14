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

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

@SuppressWarnings({"unchecked","rawtypes"})
public class LanternBlockTrait<T extends Comparable<T>> implements BlockTrait<T> {

    private final Key<? extends Value<T>> key;
    private final ImmutableSet<T> possibleValues;
    private final Class<T> valueClass;
    private final String name;

    LanternBlockTrait(String name, Key<? extends Value<T>> key, Class<T> valueClass, ImmutableSet<T> possibleValues) {
        this.possibleValues = possibleValues;
        this.valueClass = valueClass;
        this.name = name;
        this.key = key;
    }

    /**
     * Gets the block trait key.
     * 
     * @return the block trait key
     */
    public Key<? extends Value<T>> getKey() {
        return this.key;
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Collection<T> getPossibleValues() {
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
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", this.name)
                .add("key", this.key)
                .add("valueClass", this.valueClass)
                .add("possibleValues", Arrays.toString(this.possibleValues.toArray(new Object[this.possibleValues.size()])))
                .toString();
    }
}
