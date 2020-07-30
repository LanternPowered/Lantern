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
import org.lanternpowered.api.util.collections.immutableMapBuilderOf
import org.lanternpowered.api.util.optional.emptyOptional
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.permission.MemorySubjectData
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.service.permission.SubjectCollection
import org.spongepowered.api.service.permission.SubjectReference
import java.util.Optional

class OpLevelCollection internal constructor(service: LanternPermissionService) : LanternSubjectCollection(PermissionService.SUBJECTS_GROUP, service) {

    private val levels: Map<String, OpLevelSubject>

    init {
        val build = immutableMapBuilderOf<String, OpLevelSubject>()
        for (i in 0..4)
            build.put("op_$i", OpLevelSubject(service, i))
        this.levels = build.build()
    }

    override fun get(identifier: String): LanternSubject {
        val subject: LanternSubject? = this.levels[identifier]
        checkNotNull(subject) { "$identifier is not a valid op level group name (op_{0,4})" }
        return subject
    }

    override fun isRegistered(identifier: String): Boolean = this.levels.containsKey(identifier)
    override fun getLoadedSubjects(): Collection<Subject> = this.levels.values

    internal class OpLevelSubject(
            override val service: LanternPermissionService,
            val opLevel: Int
    ) : LanternSubject() {

        private val data: MemorySubjectData

        override fun getIdentifier(): String = "op_$opLevel"
        override fun getFriendlyIdentifier(): Optional<String> = emptyOptional()
        override fun getContainingCollection(): SubjectCollection = this.service.groupSubjects
        override fun asSubjectReference(): SubjectReference = this.service.newSubjectReference(this.containingCollection.identifier, this.identifier)
        override fun getSubjectData(): MemorySubjectData = this.data

        init {
            this.data = object : GlobalMemorySubjectData(this) {
                override fun getParents(contexts: Set<Context>): List<SubjectReference> {
                    if (contexts.isNotEmpty())
                        return emptyList()
                    return if (opLevel == 0) {
                        super.getParents(contexts)
                    } else {
                        immutableListBuilderOf<SubjectReference>()
                                .add(service.getGroupForOpLevel(opLevel - 1).asSubjectReference())
                                .addAll(super.getParents(contexts))
                                .build()
                    }
                }
            }
        }
    }
}
