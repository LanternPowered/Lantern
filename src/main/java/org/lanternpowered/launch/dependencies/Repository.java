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
package org.lanternpowered.launch.dependencies;

import static java.util.Objects.requireNonNull;

import java.net.URL;

public final class Repository {

    private final URL url;
    private final String name;

    public Repository(final URL url, final String name) {
        this.url = requireNonNull(url, "url");
        this.name = name;
    }

    public URL getUrl() {
        return this.url;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.format("Repository{URL:%s,Name:%s}", this.url, this.name);
    }
}
