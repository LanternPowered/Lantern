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
package org.lanternpowered.server.service.permission.base;

import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class GlobalMemorySubjectData extends MemorySubjectData {

    /**
     * Creates a new subject data instance, using the provided service to request instances of permission subjects.
     *
     * @param subject The subject to create the data for
     */
    public GlobalMemorySubjectData(Subject subject) {
        super(subject);
    }

    @Override
    public Map<Set<Context>, List<SubjectReference>> getAllParents() {
        return ImmutableMap.of(GLOBAL_CONTEXT, getParents(GLOBAL_CONTEXT));
    }

    @Override
    public CompletableFuture<Boolean> setPermission(Set<Context> contexts, String permission, Tristate value) {
        if (!contexts.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }
        return super.setPermission(contexts, permission, value);
    }

    @Override
    public CompletableFuture<Boolean> clearPermissions(Set<Context> contexts) {
        if (!contexts.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }
        return super.clearPermissions(contexts);
    }

    @Override
    public CompletableFuture<Boolean> addParent(Set<Context> contexts, SubjectReference parent) {
        if (!contexts.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }
        return super.addParent(contexts, parent);
    }

    @Override
    public CompletableFuture<Boolean> removeParent(Set<Context> contexts, SubjectReference parent) {
        if (!contexts.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }
        return super.removeParent(contexts, parent);
    }

    @Override
    public CompletableFuture<Boolean> clearParents(Set<Context> contexts) {
        if (!contexts.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }
        return super.clearParents(contexts);
    }
}
