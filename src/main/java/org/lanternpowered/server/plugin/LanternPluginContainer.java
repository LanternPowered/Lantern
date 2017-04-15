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
package org.lanternpowered.server.plugin;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import org.lanternpowered.server.inject.plugin.PluginModule;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

public final class LanternPluginContainer extends AbstractPluginContainer {

    private final ImmutableList<String> authors;
    private final Object instance;
    private final Injector injector;

    @Nullable private final String description;
    @Nullable private final String url;

    @Nullable private final Path source;

    LanternPluginContainer(Injector injector, String id, Class<?> pluginClass, @Nullable String name, @Nullable String version,
            @Nullable String description, @Nullable String url, List<String> authors, @Nullable Path source) {
        super(id, name, version);
        this.authors = ImmutableList.copyOf(authors);
        this.description = description;
        this.source = source;
        this.url = url;

        this.injector = injector.createChildInjector(new PluginModule(this, pluginClass));
        this.instance = Optional.of(this.injector.getInstance(pluginClass));
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.ofNullable(this.description);
    }

    @Override
    public Optional<String> getUrl() {
        return Optional.ofNullable(this.url);
    }

    @Override
    public List<String> getAuthors() {
        return this.authors;
    }

    @Override
    public Optional<Path> getSource() {
        return Optional.ofNullable(this.source);
    }

    @Override
    public Optional<?> getInstance() {
        return Optional.of(this.instance);
    }

    public Injector getInjector() {
        return this.injector;
    }
}
