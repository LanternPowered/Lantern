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
package org.lanternpowered.server.entity;

import org.lanternpowered.server.game.registry.type.item.inventory.equipment.EquipmentTypeRegistryModule;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.entity.ArmorEquipable;
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
