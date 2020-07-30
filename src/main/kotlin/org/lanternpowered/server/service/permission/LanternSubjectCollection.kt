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

import org.lanternpowered.api.util.collections.asUnmodifiableMap
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.api.util.optional.optional
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.service.permission.SubjectCollection
import org.spongepowered.api.service.permission.SubjectReference
import org.spongepowered.api.util.Tristate
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.Predicate

abstract class LanternSubjectCollection protected constructor(
        private val identifier: String,
        private val service: LanternPermissionService
) : SubjectCollection {

    abstract operator fun get(identifier: String): LanternSubject
    abstract fun isRegistered(identifier: String): Boolean

    override fun getIdentifier(): String = this.identifier
    override fun getIdentifierValidityPredicate(): Predicate<String> = Predicate { true }

    override fun newSubjectReference(subjectIdentifier: String): SubjectReference =
            this.service.newSubjectReference(this.getIdentifier(), subjectIdentifier)

    override fun getSubject(identifier: String): Optional<Subject> =
            this[identifier].optional()

    override fun loadSubject(identifier: String): CompletableFuture<Subject> =
            CompletableFuture.completedFuture(this[identifier])

    override fun hasSubject(identifier: String): CompletableFuture<Boolean> =
            CompletableFuture.completedFuture(this.isRegistered(identifier))

    override fun loadSubjects(identifiers: Set<String>): CompletableFuture<Map<String, Subject>> =
            CompletableFuture.completedFuture(identifiers.associateWith { this[it] }.asUnmodifiableMap())

    override fun getLoadedWithPermission(permission: String): Map<Subject, Boolean> {
        val result = mutableMapOf<Subject, Boolean>()
        for (subject in this.loadedSubjects) {
            val state = subject.getPermissionValue(subject.activeContexts, permission)
            if (state != Tristate.UNDEFINED)
                result[subject] = state.asBoolean()
        }
        return result.asUnmodifiableMap()
    }

    override fun getLoadedWithPermission(contexts: Set<Context>, permission: String): Map<Subject, Boolean> {
        val result = mutableMapOf<Subject, Boolean>()
        for (subject in this.loadedSubjects) {
            val state = subject.getPermissionValue(contexts, permission)
            if (state != Tristate.UNDEFINED)
                result[subject] = state.asBoolean()
        }
        return result.asUnmodifiableMap()
    }

    override fun getAllWithPermission(permission: String): CompletableFuture<Map<SubjectReference, Boolean>> =
            CompletableFuture.completedFuture(this.getLoadedWithPermission(permission)
                    .mapKeys { (subject, _) -> subject.asSubjectReference() }
                    .asUnmodifiableMap())

    override fun getAllWithPermission(contexts: Set<Context>, permission: String): CompletableFuture<Map<SubjectReference, Boolean>> =
            CompletableFuture.completedFuture(this.getLoadedWithPermission(contexts, permission)
                    .mapKeys { (subject, _) -> subject.asSubjectReference() }
                    .asUnmodifiableMap())

    override fun getAllIdentifiers(): CompletableFuture<Set<String>> =
            CompletableFuture.completedFuture(this.loadedSubjects.stream()
                    .map { subject -> subject.identifier }
                    .toImmutableSet())

    override fun getDefaults(): LanternSubject = this.service.defaultCollection[this.getIdentifier()]

    override fun suggestUnload(identifier: String) {
        // not needed since everything is stored in memory.
    }
}
