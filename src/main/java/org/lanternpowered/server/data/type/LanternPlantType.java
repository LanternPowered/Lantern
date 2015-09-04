package org.lanternpowered.server.data.type;

import org.lanternpowered.server.catalog.LanternSimpleCatalogType;
import org.spongepowered.api.data.type.PlantType;

public class LanternPlantType extends LanternSimpleCatalogType implements PlantType {

    public LanternPlantType(String identifier) {
        super(identifier);
    }
}
