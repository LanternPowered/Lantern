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

import org.lanternpowered.server.entity.EntityBodyPosition
import org.lanternpowered.server.entity.LanternEntity
import org.spongepowered.api.effect.sound.SoundType
import org.spongepowered.math.vector.Vector3d
import java.util.function.Supplier
import kotlin.random.Random

class DefaultLivingSoundEffect(position: EntityBodyPosition, private val soundType: SoundType) : AbstractLivingSoundEffect(position) {

    constructor(position: EntityBodyPosition, soundType: Supplier<out SoundType>) : this(position, soundType.get())

    override fun play(entity: LanternEntity, relativePosition: Vector3d, random: Random) {
        entity.makeSound(this.soundType, relativePosition, this.getVolume(entity, random), this.getPitch(entity, random))
    }
}
