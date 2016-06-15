/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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

import org.lanternpowered.server.catalog.SimpleCatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(LanternOperations.class)
public class LanternOperation extends SimpleCatalogType.Base implements Comparable<LanternOperation> {

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
    public int compareTo(LanternOperation operation) {
        if (operation instanceof LanternOperation) {
            return this.priority - ((LanternOperation) operation).priority;
        }
        return operation.compareTo(this);
    }

    public double getIncrementation(double base, double modifier, double currentValue) {
        return this.function.getIncrementation(base, modifier, currentValue);
    }

    public boolean changeValueImmediately() {
        return this.changeValueImmediately;
    }
}
