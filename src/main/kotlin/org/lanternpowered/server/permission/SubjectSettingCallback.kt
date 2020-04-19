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
package org.lanternpowered.server.permission

import org.lanternpowered.server.service.permission.LanternPermissionService
import org.spongepowered.api.service.permission.PermissionService
import java.lang.ref.WeakReference
import java.util.function.Predicate

internal class SubjectSettingCallback(ref: ProxySubject) : Predicate<PermissionService> {

    private val ref = WeakReference(ref)

    override fun test(input: PermissionService) = apply(this.ref.get(), input)

    companion object {

        fun apply(ref: ProxySubject?, service: PermissionService?): Boolean {
            if (ref == null) {
                // returning false from this predicate means this setting callback will be removed
                // as a listener, and will not be tested again.
                return false
            }
            // if PS has just been unregistered, ignore the change.
            if (service == null) {
                return true
            }

            // check if we're using the native Lantern impl
            // we can skip some unnecessary instance creation this way.
            val subject = if (service is LanternPermissionService) {
                service.get(ref.subjectCollectionIdentifier).get(ref.identifier).asSubjectReference()
            } else {
                // build a new subject reference using the permission service
                // this doesn't actually load the subject, so it will be lazily init'd when needed.
                service.newSubjectReference(ref.subjectCollectionIdentifier, ref.identifier)
            }

            ref.internalSubject = subject
            return true
        }
    }

}
