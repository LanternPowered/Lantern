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
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.collections.toImmutableMap
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.server.service.permission.OpLevelCollection.OpLevelSubject
import org.spongepowered.api.service.context.ContextCalculator
import org.spongepowered.api.service.permission.PermissionDescription
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.service.permission.SubjectCollection
import org.spongepowered.api.service.permission.SubjectReference
import org.spongepowered.plugin.PluginContainer
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.Predicate

/**
 * Permission service representing the vanilla operator permission structure.
 *
 *
 * Really doesn't do much else. Don't use this guys.
 */
class LanternPermissionService : PermissionService {

    companion object {
        private const val SUBJECTS_DEFAULT = "default"
    }

    private val descriptionMap = linkedMapOf<String, PermissionDescription>()
    private val subjects = concurrentHashMapOf<String, LanternSubjectCollection>()
    private val defaultData: LanternSubject
    private var descriptions: Collection<PermissionDescription>? = null

    val defaultCollection: LanternSubjectCollection

    init {
        this.defaultData = OpLevelSubject(this, 0)
        this.defaultCollection = this.newCollection(SUBJECTS_DEFAULT)
        this.subjects[SUBJECTS_DEFAULT] = this.defaultCollection
        this.subjects[PermissionService.SUBJECTS_USER] = UserCollection(this)
        this.subjects[PermissionService.SUBJECTS_GROUP] = OpLevelCollection(this)
        this.subjects[PermissionService.SUBJECTS_COMMAND_BLOCK] =
                DataFactoryCollection(PermissionService.SUBJECTS_COMMAND_BLOCK, this) {
                    FixedParentMemorySubjectData(defaultData, getGroupForOpLevel(2).asSubjectReference())
                }
        this.subjects[PermissionService.SUBJECTS_SYSTEM] =
                DataFactoryCollection(PermissionService.SUBJECTS_SYSTEM, this) {
                    FixedParentMemorySubjectData(defaultData, getGroupForOpLevel(4).asSubjectReference())
                }
    }

    fun getGroupForOpLevel(level: Int): Subject = this.groupSubjects["op_$level"]

    override fun getUserSubjects(): LanternSubjectCollection = this[PermissionService.SUBJECTS_USER]
    override fun getGroupSubjects(): LanternSubjectCollection = this[PermissionService.SUBJECTS_GROUP]

    private fun newCollection(identifier: String): LanternSubjectCollection =
            DataFactoryCollection(identifier, this) { GlobalMemorySubjectData(this.defaultData) }

    operator fun get(identifier: String): LanternSubjectCollection =
            this.subjects.computeIfAbsent(identifier, this@LanternPermissionService::newCollection)

    override fun getDefaults(): LanternSubject = this.defaultData
    override fun getIdentifierValidityPredicate(): Predicate<String> = Predicate { true }

    override fun newSubjectReference(collectionIdentifier: String, subjectIdentifier: String): SubjectReference =
            LanternSubjectReference(this, collectionIdentifier, subjectIdentifier)

    override fun loadCollection(identifier: String): CompletableFuture<SubjectCollection> =
            CompletableFuture.completedFuture(this[identifier])

    override fun getCollection(identifier: String): Optional<SubjectCollection> = this[identifier].optional()

    override fun hasCollection(identifier: String): CompletableFuture<Boolean> =
            CompletableFuture.completedFuture(this.subjects.containsKey(identifier))

    override fun getLoadedCollections(): Map<String, SubjectCollection> = this.subjects.toImmutableMap()
    override fun getAllIdentifiers(): CompletableFuture<Set<String>> = CompletableFuture.completedFuture(this.loadedCollections.keys)

    override fun registerContextCalculator(calculator: ContextCalculator<Subject>) {}

    override fun newDescriptionBuilder(pluginContainer: PluginContainer): PermissionDescription.Builder =
            LanternPermissionDescription.Builder(this, pluginContainer)

    fun addDescription(permissionDescription: PermissionDescription) {
        this.descriptionMap[permissionDescription.id.toLowerCase()] = permissionDescription
        this.descriptions = null
    }

    override fun getDescription(permissionId: String): Optional<PermissionDescription> =
            this.descriptionMap[permissionId.toLowerCase()].optional()

    override fun getDescriptions(): Collection<PermissionDescription> {
        var descriptions = this.descriptions
        if (descriptions == null) {
            descriptions = this.descriptionMap.values.toImmutableList()
            this.descriptions = descriptions
        }
        return descriptions
    }
}
