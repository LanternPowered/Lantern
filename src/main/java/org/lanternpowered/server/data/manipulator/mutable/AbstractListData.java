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
package org.lanternpowered.server.data.manipulator.mutable;

import org.lanternpowered.server.data.manipulator.IDataManipulatorBase;
import org.lanternpowered.server.data.manipulator.immutable.IImmutableListData;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.ImmutableListData;
import org.spongepowered.api.data.manipulator.mutable.ListData;
import org.spongepowered.api.data.value.mutable.ListValue;

import java.util.List;

@SuppressWarnings("unchecked")
public abstract class AbstractListData<E, M extends ListData<E, M, I>, I extends ImmutableListData<E, I, M>> extends AbstractData<M, I>
        implements ListData<E, M, I> {

    private final Key<ListValue<E>> listKey;

    protected AbstractListData(Class<M> manipulatorType, Class<I> immutableManipulatorType, Key<ListValue<E>> listKey, List<E> defaultList) {
        super(manipulatorType, immutableManipulatorType);
        getValueCollection().register(listKey, defaultList);
        this.listKey = listKey;
    }

    protected AbstractListData(I manipulator) {
        this((IDataManipulatorBase<M, I>) manipulator);
    }

    protected AbstractListData(M manipulator) {
        this((IDataManipulatorBase<M, I>) manipulator);
    }

    protected AbstractListData(IDataManipulatorBase<M, I> manipulator) {
        super(manipulator);
        if (manipulator instanceof IListData) {
            this.listKey = ((IListData) manipulator).getListKey();
        } else {
            this.listKey = ((IImmutableListData) manipulator).getListKey();
        }
    }

    @Override
    public ListValue<E> getListValue() {
        return getValue(this.listKey).get();
    }

    @Override
    public List<E> asList() {
        return get(this.listKey).get();
    }
}
