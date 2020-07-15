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
import org.spongepowered.api.ResourceKey;

public final class EntityEffectTypeRegistryModule extends DefaultCatalogRegistryModule<EntityEffectType> {

    public EntityEffectTypeRegistryModule() {
        super(EntityEffectTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternEntityEffectType(ResourceKey.minecraft("angry")));
        register(new LanternEntityEffectType(ResourceKey.minecraft("death")));
        register(new LanternEntityEffectType(ResourceKey.minecraft("fall")));
        register(new LanternEntityEffectType(ResourceKey.minecraft("hurt")));
        register(new LanternEntityEffectType(ResourceKey.minecraft("idle")));
        register(new LanternEntityEffectType(ResourceKey.minecraft("lightning")));
        register(new LanternEntityEffectType(ResourceKey.minecraft("merge")));
        register(new LanternEntityEffectType(ResourceKey.minecraft("splash")));
        register(new LanternEntityEffectType(ResourceKey.minecraft("swim")));
        register(new LanternEntityEffectType(ResourceKey.minecraft("villager_no")));
        register(new LanternEntityEffectType(ResourceKey.minecraft("villager_yes")));
        register(new LanternEntityEffectType(ResourceKey.minecraft("walk")));
    }
}
