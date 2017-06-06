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

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.data.manipulator.mutable.IListData;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.ImmutableListData;
import org.spongepowered.api.data.manipulator.mutable.ListData;
import org.spongepowered.api.data.value.immutable.ImmutableListValue;
import org.spongepowered.api.data.value.mutable.ListValue;

import java.util.List;

@SuppressWarnings("unchecked")
public abstract class AbstractImmutableListData<E, I extends ImmutableListData<E, I, M>, M extends ListData<E, M, I>>
        extends AbstractImmutableData<I, M> implements IImmutableListData<E, I, M> {

    private final Key<? extends ListValue<E>> listKey;

    public AbstractImmutableListData(Class<I> immutableManipulatorType, Class<M> manipulatorType, Key<ListValue<E>> listKey) {
        this(immutableManipulatorType, manipulatorType, listKey, ImmutableList.of());
    }

    public AbstractImmutableListData(Class<I> immutableManipulatorType, Class<M> manipulatorType, Key<ListValue<E>> listKey, List<E> list) {
        super(immutableManipulatorType, manipulatorType);
        getValueCollection().register(listKey, ImmutableList.copyOf(list));
        this.listKey = listKey;
    }

    public AbstractImmutableListData(M manipulator) {
        super(manipulator);
        //noinspection unchecked
        this.listKey = ((IListData<E, M, I>) manipulator).getListKey();
    }

    @Override
    public ImmutableListValue<E> getListValue() {
        return (ImmutableListValue<E>) tryGetImmutableValueFor(this.listKey).get();
    }

    @Override
    public List<E> asList() {
        return get(this.listKey).get();
    }

    @Override
    public Key<? extends ListValue<E>> getListKey() {
        return this.listKey;
    }
}
