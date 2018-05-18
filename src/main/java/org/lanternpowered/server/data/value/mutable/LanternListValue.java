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
package org.lanternpowered.server.data.value.mutable;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.data.value.immutable.ImmutableLanternListValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableListValue;
import org.spongepowered.api.data.value.mutable.ListValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LanternListValue<E> extends LanternCollectionValue<E, List<E>, ListValue<E>, ImmutableListValue<E>> implements ListValue<E> {

    public LanternListValue(Key<? extends BaseValue<List<E>>> key) {
        this(key, Collections.emptyList());
    }

    public LanternListValue(Key<? extends BaseValue<List<E>>> key, List<E> defaultList, List<E> actualList) {
        super(key, ImmutableList.copyOf(defaultList), actualList);
    }

    public LanternListValue(Key<? extends BaseValue<List<E>>> key, List<E> actualValue) {
        this(key, Collections.emptyList(), actualValue);
    }

    @Override
    public ListValue<E> transform(Function<List<E>, List<E>> function) {
        this.actualValue = new ArrayList<>(checkNotNull(function.apply(this.actualValue)));
        return this;
    }

    @Override
    public ListValue<E> filter(Predicate<? super E> predicate) {
        return new LanternListValue<>(getKey(), getDefault(), getActualValue().stream()
                .filter(element -> checkNotNull(predicate).test(element))
                .collect(Collectors.toList()));
    }

    @Override
    public List<E> getAll() {
        return new ArrayList<>(getActualValue());
    }

    @Override
    public ImmutableListValue<E> asImmutable() {
        return new ImmutableLanternListValue<>(getKey(), getDefault(), getActualValue());
    }

    @Override
    public E get(int index) {
        return getActualValue().get(index);
    }

    @Override
    public ListValue<E> add(int index, E value) {
        getActualValue().add(index, checkNotNull(value));
        return this;
    }

    @Override
    public ListValue<E> add(int index, Iterable<E> values) {
        int count = 0;
        for (Iterator<E> iterator = values.iterator(); iterator.hasNext(); count++) {
            getActualValue().add(index + count, checkNotNull(iterator.next()));
        }
        return this;
    }

    @Override
    public ListValue<E> remove(int index) {
        getActualValue().remove(index);
        return this;
    }

    @Override
    public ListValue<E> set(int index, E element) {
        getActualValue().set(index, checkNotNull(element));
        return this;
    }

    @Override
    public int indexOf(E element) {
        return getActualValue().indexOf(checkNotNull(element));
    }

    @Override
    public LanternListValue<E> copy() {
        return new LanternListValue<>(getKey(), getDefault(), getActualValue());
    }
}
