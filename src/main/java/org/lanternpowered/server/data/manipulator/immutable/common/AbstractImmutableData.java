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
package org.lanternpowered.server.data.manipulator.immutable.common;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.data.manipulator.ManipulatorHelper;
import org.lanternpowered.server.data.value.AbstractValueContainer;
import org.lanternpowered.server.data.value.KeyRegistration;
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
import java.util.concurrent.ConcurrentHashMap;

public class AbstractImmutableData<I extends ImmutableDataManipulator<I, M>, M extends DataManipulator<M, I>>
        implements AbstractValueContainer<I>, ImmutableDataManipulator<I, M> {

    private final Map<Key<?>, KeyRegistration> rawValueMap = new HashMap<>();
    private final Map<Key<?>, Optional<BaseValue<?>>> cachedValues = new ConcurrentHashMap<>();

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
    public M asMutable() {
        return null;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return ManipulatorHelper.toContainer(this);
    }

    public static abstract class AbstractImmutableManipulatorDataBuilder<T extends AbstractImmutableData> extends AbstractDataBuilder<T> {

        protected AbstractImmutableManipulatorDataBuilder(Class<T> requiredClass, int supportedVersion) {
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
