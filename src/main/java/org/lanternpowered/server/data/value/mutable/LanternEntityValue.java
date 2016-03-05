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
package org.lanternpowered.server.data.value.mutable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.data.value.immutable.ImmutableLanternValue;
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

/**
 * This class provides a safe reference for an {@link Entity} such that
 * references aren't maintained and therefor leaked. Provided that
 */
public class LanternEntityValue<T extends Entity> implements Value<T> {

    private final Key<? extends BaseValue<T>> key;
    private UUID entityid;
    private WeakReference<T> weakReference = new WeakReference<>(null);

    public LanternEntityValue(Key<? extends BaseValue<T>> key, T actualValue) {
        this.key = checkNotNull(key);
        this.entityid = checkNotNull(actualValue).getUniqueId();
        this.weakReference = new WeakReference<>(actualValue);

    }

    @Override
    public T get() {
        @Nullable Entity entity = this.weakReference.get();
        if (entity == null) {
            for (World world : Sponge.getGame().getServer().getWorlds()) {
                final Optional<T> optional = (Optional<T>) world.getEntity(this.entityid);
                if (optional.isPresent()) {
                    return optional.get();
                }
            }
        }
        throw new IllegalStateException("The entity has expired or has been permanently removed! The entity's id was: " + this.entityid.toString());
    }

    @Override
    public boolean exists() {
        return this.weakReference.get() != null;
    }

    @Override
    public T getDefault() {
        checkState(!exists(), "The entity reference expired!");
        return this.weakReference.get();
    }

    @Override
    public Optional<T> getDirect() {
        return Optional.ofNullable(this.weakReference.get());
    }

    @Override
    public Key<? extends BaseValue<T>> getKey() {
        return this.key;
    }

    @Override
    public Value<T> set(T value) {
        this.entityid = checkNotNull(value).getUniqueId();
        this.weakReference = new WeakReference<>(value);
        return this;
    }

    @Override
    public Value<T> transform(Function<T, T> function) {
        final T entity = checkNotNull(checkNotNull(function).apply(this.weakReference.get()));
        this.weakReference = new WeakReference<>(entity);
        this.entityid = checkNotNull(entity).getUniqueId();
        return this;
    }

    @Override
    public ImmutableValue<T> asImmutable() {
        return new ImmutableLanternValue<>(this.getKey(), this.weakReference.get());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("key", this.key)
                .add("entityid", this.entityid)
                .add("weakReference", this.weakReference)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.entityid, this.weakReference);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final LanternEntityValue other = (LanternEntityValue) obj;
        return Objects.equals(this.key, other.key)
                && Objects.equals(this.entityid, other.entityid)
                && Objects.equals(this.weakReference, other.weakReference);
    }
}
