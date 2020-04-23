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
package org.lanternpowered.server.network.vanilla.message.processor.play

import org.lanternpowered.server.network.message.Message
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.message.processor.Processor
import org.lanternpowered.server.network.vanilla.message.type.play.UpdateWorldSkyMessage
import org.lanternpowered.server.network.vanilla.message.type.play.internal.ChangeGameStateMessage

class UpdateWorldSkyProcessor : Processor<UpdateWorldSkyMessage> {

    override fun process(context: CodecContext, message: UpdateWorldSkyMessage, output: MutableList<Message>) {
        var rain = message.rainStrength
        var darkness = message.darkness

        // Rain strength may not be greater then 1.5
        // TODO: Check what limit may cause issues
        rain = rain.coerceAtMost(1.5f)

        // The maximum darkness value before strange things start to happen
        // Night vision changes the max from 6.0 to 4.0, we won't
        // check that for now so we will play safe.
        val f1 = 4f

        // The minimum value before the rain stops
        val f2 = 0.01f

        // The rain value may not be zero, otherwise will it cause math errors.
        if (rain < f2 && darkness > 0f) {
            rain = f2
        }
        if (darkness > 0f) {
            darkness = (darkness / rain).coerceAtMost(f1 / rain)
        }
        output.add(ChangeGameStateMessage(7, rain))
        output.add(ChangeGameStateMessage(8, darkness))
    }
}
