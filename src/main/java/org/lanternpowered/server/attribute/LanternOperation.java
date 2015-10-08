package org.lanternpowered.server.attribute;

import org.lanternpowered.server.catalog.SimpleLanternCatalogType;
import org.spongepowered.api.attribute.Operation;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class LanternOperation extends SimpleLanternCatalogType implements Operation {

    // The priority of the operation
    private final int priority;

    public LanternOperation(String name, int priority) {
        super(name);

        checkArgument(priority >= 0, "priority may not be negative");
        this.priority = priority;
    }

    @Override
    public int compareTo(Operation operation) {
        if (operation instanceof LanternOperation) {
            return this.priority - ((LanternOperation) operation).priority;
        }
        // TODO: How will we handle custom (plugin) operations?
        return operation.compareTo(this);
    }
}
