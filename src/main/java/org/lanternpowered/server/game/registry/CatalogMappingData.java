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
package org.lanternpowered.server.game.registry;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
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

    public CatalogMappingData(KClass<?> target, Map<String, ?> mappings) {
        this(JvmClassMappingKt.getJavaClass(target), mappings, Collections.emptySet());
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
