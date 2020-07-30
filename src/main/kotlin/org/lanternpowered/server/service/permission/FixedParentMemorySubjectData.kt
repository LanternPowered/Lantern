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

import org.lanternpowered.api.util.collections.immutableListBuilderOf
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.service.permission.SubjectReference
import java.util.concurrent.CompletableFuture

/**
 * Implementation that forces a single parent to always be part of the parents.
 */
class FixedParentMemorySubjectData(
        subject: Subject, private val forcedParent: SubjectReference
) : GlobalMemorySubjectData(subject) {

    override fun getParents(contexts: Set<Context>): List<SubjectReference> =
            immutableListBuilderOf<SubjectReference>().add(this.forcedParent).addAll(super.getParents(contexts)).build()

    override fun addParent(contexts: Set<Context>, parent: SubjectReference): CompletableFuture<Boolean> =
            if (this.forcedParent == parent && contexts.isEmpty()) CompletableFuture.completedFuture(true) else super.addParent(contexts, parent)

    override fun removeParent(contexts: Set<Context>, parent: SubjectReference): CompletableFuture<Boolean> =
            if (this.forcedParent == parent) CompletableFuture.completedFuture(false) else super.removeParent(contexts, parent)
}
