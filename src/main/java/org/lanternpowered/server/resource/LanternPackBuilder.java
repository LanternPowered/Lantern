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
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.resource.Pack;
import org.spongepowered.api.resource.ResourceData;
import org.spongepowered.api.resource.ResourcePath;
import org.spongepowered.api.resource.ResourceProvider;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

@SuppressWarnings({"ConstantConditions", "NullableProblems"})
public class LanternPackBuilder implements Pack.Builder {

    private Text name;
    @Nullable private DataView metadata;

    private final Map<ResourcePath, ResourceData> resources = new HashMap<>();
    @Nullable private Supplier<Map<ResourcePath, ResourceData>> resourcesSupplier;

    @Override
    public Pack.Builder name(Text name) {
        checkNotNull(name, "name");
        this.name = name;
        return this;
    }

    @Override
    public Pack.Builder metadata(DataView metadata) {
        checkNotNull(metadata, "metadata");
        this.metadata = metadata;
        return this;
    }

    @Override
    public Pack.Builder resources(ResourceProvider provider) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pack.Builder resource(ResourcePath path, ResourceData resource) {
        checkNotNull(path, "path");
        checkNotNull(resource, "resource");
        checkState(((LanternResourcePath) path).isFile(), "Resource path must be a file");
        this.resources.put(path, resource);
        return this;
    }

    @Override
    public Pack.Builder resources(Map<ResourcePath, ResourceData> resources) {
        resources.forEach(this::resource);
        return this;
    }

    @Override
    public Pack.Builder resources(Supplier<Map<ResourcePath, ResourceData>> resources) {
        checkNotNull(resources, "resources");
        this.resourcesSupplier = resources;
        return this;
    }

    @Override
    public Pack build() {
        checkState(this.name != null, "The name must be set");
        final ResourcesSupplier supplier = new ResourcesSupplier(
                ImmutableMap.copyOf(this.resources), this.resourcesSupplier);
        return new SuppliedPack(this.name, this.metadata, supplier);
    }

    @Override
    public Pack.Builder from(Pack value) {
        return this;
    }

    @Override
    public Pack.Builder reset() {
        this.name = null;
        this.metadata = null;
        this.resources.clear();
        this.resourcesSupplier = null;
        return this;
    }

    private static final class ResourcesSupplier implements Supplier<Map<ResourcePath, ResourceData>> {

        private final Map<ResourcePath, ResourceData> resources;
        @Nullable private final Supplier<Map<ResourcePath, ResourceData>> supplier;

        private ResourcesSupplier(Map<ResourcePath, ResourceData> resources,
                @Nullable Supplier<Map<ResourcePath, ResourceData>> supplier) {
            this.resources = resources;
            this.supplier = supplier;
        }

        @Override
        public Map<ResourcePath, ResourceData> get() {
            if (this.supplier == null) {
                return this.resources;
            }
            final ImmutableMap.Builder<ResourcePath, ResourceData> builder = ImmutableMap.builder();
            builder.putAll(this.resources);
            final Map<ResourcePath, ResourceData> supplied = this.supplier.get();
            checkNotNull(supplied, "Supplied resources couldn't be found");
            for (Map.Entry<ResourcePath, ResourceData> entry : supplied.entrySet()) {
                checkNotNull(entry.getKey(), "path");
                checkNotNull(entry.getValue(), "resource");
                checkState(((LanternResourcePath) entry.getKey()).isFile(), "Resource path must be a file");
                builder.put(entry.getKey(), entry.getValue());
            }
            return builder.build();
        }
    }
}
