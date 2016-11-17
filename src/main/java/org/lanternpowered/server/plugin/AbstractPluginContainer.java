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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

abstract class AbstractPluginContainer implements PluginContainer {

    private final String id;
    private final Optional<String> name;
    private final Optional<String> version;
    private final Logger logger;

    AbstractPluginContainer(String id, @Nullable String name, @Nullable String version) {
        this.id = checkNotNull(id, "id");
        this.name = Optional.ofNullable(name);
        this.version = Optional.ofNullable(version);
        this.logger = LoggerFactory.getLogger(id);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name.orElse(this.id);
    }

    @Override
    public Optional<String> getVersion() {
        return this.version;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getUrl() {
        return Optional.empty();
    }

    @Override
    public List<String> getAuthors() {
        return Collections.emptyList();
    }

    @Override
    public Optional<Path> getSource() {
        return Optional.empty();
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("Plugin")
                .omitNullValues()
                .add("id", getId())
                .add("name", getName())
                .add("version", getVersion().orElse(null))
                .add("description", getDescription().orElse(null))
                .add("url", getUrl().orElse(null))
                .add("authors", getAuthors().isEmpty() ? null : getAuthors())
                .add("source", getSource().orElse(null))
                .toString();
    }
}
