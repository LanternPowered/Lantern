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
package org.lanternpowered.server.data.manipulator.immutable;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterables;
import org.lanternpowered.server.data.DataHelper;
import org.lanternpowered.server.data.IValueContainer;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistration;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistry;
import org.lanternpowered.server.data.manipulator.mutable.IDataManipulator;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

@SuppressWarnings("unchecked")
public abstract class AbstractImmutableData<I extends ImmutableDataManipulator<I, M>, M extends DataManipulator<M, I>>
        implements IValueContainer<I>, IImmutableDataManipulator<I, M> {

    private final ValueCollection valueCollection;

    private final Class<M> manipulatorType;
    private final Class<I> immutableManipulatorType;

    protected AbstractImmutableData(Class<I> immutableManipulatorType, Class<M> manipulatorType) {
        this.valueCollection = ValueCollection.create(ValueCollection.Mode.NON_REMOVABLE);
        this.immutableManipulatorType = immutableManipulatorType;
        this.manipulatorType = manipulatorType;
        registerKeys();
    }

    protected AbstractImmutableData(M manipulator) {
        final IDataManipulator<M, I> iDataManipulator = (IDataManipulator<M, I>) manipulator;
        this.immutableManipulatorType = iDataManipulator.getImmutableType();
        this.manipulatorType = iDataManipulator.getMutableType();
        this.valueCollection = ((IValueContainer) manipulator).getValueCollection().copy();
    }

    @Override
    public ValueCollection getValueCollection() {
        return this.valueCollection;
    }

    @Override
    public M asMutable() {
        final DataManipulatorRegistration<M, I> registration = DataManipulatorRegistry.get().getByImmutable(this.immutableManipulatorType).get();
        return registration.toMutable((I) this);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return DataHelper.toContainer(this);
    }

    @Override
    public Class<I> getImmutableType() {
        return this.immutableManipulatorType;
    }

    @Override
    public Class<M> getMutableType() {
        return this.manipulatorType;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !this.immutableManipulatorType.isInstance(other)) {
            return false;
        }
        final IValueContainer<I> manipulator = (IValueContainer<I>) other;
        return IValueContainer.matchContents(this, manipulator);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", getMutableType().getName())
                .add("values", Iterables.toString(getValues()))
                .toString();
    }

    public static abstract class AbstractImmutableManipulatorDataBuilder<I extends ImmutableDataManipulator<I, M>, M extends DataManipulator<M, I>>
            extends AbstractDataBuilder<I> {

        protected AbstractImmutableManipulatorDataBuilder(Class<I> requiredClass, int supportedVersion) {
            super(requiredClass, supportedVersion);
        }

        @Override
        protected Optional<I> buildContent(DataView container) throws InvalidDataException {
            return (Optional) DataHelper.buildContent(container, () ->  (IValueContainer) create());
        }

        protected abstract I create();
    }
}
