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
import com.google.common.collect.Streams;
import org.lanternpowered.server.data.AdditionalContainerCollection;
import org.lanternpowered.server.data.AdditionalContainerHolder;
import org.lanternpowered.server.data.IImmutableDataHolder;
import org.lanternpowered.server.data.IValueContainer;
import org.lanternpowered.server.data.MutableToImmutableManipulatorCollection;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.property.IStorePropertyHolder;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.fluid.FluidStack;
import org.spongepowered.api.fluid.FluidStackSnapshot;
import org.spongepowered.api.fluid.FluidType;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class LanternFluidStackSnapshot implements FluidStackSnapshot, IImmutableDataHolder<FluidStackSnapshot>,
        IStorePropertyHolder, AdditionalContainerHolder<ImmutableDataManipulator<?,?>> {

    @Nullable private AdditionalContainerCollection<ImmutableDataManipulator<?, ?>> additionalContainers;
    private final LanternFluidStack fluidStack;

    LanternFluidStackSnapshot(LanternFluidStack fluidStack) {
        this.fluidStack = fluidStack;
    }

    @Override
    public AdditionalContainerCollection<ImmutableDataManipulator<?, ?>> getAdditionalContainers() {
        if (this.additionalContainers == null) {
            this.additionalContainers = new MutableToImmutableManipulatorCollection(this.fluidStack.getAdditionalContainers());
        }
        return this.additionalContainers;
    }

    @Override
    public ValueCollection getValueCollection() {
        // Just use the value collection of the fluid stack
        return this.fluidStack.getValueCollection();
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
    public Optional<FluidStackSnapshot> with(ImmutableDataManipulator<?, ?> valueContainer) {
        final LanternFluidStack copy = this.fluidStack.copy();
        if (copy.offerFast(valueContainer.asMutable())) {
            return Optional.of(new LanternFluidStackSnapshot(copy));
        }
        return Optional.empty();
    }

    @Override
    public Optional<FluidStackSnapshot> with(Iterable<ImmutableDataManipulator<?, ?>> valueContainers) {
        final LanternFluidStack copy = this.fluidStack.copy();
        if (copy.offerFast(Streams.stream(valueContainers)
                .map(ImmutableDataManipulator::asMutable)
                .collect(Collectors.toList()))) {
            return Optional.of(new LanternFluidStackSnapshot(copy));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<FluidStackSnapshot> without(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        final LanternFluidStack copy = this.fluidStack.copy();
        final DataRegistration registration = Lantern.getGame().getDataManager().get(containerClass)
                .orElseThrow(() -> new IllegalStateException("The container class " + containerClass.getName() + " isn't registered."));
        if (copy.removeFast(registration.getManipulatorClass())) {
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
                .add("data", IValueContainer.valuesToString(this.fluidStack))
                .toString();
    }
}
