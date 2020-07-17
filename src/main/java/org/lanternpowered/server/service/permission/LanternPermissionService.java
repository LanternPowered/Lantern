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
package org.lanternpowered.server.service.permission;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.lanternpowered.server.service.permission.base.FixedParentMemorySubjectData;
import org.lanternpowered.server.service.permission.base.GlobalMemorySubjectData;
import org.lanternpowered.server.service.permission.base.LanternSubject;
import org.lanternpowered.server.service.permission.base.LanternSubjectCollection;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionDescription.Builder;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Permission service representing the vanilla operator permission structure.
 *
 * <p>Really doesn't do much else. Don't use this guys.
 */
public final class LanternPermissionService implements PermissionService {

    private static final String SUBJECTS_DEFAULT = "default";

    private final Map<String, PermissionDescription> descriptionMap = new LinkedHashMap<>();
    @Nullable private Collection<PermissionDescription> descriptions;
    private final ConcurrentMap<String, LanternSubjectCollection> subjects = new ConcurrentHashMap<>();
    private final LanternSubjectCollection defaultCollection;
    private final LanternSubject defaultData;

    public LanternPermissionService() {
        this.defaultData = new OpLevelCollection.OpLevelSubject(this, 0);
        this.subjects.put(SUBJECTS_DEFAULT, this.defaultCollection = newCollection(SUBJECTS_DEFAULT));
        this.subjects.put(SUBJECTS_USER, new UserCollection(this));
        this.subjects.put(SUBJECTS_GROUP, new OpLevelCollection(this));
        this.subjects.put(SUBJECTS_COMMAND_BLOCK, new DataFactoryCollection(SUBJECTS_COMMAND_BLOCK, this,
                s -> new FixedParentMemorySubjectData(this.defaultData, getGroupForOpLevel(2).asSubjectReference())));
        this.subjects.put(SUBJECTS_SYSTEM, new DataFactoryCollection(SUBJECTS_SYSTEM, this,
                s -> new FixedParentMemorySubjectData(this.defaultData, getGroupForOpLevel(4).asSubjectReference())));
    }

    public Subject getGroupForOpLevel(int level) {
        return getGroupSubjects().get("op_" + level);
    }

    @Override
    public LanternSubjectCollection getUserSubjects() {
        return get(PermissionService.SUBJECTS_USER);
    }

    @Override
    public LanternSubjectCollection getGroupSubjects() {
        return get(PermissionService.SUBJECTS_GROUP);
    }

    private LanternSubjectCollection newCollection(String identifier) {
        checkNotNull(identifier, "identifier");
        return new DataFactoryCollection(identifier, this, s -> new GlobalMemorySubjectData(this.defaultData));
    }

    public LanternSubjectCollection get(String identifier) {
        checkNotNull(identifier, "identifier");
        return this.subjects.computeIfAbsent(identifier, this::newCollection);
    }

    @Override
    public LanternSubject getDefaults() {
        return this.defaultData;
    }

    @Override
    public Predicate<String> getIdentifierValidityPredicate() {
        return s -> true;
    }

    @Override
    public SubjectReference newSubjectReference(String collectionIdentifier, String subjectIdentifier) {
        checkNotNull(collectionIdentifier, "collectionIdentifier");
        checkNotNull(subjectIdentifier, "subjectIdentifier");
        return new LanternSubjectReference(this, collectionIdentifier, subjectIdentifier);
    }

    @Override
    public CompletableFuture<SubjectCollection> loadCollection(String identifier) {
        return CompletableFuture.completedFuture(get(identifier));
    }

    @Override
    public Optional<SubjectCollection> getCollection(String identifier) {
        return Optional.of(get(identifier));
    }

    @Override
    public CompletableFuture<Boolean> hasCollection(String identifier) {
        return CompletableFuture.completedFuture(this.subjects.containsKey(identifier));
    }

    @Override
    public Map<String, SubjectCollection> getLoadedCollections() {
        return ImmutableMap.copyOf(this.subjects);
    }

    @Override
    public CompletableFuture<Set<String>> getAllIdentifiers() {
        return CompletableFuture.completedFuture(getLoadedCollections().keySet());
    }

    @Override
    public void registerContextCalculator(ContextCalculator<Subject> calculator) {
    }

    @Override
    public Builder newDescriptionBuilder(PluginContainer pluginContainer) {
        checkNotNull(pluginContainer, "pluginContainer");
        return new LanternPermissionDescription.Builder(this, pluginContainer);
    }

    void addDescription(PermissionDescription permissionDescription) {
        checkNotNull(permissionDescription, "permissionDescription");
        checkNotNull(permissionDescription.getId(), "permissionId");
        this.descriptionMap.put(permissionDescription.getId().toLowerCase(), permissionDescription);
        this.descriptions = null;
    }

    @Override
    public Optional<PermissionDescription> getDescription(String permissionId) {
        return Optional.ofNullable(this.descriptionMap.get(checkNotNull(permissionId, "permissionId").toLowerCase()));
    }

    @Override
    public Collection<PermissionDescription> getDescriptions() {
        Collection<PermissionDescription> descriptions = this.descriptions;
        if (descriptions == null) {
            descriptions = ImmutableList.copyOf(this.descriptionMap.values());
            this.descriptions = descriptions;
        }
        return descriptions;
    }

    public LanternSubjectCollection getDefaultCollection() {
        return this.defaultCollection;
    }
}
