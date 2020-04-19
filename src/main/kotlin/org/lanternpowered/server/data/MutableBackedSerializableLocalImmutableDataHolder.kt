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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.server.data

import org.spongepowered.api.data.SerializableDataHolder
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView

abstract class MutableBackedSerializableLocalImmutableDataHolder<I : SerializableDataHolder.Immutable<I>, M : SerializableLocalMutableDataHolder>(
        backingDataHolder: M
) : MutableBackedCopyableLocalImmutableDataHolder<I, M>(backingDataHolder), SerializableDataHolder.Immutable<I> {

    override fun validateRawData(container: DataView): Boolean =
            this.backingDataHolder.validateRawData(container)

    override fun withRawData(container: DataView): I {
        val copy = this.backingDataHolder.copy() as M
        copy.setRawData(container)
        return withBacking(copy)
    }

    override fun toContainer(): DataContainer =
            this.backingDataHolder.toContainer()

    override fun getContentVersion(): Int =
            this.backingDataHolder.contentVersion
}
