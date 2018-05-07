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

import javax.annotation.Nullable;

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

