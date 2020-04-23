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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries

/**
 * This processor will separate the entries with different types and put them
 * into a new message, this is required because the vanilla codec can only use
 * one entry type for one message.
 */
class ProcessorPlayOutTabListEntries : Processor<MessagePlayOutTabListEntries> {

    override fun process(context: CodecContext, message: MessagePlayOutTabListEntries, output: MutableList<Message>) {
        if (message.entries.isEmpty())
            return

        val mapped = message.entries
                .groupBy { entry -> entry.javaClass }
        if (mapped.size == 1) {
            output.add(message)
        } else {
            for ((_, value) in mapped)
                output.add(MessagePlayOutTabListEntries(value))
        }
    }
}
