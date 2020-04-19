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
