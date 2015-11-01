/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.attribute;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

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
