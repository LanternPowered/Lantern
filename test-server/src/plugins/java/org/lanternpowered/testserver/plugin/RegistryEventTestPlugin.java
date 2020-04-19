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

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "registry_event_test", version = "1.0.0")
public class RegistryEventTestPlugin {

    @Inject private Logger logger;

    /*
    @Listener
    public void onRegister(GameRegistryEvent.Register event) {
        this.logger.info("onRegister: " + event.getCatalogType().getName());
    }
    */

    @Listener
    public void onRegister1(GameRegistryEvent.Register<?> event) {
        this.logger.info("onRegister<?>: " + event.getCatalogType().getName());
    }

    @Listener
    public void onRegister2(GameRegistryEvent.Register<? extends Key<?>> event) {
        this.logger.info("onRegister<? extends Key<?>>: " + event.getCatalogType().getName());
    }

    @Listener
    public void onRegister3(GameRegistryEvent.Register<Key> event) {
        this.logger.info("onRegister<Key>: " + event.getCatalogType().getName());
    }

    @Listener
    public void onRegister4(GameRegistryEvent.Register<Key<?>> event) {
        this.logger.info("onRegister<Key<?>>: " + event.getCatalogType().getName());
    }

    @Listener
    public void onRegister5(GameRegistryEvent.Register<? extends DataRegistration<?, ?>> event) {
        this.logger.info("onRegister<? extends DataRegistration<?, ?>>: " + event.getCatalogType().getName());
    }

    @Listener
    public void onRegister6(GameRegistryEvent.Register<DataRegistration> event) {
        this.logger.info("onRegister<DataRegistration>: " + event.getCatalogType().getName());
    }

    @Listener
    public void onRegister7(GameRegistryEvent.Register<DataRegistration<?,?>> event) {
        this.logger.info("onRegister<DataRegistration<?,?>>: " + event.getCatalogType().getName());
    }
}
