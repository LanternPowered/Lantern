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
package org.lanternpowered.server.util.option;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gson.reflect.TypeToken;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class Option<V> {

    public static <V> Option<V> of(String id, TypeToken<V> typeToken, @Nullable V defaultValue) {
        return new Option<>(checkId(id), typeToken, defaultValue);
    }

    public static <V> Option<V> of(String id, TypeToken<V> typeToken) {
        return new Option<>(checkId(id), typeToken, null);
    }

    public static <V> Option<V> of(String id, Class<V> type) {
        return new Option<>(checkId(id), TypeToken.get(type), null);
    }

    public static <V> Option<V> of(String id, Class<V> type, @Nullable V defaultValue) {
        return new Option<>(checkId(id), TypeToken.get(type), defaultValue);
    }

    public static Optional<Option<?>> get(String id) {
        return Optional.ofNullable(options.get(checkNotNull(id, "id").toLowerCase(Locale.ENGLISH)));
    }

    private static final Map<String, Option<?>> options = new ConcurrentHashMap<>();

    private static String checkId(String id) {
        id = id.toLowerCase(Locale.ENGLISH);
        checkArgument(!options.containsKey(id), "The id %s is already used.", id);
        return id;
    }

    private final String id;
    private final TypeToken<V> typeToken;
    @Nullable private final V defaultValue;

    private Option(String id, TypeToken<V> typeToken, @Nullable V defaultValue) {
        this.typeToken = checkNotNull(typeToken, "typeToken");
        this.id = checkNotNull(id, "id").toLowerCase(Locale.ENGLISH);
        this.defaultValue = defaultValue;
        options.put(id, this);
    }

    /**
     * Gets the id of this option.
     *
     * @return The id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the {@link TypeToken} that represents the value type.
     *
     * @return The value type token
     */
    public TypeToken<V> getTypeToken() {
        return this.typeToken;
    }

    /**
     * Gets the default value of this option.
     *
     * @return The default value
     */
    public Optional<V> getDefaultValue() {
        return Optional.ofNullable(this.defaultValue);
    }
}
