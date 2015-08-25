package org.lanternpowered.server.attribute;

import org.lanternpowered.server.catalog.SimpleCatalogType;
import org.spongepowered.api.attribute.Operation;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class LanternOperation extends SimpleCatalogType implements Operation {

    // The priority of the operation
    private final int priority;

    public LanternOperation(String identifier, String name, int priority) {
        super(identifier, name);

        checkArgument(priority >= 0, "priority may not be negative");
        this.priority = priority;
    }

    @Override
    public int compareTo(Operation operation) {
        return this.priority - ((LanternOperation) operation).priority;
    }
}
