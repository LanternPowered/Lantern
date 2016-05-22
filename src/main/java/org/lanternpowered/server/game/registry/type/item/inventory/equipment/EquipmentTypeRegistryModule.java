/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.game.registry.type.item.inventory.equipment;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.inventory.equipment.LanternEquipmentType;
import org.lanternpowered.server.inventory.equipment.LanternEquipmentTypeWorn;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class EquipmentTypeRegistryModule implements CatalogRegistryModule<EquipmentType> {

    @RegisterCatalog(EquipmentTypes.class)
    private final Map<String, EquipmentType> equipmentTypes = new HashMap<>();

    @Override
    public void registerDefaults() {
        final List<EquipmentType> types = new ArrayList<>();
        types.add(new LanternEquipmentType("all", type -> true));
        types.add(new LanternEquipmentType("equipped"));
        types.add(new LanternEquipmentTypeWorn("worn", type -> type instanceof LanternEquipmentTypeWorn));
        types.add(new LanternEquipmentTypeWorn("boots"));
        types.add(new LanternEquipmentTypeWorn("chestplate"));
        types.add(new LanternEquipmentTypeWorn("headwear"));
        types.add(new LanternEquipmentTypeWorn("leggings"));
        types.forEach(type -> this.equipmentTypes.put(type.getId(), type));
    }

    @Override
    public Optional<EquipmentType> getById(String id) {
        return Optional.ofNullable(this.equipmentTypes.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<EquipmentType> getAll() {
        return ImmutableSet.copyOf(this.equipmentTypes.values());
    }

}
