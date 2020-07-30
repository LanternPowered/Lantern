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
import org.spongepowered.api.service.permission.MemorySubjectData
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.service.permission.SubjectData
import org.spongepowered.api.service.permission.SubjectReference
import org.spongepowered.api.util.Tristate
import java.util.Optional

abstract class LanternSubject : Subject {

    abstract val service: PermissionService

    override fun getTransientSubjectData(): MemorySubjectData = this.subjectData

    abstract override fun getSubjectData(): MemorySubjectData

    override fun isSubjectDataPersisted(): Boolean = false

    override fun asSubjectReference(): SubjectReference =
            this.service.newSubjectReference(this.containingCollection.identifier, this.identifier)

    override fun hasPermission(contexts: Set<Context>, permission: String): Boolean =
            this.getPermissionValue(contexts, permission) == Tristate.TRUE

    override fun getPermissionValue(contexts: Set<Context>, permission: String): Tristate =
            this.getDataPermissionValue(this.transientSubjectData, permission)

    protected fun getDataPermissionValue(subject: MemorySubjectData, permission: String?): Tristate {
        var res = subject.getNodeTree(SubjectData.GLOBAL_CONTEXT)[permission]
        if (res === Tristate.UNDEFINED) {
            for (parent in subject.getParents(SubjectData.GLOBAL_CONTEXT)) {
                res = parent.resolve().join().getPermissionValue(SubjectData.GLOBAL_CONTEXT, permission)
                if (res !== Tristate.UNDEFINED) {
                    return res
                }
            }
        }
        return res
    }

    override fun isChildOf(contexts: Set<Context>, parent: SubjectReference): Boolean =
            this.subjectData.getParents(contexts).contains(parent)

    override fun getParents(contexts: Set<Context>): List<SubjectReference> =
            this.subjectData.getParents(contexts)

    protected fun getDataOptionValue(subject: MemorySubjectData, option: String): Optional<String> {
        var result = Optional.ofNullable(subject.getOptions(SubjectData.GLOBAL_CONTEXT)[option])
        if (!result.isPresent) {
            for (parent in subject.getParents(SubjectData.GLOBAL_CONTEXT)) {
                result = parent.resolve().join().getOption(SubjectData.GLOBAL_CONTEXT, option)
                if (result.isPresent)
                    return result
            }
        }
        return result
    }

    override fun getOption(contexts: Set<Context>, key: String): Optional<String> = this.getDataOptionValue(transientSubjectData, key)

    override fun getActiveContexts(): Set<Context> = SubjectData.GLOBAL_CONTEXT
}
