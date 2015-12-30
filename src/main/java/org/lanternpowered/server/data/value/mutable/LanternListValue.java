/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.data.value.mutable;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.lanternpowered.server.data.value.immutable.ImmutableLanternListValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableListValue;
import org.spongepowered.api.data.value.mutable.ListValue;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class LanternListValue<E> extends LanternCollectionValue<E, List<E>, ListValue<E>, ImmutableListValue<E>> implements ListValue<E> {

    public LanternListValue(Key<? extends BaseValue<List<E>>> key) {
        super(key, Lists.<E>newArrayList());
    }

    public LanternListValue(Key<? extends BaseValue<List<E>>> key, List<E> defaultList, List<E> actualList) {
        super(key, Lists.newArrayList(defaultList), Lists.newArrayList(actualList));
    }

    public LanternListValue(Key<? extends BaseValue<List<E>>> key, List<E> actualValue) {
        this(key, Lists.<E>newArrayList(), actualValue);
    }

    @Override
    public ListValue<E> transform(Function<List<E>, List<E>> function) {
        this.actualValue = Lists.newArrayList(checkNotNull(function.apply(this.actualValue)));
        return this;
    }

    @Override
    public ListValue<E> filter(Predicate<? super E> predicate) {
        final List<E> list = Lists.newArrayList();
        for (E element : this.actualValue) {
            if (checkNotNull(predicate).test(element)) {
                list.add(element);
            }
        }
        return new LanternListValue<E>(getKey(), list);
    }

    @Override
    public List<E> getAll() {
        return Lists.newArrayList(this.actualValue);
    }

    @Override
    public ImmutableListValue<E> asImmutable() {
        return new ImmutableLanternListValue<E>(getKey(), ImmutableList.copyOf(this.actualValue));
    }

    @Override
    public E get(int index) {
        return this.actualValue.get(index);
    }

    @Override
    public ListValue<E> add(int index, E value) {
        this.actualValue.add(index, checkNotNull(value));
        return this;
    }

    @Override
    public ListValue<E> add(int index, Iterable<E> values) {
        int count = 0;
        for (Iterator<E> iterator = values.iterator(); iterator.hasNext(); count++) {
            this.actualValue.add(index + count, checkNotNull(iterator.next()));
        }
        return this;
    }

    @Override
    public ListValue<E> remove(int index) {
        this.actualValue.remove(index);
        return this;
    }

    @Override
    public ListValue<E> set(int index, E element) {
        this.actualValue.set(index, checkNotNull(element));
        return this;
    }

    @Override
    public int indexOf(E element) {
        return this.actualValue.indexOf(checkNotNull(element));
    }
}
