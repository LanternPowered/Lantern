/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
