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
package org.lanternpowered.server.inject.provider;

import com.google.inject.Provider;

import java.io.File;
import java.nio.file.Path;

public abstract class PathAsFileProvider implements Provider<File> {

    protected Provider<Path> path;

    @Override
    public File get() {
        return this.path.get().toFile();
    }
}
