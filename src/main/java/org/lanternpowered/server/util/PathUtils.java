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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

// There is some nasty issues with working with URI's and having
// spaces in directory/file paths. All spaces will be replaced
// by %20 and files can't be found when that is on the path.
// Use this class to avoid deprecated methods across the codebase.
public final class PathUtils {

    /**
     * Fixes the {@link URL} by replacing all the
     * {@code %20} codes back with spaces.
     *
     * @param url The url
     * @return The fixed url
     */
    public static URL fixURL(URL url) {
        try {
            return toURL(new File(url.toURI()));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Converts the {@link URI} into a {@link URL}.
     *
     * @param uri The uri
     * @return The url
     */
    public static URL toURL(URI uri) {
        try {
            return new File(uri).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Converts the {@link Path} into a {@link URL}.
     *
     * @param path The path
     * @return The url
     */
    public static URL toURL(Path path) {
        return toURL(path.toFile());
    }

    /**
     * Converts the {@link Path} into a {@link URL}.
     *
     * @param file The file
     * @return The url
     */
    public static URL toURL(File file) {
        try {
            return file.toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Fixes the {@link Path} by replacing all the
     * {@code %20} codes back with spaces.
     *
     * @param path The path
     * @return The fixed path
     */
    public static Path fixPath(Path path) {
        return toPath(path.toUri());
    }

    /**
     * Converts the {@link URL} into a {@link Path}.
     *
     * @param url The url
     * @return The path
     */
    public static Path toPath(URL url) {
        try {
            return toPath(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Converts the {@link URI} into a {@link Path}.
     *
     * @param uri The uri
     * @return The path
     */
    public static Path toPath(URI uri) {
        return new File(uri).toPath();
    }

    private PathUtils() {
    }
}
