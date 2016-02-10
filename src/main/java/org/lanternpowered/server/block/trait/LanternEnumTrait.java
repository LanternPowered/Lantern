/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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

import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;

@SuppressWarnings("unchecked")
public final class LanternEnumTrait<E extends Enum<E>> extends LanternBlockTrait<E> implements EnumTrait<E> {

    private LanternEnumTrait(String name, Class<E> valueClass, Key<? extends Value<E>> key, ImmutableSet<E> possibleValues) {
        super(name, key, valueClass, possibleValues);
    }

    /**
     * Creates a new enum trait with the specified name and the possible values.
     * 
     * <p>The possible values array may not be empty.</p>
     * 
     * @param name the name
     * @param key the key that should be attached to the trait
     * @param possibleValues the possible values
     * @return the enum trait
     */
    public static <E extends Enum<E>> EnumTrait<E> of(String name, Key<? extends Value<E>> key, Iterable<E> possibleValues) {
        checkNotNullOrEmpty(name, "name");
        checkNotNull(possibleValues, "possibleValues");
        checkNotNull(key, "key");
        checkState(possibleValues.iterator().hasNext(), "possibleValues may not be empty");
        return new LanternEnumTrait<>(name, (Class<E>) possibleValues.iterator().getClass(),
                key, ImmutableSet.copyOf(possibleValues));
    }

    /**
     * Creates a new enum trait with the specified name and all the values from the enum.
     * 
     * <p>The enum must contain values.</p>
     * 
     * @param name the name
     * @param key the key that should be attached to the trait
     * @param enumClass the enum class
     * @return the enum trait
     */
    public static <E extends Enum<E>> EnumTrait<E> of(String name, Key<? extends Value<E>> key, Class<E> enumClass) {
        checkNotNullOrEmpty(name, "name");
        checkNotNull(enumClass, "enumClass");
        checkNotNull(key, "key");
        checkState(enumClass.getEnumConstants().length != 0, "enumClass must contain values");
        return new LanternEnumTrait<>(name, enumClass, key, ImmutableSet.copyOf(enumClass.getEnumConstants()));
    }

}
