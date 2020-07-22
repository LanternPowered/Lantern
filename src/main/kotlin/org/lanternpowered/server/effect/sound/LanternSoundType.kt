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

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.effect.sound.SoundCategory
import org.lanternpowered.api.effect.sound.SoundType
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.NamedSoundEffectPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SoundEffectPacket
import org.spongepowered.math.vector.Vector3d

class LanternSoundType @JvmOverloads constructor(
        key: NamespacedKey, private val eventId: Int? = null
) : DefaultCatalogType(key), SoundType {

    fun createMessage(position: Vector3d, soundCategory: SoundCategory, volume: Float, pitch: Float): Packet {
        return if (this.eventId != null) {
            SoundEffectPacket(this.eventId, position, soundCategory, volume, pitch)
        } else {
            NamedSoundEffectPacket(this.key.value, position, soundCategory, volume, pitch)
        }
    }
}
