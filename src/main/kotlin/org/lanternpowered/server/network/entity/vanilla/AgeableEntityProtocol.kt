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
import org.lanternpowered.api.util.optional.orZero
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.parameter.ParameterList
import kotlin.time.Duration

abstract class AgeableEntityProtocol<E : LanternEntity>(entity: E) : InsentientEntityProtocol<E>(entity) {

    private var lastIsBaby = false

    private val isBaby: Boolean
        get() = this.entity.get(Keys.IS_BABY).orElseGet { this.entity.get(Keys.AGE).orZero() < Duration.ZERO }

    override fun spawn(parameterList: ParameterList) {
        super.spawn(parameterList)
        parameterList.add(EntityParameters.Ageable.IS_BABY, this.isBaby)
    }

    override fun update(parameterList: ParameterList) {
        super.spawn(parameterList)
        val isBaby = this.isBaby
        if (isBaby != this.lastIsBaby) {
            parameterList.add(EntityParameters.Ageable.IS_BABY, isBaby)
            this.lastIsBaby = isBaby
        }
    }
}
