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
package org.lanternpowered.server.inject.config;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.config.ConfigDir;

import java.lang.annotation.Annotation;

@SuppressWarnings("all")
public class ConfigDirAnnotation implements ConfigDir {

    public static final ConfigDirAnnotation NON_SHARED = new ConfigDirAnnotation(false);
    public static final ConfigDirAnnotation SHARED = new ConfigDirAnnotation(true);

    private final boolean shared;

    private ConfigDirAnnotation(boolean shared) {
        this.shared = shared;
    }

    @Override
    public boolean sharedRoot() {
        return this.shared;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return ConfigDir.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfigDir)) {
            return false;
        }
        final ConfigDir that = (ConfigDir) o;
        return sharedRoot() == that.sharedRoot();
    }

    @Override
    public int hashCode() {
        return (127 * "sharedRoot".hashCode()) ^ Boolean.valueOf(sharedRoot()).hashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper('@' + getClass().getName())
                .add("shared", this.shared)
                .toString();
    }
}
