package org.lanternpowered.server.attribute;

import org.lanternpowered.server.catalog.SimpleLanternCatalogType;
import org.spongepowered.api.attribute.Operation;

public class LanternOperation extends SimpleLanternCatalogType implements Operation {

    private final int priority;
    private final boolean changeValueImmediately;

    private final OperationFunction function;

    public LanternOperation(String name, int priority, boolean changeValueImmediately,
            OperationFunction function) {
        super(name);

        this.function = function;
        this.priority = priority;
        this.changeValueImmediately = changeValueImmediately;
    }

    @Override
    public int compareTo(Operation operation) {
        if (operation instanceof LanternOperation) {
            return this.priority - ((LanternOperation) operation).priority;
        }
        return operation.compareTo(this);
    }

    @Override
    public double getIncrementation(double base, double modifier, double currentValue) {
        return this.function.getIncrementation(base, modifier, currentValue);
    }

    @Override
    public boolean changeValueImmediately() {
        return this.changeValueImmediately;
    }
}
