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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.block.trait.IntegerTrait;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

public final class LanternIntegerTrait extends LanternBlockTrait<Integer> implements IntegerTrait {

    private LanternIntegerTrait(CatalogKey key, Key<? extends Value<Integer>> valueKey, ImmutableSet<Integer> possibleValues) {
        super(key, valueKey, Integer.class, possibleValues);
    }

    /**
     * Creates a new integer trait with the specified name and the possible values.
     * 
     * <p>The possible values array may not be empty.</p>
     * 
     * @param key the key
     * @param valueKey the value key that should be attached to the trait
     * @param possibleValues the possible values
     * @return the integer trait
     */
    public static IntegerTrait of(CatalogKey key, Key<? extends Value<Integer>> valueKey, int... possibleValues) {
        checkNotNull(key, "key");
        checkNotNull(possibleValues, "possibleValues");
        checkNotNull(valueKey, "valueKey");
        checkState(possibleValues.length != 0, "possibleValues may not be empty");
        ImmutableSet.Builder<Integer> builder = ImmutableSet.builder();
        for (int possibleValue : possibleValues) {
            builder.add(possibleValue);
        }
        return new LanternIntegerTrait(key, valueKey, builder.build());
    }

    public static IntegerTrait minecraft(String id, Key<? extends Value<Integer>> valueKey, int... possibleValues) {
        return of(CatalogKey.minecraft(id), valueKey, possibleValues);
    }

    /**
     * Creates a new integer trait with the specified name and the possible values.
     * 
     * <p>The possible values array may not be empty.</p>
     * 
     * @param key the key
     * @param valueKey the value key that should be attached to the trait
     * @param possibleValues the possible values
     * @return the integer trait
     */
    public static IntegerTrait of(CatalogKey key, Key<? extends Value<Integer>> valueKey, Iterable<Integer> possibleValues) {
        checkNotNull(key, "key");
        checkNotNull(possibleValues, "possibleValues");
        checkNotNull(valueKey, "valueKey");
        checkState(possibleValues.iterator().hasNext(), "possibleValues may not be empty");
        return new LanternIntegerTrait(key, valueKey, ImmutableSet.copyOf(possibleValues));
    }

    public static IntegerTrait of(String id, Key<? extends Value<Integer>> valueKey, Iterable<Integer> possibleValues) {
        return of(CatalogKey.minecraft(id), valueKey, possibleValues);
    }

    /**
     * Creates a new integer trait with the specified name and the values between
     * the minimum (inclusive) and the maximum (exclusive) value.
     * 
     * <p>The difference between the minimum and the maximum value must
     * be greater then zero.</p>
     * 
     * @param key the key
     * @param valueKey the value key that should be attached to the trait
     * @param min the minimum value
     * @param max the maximum value
     * @return the integer trait
     */
    public static IntegerTrait ofRange(CatalogKey key, Key<? extends Value<Integer>> valueKey, int min, int max) {
        checkNotNull(key, "key");
        checkNotNull(valueKey, "key");
        checkState(max - min > 0, "difference between min and max must be greater then zero");
        ImmutableSet.Builder<Integer> set = ImmutableSet.builder();
        for (int i = min; i <= max; i++) {
            set.add(i);
        }
        return new LanternIntegerTrait(key, valueKey, set.build());
    }

    public static IntegerTrait minecraftRange(String id, Key<? extends Value<Integer>> valueKey, int min, int max) {
        return ofRange(CatalogKey.minecraft(id), valueKey, min, max);
    }

    @Override
    public Optional<Integer> parseValue(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
