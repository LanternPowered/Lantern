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
     * The libraries folder key.
     */
    public static final String LIBRARIES = "libraries-folder";

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
