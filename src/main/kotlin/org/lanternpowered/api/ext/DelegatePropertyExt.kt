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
package org.lanternpowered.api.ext

import org.lanternpowered.api.util.property.InitOnceProperty
import org.lanternpowered.api.util.property.ServiceProperty
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

/**
 * A property that can only be written once.
 */
inline fun <reified T> initOnce(): ReadWriteProperty<Any, T> = InitOnceProperty()

/**
 * A property that provides a service from the service manager.
 */
inline fun <reified T> service(): ReadOnlyProperty<Any, T> = ServiceProperty(T::class.java)
