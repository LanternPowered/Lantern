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
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.lanternpowered.api.script.ObjectType;
import org.lanternpowered.server.script.AbstractObjectTypeRegistryModule;
import org.spongepowered.api.CatalogKey;

import java.io.IOException;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("unchecked")
public class ObjectTypeAdapterFactory<V, O extends ObjectType<V>> implements TypeAdapterFactory {

    private static final String TYPE = "type";
    private static final String DATA = "data";

    private final AbstractObjectTypeRegistryModule registry;
    protected final com.google.common.reflect.TypeToken<V> typeToken;

    public ObjectTypeAdapterFactory(AbstractObjectTypeRegistryModule<V, O> registry, TypeToken<V> type) {
        this.typeToken = (com.google.common.reflect.TypeToken<V>) com.google.common.reflect.TypeToken.of(type.getType());
        this.registry = registry;
    }

    @Nullable
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!com.google.common.reflect.TypeToken.of(type.getType()).isSupertypeOf(this.typeToken)) {
            return null;
        }
        final TypeAdapter<JsonElement> jsonElementTypeAdapter = gson.getAdapter(JsonElement.class);
        final TypeToken theTypeToken = type;
        return new TypeAdapter<T>() {

            @Override
            public void write(JsonWriter out, T value) throws IOException {
                final JsonElement element = serialize((TypeToken<V>) theTypeToken, (V) value, gson);
                jsonElementTypeAdapter.write(out, element);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                final JsonElement element = jsonElementTypeAdapter.read(in);
                return (T) deserialize(theTypeToken, element, gson);
            }
        };
    }

    protected JsonElement serialize(TypeToken<V> type, V value, Gson gson) throws IOException {
        final Optional<O> optType = this.registry.getByClass(value.getClass());
        if (!optType.isPresent()) {
            throw new IOException("Attempted to serialize a action type that is not registered: "
                    + value.getClass().getName());
        }
        final JsonObject json = new JsonObject();
        json.addProperty(TYPE, optType.get().getKey().toString());
        final TypeAdapter<V> delegateTypeAdapter = gson.getDelegateAdapter(this,
                (TypeToken<V>) TypeToken.get(optType.get().getType()));
        json.add(DATA, delegateTypeAdapter.toJsonTree(value));
        return json;
    }

    protected V deserialize(TypeToken<V> type, JsonElement element, Gson gson) {
        final JsonObject obj = element.getAsJsonObject();
        final String valueTypeId = obj.get(TYPE).getAsString();
        final Optional<O> optType = this.registry.get(CatalogKey.resolve(valueTypeId));
        if (!optType.isPresent()) {
            throw new IllegalStateException("Unknown type id: " + valueTypeId);
        }
        final TypeAdapter<V> delegateTypeAdapter = gson.getDelegateAdapter(this,
                (TypeToken<V>) TypeToken.get(optType.get().getType()));
        try {
            return delegateTypeAdapter.fromJsonTree(obj.get(DATA));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to compile the " + this.typeToken.toString() + " from the json: " + obj.toString(), e);
        }
    }
}
