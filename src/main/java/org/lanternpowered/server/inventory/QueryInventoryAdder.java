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
package org.lanternpowered.server.inventory;

import org.spongepowered.api.item.inventory.Inventory;

/**
 * Represents a adder that will be used to collect
 * {@link Inventory}s for query operations.
 */
@FunctionalInterface
public interface QueryInventoryAdder {

    /**
     * Adds the given {@link Inventory} to be checked and possibly
     * added as query result if valid.
     * <p>A {@link Stop} control flow exception will be thrown when it's
     * no longer needed to add more {@link Inventory}s.
     *
     * @param inventory The inventory to add
     * @throws Stop When it's no longer needed to add more inventories
     */
    void add(Inventory inventory) throws Stop;

    /**
     * Represents a flow control exception that will be thrown when
     * it is no longer needed to add {@link Inventory}s to a
     * specific adder.
     */
    final class Stop extends RuntimeException {

        final static Stop INSTANCE = new Stop(); // Internal access only

        private Stop() {
        }

        @Override
        public Throwable fillInStackTrace() {
            setStackTrace(new StackTraceElement[0]);
            return this;
        }
    }
}
