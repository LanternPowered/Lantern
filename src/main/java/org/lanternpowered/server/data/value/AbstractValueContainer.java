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
package org.lanternpowered.server.data.value;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.data.value.processor.ValueProcessor;
import org.lanternpowered.server.util.TriFunction;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.annotation.Nullable;

public interface AbstractValueContainer<C extends ValueContainer<C>> extends IValueContainer<C> {

    /**
     * Some {@link ValueProcessor}'s may want to depend on the fact that
     * a specific {@link Key} is applicable or attached to a {@link ValueContainer}.
     *
     * This class will represent a {@link Value} that is attached to a
     * specific {@link ValueContainer}.
     */
    class ElementHolderKeyRegistrationImpl<V extends BaseValue<E>, E> extends SimpleKeyRegistration.SingleProcessor<V, E> implements ElementHolderKeyRegistration<V, E> {

        private @Nullable E value;

        ElementHolderKeyRegistrationImpl(Key<? extends V> key) {
            super(key);
        }

        @Nullable
        @Override
        public synchronized E set(@Nullable E value) {
            E oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Nullable
        @Override
        public synchronized E get() {
            return this.value;
        }

        @Override
        public  ElementHolderKeyRegistration<V, E> addValueProcessor(ValueProcessor<V, E> valueProcessor) {
            super.addValueProcessor(valueProcessor);
            return this;
        }

        @Override
        public ElementHolderKeyRegistration<V, E> applyValueProcessor(Consumer<ValueProcessor.Builder<V, E>> builderConsumer) {
            super.applyValueProcessor(builderConsumer);
            return this;
        }

        @Override
        public ElementHolderKeyRegistration<V, E> addElementChangeListener(ElementHolderChangeListener listener) {
            super.addElementChangeListener(listener);
            return this;
        }

        @Override
        public ElementHolderKeyRegistration<V, E> applyAttachedValueProcessor(
                Consumer<ValueProcessor.AttachedElementBuilder<V, E>> attachedElementBuilderConsumer) {
            ValueProcessor.AttachedElementBuilder<V, E> builder = ValueProcessor.attachedElementBuilder();
            checkNotNull(attachedElementBuilderConsumer, "attachedElementBuilderConsumer").accept(builder);
            return this.addValueProcessor(builder.build());
        }
    }

    /**
     * Whether all the {@link Key}s in this container should be registered
     * before they can be offered/retrieved.
     *
     * @return requires key registration
     */
    default boolean requiresKeyRegistration() {
        return false;
    }

    /**
     * Gets the raw list with all the custom data manipulators,
     * this may be null if custom data isn't supported.
     *
     * @return the raw data manipulators
     */
    @Nullable
    default List<DataManipulator<?, ?>> getRawAdditionalManipulators() {
        return null;
    }

    /**
     * Gets the internal map that is used to hold the value's by their key.
     *
     * @return the value by key map
     */
    Map<Key<?>, KeyRegistration> getRawValueMap();

    @SuppressWarnings("unchecked")
    @Nullable
    default <V extends BaseValue<E>, E> KeyRegistration<V, E> getKeyRegistration(Key<? extends BaseValue<E>> key) {
        return this.getRawValueMap().get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    default boolean supports(Key<?> key) {
        checkNotNull(key, "key");

        // Check the local key registration
        KeyRegistration<?, ?> localKeyRegistration = this.getKeyRegistration((Key) key);
        if (localKeyRegistration == null) {
            if (this.requiresKeyRegistration()) {
                return false;
            }
        } else if (!localKeyRegistration.getValueProcessors().isEmpty()) {
            return true;
        }

        // Check the global key registrations
        KeyRegistration<?, ?> keyRegistration = LanternValueFactory.getInstance().getKeyRegistration((Key) key);
        if (keyRegistration != null) {
            for (ValueProcessor<?,?> valueProcessor : keyRegistration.getValueProcessors()) {
                if (valueProcessor.getApplicableTester().test((Key) key, this)) {
                    return true;
                }
            }
        }

        // Use the global processor
        if (localKeyRegistration != null && localKeyRegistration instanceof ElementHolder) {
            return true;
        }

        // Check for the custom data manipulators
        List<DataManipulator<?, ?>> manipulators = this.getRawAdditionalManipulators();
        // Custom data is supported by this container
        if (manipulators != null) {
            for (DataManipulator<?, ?> dataManipulator : manipulators) {
                if (dataManipulator.supports(key)) {
                    return true;
                }
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    default <E> Optional<E> getWith(Key<? extends BaseValue<E>> key, ValueProcessor<BaseValue<E>, E> processor) {
        return (Optional) ((BiFunction) processor.getRetrieveHandler()).apply(key, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    default <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
        checkNotNull(key, "key");

        // Check the local key registration
        KeyRegistration<BaseValue<E>, E> localKeyRegistration = this.getKeyRegistration(key);
        if (localKeyRegistration == null) {
            if (this.requiresKeyRegistration()) {
                return Optional.empty();
            }
        } else {
            List<ValueProcessor<BaseValue<E>, E>> processors = localKeyRegistration.getValueProcessors();
            if (!processors.isEmpty()) {
                return this.getWith(key, processors.get(0));
            }
        }

        // Check the global key registrations
        KeyRegistration<BaseValue<E>, E> keyRegistration = LanternValueFactory.getInstance().getKeyRegistration(key);
        if (keyRegistration != null) {
            for (ValueProcessor<BaseValue<E>, E> valueProcessor : keyRegistration.getValueProcessors()) {
                if (valueProcessor.getApplicableTester().test((Key) key, this)) {
                    return this.getWith(key, valueProcessor);
                }
            }
        }

        // Use the global processor
        if (localKeyRegistration != null && localKeyRegistration instanceof ElementHolder) {
            return this.getWith(key, ValueProcessor.getDefaultAttachedValueProcessor());
        }

        // Check for the custom data manipulators
        List<DataManipulator<?, ?>> manipulators = this.getRawAdditionalManipulators();
        // Custom data is supported by this container
        if (manipulators != null) {
            for (DataManipulator<?, ?> dataManipulator : manipulators) {
                if (dataManipulator.supports(key)) {
                    return dataManipulator.get(key);
                }
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    default <E, V extends BaseValue<E>> Optional<V> getValueWith(Key<? extends BaseValue<E>> key, ValueProcessor<BaseValue<E>, E> processor) {
        return (Optional) ((TriFunction) processor.getValueRetrieveHandler()).apply(key, this, processor);
    }

    @SuppressWarnings("unchecked")
    @Override
    default <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
        checkNotNull(key, "key");

        // Check the local key registration
        KeyRegistration<BaseValue<E>, E> localKeyRegistration = this.getKeyRegistration(key);
        if (localKeyRegistration == null) {
            if (this.requiresKeyRegistration()) {
                return Optional.empty();
            }
        } else {
            List<ValueProcessor<BaseValue<E>, E>> processors = localKeyRegistration.getValueProcessors();
            if (!processors.isEmpty()) {
                return this.getValueWith(key, processors.get(0));
            }
        }

        // Check the global key registrations
        KeyRegistration<BaseValue<E>, E> keyRegistration = LanternValueFactory.getInstance().getKeyRegistration(key);
        if (keyRegistration != null) {
            for (ValueProcessor<BaseValue<E>, E> valueProcessor : keyRegistration.getValueProcessors()) {
                if (valueProcessor.getApplicableTester().test((Key) key, this)) {
                    return this.getValueWith(key, valueProcessor);
                }
            }
        }

        // Use the global processor
        if (localKeyRegistration != null && localKeyRegistration instanceof ElementHolder) {
            return this.getValueWith(key, ValueProcessor.getDefaultAttachedValueProcessor());
        }

        // Check for the custom data manipulators
        List<DataManipulator<?, ?>> manipulators = this.getRawAdditionalManipulators();
        // Custom data is supported by this container
        if (manipulators != null) {
            for (DataManipulator<?, ?> dataManipulator : manipulators) {
                if (dataManipulator.supports(key)) {
                    return dataManipulator.getValue(key);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    default Set<Key<?>> getKeys() {
        ImmutableSet.Builder<Key<?>> keys = ImmutableSet.builder();
        keys.addAll(this.getRawValueMap().keySet());
        List<DataManipulator<?, ?>> manipulators = this.getRawAdditionalManipulators();
        if (manipulators != null) {
            manipulators.forEach(manipulator -> keys.addAll(manipulator.getKeys()));
        }
        return keys.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    default Set<ImmutableValue<?>> getValues() {
        ImmutableSet.Builder<ImmutableValue<?>> values = ImmutableSet.builder();
        for (Map.Entry<Key<?>, KeyRegistration> entry : this.getRawValueMap().entrySet()) {
            Key key = entry.getKey();
            Optional<BaseValue> optValue = this.getValue(key);
            if (optValue.isPresent()) {
                values.add(ValueHelper.toImmutable(optValue.get()));
            }
        }
        List<DataManipulator<?, ?>> manipulators = this.getRawAdditionalManipulators();
        // Custom data is supported by this container
        if (manipulators != null) {
            for (DataManipulator<?, ?> dataManipulator : manipulators) {
                values.addAll(dataManipulator.getValues());
            }
        }
        return values.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    default <E> ElementHolder<E> getElementHolder(Key<? extends BaseValue<E>> key) {
        Object object = this.getRawValueMap().get(checkNotNull(key, "key"));
        if (object instanceof ElementHolder) {
            return (ElementHolder<E>) object;
        }
        return null;
    }

    @Override
    default <V extends BaseValue<E>, E> ElementHolderKeyRegistration<V, E> registerKey(Key<? extends V> key, @Nullable E defaultValue) {
        checkNotNull(key, "key");
        Map<Key<?>, KeyRegistration> map = this.getRawValueMap();
        checkArgument(!map.containsKey(key), "The specified key (%s) is already registered.", key);
        ElementHolderKeyRegistrationImpl<V, E> holder = new ElementHolderKeyRegistrationImpl<>(key);
        holder.set(defaultValue);
        map.put(key, holder);
        return holder;
    }

    @Override
    default <V extends BaseValue<E>, E> KeyRegistration<V, E> registerKey(Key<? extends V> key) {
        checkNotNull(key, "key");
        Map<Key<?>, KeyRegistration> map = this.getRawValueMap();
        checkArgument(!map.containsKey(key), "The specified key (%s) is already registered.", key);
        KeyRegistration<V, E> holder = new SimpleKeyRegistration.SingleProcessor<>(key);
        map.put(key, holder);
        return holder;
    }
}
