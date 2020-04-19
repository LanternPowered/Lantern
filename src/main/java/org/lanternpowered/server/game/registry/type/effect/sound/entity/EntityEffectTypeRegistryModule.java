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
package org.lanternpowered.server.game.registry.type.effect.sound.entity;

import org.lanternpowered.server.effect.entity.EntityEffectType;
import org.lanternpowered.server.effect.entity.EntityEffectTypes;
import org.lanternpowered.server.effect.entity.LanternEntityEffectType;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.CatalogKey;

public final class EntityEffectTypeRegistryModule extends DefaultCatalogRegistryModule<EntityEffectType> {

    public EntityEffectTypeRegistryModule() {
        super(EntityEffectTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternEntityEffectType(CatalogKey.minecraft("angry")));
        register(new LanternEntityEffectType(CatalogKey.minecraft("death")));
        register(new LanternEntityEffectType(CatalogKey.minecraft("fall")));
        register(new LanternEntityEffectType(CatalogKey.minecraft("hurt")));
        register(new LanternEntityEffectType(CatalogKey.minecraft("idle")));
        register(new LanternEntityEffectType(CatalogKey.minecraft("lightning")));
        register(new LanternEntityEffectType(CatalogKey.minecraft("merge")));
        register(new LanternEntityEffectType(CatalogKey.minecraft("splash")));
        register(new LanternEntityEffectType(CatalogKey.minecraft("swim")));
        register(new LanternEntityEffectType(CatalogKey.minecraft("villager_no")));
        register(new LanternEntityEffectType(CatalogKey.minecraft("villager_yes")));
        register(new LanternEntityEffectType(CatalogKey.minecraft("walk")));
    }
}
