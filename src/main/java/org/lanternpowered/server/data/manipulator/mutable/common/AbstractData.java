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
package org.lanternpowered.server.data.manipulator.mutable.common;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.data.manipulator.ManipulatorHelper;
import org.lanternpowered.server.data.value.AbstractValueContainer;
import org.lanternpowered.server.data.value.ElementHolder;
import org.lanternpowered.server.data.value.KeyRegistration;
import org.lanternpowered.server.data.value.LanternValueFactory;
import org.lanternpowered.server.data.value.processor.ValueProcessor;
import org.lanternpowered.server.util.functions.TriFunction;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
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

public class AbstractData<M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> implements AbstractValueContainer<M>, DataManipulator<M, I> {

    private final Map<Key<?>, KeyRegistration> rawValueMap = new HashMap<>();
    private final Class<M> manipulatorType;

    public AbstractData(Class<M> manipulatorType) {
        this.manipulatorType = manipulatorType;
    }

    @Override
    public Map<Key<?>, KeyRegistration> getRawValueMap() {
        return this.rawValueMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<M> fill(DataHolder dataHolder, MergeFunction overlap) {
        return Optional.of(checkNotNull(overlap, "overlap").merge(this.copy(), dataHolder.get(this.manipulatorType).orElse(null)));
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
        KeyRegistration<BaseValue<E>, E> localKeyRegistration = this.getKeyRegistration(key);
        if (localKeyRegistration == null) {
            if (this.requiresKeyRegistration()) {
                this.throwUnsupportedKeyException(key);
            }
        } else {
            List<ValueProcessor<BaseValue<E>, E>> processors = localKeyRegistration.getValueProcessors();
            if (!processors.isEmpty()) {
                return this.setWith(key, value, processors.get(0));
            }
        }

        // Check the global key registrations
        KeyRegistration<BaseValue<E>, E> keyRegistration = LanternValueFactory.getInstance().getKeyRegistration(key);
        if (keyRegistration != null) {
            for (ValueProcessor<BaseValue<E>, E> valueProcessor : keyRegistration.getValueProcessors()) {
                if (valueProcessor.getApplicableTester().test((Key) key, this)) {
                    return this.setWith(key, value, valueProcessor);
                }
            }
        }

        // Use the global processor
        if (localKeyRegistration != null && localKeyRegistration instanceof ElementHolder) {
            return this.setWith(key, value, ValueProcessor.getDefaultAttachedValueProcessor());
        }

        this.throwUnsupportedKeyException(key);
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
        KeyRegistration<BaseValue<E>, E> localKeyRegistration = this.getKeyRegistration(key);
        if (localKeyRegistration == null) {
            if (this.requiresKeyRegistration()) {
                this.throwUnsupportedKeyException(key);
            }
        } else {
            List<ValueProcessor<BaseValue<E>, E>> processors = localKeyRegistration.getValueProcessors();
            if (!processors.isEmpty()) {
                return this.transformWith(key, function, processors.get(0));
            }
        }

        // Check the global key registrations
        KeyRegistration<BaseValue<E>, E> keyRegistration = LanternValueFactory.getInstance().getKeyRegistration(key);
        if (keyRegistration != null) {
            for (ValueProcessor<BaseValue<E>, E> valueProcessor : keyRegistration.getValueProcessors()) {
                if (valueProcessor.getApplicableTester().test((Key) key, this)) {
                    return this.transformWith(key, function, valueProcessor);
                }
            }
        }

        // Use the global processor
        if (localKeyRegistration != null && localKeyRegistration instanceof ElementHolder) {
            return this.transformWith(key, function, ValueProcessor.getDefaultAttachedValueProcessor());
        }

        this.throwUnsupportedKeyException(key);
        return (M) this;
    }

    @Override
    public I asImmutable() {
        return null;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataContainer toContainer() {
        return ManipulatorHelper.toContainer(this);
    }

    @Override
    public M copy() {
        return null;
    }

    public static abstract class AbstractManipulatorDataBuilder<T extends AbstractData> extends AbstractDataBuilder<T> {

        protected AbstractManipulatorDataBuilder(Class<T> requiredClass, int supportedVersion) {
            super(requiredClass, supportedVersion);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Optional<T> buildContent(DataView container) throws InvalidDataException {
            return ManipulatorHelper.buildContent(container, this::buildManipulator);
        }

        protected abstract T buildManipulator();
    }
}
