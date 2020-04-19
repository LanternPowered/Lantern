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

import org.apache.logging.log4j.util.PropertiesUtil;

import java.util.Properties;

public final class SystemProperties {

    private static final PropertiesUtil systemPropertiesUtil = new PropertiesUtil(System.getProperties());

    /**
     * Gets the {@link PropertiesUtil} for the
     * system {@link Properties}.
     *
     * @return The system properties util
     */
    public static PropertiesUtil get() {
        return systemPropertiesUtil;
    }
}
