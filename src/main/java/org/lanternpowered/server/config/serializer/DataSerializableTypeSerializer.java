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
package org.lanternpowered.server.config.serializer;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.lanternpowered.server.data.translator.ConfigurateTranslator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.persistence.DataBuilder;

import java.util.Optional;

/**
 * An implementation of {@link TypeSerializer} so that DataSerializables can be
 * provided in {@link ObjectMapper}-using classes.
 */
public final class DataSerializableTypeSerializer implements TypeSerializer<DataSerializable> {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public DataSerializable deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        final DataManager dataManager = Sponge.getDataManager();
        final Optional<DataBuilder<?>> builderOpt = (Optional) dataManager.getBuilder(type.getRawType().asSubclass(DataSerializable.class));
        if (!builderOpt.isPresent()) {
            throw new ObjectMappingException("No data builder is registered for " + type);
        }
        final Optional<? extends DataSerializable> built = builderOpt.get().build(ConfigurateTranslator.instance().translate(value));
        if (!built.isPresent()) {
            throw new ObjectMappingException("Unable to build instance of " + type);
        }
        return built.get();
    }

    @Override
    public void serialize(TypeToken<?> type, DataSerializable obj, ConfigurationNode value) throws ObjectMappingException {
        final DataContainer container = obj.toContainer();
        value.setValue(ConfigurateTranslator.instance().translate(container));
    }

}
