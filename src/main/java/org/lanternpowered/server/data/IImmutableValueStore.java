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
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.ImmutableValueStore;

import java.util.List;

@SuppressWarnings("unchecked")
public interface IImmutableValueStore<S extends ImmutableValueStore<S, H>, H extends ValueContainer<?>>
        extends IValueContainer<S>, ImmutableValueStore<S, H> {

    @Override
    default S copy() {
        return (S) this;
    }

    @Override
    default List<H> getContainers() {
        if (this instanceof AdditionalContainerHolder) {
            final ImmutableList.Builder<H> builder = ImmutableList.builder();
            final AdditionalContainerCollection<H> manipulators = ((AdditionalContainerHolder<H>) this).getAdditionalContainers();
            manipulators.getAll().forEach(builder::add);
            return builder.build();
        }
        return ImmutableList.of();
    }
}
