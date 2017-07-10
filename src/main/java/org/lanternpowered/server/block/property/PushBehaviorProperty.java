package org.lanternpowered.server.block.property;

import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.property.AbstractProperty;

@SuppressWarnings("ConstantConditions")
public final class PushBehaviorProperty extends AbstractProperty<String, PushBehavior> {

    public PushBehaviorProperty(PushBehavior value) {
        super(value);
    }

    public PushBehaviorProperty(PushBehavior value, Operator operator) {
        super(value, operator);
    }

    @Override
    public int compareTo(Property<?, ?> o) {
        return o == null ?  0 : getValue().compareTo((PushBehavior) o.getValue());
    }
}
