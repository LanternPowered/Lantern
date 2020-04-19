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
package org.lanternpowered.server.game.registry.type.data;

import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.server.data.type.LanternHandPreference;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.data.type.HandPreference;
import org.spongepowered.api.data.type.HandPreferences;

public final class HandPreferenceRegistryModule extends DefaultCatalogRegistryModule<HandPreference> {

    public HandPreferenceRegistryModule() {
        super(HandPreferences.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternHandPreference(CatalogKeys.minecraft("left"), "options.mainHand.left"));
        register(new LanternHandPreference(CatalogKeys.minecraft("right"), "options.mainHand.right"));
    }
}
