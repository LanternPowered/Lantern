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

public final class Dependency {

    public static final String SNAPSHOT_TAG = "-SNAPSHOT";

    private final String group;
    private final String name;
    private final String version;

    public Dependency(String group, String name, String version) {
        this.version = requireNonNull(version, "version");
        this.group = requireNonNull(group, "group");
        this.name = requireNonNull(name, "name");
    }

    public String getGroup() {
        return this.group;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public boolean isSnapshot() {
        return this.version.contains(SNAPSHOT_TAG);
    }

    @Override
    public String toString() {
        return String.format("Dependency{Group:%s,Name:%s,Version:%s}", this.group, this.name, this.version);
    }
}
