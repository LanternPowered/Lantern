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

import org.lanternpowered.api.util.collections.immutableMapOf
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.permission.MemorySubjectData
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.service.permission.SubjectData
import org.spongepowered.api.service.permission.SubjectReference
import org.spongepowered.api.util.Tristate
import java.util.concurrent.CompletableFuture

open class GlobalMemorySubjectData(subject: Subject) : MemorySubjectData(subject) {

    override fun getAllParents(): Map<Set<Context>, List<SubjectReference>> =
            immutableMapOf(SubjectData.GLOBAL_CONTEXT to this.getParents(SubjectData.GLOBAL_CONTEXT))

    override fun setPermission(contexts: Set<Context>, permission: String, value: Tristate): CompletableFuture<Boolean> =
            if (contexts.isNotEmpty()) CompletableFuture.completedFuture(false) else super.setPermission(contexts, permission, value)

    override fun clearPermissions(contexts: Set<Context>): CompletableFuture<Boolean> =
            if (contexts.isNotEmpty()) CompletableFuture.completedFuture(false) else super.clearPermissions(contexts)

    override fun addParent(contexts: Set<Context>, parent: SubjectReference): CompletableFuture<Boolean> =
            if (contexts.isNotEmpty()) CompletableFuture.completedFuture(false) else super.addParent(contexts, parent)

    override fun removeParent(contexts: Set<Context>, parent: SubjectReference): CompletableFuture<Boolean> =
            if (contexts.isNotEmpty()) CompletableFuture.completedFuture(false) else super.removeParent(contexts, parent)

    override fun clearParents(contexts: Set<Context>): CompletableFuture<Boolean> =
            if (contexts.isNotEmpty()) CompletableFuture.completedFuture(false) else super.clearParents(contexts)
}
