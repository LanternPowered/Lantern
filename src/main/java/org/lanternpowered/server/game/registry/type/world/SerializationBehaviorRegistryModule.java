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
package org.lanternpowered.server.game.registry.type.world;

import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.lanternpowered.server.world.LanternSerializationBehavior;
import org.spongepowered.api.world.SerializationBehavior;
import org.spongepowered.api.world.SerializationBehaviors;

public final class SerializationBehaviorRegistryModule extends DefaultCatalogRegistryModule<SerializationBehavior> {

    public SerializationBehaviorRegistryModule() {
        super(SerializationBehaviors.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternSerializationBehavior(CatalogKeys.minecraft("automatic", "Automatic")));
        register(new LanternSerializationBehavior(CatalogKeys.minecraft("manual", "Manual")));
        register(new LanternSerializationBehavior(CatalogKeys.minecraft("none", "None")));
    }
}

