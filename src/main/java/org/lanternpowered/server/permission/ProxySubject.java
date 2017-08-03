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

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.service.LanternServiceListeners;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public interface ProxySubject extends Subject {

    /**
     * Sets the internal {@link SubjectReference}.
     *
     * @param reference The subject reference
     */
    void setInternalSubject(@Nullable SubjectReference reference);

    /**
     * Gets the internal {@link SubjectReference}.
     *
     * @return The internal subject reference
     */
    @Nullable
    SubjectReference getInternalSubject();

    /**
     * Gets the identifier of the subject collection.
     *
     * @return The subject collection identifier
     */
    String getSubjectCollectionIdentifier();

    /**
     * Gets the default {@link Tristate} result
     * for the given permission.
     *
     * @param permission The permission
     * @return The result
     */
    Tristate getPermissionDefault(String permission);

    /**
     * Initializes the {@link ProxySubject}.
     */
    default void initializeSubject() {
        LanternServiceListeners.getInstance().registerExpirableServiceCallback(PermissionService.class, new SubjectSettingCallback(this));
    }

    @Nullable
    default Subject resolveNullableSubject() {
        SubjectReference reference = getInternalSubject();
        if (reference == null) {
            final Optional<PermissionService> optService = Lantern.getGame().getServiceManager().provide(PermissionService.class);
            if (optService.isPresent()) {
                // Try to update the internal subject
                SubjectSettingCallback.apply(this, optService.get());
                // Get the new subject reference, can be null if failed
                reference = getInternalSubject();
            }
        }
        return reference == null ? null : reference.resolve().join();
    }

    default Subject resolveSubject() {
        final Subject subject = resolveNullableSubject();
        if (subject == null) {
            throw new IllegalStateException("No subject present for " + getIdentifier());
        }
        return subject;
    }

    // Delegated methods

    @Override
    default SubjectReference asSubjectReference() {
        return checkNotNull(getInternalSubject(), "No internal subject reference is set");
    }

    @Override
    default SubjectCollection getContainingCollection() {
        return resolveSubject().getContainingCollection();
    }

    @Override
    default SubjectData getSubjectData() {
        return resolveSubject().getSubjectData();
    }

    @Override
    default SubjectData getTransientSubjectData() {
        return resolveSubject().getTransientSubjectData();
    }

    @Override
    default boolean isSubjectDataPersisted() {
        final Subject subject = resolveNullableSubject();
        return subject != null && subject.isSubjectDataPersisted();
    }

    @Override
    default Optional<String> getFriendlyIdentifier() {
        final Subject subject = resolveNullableSubject();
        return subject == null ? Optional.empty() : subject.getFriendlyIdentifier();
    }

    @Override
    default boolean hasPermission(Set<Context> contexts, String permission) {
        final Subject subject = resolveNullableSubject();
        if (subject == null) {
            return getPermissionDefault(permission).asBoolean();
        } else {
            Tristate ret = getPermissionValue(contexts, permission);
            switch (ret) {
                case UNDEFINED:
                    return getPermissionDefault(permission).asBoolean();
                default:
                    return ret.asBoolean();
            }
        }
    }

    @Override
    default Tristate getPermissionValue(Set<Context> contexts, String permission) {
        final Subject subject = resolveNullableSubject();
        return subject == null ? getPermissionDefault(permission) : subject.getPermissionValue(contexts, permission);
    }

    @Override
    default boolean isChildOf(SubjectReference parent) {
        final Subject subject = resolveNullableSubject();
        return subject != null && subject.isChildOf(parent);
    }

    @Override
    default boolean isChildOf(Set<Context> contexts, SubjectReference parent) {
        final Subject subject = resolveNullableSubject();
        return subject != null && subject.isChildOf(contexts, parent);
    }

    @Override
    default List<SubjectReference> getParents() {
        final Subject subject = resolveNullableSubject();
        return subject == null ? Collections.emptyList() : subject.getParents();
    }

    @Override
    default List<SubjectReference> getParents(Set<Context> contexts) {
        final Subject subject = resolveNullableSubject();
        return subject == null ? Collections.emptyList() : subject.getParents(contexts);
    }

    @Override
    default Set<Context> getActiveContexts() {
        final Subject subject = resolveNullableSubject();
        return subject == null ? Collections.emptySet() : subject.getActiveContexts();
    }

    @Override
    default Optional<String> getOption(String key) {
        final Subject subject = resolveNullableSubject();
        return subject == null ? Optional.empty() : subject.getOption(key);
    }

    @Override
    default Optional<String> getOption(Set<Context> contexts, String key) {
        final Subject subject = resolveNullableSubject();
        return subject == null ? Optional.empty() : subject.getOption(contexts, key);
    }
}
