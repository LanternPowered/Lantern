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
package org.lanternpowered.server.text.title

import org.lanternpowered.api.text.title.Title
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.TitlePacket
import java.time.Duration

/**
 * Gets the [Packet]s to show a [Title].
 */
fun Title.toPackets(): List<Packet> {
    val packets = mutableListOf(
            TitlePacket.SetTitle(this.title()),
            TitlePacket.SetSubtitle(this.subtitle()))
    val times = this.times()
    if (times != null)
        packets += TitlePacket.SetTimes(times.fadeIn().toTicks(), times.stay().toTicks(), times.fadeOut().toTicks())
    return packets
}

private fun Duration.toTicks(): Int = toMillis().toInt() / 50
