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

import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.service.permission.SubjectReference
import java.util.concurrent.CompletableFuture

open class SingleParentMemorySubjectData(subject: Subject) : GlobalMemorySubjectData(subject) {

    open var parent: SubjectReference? = null

    override fun getParents(contexts: Set<Context>): List<SubjectReference> {
        val parent = this.parent
        return if (contexts.isEmpty() && parent != null) listOf(parent) else emptyList()
    }

    override fun addParent(contexts: Set<Context>, parent: SubjectReference): CompletableFuture<Boolean> {
        if (contexts.isNotEmpty())
            return CompletableFuture.completedFuture(false)
        this.parent = parent
        return CompletableFuture.completedFuture(true)
    }

    override fun removeParent(contexts: Set<Context>, parent: SubjectReference): CompletableFuture<Boolean> {
        if (parent == this.parent) {
            this.parent = null
            return CompletableFuture.completedFuture(true)
        }
        return CompletableFuture.completedFuture(false)
    }

    override fun clearParents(): CompletableFuture<Boolean> {
        this.parent = null
        return CompletableFuture.completedFuture(true)
    }

    override fun clearParents(contexts: Set<Context>): CompletableFuture<Boolean> =
            if (contexts.isNotEmpty()) CompletableFuture.completedFuture(false) else clearParents()
}
