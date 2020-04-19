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

import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.CopyableDataHolder
import org.spongepowered.api.data.DataHolder

interface CopyableImmutableDataHolder<I> : ImmutableDataHolder<I>, CopyableDataHolderBase where I : CopyableDataHolder, I : DataHolder.Immutable<I> {

    @JvmDefault
    override fun copy(): I = uncheckedCast()
}
