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

import org.lanternpowered.server.inventory.query.LanternQueryTransformationBuilder;
import org.lanternpowered.server.inventory.transformation.InventoryTransforms;
import org.spongepowered.api.item.inventory.InventoryTransformation;
import org.spongepowered.api.item.inventory.InventoryTransformations;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
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
        register("player_main_hotbar_first", new LanternQueryTransformationBuilder()
                .append(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class),
                        QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class))
                .build());
        register("reverse", InventoryTransforms.REVERSE);
        register("empty", InventoryTransforms.EMPTY);
    }

    private void register(String id, InventoryTransformation value) {
        this.mappings.put(id, value);
    }
}
