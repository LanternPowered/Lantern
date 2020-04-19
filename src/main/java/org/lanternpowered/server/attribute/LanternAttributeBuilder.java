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
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.CopyableBuilder;

import java.util.function.Predicate;

public final class LanternAttributeBuilder implements CopyableBuilder<LanternAttribute, LanternAttributeBuilder> {

    private String identifier;
    private Text name;

    private Double min;
    private Double max;
    private Double def;

    private Predicate<DataHolder> targets;

    public LanternAttributeBuilder() {
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
        checkState(this.name != null, "name is not set");
        checkState(this.min != null, "minimum is not set");
        checkState(this.max != null, "maximum is not set");
        checkState(this.def != null, "defaultValue is not set");
        checkState(this.def >= this.min && this.def <= this.max, "defaultValue must scale between the minimum and maximum value");
        return new LanternAttribute(this.identifier, this.name, this.min, this.max, this.def, this.targets);
    }

    @Override
    public LanternAttributeBuilder from(LanternAttribute value) {
        return this;
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
