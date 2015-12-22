/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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

import java.util.function.Predicate;

import org.lanternpowered.server.catalog.CatalogTypeRegistry;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.text.Text;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

public final class LanternAttributeBuilder {

    private final CatalogTypeRegistry<LanternAttribute> registry;

    private String identifier;
    private Text name;

    private Double min;
    private Double max;
    private Double def;

    private Predicate<DataHolder> targets;

    public LanternAttributeBuilder(CatalogTypeRegistry<LanternAttribute> registry) {
        this.registry = checkNotNull(registry, "registry");
        this.reset();
    }

    public LanternAttributeBuilder id(String id) {
        this.identifier = checkNotNullOrEmpty(id, "identifier");
        return this;
    }

    public LanternAttributeBuilder minimum(double minimum) {
        this.min = minimum;
        return this;
    }

    public LanternAttributeBuilder maximum(double maximum) {
        this.max = maximum;
        return this;
    }

    public LanternAttributeBuilder defaultValue(double defaultValue) {
        this.def = defaultValue;
        return this;
    }

    public LanternAttributeBuilder targets(Predicate<DataHolder> targets) {
        this.targets = checkNotNull(targets, "targets");
        return this;
    }

    public LanternAttributeBuilder name(Text name) {
        this.name = checkNotNull(name, "name");
        return this;
    }

    public LanternAttribute build() {
        checkState(this.identifier != null, "identifier is not set");
        checkState(!this.registry.get(this.identifier).isPresent(), "identifier already in use");
        checkState(this.name != null, "name is not set");
        checkState(this.min != null, "minimum is not set");
        checkState(this.max != null, "maximum is not set");
        checkState(this.def != null, "defaultValue is not set");
        checkState(this.def >= this.min && this.def <= this.max, "defaultValue must scale between the minimum and maximum value");
        LanternAttribute attribute = new LanternAttribute(this.identifier, this.name, this.min, this.max, this.def, this.targets);
        this.registry.register(attribute);
        return attribute;
    }

    public LanternAttributeBuilder reset() {
        this.targets = target -> true;
        this.identifier = null;
        this.name = null;
        this.max = null;
        this.min = null;
        this.def = null;
        return this;
    }
}
