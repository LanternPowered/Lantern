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

import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.optional.asOptional
import org.spongepowered.api.service.permission.PermissionDescription
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.service.permission.SubjectData
import org.spongepowered.api.service.permission.SubjectReference
import org.spongepowered.api.util.Tristate
import org.spongepowered.plugin.PluginContainer
import java.util.Optional
import java.util.concurrent.CompletableFuture

/**
 * Basic implementation of [PermissionDescription]. Can only be used in
 * conjunction with [LanternPermissionService].
 */
internal class LanternPermissionDescription private constructor(
        private val permissionService: LanternPermissionService,
        private val id: String,
        private val owner: PluginContainer,
        private val description: Text?
) : PermissionDescription {

    override fun getId(): String = this.id
    override fun getDescription(): Optional<Text> = this.description.asOptional()

    override fun getAssignedSubjects(identifier: String): Map<Subject, Boolean> {
        val subjects = this.permissionService[identifier]
        return subjects.getLoadedWithPermission(this.id)
    }

    override fun findAssignedSubjects(type: String): CompletableFuture<Map<SubjectReference, Boolean>> {
        val subjects = this.permissionService[type]
        return subjects.getAllWithPermission(this.id)
    }

    override fun getOwner(): Optional<PluginContainer> = this.owner.asOptional()
    override fun hashCode(): Int = this.id.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (this.javaClass != other.javaClass) {
            return false
        }
        other as LanternPermissionDescription
        return id == other.id && owner == other.owner && description == other.description
    }

    override fun toString(): String = ToStringHelper(this)
            .add("owner", this.owner)
            .add("id", this.id)
            .toString()

    internal class Builder(
            private val permissionService: LanternPermissionService,
            private val owner: PluginContainer
    ) : PermissionDescription.Builder {

        private val roleAssignments = linkedMapOf<String, Tristate>()
        private var description: Text? = null
        private var id: String? = null

        override fun id(id: String): Builder = apply { this.id = id }
        override fun description(description: Text?): Builder = apply { this.description = description }

        override fun assign(role: String, value: Boolean): Builder {
            this.roleAssignments[role] = Tristate.fromBoolean(value)
            return this
        }

        override fun register(): LanternPermissionDescription {
            val id = checkNotNull(this.id) { "No id set" }
            val description = LanternPermissionDescription(this.permissionService, id, this.owner, this.description)
            this.permissionService.addDescription(description)

            // Set role-templates
            val subjects = this.permissionService[PermissionService.SUBJECTS_ROLE_TEMPLATE]
            for ((key, value) in this.roleAssignments)
                subjects[key].transientSubjectData.setPermission(SubjectData.GLOBAL_CONTEXT, id, value)
            return description
        }
    }
}
