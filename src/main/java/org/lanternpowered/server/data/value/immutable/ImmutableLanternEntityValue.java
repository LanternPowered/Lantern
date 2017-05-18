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
package org.lanternpowered.server.data.value.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.data.value.mutable.LanternEntityValue;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.World;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;

public class ImmutableLanternEntityValue<T extends Entity> implements ImmutableValue<T> {

    private final Key<? extends BaseValue<T>> key;

    private final UUID entityId;
    private WeakReference<T> weakReference;

    public ImmutableLanternEntityValue(Key<? extends BaseValue<T>> key, T entity) {
        this.key = checkNotNull(key);
        this.weakReference = new WeakReference<>(checkNotNull(entity));
        this.entityId = entity.getUniqueId();
    }

    @SuppressWarnings("unchecked")
    private T getRaw() {
        T entity = this.weakReference.get();
        if (entity != null) {
            return entity;
        }
        for (World world : Sponge.getGame().getServer().getWorlds()) {
            final Optional<T> optional = (Optional<T>) world.getEntity(this.entityId);
            if (optional.isPresent()) {
                entity = optional.get();
                this.weakReference = new WeakReference<>(entity);
                return entity;
            }
        }
        return null;
    }

    @Override
    public ImmutableValue<T> with(T value) {
        return new ImmutableLanternEntityValue<>(getKey(), checkNotNull(value));
    }

    @Override
    public ImmutableValue<T> transform(Function<T, T> function) {
        return with(checkNotNull(function).apply(get()));
    }

    @Override
    public Value<T> asMutable() {
        return new LanternEntityValue<>(getKey(), get());
    }

    @Override
    public T get() {
        final T entity = getRaw();
        if (entity != null) {
            return entity;
        }
        throw new IllegalStateException("The entity has expired or has been permanently removed! The entity's id was: " + this.entityId);
    }

    @Override
    public boolean exists() {
        return this.weakReference.get() != null;
    }

    @Override
    public T getDefault() {
        return get();
    }

    @Override
    public Optional<T> getDirect() {
        return Optional.of(get());
    }

    @Override
    public Key<? extends BaseValue<T>> getKey() {
        return this.key;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.weakReference, this.key);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ImmutableLanternEntityValue other = (ImmutableLanternEntityValue) obj;
        return Objects.equals(this.weakReference, other.weakReference)
                && Objects.equals(this.key, other.key);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("weakReference", this.weakReference)
                .add("key", this.key)
                .toString();
    }
}
