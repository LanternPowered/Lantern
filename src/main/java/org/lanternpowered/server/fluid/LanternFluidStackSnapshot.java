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

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.data.LocalImmutableDataHolder;
import org.lanternpowered.server.data.LocalKeyRegistry;
import org.lanternpowered.server.data.property.PropertyHolderBase;
import org.lanternpowered.server.data.value.ValueFactory;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.value.MergeFunction;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.fluid.FluidStack;
import org.spongepowered.api.fluid.FluidStackSnapshot;
import org.spongepowered.api.fluid.FluidType;

import java.util.Optional;
import java.util.function.Function;

public final class LanternFluidStackSnapshot implements FluidStackSnapshot, LocalImmutableDataHolder<FluidStackSnapshot>, PropertyHolderBase {

    private final LanternFluidStack fluidStack;

    LanternFluidStackSnapshot(LanternFluidStack fluidStack) {
        this.fluidStack = fluidStack;
    }

    @Override
    public LocalKeyRegistry<LanternFluidStackSnapshot> getKeyRegistry() {
        return this.fluidStack.getKeyRegistry().forHolder(LanternFluidStackSnapshot.class);
    }

    @Override
    public FluidType getFluid() {
        return this.fluidStack.getFluid();
    }

    @Override
    public int getVolume() {
        return this.fluidStack.getVolume();
    }

    @Override
    public FluidStack createStack() {
        return this.fluidStack.copy();
    }

    @Override
    public <E> Optional<FluidStackSnapshot> transform(Key<? extends Value<E>> key, Function<E, E> function) {
        final LanternFluidStack copy = this.fluidStack.copy();
        if (copy.transformFast(key, function)) {
            return Optional.of(new LanternFluidStackSnapshot(copy));
        }
        return Optional.empty();
    }

    @Override
    public <E> Optional<FluidStackSnapshot> with(Key<? extends Value<E>> key, E value) {
        final LanternFluidStack copy = this.fluidStack.copy();
        if (copy.offerFast(key, value)) {
            return Optional.of(new LanternFluidStackSnapshot(copy));
        }
        return Optional.empty();
    }

    @Override
    public FluidStackSnapshot merge(FluidStackSnapshot that, MergeFunction function) {
        final LanternFluidStack copy = this.fluidStack.copy();
        copy.copyFromNoEvents(((LanternFluidStackSnapshot) that).fluidStack, function);
        return new LanternFluidStackSnapshot(copy);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fluid", getFluid().getKey())
                .add("volume", getVolume())
                .add("data", ValueFactory.INSTANCE.toString(this.fluidStack))
                .toString();
    }
}
