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
package org.lanternpowered.server.permission;

import org.lanternpowered.server.service.LanternServiceListeners;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.util.Tristate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public interface AbstractSubject extends Subject {

    void setInternalSubject(@Nullable Subject subj);

    @Nullable
    Subject getInternalSubject();

    String getSubjectCollectionIdentifier();

    Tristate getPermissionDefault(String permission);

    default void initSubject() {
        LanternServiceListeners.getInstance().registerExpirableServiceCallback(PermissionService.class, new SubjectSettingCallback(this));
    }

    default void findPermissionSubject() {
        Sponge.getServiceManager().provide(PermissionService.class).ifPresent(service -> SubjectSettingCallback.apply(this, service));
    }

    @Override
    default SubjectCollection getContainingCollection() {
        final Subject subject = this.getInternalSubject();
        if (subject == null) {
            throw new IllegalStateException("No subject present for " + this.getIdentifier());
        }
        return subject.getContainingCollection();
    }

    @Override
    default SubjectData getSubjectData() {
        final Subject subject = this.getInternalSubject();
        if (subject == null) {
            throw new IllegalStateException("No subject present for " + this.getIdentifier());
        }
        return subject.getSubjectData();
    }

    @Override
    default SubjectData getTransientSubjectData() {
        final Subject subject = this.getInternalSubject();
        if (subject == null) {
            throw new IllegalStateException("No subject present for " + this.getIdentifier());
        }
        return subject.getTransientSubjectData();
    }

    @Override
    default boolean hasPermission(Set<Context> contexts, String permission) {
        final Subject subject = this.getInternalSubject();
        if (subject == null) {
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
    default boolean hasPermission(String permission) {
        return this.hasPermission(this.getActiveContexts(), permission);
    }

    @Override
    default Tristate getPermissionValue(Set<Context> contexts, String permission) {
        final Subject subject = this.getInternalSubject();
        return subject == null ? this.getPermissionDefault(permission) : subject.getPermissionValue(contexts, permission);
    }

    @Override
    default boolean isChildOf(Subject parent) {
        final Subject subject = this.getInternalSubject();
        return subject != null && subject.isChildOf(parent);
    }

    @Override
    default boolean isChildOf(Set<Context> contexts, Subject parent) {
        final Subject subject = this.getInternalSubject();
        return subject != null && subject.isChildOf(contexts, parent);
    }

    @Override
    default List<Subject> getParents() {
        final Subject subject = this.getInternalSubject();
        return subject == null ? Collections.emptyList() : subject.getParents();
    }

    @Override
    default List<Subject> getParents(Set<Context> contexts) {
        final Subject subject = this.getInternalSubject();
        return subject == null ? Collections.emptyList() : subject.getParents(contexts);
    }

    @Override
    default Set<Context> getActiveContexts() {
        final Subject subject = this.getInternalSubject();
        return subject == null ? Collections.emptySet() : subject.getActiveContexts();
    }

    @Override
    default Optional<String> getOption(String key) {
        final Subject subject = this.getInternalSubject();
        return subject == null ? Optional.empty() : subject.getOption(key);
    }

    @Override
    default Optional<String> getOption(Set<Context> contexts, String key) {
        final Subject subject = this.getInternalSubject();
        return subject == null ? Optional.empty() : subject.getOption(contexts, key);
    }
}
