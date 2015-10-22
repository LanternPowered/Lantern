/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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
package org.lanternpowered.server.data.value.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;
import java.util.function.Function;

import org.lanternpowered.server.data.value.mutable.LanternOptionalValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableOptionalValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.OptionalValue;

import javax.annotation.Nullable;

public class ImmutableLanternOptionalValue<E> extends ImmutableLanternValue<Optional<E>> implements ImmutableOptionalValue<E> {

    public ImmutableLanternOptionalValue(Key<? extends BaseValue<Optional<E>>> key) {
        super(key, Optional.<E>empty());
    }

    public ImmutableLanternOptionalValue(Key<? extends BaseValue<Optional<E>>> key, Optional<E> actualValue) {
        super(key, Optional.<E>empty(), actualValue);
    }

    @Override
    public ImmutableOptionalValue<E> with(Optional<E> value) {
        return new ImmutableLanternOptionalValue<E>(getKey(), checkNotNull(value));
    }

    @Override
    public ImmutableOptionalValue<E> transform(Function<Optional<E>, Optional<E>> function) {
        return new ImmutableLanternOptionalValue<E>(getKey(), checkNotNull(function.apply(get())));
    }

    @Override
    public OptionalValue<E> asMutable() {
        return new LanternOptionalValue<E>(getKey(), this.actualValue);
    }

    @Override
    public ImmutableOptionalValue<E> instead(@Nullable E value) {
        return new ImmutableLanternOptionalValue<E>(getKey(), Optional.ofNullable(value));
    }

    @Override
    public ImmutableValue<E> or(E value) { // TODO actually construct a new key for this kind...
        return new ImmutableLanternValue<E>(null, get().isPresent() ? get().get() : checkNotNull(value));
    }
}
