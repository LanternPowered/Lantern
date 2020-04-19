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
package org.lanternpowered.server.util.function;

import java.util.function.Function;

/**
 * Similar like the {@link Function} class but allows the
 * {@link #apply(Object)} method to throw a {@link Throwable}
 * of a specific type {@link X}.
 *
 * @param <T> The type of the parameter
 * @param <R> The type of the returned value
 * @param <X> The type of the exception that may be thrown
 */
@FunctionalInterface
public interface ThrowableFunction<T, R, X extends Throwable> {

    R apply(T t) throws X;
}
