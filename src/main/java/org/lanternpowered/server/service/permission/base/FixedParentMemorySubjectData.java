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

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation that forces a single parent to always be part of the parents.
 */
public class FixedParentMemorySubjectData extends GlobalMemorySubjectData {

    private final SubjectReference forcedParent;

    /**
     * Creates a new subject data instance, using the provided service to request instances of permission subjects.
     *
     * @param subject The subject in question
     * @param parent The fixed parent
     */
    public FixedParentMemorySubjectData(Subject subject, SubjectReference parent) {
        super(subject);
        this.forcedParent = parent;
    }

    @Override
    public List<SubjectReference> getParents(Set<Context> contexts) {
        return ImmutableList.<SubjectReference>builder().add(this.forcedParent).addAll(super.getParents(contexts)).build();
    }

    @Override
    public CompletableFuture<Boolean> addParent(Set<Context> contexts, SubjectReference parent) {
        if (Objects.equal(this.forcedParent, parent) && contexts.isEmpty()) {
            return CompletableFuture.completedFuture(true);
        }
        return super.addParent(contexts, parent);
    }

    @Override
    public CompletableFuture<Boolean> removeParent(Set<Context> contexts, SubjectReference parent) {
        if (Objects.equal(this.forcedParent, parent)) {
            return CompletableFuture.completedFuture(false);
        }
        return super.removeParent(contexts, parent);
    }
}
