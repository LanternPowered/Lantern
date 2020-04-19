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
package org.lanternpowered.server.attribute;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.text.Text;

import java.util.function.Predicate;

public class LanternAttribute extends DefaultCatalogType {

    private final Text name;
    private final Predicate<DataHolder> targets;

    private final double max;
    private final double min;
    private final double def;

    LanternAttribute(String id, Text name, double max, double min, double def, Predicate<DataHolder> targets) {
        super(CatalogKeys.activePlugin(id));

        this.name = checkNotNull(name, "name");
        this.targets = checkNotNull(targets, "targets");

        // Make sure that the values aren't switched
        this.max = Math.max(max, min);
        this.min = Math.min(max, min);
        this.def = def;
    }

    public double getMinimum() {
        return this.min;
    }

    public double getMaximum() {
        return this.max;
    }

    public double getDefaultValue() {
        return this.def;
    }

    public Predicate<DataHolder> getTargets() {
        return this.targets;
    }

    /**
     * Gets the display name of the attribute.
     *
     * <p>No method in the sponge api? There is one in the builder though.</p>
     *
     * @return the display name
     */
    public Text getDisplayName() {
        return this.name;
    }
}
