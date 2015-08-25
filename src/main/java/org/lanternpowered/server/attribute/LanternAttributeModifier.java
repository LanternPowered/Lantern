package org.lanternpowered.server.attribute;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.attribute.AttributeModifier;
import org.spongepowered.api.attribute.Operation;

public class LanternAttributeModifier implements AttributeModifier {

    private final Operation operation;
    private final double value;

    public LanternAttributeModifier(Operation operation, double value) {
        checkNotNull(operation, "operation");

        this.operation = operation;
        this.value = value;
    }

    @Override
    public Operation getOperation() {
        return this.operation;
    }

    @Override
    public double getValue() {
        return this.value;
    }
}
