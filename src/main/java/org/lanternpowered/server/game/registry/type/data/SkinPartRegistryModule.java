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

import org.lanternpowered.server.data.type.LanternSkinPart;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.type.SkinPart;
import org.spongepowered.api.data.type.SkinParts;

public final class SkinPartRegistryModule extends DefaultCatalogRegistryModule<SkinPart> {

    public SkinPartRegistryModule() {
        super(SkinParts.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternSkinPart(CatalogKey.minecraft("cape"), 0));
        register(new LanternSkinPart(CatalogKey.minecraft("jacket"), 1));
        register(new LanternSkinPart(CatalogKey.minecraft("left_sleeve"), 2));
        register(new LanternSkinPart(CatalogKey.minecraft("right_sleeve"), 3));
        register(new LanternSkinPart(CatalogKey.minecraft("left_pants_leg"), 4));
        register(new LanternSkinPart(CatalogKey.minecraft("right_pants_leg"), 5));
        register(new LanternSkinPart(CatalogKey.minecraft("hat"), 6));
    }
}
