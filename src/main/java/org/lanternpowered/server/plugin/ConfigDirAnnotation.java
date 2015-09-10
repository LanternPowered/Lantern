package org.lanternpowered.server.plugin;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.service.config.ConfigDir;

import java.lang.annotation.Annotation;

public class ConfigDirAnnotation implements ConfigDir {

    private final boolean shared;

    public ConfigDirAnnotation(boolean shared) {
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

        ConfigDir that = (ConfigDir) o;
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