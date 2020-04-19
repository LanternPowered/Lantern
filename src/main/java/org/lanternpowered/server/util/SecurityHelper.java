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
package org.lanternpowered.server.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

public final class SecurityHelper {

    private static SecureRandom random = new SecureRandom();

    public static KeyPair generateKeyPair() {
        try {
            final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to generate RSA key pair", e);
        }
    }

    public static byte[] generateVerifyToken() {
        byte[] token = new byte[4];
        random.nextBytes(token);
        return token;
    }

    public static Key generateX509Key(Key base) {
        try {
            final X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(base.getEncoded());
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(encodedKeySpec);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to generate X509 encoded key", e);
        }
    }

    private SecurityHelper() {
    }
}
