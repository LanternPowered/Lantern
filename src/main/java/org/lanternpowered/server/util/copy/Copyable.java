/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.util.copy;

import com.google.common.reflect.TypeToken;

import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a object that can be copied.
 *
 * @param <T> The type of the object
 */
public interface Copyable<T> {

    /**
     * Creates a copy of this object.
     *
     * @return The copy
     */
    T copy();

    /**
     * Attempts to create a copy of the given object.
     *
     * @param object The object
     * @param <T> The object type
     * @return The copy if successful, otherwise {@link Optional#empty()}
     */
    static <T> Optional<T> copy(T object) {
        return CopyableTypes.copy(object);
    }

    /**
     * Attempts to create a copy of the given object.
     *
     * @param object The object
     * @param <T> The object type
     * @return The copy if successful, otherwise {@link Optional#empty()}
     */
    static <T> T copyOrSelf(T object) {
        return CopyableTypes.copyOrSelf(object);
    }

    /**
     * Registers a copy function for the given
     * {@link Class}.
     *
     * @param type The type token
     * @param copyFunction The copy function
     * @param <T> The copied type
     */
    static <T> void register(Class<T> type, Function<T, T> copyFunction) {
        register(TypeToken.of(type), copyFunction);
    }

    /**
     * Registers a copy function for the given
     * {@link TypeToken}.
     *
     * @param type The type token
     * @param copyFunction The copy function
     * @param <T> The copied type
     */
    static <T> void register(TypeToken<T> type, Function<T, T> copyFunction) {
        CopyableTypes.register(type, copyFunction);
    }
}
