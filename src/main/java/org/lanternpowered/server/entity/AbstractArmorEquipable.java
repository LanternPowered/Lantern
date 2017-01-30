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

import org.lanternpowered.server.inventory.equipment.LanternEquipmentTypes;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;

import java.util.Optional;

import javax.annotation.Nullable;

public interface AbstractArmorEquipable extends AbstractEquipable, ArmorEquipable {

    @Override
    default Optional<ItemStack> getHelmet() {
        return getEquipped(EquipmentTypes.HEADWEAR);
    }

    @Override
    default void setHelmet(@Nullable ItemStack helmet) {
        equip(EquipmentTypes.HEADWEAR, helmet);
    }

    @Override
    default Optional<ItemStack> getChestplate() {
        return getEquipped(EquipmentTypes.CHESTPLATE);
    }

    @Override
    default void setChestplate(@Nullable ItemStack chestplate) {
        equip(EquipmentTypes.CHESTPLATE, chestplate);
    }

    @Override
    default Optional<ItemStack> getLeggings() {
        return getEquipped(EquipmentTypes.LEGGINGS);
    }

    @Override
    default void setLeggings(@Nullable ItemStack leggings) {
        equip(EquipmentTypes.LEGGINGS, leggings);
    }

    @Override
    default Optional<ItemStack> getBoots() {
        return getEquipped(EquipmentTypes.BOOTS);
    }

    @Override
    default void setBoots(@Nullable ItemStack boots) {
        equip(EquipmentTypes.BOOTS, boots);
    }

    @Override
    default Optional<ItemStack> getItemInHand(HandType handType) {
        return getEquipped(LanternEquipmentTypes.forHand(handType));
    }

    @Override
    default void setItemInHand(HandType handType, @Nullable ItemStack itemInHand) {
        equip(LanternEquipmentTypes.forHand(handType), itemInHand);
    }

}
