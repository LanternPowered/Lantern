package org.lanternpowered.server.world.gen;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.catalog.SimpleLanternCatalogType;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.world.gen.Populator;
import org.spongepowered.api.world.gen.PopulatorType;

public final class LanternPopulatorType extends SimpleLanternCatalogType implements PopulatorType {

    private final Class<? extends Populator> populatorClass;

    public LanternPopulatorType(String identifier, Class<? extends Populator> populatorClass) {
        super(identifier);

        this.populatorClass = checkNotNull(populatorClass, "populatorClass");
    }

    @Override
    public Translation getTranslation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<? extends Populator> getPopulatorClass() {
        return this.populatorClass;
    }
}
