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
package org.lanternpowered.server.inject.plugin;

import com.google.inject.AbstractModule;
import org.lanternpowered.server.inject.InjectablePropertyProvider;
import org.slf4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;

public abstract class PluginModule extends AbstractModule {

    private final PluginContainer container;

    protected PluginModule(PluginContainer container) {
        this.container = container;
    }

    @Override
    protected void configure() {
        bind(PluginContainer.class).toInstance(this.container);
        bind(Logger.class).toInstance(this.container.getLogger());

        install(new PluginConfigurationModule());
        install(new InjectablePropertyProvider());
    }
}
