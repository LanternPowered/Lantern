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
package org.lanternpowered.server.data.value.mutable;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.data.manipulator.DataManipulatorRegistration;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistry;
import org.lanternpowered.server.data.manipulator.IDataManipulatorBase;
import org.lanternpowered.server.data.manipulator.immutable.IImmutableDataManipulator;
import org.lanternpowered.server.data.value.AbstractValueContainer;
import org.lanternpowered.server.data.value.ElementHolder;
import org.lanternpowered.server.data.value.KeyRegistration;
import org.lanternpowered.server.data.value.LanternValueFactory;
import org.lanternpowered.server.data.value.ValueHelper;
import org.lanternpowered.server.data.value.processor.ValueProcessor;
import org.lanternpowered.server.util.functions.TriFunction;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.CompositeValueStore;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.event.cause.Cause;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public interface AbstractCompositeValueStore<S extends CompositeValueStore<S, H>, H extends ValueContainer<?>>
        extends AbstractValueContainer<S, H>, CompositeValueStore<S, H> {

    @SuppressWarnings("unchecked")
    default <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> Optional<M> createManipulator(
            DataManipulatorRegistration<M, I> registration) {
        final M manipulator = registration.createMutable();
        for (Key<?> key : registration.getRequiredKeys()) {
            final Optional value = getValue((Key) key);
            if (value.isPresent()) {
                manipulator.set((Value) value.get());
            } else if (!supports(key)) {
                return Optional.empty();
            }
        }
        return Optional.of(manipulator);
    }

    @SuppressWarnings("unchecked")
    default <E> DataTransactionResult offerWith(Key<? extends BaseValue<E>> key, E element, ValueProcessor<BaseValue<E>, E> processor) {
        return (DataTransactionResult) ((TriFunction) processor.getOfferHandler()).apply(key, this, element);
    }

    @Override
    default <E> DataTransactionResult offer(Key<? extends BaseValue<E>> key, E value, Cause cause) {
        return offer(key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    default <E> DataTransactionResult offer(Key<? extends BaseValue<E>> key, E element) {
        checkNotNull(key, "key");
        checkNotNull(element, "element");

        // Check the local key registration
        final KeyRegistration<BaseValue<E>, E> localKeyRegistration = getKeyRegistration(key);
        if (localKeyRegistration == null) {
            if (requiresKeyRegistration()) {
                return DataTransactionResult.failNoData();
            }
        } else {
            final List<ValueProcessor<BaseValue<E>, E>> processors = localKeyRegistration.getValueProcessors();
            if (!processors.isEmpty()) {
                return offerWith(key, element, processors.get(0));
            }
        }

        // Check the global key registrations
        final KeyRegistration<BaseValue<E>, E> keyRegistration = LanternValueFactory.getInstance().getKeyRegistration(key);
        if (keyRegistration != null) {
            for (ValueProcessor<BaseValue<E>, E> valueProcessor : keyRegistration.getValueProcessors()) {
                if (valueProcessor.getApplicableTester().test((Key) key, this)) {
                    return offerWith(key, element, valueProcessor);
                }
            }
        }

        // Use the global processor
        if (localKeyRegistration != null && localKeyRegistration instanceof ElementHolder) {
            return offerWith(key, element, ValueProcessor.getDefaultAttachedValueProcessor());
        }

        // Check for the custom data manipulators
        final Map<Class<?>, H> valueContainers = getRawAdditionalContainers();
        // Custom data is supported by this container
        if (valueContainers != null) {
            for (H valueContainer : valueContainers.values()) {
                if (valueContainer.supports(key)) {
                    if (valueContainer instanceof CompositeValueStore) {
                        return ((CompositeValueStore) valueContainer).offer(key, element);
                    } else if (valueContainer instanceof DataManipulator) {
                        final ImmutableValue oldImmutableValue = ValueHelper.toImmutable((BaseValue) valueContainer.getValue((Key) key).get());
                        ((DataManipulator) valueContainer).set(key, element);
                        final ImmutableValue immutableValue = ValueHelper.toImmutable((BaseValue) valueContainer.getValue((Key) key).get());
                        return DataTransactionResult.successReplaceResult(immutableValue, oldImmutableValue);
                    } else {
                        final ImmutableValue immutableValue = ValueHelper.toImmutable((BaseValue) valueContainer.getValue((Key) key).get());
                        return DataTransactionResult.failResult(immutableValue);
                    }
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
        final Key<? extends BaseValue<E>> key = value.getKey();
        final E element = value.get();

        // Check the local key registration
        final KeyRegistration<BaseValue<E>, E> localKeyRegistration = getKeyRegistration(key);
        if (localKeyRegistration == null) {
            if (requiresKeyRegistration()) {
                return DataTransactionResult.failNoData();
            }
        } else {
            final List<ValueProcessor<BaseValue<E>, E>> processors = localKeyRegistration.getValueProcessors();
            if (!processors.isEmpty()) {
                return offerWith(value, processors.get(0));
            }
        }

        // Check the global key registrations
        final KeyRegistration<BaseValue<E>, E> keyRegistration = LanternValueFactory.getInstance().getKeyRegistration(key);
        if (keyRegistration != null) {
            for (ValueProcessor<BaseValue<E>, E> valueProcessor : keyRegistration.getValueProcessors()) {
                if (valueProcessor.getApplicableTester().test((Key) key, this)) {
                    return offerWith(value, valueProcessor);
                }
            }
        }

        // Use the global processor
        if (localKeyRegistration != null && localKeyRegistration instanceof ElementHolder) {
            return offerWith(value, ValueProcessor.getDefaultAttachedValueProcessor());
        }

        // Check for the custom data manipulators
        final Map<Class<?>, H> valueContainers = getRawAdditionalContainers();
        // Custom data is supported by this container
        if (valueContainers != null) {
            for (H valueContainer : valueContainers.values()) {
                if (valueContainer.supports(key)) {
                    final ImmutableValue oldImmutableValue = ValueHelper.toImmutable((BaseValue) valueContainer.getValue((Key) key).get());
                    if (valueContainer instanceof CompositeValueStore) {
                        return ((CompositeValueStore) valueContainer).offer(key, element);
                    } else if (valueContainer instanceof DataManipulator) {
                        ((DataManipulator) valueContainer).set(key, element);
                        final ImmutableValue immutableValue = ValueHelper.toImmutable(value);
                        return DataTransactionResult.successReplaceResult(immutableValue, oldImmutableValue);
                    } else {
                        return DataTransactionResult.failResult(ValueHelper.toImmutable(value));
                    }
                }
            }
        }

        return DataTransactionResult.failNoData();
    }

    @Override
    default DataTransactionResult remove(Class<? extends H> containerClass) {
        checkNotNull(containerClass, "containerClass");
        // You cannot remove default data manipulators?
        final Optional optRegistration = DataManipulatorRegistry.get().getBy((Class) containerClass);
        if (optRegistration.isPresent()) {
            return DataTransactionResult.failNoData();
        }

        final Map<Class<?>, H> containers = getRawAdditionalContainers();
        if (containers != null) {
            final H old = containers.remove(containerClass);
            if (old != null) {
                return DataTransactionResult.successRemove(old.getValues());
            }
        }

        return DataTransactionResult.failNoData();
    }

    @SuppressWarnings("unchecked")
    @Override
    default boolean supports(Class<? extends H> containerClass) {
        checkNotNull(containerClass, "containerClass");

        // Offer all the default key values as long if they are supported
        final Optional<DataManipulatorRegistration> optRegistration = DataManipulatorRegistry.get().getBy(containerClass);
        if (optRegistration.isPresent()) {
            final DataManipulatorRegistration registration = optRegistration.get();
            for (Key key : (Set<Key>) registration.getRequiredKeys()) {
                if (!supports(key)) {
                       return false;
                }
            }
            return true;
        }

        // Support all the additional manipulators
        return getRawAdditionalContainers() != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    default <T extends H> Optional<T> getOrCreate(Class<T> containerClass) {
        return get(containerClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    default <T extends H> Optional<T> get(Class<T> containerClass) {
        checkNotNull(containerClass, "containerClass");

        // Check default registrations
        final Optional<DataManipulatorRegistration> optRegistration = DataManipulatorRegistry.get().getBy(containerClass);
        if (optRegistration.isPresent()) {
            final DataManipulator manipulator = (DataManipulator) createManipulator(optRegistration.get()).orElse(null);
            return manipulator == null ? Optional.empty() : Optional.of(
                    (T) (IImmutableDataManipulator.class.isAssignableFrom(containerClass) ? manipulator.asImmutable() : manipulator));
        }

        // Try the additional containers if they are supported
        final Map<Class<?>, H> containers = getRawAdditionalContainers();
        if (containers != null) {
            for (H container : containers.values()) {
                if (containerClass.isInstance(containers)) {
                    return Optional.of((T) (container instanceof DataManipulator ?
                            ((DataManipulator) container).copy() : container));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    default DataTransactionResult offer(H valueContainer, MergeFunction function, Cause cause) {
        return offer(valueContainer, function);
    }

    @SuppressWarnings("unchecked")
    @Override
    default DataTransactionResult offer(H valueContainer, MergeFunction function) {
        if (valueContainer instanceof IDataManipulatorBase) {
            // Offer all the default key values as long if they are supported
            final Optional<DataManipulatorRegistration> optRegistration =
                    DataManipulatorRegistry.get().getByMutable(((IDataManipulatorBase) valueContainer).getMutableType());
            if (optRegistration.isPresent()) {
                if (function == MergeFunction.FORCE_NOTHING) {
                    final DataTransactionResult.Builder builder = DataTransactionResult.builder();
                    for (ImmutableValue value : valueContainer.getValues()) {
                        getValue(value.getKey()).ifPresent(value1 -> builder.replace(((Value) value1).asImmutable()));
                    }
                    return builder.result(DataTransactionResult.Type.SUCCESS).build();
                } else if (function != MergeFunction.IGNORE_ALL) {
                    ValueContainer old = (ValueContainer) createManipulator(optRegistration.get()).orElse(null);
                    if (valueContainer instanceof IImmutableDataManipulator) {
                        old = ((DataManipulator) old).asImmutable();
                    }
                    valueContainer = (H) function.merge(old, valueContainer);
                }
                final DataTransactionResult.Builder builder = DataTransactionResult.builder();
                boolean success = false;
                for (ImmutableValue value : valueContainer.getValues()) {
                    final DataTransactionResult result = offer(value);
                    if (result.isSuccessful()) {
                        builder.success(value);
                        builder.replace(result.getReplacedData());
                        success = true;
                    } else {
                        builder.reject(value);
                    }
                }
                if (success) {
                    builder.result(DataTransactionResult.Type.SUCCESS);
                } else {
                    builder.result(DataTransactionResult.Type.FAILURE);
                }
                return builder.build();
            }
        }
        if (valueContainer instanceof DataManipulator ||
                valueContainer instanceof ImmutableDataManipulator) {
            final Map<Class<?>, H> manipulators = getRawAdditionalContainers();
            if (manipulators != null) {
                final Class<?> key = valueContainer.getClass();
                final H old = manipulators.get(key);
                final H merged = function.merge(old, valueContainer);

                final DataTransactionResult.Builder builder = DataTransactionResult.builder().result(DataTransactionResult.Type.SUCCESS);
                builder.success(merged.getValues());
                if (old != null) {
                    builder.replace(old.getValues());
                }
                return builder.build();
            }
        }
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
        final KeyRegistration<?, ?> localKeyRegistration = getKeyRegistration((Key) key);
        if (localKeyRegistration == null) {
            if (requiresKeyRegistration()) {
                return DataTransactionResult.failNoData();
            }
        } else {
            final List<? extends ValueProcessor<? extends BaseValue<?>, ?>> processors = localKeyRegistration.getValueProcessors();
            if (!processors.isEmpty()) {
                return removeWith(key, processors.get(0));
            }
        }

        // Check the global key registrations
        final KeyRegistration<?, ?> keyRegistration = LanternValueFactory.getInstance().getKeyRegistration((Key) key);
        if (keyRegistration != null) {
            for (ValueProcessor<?, ?> valueProcessor : keyRegistration.getValueProcessors()) {
                if (valueProcessor.getApplicableTester().test((Key) key, this)) {
                    return removeWith(key, valueProcessor);
                }
            }
        }

        // Use the global processor
        if (localKeyRegistration != null && localKeyRegistration instanceof ElementHolder) {
            return removeWith(key, ValueProcessor.getDefaultAttachedValueProcessor());
        }

        // Custom data container doesn't support their data to be removed

        return DataTransactionResult.failNoData();
    }

    @Override
    default DataTransactionResult undo(DataTransactionResult result) {
        if (result.getReplacedData().isEmpty() && result.getSuccessfulData().isEmpty()) {
            return DataTransactionResult.successNoData();
        }
        final DataTransactionResult.Builder builder = DataTransactionResult.builder();
        for (ImmutableValue<?> replaced : result.getReplacedData()) {
            builder.absorbResult(offer(replaced));
        }
        for (ImmutableValue<?> successful : result.getSuccessfulData()) {
            builder.absorbResult(remove(successful));
        }
        return builder.build();
    }
}
