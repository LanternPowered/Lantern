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
package org.lanternpowered.server.script.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;

import java.io.IOException;

import org.checkerframework.checker.nullness.qual.Nullable;

public class CatalogTypeTypeAdapterFactory implements TypeAdapterFactory {

    @Nullable
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final Class<? super T> raw = type.getRawType();
        if (!CatalogType.class.isAssignableFrom(raw)) {
            return null;
        }
        final TypeAdapter<JsonElement> jsonElementTypeAdapter = gson.getAdapter(JsonElement.class);
        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                jsonElementTypeAdapter.write(out, new JsonPrimitive(((CatalogType) value).getKey().toString()));
            }

            @Override
            public T read(JsonReader in) throws IOException {
                final String id = jsonElementTypeAdapter.read(in).getAsString();
                //noinspection unchecked
                return (T) Sponge.getRegistry().getType((Class<? extends CatalogType>) raw, CatalogKey.resolve(id)).orElseThrow(
                        () -> new IllegalArgumentException("There does not exist a " + raw.getName() + " with id: " + id));
            }
        };
    }
}
