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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.extra.fluid.FluidStack;
import org.spongepowered.api.extra.fluid.FluidStackSnapshot;
import org.spongepowered.api.extra.fluid.FluidType;
import org.spongepowered.api.extra.fluid.FluidTypes;

import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
abstract class AbstractFluidStackBuilder<T extends DataSerializable, B extends DataBuilder<T>> extends AbstractDataBuilder<T> {

    @Nullable private LanternFluidStack fluidStack;
    private boolean fluidTypeSet;

    AbstractFluidStackBuilder(Class<T> type) {
        super(type, 1);
    }

    LanternFluidStack fluidStack(@Nullable FluidType fluidType) {
        if (fluidType != null) {
            if (this.fluidStack == null) {
                this.fluidStack = new LanternFluidStack(fluidType, 0);
            } else if (this.fluidStack.getFluid() != fluidType) {
                final FluidStack old = this.fluidStack;
                this.fluidStack = new LanternFluidStack(fluidType, 0);
                this.fluidStack.setVolume(old.getVolume());
                this.fluidStack.copyFrom(old);
            }
            this.fluidTypeSet = true;
        } else if (this.fluidStack == null) {
            this.fluidStack = new LanternFluidStack(FluidTypes.WATER, 0);
        }
        return this.fluidStack;
    }

    public B fluid(FluidType fluidType) {
        fluidStack(checkNotNull(fluidType, "fluidType"));
        return (B) this;
    }

    public B volume(int volume) {
        fluidStack(null).setVolume(volume);
        return (B) this;
    }

    LanternFluidStack buildStack() {
        checkState(this.fluidTypeSet, "The fluid type must be set");
        return fluidStack(null).copy();
    }

    public B from(FluidStackSnapshot fluidStackSnapshot) {
        this.fluidStack = (LanternFluidStack) fluidStackSnapshot.createStack();
        this.fluidTypeSet = true;
        return (B) this;
    }

    public B from(FluidStack value) {
        this.fluidStack = (LanternFluidStack) value.copy();
        this.fluidTypeSet = true;
        return (B) this;
    }

    public B reset() {
        this.fluidStack = null;
        this.fluidTypeSet = false;
        return (B) this;
    }

    @Override
    protected Optional<T> buildContent(DataView container) throws InvalidDataException {
        throw new UnsupportedOperationException("TODO");
    }
}
