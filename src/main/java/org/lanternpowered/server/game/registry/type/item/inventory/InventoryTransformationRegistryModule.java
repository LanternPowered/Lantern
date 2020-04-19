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

import org.lanternpowered.server.inventory.query.LanternQueryTransformationBuilder;
import org.lanternpowered.server.inventory.transformation.InventoryTransforms;
import org.spongepowered.api.item.inventory.InventoryTransformation;
import org.spongepowered.api.item.inventory.InventoryTransformations;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.PrimaryPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.registry.RegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.registry.util.RegistrationDependency;

import java.util.HashMap;
import java.util.Map;

@RegistrationDependency({ QueryOperationRegistryModule.class })
public final class InventoryTransformationRegistryModule implements RegistryModule {

    @RegisterCatalog(InventoryTransformations.class)
    private final Map<String, InventoryTransformation> mappings = new HashMap<>();

    @Override
    public void registerDefaults() {
        register("no_op", InventoryTransforms.NO_OP);
        register("player_primary_hotbar_first", new LanternQueryTransformationBuilder()
                .append(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class),
                        QueryOperationTypes.INVENTORY_TYPE.of(PrimaryPlayerInventory.class))
                .build());
        register("reverse", InventoryTransforms.REVERSE);
        register("empty", InventoryTransforms.EMPTY);
    }

    private void register(String id, InventoryTransformation value) {
        this.mappings.put(id, value);
    }
}
