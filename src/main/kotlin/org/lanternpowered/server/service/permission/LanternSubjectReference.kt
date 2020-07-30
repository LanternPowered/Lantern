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
package org.lanternpowered.server.service.permission

import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.service.permission.SubjectReference
import java.util.concurrent.CompletableFuture

internal class LanternSubjectReference(
        private val service: LanternPermissionService,
        private val collectionId: String,
        private val subjectId: String
) : SubjectReference {

    private var cache: LanternSubject? = null

    override fun getCollectionIdentifier(): String = this.collectionId
    override fun getSubjectIdentifier(): String = this.subjectId

    @Synchronized
    override fun resolve(): CompletableFuture<Subject> {
        // lazily load
        if (this.cache == null) {
            this.cache = this.service[this.collectionId][this.subjectId]
        }
        return CompletableFuture.completedFuture(this.cache)
    }
}
