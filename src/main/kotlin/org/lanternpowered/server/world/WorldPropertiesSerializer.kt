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
package org.lanternpowered.server.world

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.data.persistence.DataContainer
import org.lanternpowered.api.data.persistence.DataView

object WorldPropertiesSerializer {

    fun deserialize(key: NamespacedKey, data: DataView): LanternWorldProperties {
        TODO()
    }

    fun serialize(properties: LanternWorldProperties): DataContainer {
        TODO()
    }
}