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
package org.lanternpowered.server.data.manipulator.mutable;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterables;
import org.lanternpowered.server.data.DataHelper;
import org.lanternpowered.server.data.IValueContainer;
import org.lanternpowered.server.data.KeyRegistration;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistration;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistry;
import org.lanternpowered.server.data.manipulator.IDataManipulatorBase;
import org.lanternpowered.server.data.manipulator.immutable.IImmutableDataManipulator;
import org.lanternpowered.server.data.processor.Processor;
import org.lanternpowered.server.data.processor.ValueProcessorKeyRegistration;
import org.lanternpowered.server.data.value.LanternValueFactory;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.Value;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public abstract class AbstractData<M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>>
        implements IValueContainer<M>, IDataManipulator<M, I> {

    private final ValueCollection valueCollection;
    private final Class<M> manipulatorType;
    private final Class<I> immutableManipulatorType;

    protected AbstractData(Class<M> manipulatorType, Class<I> immutableManipulatorType) {
        this.valueCollection = ValueCollection.create(ValueCollection.Mode.NON_REMOVABLE);
        this.immutableManipulatorType = immutableManipulatorType;
        this.manipulatorType = manipulatorType;
        registerKeys();
    }

    protected AbstractData(I manipulator) {
        this((IDataManipulatorBase<M, I>) manipulator);
    }

    protected AbstractData(M manipulator) {
        this((IDataManipulatorBase<M, I>) manipulator);
    }

    protected AbstractData(IDataManipulatorBase<M, I> manipulator) {
        final IImmutableDataManipulator<I, M> iDataManipulator = (IImmutableDataManipulator<I, M>) manipulator;
        this.immutableManipulatorType = iDataManipulator.getImmutableType();
        this.manipulatorType = iDataManipulator.getMutableType();
        this.valueCollection = ((IValueContainer) manipulator).getValueCollection().copy();
    }

    @Override
    public ValueCollection getValueCollection() {
        return this.valueCollection;
    }

    @Override
    public Optional<M> fill(DataHolder dataHolder, MergeFunction overlap) {
        return Optional.of(checkNotNull(overlap, "overlap").merge(copy(), dataHolder.get(this.manipulatorType).orElse(null)));
    }

    @Override
    public Optional<M> from(DataContainer container) {
        return Optional.empty();
    }

    private void throwUnsupportedKeyException(Key<?> key) {
        throw new IllegalArgumentException("This data manipulator doesn't support the following key: " + key.toString());
    }

    @Override
    public <E> M set(Key<? extends Value<E>> key, E value) {
        checkNotNull(key, "key");
        checkNotNull(value, "value");

        // Check the local key registration
        final KeyRegistration<Value<E>, E> localKeyRegistration = getValueCollection().get(key).orElse(null);
        if (localKeyRegistration != null) {
            ((Processor<?, E>) localKeyRegistration).offerTo(this, value);
            return (M) this;
        }

        // Check for a global registration
        final Optional<ValueProcessorKeyRegistration<Value<E>, E>> globalRegistration = LanternValueFactory.get().getKeyRegistration(key);
        if (globalRegistration.isPresent()) {
            ((Processor<Value<E>, E>) globalRegistration.get()).offerTo(this, value);
            return (M) this;
        }

        throwUnsupportedKeyException(key);
        return (M) this;
    }

    private <E> M transformWith(Function<E, E> function, Processor<Value<E>, E> processor) {
        processor.offerTo(this, function.apply(processor.getFrom(this).orElse(null)));
        return (M) this;
    }

    @Override
    public <E> M transform(Key<? extends Value<E>> key, Function<E, E> function) {
        checkNotNull(key, "key");
        checkNotNull(function, "function");

        // Check the local key registration
        final KeyRegistration<Value<E>, E> localKeyRegistration = getValueCollection().get(key).orElse(null);
        if (localKeyRegistration != null) {
            return transformWith(function, (Processor<Value<E>, E>) localKeyRegistration);
        }

        // Check for a global registration
        final Optional<ValueProcessorKeyRegistration<Value<E>, E>> globalRegistration = LanternValueFactory.get().getKeyRegistration(key);
        if (globalRegistration.isPresent()) {
            return transformWith(function, (Processor<Value<E>, E>) globalRegistration.get());
        }

        throwUnsupportedKeyException(key);
        return (M) this;
    }

    @Override
    public I asImmutable() {
        final DataManipulatorRegistration<M, I> registration = DataManipulatorRegistry.get().getByImmutable(this.immutableManipulatorType).get();
        return registration.toImmutable((M) this);
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
    public M copy() {
        final DataManipulatorRegistration<M, I> registration = DataManipulatorRegistry.get().getByImmutable(this.immutableManipulatorType).get();
        return registration.copyMutable((M) this);
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
        if (other == null || !this.manipulatorType.isInstance(other)) {
            return false;
        }
        final IValueContainer<M> manipulator = (IValueContainer<M>) other;
        return IValueContainer.matchContents(this, manipulator);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", getMutableType().getName())
                .add("values", Iterables.toString(getValues()))
                .toString();
    }

    public static abstract class AbstractManipulatorDataBuilder<M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>>
            extends AbstractDataBuilder<M> implements DataManipulatorBuilder<M, I> {

        private final Class<M> requiredClass;

        protected AbstractManipulatorDataBuilder(Class<M> requiredClass, int supportedVersion) {
            super(requiredClass, supportedVersion);
            this.requiredClass = requiredClass;
        }

        @Override
        public Optional<M> createFrom(DataHolder dataHolder) {
            return dataHolder.get(this.requiredClass);
        }

        @Override
        protected Optional<M> buildContent(DataView container) throws InvalidDataException {
            return (Optional) DataHelper.buildContent(container, () -> (IValueContainer) create());
        }
    }
}
