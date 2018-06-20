/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.lanternpowered.server.inventory.property;

import com.flowpowered.math.vector.Vector2i;
import org.spongepowered.api.item.inventory.property.SlotPos;

public final class LanternSlotPos extends NonnullAbstractComparableProperty<String, Vector2i> implements SlotPos {

    public LanternSlotPos(Vector2i value) {
        super(value);
    }

    public LanternSlotPos(Vector2i value, Operator operator) {
        super(value, operator);
    }

    @Override
    public int getX() {
        return getValue().getX();
    }

    @Override
    public int getY() {
        return getValue().getY();
    }

    public static final class Builder extends AbstractInventoryPropertyBuilder<Vector2i, SlotPos, SlotPos.Builder>
            implements SlotPos.Builder {

        @Override
        public SlotPos build() {
            return new LanternSlotPos(this.value, this.operator);
        }
    }
}
