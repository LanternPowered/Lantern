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
