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

import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.data.value.mutable.AbstractCompositeValueStore;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.event.cause.Cause;

import java.util.Collection;
import java.util.Optional;

public interface AbstractDataHolder extends AbstractCompositeValueStore<DataHolder, DataManipulator<?,?>>, DataHolder, AbstractPropertyHolder {

    @Override
    default <T extends DataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        return null;
    }

    @Override
    default <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
        return null;
    }

    @Override
    default boolean supports(Class<? extends DataManipulator<?, ?>> holderClass) {
        return false;
    }

    @Override
    default DataTransactionResult offer(DataManipulator<?, ?> valueContainer, MergeFunction function, Cause cause) {
        return null;
    }

    @Override
    default DataTransactionResult remove(Class<? extends DataManipulator<?, ?>> containerClass) {
        return null;
    }

    @Override
    default DataTransactionResult copyFrom(DataHolder that, MergeFunction function) {
        return null;
    }

    @Override
    default Collection<DataManipulator<?, ?>> getContainers() {
        return null;
    }

    @Override
    default DataContainer toContainer() {
        return new MemoryDataContainer();
    }
}
