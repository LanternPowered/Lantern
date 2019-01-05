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
