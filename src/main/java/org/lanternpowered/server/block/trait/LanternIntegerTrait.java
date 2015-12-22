/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import org.spongepowered.api.block.trait.IntegerTrait;

import com.google.common.collect.ImmutableSet;

public final class LanternIntegerTrait extends LanternBlockTrait<Integer> implements IntegerTrait {

    private LanternIntegerTrait(String name, ImmutableSet<Integer> possibleValues) {
        super(name, Integer.class, possibleValues);
    }

    /**
     * Creates a new integer trait with the specified name and the possible values.
     * 
     * <p>The possible values array may not be empty.</p>
     * 
     * @param name the name
     * @param possibleValues the possible values
     * @return the integer trait
     */
    public static IntegerTrait of(String name, int... possibleValues) {
        checkNotNullOrEmpty(name, "name");
        checkNotNull(possibleValues, "possibleValues");
        checkState(possibleValues.length != 0, "possibleValues may not be empty");
        ImmutableSet.Builder<Integer> builder = ImmutableSet.builder();
        for (int i = 0; i < possibleValues.length; i++) {
            builder.add(possibleValues[i]);
        }
        return new LanternIntegerTrait(name, builder.build());
    }

    /**
     * Creates a new integer trait with the specified name and the possible values.
     * 
     * <p>The possible values array may not be empty.</p>
     * 
     * @param name the name
     * @param possibleValues the possible values
     * @return the integer trait
     */
    public static IntegerTrait of(String name, Iterable<Integer> possibleValues) {
        checkNotNullOrEmpty(name, "name");
        checkNotNull(possibleValues, "possibleValues");
        checkState(possibleValues.iterator().hasNext(), "possibleValues may not be empty");
        return new LanternIntegerTrait(name, ImmutableSet.copyOf(possibleValues));
    }

    /**
     * Creates a new integer trait with the specified name and the values between
     * the minimum (inclusive) and the maximum (exclusive) value.
     * 
     * <p>The difference between the minimum and the maximum value must
     * be greater then zero.</p>
     * 
     * @param name the name
     * @param min the minimum value
     * @param max the maximum value
     * @return the integer trait
     */
    public static IntegerTrait ofRange(String name, int min, int max) {
        checkNotNullOrEmpty(name, "name");
        checkState(max - min > 0, "difference between min and max must be greater then zero");
        ImmutableSet.Builder<Integer> set = ImmutableSet.builder();
        for (int i = min; i <= max; i++) {
            set.add(i);
        }
        return new LanternIntegerTrait(name, set.build());
    }
}
