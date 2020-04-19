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

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LanternAttributeCalculator {

    public double calculateValue(double base, Collection<LanternAttributeModifier> modifiers) {
        List<LanternAttributeModifier> modifiers0 = Lists.newArrayList(modifiers);

        // Sort the modifiers by operation priority
        Collections.sort(modifiers0, (m1, m2) -> m1.getOperation().compareTo(m2.getOperation()));

        // The last operation
        LanternOperation lastOperation = null;

        // The new value
        double value = base;
        double value0 = 0;

        // Add the incrementations of all the modifiers
        for (LanternAttributeModifier modifier : modifiers) {
            LanternOperation operation = modifier.getOperation();

            if (lastOperation == null || operation.equals(lastOperation)) {
                double value1 = operation.getIncrementation(base, modifier.getValue(), value);
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
