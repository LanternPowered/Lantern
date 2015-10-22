/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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
package org.lanternpowered.server.permission;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.context.Context;
import org.spongepowered.api.util.Tristate;

public abstract class SubjectBase implements Subject {

    @Nullable
    private Subject thisSubject;

    @Nullable
    private Subject internalSubject() {
        if (this.thisSubject == null) {
            Optional<PermissionService> service = LanternGame.get().getServiceManager().provide(PermissionService.class);
            if (service.isPresent()) {
                SubjectCollection userSubjects = service.get().getSubjects(this.getSubjectCollectionIdentifier());
                if (userSubjects != null) {
                    return this.thisSubject = userSubjects.get(this.getIdentifier());
                }
            }
        }
        return this.thisSubject;
    }

    @Override
    public SubjectCollection getContainingCollection() {
        Subject subj = this.internalSubject();
        if (subj == null) {
            throw new IllegalStateException("No subject present for " + this.getIdentifier());
        }
        return subj.getContainingCollection();
    }

    @Override
    public SubjectData getSubjectData() {
        Subject subj = this.internalSubject();
        if (subj == null) {
            throw new IllegalStateException("No subject present for " + this.getIdentifier());
        }
        return subj.getSubjectData();
    }

    @Override
    public SubjectData getTransientSubjectData() {
        Subject subj = this.internalSubject();
        if (subj == null) {
            throw new IllegalStateException("No subject present for " + this.getIdentifier());
        }
        return subj.getTransientSubjectData();
    }

    @Override
    public boolean hasPermission(Set<Context> contexts, String permission) {
        Subject subj = this.internalSubject();
        if (subj == null) {
            return this.getPermissionDefault(permission).asBoolean();
        } else {
            Tristate ret = this.getPermissionValue(contexts, permission);
            switch (ret) {
                case UNDEFINED:
                    return this.getPermissionDefault(permission).asBoolean();
                default:
                    return ret.asBoolean();
            }
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.hasPermission(this.getActiveContexts(), permission);
    }

    @Override
    public Tristate getPermissionValue(Set<Context> contexts, String permission) {
        Subject subj = this.internalSubject();
        return subj == null ? this.getPermissionDefault(permission) : subj.getPermissionValue(contexts, permission);
    }

    @Override
    public boolean isChildOf(Subject parent) {
        Subject subj = this.internalSubject();
        return subj != null && subj.isChildOf(parent);
    }

    @Override
    public boolean isChildOf(Set<Context> contexts, Subject parent) {
        Subject subj = this.internalSubject();
        return subj != null && subj.isChildOf(contexts, parent);
    }

    @Override
    public List<Subject> getParents() {
        Subject subj = this.internalSubject();
        return subj == null ? Collections.<Subject>emptyList() : subj.getParents();
    }

    @Override
    public List<Subject> getParents(Set<Context> contexts) {
        Subject subj = this.internalSubject();
        return subj == null ? Collections.<Subject>emptyList() : subj.getParents(contexts);
    }

    @Override
    public Set<Context> getActiveContexts() {
        Subject subj = this.internalSubject();
        return subj == null ? Collections.<Context>emptySet() : subj.getActiveContexts();
    }

    protected abstract String getSubjectCollectionIdentifier();

    protected abstract Tristate getPermissionDefault(String permission);

}
