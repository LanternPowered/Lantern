package org.lanternpowered.server.util.rotation;

import org.lanternpowered.server.catalog.SimpleCatalogType;
import org.spongepowered.api.util.rotation.Rotation;

public class LanternRotation extends SimpleCatalogType implements Rotation {

    private final int angle;

    public LanternRotation(String identifier, String name, int angle) {
        super(identifier, name);
        this.angle = angle;
    }

    @Override
    public int getAngle() {
        return this.angle;
    }
}
