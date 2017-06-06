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
import org.lanternpowered.server.data.DataHelper;
import org.lanternpowered.server.data.IImmutableValueHolder;
import org.lanternpowered.server.data.IValueContainer;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistration;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistry;
import org.lanternpowered.server.data.manipulator.mutable.IDataManipulator;
import org.lanternpowered.server.util.collect.Collections3;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractImmutableData<I extends ImmutableDataManipulator<I, M>, M extends DataManipulator<M, I>>
        implements IValueContainer<I>, IImmutableDataManipulator<I, M> {

    private final ValueCollection valueCollection;

    private final Class<M> manipulatorType;
    private final Class<I> immutableManipulatorType;

    public AbstractImmutableData(Class<I> immutableManipulatorType, Class<M> manipulatorType) {
        this.valueCollection = ValueCollection.create(ValueCollection.Mode.NON_REMOVABLE);
        this.immutableManipulatorType = immutableManipulatorType;
        this.manipulatorType = manipulatorType;
        registerKeys();
    }

    public AbstractImmutableData(M manipulator) {
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
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", getMutableType().getName())
                .add("values", Collections3.toString(getValues()))
                .toString();
    }

    public static abstract class AbstractImmutableManipulatorDataBuilder<I extends ImmutableDataManipulator<I, M>, M extends DataManipulator<M, I>>
            extends AbstractDataBuilder<I> {

        protected AbstractImmutableManipulatorDataBuilder(Class<I> requiredClass, int supportedVersion) {
            super(requiredClass, supportedVersion);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Optional<I> buildContent(DataView container) throws InvalidDataException {
            return (Optional) DataHelper.buildContent(container, () ->  (IValueContainer) create());
        }

        protected abstract I create();
    }
}
