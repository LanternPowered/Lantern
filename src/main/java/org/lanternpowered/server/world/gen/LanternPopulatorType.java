package org.lanternpowered.server.world.gen;

import org.lanternpowered.server.catalog.LanternPluginCatalogType;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.world.gen.Populator;
import org.spongepowered.api.world.gen.PopulatorType;

import com.google.common.base.MoreObjects;

public final class LanternPopulatorType extends LanternPluginCatalogType implements PopulatorType {

    public final Class<? extends Populator> populatorClass;

    public LanternPopulatorType(String name, Class<? extends Populator> populatorClass) {
        this("minecraft", name, populatorClass);
    }

    public LanternPopulatorType(String pluginId, String name, Class<? extends Populator> populatorClass) {
        super(pluginId, name);
        this.populatorClass = populatorClass;
    }

    @Override
    public Class<? extends Populator> getPopulatorClass() {
        return this.populatorClass;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final LanternPopulatorType other = (LanternPopulatorType) obj;
        if (!this.getId().equals(other.getId())) {
            return false;
        } else if (!this.populatorClass.equals(other.populatorClass)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", this.getId())
                .add("name", this.getName())
                .add("pluginId", this.getPluginId())
                .add("class", this.populatorClass.getName())
                .toString();
    }

    @Override
    public Translation getTranslation() {
        return null;
    }
}