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

class SnowmanEntityProtocol<E : LanternEntity>(entity: E) : InsentientEntityProtocol<E>(entity) {

    companion object {

        private val TYPE = minecraftKey("snow_golem")
    }

    private var lastNoPumpkin = false

    override val mobType: NamespacedKey get() = TYPE

    override fun spawn(parameterList: ParameterList) {
        parameterList.add(EntityParameters.Snowman.FLAGS,
                (if (this.entity.get(Keys.HAS_PUMPKIN_HEAD).orElse(true)) 0 else 0x10).toByte())
    }

    override fun update(parameterList: ParameterList) {
        val noPumpkin = !this.entity.get(Keys.HAS_PUMPKIN_HEAD).orElse(true)
        if (this.lastNoPumpkin != noPumpkin) {
            parameterList.add(EntityParameters.Snowman.FLAGS, (if (noPumpkin) 0x10 else 0).toByte())
            this.lastNoPumpkin = noPumpkin
        }
    }
}
