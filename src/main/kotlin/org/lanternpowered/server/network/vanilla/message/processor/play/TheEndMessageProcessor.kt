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
import org.lanternpowered.server.network.vanilla.message.type.play.TheEndMessage
import org.lanternpowered.server.network.vanilla.message.type.play.internal.ChangeGameStateMessage

class TheEndMessageProcessor : Processor<TheEndMessage> {

    override fun process(context: CodecContext, message: TheEndMessage, output: MutableList<Message>) {
        output.add(ChangeGameStateMessage(4, if (message.playCredits) 1f else 0f))
    }
}
