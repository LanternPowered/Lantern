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

import org.lanternpowered.api.effect.sound.SoundEffect
import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.key.asNamespacedKey
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.EntitySoundEffectPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.NamedSoundEffectPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SoundEffectPacket
import org.lanternpowered.server.registry.type.effect.sound.SoundTypeRegistry
import org.spongepowered.math.vector.Vector3d

fun SoundEffect.getEntityPacketBuilder(): (entity: Entity) -> Packet {
    val key = this.name().asNamespacedKey()
    val type = SoundTypeRegistry[key]
    val pitch = this.pitch()
    val volume = this.volume()
    val source = this.source()
    if (type is LanternSoundType) {
        val eventId = type.eventId
        if (eventId != null) {
            return { entity ->
                entity as LanternEntity
                val entityId = entity.world.entityProtocolManager.getProtocolId(entity)
                if (entityId != -1) {
                    EntitySoundEffectPacket(eventId, entityId, source, volume, pitch)
                } else {
                    SoundEffectPacket(eventId, entity.position, source, volume, pitch)
                }
            }
        }
    }
    return { entity -> NamedSoundEffectPacket(key, entity.position, source, volume, pitch) }
}

fun SoundEffect.getPacketBuilder(): (position: Vector3d) -> Packet {
    val key = this.name().asNamespacedKey()
    val type = SoundTypeRegistry[key]
    val pitch = this.pitch()
    val volume = this.volume()
    val source = this.source()
    if (type is LanternSoundType) {
        val eventId = type.eventId
        if (eventId != null)
            return { position -> SoundEffectPacket(eventId, position, source, volume, pitch) }
    }
    return { position -> NamedSoundEffectPacket(key, position, source, volume, pitch) }
}
