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
import org.lanternpowered.api.data.eq
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.parameter.ParameterList
import org.spongepowered.api.data.type.HandPreferences

abstract class InsentientEntityProtocol<E : LanternEntity> protected constructor(entity: E) : CreatureEntityProtocol<E>(entity) {

    private var lastDominantHand = HandPreferences.RIGHT.get()

    override fun spawn(parameterList: ParameterList) {
        super.spawn(parameterList)
        // Ignore the NoAI tag, isn't used on the client
        parameterList.add(EntityParameters.Insentient.FLAGS,
                (if (this.entity.get(Keys.DOMINANT_HAND).orElseGet(HandPreferences.RIGHT) eq HandPreferences.LEFT) 0x2 else 0).toByte())
    }

    override fun update(parameterList: ParameterList) {
        super.update(parameterList)
        val dominantHand = this.entity.get(Keys.DOMINANT_HAND).orElseGet(HandPreferences.RIGHT)
        if (dominantHand != this.lastDominantHand) {
            // Ignore the NoAI tag, isn't used on the client
            parameterList.add(EntityParameters.Insentient.FLAGS, (if (dominantHand eq HandPreferences.LEFT) 0x2 else 0).toByte())
            this.lastDominantHand = dominantHand
        }
    }
}
