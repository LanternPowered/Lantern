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

interface SerializableMutableForwardingDataHolder : SerializableForwardingDataHolder, SerializableDataHolder.Mutable {

    override val delegateDataHolder: SerializableDataHolder.Mutable

    @JvmDefault
    override fun setRawData(container: DataView) = this.delegateDataHolder.setRawData(container)

    @JvmDefault
    override fun copy(): SerializableMutableForwardingDataHolder
}
