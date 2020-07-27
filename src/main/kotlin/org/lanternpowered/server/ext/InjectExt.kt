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
package org.lanternpowered.server.ext

import org.lanternpowered.server.util.guice.inject.property.DefaultInjectedProperty
import org.lanternpowered.server.util.guice.inject.property.LazyInjectedProperty

inline fun <reified T> injectLazily() = LazyInjectedProperty<T>()
inline fun <reified T> inject() = DefaultInjectedProperty<T>()
