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
package org.lanternpowered.server.event;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import com.google.common.reflect.TypeToken;
import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.server.game.registry.type.cause.EventContextKeysModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.event.cause.EventContextKey;

import javax.annotation.Nullable;

public final class LanternEventContextKeyBuilder<T> implements EventContextKey.Builder<T> {

    @Nullable private TypeToken<T> typeToken;
    @Nullable private String name;
    @Nullable private CatalogKey id;

    public LanternEventContextKeyBuilder<T> type(TypeToken<T> typeToken) {
        this.typeToken = checkNotNull(typeToken, "typeToken");
        return this;
    }

    @Override
    public LanternEventContextKeyBuilder<T> type(Class<T> typeClass) {
        return type(TypeToken.of(typeClass));
    }

    @Override
    public LanternEventContextKeyBuilder<T> id(CatalogKey id) {
        this.id = checkNotNull(id, "id");
        return this;
    }

    @Override
    public LanternEventContextKeyBuilder<T> name(String name) {
        this.name = checkNotNullOrEmpty(name, "name");
        return this;
    }

    @Override
    public LanternEventContextKey<T> build() {
        checkState(this.id != null, "The id must be set");
        checkState(this.typeToken != null, "The type must be set");
        final LanternEventContextKey<T> contextKey = new LanternEventContextKey<>(
                CatalogKeys.of(this.id.getNamespace(), this.id.getValue(), this.name == null ? this.id.getValue() : this.name), this.typeToken);
        EventContextKeysModule.get().registerAdditionalCatalog(contextKey);
        return contextKey;
    }

    @Override
    public LanternEventContextKeyBuilder<T> from(EventContextKey<T> value) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot create a new EventContextKey based on another key!");
    }

    @Override
    public LanternEventContextKeyBuilder<T> reset() {
        this.typeToken = null;
        this.name = null;
        this.id = null;
        return this;
    }
}
