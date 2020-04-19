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

import org.lanternpowered.server.service.permission.base.LanternSubject;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;

import java.util.concurrent.CompletableFuture;

import org.checkerframework.checker.nullness.qual.Nullable;

final class LanternSubjectReference implements SubjectReference {

    private final LanternPermissionService service;
    private final String collectionId;
    private final String subjectId;
    @Nullable private LanternSubject cache = null;

    LanternSubjectReference(LanternPermissionService service, String collectionId, String subjectId) {
        this.service = service;
        this.collectionId = collectionId;
        this.subjectId = subjectId;
    }

    @Override
    public String getCollectionIdentifier() {
        return this.collectionId;
    }

    @Override
    public String getSubjectIdentifier() {
        return this.subjectId;
    }

    @Override
    public synchronized CompletableFuture<Subject> resolve() {
        // lazily load
        if (this.cache == null) {
            this.cache = this.service.get(this.collectionId).get(this.subjectId);
        }
        return CompletableFuture.completedFuture(this.cache);
    }

}
