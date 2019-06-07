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
package org.lanternpowered.server.data.processor;

import org.lanternpowered.server.data.IValueContainer;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.value.Value;

import java.util.Optional;

public interface Processor<V extends Value<E>, E> {

    /**
     * Gets the {@link Key}.
     *
     * @return The key
     */
    Key<? extends V> getKey();

    /**
     * Gets whether the target {@link IValueContainer}
     * supports the given {@link Key}.
     *
     * @param valueContainer The value container
     * @return Whether the key is applicable
     */
    boolean isApplicableTo(IValueContainer<?> valueContainer);

    /**
     * Attempts to remove the {@link Key} from
     * the given {@link IValueContainer}.
     *
     * @param valueContainer The value container
     * @return The data transaction result
     */
    DataTransactionResult removeFrom(IValueContainer<?> valueContainer);

    /**
     * Attempts to remove the {@link Key} from
     * the given {@link IValueContainer}.
     *
     * @param valueContainer The value container
     * @return The result
     */
    default boolean removeFastFrom(IValueContainer<?> valueContainer) {
        return removeFrom(valueContainer).isSuccessful();
    }

    /**
     * Attempts to offer the {@link E} for the specified
     * {@link Key} to the target {@link IValueContainer}.
     *
     * @param valueContainer The target value container
     * @param element The element
     * @return The data transaction result
     */
    DataTransactionResult offerTo(IValueContainer<?> valueContainer, E element);

    /**
     * Attempts to offer the {@link E} for the specified
     * {@link Key} to the target {@link IValueContainer}.
     *
     * @param valueContainer The target value container
     * @param element The element
     * @return The result
     */
    default boolean offerFastTo(IValueContainer<?> valueContainer, E element) {
        return offerTo(valueContainer, element).isSuccessful();
    }

    /**
     * Attempts to offer the {@link V} for the specified
     * {@link Key} to the target {@link IValueContainer}.
     *
     * @param valueContainer The target value container
     * @param value The value
     * @return The data transaction result
     */
    DataTransactionResult offerTo(IValueContainer<?> valueContainer, V value);

    /**
     * Attempts to offer the {@link V} for the specified
     * {@link Key} to the target {@link IValueContainer}.
     *
     * @param valueContainer The target value container
     * @param value The value
     * @return The result
     */
    default boolean offerFastTo(IValueContainer<?> valueContainer, V value) {
        return offerTo(valueContainer, value).isSuccessful();
    }

    /**
     * Attempts to get the {@link E} for the specified
     * {@link Key} from the target {@link IValueContainer}.
     *
     * @param valueContainer The target value container
     * @return The element if present, otherwise {@link Optional#empty()}
     */
    Optional<E> getFrom(IValueContainer<?> valueContainer);

    /**
     * Attempts to get the {@link Value} with element {@link E} value for the specified
     * {@link Key} from the target {@link IValueContainer}.
     *
     * @param valueContainer The target value container
     * @return The element if present, otherwise {@link Optional#empty()}
     */
    Optional<V> getValueFrom(IValueContainer<?> valueContainer);

    /**
     * Attempts to create a {@link Value} instance for the
     * given {@link IValueContainer} and {@link E}.
     *
     * @param valueContainer The value container
     * @param element The element
     * @return The value instance
     */
    V createValueFor(IValueContainer<?> valueContainer, E element);
}
