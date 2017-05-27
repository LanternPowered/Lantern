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
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistration;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistry;
import org.lanternpowered.server.data.manipulator.IDataManipulatorBase;
import org.lanternpowered.server.data.DataHelper;
import org.lanternpowered.server.data.manipulator.immutable.IImmutableDataManipulator;
import org.lanternpowered.server.data.value.AbstractValueContainer;
import org.lanternpowered.server.data.value.ElementHolder;
import org.lanternpowered.server.data.value.ElementHolderKeyRegistration;
import org.lanternpowered.server.data.value.KeyRegistration;
import org.lanternpowered.server.data.value.LanternValueFactory;
import org.lanternpowered.server.data.value.processor.ValueProcessor;
import org.lanternpowered.server.util.collect.Collections3;
import org.lanternpowered.server.util.functions.TriFunction;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nullable;

public abstract class AbstractData<M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> implements AbstractValueContainer<M, I>,
        IDataManipulator<M, I> {

    private final Map<Key<?>, KeyRegistration> rawValueMap;
    private final Class<M> manipulatorType;
    private final Class<I> immutableManipulatorType;

    protected AbstractData(Class<M> manipulatorType, Class<I> immutableManipulatorType) {
        this.immutableManipulatorType = immutableManipulatorType;
        this.manipulatorType = manipulatorType;
        this.rawValueMap = new HashMap<>();
        registerKeys();
    }

    protected AbstractData(I manipulator) {
        //noinspection unchecked
        this((IDataManipulatorBase<M, I>) manipulator);
    }

    protected AbstractData(M manipulator) {
        //noinspection unchecked
        this((IDataManipulatorBase<M, I>) manipulator);
    }

    protected AbstractData(IDataManipulatorBase<M, I> manipulator) {
        //noinspection unchecked
        final IImmutableDataManipulator<I, M> iDataManipulator = (IImmutableDataManipulator<I, M>) manipulator;
        this.immutableManipulatorType = iDataManipulator.getImmutableType();
        this.manipulatorType = iDataManipulator.getMutableType();
        if (manipulator instanceof AbstractValueContainer) {
            //noinspection unchecked
            this.rawValueMap = ((AbstractValueContainer) manipulator).copyRawValueMap();
        } else {
            throw new IllegalArgumentException(
                    "The default DataManipulator's should extend AbstractValueContainer, others are currently unsupported.");
        }
    }

    @Override
    public <V extends BaseValue<E>, E> ElementHolderKeyRegistration<V, E> registerKey(Key<? extends V> key, @Nullable E defaultValue) {
        // Data container keys are non removable by default
        final ElementHolderKeyRegistration<V, E> registration = AbstractValueContainer.super.registerKey(key, defaultValue);
        if (defaultValue != null) {
            registration.notRemovable();
        }
        return registration;
    }

    @Override
    public Map<Key<?>, KeyRegistration> getRawValueMap() {
        return this.rawValueMap;
    }

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    private <E> M setWith(Key<? extends BaseValue<E>> key, E value, ValueProcessor<BaseValue<E>, E> processor) {
        ((TriFunction) processor.getOfferHandler()).apply(key, this, value);
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> M set(Key<? extends BaseValue<E>> key, E value) {
        checkNotNull(key, "key");
        checkNotNull(value, "value");

        // Check the local key registration
        KeyRegistration<BaseValue<E>, E> localKeyRegistration = getKeyRegistration(key);
        if (localKeyRegistration == null) {
            if (requiresKeyRegistration()) {
                throwUnsupportedKeyException(key);
            }
        } else {
            List<ValueProcessor<BaseValue<E>, E>> processors = localKeyRegistration.getValueProcessors();
            if (!processors.isEmpty()) {
                return setWith(key, value, processors.get(0));
            }
        }

        // Check the global key registrations
        KeyRegistration<BaseValue<E>, E> keyRegistration = LanternValueFactory.getInstance().getKeyRegistration(key);
        if (keyRegistration != null) {
            for (ValueProcessor<BaseValue<E>, E> valueProcessor : keyRegistration.getValueProcessors()) {
                if (valueProcessor.getApplicableTester().test((Key) key, this)) {
                    return setWith(key, value, valueProcessor);
                }
            }
        }

        // Use the global processor
        if (localKeyRegistration != null && localKeyRegistration instanceof ElementHolder) {
            return setWith(key, value, ValueProcessor.getDefaultAttachedValueProcessor());
        }

        throwUnsupportedKeyException(key);
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    private <E> M transformWith(Key<? extends BaseValue<E>> key, Function<E, E> function, ValueProcessor<BaseValue<E>, E> processor) {
        ((TriFunction) processor.getOfferHandler()).apply(key, this, function.apply(
                (E) ((Optional) ((BiFunction) processor.getRetrieveHandler()).apply(key, this)).orElse(null)));
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> M transform(Key<? extends BaseValue<E>> key, Function<E, E> function) {
        checkNotNull(key, "key");
        checkNotNull(function, "function");

        // Check the local key registration
        KeyRegistration<BaseValue<E>, E> localKeyRegistration = getKeyRegistration(key);
        if (localKeyRegistration == null) {
            if (requiresKeyRegistration()) {
                throwUnsupportedKeyException(key);
            }
        } else {
            List<ValueProcessor<BaseValue<E>, E>> processors = localKeyRegistration.getValueProcessors();
            if (!processors.isEmpty()) {
                return transformWith(key, function, processors.get(0));
            }
        }

        // Check the global key registrations
        KeyRegistration<BaseValue<E>, E> keyRegistration = LanternValueFactory.getInstance().getKeyRegistration(key);
        if (keyRegistration != null) {
            for (ValueProcessor<BaseValue<E>, E> valueProcessor : keyRegistration.getValueProcessors()) {
                if (valueProcessor.getApplicableTester().test((Key) key, this)) {
                    return transformWith(key, function, valueProcessor);
                }
            }
        }

        // Use the global processor
        if (localKeyRegistration != null && localKeyRegistration instanceof ElementHolder) {
            return transformWith(key, function, ValueProcessor.getDefaultAttachedValueProcessor());
        }

        throwUnsupportedKeyException(key);
        return (M) this;
    }

    @Override
    public I asImmutable() {
        final DataManipulatorRegistration<M, I> registration = DataManipulatorRegistry.get().getByImmutable(this.immutableManipulatorType).get();
        //noinspection unchecked
        return registration.toImmutable((M) this);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataContainer toContainer() {
        return DataHelper.toContainer(this);
    }

    @Override
    public M copy() {
        final DataManipulatorRegistration<M, I> registration = DataManipulatorRegistry.get().getByImmutable(this.immutableManipulatorType).get();
        //noinspection unchecked
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
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", getMutableType().getName())
                .add("values", Collections3.toString(getValues()))
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

        @SuppressWarnings("unchecked")
        @Override
        protected Optional<M> buildContent(DataView container) throws InvalidDataException {
            return (Optional) DataHelper.buildContent(container, () -> (AbstractValueContainer) create());
        }
    }
}
