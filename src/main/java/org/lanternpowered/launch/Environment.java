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
package org.lanternpowered.launch;

import static java.util.Objects.requireNonNull;

/**
 * Represents the environment where
 * the server is run.
 */
public enum Environment {
    DEVELOPMENT,
    PRODUCTION,
    ;

    private static Environment current;

    /**
     * Gets the current {@link Environment}.
     *
     * @return The environment
     */
    public static Environment get() {
        return current;
    }

    /**
     * Sets the current {@link Environment}.
     *
     * @param environment The environment
     */
    static void set(Environment environment) {
        current = requireNonNull(environment, "environment");
    }
}
