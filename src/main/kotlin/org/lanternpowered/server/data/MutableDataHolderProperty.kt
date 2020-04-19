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
package org.lanternpowered.server.data

import org.spongepowered.api.data.DataHolder
import kotlin.reflect.KProperty

interface MutableDataHolderProperty<H : DataHolder, T> : DataHolderProperty<H, T> {

    operator fun setValue(thisRef: H, property: KProperty<*>, value: T)
}
