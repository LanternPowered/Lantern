package org.lanternpowered.server.attribute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.attribute.AttributeModifier;
import org.spongepowered.api.attribute.AttributeModifierBuilder;
import org.spongepowered.api.attribute.Operation;

public final class LanternAttributeModifierBuilder implements AttributeModifierBuilder {

    private Double value;
    private Operation operation;

    /**
     * How can you check whether the value scales between the min and max value
     * if there is no way to access the attribute?
     */
    @Override
    public AttributeModifierBuilder value(double value) throws IllegalArgumentException {
        this.value = value;
        return this;
    }

    @Override
    public AttributeModifierBuilder operation(Operation operation) {
        this.operation = checkNotNull(operation, "operation");
        return this;
    }

    @Override
    public AttributeModifier build() {
        checkState(this.value != null, "value is not set");
        checkState(this.operation != null, "operation is not set");
        return new LanternAttributeModifier(this.operation, this.value);
    }

    @Override
    public AttributeModifierBuilder reset() {
        this.value = null;
        this.operation = null;
        return this;
    }
}
