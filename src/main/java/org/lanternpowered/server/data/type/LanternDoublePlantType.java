package org.lanternpowered.server.data.type;

import org.lanternpowered.server.catalog.SimpleCatalogType;
import org.spongepowered.api.data.type.DoublePlantType;

public interface LanternDoublePlantType extends DoublePlantType, SimpleCatalogType {

    byte getInternalId();
}
