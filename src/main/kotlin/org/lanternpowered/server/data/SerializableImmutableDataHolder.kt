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

import org.spongepowered.api.data.SerializableDataHolder
import org.spongepowered.api.data.persistence.DataView

interface SerializableImmutableDataHolder<I : SerializableDataHolder.Immutable<I>> :
        CopyableImmutableDataHolder<I>, SerializableDataHolderBase, SerializableDataHolder.Immutable<I> {

    override fun withRawData(container: DataView): I

    @JvmDefault
    override fun copy(): I = super.copy()
}
