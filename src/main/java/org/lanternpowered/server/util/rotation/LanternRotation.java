package org.lanternpowered.server.util.rotation;

import org.lanternpowered.server.catalog.LanternCatalogType;
import org.spongepowered.api.util.rotation.Rotation;

public class LanternRotation extends LanternCatalogType implements Rotation {

    private final int angle;

    public LanternRotation(String name, int angle) {
        super(name, name);
        this.angle = angle;
    }

    @Override
    public int getAngle() {
        return this.angle;
    }
}
