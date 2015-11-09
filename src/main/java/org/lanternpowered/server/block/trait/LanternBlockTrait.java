/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
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

import java.util.Collection;
import java.util.function.Predicate;

import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.value.mutable.Value;

import com.google.common.collect.ImmutableSet;

@SuppressWarnings({"unchecked","rawtypes"})
public class LanternBlockTrait<T extends Comparable<T>> implements BlockTrait<T> {

    private final BlockTraitKey<T, MutableBlockTraitValue<T>> key;
    private final ImmutableSet<T> possibleValues;
    private final Class<T> valueClass;
    private final String name;

    LanternBlockTrait(String name, Class<T> valueClass, ImmutableSet<T> possibleValues) {
        this.possibleValues = possibleValues;
        this.valueClass = valueClass;
        this.name = name;
        this.key = new BlockTraitKey(this, valueClass, Value.class);
    }

    /**
     * Gets the block trait key.
     * 
     * @return the block trait key
     */
    public BlockTraitKey<T, MutableBlockTraitValue<T>> getKey() {
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
        return input -> this.possibleValues.contains(input);
    }
}
