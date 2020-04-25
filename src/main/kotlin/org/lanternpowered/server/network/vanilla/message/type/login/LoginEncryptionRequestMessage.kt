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
package org.lanternpowered.server.network.vanilla.message.type.login

import org.lanternpowered.server.network.message.Message

/**
 * @property publicKey The public key
 * @property verifyToken The verify token
 */
class LoginEncryptionRequestMessage(val publicKey: ByteArray, val verifyToken: ByteArray) : Message
