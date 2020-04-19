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
import org.spongepowered.api.data.DirectionRelativeDataProvider
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.util.Direction
import java.util.Optional

interface IDirectionalDataProvider<V : Value<E>, E : Any> : DirectionRelativeDataProvider<V, E>, IDataProvider<V, E> {

    @JvmDefault
    override fun isSupported(container: DataHolder): Boolean = super.isSupported(container)

    @JvmDefault
    override fun isSupported(dataHolder: DataHolder, direction: Direction): Boolean

    @JvmDefault
    override fun getValue(container: DataHolder): Optional<V> = super<DirectionRelativeDataProvider>.getValue(container)

    @JvmDefault
    override fun get(container: DataHolder): Optional<E> = super.get(container)
}