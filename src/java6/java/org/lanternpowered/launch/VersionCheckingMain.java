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
package org.lanternpowered.launch;

import java.awt.GraphicsEnvironment;

import javax.swing.JOptionPane;

public class VersionCheckingMain {

    private static final String REQUIRED_VERSION = "1.8.0_40";
    private static final String ERROR_MESSAGE = "We have detected that you are JRE version %s, which is out of date!\n"
            + "In order to run LanternServer, you **must** be running JRE version %s (or above).\n"
            + "Previous builds of JRE version 8 will not work with the LanternServer.\n"
            + "This can be downloaded from Oracle: http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html";

    public static void main(String[] args) throws Exception {
        final String current = System.getProperty("java.version");
        if (getVersionValue(current) < getVersionValue(REQUIRED_VERSION)) {
            final String error = String.format(ERROR_MESSAGE, current, REQUIRED_VERSION);
            if (!GraphicsEnvironment.isHeadless()) {
                JOptionPane.showMessageDialog(null, error, "PEBKACException!", JOptionPane.ERROR_MESSAGE);
            }
            throw new RuntimeException(error);
        }
        LanternLaunch.main(args);
    }

    /**
     * Calculates a double value based on a Java version string such that a
     * higher version will produce a higher value
     *
     * @param version The Java version
     * @return The double value of the Java version
     */
    private static double getVersionValue(String version) {
        // Get rid of any dashes, such as those in early access versions which have "-ea" on the end of the version
        if (version.contains("-")) {
            version = version.substring(0, version.indexOf('-'));
        }
        // Replace underscores with periods for easier String splitting
        version = version.replace('_', '.');
        // Ignore the '1.' in the version string since it no longer has any
        // meaning because it got removed in Java 9+
        if (version.startsWith("1.")) {
            version = version.substring(2);
        }
        // Split the version up into parts
        final String[] versionParts = version.split("\\.");
        double versionValue = 0;
        for (int i = 0; i < versionParts.length; i++) {
            try {
                final int part = Integer.valueOf(versionParts[i]);
                // first value is before zero, remaining values are 3 digits each, shifted behind zero
                versionValue += i == 0 ? part : (double) part / Math.pow(1000.0, i);
            } catch (NumberFormatException ignored) {
            }
        }
        return versionValue;
    }
}
