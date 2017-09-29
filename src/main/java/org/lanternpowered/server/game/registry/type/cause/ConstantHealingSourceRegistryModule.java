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
import org.lanternpowered.server.cause.entity.healing.source.LanternHealingSourceBuilder;
import org.lanternpowered.server.game.registry.CatalogMappingData;
import org.lanternpowered.server.game.registry.CatalogMappingDataHolder;
import org.spongepowered.api.event.cause.entity.health.HealingTypes;
import org.spongepowered.api.event.cause.entity.health.source.HealingSource;
import org.spongepowered.api.event.cause.entity.health.source.HealingSources;
import org.spongepowered.api.registry.RegistryModule;
import org.spongepowered.api.registry.util.RegistrationDependency;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RegistrationDependency(HealingTypeRegistryModule.class)
public class ConstantHealingSourceRegistryModule implements RegistryModule, CatalogMappingDataHolder {

    private final Map<String, HealingSource> healingSources = new HashMap<>();

    @Override
    public List<CatalogMappingData> getCatalogMappings() {
        return ImmutableList.of(new CatalogMappingData(HealingSources.class, this.healingSources));
    }

    @Override
    public void registerDefaults() {
        register("food", new LanternHealingSourceBuilder()
                .type(HealingTypes.FOOD)
                .build());
        register("generic", new LanternHealingSourceBuilder()
                .type(HealingTypeRegistryModule.GENERIC)
                .build());
        register("magic", new LanternHealingSourceBuilder()
                .type(HealingTypeRegistryModule.MAGIC)
                .build());
    }

    public void register(String mapping, HealingSource healingSource) {
        this.healingSources.put(mapping.toLowerCase(Locale.ENGLISH), healingSource);
    }
}
