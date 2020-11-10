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

abstract class AbstractSlimeEntityProtocol<E : LanternEntity>(entity: E) : InsentientEntityProtocol<E>(entity) {

    private var lastSize = 0

    override fun spawn(parameterList: ParameterList) {
        parameterList.add(EntityParameters.AbstractSlime.SIZE, this.entity.get(Keys.SIZE).orElse(1))
    }

    override fun update(parameterList: ParameterList) {
        val size: Int = entity.get(Keys.SIZE).orElse(1)
        if (this.lastSize != size) {
            parameterList.add(EntityParameters.AbstractSlime.SIZE, size)
            this.lastSize = size
        }
    }
}
