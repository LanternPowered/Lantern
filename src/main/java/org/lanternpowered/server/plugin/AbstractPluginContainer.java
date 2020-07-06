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
package org.lanternpowered.server.plugin;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.plugin.PluginContainer;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractPluginContainer implements PluginContainer {

    private final String id;
    private final Logger logger;

    @Nullable private final String name;
    @Nullable private final String version;

    protected AbstractPluginContainer(String id, @Nullable String name, @Nullable String version) {
        this.id = checkNotNull(id, "id");
        this.logger = LoggerFactory.getLogger(id);
        this.version = version;
        this.name = name;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name == null ? this.id : this.name;
    }

    @Override
    public Optional<String> getVersion() {
        return Optional.ofNullable(this.version);
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
    public Optional<?> getInstance() {
        return Optional.empty();
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
