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

import com.google.common.base.MoreObjects;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.resource.Pack;
import org.spongepowered.api.resource.Resource;
import org.spongepowered.api.resource.ResourceData;
import org.spongepowered.api.resource.ResourcePath;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public final class LanternResource implements Resource, Comparable<LanternResource> {

    private final LanternResourcePath resourcePath;
    private final ResourceData resourceData;
    private final Pack pack;

    LanternResource(ResourcePath resourcePath, Pack pack, ResourceData resourceData) {
        this.resourcePath = (LanternResourcePath) resourcePath;
        this.resourceData = resourceData;
        this.pack = pack;
    }

    @Override
    public LanternResourcePath getResourcePath() {
        return this.resourcePath;
    }

    @Override
    public Pack getPack() {
        return this.pack;
    }

    @Override
    public InputStream openStream() throws IOException {
        return this.resourceData.openStream();
    }

    @Override
    public Optional<DataView> getMetadata() throws IOException {
        return this.resourceData.getMetadata();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("pack", this.pack.getName().toPlain())
                .add("path", this.resourcePath)
                .add("data", this.resourceData instanceof IResourceData ? this.resourceData : null)
                .omitNullValues()
                .toString();
    }

    @Override
    public int compareTo(LanternResource o) {
        return this.resourcePath.compareTo(o.resourcePath);
    }
}
