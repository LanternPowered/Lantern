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
package org.lanternpowered.server.game.registry.type.cause;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.cause.entity.damage.source.LanternDamageSourceBuilder;
import org.lanternpowered.server.game.registry.CatalogMappingData;
import org.lanternpowered.server.game.registry.CatalogMappingDataHolder;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.registry.RegistryModule;
import org.spongepowered.api.registry.util.RegistrationDependency;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RegistrationDependency(DamageTypeRegistryModule.class)
public class ConstantDamageSourceRegistryModule implements RegistryModule, CatalogMappingDataHolder {

    private final Map<String, DamageSource> damageSources = new HashMap<>();

    @Override
    public List<CatalogMappingData> getCatalogMappings() {
        return ImmutableList.of(new CatalogMappingData(DamageSources.class, this.damageSources));
    }

    @Override
    public void registerDefaults() {
        register("drowning", new LanternDamageSourceBuilder()
                .type(DamageTypes.DROWN)
                .bypassesArmor()
                .build());
        register("falling", new LanternDamageSourceBuilder()
                .type(DamageTypes.FALL)
                .bypassesArmor()
                .build());
        register("fire_tick", new LanternDamageSourceBuilder()
                .type(DamageTypes.FIRE)
                .build());
        register("generic", new LanternDamageSourceBuilder()
                .type(DamageTypes.GENERIC)
                .build());
        register("magic", new LanternDamageSourceBuilder()
                .type(DamageTypes.MAGIC)
                .bypassesArmor()
                .magical()
                .build());
        register("melting", new LanternDamageSourceBuilder()
                .type(DamageTypes.FIRE)
                .bypassesArmor()
                .build());
        register("poison", new LanternDamageSourceBuilder()
                .type(DamageTypeRegistryModule.POISON)
                .bypassesArmor()
                .build());
        register("starvation", new LanternDamageSourceBuilder()
                .type(DamageTypes.HUNGER)
                .bypassesArmor()
                .absolute()
                .build());
        register("void", new LanternDamageSourceBuilder()
                .type(DamageTypes.VOID)
                .bypassesArmor()
                .creative()
                .build());
        register("wither", new LanternDamageSourceBuilder()
                .type(DamageTypeRegistryModule.WITHER)
                .bypassesArmor()
                .build());
    }

    public void register(String mapping, DamageSource damageSource) {
        this.damageSources.put(mapping.toLowerCase(Locale.ENGLISH), damageSource);
    }
}
