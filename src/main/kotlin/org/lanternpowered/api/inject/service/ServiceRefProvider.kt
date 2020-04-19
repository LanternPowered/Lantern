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
package org.lanternpowered.api.inject.service

import com.google.inject.Inject
import com.google.inject.Provider
import org.lanternpowered.api.inject.InjectionPoint
import org.lanternpowered.api.util.uncheckedCast

internal class ServiceRefProvider<T> : Provider<ServiceRef<T>> {

    @Inject private lateinit var point: InjectionPoint

    override fun get(): ServiceRef<T> {
        // Extract the service type from the injection point
        val valueType = this.point.type.resolveType(VALUE_VARIABLE).rawType.uncheckedCast<Class<T>>()
        // Must be present, so not the object class
        if (valueType == Object::class.java) {
            throw IllegalStateException("Missing service type.")
        }
        return ServiceRef.of(valueType)
    }

    companion object {

        private val VALUE_VARIABLE = ServiceRef::class.java.typeParameters[0]
    }
}
