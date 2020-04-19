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

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.service.permission.LanternPermissionService;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class LanternSubjectCollection implements SubjectCollection {

    private final String identifier;
    protected final LanternPermissionService service;

    protected LanternSubjectCollection(String identifier, LanternPermissionService service) {
        this.identifier = identifier;
        this.service = service;
    }

    public abstract LanternSubject get(String identifier);

    public abstract boolean isRegistered(String identifier);

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public Predicate<String> getIdentifierValidityPredicate() {
        return s -> true;
    }

    @Override
    public SubjectReference newSubjectReference(String subjectIdentifier) {
        return this.service.newSubjectReference(getIdentifier(), subjectIdentifier);
    }

    @Override
    public CompletableFuture<Subject> loadSubject(String identifier) {
        return CompletableFuture.completedFuture(get(identifier));
    }

    @Override
    public Optional<Subject> getSubject(String identifier) {
        return Optional.of(get(identifier));
    }

    @Override
    public CompletableFuture<Boolean> hasSubject(String identifier) {
        return CompletableFuture.completedFuture(isRegistered(identifier));
    }

    @Override
    public CompletableFuture<Map<String, Subject>> loadSubjects(Set<String> identifiers) {
        final Map<String, Subject> ret = new HashMap<>();
        for (String id : identifiers) {
            ret.put(id, get(id));
        }
        return CompletableFuture.completedFuture(Collections.unmodifiableMap(ret));
    }

    @Override
    public Map<Subject, Boolean> getLoadedWithPermission(String permission) {
        final Map<Subject, Boolean> ret = new HashMap<>();
        for (Subject subj : getLoadedSubjects()) {
            Tristate state = subj.getPermissionValue(subj.getActiveContexts(), permission);
            if (state != Tristate.UNDEFINED) {
                ret.put(subj, state.asBoolean());
            }
        }
        return Collections.unmodifiableMap(ret);
    }

    @Override
    public Map<Subject, Boolean> getLoadedWithPermission(Set<Context> contexts, String permission) {
        final Map<Subject, Boolean> ret = new HashMap<>();
        for (Subject subj : getLoadedSubjects()) {
            Tristate state = subj.getPermissionValue(contexts, permission);
            if (state != Tristate.UNDEFINED) {
                ret.put(subj, state.asBoolean());
            }
        }
        return Collections.unmodifiableMap(ret);
    }

    @Override
    public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(String permission) {
        return CompletableFuture.completedFuture(getLoadedWithPermission(permission).entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().asSubjectReference(), Map.Entry::getValue)));
    }

    @Override
    public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(Set<Context> contexts, String permission) {
        return CompletableFuture.completedFuture(getLoadedWithPermission(contexts, permission).entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().asSubjectReference(), Map.Entry::getValue)));
    }

    @Override
    public CompletableFuture<Set<String>> getAllIdentifiers() {
        return CompletableFuture.completedFuture(getLoadedSubjects().stream()
                .map(Subject::getIdentifier)
                .collect(ImmutableSet.toImmutableSet())
        );
    }

    @Override
    public LanternSubject getDefaults() {
        return this.service.getDefaultCollection().get(getIdentifier());
    }

    @Override
    public void suggestUnload(String identifier) {
        // not needed since everything is stored in memory.
    }
}
