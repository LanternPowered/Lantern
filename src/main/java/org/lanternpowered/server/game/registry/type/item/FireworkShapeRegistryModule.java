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
package org.lanternpowered.server.game.registry.type.item;

import org.lanternpowered.server.effect.firework.LanternFireworkShape;
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.item.FireworkShape;
import org.spongepowered.api.item.FireworkShapes;

public class FireworkShapeRegistryModule extends InternalPluginCatalogRegistryModule<FireworkShape> {

    private static final FireworkShapeRegistryModule INSTANCE = new FireworkShapeRegistryModule();

    public static FireworkShapeRegistryModule get() {
        return INSTANCE;
    }

    private FireworkShapeRegistryModule() {
        super(FireworkShapes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternFireworkShape(CatalogKey.minecraft("ball"), 0));
        register(new LanternFireworkShape(CatalogKey.minecraft("large_ball"), 1));
        register(new LanternFireworkShape(CatalogKey.minecraft("star"), 2));
        register(new LanternFireworkShape(CatalogKey.minecraft("creeper"), 3));
        register(new LanternFireworkShape(CatalogKey.minecraft("burst"), 4));
    }
}
