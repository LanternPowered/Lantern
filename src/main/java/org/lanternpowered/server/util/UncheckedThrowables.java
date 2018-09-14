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
