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

import java.io.IOException;
import java.util.Optional;

import javax.annotation.Nullable;

public class ObjectTypeAdapterFactory<V, O extends ObjectType<V>> implements TypeAdapterFactory {

    private static final String TYPE = "type";
    private static final String DATA = "data";

    private final AbstractObjectTypeRegistryModule registry;
    protected final TypeToken<V> typeToken;

    public ObjectTypeAdapterFactory(AbstractObjectTypeRegistryModule<V, O> registry, TypeToken<V> type) {
        this.registry = registry;
        this.typeToken = type;
    }

    @Nullable
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!type.isAssignableFrom(this.typeToken)) {
            return null;
        }
        final TypeAdapter<JsonElement> jsonElementTypeAdapter = gson.getAdapter(JsonElement.class);
        final TypeToken theTypeToken = type;
        return new TypeAdapter<T>() {

            @SuppressWarnings("unchecked")
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                final JsonElement element = serialize((TypeToken<V>) theTypeToken, (V) value, gson);
                jsonElementTypeAdapter.write(out, element);
            }

            @SuppressWarnings("unchecked")
            @Override
            public T read(JsonReader in) throws IOException {
                final JsonElement element = jsonElementTypeAdapter.read(in);
                return (T) deserialize(theTypeToken, element, gson);
            }
        };
    }

    protected JsonElement serialize(TypeToken<V> type, V value, Gson gson) throws IOException {
        //noinspection unchecked
        final Optional<O> optType = this.registry.getByClass((Class) value.getClass());
        if (!optType.isPresent()) {
            throw new IOException("Attempted to serialize a action type that is not registered: "
                    + value.getClass().getName());
        }
        final JsonObject json = new JsonObject();
        json.addProperty(TYPE, optType.get().getId());
        //noinspection unchecked
        final TypeAdapter<V> delegateTypeAdapter = gson.getDelegateAdapter(this,
                (TypeToken<V>) TypeToken.get(optType.get().getType()));
        json.add(DATA, delegateTypeAdapter.toJsonTree(value));
        return json;
    }

    protected V deserialize(TypeToken<V> type, JsonElement element, Gson gson) {
        final JsonObject obj = element.getAsJsonObject();
        final String valueTypeId = obj.get(TYPE).getAsString();
        //noinspection unchecked
        final Optional<O> optType = this.registry.getById(valueTypeId);
        if (!optType.isPresent()) {
            throw new IllegalStateException("Unknown type id: " + valueTypeId);
        }
        //noinspection unchecked
        final TypeAdapter<V> delegateTypeAdapter = gson.getDelegateAdapter(this,
                (TypeToken<V>) TypeToken.get(optType.get().getType()));
        try {
            return delegateTypeAdapter.fromJsonTree(obj.get(DATA));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to compile the " + this.typeToken.toString() + " from the json: " + obj.toString(), e);
        }
    }
}
