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
