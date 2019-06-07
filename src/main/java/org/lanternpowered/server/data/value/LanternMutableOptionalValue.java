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
package org.lanternpowered.server.data.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.value.OptionalValue;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.common.data.SpongeKey;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings({"unchecked", "OptionalUsedAsFieldOrParameterType"})
public class LanternMutableOptionalValue<E> extends LanternValue<Optional<E>> implements OptionalValue.Mutable<E> {

    public LanternMutableOptionalValue(Key<? extends Value<Optional<E>>> key, Optional<E> value) {
        super(key, value);
    }

    @Override
    public Value.Mutable<E> orElse(E defaultValue) {
        checkNotNull(defaultValue, "defaultValue");
        final Key<Value<E>> unwrappedKey = ((SpongeKey) this.key).getUnwrappedOptionalKey();
        checkState(unwrappedKey != null, "Key %s is missing the unwrapped optional key", this.key.getKey());
        return new LanternMutableValue<>(unwrappedKey, this.value.orElse(defaultValue));
    }

    @Override
    public Mutable<E> transform(Function<Optional<E>, Optional<E>> function) {
        return set(checkNotNull(function, "function").apply(get()));
    }

    @Override
    public Immutable<E> asImmutable() {
        return new LanternImmutableOptionalValue<>(this.key, CopyHelper.copy(this.value));
    }

    @Override
    public Mutable<E> set(Optional<E> value) {
        return new LanternMutableOptionalValue<>(this.key, CopyHelper.copy(value));
    }

    @Override
    public Mutable<E> copy() {
        return new LanternMutableOptionalValue<>(this.key, CopyHelper.copy(this.value));
    }
}
