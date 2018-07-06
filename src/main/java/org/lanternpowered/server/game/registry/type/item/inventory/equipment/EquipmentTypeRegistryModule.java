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
package org.lanternpowered.server.game.registry.type.item.inventory.equipment;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.inventory.equipment.LanternEquipmentType;
import org.lanternpowered.server.inventory.equipment.LanternHeldEquipmentType;
import org.lanternpowered.server.inventory.equipment.LanternWornEquipmentType;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;

public final class EquipmentTypeRegistryModule extends AdditionalPluginCatalogRegistryModule<EquipmentType> {

    public EquipmentTypeRegistryModule() {
        super(EquipmentTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternEquipmentType(CatalogKey.minecraft("all"),
                type -> true));
        register(new LanternEquipmentType(CatalogKey.minecraft("equipped"),
                type -> type instanceof LanternHeldEquipmentType || type instanceof LanternWornEquipmentType));
        register(new LanternHeldEquipmentType(CatalogKey.minecraft("held"),
                type -> type instanceof LanternHeldEquipmentType));
        register(new LanternHeldEquipmentType(CatalogKey.minecraft("main_hand")));
        register(new LanternHeldEquipmentType(CatalogKey.minecraft("off_hand")));
        register(new LanternWornEquipmentType(CatalogKey.minecraft("worn"),
                type -> type instanceof LanternWornEquipmentType));
        register(new LanternWornEquipmentType(CatalogKey.minecraft("boots")));
        register(new LanternWornEquipmentType(CatalogKey.minecraft("chestplate")));
        register(new LanternWornEquipmentType(CatalogKey.minecraft("headwear")));
        register(new LanternWornEquipmentType(CatalogKey.minecraft("leggings")));
    }

    public static EquipmentType forHand(HandType handType) {
        checkNotNull(handType, "handType");
        return handType == HandTypes.MAIN_HAND ? EquipmentTypes.MAIN_HAND : EquipmentTypes.OFF_HAND;
    }
}
