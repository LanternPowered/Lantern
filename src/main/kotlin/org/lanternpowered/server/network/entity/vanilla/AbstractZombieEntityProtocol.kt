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
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.parameter.ParameterList

abstract class AbstractZombieEntityProtocol<E : LanternEntity>(entity: E) : AgeableEntityProtocol<E>(entity) {

    private var lastAreHandsUp = false

    private val areHandsUp: Boolean
        get() = this.entity.get(Keys.ARE_HANDS_UP).orElse(false)

    override fun spawn(parameterList: ParameterList) {
        super.spawn(parameterList)
        parameterList.add(EntityParameters.AbstractZombie.UNUSED, 0)
        parameterList.add(EntityParameters.AbstractZombie.HANDS_UP, this.areHandsUp)
    }

    override fun update(parameterList: ParameterList) {
        super.update(parameterList)
        val handsUp = this.areHandsUp
        if (handsUp != this.lastAreHandsUp) {
            parameterList.add(EntityParameters.AbstractZombie.HANDS_UP, handsUp)
            this.lastAreHandsUp = handsUp
        }
    }

    override fun hasEquipment(): Boolean = true
}
