/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.attribute.AttributeModifier;
import org.spongepowered.api.attribute.AttributeModifierBuilder;
import org.spongepowered.api.attribute.Operation;

public final class LanternAttributeModifierBuilder implements AttributeModifierBuilder {

    private Double value;
    private Operation operation;

    /**
     * How can you check whether the value scales between the min and max value
     * if there is no way to access the attribute?
     */
    @Override
    public AttributeModifierBuilder value(double value) throws IllegalArgumentException {
        this.value = value;
        return this;
    }

    @Override
    public AttributeModifierBuilder operation(Operation operation) {
        this.operation = checkNotNull(operation, "operation");
        return this;
    }

    @Override
    public AttributeModifier build() {
        checkState(this.value != null, "value is not set");
        checkState(this.operation != null, "operation is not set");
        return new LanternAttributeModifier(this.operation, this.value);
    }

    @Override
    public AttributeModifierBuilder reset() {
        this.value = null;
        this.operation = null;
        return this;
    }
}
