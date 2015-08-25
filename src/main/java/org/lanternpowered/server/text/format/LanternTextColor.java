package org.lanternpowered.server.text.format;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Color;

import org.lanternpowered.server.catalog.SimpleCatalogType;
import org.spongepowered.api.text.format.TextColor;

public class LanternTextColor extends SimpleCatalogType implements TextColor {

    private final Color color;

    public LanternTextColor(String name, Color color, char legacyCode) {
        super(name, name.toLowerCase());
        this.color = checkNotNull(color, "color");
    }

    @Override
    public Color getColor() {
        return this.color;
    }
}
