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
package org.lanternpowered.server.resource;

import com.google.common.base.Splitter;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.resource.ResourcePath;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public final class LanternResourcePath implements ResourcePath {

    private static final String RAW_PATH_PATTERN = "/?((?:[a-z][a-z0-9_\\-]*/)*(?:[a-z][a-z0-9_\\-.]*)*)";
    private static final String RAW_NAMESPACE_PATTERN = "(" + Plugin.ID_PATTERN.pattern() + ")?";

    public static final Pattern PATH_PATTERN = Pattern.compile(
            '^' + RAW_PATH_PATTERN + '$');
    public static final Pattern NAMESPACE_PATTERN = Pattern.compile(
            '^' + RAW_NAMESPACE_PATTERN + '$');
    public static final Pattern NAMESPACE_PATH_PATTERN = Pattern.compile(
            '^' + RAW_NAMESPACE_PATTERN + ":" + RAW_PATH_PATTERN + "$");

    /**
     * All the files will be matched when using this {@link ResourcePath}.
     */
    public static final LanternResourcePath ALL = new LanternResourcePath("", "");

    public static LanternResourcePath uncheckedOf(String namespace, String path) {
        return new LanternResourcePath(namespace, path);
    }

    private final static Splitter PATH_SPLITTER = Splitter.on('/');

    private final String namespace;
    private final String path;

    // Some cached values, big chance that they will be reused

    @Nullable private List<String> names;
    @Nullable private LanternResourcePath parent;
    private int hashCode;

    LanternResourcePath(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    public List<String> getNames() {
        if (this.names == null) {
            this.names = this.path.isEmpty() ? Collections.emptyList() : PATH_SPLITTER.splitToList(this.path);
        }
        return names;
    }

    public int getNamesCount() {
        return getNames().size();
    }

    /**
     * Gets the parent {@link ResourcePath} of this path. Will
     * return itself if this is the root path.
     *
     * @return The parent
     */
    public LanternResourcePath getParent() {
        if (this.parent == null) {
            // No more parents
            if (this.path.isEmpty()) {
                this.parent = this;
            } else {
                final int index = this.path.lastIndexOf('/');
                // We reached the root path
                if (index == -1) {
                    this.parent = new LanternResourcePath(this.namespace, "");
                } else {
                    this.parent = new LanternResourcePath(this.namespace, this.path.substring(0, index));
                }
            }
        }
        return this.parent;
    }

    /**
     * Gets whether this {@link ResourcePath} starts with the
     * given {@link ResourcePath}. The namespace of the given resource
     * path may be empty and this will be handled as a wildcard
     * to allow every namespace.
     *
     * @param resourcePath The resource path
     * @return Starts with target path
     */
    public boolean startsWith(ResourcePath resourcePath) {
        final LanternResourcePath that = (LanternResourcePath) resourcePath;
        if (!that.namespace.isEmpty() &&
                !this.namespace.equals(that.namespace)) {
            return false;
        }
        // If that is a file, the paths should match
        if (that.isFile()) {
            return this.path.equals(that.path);
        }
        return this.path.startsWith(that.path + '/');
    }

    /**
     * Gets whether this {@link LanternResourcePath}
     * points to a file.
     *
     * @return Is a file
     */
    public boolean isFile() {
        return this.path.indexOf('.') != -1;
    }

    /**
     * Gets whether this {@link LanternResourcePath}
     * points to a directory.
     *
     * @return Is a directory
     */
    public boolean isDirectory() {
        return !isFile();
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = Objects.hash(this.namespace, this.path);
        }
        return this.hashCode;
    }

    @Override
    public String toString() {
        return this.namespace + ':' + this.path;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ResourcePath)) {
            return false;
        }
        final ResourcePath that = (ResourcePath) obj;
        return that.getNamespace().equals(this.namespace) &&
                that.getPath().equals(this.path);
    }

    @Override
    public int compareTo(ResourcePath o) {
        return toString().compareTo(o.toString());
    }
}
