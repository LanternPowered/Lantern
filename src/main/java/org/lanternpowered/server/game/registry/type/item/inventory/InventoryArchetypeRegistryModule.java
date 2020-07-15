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
package org.lanternpowered.server.game.registry.type.item.inventory;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.game.registry.CatalogMappingData;
import org.lanternpowered.server.game.registry.type.item.inventory.equipment.EquipmentTypeRegistryModule;
import org.lanternpowered.server.inventory.LanternInventoryArchetype;
import org.lanternpowered.server.inventory.LanternInventoryArchetypes;
import org.lanternpowered.server.inventory.UnknownInventoryArchetype;
import org.lanternpowered.server.inventory.sponge.SpongeInventoryArchetypes;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.lanternpowered.server.item.ItemTypeRegistry;
import org.lanternpowered.server.util.UncheckedThrowables;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.registry.util.RegistrationDependency;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@RegistrationDependency({ ClientContainerRegistryModule.class, EquipmentTypeRegistryModule.class, ItemTypeRegistry.class,
        QueryOperationRegistryModule.class })
public class InventoryArchetypeRegistryModule extends DefaultCatalogRegistryModule<InventoryArchetype> {

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
        register(new UnknownInventoryArchetype(ResourceKey.minecraft("unknown")));
        register(new UnknownInventoryArchetype(ResourceKey.minecraft("empty")));

        for (Class<?> target : Arrays.asList(VanillaInventoryArchetypes.class, SpongeInventoryArchetypes.class)) {
            for (Field field : target.getFields()) {
                if (LanternInventoryArchetype.class.isAssignableFrom(field.getType())) {
                    try {
                        register((InventoryArchetype) field.get(null));
                    } catch (IllegalAccessException e) {
                        throw UncheckedThrowables.throwUnchecked(e);
                    }
                }
            }
        }
    }
}
