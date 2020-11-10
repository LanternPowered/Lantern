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
package org.lanternpowered.server.network.entity.vanilla

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.parameter.ParameterList

class LightningEntityProtocol<E : LanternEntity>(entity: E) : ObjectEntityProtocol<E>(entity) {

    init {
        this.trackingRange = 512.0
    }

    companion object {
        private val TYPE = minecraftKey("lightning_bolt")
    }

    override val objectType: NamespacedKey get() = TYPE
    override val objectData: Int get() = 0

    override fun spawn(parameterList: ParameterList) {}
    override fun update(parameterList: ParameterList) {}
}
