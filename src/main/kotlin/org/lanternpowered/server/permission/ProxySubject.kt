/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.permission

import org.lanternpowered.api.service.ServiceManager
import org.lanternpowered.api.service.provide
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.server.service.LanternServiceListeners
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.service.permission.SubjectCollection
import org.spongepowered.api.service.permission.SubjectData
import org.spongepowered.api.service.permission.SubjectReference
import org.spongepowered.api.util.Tristate
import java.util.Optional

interface ProxySubject : Subject {

    /**
     * The internal [SubjectReference].
     *
     * @return The internal subject reference
     */
    var internalSubject: SubjectReference?

    /**
     * Gets the identifier of the subject collection.
     *
     * @return The subject collection identifier
     */
    val subjectCollectionIdentifier: String

    /**
     * Gets the default [Tristate] result
     * for the given permission.
     *
     * @param permission The permission
     * @return The result
     */
    fun getPermissionDefault(permission: String): Tristate

    /**
     * Initializes the [ProxySubject].
     */
    @JvmDefault
    fun initializeSubject() {
        LanternServiceListeners.getInstance().registerExpirableServiceCallback(PermissionService::class.java, SubjectSettingCallback(this))
    }

    @JvmDefault
    fun resolveNullableSubject(): Subject? {
        var reference = this.internalSubject
        if (reference == null) {
            val service = ServiceManager.provide<PermissionService>()
            if (service != null) {
                // Try to update the internal subject
                SubjectSettingCallback.apply(this, service)
                // Get the new subject reference, can be null if failed
                reference = this.internalSubject
            }
        }
        return reference?.resolve()?.join()
    }

    @JvmDefault
    fun resolveSubject(): Subject {
        return resolveNullableSubject() ?: throw IllegalStateException("No subject present for $identifier")
    }

    // Delegated methods

    @JvmDefault override fun asSubjectReference() = checkNotNull(this.internalSubject) { "No internal subject reference is set" }

    @JvmDefault override fun getContainingCollection(): SubjectCollection = resolveSubject().containingCollection
    @JvmDefault override fun getSubjectData(): SubjectData = resolveSubject().subjectData
    @JvmDefault override fun getTransientSubjectData(): SubjectData = resolveSubject().transientSubjectData
    @JvmDefault override fun isSubjectDataPersisted(): Boolean = resolveNullableSubject()?.isSubjectDataPersisted ?: false
    @JvmDefault override fun getFriendlyIdentifier(): Optional<String> = resolveNullableSubject()?.friendlyIdentifier ?: emptyOptional()

    @JvmDefault
    override fun hasPermission(contexts: Set<Context>, permission: String): Boolean {
        val subject = resolveNullableSubject()
        return if (subject == null) {
            getPermissionDefault(permission).asBoolean()
        } else {
            when (val ret = getPermissionValue(contexts, permission)) {
                Tristate.UNDEFINED -> getPermissionDefault(permission).asBoolean()
                else -> ret.asBoolean()
            }
        }
    }

    @JvmDefault
    override fun getPermissionValue(contexts: Set<Context>, permission: String): Tristate =
            resolveNullableSubject()?.getPermissionValue(contexts, permission) ?: getPermissionDefault(permission)

    @JvmDefault
    override fun isChildOf(parent: SubjectReference) = resolveNullableSubject()?.isChildOf(parent) ?: false

    @JvmDefault
    override fun isChildOf(contexts: Set<Context>, parent: SubjectReference) = resolveNullableSubject()?.isChildOf(contexts, parent) ?: false

    @JvmDefault
    override fun getParents(): List<SubjectReference> = resolveNullableSubject()?.parents ?: emptyList()

    @JvmDefault
    override fun getParents(contexts: Set<Context>): List<SubjectReference> = resolveNullableSubject()?.getParents(contexts) ?: emptyList()

    @JvmDefault
    override fun getActiveContexts(): Set<Context> = resolveNullableSubject()?.activeContexts ?: emptySet()

    @JvmDefault
    override fun getOption(key: String): Optional<String> = resolveNullableSubject()?.getOption(key) ?: emptyOptional()

    @JvmDefault
    override fun getOption(contexts: Set<Context>, key: String): Optional<String> =
            resolveNullableSubject()?.getOption(contexts, key) ?: emptyOptional()
}
