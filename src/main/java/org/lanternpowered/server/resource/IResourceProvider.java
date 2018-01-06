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

import org.spongepowered.api.resource.Resource;
import org.spongepowered.api.resource.ResourcePath;
import org.spongepowered.api.resource.ResourceProvider;

import java.util.Collection;
import java.util.stream.Stream;

public interface IResourceProvider extends ResourceProvider {

    Collection<Resource> getAllResources();

    /**
     * Lists all the {@link ResourcePath}s within the directory
     * {@link ResourcePath}. If it's not a directory nothing will
     * be listed.
     *
     * @param resourcePath The resource path
     * @return The resource paths
     */
    default Stream<ResourcePath> listResourcePaths(ResourcePath resourcePath) {
        return walkResourcePaths(resourcePath, 1);
    }

    /**
     * Walks through the {@link LanternResource}s of the {@link ResourceProvider}. The
     * depth specifies how deep the walked structure can be, this only defines
     * the depth starting inside the {code data/namespace/} directory.
     * It is also allowed to specify wildcards for the namespace.
     * <p>These {@link Stream}s are by default not sorted,
     * call {@link Stream#sorted()} if this is desired.
     *
     * @param resourcePath The resource path
     * @return The stream to walk through the resources
     */
    Stream<ResourcePath> walkResourcePaths(ResourcePath resourcePath, int maxDepth);

    /**
     * Walks through the {@link LanternResource}s of the {@link ResourceProvider}. The
     * depth specifies how deep the walked structure can be, this only defines
     * the depth starting inside the {code data/namespace/} directory.
     * It is also allowed to specify wildcards for the namespace.
     * <p>These {@link Stream}s are by default not sorted,
     * call {@link Stream#sorted()} if this is desired.
     *
     * @param resourcePath The resource path
     * @return The stream to walk through the resources
     */
    default Stream<ResourcePath> walkResourcePaths(ResourcePath resourcePath) {
        return walkResourcePaths(resourcePath, Integer.MAX_VALUE);
    }

    /**
     * Walks through the {@link LanternResource}s of the {@link ResourceProvider}. The
     * depth specifies how deep the walked structure can be, this only defines
     * the depth starting inside the {code data/namespace/} directory.
     * It is also allowed to specify wildcards for the namespace.
     * <p>These {@link Stream}s are by default not sorted,
     * call {@link Stream#sorted()} if this is desired.
     *
     * @param resourcePath The resource path
     * @return The stream to walk through the resources
     */
    Stream<LanternResource> walkResources(ResourcePath resourcePath, int maxDepth);

    /**
     * Walks through the {@link LanternResource}s of the {@link ResourceProvider}. The
     * depth specifies how deep the walked structure can be, this only defines
     * the depth starting inside the {code data/namespace/} directory.
     * It is also allowed to specify wildcards for the namespace.
     * <p>These {@link Stream}s are by default not sorted,
     * call {@link Stream#sorted()} if this is desired.
     *
     * @param resourcePath The resource path
     * @return The stream to walk through the resources
     */
    default Stream<LanternResource> walkResources(ResourcePath resourcePath) {
        return walkResources(resourcePath, Integer.MAX_VALUE);
    }
}
