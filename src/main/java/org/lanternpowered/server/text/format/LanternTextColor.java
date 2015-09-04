package org.lanternpowered.server.text.format;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Color;

import org.lanternpowered.server.catalog.LanternCatalogType;
import org.spongepowered.api.text.format.TextColor;

public class LanternTextColor extends LanternCatalogType implements TextColor {

    private final Color color;

    public LanternTextColor(String name, Color color) {
        super(name, name.toLowerCase());
        this.color = checkNotNull(color, "color");
    }

    @Override
    public Color getColor() {
        return this.color;
    }
}
