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
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public final class LanternEnumTrait<E extends Enum<E>> extends LanternBlockTrait<E> implements EnumTrait<E> {

    private LanternEnumTrait(CatalogKey key, Class<E> valueClass, Key<? extends Value<E>> valueKey, ImmutableSet<E> possibleValues) {
        super(key, valueKey, valueClass, possibleValues);
    }

    /**
     * Creates a new enum trait with the specified name and the possible values.
     * 
     * <p>The possible values array may not be empty.</p>
     * 
     * @param key the key
     * @param valueKey the value key that should be attached to the trait
     * @param possibleValues the possible values
     * @return the enum trait
     */
    public static <E extends Enum<E>> EnumTrait<E> of(CatalogKey key, Key<? extends Value<E>> valueKey, Iterable<E> possibleValues) {
        checkNotNull(key, "key");
        checkNotNull(possibleValues, "possibleValues");
        checkNotNull(valueKey, "valueKey");
        checkState(possibleValues.iterator().hasNext(), "possibleValues may not be empty");
        return new LanternEnumTrait<>(key, (Class<E>) possibleValues.iterator().getClass(),
                valueKey, ImmutableSet.copyOf(possibleValues));
    }

    public static <E extends Enum<E>> EnumTrait<E> minecraft(String id, Key<? extends Value<E>> valueKey, Iterable<E> possibleValues) {
        return of(CatalogKey.minecraft(id), valueKey, possibleValues);
    }

    /**
     * Creates a new enum trait with the specified name and all the values from the enum.
     * 
     * <p>The enum must contain values.</p>
     * 
     * @param key the key
     * @param valueKey the value key that should be attached to the trait
     * @param enumClass the enum class
     * @return the enum trait
     */
    public static <E extends Enum<E>> EnumTrait<E> of(CatalogKey key, Key<? extends Value<E>> valueKey, Class<E> enumClass) {
        checkNotNull(key, "key");
        checkNotNull(enumClass, "enumClass");
        checkNotNull(valueKey, "valueKey");
        checkState(enumClass.getEnumConstants().length != 0, "enumClass must contain values");
        return new LanternEnumTrait<>(key, enumClass, valueKey, ImmutableSet.copyOf(enumClass.getEnumConstants()));
    }

    public static <E extends Enum<E>> EnumTrait<E> minecraft(String id, Key<? extends Value<E>> valueKey, Class<E> enumClass) {
        return of(CatalogKey.minecraft(id), valueKey, enumClass);
    }

    /**
     * Creates a new enum trait with the specified name and all the values
     * from the enum that match the {@link Predicate}.
     *
     * <p>The enum must contain values.</p>
     *
     * @param key the key
     * @param valueKey the value key that should be attached to the trait
     * @param enumClass the enum class
     * @return the enum trait
     */
    public static <E extends Enum<E>> EnumTrait<E> of(CatalogKey key, Key<? extends Value<E>> valueKey,
            Class<E> enumClass, Predicate<E> predicate) {
        checkNotNull(key, "key");
        checkNotNull(enumClass, "enumClass");
        checkNotNull(valueKey, "valueKey");
        checkState(enumClass.getEnumConstants().length != 0, "enumClass must contain values");
        return new LanternEnumTrait<>(key, enumClass, valueKey, ImmutableSet.copyOf(enumClass.getEnumConstants())
                .stream().filter(predicate).collect(ImmutableSet.toImmutableSet()));
    }

    public static <E extends Enum<E>> EnumTrait<E> minecraft(String id, Key<? extends Value<E>> valueKey,
            Class<E> enumClass, Predicate<E> predicate) {
        return of(CatalogKey.minecraft(id), valueKey, enumClass, predicate);
    }

    /**
     * Creates a new enum trait with the specified name and all the values
     * from the enum that match the {@link Predicate}.
     *
     * <p>The enum must contain values.</p>
     *
     * @param key the key
     * @param valueKey the value key that should be attached to the trait
     * @param value the value
     * @param values the values
     * @return the enum trait
     */
    public static <E extends Enum<E>> EnumTrait<E> of(CatalogKey key, Key<? extends Value<E>> valueKey, E value, E... values) {
        checkNotNull(key, "key");
        checkNotNull(value, "value");
        checkNotNull(values, "values");
        checkNotNull(valueKey, "valueKey");
        checkState(values.length != 0, "enumClass must contain values");
        return new LanternEnumTrait<>(key, (Class) value.getClass(), valueKey,
                ImmutableSet.<E>builder().add(value).add(values).build());
    }

    public static <E extends Enum<E>> EnumTrait<E> minecraft(String id, Key<? extends Value<E>> valueKey, E value, E... values) {
        return of(CatalogKey.minecraft(id), valueKey, value, values);
    }

    @Override
    public Optional<E> parseValue(String value) {
        checkNotNull(value);
        for (E enumValue : getValueClass().getEnumConstants()) {
            if (enumValue.name().equalsIgnoreCase(value)) {
                return Optional.of(enumValue);
            }
        }
        return Optional.empty();
    }
}
