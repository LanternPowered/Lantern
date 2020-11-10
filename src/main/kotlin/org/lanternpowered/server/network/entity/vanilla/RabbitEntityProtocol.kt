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
import org.lanternpowered.server.registry.type.data.RabbitTypeRegistry
import org.spongepowered.api.data.type.RabbitTypes

class RabbitEntityProtocol<E : LanternEntity>(entity: E) : AnimalEntityProtocol<E>(entity) {

    companion object {
        private val TYPE = minecraftKey("rabbit")
    }

    private var lastTypeId = 0

    private val typeId: Int
        get() = RabbitTypeRegistry.getId(this.entity.get(Keys.RABBIT_TYPE).orElseGet(RabbitTypes.WHITE))

    override val mobType: NamespacedKey get() = TYPE

    override fun spawn(parameterList: ParameterList) {
        super.spawn(parameterList)
        parameterList.add(EntityParameters.Rabbit.VARIANT, this.typeId)
    }

    override fun update(parameterList: ParameterList) {
        super.update(parameterList)
        val type = this.typeId
        if (type != this.lastTypeId) {
            parameterList.add(EntityParameters.Rabbit.VARIANT, type)
            this.lastTypeId = type
        }
    }
}
