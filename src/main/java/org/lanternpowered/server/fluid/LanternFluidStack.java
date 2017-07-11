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

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.data.AdditionalContainerCollection;
import org.lanternpowered.server.data.DataQueries;
import org.lanternpowered.server.data.IAdditionalDataHolder;
import org.lanternpowered.server.data.IValueContainer;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.extra.fluid.FluidStack;
import org.spongepowered.api.extra.fluid.FluidStackSnapshot;
import org.spongepowered.api.extra.fluid.FluidType;

public class LanternFluidStack implements FluidStack, AbstractPropertyHolder, IAdditionalDataHolder {

    private final ValueCollection valueCollection;
    private final AdditionalContainerCollection<DataManipulator<?, ?>> additionalContainers;
    private final FluidType fluidType;
    private int volume;

    public LanternFluidStack(FluidType fluidType, int volume) {
        this(fluidType, volume, ValueCollection.create(ValueCollection.Mode.NORMAL), AdditionalContainerCollection.create());
    }

    public LanternFluidStack(FluidType fluidType, int volume,
            ValueCollection valueCollection, AdditionalContainerCollection<DataManipulator<?, ?>> additionalContainers) {
        this.valueCollection = valueCollection;
        this.additionalContainers = additionalContainers;
        this.fluidType = fluidType;
        this.volume = volume;
    }

    @Override
    public ValueCollection getValueCollection() {
        return this.valueCollection;
    }

    @Override
    public AdditionalContainerCollection<DataManipulator<?, ?>> getAdditionalContainers() {
        return this.additionalContainers;
    }

    @Override
    public FluidType getFluid() {
        return this.fluidType;
    }

    @Override
    public int getVolume() {
        return this.volume;
    }

    @Override
    public LanternFluidStack setVolume(int volume) {
        checkArgument(volume >= 0, "volume cannot be negative");
        checkArgument(volume <= 1000, "volume cannot be greater then 1000");
        this.volume = volume;
        return this;
    }

    @Override
    public FluidStackSnapshot createSnapshot() {
        return new LanternFluidStackSnapshot(copy());
    }

    @Override
    public LanternFluidStack copy() {
        return new LanternFluidStack(getFluid(), getVolume(),
                getValueCollection().copy(), getAdditionalContainers().copy());
    }

    @Override
    public boolean validateRawData(DataView container) {
        return container.contains(DataQueries.FLUID_TYPE);
    }

    @Override
    public void setRawData(DataView dataView) throws InvalidDataException {
        checkNotNull(dataView, "dataView");
        dataView.remove(DataQueries.FLUID_TYPE);
        this.volume = dataView.getInt(DataQueries.VOLUME).orElse(0);
        IAdditionalDataHolder.super.setRawData(dataView);
    }

    @Override
    public DataContainer toContainer() {
        return IAdditionalDataHolder.super.toContainer()
                .set(DataQueries.FLUID_TYPE, getFluid())
                .set(DataQueries.VOLUME, getVolume());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fluid", getFluid().getId())
                .add("volume", getVolume())
                .add("data", IValueContainer.valuesToString(this))
                .toString();
    }
}
