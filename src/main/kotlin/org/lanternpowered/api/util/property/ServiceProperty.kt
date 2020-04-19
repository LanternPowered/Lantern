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
package org.lanternpowered.api.util.property

import org.lanternpowered.api.Lantern
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ServiceProperty<T>(private val type: Class<T>) : ReadOnlyProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T = Lantern.serviceManager.provideUnchecked(this.type)
}
