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
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.parameter.ParameterList

class BatEntityProtocol<E : LanternEntity>(entity: E) : InsentientEntityProtocol<E>(entity) {

    companion object {
        private val TYPE = minecraftKey("bat")
    }

    private var lastHanging = false

    override val mobType: NamespacedKey get() = TYPE

    override fun spawn(parameterList: ParameterList) {
        parameterList.add(EntityParameters.Bat.FLAGS,
                (if (this.entity.get(LanternKeys.IS_HANGING).orElse(false)) 0x1 else 0).toByte())
    }

    override fun update(parameterList: ParameterList) {
        val hanging = this.entity.get(LanternKeys.IS_HANGING).orElse(false)
        if (this.lastHanging != hanging) {
            parameterList.add(EntityParameters.Bat.FLAGS, (if (hanging) 0x1 else 0).toByte())
            this.lastHanging = hanging
        }
    }
}
