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
package org.lanternpowered.server.fluid;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.fluid.FluidStackSnapshot;

public final class LanternFluidStackSnapshotBuilder extends AbstractFluidStackBuilder<FluidStackSnapshot, FluidStackSnapshot.Builder>
        implements FluidStackSnapshot.Builder {

    public LanternFluidStackSnapshotBuilder() {
        super(FluidStackSnapshot.class);
    }

    @Override
    public FluidStackSnapshot.Builder add(DataManipulator<?, ?> manipulator) {
        fluidStack(null).offerFastNoEvents(manipulator, MergeFunction.IGNORE_ALL);
        return this;
    }

    @Override
    public FluidStackSnapshot.Builder add(ImmutableDataManipulator<?, ?> manipulator) {
        return add(manipulator.asMutable());
    }

    @Override
    public <V> FluidStackSnapshot.Builder add(Key<? extends BaseValue<V>> key, V value) {
        fluidStack(null).offerFastNoEvents(key, value);
        return this;
    }

    @Override
    public FluidStackSnapshot build() {
        return new LanternFluidStackSnapshot(buildStack());
    }
}
