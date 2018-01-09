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
package org.lanternpowered.server.game.registry.type.item.inventory;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.game.registry.CatalogMappingData;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.game.registry.type.item.inventory.equipment.EquipmentTypeRegistryModule;
import org.lanternpowered.server.inventory.LanternInventoryArchetype;
import org.lanternpowered.server.inventory.LanternInventoryArchetypes;
import org.lanternpowered.server.inventory.UnknownInventoryArchetype;
import org.lanternpowered.server.inventory.sponge.SpongeInventoryArchetypes;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.registry.util.RegistrationDependency;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@RegistrationDependency({ ClientContainerRegistryModule.class, EquipmentTypeRegistryModule.class, ItemRegistryModule.class,
        QueryOperationRegistryModule.class })
public class InventoryArchetypeRegistryModule extends PluginCatalogRegistryModule<InventoryArchetype> {

    public InventoryArchetypeRegistryModule() {
        super(InventoryArchetypes.class);
    }

    @Override
    public List<CatalogMappingData> getCatalogMappings() {
        return ImmutableList.<CatalogMappingData>builder()
                .addAll(super.getCatalogMappings())
                .add(new CatalogMappingData(LanternInventoryArchetypes.class, provideCatalogMap()))
                .build();
    }

    @Override
    public void registerDefaults() {
        register(new UnknownInventoryArchetype("minecraft", "unknown"));
        register(new UnknownInventoryArchetype("minecraft", "empty"));

        for (Class<?> target : Arrays.asList(VanillaInventoryArchetypes.class, SpongeInventoryArchetypes.class)) {
            for (Field field : target.getFields()) {
                if (LanternInventoryArchetype.class.isAssignableFrom(field.getType())) {
                    try {
                        register((InventoryArchetype) field.get(null));
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
    }
}
