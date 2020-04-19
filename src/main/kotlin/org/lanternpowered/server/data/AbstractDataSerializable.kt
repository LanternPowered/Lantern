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

import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataSerializable
import org.spongepowered.api.data.persistence.Queries

abstract class AbstractDataSerializable : DataSerializable {

    override fun toContainer(): DataContainer = DataContainer.createNew()
            .set(Queries.CONTENT_VERSION, this.contentVersion)
}
