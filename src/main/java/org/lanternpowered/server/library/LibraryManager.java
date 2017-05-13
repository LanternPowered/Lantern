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
package org.lanternpowered.server.library;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.lanternpowered.server.LanternClassLoader;
import org.lanternpowered.server.game.DirectoryKeys;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Singleton
public final class LibraryManager {

    private final Path librariesDir;
    private final Set<Path> loadedFiles = new HashSet<>();

    @Inject
    public LibraryManager(@Named(DirectoryKeys.LIBRARIES) Path librariesDir) {
        this.librariesDir = librariesDir;
    }

    public void load() {
        if (!Files.exists(this.librariesDir)) {
            try {
                Files.createDirectories(this.librariesDir);
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }
        try (DirectoryStream<Path> dir = Files.newDirectoryStream(this.librariesDir, path -> path.toString().endsWith(".jar"))) {
            for (Path path : dir) {
                if (this.loadedFiles.add(path)) {
                    LanternClassLoader.get().addURL(path.toUri().toURL());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the libraries: {}", e);
        }
    }
}
