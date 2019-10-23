/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
