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
package org.lanternpowered.server.script.context;

import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.api.script.context.Parameter;
import org.lanternpowered.api.script.context.Parameters;
import org.lanternpowered.api.world.World;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.world.Location;

public class ContextParameterRegistryModule extends DefaultCatalogRegistryModule<Parameter> {

    public ContextParameterRegistryModule() {
        super(Parameters.class);
    }

    @Override
    public void registerDefaults() {
        this.register(new ContextParameterImpl<>(CatalogKeys.lantern("target_location"), Location.class));
        this.register(new ContextParameterImpl<>(CatalogKeys.lantern("world"), World.class));
    }
}
