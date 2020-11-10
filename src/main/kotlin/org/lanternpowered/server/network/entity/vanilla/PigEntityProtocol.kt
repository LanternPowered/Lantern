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

import org.lanternpowered.api.data.Keys
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.parameter.ParameterList

class PigEntityProtocol<E : LanternEntity>(entity: E) : AnimalEntityProtocol<E>(entity) {

    companion object {
        private val TYPE = minecraftKey("pig")
    }

    private var lastSaddled = false

    override val mobType: NamespacedKey get() = TYPE

    override fun spawn(parameterList: ParameterList) {
        super.spawn(parameterList)
        parameterList.add(EntityParameters.Pig.HAS_SADDLE, this.entity.get(Keys.IS_SADDLED).orElse(false))
    }

    override fun update(parameterList: ParameterList) {
        super.update(parameterList)
        val saddled = this.entity.get(Keys.IS_SADDLED).orElse(false)
        if (this.lastSaddled != saddled) {
            parameterList.add(EntityParameters.Pig.HAS_SADDLE, saddled)
            this.lastSaddled = saddled
        }
    }
}
