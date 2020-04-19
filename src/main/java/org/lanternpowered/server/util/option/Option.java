/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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
