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
package org.lanternpowered.testserver.plugin;

import com.google.common.reflect.TypeToken;
import org.slf4j.Logger;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import javax.inject.Inject;

@Plugin(id = "data_translator_test", name = "Data Translator Test", version = "0.0.0")
public class DataTranslatorTest {

    @Inject private Logger logger;

    @Listener
    public void onRegisterDataTranslators(GameRegistryEvent.Register<DataTranslator<?>> event) {
        event.register(new MyObjectDataTranslator());
        this.logger.info("Registered MyObjectDataTranslator");
    }

    @Listener
    public void onPostInit(GamePostInitializationEvent event) {
        final DataQuery query = DataQuery.of("Test");

        final DataContainer container = DataContainer.createNew();
        container.set(query, new MyObject("MyValue"));

        if (container.getObject(query, MyObject.class).get().getValue().equals("MyValue")) {
            this.logger.info("MyObjectDataTranslator: OK");
        } else {
            this.logger.info("MyObjectDataTranslator: FAIL");
        }
    }

    public static class MyObject {

        private final String value;

        public MyObject(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static class MyObjectDataTranslator implements DataTranslator<MyObject> {

        @Override
        public TypeToken<MyObject> getToken() {
            return TypeToken.of(MyObject.class);
        }

        @Override
        public MyObject translate(DataView view) throws InvalidDataException {
            return new MyObject(view.getString(DataQuery.of("value")).get());
        }

        @Override
        public DataContainer translate(MyObject obj) throws InvalidDataException {
            return DataContainer.createNew().set(DataQuery.of("value"), obj.getValue());
        }

        @Override
        public CatalogKey getKey() {
            return CatalogKey.of("data_translator_test", "my_object");
        }
    }
}
