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

import org.spongepowered.api.data.DataView;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.resource.Pack;
import org.spongepowered.api.resource.Resource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

import javax.annotation.Nullable;

public abstract class LanternPack implements Pack, IResourceProvider {

    private final Text name;
    @Nullable private final DataView metadata;
    @Nullable final PluginContainer plugin;

    // Whether the pack is currently active
    private boolean active;

    LanternPack(Text name, @Nullable DataView metadata,
            @Nullable PluginContainer plugin) {
        this.metadata = metadata;
        this.plugin = plugin;
        this.name = name;
    }

    boolean isActive() {
        return this.active;
    }

    void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public Text getName() {
        return this.name;
    }

    @Override
    public Optional<DataView> getMetadata() {
        return Optional.ofNullable(this.metadata);
    }

    /**
     * Reloads all the {@link Resource}s within
     * this pack file system.
     */
    abstract void reload();
}
