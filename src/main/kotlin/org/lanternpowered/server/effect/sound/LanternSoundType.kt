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
package org.lanternpowered.server.effect.sound

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.network.message.Message
import org.lanternpowered.server.network.vanilla.message.type.play.NamedSoundEffectMessage
import org.lanternpowered.server.network.vanilla.message.type.play.SoundEffectMessage
import org.spongepowered.api.effect.sound.SoundCategory
import org.spongepowered.api.effect.sound.SoundType
import org.spongepowered.math.vector.Vector3d

class LanternSoundType @JvmOverloads constructor(
        key: ResourceKey, private val eventId: Int? = null
) : DefaultCatalogType(key), SoundType {

    fun createMessage(position: Vector3d, soundCategory: SoundCategory, volume: Float, pitch: Float): Message {
        return if (this.eventId != null) {
            SoundEffectMessage(this.eventId, position, soundCategory, volume, pitch)
        } else {
            NamedSoundEffectMessage(this.key.value, position, soundCategory, volume, pitch)
        }
    }
}
