/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.data.value.mutable;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.data.value.AbstractValueContainer;
import org.lanternpowered.server.data.value.ElementHolder;
import org.lanternpowered.server.data.value.KeyRegistration;
import org.lanternpowered.server.data.value.LanternValueFactory;
import org.lanternpowered.server.data.value.ValueHelper;
import org.lanternpowered.server.data.value.processor.ValueProcessor;
import org.lanternpowered.server.util.TriFunction;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.CompositeValueStore;
import org.spongepowered.api.event.cause.Cause;

import java.util.List;
import java.util.function.BiFunction;

public interface AbstractCompositeValueStore<S extends CompositeValueStore<S, H>, H extends ValueContainer<?>>
        extends AbstractValueContainer<S>, CompositeValueStore<S, H> {

    @SuppressWarnings("unchecked")
    default <E> DataTransactionResult offerWith(Key<? extends BaseValue<E>> key, E element, ValueProcessor<BaseValue<E>, E> processor) {
        return (DataTransactionResult) ((TriFunction) processor.getOfferHandler()).apply(key, this, element);
    }

    @Override
    default <E> DataTransactionResult offer(Key<? extends BaseValue<E>> key, E value, Cause cause) {
        return this.offer(key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    default <E> DataTransactionResult offer(Key<? extends BaseValue<E>> key, E element) {
        checkNotNull(key, "key");
        checkNotNull(element, "element");

        // Check the local key registration
        KeyRegistration<BaseValue<E>, E> localKeyRegistration = this.getKeyRegistration(key);
        if (localKeyRegistration == null) {
            if (this.requiresKeyRegistration()) {
                return DataTransactionResult.failNoData();
            }
        } else {
            List<ValueProcessor<BaseValue<E>, E>> processors = localKeyRegistration.getValueProcessors();
            if (!processors.isEmpty()) {
                return this.offerWith(key, element, processors.get(0));
            }
        }

        // Check the global key registrations
        KeyRegistration<BaseValue<E>, E> keyRegistration = LanternValueFactory.getInstance().getKeyRegistration(key);
        if (keyRegistration != null) {
            for (ValueProcessor<BaseValue<E>, E> valueProcessor : keyRegistration.getValueProcessors()) {
                if (valueProcessor.getApplicableTester().test((Key) key, this)) {
                    return this.offerWith(key, element, valueProcessor);
                }
            }
        }

        // Use the global processor
        if (localKeyRegistration != null && localKeyRegistration instanceof ElementHolder) {
            return this.offerWith(key, element, ValueProcessor.getDefaultAttachedValueProcessor());
        }

        // Check for the custom data manipulators
        List<DataManipulator<?, ?>> manipulators = this.getRawAdditionalManipulators();
        // Custom data is supported by this container
        if (manipulators != null) {
            for (DataManipulator<?, ?> dataManipulator : manipulators) {
                if (dataManipulator.supports(key)) {
                    ImmutableValue oldImmutableValue = ValueHelper.toImmutable((BaseValue) dataManipulator.getValue((Key) key).get());
                    dataManipulator.set(key, element);
                    ImmutableValue immutableValue = ValueHelper.toImmutable((BaseValue) dataManipulator.getValue((Key) key).get());
                    return DataTransactionResult.successReplaceResult(immutableValue, oldImmutableValue);
                }
            }
        }

        return DataTransactionResult.failNoData();
    }

    @SuppressWarnings("unchecked")
    default <E> DataTransactionResult offerWith(BaseValue<E> value, ValueProcessor<BaseValue<E>, E> processor) {
        return (DataTransactionResult) ((BiFunction) processor.getValueOfferHandler()).apply(this, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    default <E> DataTransactionResult offer(BaseValue<E> value) {
        checkNotNull(value, "value");
        Key<? extends BaseValue<E>> key = value.getKey();
        E element = value.get();

        // Check the local key registration
        KeyRegistration<BaseValue<E>, E> localKeyRegistration = this.getKeyRegistration(key);
        if (localKeyRegistration == null) {
            if (this.requiresKeyRegistration()) {
                return DataTransactionResult.failNoData();
            }
        } else {
            List<ValueProcessor<BaseValue<E>, E>> processors = localKeyRegistration.getValueProcessors();
            if (!processors.isEmpty()) {
                return this.offerWith(value, processors.get(0));
            }
        }

        // Check the global key registrations
        KeyRegistration<BaseValue<E>, E> keyRegistration = LanternValueFactory.getInstance().getKeyRegistration(key);
        if (keyRegistration != null) {
            for (ValueProcessor<BaseValue<E>, E> valueProcessor : keyRegistration.getValueProcessors()) {
                if (valueProcessor.getApplicableTester().test((Key) key, this)) {
                    return this.offerWith(value, valueProcessor);
                }
            }
        }

        // Use the global processor
        if (localKeyRegistration != null && localKeyRegistration instanceof ElementHolder) {
            return this.offerWith(value, ValueProcessor.getDefaultAttachedValueProcessor());
        }

        // Check for the custom data manipulators
        List<DataManipulator<?, ?>> manipulators = this.getRawAdditionalManipulators();
        // Custom data is supported by this container
        if (manipulators != null) {
            for (DataManipulator<?, ?> dataManipulator : manipulators) {
                if (dataManipulator.supports(key)) {
                    ImmutableValue oldImmutableValue = ValueHelper.toImmutable((BaseValue) dataManipulator.getValue((Key) key).get());
                    dataManipulator.set(key, element);
                    ImmutableValue immutableValue = ValueHelper.toImmutable(value);
                    return DataTransactionResult.successReplaceResult(immutableValue, oldImmutableValue);
                }
            }
        }

        return DataTransactionResult.failNoData();
    }

    @Override
    default DataTransactionResult offer(H valueContainer, MergeFunction function) {
        // TODO
        return DataTransactionResult.failNoData();
    }

    @SuppressWarnings("unchecked")
    default DataTransactionResult removeWith(Key<?> key, ValueProcessor<?, ?> processor) {
        return (DataTransactionResult) ((BiFunction) processor.getRemoveHandler()).apply(key, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    default DataTransactionResult remove(Key<?> key) {
        checkNotNull(key, "key");

        // Check the local key registration
        KeyRegistration<?, ?> localKeyRegistration = this.getKeyRegistration((Key) key);
        if (localKeyRegistration == null) {
            if (this.requiresKeyRegistration()) {
                return DataTransactionResult.failNoData();
            }
        } else {
            List<? extends ValueProcessor<? extends BaseValue<?>, ?>> processors = localKeyRegistration.getValueProcessors();
            if (!processors.isEmpty()) {
                return this.removeWith(key, processors.get(0));
            }
        }

        // Check the global key registrations
        KeyRegistration<?, ?> keyRegistration = LanternValueFactory.getInstance().getKeyRegistration((Key) key);
        if (keyRegistration != null) {
            for (ValueProcessor<?, ?> valueProcessor : keyRegistration.getValueProcessors()) {
                if (valueProcessor.getApplicableTester().test((Key) key, this)) {
                    return this.removeWith(key, valueProcessor);
                }
            }
        }

        // Use the global processor
        if (localKeyRegistration != null && localKeyRegistration instanceof ElementHolder) {
            return this.removeWith(key, ValueProcessor.getDefaultAttachedValueProcessor());
        }

        // Custom data container doesn't support their data to be removed

        return DataTransactionResult.failNoData();
    }

    @Override
    default DataTransactionResult undo(DataTransactionResult result) {
        if (result.getReplacedData().isEmpty() && result.getSuccessfulData().isEmpty()) {
            return DataTransactionResult.successNoData();
        }
        DataTransactionResult.Builder builder = DataTransactionResult.builder();
        for (ImmutableValue<?> replaced : result.getReplacedData()) {
            builder.absorbResult(this.offer(replaced));
        }
        for (ImmutableValue<?> successful : result.getSuccessfulData()) {
            builder.absorbResult(this.remove(successful));
        }
        return builder.build();
    }
}
