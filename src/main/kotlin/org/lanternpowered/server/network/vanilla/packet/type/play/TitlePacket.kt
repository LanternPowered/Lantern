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
package org.lanternpowered.server.network.vanilla.packet.type.play

import org.lanternpowered.api.text.Text
import org.lanternpowered.server.network.packet.Packet

sealed class TitlePacket : Packet {

    data class SetTitle(val title: Text) : TitlePacket()

    data class SetSubtitle(val title: Text) : TitlePacket()

    data class SetActionbarTitle(val title: Text) : TitlePacket()

    data class SetTimes(val fadeIn: Int, val stay: Int, val fadeOut: Int) : TitlePacket()

    object Clear : TitlePacket()

    object Reset : TitlePacket()
}
