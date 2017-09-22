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
package org.lanternpowered.server.inventory.entity;

import org.lanternpowered.server.inventory.LanternEquipmentInventory;
import org.lanternpowered.server.inventory.LanternGridInventory;
import org.lanternpowered.server.inventory.LanternOrderedInventory;
import org.lanternpowered.server.inventory.slot.LanternEquipmentSlot;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.entity.UserInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.text.translation.Translation;

import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class LanternUserInventory<T extends User> extends LanternOrderedInventory implements UserInventory<T> {

    final LanternEquipmentInventory equipmentInventory;
    final LanternHumanMainInventory mainInventory;
    final OffHandSlot offHandSlot;

    public LanternUserInventory(@Nullable Inventory parent, @Nullable Translation name, @Nullable T user) {
        super(parent, name);
        initCraftingInventory();
        this.equipmentInventory = registerChild(new LanternEquipmentInventory(this, user) {
            {
                registerSlot(new LanternEquipmentSlot(this, EquipmentTypes.HEADWEAR));
                registerSlot(new LanternEquipmentSlot(this, EquipmentTypes.CHESTPLATE));
                registerSlot(new LanternEquipmentSlot(this, EquipmentTypes.LEGGINGS));
                registerSlot(new LanternEquipmentSlot(this, EquipmentTypes.BOOTS));
                this.slots.forEach(slot -> slot.setMaxStackSize(1));
                finalizeContent();
            }
        });
        this.offHandSlot = registerChild(new OffHandSlot(this, null));
        this.mainInventory = registerChild(new LanternHumanMainInventory(this) {
            {
                final LanternHumanMainInventory main = this;
                this.grid = new LanternGridInventory(this) {
                    {
                        for (int y = 0; y < 3; y++) {
                            for (int x = 0; x < 9; x++) {
                                final LanternSlot slot = registerSlotAt(x, y);
                                main.registerSlotAt(x, y, slot);
                            }
                        }
                        finalizeContent();
                    }
                };
                this.hotbar = new LanternHotbar(this) {
                    {
                        for (int x = 0; x < 9; x++) {
                            registerSlotAt(x);
                        }
                        finalizeContent();
                    }
                };
                registerRow(3, this.hotbar);
                finalizeContent();
                prioritizeChild(this.hotbar);
            }
        });
        finalizeContent();
    }

    protected void initCraftingInventory() {
    }

    @Override
    public LanternHotbar getHotbar() {
        return this.mainInventory.getHotbar();
    }

    @Override
    public LanternHumanMainInventory getMain() {
        return this.mainInventory;
    }

    @Override
    public LanternEquipmentInventory getEquipment() {
        return this.equipmentInventory;
    }

    @Override
    public OffHandSlot getOffhand() {
        return this.offHandSlot;
    }

    @Override
    public Optional<T> getCarrier() {
        return (Optional) this.equipmentInventory.getCarrier();
    }
}
