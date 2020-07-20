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

public final class LanternLaunch {

    public static void main(String[] args) {
        // Initialize the class loader
        final LanternClassLoader classLoader = LanternClassLoader.get();

        try {
            final Class<?> serverLaunchClass = classLoader.forName("org.lanternpowered.server.LanternServerLaunch", true);
            serverLaunchClass.getMethod("main", String[].class).invoke(null, new Object[] { args });
        } catch (Exception e) {
            LanternClassLoader.sneakyThrow(e);
        }
    }

    private LanternLaunch() {
    }
}
