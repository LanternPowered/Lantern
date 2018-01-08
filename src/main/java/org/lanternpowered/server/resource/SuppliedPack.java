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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.resource.Resource;
import org.spongepowered.api.resource.ResourceData;
import org.spongepowered.api.resource.ResourcePath;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.Nullable;

public class SuppliedPack extends LanternPack {

    private final Supplier<Map<ResourcePath, ResourceData>> supplier;

    private Map<ResourcePath, Resource> resources = Collections.emptyMap();

    SuppliedPack(Text name, @Nullable DataView metadata,
            Supplier<Map<ResourcePath, ResourceData>> supplier) {
        super(name, metadata, null);
        this.supplier = supplier;
    }

    @Override
    public Collection<Resource> getAllResources() {
        return ImmutableList.copyOf(this.resources.values());
    }

    @Override
    public Stream<ResourcePath> walkResourcePaths(ResourcePath path, int maxDepth) {
        checkNotNull(path, "path");
        final LanternResourcePath path1 = (LanternResourcePath) path;
        // Not possible for files
        if (path1.isFile() || maxDepth <= 0) {
            return Stream.empty();
        }
        int startDepth = path1.getNamesCount();
        // The max depth is increased based on the start path
        maxDepth += startDepth;
        // Start searching at one higher level
        startDepth++;
        // A set to avoid duplicate parent paths
        final Set<ResourcePath> set = new HashSet<>();
        for (ResourcePath resPath : this.resources.keySet()) {
            final LanternResourcePath resPath1 = (LanternResourcePath) resPath;
            int depth = resPath1.getNamesCount();
            // Out of scope or in different directory
            if (depth < startDepth || !resPath1.startsWith(path)) {
                continue;
            }
            if (depth <= maxDepth) {
                set.add(resPath1);
            } else {
                depth = maxDepth;
            }
            // Add parent directories
            if (depth != startDepth) {
                LanternResourcePath parent = resPath1.getParent();
                while (depth-- > startDepth) {
                    set.add(parent);
                    parent = parent.getParent();
                }
            }
        }
        return set.stream();
    }

    @Override
    public Stream<LanternResource> walkResources(ResourcePath path, int maxDepth) {
        checkNotNull(path, "path");
        final LanternResourcePath path1 = (LanternResourcePath) path;
        // Not possible for files
        if (path1.isFile() || maxDepth <= 0) {
            return Stream.empty();
        }
        int startDepth = path1.getNamesCount();
        // The max depth is increased based on the start path
        maxDepth += startDepth;
        // Start searching at one higher level
        startDepth++;
        // A set to avoid duplicate parent paths
        final Set<LanternResource> set = new HashSet<>();
        for (Map.Entry<ResourcePath, Resource> entry : this.resources.entrySet()) {
            final LanternResourcePath resPath = (LanternResourcePath) entry.getKey();
            int depth = resPath.getNamesCount();
            // Out of scope or in different directory
            if (depth >= startDepth && depth <= maxDepth && resPath.startsWith(path)) {
                set.add((LanternResource) entry.getValue());
            }
        }
        return set.stream();
    }

    @Override
    public Optional<Resource> getResource(ResourcePath path) {
        checkNotNull(path, "path");
        return Optional.ofNullable(this.resources.get(path));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    void reload() {
        this.resources = Maps.transformEntries(this.supplier.get(),
                (key, value) -> new LanternResource(key, this, value));
    }
}
