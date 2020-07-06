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
package org.lanternpowered.server.network.vanilla.message.codec.login

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.login.LoginSuccessMessage

class LoginSuccessCodec : Codec<LoginSuccessMessage> {

    override fun encode(context: CodecContext, message: LoginSuccessMessage): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeUniqueId(message.uniqueId)
            writeString(message.username)
        }
    }
}
