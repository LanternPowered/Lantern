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
import org.spongepowered.api.data.persistence.Queries

interface SerializableDataHolderBase : CopyableDataHolderBase, SerializableDataHolder {

    /**
     * Gets the content version of this data holder. Defaults to `1`.
     *
     * @return The content version
     */
    @JvmDefault
    override fun getContentVersion(): Int = 1

    @JvmDefault
    override fun toContainer(): DataContainer {
        val dataContainer = DataContainer.createNew()
                .set(Queries.CONTENT_VERSION, this.contentVersion)
        DataHelper.serializeRawData(dataContainer, this)
        return dataContainer
    }
}
