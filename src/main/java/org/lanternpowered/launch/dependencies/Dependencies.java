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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Dependencies {

    private final List<Repository> repositories;
    private final List<Dependency> dependencies;

    public Dependencies(List<Repository> repositories, List<Dependency> dependencies) {
        this.repositories = Collections.unmodifiableList(new ArrayList<>(repositories));
        this.dependencies = Collections.unmodifiableList(new ArrayList<>(dependencies));
    }

    public List<Repository> getRepositories() {
        return this.repositories;
    }

    public List<Dependency> getDependencies() {
        return this.dependencies;
    }

    @Override
    public String toString() {
        return String.format("Dependencies{Repositories:%s,Dependencies:%s}",
                Arrays.toString(this.repositories.toArray()),
                Arrays.toString(this.dependencies.toArray()));
    }
}
