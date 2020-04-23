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
import org.lanternpowered.server.network.vanilla.message.type.play.SetGameModeMessage
import org.lanternpowered.server.network.vanilla.message.type.play.internal.ChangeGameStateMessage
import org.lanternpowered.server.registry.type.data.GameModeRegistry

class SetGameModeProcessor : Processor<SetGameModeMessage> {

    override fun process(context: CodecContext, message: SetGameModeMessage, output: MutableList<Message>) {
        output.add(ChangeGameStateMessage(3, GameModeRegistry.getId(message.gameMode).toFloat()))
    }
}
