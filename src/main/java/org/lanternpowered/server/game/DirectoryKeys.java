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
package org.lanternpowered.server.game;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class DirectoryKeys {

    /**
     * The root folder key.
     */
    public static final String ROOT = "root-folder";

    /**
     * The config folder key.
     */
    public static final String CONFIG = "config-folder";

    /**
     * The plugins folder key.
     */
    public static final String PLUGINS = "plugins-folder";

    /**
     * The world folder key.
     */
    public static final String WORLD = "world-folder";

    /**
     * The root world folder key.
     */
    public static final String ROOT_WORLD = "root-world-folder";

    public static final class DefaultValues {

        /**
         * The root folder {@link Path}.
         */
        public static final Path ROOT = Paths.get("");

        /**
         * The config folder {@link Path}.
         */
        public static final Path CONFIG = ROOT.resolve("config");

        /**
         * The plugins folder {@link Path}.
         */
        public static final Path PLUGINS = ROOT.resolve("plugins");

        /**
         * The libraries folder {@link Path}.
         */
        public static final Path LIBRARIES = ROOT.resolve("libraries");

    }

    private DirectoryKeys() {
    }
}
