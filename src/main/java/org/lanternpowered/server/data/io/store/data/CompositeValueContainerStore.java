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
package org.lanternpowered.server.data.io.store.data;

import org.lanternpowered.server.data.io.store.ObjectStore;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.data.value.AbstractValueContainer;
import org.lanternpowered.server.data.value.ElementHolder;
import org.lanternpowered.server.data.value.KeyRegistration;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.mutable.CompositeValueStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeValueContainerStore<T extends S, S extends CompositeValueStore<S, H>, H extends ValueContainer<?>> implements ObjectStore<T> {

    @SuppressWarnings("unchecked")
    @Override
    public void deserialize(T object, DataContainer dataContainer) {
        if (object instanceof AbstractValueContainer) {
            AbstractValueContainer<S> valueContainer = (AbstractValueContainer) object;
            SimpleValueContainer simpleValueContainer = new SimpleValueContainer(new HashMap<>());

            this.deserializeValues(object, simpleValueContainer, dataContainer);
            for (Map.Entry<Key<?>, Object> entry : simpleValueContainer.getValues().entrySet()) {
                ElementHolder elementHolder = valueContainer.getElementHolder((Key) entry.getKey());
                if (elementHolder != null) {
                    elementHolder.set(entry.getValue());
                } else {
                    Lantern.getLogger().warn("Attempted to offer a unsupported value with key \"{}\" to the object {}",
                            entry.getKey().toString(), object.toString());
                }
            }

            List<DataManipulator<?,?>> additionalManipulators = valueContainer.getRawAdditionalManipulators();
            if (additionalManipulators != null) {
                this.deserializeAdditionalData(object, additionalManipulators, dataContainer);
            }
        } else {
            // Not sure what to do
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void serialize(T object, DataContainer dataContainer) {
        if (object instanceof AbstractValueContainer) {
            AbstractValueContainer<S> valueContainer = (AbstractValueContainer) object;
            SimpleValueContainer simpleValueContainer = new SimpleValueContainer(new HashMap<>());

            for (Map.Entry<Key<?>, KeyRegistration> entry : valueContainer.getRawValueMap().entrySet()) {
                Object value = entry.getValue();
                if (value instanceof ElementHolder) {
                    Object element = ((ElementHolder) value).get();
                    if (element != null) {
                        simpleValueContainer.set((Key) entry.getKey(), element);
                    }
                }
            }

            this.serializeValues(object, simpleValueContainer, dataContainer);

            List<DataManipulator<?,?>> additionalManipulators = valueContainer.getRawAdditionalManipulators();
            if (additionalManipulators != null) {
                this.serializeAdditionalData(object, additionalManipulators, dataContainer);
            }
        } else {
            // Not sure what to do
        }
    }

    /**
     * Serializes all the {@link DataManipulator}s and puts
     * them into the {@link DataContainer}.
     *
     * @param manipulators The data manipulators
     * @param dataContainer The data container
     */
    public void serializeAdditionalData(T object, List<DataManipulator<?, ?>> manipulators, DataContainer dataContainer) {
    }

    /**
     * Deserializes all the {@link DataManipulator}s from the {@link DataContainer}
     * and puts them into the {@link List}.
     *
     * @param manipulators The data manipulators
     * @param dataContainer The data container
     */
    public void deserializeAdditionalData(T object, List<DataManipulator<?, ?>> manipulators, DataContainer dataContainer) {
    }

    /**
     * Serializes all the values of the {@link SimpleValueContainer} and puts
     * them into the {@link DataContainer}.
     *
     * @param valueContainer The value container
     * @param dataContainer The data container
     */
    public void serializeValues(T object, SimpleValueContainer valueContainer, DataContainer dataContainer) {
    }

    /**
     * Deserializers all the values from the {@link DataContainer}
     * into the {@link SimpleValueContainer}.
     *
     * @param valueContainer The value container
     * @param dataContainer The data container
     */
    public void deserializeValues(T object, SimpleValueContainer valueContainer, DataContainer dataContainer) {
    }
}
