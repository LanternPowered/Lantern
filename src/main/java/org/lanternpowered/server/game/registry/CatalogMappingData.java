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
package org.lanternpowered.server.game.registry;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class CatalogMappingData {

    private final Class<?> target;
    private final Set<String> ignoredFields;
    private final Map<String, ?> mappings;

    public CatalogMappingData(RegisterCatalog annotation, Map<String, ?> mappings) {
        this(annotation.value(), mappings, ImmutableSet.copyOf(annotation.ignoredFields()));
    }

    public CatalogMappingData(Class<?> target, Map<String, ?> mappings) {
        this(target, mappings, Collections.emptySet());
    }

    public CatalogMappingData(Class<?> target, Map<String, ?> mappings, Set<String> ignoredFields) {
        this.ignoredFields = ImmutableSet.copyOf(checkNotNull(ignoredFields, "ignoredFields"));
        this.mappings = ImmutableMap.copyOf(checkNotNull(mappings, "mappings"));
        this.target = checkNotNull(target, "target");
    }

    public Class<?> getTarget() {
        return this.target;
    }

    public Set<String> getIgnoredFields() {
        return this.ignoredFields;
    }

    public Map<String, ?> getMappings() {
        return this.mappings;
    }
}
