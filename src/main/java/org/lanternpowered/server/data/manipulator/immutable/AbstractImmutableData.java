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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistration;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistry;
import org.lanternpowered.server.data.manipulator.ManipulatorHelper;
import org.lanternpowered.server.data.manipulator.mutable.IDataManipulator;
import org.lanternpowered.server.data.value.AbstractValueContainer;
import org.lanternpowered.server.data.value.ElementHolderKeyRegistration;
import org.lanternpowered.server.data.value.KeyRegistration;
import org.lanternpowered.server.util.collect.Collections3;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

public abstract class AbstractImmutableData<I extends ImmutableDataManipulator<I, M>, M extends DataManipulator<M, I>>
        implements AbstractValueContainer<I, M>, IImmutableDataManipulator<I, M> {

    private final Map<Key<?>, KeyRegistration> rawValueMap;
    private final Map<Key<?>, Optional> cachedValues = new ConcurrentHashMap<>();
    private final Map<Key<?>, Optional> cachedImmutableValues = new ConcurrentHashMap<>();

    private final Class<M> manipulatorType;
    private final Class<I> immutableManipulatorType;

    public AbstractImmutableData(Class<I> immutableManipulatorType, Class<M> manipulatorType) {
        this.immutableManipulatorType = immutableManipulatorType;
        this.manipulatorType = manipulatorType;
        this.rawValueMap = new HashMap<>();
        registerKeys();
    }

    public AbstractImmutableData(M manipulator) {
        //noinspection unchecked
        final IDataManipulator<M, I> iDataManipulator = (IDataManipulator<M, I>) manipulator;
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
    public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
        // Cache the values if needed, to avoid unneeded object creation
        return (Optional) this.cachedValues.computeIfAbsent(checkNotNull(key, "key"),
                key1 -> (Optional) AbstractValueContainer.super.getValue((Key) key1));
    }

    @Override
    public <E, R extends ImmutableValue<E>> Optional<R> getImmutableValue(Key<? extends BaseValue<E>> key) {
        //noinspection unchecked
        return this.cachedImmutableValues.computeIfAbsent(key, key1 -> {
            //noinspection unchecked
            final Optional<? extends BaseValue<E>> optValue = AbstractValueContainer.super.getValue((Key) key1);
            if (optValue.isPresent()) {
                if (optValue.get() instanceof ImmutableValue) {
                    return optValue;
                } else {
                    return Optional.of(((Value<E>) optValue.get()).asImmutable());
                }
            }
            return Optional.empty();
        });
    }

    @SuppressWarnings("unchecked")
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
        return ManipulatorHelper.toContainer(this);
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
            return (Optional) ManipulatorHelper.buildContent(container, () ->  (AbstractValueContainer) create());
        }

        protected abstract I create();
    }
}
