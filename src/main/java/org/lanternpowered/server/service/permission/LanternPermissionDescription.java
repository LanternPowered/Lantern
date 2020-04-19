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
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.lanternpowered.server.service.permission.base.LanternSubjectCollection;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tristate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Basic implementation of {@link PermissionDescription}. Can only be used in
 * conjunction with {@link LanternPermissionService}.
 */
final class LanternPermissionDescription implements PermissionDescription {

    private final LanternPermissionService permissionService;
    private final PluginContainer owner;
    private final String id;
    @Nullable private final Text description;

    private LanternPermissionDescription(LanternPermissionService permissionService, String id, PluginContainer owner, @Nullable Text description) {
        super();
        this.permissionService = permissionService;
        this.description = description;
        this.owner = owner;
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Optional<Text> getDescription() {
        return Optional.ofNullable(this.description);
    }

    @Override
    public Map<Subject, Boolean> getAssignedSubjects(String identifier) {
        final SubjectCollection subjects = this.permissionService.get(identifier);
        return subjects.getLoadedWithPermission(this.id);
    }

    @Override
    public CompletableFuture<Map<SubjectReference, Boolean>> findAssignedSubjects(String type) {
        final SubjectCollection subjects = this.permissionService.get(type);
        return subjects.getAllWithPermission(this.id);
    }

    @Override
    public Optional<PluginContainer> getOwner() {
        return Optional.of(this.owner);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LanternPermissionDescription other = (LanternPermissionDescription) obj;
        return this.id.equals(other.id) && this.owner.equals(other.owner) && Objects.equal(this.description, other.description);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("owner", this.owner)
                .add("id", this.id)
                .toString();
    }

    static class Builder implements PermissionDescription.Builder {

        private final Map<String, Tristate> roleAssignments = new LinkedHashMap<>();
        private final LanternPermissionService permissionService;
        private final PluginContainer owner;
        @Nullable private Text description;
        @Nullable private String id;

        Builder(LanternPermissionService permissionService, PluginContainer owner) {
            super();
            this.permissionService = permissionService;
            this.owner = owner;
        }

        @Override
        public Builder id(String id) {
            this.id = checkNotNull(id, "permissionId");
            return this;
        }

        @Override
        public Builder description(@Nullable Text description) {
            this.description = description;
            return this;
        }

        @Override
        public Builder assign(String role, boolean value) {
            Preconditions.checkNotNull(role, "role");
            this.roleAssignments.put(role, Tristate.fromBoolean(value));
            return this;
        }

        @Override
        public LanternPermissionDescription register() throws IllegalStateException {
            checkState(this.id != null, "No id set");
            final LanternPermissionDescription description = new LanternPermissionDescription(
                    this.permissionService, this.id, this.owner, this.description);
            this.permissionService.addDescription(description);

            // Set role-templates
            final LanternSubjectCollection subjects = this.permissionService.get(PermissionService.SUBJECTS_ROLE_TEMPLATE);
            for (Entry<String, Tristate> assignment : this.roleAssignments.entrySet()) {
                final Subject subject = subjects.get(assignment.getKey());
                subject.getTransientSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT, this.id, assignment.getValue());
            }
            return description;
        }
    }

}

