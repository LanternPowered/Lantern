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
package org.lanternpowered.server.game.registry.type.text;

import org.lanternpowered.server.text.selector.LanternSelectorFactory;
import org.spongepowered.api.registry.RegistryModule;
import org.spongepowered.api.registry.util.RegistrationDependency;

@RegistrationDependency(SelectorTypeRegistryModule.class)
public final class SelectorFactoryRegistryModule implements RegistryModule {

    private LanternSelectorFactory selectorFactory;

    @Override
    public void registerDefaults() {
        this.selectorFactory = new LanternSelectorFactory();
    }

    public LanternSelectorFactory getFactory() {
        return this.selectorFactory;
    }

}
