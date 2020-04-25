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
package org.lanternpowered.server.util

import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.SecureRandom
import javax.crypto.Cipher

object EncryptionHelper {

    private val random = SecureRandom()

    @JvmStatic
    fun generateRsaKeyPair(): KeyPair {
        return try {
            val generator = KeyPairGenerator.getInstance("RSA")
            generator.initialize(1024)
            generator.generateKeyPair()
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalStateException("Unable to generate RSA key pair", e)
        }
    }

    fun generateVerifyToken(): ByteArray {
        val token = ByteArray(4)
        this.random.nextBytes(token)
        return token
    }

    fun generateServerId(sharedSecret: ByteArray, key: PublicKey): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-1")
            digest.update(sharedSecret)
            digest.update(key.encoded)
            BigInteger(digest.digest()).toString(16)
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalStateException(e)
        }
    }

    fun decryptRsa(keyPair: KeyPair, bytes: ByteArray): ByteArray {
        val cipher: Cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, keyPair.private)
        return cipher.doFinal(bytes)
    }
}
