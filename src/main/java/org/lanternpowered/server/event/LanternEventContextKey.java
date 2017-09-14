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

import com.google.common.base.MoreObjects;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.event.cause.EventContextKey;

@SuppressWarnings("unchecked")
public final class LanternEventContextKey<T> extends PluginCatalogType.Base implements EventContextKey<T> {

    public static <T> LanternEventContextKeyBuilder<T> builder(Class<T> type) {
        return new LanternEventContextKeyBuilder<T>().type(type);
    }

    public static <T> LanternEventContextKeyBuilder<T> builder(TypeToken<T> type) {
        return new LanternEventContextKeyBuilder<T>().type(type);
    }

    private final TypeToken<T> typeToken;

    public LanternEventContextKey(String pluginId, String id, String name, Class<T> type) {
        this(pluginId, id, name, TypeToken.of(type));
    }

    public LanternEventContextKey(String pluginId, String id, String name, TypeToken<T> typeToken) {
        super(pluginId, id, name);
        this.typeToken = typeToken;
    }

    public TypeToken<T> getAllowedTypeToken() {
        return this.typeToken;
    }

    @Override
    public Class<T> getAllowedType() {
        return (Class<T>) this.typeToken.getRawType();
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("allowedType", this.typeToken);
    }
}
