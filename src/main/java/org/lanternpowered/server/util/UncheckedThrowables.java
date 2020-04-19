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
package org.lanternpowered.server.util;

import org.lanternpowered.server.util.function.ThrowableRunnable;
import org.lanternpowered.server.util.function.ThrowableSupplier;

public final class UncheckedThrowables {

    /**
     * Throws the {@link Throwable} as an unchecked exception.
     *
     * <p>The returned {@link RuntimeException} can be used to make code
     * after the unchecked throw unreachable.
     *
     * @param throwable The throwable to throw
     * @return A runtime exception
     */
    public static RuntimeException throwUnchecked(Throwable throwable) {
        throw0(throwable);
        throw new AssertionError("Unreachable.");
    }

    /**
     * Executes the {@link ThrowableSupplier} and throws all
     * the exceptions as unchecked exceptions.
     *
     * @param supplier The supplier
     * @param <T> The result type of the supplier
     * @return The result from the given supplier
     * @see #throwUnchecked(Throwable)
     */
    public static <T> T doUnchecked(ThrowableSupplier<T, ? extends Throwable> supplier) {
        try {
            return supplier.get();
        } catch (Throwable throwable) {
            throw throwUnchecked(throwable);
        }
    }

    /**
     * Executes the {@link ThrowableRunnable} and throws all
     * the exceptions as unchecked exceptions.
     *
     * @param runnable The runnable
     * @see #throwUnchecked(Throwable)
     */
    public static void doUnchecked(ThrowableRunnable<? extends Throwable> runnable) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            throw throwUnchecked(throwable);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throw0(Throwable t) throws T {
        throw (T) t;
    }

    private UncheckedThrowables() {
    }
}
