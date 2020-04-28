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
package org.lanternpowered.server.inject.provider;

import static org.lanternpowered.server.inject.provider.ProviderHelper.provideNameOrFail;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.lanternpowered.server.inject.InjectionPoint;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;

import java.util.NoSuchElementException;

public class PluginContainerProvider implements Provider<PluginContainer> {

    @Inject private PluginManager pluginManager;
    @Inject private InjectionPoint point;

    @Override
    public PluginContainer get() {
        final String name = provideNameOrFail(this.point);
        return this.pluginManager.getPlugin(name)
                .orElseThrow(() -> new NoSuchElementException("Cannot find plugin: " + name));
    }
}
