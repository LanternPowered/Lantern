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
package org.lanternpowered.server.shards.internal;

import com.google.common.base.MoreObjects;
import org.lanternpowered.api.shard.Shard;
import org.lanternpowered.server.shards.dependency.Requirement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class DependencySpec {

    static List<DependencySpec> merge(Iterable<DependencySpec> dependencies) {
        final List<DependencySpec> newDependencies = new ArrayList<>();
        for (DependencySpec spec : dependencies) {
            mergeAndAdd(newDependencies, spec.type, spec.dependencyType, spec.autoAttach);
        }
        return newDependencies;
    }

    /**
     * Adds and merges the given {@link DependencySpec} settings.
     *
     * @param dependencies The dependencies
     * @param type The dependency class
     * @param requiredType The dependency type
     * @param autoAttach Auto attach mode
     */
    static void mergeAndAdd(List<DependencySpec> dependencies,
            Class<? extends Shard> type, Requirement requiredType, boolean autoAttach) {
        final Iterator<DependencySpec> it = dependencies.iterator();
        while (it.hasNext()) {
            final DependencySpec spec = it.next();
            if (spec.getType().isAssignableFrom(type)) { // We got a more specific dependency
                requiredType = merge(spec.getDependencyType(), requiredType);
                autoAttach = spec.getAutoAttach() || autoAttach;
            } else if (type.isAssignableFrom(spec.getType())) { // We got a less specific type
                requiredType = merge(spec.getDependencyType(), requiredType);
                autoAttach = spec.getAutoAttach() || autoAttach;
                type = spec.getType();
            } else {
                continue;
            }
            it.remove();
        }
        dependencies.add(new DependencySpec(type, requiredType, autoAttach));
    }

    private static Requirement merge(Requirement type, Requirement otherType) {
        // Required is dominant
        if (type == Requirement.REQUIRED ||
                otherType == Requirement.REQUIRED) {
            return Requirement.REQUIRED;
        // Then dynamic required
        } else {
            return Requirement.OPTIONAL;
        }
    }

    private final Class<? extends Shard> type;
    private final boolean autoAttach;
    private final Requirement dependencyType;

    DependencySpec(Class<? extends Shard> type, Requirement requiredType, boolean autoAttach) {
        this.dependencyType = requiredType;
        this.autoAttach = autoAttach;
        this.type = type;
    }

    public Requirement getDependencyType() {
        return this.dependencyType;
    }

    public Class<? extends Shard> getType() {
        return this.type;
    }

    public boolean getAutoAttach() {
        return this.autoAttach;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("Dependency")
                .add("component", this.type.getName())
                .add("requireMode", this.dependencyType)
                .add("autoAttach", this.autoAttach)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DependencySpec)) {
            return false;
        }
        final DependencySpec other = (DependencySpec) o;
        return this.type == other.type &&
                this.autoAttach == other.autoAttach &&
                this.dependencyType == other.dependencyType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.autoAttach, this.dependencyType);
    }
}
