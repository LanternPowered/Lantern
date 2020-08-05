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
package org.lanternpowered.server.entity.player

import org.lanternpowered.server.network.packet.Packet
import org.spongepowered.api.entity.living.player.Player

fun Iterable<Player>.send(packet: Packet) {
    for (player in this)
        (player as LanternPlayer).connection.send(packet)
}
