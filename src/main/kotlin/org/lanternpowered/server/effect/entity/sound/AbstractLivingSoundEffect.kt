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
package org.lanternpowered.server.effect.entity.sound

import org.lanternpowered.server.effect.entity.AbstractEntityEffect
import org.lanternpowered.server.entity.EntityBodyPosition
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.api.data.Keys
import kotlin.random.Random

abstract class AbstractLivingSoundEffect(position: EntityBodyPosition) : AbstractEntityEffect(position) {

    /**
     * Gets a randomized volume value for the sound effect.
     *
     * @param random The random
     * @return The volume value
     */
    protected fun getVolume(entity: LanternEntity, random: Random): Double {
        return 1.0
    }

    /**
     * Gets a randomized pitch value for the sound effect.
     *
     * @param random The random
     * @return The pitch value
     */
    protected fun getPitch(entity: LanternEntity, random: Random): Double {
        var value = random.nextFloat() - random.nextFloat() * 0.2
        // Adults and children use a different pitch value
        value += if (entity.get(Keys.IS_ADULT).orElse(true)) {
            1.0
        } else {
            1.5
        }
        return value
    }
}
