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

import org.lanternpowered.server.data.type.LanternHandType;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;

public final class HandTypeRegistryModule extends DefaultCatalogRegistryModule<HandType> {

    public HandTypeRegistryModule() {
        super(HandTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternHandType(CatalogKey.minecraft("main_hand"), "options.mainHand"));
        register(new LanternHandType(CatalogKey.minecraft("off_hand"), "hand.off"));
    }
}
