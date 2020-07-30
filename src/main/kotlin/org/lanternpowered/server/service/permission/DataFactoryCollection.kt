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

import org.lanternpowered.api.util.collections.concurrentHashMapOf
import org.lanternpowered.api.util.optional.emptyOptional
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.permission.MemorySubjectData
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.service.permission.SubjectCollection
import org.spongepowered.api.service.permission.SubjectReference
import org.spongepowered.api.util.Tristate
import java.util.Optional

internal class DataFactoryCollection(
        identifier: String,
        private val service: LanternPermissionService,
        private val dataFactory: (String) -> MemorySubjectData
) : LanternSubjectCollection(identifier, service) {

    private val subjects = concurrentHashMapOf<String, LanternSubject>()

    override fun get(identifier: String): LanternSubject =
            this.subjects.computeIfAbsent(identifier) { id -> DataFactorySubject(id, this.dataFactory(id)) }

    override fun isRegistered(identifier: String): Boolean = this.subjects.containsKey(identifier)
    override fun getLoadedSubjects(): Collection<Subject> = this.subjects.values

    private inner class DataFactorySubject internal constructor(
            private val identifier: String,
            private val data: MemorySubjectData
    ) : LanternSubject() {

        override val service: PermissionService
            get() = this@DataFactoryCollection.service

        override fun getIdentifier(): String = this.identifier
        override fun getFriendlyIdentifier(): Optional<String> = emptyOptional()
        override fun getContainingCollection(): SubjectCollection = this@DataFactoryCollection
        override fun getSubjectData(): MemorySubjectData = this.data

        override fun asSubjectReference(): SubjectReference =
                this@DataFactoryCollection.service.newSubjectReference(this@DataFactoryCollection.identifier, this.getIdentifier())

        override fun getPermissionValue(contexts: Set<Context>, permission: String): Tristate {
            var result = super.getPermissionValue(contexts, permission)
            if (result == Tristate.UNDEFINED)
                result = this.getDataPermissionValue(this@DataFactoryCollection.defaults.transientSubjectData, permission)
            if (result == Tristate.UNDEFINED)
                result = this.getDataPermissionValue(this@DataFactoryCollection.service.defaults.transientSubjectData, permission)
            return result
        }

        override fun getOption(contexts: Set<Context>, key: String): Optional<String> {
            var result = super.getOption(contexts, key)
            if (!result.isPresent)
                result = this.getDataOptionValue(this@DataFactoryCollection.defaults.subjectData, key)
            if (!result.isPresent)
                result = this.getDataOptionValue(this@DataFactoryCollection.service.defaults.subjectData, key)
            return result
        }
    }
}
