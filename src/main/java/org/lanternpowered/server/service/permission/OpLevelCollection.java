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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.service.permission.base.GlobalMemorySubjectData;
import org.lanternpowered.server.service.permission.base.LanternSubject;
import org.lanternpowered.server.service.permission.base.LanternSubjectCollection;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class OpLevelCollection extends LanternSubjectCollection {

    private final Map<String, OpLevelSubject> levels;

    OpLevelCollection(LanternPermissionService service) {
        super(PermissionService.SUBJECTS_GROUP, service);
        final ImmutableMap.Builder<String, OpLevelSubject> build = ImmutableMap.builder();
        for (int i = 0; i <= 4; ++i) {
            build.put("op_" + i, new OpLevelSubject(service, i));
        }
        this.levels = build.build();
    }

    @Override
    public LanternSubject get(String identifier) {
        final LanternSubject subject = this.levels.get(identifier);
        checkArgument(subject != null, "%s is not a valid op level group name (op_{0,4})", identifier);
        return subject;
    }

    @Override
    public boolean isRegistered(String identifier) {
        return this.levels.containsKey(identifier);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Subject> getLoadedSubjects() {
        return (Collection) this.levels.values();
    }

    static final class OpLevelSubject extends LanternSubject {

        private final LanternPermissionService service;
        private final MemorySubjectData data;
        private final int level;

        OpLevelSubject(LanternPermissionService service, int level) {
            this.service = service;
            this.level = level;
            this.data = new GlobalMemorySubjectData(this) {

                @Override
                public List<SubjectReference> getParents(Set<Context> contexts) {
                    if (!contexts.isEmpty()) {
                        return Collections.emptyList();
                    }
                    if (level == 0) {
                        return super.getParents(contexts);
                    } else {
                        return ImmutableList.<SubjectReference>builder()
                                .add(service.getGroupForOpLevel(level - 1).asSubjectReference())
                                .addAll(super.getParents(contexts))
                                .build();
                    }
                }
            };
        }

        int getOpLevel() {
            return this.level;
        }

        @Override
        public String getIdentifier() {
            return "op_" + this.level;
        }

        @Override
        public Optional<String> getFriendlyIdentifier() {
            return Optional.empty();
        }

        @Override
        public Optional<CommandSource> getCommandSource() {
            return Optional.empty();
        }

        @Override
        public SubjectCollection getContainingCollection() {
            return this.service.getGroupSubjects();
        }

        @Override
        public PermissionService getService() {
            return service;
        }

        @Override
        public SubjectReference asSubjectReference() {
            return this.service.newSubjectReference(getContainingCollection().getIdentifier(), getIdentifier());
        }

        @Override
        public MemorySubjectData getSubjectData() {
            return this.data;
        }
    }
}
