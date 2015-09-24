package org.lanternpowered.server.attribute.operation;

import org.lanternpowered.server.attribute.LanternOperation;

public class OperationMultiplyBase extends LanternOperation {

    public OperationMultiplyBase(String name, int priority) {
        super(name, priority);
    }

    @Override
    public double getIncrementation(double base, double modifier, double currentValue) {
        return base * modifier - base;
    }

    @Override
    public boolean changeValueImmediately() {
        return false;
    }
}
