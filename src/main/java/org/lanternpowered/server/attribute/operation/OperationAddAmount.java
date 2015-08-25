package org.lanternpowered.server.attribute.operation;

import org.lanternpowered.server.attribute.LanternOperation;

public class OperationAddAmount extends LanternOperation {

    public OperationAddAmount(String identifier, String name, int priority) {
        super(identifier, name, priority);
    }

    @Override
    public double getIncrementation(double base, double modifier, double currentValue) {
        return modifier;
    }

    @Override
    public boolean changeValueImmediately() {
        return false;
    }
}
