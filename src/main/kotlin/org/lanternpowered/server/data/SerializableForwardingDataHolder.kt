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
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView

interface SerializableForwardingDataHolder : CopyableForwardingDataHolder, SerializableDataHolder {

    override val delegateDataHolder: SerializableDataHolder

    @JvmDefault
    override fun getContentVersion() = this.delegateDataHolder.contentVersion

    @JvmDefault
    override fun toContainer(): DataContainer = this.delegateDataHolder.toContainer()

    @JvmDefault
    override fun validateRawData(container: DataView) = this.delegateDataHolder.validateRawData(container)

    @JvmDefault
    override fun copy(): SerializableForwardingDataHolder
}
