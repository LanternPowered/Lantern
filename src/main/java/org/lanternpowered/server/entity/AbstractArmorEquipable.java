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
package org.lanternpowered.server.entity;

import org.lanternpowered.server.game.registry.type.item.inventory.equipment.EquipmentTypeRegistryModule;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.item.inventory.ArmorEquipable;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;

public interface AbstractArmorEquipable extends AbstractEquipable, ArmorEquipable {

    @Override
    default ItemStack getHelmet() {
        return getEquipped(EquipmentTypes.HEADWEAR).orElseGet(ItemStack::empty);
    }

    @Override
    default void setHelmet(ItemStack helmet) {
        equip(EquipmentTypes.HEADWEAR, helmet);
    }

    @Override
    default ItemStack getChestplate() {
        return getEquipped(EquipmentTypes.CHESTPLATE).orElseGet(ItemStack::empty);
    }

    @Override
    default void setChestplate(ItemStack chestplate) {
        equip(EquipmentTypes.CHESTPLATE, chestplate);
    }

    @Override
    default ItemStack getLeggings() {
        return getEquipped(EquipmentTypes.LEGGINGS).orElseGet(ItemStack::empty);
    }

    @Override
    default void setLeggings(ItemStack leggings) {
        equip(EquipmentTypes.LEGGINGS, leggings);
    }

    @Override
    default ItemStack getBoots() {
        return getEquipped(EquipmentTypes.BOOTS).orElseGet(ItemStack::empty);
    }

    @Override
    default void setBoots(ItemStack boots) {
        equip(EquipmentTypes.BOOTS, boots);
    }

    @Override
    default ItemStack getItemInHand(HandType handType) {
        return getEquipped(EquipmentTypeRegistryModule.forHand(handType)).orElseGet(ItemStack::empty);
    }

    @Override
    default void setItemInHand(HandType handType, ItemStack itemInHand) {
        equip(EquipmentTypeRegistryModule.forHand(handType), itemInHand);
    }
}
