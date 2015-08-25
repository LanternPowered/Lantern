package org.lanternpowered.server.attribute;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.spongepowered.api.attribute.AttributeCalculator;
import org.spongepowered.api.attribute.AttributeModifier;
import org.spongepowered.api.attribute.Operation;

import com.google.common.collect.Lists;

public class LanternAttributeCalculator implements AttributeCalculator {

    @Override
    public double calculateValue(double base, Collection<AttributeModifier> modifiers) {
        List<AttributeModifier> modifiers0 = Lists.newArrayList(modifiers);

        // Sort the modifiers by operation priority
        Collections.sort(modifiers0, new Comparator<AttributeModifier>() {

            @Override
            public int compare(AttributeModifier arg0, AttributeModifier arg1) {
                return arg0.getOperation().compareTo(arg1.getOperation());
            }

        });

        // The last operation
        Operation lastOperation = null;

        // The new value
        double value = base;
        double value0 = 0;

        // Add the incrementations of all the modifiers
        for (AttributeModifier modifier : modifiers) {
            Operation operation = modifier.getOperation();

            if (lastOperation == null || operation.equals(lastOperation)) {
                double value1 = modifier.getOperation().getIncrementation(base, modifier.getValue(), value);
                if (operation.changeValueImmediately()) {
                    value += value1;
                } else {
                    value0 += value1;
                }
            } else {
                lastOperation = operation;
                value += value0;
                value0 = 0;
            }
        }

        value += value0;
        return value;
    }
}
