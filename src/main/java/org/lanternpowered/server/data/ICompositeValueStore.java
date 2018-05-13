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
package org.lanternpowered.server.data;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.data.key.LanternKey;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistration;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistry;
import org.lanternpowered.server.data.manipulator.IDataManipulatorBase;
import org.lanternpowered.server.data.manipulator.immutable.IImmutableDataManipulator;
import org.lanternpowered.server.data.processor.Processor;
import org.lanternpowered.server.data.processor.ValueProcessorKeyRegistration;
import org.lanternpowered.server.data.value.LanternValueFactory;
import org.lanternpowered.server.data.value.ValueHelper;
import org.lanternpowered.server.util.copy.Copyable;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.CompositeValueStore;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public interface ICompositeValueStore<S extends CompositeValueStore<S, H>, H extends ValueContainer<?>>
        extends IValueContainer<S>, CompositeValueStore<S, H> {

    /**
     * A fast equivalent of {@link #transform(Key, Function)} which
     * avoids the construction of {@link DataTransactionResult}s.
     *
     * @param key The key
     * @param function The function
     * @param <E> The element type
     * @return Whether the offer was successful
     */
    default <E> boolean transformFast(Key<? extends BaseValue<E>> key, Function<E, E> function) {
        return supports(key) && offerFast(key, checkNotNull(function.apply(get(key).orElse(null))));
    }

    /**
     * A fast equivalent of {@link #offer(Key, Object)} which
     * avoids the construction of {@link DataTransactionResult}s.
     *
     * @param key The key
     * @param element The element
     * @param <E> The element type
     * @return Whether the offer was successful
     */
    default <E> boolean offerFast(Key<? extends BaseValue<E>> key, E element) {
        return CompositeValueStoreHelper.offerFast(this, key, element);
    }

    default <E> boolean offerFastNoEvents(Key<? extends BaseValue<E>> key, E element) {
        // Optional unwrapped key handling
        final LanternKey optionalWrappedKey = ((LanternKey) key).getOptionalWrappedKey();
        if (optionalWrappedKey != null) {
            return offerFastNoEvents(optionalWrappedKey, Optional.of(element));
        }

        // Check the local key registration
        final KeyRegistration<?, ?> localKeyRegistration = (KeyRegistration<?, ?>) getValueCollection().get((Key) key).orElse(null);
        if (localKeyRegistration != null) {
            return ((Processor<BaseValue<E>, E>) localKeyRegistration).offerFastTo(this, element);
        }

        // Check for a global registration
        final Optional<ValueProcessorKeyRegistration> globalRegistration = LanternValueFactory.get().getKeyRegistration((Key) key);
        if (globalRegistration.isPresent()) {
            return ((Processor<BaseValue<E>, E>) globalRegistration.get()).offerFastTo(this, element);
        }

        // Check if custom data is supported by this container
        if (this instanceof AdditionalContainerHolder) {
            // Check for the custom value containers
            final AdditionalContainerCollection<H> containers = ((AdditionalContainerHolder<H>) this).getAdditionalContainers();
            for (H valueContainer : containers.getAll()) {
                if (valueContainer.supports(key)) {
                    if (valueContainer instanceof ICompositeValueStore) {
                        return ((ICompositeValueStore) valueContainer).offerFastNoEvents(key, element);
                    } else if (valueContainer instanceof CompositeValueStore) {
                        return ((CompositeValueStore) valueContainer).offer(key, element).isSuccessful();
                    } else if (valueContainer instanceof DataManipulator) {
                        ((DataManipulator) valueContainer).set(key, element);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

        return false;
    }

    @Override
    default <E> DataTransactionResult offer(Key<? extends BaseValue<E>> key, E element) {
        return CompositeValueStoreHelper.offer(this, key, element);
    }

    default <E> DataTransactionResult offerNoEvents(Key<? extends BaseValue<E>> key, E element) {
        // Optional unwrapped key handling
        final LanternKey optionalWrappedKey = ((LanternKey) key).getOptionalWrappedKey();
        if (optionalWrappedKey != null) {
            return offerNoEvents(optionalWrappedKey, Optional.of(element));
        }

        // Check the local key registration
        final KeyRegistration<?, ?> localKeyRegistration = (KeyRegistration<?, ?>) getValueCollection().get((Key) key).orElse(null);
        if (localKeyRegistration != null) {
            return ((Processor<BaseValue<E>, E>) localKeyRegistration).offerTo(this, element);
        }

        // Check for a global registration
        final Optional<ValueProcessorKeyRegistration> globalRegistration = LanternValueFactory.get().getKeyRegistration((Key) key);
        if (globalRegistration.isPresent()) {
            return ((Processor<BaseValue<E>, E>) globalRegistration.get()).offerTo(this, element);
        }

        // Check if custom data is supported by this container
        if (this instanceof AdditionalContainerHolder) {
            // Check for the custom value containers
            final AdditionalContainerCollection<H> containers = ((AdditionalContainerHolder<H>) this).getAdditionalContainers();
            for (H valueContainer : containers.getAll()) {
                if (valueContainer.supports(key)) {
                    if (valueContainer instanceof ICompositeValueStore) {
                        return ((ICompositeValueStore) valueContainer).offerNoEvents(key, element);
                    } else if (valueContainer instanceof CompositeValueStore) {
                        return ((CompositeValueStore) valueContainer).offer(key, element);
                    } else if (valueContainer instanceof DataManipulator) {
                        final ImmutableValue oldImmutableValue = (ImmutableValue) valueContainer.getValue((Key) key)
                                .map(value -> ValueHelper.toImmutable((BaseValue) value))
                                .orElse(null);
                        ((DataManipulator) valueContainer).set(key, element);
                        final ImmutableValue immutableValue = (ImmutableValue) valueContainer.getValue((Key) key)
                                .map(value -> ValueHelper.toImmutable((BaseValue) value))
                                .orElse(null);
                        if (oldImmutableValue == null && immutableValue == null) {
                            return DataTransactionResult.successNoData();
                        } else if (oldImmutableValue == null) {
                            return DataTransactionResult.successResult(immutableValue);
                        } else if (immutableValue == null) {
                            return DataTransactionResult.successRemove(oldImmutableValue);
                        } else {
                            return DataTransactionResult.successReplaceResult(immutableValue, oldImmutableValue);
                        }
                    } else {
                        // TODO: Support immutable manipulators?
                        return DataTransactionResult.failNoData();
                    }
                }
            }
        }

        return DataTransactionResult.failNoData();
    }

    /**
     * A fast equivalent of {@link #offer(BaseValue)} which
     * avoids the construction of {@link DataTransactionResult}s.
     *
     * @param value The value
     * @param <E> The element type
     * @return Whether the offer was successful
     */
    default <E> boolean offerFast(BaseValue<E> value) {
        return CompositeValueStoreHelper.offerFast(this, value);
    }

    default <E> boolean offerFastNoEvents(BaseValue<E> value) {
        checkNotNull(value, "value");
        final Key<? extends BaseValue<E>> key = value.getKey();

        // Optional unwrapped key handling
        final LanternKey optionalWrappedKey = ((LanternKey) key).getOptionalWrappedKey();
        if (optionalWrappedKey != null) {
            return offerFastNoEvents(optionalWrappedKey, value.getDirect());
        }

        // Check the local key registration
        final KeyRegistration<?, ?> localKeyRegistration = (KeyRegistration<?, ?>) getValueCollection().get((Key) key).orElse(null);
        if (localKeyRegistration != null) {
            return ((Processor<BaseValue<E>, E>) localKeyRegistration).offerFastTo(this, value);
        }

        // Check for a global registration
        final Optional<ValueProcessorKeyRegistration> globalRegistration = LanternValueFactory.get().getKeyRegistration((Key) key);
        if (globalRegistration.isPresent()) {
            return ((Processor<BaseValue<E>, E>) globalRegistration.get()).offerFastTo(this, value);
        }

        // Check if custom data is supported by this container
        if (this instanceof AdditionalContainerHolder) {
            // Check for the custom value containers
            final AdditionalContainerCollection<H> containers = ((AdditionalContainerHolder<H>) this).getAdditionalContainers();
            for (H valueContainer : containers.getAll()) {
                if (valueContainer.supports(key)) {
                    if (valueContainer instanceof ICompositeValueStore) {
                        return ((ICompositeValueStore) valueContainer).offerFastNoEvents(value);
                    } else if (valueContainer instanceof CompositeValueStore) {
                        return ((CompositeValueStore) valueContainer).offer(value).isSuccessful();
                    } else if (valueContainer instanceof DataManipulator) {
                        ((DataManipulator) valueContainer).set(value);
                        return true;
                    } else {
                        // TODO: Support immutable manipulators?
                        return false;
                    }
                }
            }
        }

        return false;
    }

    @Override
    default <E> DataTransactionResult offer(BaseValue<E> value) {
        return CompositeValueStoreHelper.offer(this, value);
    }

    default <E> DataTransactionResult offerNoEvents(BaseValue<E> value) {
        checkNotNull(value, "value");
        final Key<? extends BaseValue<E>> key = value.getKey();

        // Optional unwrapped key handling
        final LanternKey optionalWrappedKey = ((LanternKey) key).getOptionalWrappedKey();
        if (optionalWrappedKey != null) {
            return offerNoEvents(optionalWrappedKey, value.getDirect());
        }

        // Check the local key registration
        final KeyRegistration<?, ?> localKeyRegistration = (KeyRegistration<?, ?>) getValueCollection().get((Key) key).orElse(null);
        if (localKeyRegistration != null) {
            return ((Processor<BaseValue<E>, E>) localKeyRegistration).offerTo(this, value);
        }

        // Check for a global registration
        final Optional<ValueProcessorKeyRegistration> globalRegistration = LanternValueFactory.get().getKeyRegistration((Key) key);
        if (globalRegistration.isPresent()) {
            return ((Processor<BaseValue<E>, E>) globalRegistration.get()).offerTo(this, value);
        }

        // Check if custom data is supported by this container
        if (this instanceof AdditionalContainerHolder) {
            // Check for the custom value containers
            final AdditionalContainerCollection<H> containers = ((AdditionalContainerHolder<H>) this).getAdditionalContainers();
            for (H valueContainer : containers.getAll()) {
                if (valueContainer.supports(key)) {
                    if (valueContainer instanceof CompositeValueStore) {
                        return ((CompositeValueStore) valueContainer).offer(value);
                    } else if (valueContainer instanceof DataManipulator) {
                        final ImmutableValue oldImmutableValue = (ImmutableValue) valueContainer.getValue((Key) key)
                                .map(value1 -> ValueHelper.toImmutable((BaseValue) value1))
                                .orElse(null);
                        ((DataManipulator) valueContainer).set(value);
                        final ImmutableValue immutableValue = ValueHelper.toImmutable((BaseValue) value);
                        if (oldImmutableValue == null) {
                            return DataTransactionResult.successResult(immutableValue);
                        } else {
                            return DataTransactionResult.successReplaceResult(immutableValue, oldImmutableValue);
                        }
                    } else {
                        // TODO: Support immutable manipulators?
                        return DataTransactionResult.failNoData();
                    }
                }
            }
        }

        return DataTransactionResult.failNoData();
    }

    /**
     * A fast equivalent of {@link #tryOffer(Key, Object)} which
     * avoids the construction of {@link DataTransactionResult}s.
     *
     * @param key The key
     * @param value The value
     * @return Whether the offer was successful
     */
    default <E> boolean tryOfferFast(Key<? extends BaseValue<E>> key, E value) throws IllegalArgumentException {
        final boolean result = offerFast(key, value);
        if (!result) {
            throw new IllegalArgumentException("Failed offer transaction!");
        }
        return true;
    }

    /**
     * A fast equivalent of {@link #tryOffer(BaseValue)} which
     * avoids the construction of {@link DataTransactionResult}s.
     *
     * @param value The value
     * @return Whether the offer was successful
     */
    default <E> boolean tryOfferFast(BaseValue<E> value) throws IllegalArgumentException {
        return tryOfferFast(value.getKey(), value.get());
    }

    /**
     * A fast equivalent of {@link #remove(Key)} which
     * avoids the construction of {@link DataTransactionResult}s.
     *
     * @param key The key
     * @return Whether the removal was successful
     */
    default boolean removeFast(Key<?> key) {
        return CompositeValueStoreHelper.removeFast(this, key);
    }

    default boolean removeFastNoEvents(Key<?> key) {
        checkNotNull(key, "key");

        // Optional unwrapped key handling
        final LanternKey optionalWrappedKey = ((LanternKey) key).getOptionalWrappedKey();
        if (optionalWrappedKey != null) {
            return removeFastNoEvents(optionalWrappedKey);
        }

        // Check the local key registration
        final KeyRegistration<?, ?> localKeyRegistration = (KeyRegistration<?, ?>) getValueCollection().get((Key) key).orElse(null);
        if (localKeyRegistration != null) {
            return ((Processor<BaseValue<?>, ?>) localKeyRegistration).removeFastFrom(this);
        }

        // Check for a global registration
        final Optional<ValueProcessorKeyRegistration> globalRegistration = LanternValueFactory.get().getKeyRegistration((Key) key);
        if (globalRegistration.isPresent()) {
            return ((Processor<BaseValue<?>, ?>) globalRegistration.get()).removeFastFrom(this);
        }

        // Check if custom data is supported by this container
        if (this instanceof AdditionalContainerHolder) {
            // Check for the custom value containers
            final AdditionalContainerCollection<H> containers = ((AdditionalContainerHolder<H>) this).getAdditionalContainers();
            for (H valueContainer : containers.getAll()) {
                if (valueContainer.supports(key)) {
                    if (valueContainer instanceof ICompositeValueStore) {
                        return ((ICompositeValueStore) valueContainer).removeFastNoEvents(key);
                    } else if (valueContainer instanceof CompositeValueStore) {
                        return ((CompositeValueStore) valueContainer).remove(key).isSuccessful();
                    } else if (valueContainer instanceof DataManipulator ||
                            valueContainer instanceof ImmutableDataManipulator) {
                        return false;
                    }
                    return false;
                }
            }
        }

        return false;
    }

    @Override
    default DataTransactionResult remove(Key<?> key) {
        return CompositeValueStoreHelper.remove(this, key);
    }

    default DataTransactionResult removeNoEvents(Key<?> key) {
        checkNotNull(key, "key");

        // Optional unwrapped key handling
        final LanternKey optionalWrappedKey = ((LanternKey) key).getOptionalWrappedKey();
        if (optionalWrappedKey != null) {
            return removeNoEvents(optionalWrappedKey);
        }

        // Check the local key registration
        final KeyRegistration<?, ?> localKeyRegistration = (KeyRegistration<?, ?>) getValueCollection().get((Key) key).orElse(null);
        if (localKeyRegistration != null) {
            return ((Processor<BaseValue<?>, ?>) localKeyRegistration).removeFrom(this);
        }

        // Check for a global registration
        final Optional<ValueProcessorKeyRegistration> globalRegistration = LanternValueFactory.get().getKeyRegistration((Key) key);
        if (globalRegistration.isPresent()) {
            return ((Processor<BaseValue<?>, ?>) globalRegistration.get()).removeFrom(this);
        }

        // Check if custom data is supported by this container
        if (this instanceof AdditionalContainerHolder) {
            // Check for the custom value containers
            final AdditionalContainerCollection<H> containers = ((AdditionalContainerHolder<H>) this).getAdditionalContainers();
            for (H valueContainer : containers.getAll()) {
                if (valueContainer.supports(key)) {
                    if (valueContainer instanceof ICompositeValueStore) {
                        return ((ICompositeValueStore) valueContainer).removeNoEvents(key);
                    } else if (valueContainer instanceof CompositeValueStore) {
                        return ((CompositeValueStore) valueContainer).remove(key);
                    }
                    return DataTransactionResult.failNoData();
                }
            }
        }

        return DataTransactionResult.failNoData();
    }

    /**
     * A fast equivalent of {@link #remove(BaseValue)} which
     * avoids the construction of {@link DataTransactionResult}s.
     *
     * @param value The value
     * @return Whether the removal was successful
     */
    default boolean removeFast(BaseValue<?> value) {
        return removeFast(value.getKey());
    }

    /**
     * A fast equivalent of {@link #undo(DataTransactionResult)} which
     * avoids the construction of {@link DataTransactionResult}s.
     *
     * @param result The result
     * @return Whether the removal was successful
     */
    default boolean undoFast(DataTransactionResult result) {
        return CompositeValueStoreHelper.undoFast(this, result);
    }

    default boolean undoFastNoEvents(DataTransactionResult result) {
        if (result.getReplacedData().isEmpty() &&
                result.getSuccessfulData().isEmpty()) {
            return true;
        }
        result.getSuccessfulData().forEach(value -> removeFastNoEvents(value.getKey()));
        result.getReplacedData().forEach(value -> offerFastNoEvents((Key) value.getKey(), value.get()));
        return true;
    }

    @Override
    default DataTransactionResult undo(DataTransactionResult result) {
        return CompositeValueStoreHelper.undo(this, result);
    }

    default DataTransactionResult undoNoEvents(DataTransactionResult result) {
        if (result.getReplacedData().isEmpty() &&
                result.getSuccessfulData().isEmpty()) {
            return DataTransactionResult.successNoData();
        }
        final DataTransactionResult.Builder builder = DataTransactionResult.builder();
        for (ImmutableValue<?> replaced : result.getReplacedData()) {
            builder.absorbResult(offerNoEvents(replaced));
        }
        for (ImmutableValue<?> successful : result.getSuccessfulData()) {
            builder.absorbResult(removeNoEvents(successful.getKey()));
        }
        return builder.result(DataTransactionResult.Type.SUCCESS).build();
    }

    /**
     * A fast equivalent of {@link #offer(ValueContainer)} which
     * avoids the construction of {@link DataTransactionResult}s.
     *
     * @param valueContainer The value container
     * @return Whether the offer was successful
     */
    default boolean offerFast(H valueContainer) {
        return offerFast(valueContainer, MergeFunction.IGNORE_ALL);
    }

    /**
     * A fast equivalent of {@link #offer(ValueContainer, MergeFunction)} which
     * avoids the construction of {@link DataTransactionResult}s.
     *
     * @param valueContainer The value container
     * @param function The merge function
     * @return Whether the removal was successful
     */
    default boolean offerFast(H valueContainer, MergeFunction function) {
        return CompositeValueStoreHelper.offerFast(this, valueContainer, function);
    }

    default boolean offerFastNoEvents(H valueContainer, MergeFunction function) {
        if (valueContainer instanceof IDataManipulatorBase) {
            // Offer all the default key values as long if they are supported
            final Optional<DataManipulatorRegistration> optRegistration =
                    DataManipulatorRegistry.get().getByMutable(((IDataManipulatorBase) valueContainer).getMutableType());
            if (optRegistration.isPresent()) {
                if (function == MergeFunction.FORCE_NOTHING) {
                    return true;
                } else if (function != MergeFunction.IGNORE_ALL) {
                    ValueContainer old = DataHelper.create(this, optRegistration.get());
                    if (valueContainer instanceof IImmutableDataManipulator && old != null) {
                        old = ((DataManipulator) old).asImmutable();
                    }
                    valueContainer = (H) function.merge(old, valueContainer);
                }
                boolean success = false;
                for (ImmutableValue value : valueContainer.getValues()) {
                    if (offerFast(value)) {
                        success = true;
                        break;
                    }
                }
                return success;
            }
        }
        if (this instanceof AdditionalContainerHolder) {
            final AdditionalContainerCollection<H> containers =
                    ((AdditionalContainerHolder<H>) this).getAdditionalContainers();
            final Class key = valueContainer.getClass();
            final H old = (H) containers.get(key).orElse(null);
            final H merged = function.merge(old, valueContainer);
            containers.offer(merged);
            return true;
        }
        return false;
    }

    @Override
    default DataTransactionResult offer(H valueContainer, MergeFunction function) {
        return CompositeValueStoreHelper.offer(this, valueContainer, function);
    }

    default DataTransactionResult offerNoEvents(H valueContainer) {
        return offerNoEvents(valueContainer, MergeFunction.IGNORE_ALL);
    }

    default DataTransactionResult offerNoEvents(H valueContainer, MergeFunction function) {
        if (valueContainer instanceof IDataManipulatorBase) {
            // Offer all the default key values as long if they are supported
            final Optional<DataManipulatorRegistration> optRegistration =
                    DataManipulatorRegistry.get().getByMutable(((IDataManipulatorBase) valueContainer).getMutableType());
            if (optRegistration.isPresent()) {
                if (function != MergeFunction.IGNORE_ALL) {
                    ValueContainer old = DataHelper.create(this, optRegistration.get());
                    if (valueContainer instanceof IImmutableDataManipulator && old != null) {
                        old = ((DataManipulator) old).asImmutable();
                    }
                    valueContainer = (H) function.merge(old, valueContainer);
                }
                final DataTransactionResult.Builder builder = DataTransactionResult.builder();
                boolean success = false;
                for (ImmutableValue value : valueContainer.getValues()) {
                    final DataTransactionResult result = offerNoEvents(value);
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
        if (this instanceof AdditionalContainerHolder) {
            final AdditionalContainerCollection<H> containers =
                    ((AdditionalContainerHolder<H>) this).getAdditionalContainers();
            final Class key = valueContainer.getClass();
            final H old = (H) containers.get(key).orElse(null);
            final H merged = function.merge(old, valueContainer);
            containers.offer(merged);

            final DataTransactionResult.Builder builder = DataTransactionResult.builder().result(DataTransactionResult.Type.SUCCESS);
            builder.success(merged.getValues());
            if (old != null) {
                builder.replace(old.getValues());
            }
            return builder.build();
        }
        return DataTransactionResult.failNoData();
    }

    /**
     * A fast equivalent of {@link #offer(Iterable)} which
     * avoids the construction of {@link DataTransactionResult}s.
     *
     * @param valueContainers The value containers
     * @return Whether the offer was successful
     */
    default boolean offerFast(Iterable<H> valueContainers) {
        return offerFast(valueContainers, MergeFunction.IGNORE_ALL);
    }

    /**
     * A fast equivalent of {@link #offer(Iterable, MergeFunction)} which
     * avoids the construction of {@link DataTransactionResult}s.
     *
     * @param valueContainers The value containers
     * @param function The merge function
     * @return Whether the offer was successful
     */
    default boolean offerFast(Iterable<H> valueContainers, MergeFunction function) {
        return CompositeValueStoreHelper.offerFast(this, valueContainers, function);
    }

    default boolean offerFastNoEvents(Iterable<H> valueContainers, MergeFunction function) {
        boolean success = false;
        for (H valueContainer : valueContainers) {
            if (offerFastNoEvents(valueContainer, function)) {
                success = true;
            }
        }
        return success;
    }

    @Override
    default DataTransactionResult offer(Iterable<H> valueContainers, MergeFunction function) {
        return CompositeValueStoreHelper.offer(this, valueContainers, function);
    }

    default DataTransactionResult offerNoEvents(Iterable<H> valueContainers, MergeFunction function) {
        final DataTransactionResult.Builder builder = DataTransactionResult.builder();
        for (H valueContainer : valueContainers) {
            builder.absorbResult(offerNoEvents(valueContainer, function));
        }
        return builder.build();
    }

    /**
     * A fast equivalent of {@link #tryOffer(ValueContainer)} which
     * avoids the construction of {@link DataTransactionResult}s.
     *
     * @param valueContainer The value container
     * @return Whether the offer was successful
     */
    default boolean tryOfferFast(H valueContainer) throws IllegalArgumentException {
        final boolean result = offerFast(valueContainer);
        if (!result) {
            throw new IllegalArgumentException("Failed offer transaction!");
        }
        return true;
    }

    /**
     * A fast equivalent of {@link #tryOffer(ValueContainer, MergeFunction)} which
     * avoids the construction of {@link DataTransactionResult}s.
     *
     * @param valueContainer The value container
     * @param function The merge function
     * @return Whether the offer was successful
     */
    default boolean tryOfferFast(H valueContainer, MergeFunction function) throws IllegalArgumentException {
        final boolean result = offerFast(valueContainer, function);
        if (!result) {
            throw new IllegalArgumentException("Failed offer transaction!");
        }
        return true;
    }

    @Override
    default Collection<H> getContainers() {
        // Try the additional manipulators if they are supported
        if (this instanceof AdditionalContainerHolder) {
            final ImmutableList.Builder<H> builder = ImmutableList.builder();
            final AdditionalContainerCollection<H> containers = ((AdditionalContainerHolder<H>) this).getAdditionalContainers();
            containers.getAll().forEach(container -> builder.add(Copyable.copy(container).orElse(container)));
            return builder.build();
        }
        return ImmutableList.of();
    }

    @Override
    default DataTransactionResult copyFrom(S that, MergeFunction function) {
        return CompositeValueStoreHelper.copyFrom(this, that, function);
    }

    default DataTransactionResult copyFromNoEvents(S that, MergeFunction function) {
        final DataTransactionResult.Builder builder = DataTransactionResult.builder();
        boolean success = false;
        boolean merge = true;
        if (function == MergeFunction.IGNORE_ALL) {
            // Lets boost performance a bit by avoiding
            // constructing unnecessary containers
            if (that instanceof IValueContainer) {
                for (Key key : ((IValueContainer) that).getValueCollection().getKeys()) {
                    final Optional optElement = that.get(key);
                    if (optElement.isPresent()) {
                        final DataTransactionResult result = offerNoEvents(key, optElement.get());
                        builder.absorbResult(result);
                        if (result.isSuccessful()) {
                            success = true;
                        }
                    }
                }
                if (that instanceof AdditionalContainerHolder) {
                    final AdditionalContainerCollection<H> collection = ((AdditionalContainerHolder) that).getAdditionalContainers();
                    for (H container : collection.getAll()) {
                        final DataTransactionResult result = offerNoEvents(container);
                        builder.absorbResult(result);
                        if (result.isSuccessful()) {
                            success = true;
                        }
                    }
                }
                return builder.result(success ? DataTransactionResult.Type.SUCCESS : DataTransactionResult.Type.FAILURE).build();
            }
            // Ignore all just overrides the original containers, so
            // no need to copy the current one for the merge function
            merge = false;
        }
        final Collection<H> containers = that.getContainers();
        for (H thatContainer : containers) {
            final H thisContainer = merge ? get((Class<H>) thatContainer.getClass()).orElse(null) : null;
            final H merged = function.merge(thisContainer, thatContainer);
            final DataTransactionResult result = offerNoEvents(merged);
            builder.absorbResult(result);
            if (!result.getSuccessfulData().isEmpty()) {
                success = true;
            }
        }
        return builder.result(success ? DataTransactionResult.Type.SUCCESS : DataTransactionResult.Type.FAILURE).build();
    }

    /**
     * A fast equivalent of {@link #remove(Class)} which
     * avoids the construction of {@link DataTransactionResult}s.
     *
     * @param containerClass The container class
     * @return Whether the removal was successful
     */
    default boolean removeFast(Class<? extends H> containerClass) {
        checkNotNull(containerClass, "containerClass");

        if (this instanceof AdditionalContainerHolder) {
            final AdditionalContainerCollection<H> containers =
                    ((AdditionalContainerHolder<H>) this).getAdditionalContainers();
            final Optional<H> old = containers.remove(containerClass);
            if (old.isPresent()) {
                return true;
            }
        }

        return false;
    }

    @Override
    default DataTransactionResult remove(Class<? extends H> containerClass) {
        checkNotNull(containerClass, "containerClass");

        if (this instanceof AdditionalContainerHolder) {
            final AdditionalContainerCollection<H> containers =
                    ((AdditionalContainerHolder<H>) this).getAdditionalContainers();
            final Optional<H> old = containers.remove(containerClass);
            if (old.isPresent()) {
                return DataTransactionResult.successRemove(old.get().getValues());
            }
        }

        return DataTransactionResult.failNoData();
    }

    @Override
    default boolean supports(Class<? extends H> containerClass) {
        checkNotNull(containerClass, "containerClass");
        // Support all the additional manipulators
        return this instanceof AdditionalContainerHolder;
    }

    @Override
    default <T extends H> Optional<T> getOrCreate(Class<T> containerClass) {
        return get(containerClass);
    }

    @Override
    default <T extends H> Optional<T> get(Class<T> containerClass) {
        checkNotNull(containerClass, "containerClass");

        // Try the additional containers if they are supported
        if (this instanceof AdditionalContainerHolder) {
            final AdditionalContainerCollection<H> containers =
                    ((AdditionalContainerHolder<H>) this).getAdditionalContainers();
            return containers.get(containerClass);
        }

        return Optional.empty();
    }
}
