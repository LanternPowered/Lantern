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

import org.lanternpowered.server.service.permission.base.LanternSubject;
import org.lanternpowered.server.service.permission.base.LanternSubjectCollection;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

final class DataFactoryCollection extends LanternSubjectCollection {

    private final LanternPermissionService service;
    private final ConcurrentMap<String, LanternSubject> subjects = new ConcurrentHashMap<>();
    private final Function<String, MemorySubjectData> dataFactory;

    DataFactoryCollection(String identifier, LanternPermissionService service,
            Function<String, MemorySubjectData> dataFactory) {
        super(identifier, service);
        this.dataFactory = dataFactory;
        this.service = service;
    }

    @Override
    public LanternSubject get(String identifier) {
        checkNotNull(identifier, "identifier");
        return this.subjects.computeIfAbsent(identifier, id -> new DataFactorySubject(id, this.dataFactory.apply(id)));
    }

    @Override
    public boolean isRegistered(String identifier) {
        checkNotNull(identifier, "identifier");
        return this.subjects.containsKey(identifier);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Subject> getLoadedSubjects() {
        return (Collection) this.subjects.values();
    }

    private final class DataFactorySubject extends LanternSubject {

        private final String identifier;
        private final MemorySubjectData data;

        DataFactorySubject(String identifier, MemorySubjectData data) {
            this.identifier = identifier;
            this.data = data;
        }

        @Override
        public String getIdentifier() {
            return this.identifier;
        }

        @Override
        public Optional<String> getFriendlyIdentifier() {
            return Optional.empty();
        }

        @Override
        public SubjectCollection getContainingCollection() {
            return DataFactoryCollection.this;
        }

        @Override
        public PermissionService getService() {
            return DataFactoryCollection.this.service;
        }

        @Override
        public SubjectReference asSubjectReference() {
            return DataFactoryCollection.this.service.newSubjectReference(DataFactoryCollection.this.getIdentifier(), getIdentifier());
        }

        @Override
        public MemorySubjectData getSubjectData() {
            return this.data;
        }

        @Override
        public Tristate getPermissionValue(Set<Context> contexts, String permission) {
            Tristate ret = super.getPermissionValue(contexts, permission);
            if (ret == Tristate.UNDEFINED) {
                ret = getDataPermissionValue(DataFactoryCollection.this.getDefaults().getTransientSubjectData(), permission);
            }
            if (ret == Tristate.UNDEFINED) {
                ret = getDataPermissionValue(DataFactoryCollection.this.service.getDefaults().getTransientSubjectData(), permission);
            }
            return ret;
        }

        @Override
        public Optional<String> getOption(Set<Context> contexts, String option) {
            Optional<String> ret = super.getOption(contexts, option);
            if (!ret.isPresent()) {
                ret = getDataOptionValue(DataFactoryCollection.this.getDefaults().getSubjectData(), option);
            }
            if (!ret.isPresent()) {
                ret = getDataOptionValue(DataFactoryCollection.this.service.getDefaults().getSubjectData(), option);
            }
            return ret;
        }
    }
}
