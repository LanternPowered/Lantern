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

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistration;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistry;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.data.value.mutable.AbstractCompositeValueStore;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface AbstractDataHolder extends AbstractCompositeValueStore<DataHolder, DataManipulator<?,?>>,
        DataHolder, AbstractPropertyHolder {

    @SuppressWarnings("unchecked")
    @Override
    default DataTransactionResult copyFrom(DataHolder that, MergeFunction function) {
        final Collection<DataManipulator<?, ?>> containers = that.getContainers();
        final DataTransactionResult.Builder builder = DataTransactionResult.builder();
        boolean success = false;
        for (DataManipulator<?, ?> thatContainer : containers) {
            final DataManipulator<?, ?> thisContainer = get(thatContainer.getClass()).orElse(null);
            final DataManipulator<?, ?> merged = function.merge(thisContainer, thatContainer);
            final DataTransactionResult result = offer(merged);
            builder.reject(result.getRejectedData());
            builder.replace(result.getReplacedData());
            builder.success(result.getSuccessfulData());
            if (!result.getSuccessfulData().isEmpty()) {
                success = true;
            }
        }
        return builder.result(success ? DataTransactionResult.Type.SUCCESS : DataTransactionResult.Type.FAILURE).build();
    }

    @SuppressWarnings("unchecked")
    @Override
    default Collection<DataManipulator<?, ?>> getContainers() {
        final ImmutableList.Builder<DataManipulator<?, ?>> builder = ImmutableList.builder();
        for (DataManipulatorRegistration registration : DataManipulatorRegistry.get().getAll()) {
            DataManipulator manipulator = (DataManipulator<?, ?>) registration.createMutable();
            for (Key key : (Set<Key>) registration.getRequiredKeys()) {
                final Optional value = getValue(key);
                if (value.isPresent()) {
                    manipulator.set(key, value.get());
                } else if (!supports(key)) {
                    manipulator = null;
                    break;
                }
            }
            if (manipulator != null) {
                builder.add(manipulator);
            }
        }

        // Try the additional manipulators if they are supported
        final Map<Class<?>, DataManipulator<?, ?>> manipulators = getRawAdditionalContainers();
        if (manipulators != null) {
            manipulators.values().forEach(manipulator -> builder.add(manipulator.copy()));
        }

        return builder.build();
    }

    @Override
    default DataHolder copy() {
        return this;
    }

    @Override
    default int getContentVersion() {
        return 1;
    }

    @Override
    default DataContainer toContainer() {
        return DataContainer.createNew();
    }
}
