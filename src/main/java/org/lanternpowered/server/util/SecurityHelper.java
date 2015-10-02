package org.lanternpowered.server.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

import org.lanternpowered.server.game.LanternGame;

public final class SecurityHelper {

    private static SecureRandom random = new SecureRandom();

    private SecurityHelper() {
    }

    /**
     * Generate a RSA key pair
     */
    public static KeyPair generateKeyPair() {
        KeyPair keyPair = null;
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);

            keyPair = generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            LanternGame.log().error("Unable to generate RSA key pair", e);
        }
        return keyPair;
    }

    /**
     * Generate a random verify token
     */
    public static byte[] generateVerifyToken() {
        byte[] token = new byte[4];
        random.nextBytes(token);
        return token;
    }

    /**
     * Generates an X509 formatted key used in authentication
     */
    public static Key generateX509Key(Key base) {
        Key key = null;
        try {
            X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(base.getEncoded());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            key = keyFactory.generatePublic(encodedKeySpec);
        } catch (Exception ex) {
            LanternGame.log().error("Unable to generate X509 encoded key", ex);
        }
        return key;
    }
}
