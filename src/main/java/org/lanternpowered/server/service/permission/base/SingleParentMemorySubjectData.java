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

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.checkerframework.checker.nullness.qual.Nullable;

public class SingleParentMemorySubjectData extends GlobalMemorySubjectData {

    @Nullable private SubjectReference parent;

    /**
     * Creates a new subject data instance, using the provided service to request instances of permission subjects.
     *
     * @param subject The subject
     */
    public SingleParentMemorySubjectData(Subject subject) {
        super(subject);
    }

    @Override
    public List<SubjectReference> getParents(Set<Context> contexts) {
        final SubjectReference parent = getParent();
        return contexts.isEmpty() && parent != null ? Collections.singletonList(parent) : Collections.emptyList();
    }

    @Override
    public CompletableFuture<Boolean> addParent(Set<Context> contexts, SubjectReference parent) {
        if (!contexts.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }
        setParent(parent);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> removeParent(Set<Context> contexts, SubjectReference parent) {
        if (parent == this.parent) {
            setParent(null);
            return CompletableFuture.completedFuture(true);
        }
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> clearParents() {
        setParent(null);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> clearParents(Set<Context> contexts) {
        if (!contexts.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }
        return clearParents();
    }

    public void setParent(@Nullable SubjectReference parent) {
        this.parent = parent;
    }

    @Nullable
    public SubjectReference getParent() {
        return this.parent;
    }
}
